/**
 *
 * Copyright (c) 2006-2016, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.internal.manager.sql;

import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.db.AsynchronousQueryResult;
import com.speedment.runtime.field.Field;
import com.speedment.runtime.field.predicate.SqlPredicateFragment;
import com.speedment.runtime.internal.stream.builder.pipeline.DoublePipeline;
import com.speedment.runtime.internal.stream.builder.pipeline.IntPipeline;
import com.speedment.runtime.internal.stream.builder.pipeline.LongPipeline;
import com.speedment.runtime.internal.stream.builder.pipeline.ReferencePipeline;
import com.speedment.runtime.internal.stream.builder.streamterminator.StreamTerminator;
import com.speedment.runtime.internal.stream.builder.streamterminator.StreamTerminatorUtil;
import com.speedment.runtime.stream.Pipeline;
import com.speedment.runtime.stream.StreamDecorator;
import com.speedment.runtime.stream.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

import static com.speedment.runtime.stream.action.Property.SIZE;
import static com.speedment.runtime.stream.action.Verb.PRESERVE;
import static java.util.stream.Collectors.toList;
import com.speedment.runtime.field.predicate.FieldPredicate;
import com.speedment.runtime.field.predicate.FieldPredicateView;
import static com.speedment.runtime.util.NullUtil.requireNonNulls;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author pemi
 * @param <ENTITY> the entity type
 */
public final class SqlStreamTerminator<ENTITY> implements StreamTerminator {
    
    private final AbstractSqlManager<ENTITY> manager;
    private final AsynchronousQueryResult<ENTITY> asynchronousQueryResult;
    private final StreamDecorator decorator;
    
    public SqlStreamTerminator(AbstractSqlManager<ENTITY> manager, AsynchronousQueryResult<ENTITY> asynchronousQueryResult, StreamDecorator decorator) {
        this.manager = requireNonNull(manager);
        this.asynchronousQueryResult = requireNonNull(asynchronousQueryResult);
        this.decorator = requireNonNull(decorator);
    }
    
    @Override
    public StreamDecorator getStreamDecorator() {
        return decorator;
    }
    
    @Override
    public <P extends Pipeline> P optimize(P initialPipeline) {
        requireNonNull(initialPipeline);
        final List<FieldPredicate<ENTITY>> andPredicateBuilders = StreamTerminatorUtil.topLevelAndPredicates(initialPipeline);
        
        if (!andPredicateBuilders.isEmpty()) {
            modifySource(andPredicateBuilders, asynchronousQueryResult);
        }
        
        return getStreamDecorator().apply(initialPipeline);
    }
    
    public void modifySource(List<FieldPredicate<ENTITY>> predicateBuilders, AsynchronousQueryResult<ENTITY> qr) {
        requireNonNull(predicateBuilders);
        requireNonNull(qr);
        
        if (predicateBuilders.isEmpty()) {
            // Nothing to do...
            return;
        }

        final FieldPredicateView spv = manager.getDbmsType().getFieldPredicateView();
        final List<SqlPredicateFragment> fragments = predicateBuilders.stream()
            .map(sp -> spv.transform(manager, sp))
            .collect(toList());

        final String sql = manager.sqlSelect()
            + " WHERE "
            + fragments.stream()
                .map(SqlPredicateFragment::getSql)
                .collect(joining(" AND "));

        final List<Object> values = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            
            final FieldPredicate<ENTITY> p = predicateBuilders.get(i);
            final Field<ENTITY> referenceFieldTrait = p.getField();
            
            @SuppressWarnings("unchecked")
            final TypeMapper<Object, Object> tm = (TypeMapper<Object, Object>) 
                referenceFieldTrait.typeMapper();
            
            fragments.get(i).objects()
                .map(tm::toDatabaseType)
                .forEach(values::add);
        }

        qr.setSql(sql);
        qr.setValues(values);
    }
    
    @Override
    public long count(DoublePipeline pipeline) {
        requireNonNull(pipeline);
        return countHelper(pipeline, () -> StreamTerminator.super.count(pipeline));
    }
    
    @Override
    public long count(IntPipeline pipeline) {
        requireNonNull(pipeline);
        return countHelper(pipeline, () -> StreamTerminator.super.count(pipeline));
    }
    
    @Override
    public long count(LongPipeline pipeline) {
        requireNonNull(pipeline);
        return countHelper(pipeline, () -> StreamTerminator.super.count(pipeline));
    }
    
    @Override
    public <T> long count(ReferencePipeline<T> pipeline) {
        requireNonNull(pipeline);
        return countHelper(pipeline, () -> StreamTerminator.super.count(pipeline));
    }
    
    private static final Predicate<Action<?, ?>> CHECK_RETAIN_SIZE = action -> action.is(PRESERVE, SIZE);

    /**
     * Optimizer for count operations.
     *
     * @param pipeline          the pipeline
     * @param fallbackSupplier  a fallback supplier should every item be size
     *                          retaining
     * @return the number of rows
     */
    private long countHelper(Pipeline pipeline, LongSupplier fallbackSupplier) {
        requireNonNulls(pipeline, fallbackSupplier);
        
        if (pipeline.stream().allMatch(CHECK_RETAIN_SIZE)) {
            return manager.count();
        } else return fallbackSupplier.getAsLong();
    }
    
}

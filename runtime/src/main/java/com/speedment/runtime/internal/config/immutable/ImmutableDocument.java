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
package com.speedment.runtime.internal.config.immutable;

import com.speedment.runtime.config.Document;
import com.speedment.runtime.internal.config.BaseDocument;
import com.speedment.runtime.internal.util.document.DocumentUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.speedment.runtime.internal.util.ImmutableUtil.throwNewUnsupportedOperationExceptionImmutable;
import static com.speedment.runtime.internal.util.document.DocumentUtil.toStringHelper;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Emil Forslund
 */
public class ImmutableDocument extends BaseDocument {

    private final transient Map<String, List<Document>> children;

    protected ImmutableDocument(Map<String, Object> data) {
        super(null, Collections.unmodifiableMap(data));
        children = new ConcurrentHashMap<>();
    }

    protected ImmutableDocument(ImmutableDocument parent, Map<String, Object> data) {
        super(parent, Collections.unmodifiableMap(data));
        children = new ConcurrentHashMap<>();
    }

    @Override
    public final Map<String, Object> getData() {
        return super.getData();
    }

    @Override
    public final void put(String key, Object value) {
        throwNewUnsupportedOperationExceptionImmutable();
    }

    @Override
    public <P extends Document, T extends Document> Stream<T> children(String key, BiFunction<P, Map<String, Object>, T> constructor) {

        @SuppressWarnings("unchecked")
        final Function<Document, T> typeMapper = d -> (T) d;

        return children.computeIfAbsent(key, k -> {
            final List<Map<String, Object>> list = get(k)
                .map(DocumentUtil::castToDocumentList)
                .orElse(null);

            if (list == null) {
                return new ArrayList<>();
            } else {
                @SuppressWarnings("unchecked")
                final P thizz = (P) this;
                return list.stream()
                        .map(Collections::unmodifiableMap)
                        .map(data -> constructor.apply(thizz, data))
                        .map(Document.class::cast)
                        .collect(toList());
            }
        }).stream().map(typeMapper);
    }
    
    @Override
    public String toString() {
        return toStringHelper(this);
    }

    public static ImmutableDocument wrap(Document document) {
        return new ImmutableDocument(document.getData());
    }
}
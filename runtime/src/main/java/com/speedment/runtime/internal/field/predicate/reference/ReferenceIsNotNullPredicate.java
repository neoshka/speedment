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
package com.speedment.runtime.internal.field.predicate.reference;

import static com.speedment.runtime.field.predicate.PredicateType.IS_NOT_NULL;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.internal.field.predicate.AbstractFieldPredicate;

/**
 *
 * @param <ENTITY>  the entity type
 * @param <D>       the database type
 * @param <V>       the value type
 * 
 * @author  Per Minborg
 * @since   2.2.0
 */
public final class ReferenceIsNotNullPredicate<ENTITY, D, V>
        extends AbstractFieldPredicate<ENTITY, HasReferenceValue<ENTITY, D, V>> {
    
    public ReferenceIsNotNullPredicate(HasReferenceValue<ENTITY, D, V> field) {
        super(IS_NOT_NULL, field, entity -> entity != null && field.get(entity) != null);
    }
}
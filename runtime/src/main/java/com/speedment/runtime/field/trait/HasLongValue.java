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
package com.speedment.runtime.field.trait;

import com.speedment.runtime.annotation.Api;
import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.field.Field;
import com.speedment.runtime.field.method.LongGetter;
import com.speedment.runtime.field.method.LongSetter;
import com.speedment.runtime.field.method.SetToLong;
import com.speedment.runtime.internal.field.setter.SetToLongImpl;
import javax.annotation.Generated;

/**
 * A representation of an Entity field that is a primitive {@code long} type.
 * 
 * @param <ENTITY> entity type
 * @param <D>      database type
 * 
 * @author Emil Forslund
 * @since  3.0.0
 */
@Api(version = "3.0")
@Generated(value = "Speedment")
public interface HasLongValue<ENTITY, D> extends Field<ENTITY> {
    
    @Override
    LongSetter<ENTITY> setter();
    
    @Override
    LongGetter<ENTITY> getter();
    
    @Override
    TypeMapper<D, Long> typeMapper();
    
    /**
     * Gets the value from the Entity field.
     * 
     * @param entity the entity
     * @return       the value of the field
     */
    default long getAsLong(ENTITY entity) {
        return getter().getAsLong(entity);
    }
    
    /**
     * Sets the value in the given Entity.
     * 
     * @param entity the entity
     * @param value  to set
     * @return       the entity itself
     */
    default ENTITY set(ENTITY entity, long value) {
        return setter().setAsLong(entity, value);
    }
    
    /**
     * Creates and returns a setter handler with a given value.
     * 
     * @param value to set
     * @return      a set-operation with a given value
     */
    default SetToLong<ENTITY, D> setTo(long value) {
        return new SetToLongImpl<>(this, value);
    }
}
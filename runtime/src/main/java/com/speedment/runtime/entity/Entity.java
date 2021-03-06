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
package com.speedment.runtime.entity;

import com.speedment.runtime.annotation.Api;
import com.speedment.runtime.exception.SpeedmentException;
import com.speedment.runtime.manager.Manager;

/**
 * This interface contains the common methods that are the same for all
 * entities. Do not assume that an entity must implement this interface.
 *
 * @param <ENTITY>  the entity type
 * 
 * @author  Emil Forslund
 * @author  Per Minborg
 * @since   2.1.0
 */
@Api(version = "3.0")
public interface Entity<ENTITY> {

    /**
     * Creates and returns a new copy of this entity.
     *
     * @return  the copy
     */
    ENTITY copy();

    /**
     * Persists this entity to the underlying database and returns a potentially
     * updated entity. If the persistence fails for any reason, an unchecked
     * {@link SpeedmentException} is thrown.
     * <p>
     * It is unspecified if the returned updated entity is this entity instance
     * or another entity instance. It is erroneous to assume either, so you
     * should use only the returned entity after the method has been called.
     * However, it is guaranteed that this entity is untouched if an exception
     * is thrown.
     * <p>
     * The fields of returned entity instance may differ from this entity's
     * fields due to auto generated column(s) or because of any other
     * modification that the underlying database imposed on the persisted
     * entity.
     *
     * @param manager              the manager for this entity
     * @return                     an entity reflecting the result of the persisted entity
     * @throws SpeedmentException  if the underlying database throws an exception
     *                             (e.g. SQLException)
     */
    ENTITY persist(Manager<ENTITY> manager) throws SpeedmentException;

    /**
     * Updates this entity in the underlying database and returns a potentially
     * updated entity. If the update fails for any reason, an unchecked
     * {@link SpeedmentException} is thrown.
     * <p>
     * It is unspecified if the returned updated entity is the same as this
     * entity instance or another entity instance. It is erroneous to assume
     * either, so you should use only the returned entity after the method has
     * been called. However, it is guaranteed that this entity is untouched if
     * an exception is thrown.
     * <p>
     * The fields of returned entity instance may differ from the provided
     * entity fields due to auto generated column(s) or because of any other
     * modification that the underlying database imposed on the persisted
     * entity.
     * <p>
     * Entities are uniquely identified by their primary key(s).
     *
     * @param manager              the manager for this entity
     * @return                     an entity reflecting the result of the updated entity
     * @throws SpeedmentException  if the underlying database throws an exception
     *                             (e.g. SQLException)
     */
    ENTITY update(Manager<ENTITY> manager) throws SpeedmentException;

    /**
     * Removes the provided entity from the underlying database and returns this
     * entity instance. If the deletion fails for any reason, an unchecked
     * {@link SpeedmentException} is thrown.
     * <p>
     * Entities are uniquely identified by their primary key(s).
     *
     * @param manager              the manager for this entity
     * @return                     the provided entity instance
     * @throws SpeedmentException  if the underlying database throws an exception
     *                             (e.g. SQLException)
     */
    ENTITY remove(Manager<ENTITY> manager) throws SpeedmentException;
}

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
import com.speedment.runtime.field.finder.FindFrom;
import com.speedment.runtime.manager.Manager;
import com.speedment.runtime.field.method.Finder;

/**
 * A representation of an Entity field that use a foreign key to 
 * reference some other field.
 * 
 * @param <ENTITY>     the entity type
 * @param <FK_ENTITY>  the foreign entity type
 * 
 * @author  Per Minborg
 * @author  Emil Forslund
 * @since   2.2.0
 */
@Api(version = "3.0")
public interface HasFinder<ENTITY, FK_ENTITY> {

    /**
     * Returns a function that can find a foreign entity pointed out by this
     * field.
     *
     * @return the finder
     */
    Finder<ENTITY, FK_ENTITY> finder();
    
    /**
     * Returns a function that can be used to find referenced entites using the
     * specified manager.
     * 
     * @param foreignManager  the foreign manager
     * @return                finder method
     */
    FindFrom<ENTITY, FK_ENTITY> findFrom(Manager<FK_ENTITY> foreignManager);

    /**
     * Finds the foreign entity associated by this field.
     *
     * @param entity          this entity
     * @param foreignManager  the foreign manager
     * @return                the foreign entity associated by this field
     */
    default FK_ENTITY findFrom(ENTITY entity, Manager<FK_ENTITY> foreignManager) {
        return finder().apply(entity, foreignManager);
    }
}
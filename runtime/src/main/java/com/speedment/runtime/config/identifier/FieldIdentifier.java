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
package com.speedment.runtime.config.identifier;

import com.speedment.runtime.annotation.Api;
import com.speedment.runtime.config.Column;
import com.speedment.runtime.config.Dbms;
import com.speedment.runtime.config.Schema;
import com.speedment.runtime.config.Table;
import com.speedment.runtime.internal.util.document.DocumentDbUtil;

/**
 * Identifies a particular field in an entity. The identifier is a immutable
 * non-complex object that only contains the names of the nodes required to
 * uniquely identify it in the database.
 * <p>
 * To find the actual documents refered to by the identifier, the following
 * utility methods can be used:
 * <ul>
 *      <li>{@link DocumentDbUtil#referencedColumn(Project, FieldIdentifier)}
 *      <li>{@link DocumentDbUtil#referencedTable(Project, FieldIdentifier)}
 *      <li>{@link DocumentDbUtil#referencedSchema(Project, FieldIdentifier)}
 *      <li>{@link DocumentDbUtil#referencedDbms(Project, FieldIdentifier)}
 * </ul>
 * 
 * @param <ENTITY>  the entity type
 * 
 * @author  Emil Forslund
 * @since   2.3
 */
@Api(version = "3.0")
public interface FieldIdentifier<ENTITY> {
    
    /**
     * Returns the database name of the {@link Dbms} that this field is in.
     * 
     * @return  the {@link Dbms} name
     */
    String dbmsName();
    
    /**
     * Returns the database name of the {@link Schema} that this field is in.
     * 
     * @return  the {@link Schema} name
     */
    String schemaName();
    
    /**
     * Returns the database name of the {@link Table} that this field is in.
     * 
     * @return  the {@link Table} name
     */
    String tableName();
    
    /**
     * Returns the database name of the {@link Column} that this field is in.
     * 
     * @return  the {@link Column} name
     */
    String columnName();
}
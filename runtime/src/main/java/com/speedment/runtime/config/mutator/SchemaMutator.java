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
package com.speedment.runtime.config.mutator;

import com.speedment.runtime.annotation.Api;
import com.speedment.runtime.config.Schema;
import com.speedment.runtime.config.Table;
import com.speedment.runtime.config.mutator.trait.HasAliasMutator;
import com.speedment.runtime.config.mutator.trait.HasEnabledMutator;
import com.speedment.runtime.config.mutator.trait.HasNameMutator;
import com.speedment.runtime.internal.config.TableImpl;

import static com.speedment.runtime.config.Schema.DEFAULT_SCHEMA;
import static com.speedment.runtime.config.Schema.TABLES;
import static com.speedment.runtime.internal.util.document.DocumentUtil.newDocument;

/**
 *
 * @author       Per Minborg
 * @param <DOC>  document type
 */
@Api(version = "3.0")
public class SchemaMutator<DOC extends Schema> extends DocumentMutatorImpl<DOC> implements 
        HasEnabledMutator<DOC>, 
        HasNameMutator<DOC>, 
        HasAliasMutator<DOC> {

    public SchemaMutator(DOC schema) {
        super(schema);
    }

    public void setDefaultSchema(Boolean defaultSchema) {
        put(DEFAULT_SCHEMA, defaultSchema);
    }
    
    public Table addNewTable() {
        return new TableImpl(document(), newDocument(document(), TABLES));
    }
}
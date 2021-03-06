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
import com.speedment.runtime.config.Column;
import com.speedment.runtime.config.ForeignKey;
import com.speedment.runtime.config.Index;
import com.speedment.runtime.config.PrimaryKeyColumn;
import com.speedment.runtime.config.Table;
import com.speedment.runtime.config.mutator.trait.HasAliasMutator;
import com.speedment.runtime.config.mutator.trait.HasEnabledMutator;
import com.speedment.runtime.config.mutator.trait.HasNameMutator;
import com.speedment.runtime.internal.config.ColumnImpl;
import com.speedment.runtime.internal.config.ForeignKeyImpl;
import com.speedment.runtime.internal.config.IndexImpl;
import com.speedment.runtime.internal.config.PrimaryKeyColumnImpl;

import static com.speedment.runtime.config.Table.*;
import static com.speedment.runtime.internal.util.document.DocumentUtil.newDocument;

/**
 *
 * @author       Per Minborg
 * @param <DOC>  document type
 */
@Api(version = "3.0")
public class TableMutator<DOC extends Table> extends DocumentMutatorImpl<DOC> implements 
        HasEnabledMutator<DOC>, 
        HasNameMutator<DOC>, 
        HasAliasMutator<DOC> {

    public TableMutator(DOC table) {
        super(table);
    }

    public Column addNewColumn() {
        return new ColumnImpl(document(), newDocument(document(), COLUMNS));
    }
    
    public Index addNewIndex() {
        return new IndexImpl(document(), newDocument(document(), INDEXES));
    }
    
    public ForeignKey addNewForeignKey() {
        return new ForeignKeyImpl(document(), newDocument(document(), FOREIGN_KEYS));
    }
    
    public PrimaryKeyColumn addNewPrimaryKeyColumn() {
        return new PrimaryKeyColumnImpl(document(), newDocument(document(), PRIMARY_KEY_COLUMNS));
    }
}
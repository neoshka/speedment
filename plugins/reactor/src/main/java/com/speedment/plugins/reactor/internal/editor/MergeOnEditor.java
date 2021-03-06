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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.speedment.plugins.reactor.internal.editor;

import com.speedment.generator.component.TypeMapperComponent;
import com.speedment.internal.common.injector.annotation.Inject;
import com.speedment.plugins.reactor.ReactorComponent;
import static com.speedment.plugins.reactor.internal.util.ReactorComponentUtil.validMergingColumns;
import com.speedment.runtime.config.Column;
import com.speedment.tool.config.TableProperty;
import com.speedment.tool.property.PropertyEditor;
import com.speedment.tool.property.item.ChoiceBoxItem;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author Simon
 */
public final class MergeOnEditor<T extends TableProperty> implements PropertyEditor<T>{

    private @Inject TypeMapperComponent typeMappers;
        
    @Override
    public Stream<Item> fieldsFor(T document) {
        
        return Stream.of(
            new ChoiceBoxItem<>(
                "Merge event on", 
                document.stringPropertyOf(ReactorComponent.MERGE_ON, () -> null),
                observableList(
                    validMergingColumns(document, typeMappers)
                        .stream()
                        .map(Column::getJavaName)
                        .collect(toList())
                ),
                "This column will be used to merge events in a " + 
                "materialized object view (MOV) so that only the " + 
                "most recent revision of an entity is visible."
            )
        );
    }
}

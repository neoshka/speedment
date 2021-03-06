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
package com.speedment.plugins.reactor.internal.translator;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Interface;
import com.speedment.generator.internal.DefaultJavaClassTranslator;
import com.speedment.runtime.config.Table;


/**
 * Translator that generates the interface for the table specific materialized
 * views.
 * 
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class ViewTranslator extends DefaultJavaClassTranslator<Table, Interface> {

    public ViewTranslator(Table document) {
        super(document, Interface::of);
    }

    @Override
    protected String getClassOrInterfaceName() {
        return getSupport().namer().javaTypeName(getDocument().getJavaName()) + "View";
    }

    @Override
    protected Interface makeCodeGenModel(File file) {
        return newBuilder(file, getClassOrInterfaceName())
            .forEveryTable((intrf, table) -> {
                intrf.public_().add(SimpleType.create(
                    getSupport().basePackageName() + 
                    ".generated.Generated" + 
                    getSupport().namer().javaTypeName(getDocument().getJavaName()) + 
                    "View"
                ));
            }).build();
    }

    @Override
    protected String getJavadocRepresentText() {
        return "A materialized object view";
    }
}
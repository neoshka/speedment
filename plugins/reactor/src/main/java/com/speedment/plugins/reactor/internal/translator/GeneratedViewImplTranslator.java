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

import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Method;
import com.speedment.generator.internal.DefaultJavaClassTranslator;
import com.speedment.plugins.reactor.MaterializedViewImpl;
import com.speedment.runtime.Speedment;
import com.speedment.runtime.config.Column;
import com.speedment.runtime.config.Table;

import java.util.Objects;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import static com.speedment.common.codegen.internal.util.CollectorUtil.joinIfNotEmpty;
import static com.speedment.common.codegen.internal.util.Formatting.*;
import com.speedment.internal.common.injector.annotation.Inject;
import com.speedment.plugins.reactor.util.MergingSupport;
import java.lang.reflect.Type;

/**
 *
 * @author Emil Forslund
 * @since  1.1.0
 */
public final class GeneratedViewImplTranslator extends DefaultJavaClassTranslator<Table, Class> {

    private @Inject MergingSupport merger;
    
    public GeneratedViewImplTranslator(Table document) {
        super(document, Class::of);
    }

    @Override
    protected String getClassOrInterfaceName() {
        return "Generated" + getSupport().namer().javaTypeName(getDocument().getJavaName()) + "ViewImpl";
    }

    @Override
    public boolean isInGeneratedPackage() {
        return true;
    }

    @Override
    protected Class makeCodeGenModel(File file) {
        return newBuilder(file, getClassOrInterfaceName())
            .forEveryTable((clazz, table) -> {
                file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
                
                clazz.public_().abstract_()
                    .setSupertype(SimpleParameterizedType.create(
                        MaterializedViewImpl.class, 
                        getSupport().entityType(), 
                        merger.mergingColumnType(table)
                    ))
                    .add(SimpleType.create(getSupport().basePackageName() + ".generated.Generated" + getSupport().entityName() + "View"))
                    .add(Field.of("app", Speedment.class).protected_().final_())
                    .add(Constructor.of().public_()
                        .add(Field.of("app", Speedment.class))
                        .add(
                            "super(" + merger.mergingColumnField(table) + ");",
                            "this.app = requireNonNull(app);"
                        )
                    )
                    .add(merge());
            }).build();
    }

    @Override
    protected String getJavadocRepresentText() {
        return "A materialized object view";
    }
    
    private Method merge() {
        final Type type       = getSupport().entityType();
        final String typeName = getSupport().typeName();
        final String varName  = getSupport().variableName();
        
        return Method.of("merge", type)
            .add(OVERRIDE)
            .protected_()
            .add(Field.of("existing", type))
            .add(Field.of("loaded", type))
            .add(
                "if (existing == null) " + block(
                    "return loaded;"
                ) + " else " + block(
                    "final " + typeName + " " + varName + " = loaded.copy(app);",
                    getSupport().tableOrThrow()
                        .columns()
                        .filter(Column::isNullable)
                        .map(col -> "if (!" + varName + 
                            ".get" + getSupport().typeName(col) + "().isPresent()) " + block(
                                varName + ".set" + getSupport().typeName(col) + 
                                "(existing.get" + getSupport().typeName(col) + 
                                "().orElse(null));"
                            )
                        ).collect(joinIfNotEmpty(dnl(), nl(), dnl())) + 
                        "return " + varName + ";"
                )
            );
    }
}
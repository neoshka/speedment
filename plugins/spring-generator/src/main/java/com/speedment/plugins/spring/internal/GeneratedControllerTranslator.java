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
package com.speedment.plugins.spring.internal;

import com.speedment.common.codegen.internal.model.value.ReferenceValue;
import com.speedment.common.codegen.internal.model.value.TextValue;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Method;
import com.speedment.generator.TranslatorSupport;
import com.speedment.generator.internal.DefaultJavaClassTranslator;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.config.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

import static com.speedment.common.codegen.constant.DefaultType.list;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.internal.common.injector.Injector;
import com.speedment.internal.common.injector.annotation.Inject;
import java.lang.reflect.Type;

/**
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class GeneratedControllerTranslator extends DefaultJavaClassTranslator<Table, Class> {

    private @Inject Injector injector;
    
    public GeneratedControllerTranslator(Table document) {
        super(document, Class::of);
    }
    
    @Override
    protected Class makeCodeGenModel(File file) {
        return newBuilder(file, getClassOrInterfaceName())
            .forEveryTable((clazz, table) -> {
                
                final Project project = getSupport().projectOrThrow();
                final TranslatorSupport<Project> projectSupport = new TranslatorSupport<>(injector, project);
                
                final Type appType = SimpleType.create(
                    projectSupport.basePackageName() + "." +
                    projectSupport.typeName() + "Application"
                );
                
                clazz.public_().abstract_();
                
                clazz.add(Field.of("app", appType)
                    .protected_()
                    .add(AnnotationUsage.of(Autowired.class))
                );
                
                clazz.add(Field.of("manager", getSupport().managerType())
                    .protected_()
                    .add(AnnotationUsage.of(Autowired.class))
                );
                
                file.add(Import.of(RequestMethod.class).static_().setStaticMember("GET"));
                file.add(Import.of(Collectors.class).static_().setStaticMember("toList"));
                
                clazz.add(Method.of("get", list(getSupport().entityType()))
                    .public_()
                    .add(AnnotationUsage.of(RequestMapping.class)
                        .put("value", new TextValue("/" + getSupport().variableName()))
                        .put("method", new ReferenceValue("GET"))
                    )
                    .add(Field.of("start", long.class)
                        .add(AnnotationUsage.of(RequestParam.class)
                            .put("value", new TextValue("start"))
                            .put("defaultValue", new TextValue("0"))
                        )
                    )
                    .add(Field.of("limit", long.class)
                        .add(AnnotationUsage.of(RequestParam.class)
                            .put("value", new TextValue("limit"))
                            .put("defaultValue", new TextValue("25"))
                        )
                    )
                    .add(
                        "return manager.stream()",
                        "    .skip(start)",
                        "    .limit(limit)",
                        "    .collect(toList());"
                    )
                );
            }).build();
    }
    
    @Override
    protected String getClassOrInterfaceName() {
        return "Generated" + getSupport().typeName() + "Controller";
    }
    
    @Override
    protected String getJavadocRepresentText() {
        return "The default REST controller logic";
    }

    @Override
    public boolean isInGeneratedPackage() {
        return true;
    }
}
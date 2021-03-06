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
package com.speedment.generator;

import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Interface;
import com.speedment.generator.internal.TranslatorKeyImpl;
import com.speedment.runtime.annotation.Api;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.config.Table;

import java.util.stream.Stream;

import static com.speedment.common.codegen.internal.util.StaticClassUtil.instanceNotAllowed;

/**
 * Standard implementations of the {@link TranslatorKey} interface to make it
 * easier and more efficient to mention the most common files generated by
 * Speedment.
 * 
 * @author  Emil Forslund
 * @author  Per Minborg
 * @since   2.3.0
 */
@Api(version = "3.0")
public final class StandardTranslatorKey {
    
    public final static TranslatorKey<Project, Interface> 
        APPLICATION           = new TranslatorKeyImpl<>("Application", Interface.class),
        GENERATED_APPLICATION = new TranslatorKeyImpl<>("GeneratedApplication", Interface.class);

    public final static TranslatorKey<Project, Class> 
        APPLICATION_IMPL              = new TranslatorKeyImpl<>("ApplicationImpl", Class.class),
        GENERATED_APPLICATION_IMPL    = new TranslatorKeyImpl<>("GeneratedApplicationImpl", Class.class),
        APPLICATION_BUILDER           = new TranslatorKeyImpl<>("ApplicationBuilder", Class.class),
        GENERATED_APPLICATION_BUILDER = new TranslatorKeyImpl<>("GeneratedApplicationBuilder", Class.class),
        GENERATED_METADATA            = new TranslatorKeyImpl<>("GeneratedMetadata", Class.class);
    
    public final static TranslatorKey<Table, Interface> 
        ENTITY            = new TranslatorKeyImpl<>("Entity", Interface.class),
        MANAGER           = new TranslatorKeyImpl<>("Manager", Interface.class),
        GENERATED_ENTITY  = new TranslatorKeyImpl<>("GeneratedEntity", Interface.class),
        GENERATED_MANAGER = new TranslatorKeyImpl<>("GeneratedManager", Interface.class);
    
    public final static TranslatorKey<Table, Class>
        ENTITY_IMPL            = new TranslatorKeyImpl<>("EntityImpl", Class.class),
        MANAGER_IMPL           = new TranslatorKeyImpl<>("ManagerImpl", Class.class),
        GENERATED_ENTITY_IMPL  = new TranslatorKeyImpl<>("GeneratedEntityImpl", Class.class),
        GENERATED_MANAGER_IMPL = new TranslatorKeyImpl<>("GeneratedManagerImpl", Class.class);

    /**
     * Returns a stream of the standard {@link TranslatorKey Translator Keys}
     * that is used on a 'per project' basis.
     * 
     * @return  stream of standard project {@link TranslatorKey Translator Keys}
     */
    public static Stream<TranslatorKey<Project, ? extends ClassOrInterface<?>>> projectTranslatorKeys() {
        return Stream.of(
            APPLICATION, GENERATED_APPLICATION, 
            APPLICATION_IMPL, GENERATED_APPLICATION_IMPL,
            APPLICATION_BUILDER, GENERATED_APPLICATION_BUILDER,
            GENERATED_METADATA
        );
    }
    
    /**
     * Returns a stream of the standard {@link TranslatorKey Translator Keys}
     * that is used on a 'per table' basis.
     * 
     * @return  stream of standard table {@link TranslatorKey Translator Keys}
     */
    public static Stream<TranslatorKey<Table, ? extends ClassOrInterface<?>>> tableTranslatorKeys() {
        return Stream.of(
            ENTITY, GENERATED_ENTITY,
            ENTITY_IMPL, GENERATED_ENTITY_IMPL,
            MANAGER, GENERATED_MANAGER,
            MANAGER_IMPL , GENERATED_MANAGER_IMPL
        );
    }

    /**
     * Utility classes should not be instantiated.
     */
    private StandardTranslatorKey() {
        instanceNotAllowed(getClass());
    }
}
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
package com.speedment.generator.internal.entity;

import com.speedment.common.codegen.internal.model.value.ReferenceValue;
import com.speedment.common.codegen.internal.model.value.TextValue;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Enum;
import com.speedment.common.codegen.model.EnumConstant;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.generator.TranslatorSupport;
import com.speedment.generator.internal.EntityAndManagerTranslator;
import com.speedment.generator.internal.util.EntityTranslatorSupport;
import com.speedment.generator.internal.util.FkHolder;
import com.speedment.runtime.config.Dbms;
import com.speedment.runtime.config.Table;
import com.speedment.runtime.config.identifier.FieldIdentifier;
import com.speedment.runtime.entity.Entity;
import com.speedment.runtime.internal.util.document.DocumentDbUtil;
import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import com.speedment.common.codegen.constant.DefaultType;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import static com.speedment.common.codegen.internal.util.Formatting.shortName;
import com.speedment.generator.component.TypeMapperComponent;
import static com.speedment.generator.internal.util.ColumnUtil.usesOptional;
import com.speedment.internal.common.injector.Injector;
import com.speedment.internal.common.injector.annotation.Inject;
import com.speedment.runtime.config.Column;
import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.config.mapper.primitive.PrimitiveTypeMapper;
import static com.speedment.runtime.internal.util.document.DocumentUtil.Name.DATABASE_NAME;
import com.speedment.runtime.util.OptionalBoolean;
import com.speedment.runtime.util.OptionalUtil;
import static com.speedment.runtime.internal.util.document.DocumentUtil.relativeName;
import com.speedment.runtime.manager.Manager;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 *
 * @author Emil Forslund
 * @author Per-Åke Minborg
 */
public final class GeneratedEntityTranslator extends EntityAndManagerTranslator<Interface> {
    
    public final static String IDENTIFIER_NAME = "Identifier";
    
    private @Inject Injector injector;
    private @Inject TypeMapperComponent typeMappers;

    public GeneratedEntityTranslator(Table table) {
        super(table, Interface::of);
    }

    @Override
    protected Interface makeCodeGenModel(File file) {

        final Enum identifier = Enum.of(IDENTIFIER_NAME)
            .add(Field.of("columnName", String.class).private_().final_())
            .add(SimpleParameterizedType.create(FieldIdentifier.class, getSupport().entityType()))
            .add(Constructor.of()
                .add(Field.of("columnName", String.class))
                .add("this.columnName = columnName;")
            )
            .add(Method.of("dbmsName", String.class).public_()
                .add(OVERRIDE)
                .add("return \"" + getSupport().dbmsOrThrow().getName() + "\";")
            )
            .add(Method.of("schemaName", String.class).public_()
                .add(OVERRIDE)
                .add("return \"" + getSupport().schemaOrThrow().getName() + "\";")
            )
            .add(Method.of("tableName", String.class).public_()
                .add(OVERRIDE)
                .add("return \"" + getSupport().tableOrThrow().getName() + "\";")
            )
            .add(Method.of("columnName", String.class).public_()
                .add(OVERRIDE)
                .add("return this.columnName;")
            );

        final Interface iface = newBuilder(file, getSupport().generatedEntityName())
            /**
             * General
             */
            .forEveryTable((intrf, col)
                -> intrf.public_()
                .add(identifier)
                .add(SimpleParameterizedType.create(Entity.class, getSupport().entityType()))
            )
            
            /**
             * Getters
             */
            .forEveryColumn((intrf, col) -> {
                final Type retType = getterReturnType(typeMappers, col);

                intrf.add(Method.of(GETTER_METHOD_PREFIX + getSupport().typeName(col), retType)
                    .set(Javadoc.of(
                        "Returns the " + getSupport().variableName(col)
                        + " of this " + getSupport().entityName()
                        + ". The " + getSupport().variableName(col)
                        + " field corresponds to the database column "
                        + relativeName(col, Dbms.class, DATABASE_NAME) + "."
                    ).add(RETURN.setText(
                        "the " + getSupport().variableName(col)
                        + " of this " + getSupport().entityName()
                    ))
                    )
                );
            })
            
            /**
             * Setters
             */
            .forEveryColumn((intrf, col) -> {
                intrf.add(Method.of(SETTER_METHOD_PREFIX + getSupport().typeName(col), getSupport().entityType())
                    .add(Field.of(getSupport().variableName(col), typeMappers.get(col).getJavaType(col)))
                    .set(Javadoc.of(
                        "Sets the " + getSupport().variableName(col)
                        + " of this " + getSupport().entityName()
                        + ". The " + getSupport().variableName(col)
                        + " field corresponds to the database column "
                        + relativeName(col, Dbms.class, DATABASE_NAME) + "."
                    )
                        .add(PARAM.setValue(getSupport().variableName(col)).setText("to set of this " + getSupport().entityName()))
                        .add(RETURN.setText("this " + getSupport().entityName() + " instance")))
                );
            }) 
            
            /**
             * Finders
             */
            .forEveryColumn((intrf, col) -> {
                EntityTranslatorSupport.getForeignKey(
                    getSupport().tableOrThrow(), col
                ).ifPresent(fkc -> {
                    final FkHolder fu = new FkHolder(injector, fkc.getParentOrThrow());
                    final TranslatorSupport<Table> fuSupport = fu.getForeignEmt().getSupport();
                    
                    file.add(Import.of(fuSupport.entityType()));
                    
                    intrf.add(Method.of(FINDER_METHOD_PREFIX + getSupport().typeName(col), 
                            col.isNullable() 
                                ? DefaultType.optional(fuSupport.entityType()) 
                                : fuSupport.entityType()
                        )
                        .set(Javadoc.of(
                                "Queries the specified manager for the referenced " + 
                                fuSupport.entityName() + ". If no such " + 
                                fuSupport.entityName() + 
                                " exists, an {@code NullPointerException} will be thrown."
                            ).add(DefaultJavadocTag.PARAM.setValue("foreignManager").setText("the manager to query for the entity"))
                            .add(DefaultJavadocTag.RETURN.setText("the foreign entity referenced"))
                        )
                        .add(Field.of("foreignManager", SimpleParameterizedType.create(
                            Manager.class, fuSupport.entityType()
                        )))
                    );
                });
            })
            
            /**
             * Fields
             */
            .forEveryColumn((intrf, col) -> {

                final EntityTranslatorSupport.ReferenceFieldType ref = 
                    EntityTranslatorSupport.getReferenceFieldType(
                        file, getSupport().tableOrThrow(), col, getSupport().entityType(), injector
                    );

                final Type entityType        = getSupport().entityType();
                final String shortEntityName = getSupport().entityName();

                file.add(Import.of(entityType));

                final String getter;
                if (usesOptional(col)) {
                    getter = "o -> OptionalUtil.unwrap(o." + GETTER_METHOD_PREFIX + getSupport().typeName(col) + "())";
                    file.add(Import.of(OptionalUtil.class));
                } else {
                    getter = shortEntityName + "::get" + getSupport().typeName(col);
                }

                final String finder = EntityTranslatorSupport.getForeignKey(getSupport().tableOrThrow(), col)
                    .map(fkc -> {
                        final FkHolder fu = new FkHolder(injector, fkc.getParentOrThrow());
                        final TranslatorSupport<Table> fuSupport = fu.getForeignEmt().getSupport();

                        if (col.isNullable()) {
                            return
                                ", " + fuSupport.entityName() + "." + fuSupport.namer().javaStaticFieldName(fu.getForeignColumn().getJavaName()) +
                                ", (entity, fkManager) -> OptionalUtil.unwrap(entity." + FINDER_METHOD_PREFIX + getSupport().typeName(col) + "(fkManager))";
                        } else {
                            return
                                ", " + fuSupport.entityName() + "." + fuSupport.namer().javaStaticFieldName(fu.getForeignColumn().getJavaName()) +
                                ", " + shortEntityName + "::" + FINDER_METHOD_PREFIX + getSupport().typeName(col);
                        }
                    }).orElse("");

                final String setter = ", " + shortEntityName + "::" + SETTER_METHOD_PREFIX + getSupport().typeName(col);

                final String constant = getSupport().namer().javaStaticFieldName(col.getJavaName());
                identifier.add(EnumConstant.of(constant).add(new TextValue(col.getName())));

                final String typeMapperCode;
                if (col.getTypeMapper().isPresent()) {
                    final String typeMapper = col.getTypeMapper().get();

                    if (PrimitiveTypeMapper.class.getName().equals(typeMapper)) {
                        file.add(Import.of(TypeMapper.class));
                        typeMapperCode = "TypeMapper.primitive()";
                    } else {
                        file.add(Import.of(SimpleType.create(typeMapper)));
                        typeMapperCode = "new " + shortName(typeMapper) + "()";
                    }
                } else {
                    typeMapperCode = "TypeMapper.identity()";
                    file.add(Import.of(TypeMapper.class));
                }

                file.add(Import.of(ref.implType));
                intrf.add(Field.of(getSupport().namer().javaStaticFieldName(col.getJavaName()), ref.type)
                    .final_()
                    .set(new ReferenceValue(
                        "new " + shortName(ref.implType.getTypeName())
                        + "<>(Identifier."
                        + constant
                        + ", "
                        + getter
                        + setter
                        + finder
                        + ", "
                        + typeMapperCode
                        + ", "
                        + DocumentDbUtil.isUnique(col)
                        + ")"
                    ))
                    .set(Javadoc.of(
                        "This Field corresponds to the {@link " + shortEntityName + "} field that can be obtained using the "
                        + "{@link " + shortEntityName + "#get" + getSupport().typeName(col) + "()} method."
                    )));
            })
            .build();

        return iface;
    }

    @Override
    protected String getJavadocRepresentText() {
        return "The generated base for the {@link " + 
            getSupport().entityType().getTypeName() + 
            "}-interface representing entities of the {@code " + 
            getDocument().getName() + "}-table in the database.";
    }

    @Override
    protected String getClassOrInterfaceName() {
        return getSupport().generatedEntityName();
    }

    @Override
    public boolean isInGeneratedPackage() {
        return true;
    }

    static Type getterReturnType(TypeMapperComponent typeMappers, Column col) {
        final Type retType;
        final Type type = typeMappers.get(col).getJavaType(col);

        if (usesOptional(col)) {
            if (type.equals(Integer.class)) {
                retType = OptionalInt.class;
            } else if (type.equals(Long.class)) {
                retType = OptionalLong.class;
            } else if (type.equals(Double.class)) {
                retType = OptionalDouble.class;
            } else if (type.equals(Boolean.class)) {
                retType = OptionalBoolean.class;
            } else {
                retType = SimpleParameterizedType.create(Optional.class, type);
            } 
        } else {
            retType = type;
        }

        return retType;
    }
}

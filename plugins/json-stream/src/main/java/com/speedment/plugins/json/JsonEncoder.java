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
package com.speedment.plugins.json;

import com.speedment.runtime.manager.Manager;
import com.speedment.runtime.field.trait.HasFinder;
import com.speedment.plugins.json.internal.JsonEncoderImpl;
import com.speedment.runtime.field.BooleanField;
import com.speedment.runtime.field.ByteField;
import com.speedment.runtime.field.CharField;
import com.speedment.runtime.field.DoubleField;
import com.speedment.runtime.field.Field;
import com.speedment.runtime.field.FloatField;
import com.speedment.runtime.field.IntField;
import com.speedment.runtime.field.LongField;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.ShortField;
import com.speedment.runtime.field.method.BooleanGetter;
import com.speedment.runtime.field.method.ByteGetter;
import com.speedment.runtime.field.method.CharGetter;
import com.speedment.runtime.field.method.DoubleGetter;
import com.speedment.runtime.field.method.Finder;
import com.speedment.runtime.field.method.FloatGetter;
import com.speedment.runtime.field.method.IntGetter;
import com.speedment.runtime.field.method.LongGetter;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.method.ShortGetter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An encoder that can transform Speedment entities to JSON.
 * <p>
 * <em>Example usage:</em> 
 * {@code
 *      Manager<Address> addresses    = app.managerOf(Address.class);
 *      Manager<Employee> employees   = app.managerOf(Employee.class);
 *      Manager<Employee> departments = app.managerOf(Department.class);
 *
 *      // Include street, zip-code and city only.
 *      JconEncoder<Address> addrEncoder = JsonEncoder.noneOf(addresses)
 *          .add(Address.STREET)
 *          .add(Address.ZIPCODE)
 *          .add(Address.CITY);
 *
 *      // Do not expose SSN but do inline the home address.
 *      JconEncoder<Employee> empEncoder = JsonEncoder.allOf(employees)
 *          .remove(Employee.SSN)
 *          .remove(Employee.DEPARTMENT);             // Go the other way around
 *          .add(Employee.HOME_ADDRESS, addrEncoder); // Foreign key
 *
 *      // Inline every employee in the department.
 *      JconEncoder<Department> depEncoder = JsonEncoder.allOf(departments)
 *          .putStreamer("employees", Department::employees, empEncoder);
 *
 *      String json = depEncoder.apply(departments.findAny().get());
 * }
 *
 * @param <ENTITY> entity type
 * 
 * @author Emil Forslund
 * @since  2.1.0
 */
public interface JsonEncoder<ENTITY> {
    
    /**************************************************************************/
    /*                             Getters                                    */
    /**************************************************************************/
    
    /**
     * Returns the manager for the entity type that this encoder works with.
     * 
     * @return  the manager
     */
    Manager<ENTITY> getManager();

    /**************************************************************************/
    /*                          Field Putters                                 */
    /**************************************************************************/
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param <V>    the java type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D, V> JsonEncoder<ENTITY> put(ReferenceField<ENTITY, D, V> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putByte(ByteField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putShort(ShortField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putInt(IntField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putLong(LongField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putFloat(FloatField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putDouble(DoubleField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putChar(CharField<ENTITY, D> field);
    
    /**
     * Include the specified field in the encoder.
     * 
     * @param <D>    the database type
     * @param field  the field type
     * @return       a reference to this encoder
     */
    <D> JsonEncoder<ENTITY> putBoolean(BooleanField<ENTITY, D> field);

    /**************************************************************************/
    /*                        Put Labels with Getters                         */
    /**************************************************************************/
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param <T>     the reference type of the field
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    <T> JsonEncoder<ENTITY> put(String label, ReferenceGetter<ENTITY, T> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putByte(String label, ByteGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putShort(String label, ShortGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putInt(String label, IntGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putLong(String label, LongGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putFloat(String label, FloatGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putDouble(String label, DoubleGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putChar(String label, CharGetter<ENTITY> getter);
    
    /**
     * Include the specified label in the encoder, using the specified getter to
     * determine its value. If the label is the same as an existing field name, 
     * it will be replaced.
     * 
     * @param label   the label to store it under
     * @param getter  how to retreive the value to store
     * @return        a reference to this encoder
     */
    JsonEncoder<ENTITY> putBoolean(String label, BooleanGetter<ENTITY> getter);
    
    /**************************************************************************/
    /*                        Put Fields with Finders                         */
    /**************************************************************************/
    
    /**
     * Include the specified foreign key field in the encoder, embedding foreign
     * entities as inner JSON objects.
     * 
     * @param <FK_ENTITY>  the type of the foreign entity
     * @param <FIELD>      the type of the field itself
     * @param field        the foreign key field
     * @param encoder      encoder for the foreign entity
     * @return             a reference to this encoder
     */
    <FK_ENTITY, FIELD extends Field<ENTITY> & HasFinder<ENTITY, FK_ENTITY>> 
    JsonEncoder<ENTITY> put(FIELD field, JsonEncoder<FK_ENTITY> encoder);

    /**************************************************************************/
    /*                        Put Labels with Finders                         */
    /**************************************************************************/
    
    /**
     * Include the specified label in this encoder, populating it with an inner
     * JSON object representing a foreign entity. This can be used to create
     * custom object hierarchies. If the specified label already exists in the
     * encoder, it will be replaced.
     * 
     * @param <FK_ENTITY>  the foreign entity type
     * @param label        the label to store it under
     * @param finder       a finder method used to retrieve the foreign entity
     * @param fkEncoder    an encoder for the foreign entity type
     * @return             a reference to this encoder
     */
    <FK_ENTITY> JsonEncoder<ENTITY> put(
            String label, 
            Finder<ENTITY, FK_ENTITY> finder, 
            JsonEncoder<FK_ENTITY> fkEncoder);

    /**************************************************************************/
    /*                        Put Labels with Find Many                       */
    /**************************************************************************/
    
    /**
     * Include the specified label in this encoder, using it to store an array
     * of inner JSON objects determined by applying the specified streaming
     * method. If the specified label already exists in the encoder, it will be 
     * replaced.
     * 
     * @param <FK_ENTITY>  the foreign entity type
     * @param label        the label to store it under
     * @param streamer     the streaming method to use
     * @param fkEncoder    encoder for the foreign entity type
     * @return             a reference to this encoder
     */
    <FK_ENTITY> JsonEncoder<ENTITY> putStreamer(
            String label, 
            BiFunction<Manager<ENTITY>, ENTITY, Stream<FK_ENTITY>> streamer, 
            JsonEncoder<FK_ENTITY> fkEncoder);

    /**
     * Include the specified label in this encoder, using it to store an array
     * of inner JSON objects determined by applying the specified streaming
     * method. If the specified label already exists in the encoder, it will be 
     * replaced.
     * 
     * @param <FK_ENTITY>  the foreign entity type
     * @param label        the label to store it under
     * @param streamer     the streaming method to use
     * @param fkEncoder    encoder for the foreign entity type
     * @return             a reference to this encoder
     */
    <FK_ENTITY> JsonEncoder<ENTITY> putStreamer(
            String label, 
            BiFunction<Manager<ENTITY>, ENTITY, Stream<FK_ENTITY>> streamer, 
            Function<FK_ENTITY, String> fkEncoder);

    /**************************************************************************/
    /*                             Remove by Label                            */
    /**************************************************************************/
    
    /**
     * Exclude the specified label from this encoder. If the label was not 
     * already included, this method will have no effect.
     * <p>
     * This method can be used to remove fields as well if the correct field
     * name is specified.
     * 
     * @param label  the label to remove
     * @return       a reference to this encoder
     */
    JsonEncoder<ENTITY> remove(String label);

    /**
     * Exclude the specified field from this encoder. If the field was not 
     * already included in the encoder, this method has no effect.
     * 
     * @param field  the field to remove
     * @return       a reference to this encoder
     */
    JsonEncoder<ENTITY> remove(Field<ENTITY> field);

    /**************************************************************************/
    /*                                  Encode                                */
    /**************************************************************************/
    
    /**
     * Encodes the specified entity using this encoder.
     * 
     * @param entity  the entity to encode
     * @return        the JSON encoded string
     */
    String apply(ENTITY entity);
    
    /**
     * Returns a collector that will use this encoder to encode any incoming
     * entities.
     * 
     * @return  the collector
     */
    JsonCollector<ENTITY> collector();

    /**************************************************************************/
    /*                         Static Factory Methods                         */
    /**************************************************************************/
    
    /**
     * Creates and return a new JsonEncoder with no fields added to the
     * renderer.
     *
     * @param <ENTITY> the Entity type
     * @param manager of the entity
     * @return a new JsonEncoder with no fields added to the renderer
     */
    static <ENTITY> JsonEncoder<ENTITY> noneOf(Manager<ENTITY> manager) {
        return JsonEncoderImpl.noneOf(manager);
    }

    /**
     * Creates and return a new JsonEncoder with all the Entity fields added to
     * the renderer. The field(s) will be rendered using their default class
     * renderer.
     *
     * @param <ENTITY> the Entity type
     * @param manager of the entity
     * @return a new JsonEncoder with all the Entity fields added to the
     * renderer
     */
    static <ENTITY> JsonEncoder<ENTITY> allOf(Manager<ENTITY> manager) {
        return JsonEncoderImpl.allOf(manager);
    }

    /**
     * Creates and return a new JsonEncoder with the provided Entity field(s)
     * added to the renderer. The field(s) will be rendered using their default
     * class renderer.
     *
     * @param <ENTITY> the Entity type
     * @param manager of the ENTITY
     * @param fields to add to the output renderer
     * @return a new JsonEncoder with the specified fields added to the renderer
     */
    @SafeVarargs
    @SuppressWarnings("varargs") // Using the array in a Stream.of() is safe
    static <ENTITY> JsonEncoder<ENTITY> of(Manager<ENTITY> manager, Field<ENTITY>... fields) {
        return JsonEncoderImpl.of(manager, fields);
    }
}
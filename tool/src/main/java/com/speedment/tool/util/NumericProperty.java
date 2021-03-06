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
package com.speedment.tool.util;

import com.speedment.runtime.annotation.Api;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;

/**
 * A type of {@link Property} that holds values that implement {@link Number}.
 * 
 * @author  Emil Forslund
 * @since   2.3.0
 */
@Api(version="3.0")
public interface NumericProperty extends Property<Number> {
    IntegerProperty asIntegerProperty();
    DoubleProperty asDoubleProperty();
    LongProperty asLongProperty();
    FloatProperty asFloatProperty();
}
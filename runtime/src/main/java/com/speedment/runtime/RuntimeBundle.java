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
package com.speedment.runtime;

import com.speedment.common.injector.InjectBundle;
import com.speedment.runtime.internal.config.dbms.MariaDbDbmsType;
import com.speedment.runtime.internal.config.dbms.MySqlDbmsType;
import com.speedment.runtime.internal.config.dbms.PostgresDbmsType;
import com.speedment.runtime.internal.config.dbms.StandardDbmsTypes;
import com.speedment.runtime.internal.AbstractSpeedment;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public class RuntimeBundle implements InjectBundle {

    @Override
    public Stream<Class<?>> injectables() {
        return AbstractSpeedment.include()
            .andThen(StandardDbmsTypes.include())
            .andThen(MariaDbDbmsType.include())
            .andThen(MySqlDbmsType.include())
            .andThen(PostgresDbmsType.include())
            .injectables();
    }

}

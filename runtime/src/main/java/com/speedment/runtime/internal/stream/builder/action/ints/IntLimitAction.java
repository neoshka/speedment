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
package com.speedment.runtime.internal.stream.builder.action.ints;

import com.speedment.runtime.internal.stream.builder.action.trait.HasLimit;
import com.speedment.runtime.stream.action.Action;

import java.util.stream.IntStream;

import static com.speedment.runtime.internal.stream.builder.action.StandardBasicAction.LIMIT;

/**
 *
 * @author pemi
 */
public final class IntLimitAction extends Action<IntStream, IntStream> implements HasLimit {

    private final long limit;

    public IntLimitAction(long maxSize) {
        super(s -> s.limit(maxSize), IntStream.class, LIMIT);
        this.limit = maxSize;
    }

    @Override
    public long getLimit() {
        return limit;
    }

}

/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
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
package com.speedment.internal.util.document;

import com.speedment.config.Document;
import com.speedment.config.db.trait.HasName;
import com.speedment.internal.util.Cast;
import static com.speedment.util.NullUtil.requireNonNulls;
import static com.speedment.util.StaticClassUtil.instanceNotAllowed;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author pemi
 */
public final class DocumentUtil {

    @SuppressWarnings("unchecked")

    public static <E extends Document> Optional<E> ancestor(Document document, final Class<E> clazz) {
        requireNonNulls(document, clazz);
        return document.ancestors()
                .filter(p -> clazz.isAssignableFrom(p.getClass()))
                .map(p -> (E) p)
                .findFirst();
    }

    public static <T extends Document & HasName, D extends Document & HasName> String relativeName(D document, final Class<T> from, Function<String, String> nameMapper) {
        requireNonNulls(document, from, nameMapper);
        final StringJoiner sj = new StringJoiner(".", "", ".").setEmptyValue("");
        final List<Document> ancestors = document.ancestors()/*.map(p -> (Parent<?>) p)*/.collect(toList());
        boolean add = false;
        for (final Document parent : ancestors) {
            if (from.isAssignableFrom(parent.getClass())) {
                add = true;
            }
            if (add) {
                nameFrom(parent).ifPresent(n -> sj.add(nameMapper.apply(n)));
            }
        }
        nameFrom(document).ifPresent(n -> sj.add(nameMapper.apply(n)));
        return sj.toString();
    }

    public static <T extends Document & HasName, D extends Document & HasName> String relativeName(D document, final Class<T> from) {
        return relativeName(document, from, s -> s);
    }

    private static Optional<String> nameFrom(Document document) {
        return Cast.cast(document, HasName.class).map(HasName::getName);
    }

    /**
     * Utility classes should not be instantiated.
     */
    private DocumentUtil() {
        instanceNotAllowed(getClass());
    }
}

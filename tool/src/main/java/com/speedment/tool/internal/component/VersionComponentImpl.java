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
package com.speedment.tool.internal.component;

import com.speedment.common.rest.Rest;
import com.speedment.runtime.internal.component.InternalOpenSourceComponent;
import com.speedment.tool.component.VersionComponent;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The default implementation of the {@link VersionComponent} interface that 
 * uses the public GitHub API to establish the latest released version.
 * 
 * @author  Emil Forslund
 * @since   3.0.0
 */
public final class VersionComponentImpl extends InternalOpenSourceComponent implements VersionComponent {

    @Override
    protected String getDescription() {
        return "Communicates with the GitHub API to determine if there are " +
            " any new versions of the software.";
    }
    
    @Override
    public CompletableFuture<String> latestVersion() {
        return Rest.connectHttps("api.github.com")
            .get("repos/speedment/speedment/releases")
            .thenApplyAsync(res -> {
                if (res.success()) {
                    final Optional<String> latest = res.decodeJsonArray()
                        .map(o -> {
                            @SuppressWarnings("unchecked")
                            final Map<String, Object> map = (Map<String, Object>) o;
                            return map;
                        })
                        .filter(m -> !((Boolean) m.get("draft")))
                        .map(m -> m.get("tag_name"))
                        .map(String.class::cast)
                        .sorted(Comparator.reverseOrder())
                        .findFirst();
                    
                    if (latest.isPresent()) {
                        return latest.get();
                    } else {
                        throw new RuntimeException(
                            "Could not establish the latest version."
                        );
                    } 
                } else {
                    throw new RuntimeException(
                        "Received an error '" + res.getText() + "' from the GitHub API."
                    );
                }
            });
    }
}
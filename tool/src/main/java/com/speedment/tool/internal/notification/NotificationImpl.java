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
package com.speedment.tool.internal.notification;

import com.speedment.tool.brand.Palette;
import com.speedment.tool.notification.Notification;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author  Emil Forslund
 * @since   2.3.0
 */
public final class NotificationImpl implements Notification {

    private final String message;
    private final FontAwesomeIcon icon;
    private final Palette palette;
    private final Runnable onClose;

    public NotificationImpl(String message, FontAwesomeIcon icon, Palette palette, Runnable onClose) {
        this.message = requireNonNull(message);
        this.icon    = requireNonNull(icon);
        this.palette = requireNonNull(palette);
        this.onClose = requireNonNull(onClose);
    }

    @Override
    public String text() {
        return message;
    }

    @Override
    public FontAwesomeIcon icon() {
        return icon;
    }

    @Override
    public Palette palette() {
        return palette;
    }

    @Override
    public Runnable onClose() {
        return onClose;
    }
}
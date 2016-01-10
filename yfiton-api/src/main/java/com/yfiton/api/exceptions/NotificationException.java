/*
 * Copyright 2015 Laurent Pellegrino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yfiton.api.exceptions;

/**
 * @author lpellegr
 */
public class NotificationException extends YfitonException {

    private String[] messages;

    public NotificationException() {
        super();
        this.messages = new String[0];
    }

    public NotificationException(String... messages) {
        this(messages, null);
    }

    public NotificationException(String message, Throwable cause) {
        this(new String[] { message }, cause);
    }

    public NotificationException(String[] messages, Throwable cause) {
        super(cause);
        this.messages = messages;
    }

    public NotificationException(Throwable cause) {
        super(cause);
        this.messages = new String[] {cause.getMessage()};
    }

    public String[] getMessages() {
        return messages;
    }

    @Override
    public String getMessage() {
        if (messages.length > 0) {
            return messages[0];
        }

        return null;
    }

}

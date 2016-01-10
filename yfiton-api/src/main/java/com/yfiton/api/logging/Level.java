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

package com.yfiton.api.logging;

/**
 * @author lpellegr
 */
public enum Level {

    ERROR("error", ch.qos.logback.classic.Level.ERROR),
    WARN("warn", ch.qos.logback.classic.Level.WARN),
    INFO("info", ch.qos.logback.classic.Level.INFO),
    DEBUG("debug", ch.qos.logback.classic.Level.DEBUG),
    TRACE("trace", ch.qos.logback.classic.Level.TRACE);

    private final String name;

    private final ch.qos.logback.classic.Level logbackLevel;

    Level(String name, ch.qos.logback.classic.Level logbackLevel) {
        this.name = name;
        this.logbackLevel = logbackLevel;
    }

    public ch.qos.logback.classic.Level getLogbackLevel() {
        return logbackLevel;
    }

    public String getName() {
        return name;
    }

    public static Level from(String name) {
        Level[] values = Level.values();

        for (Level loggingLevel : values) {
            if (loggingLevel.name.equals(name)) {
                return loggingLevel;
            }
        }

        return null;
    }

}

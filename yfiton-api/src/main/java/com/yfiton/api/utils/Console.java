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

package com.yfiton.api.utils;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Utility methods to handle data from command-line.
 *
 * @author lpellegr
 */
public class Console {

    public static String readParameterValueFromStdin(String parameterName) {
        return readPassword("Please enter value for parameter '%s':\n", parameterName);
    }

    public static String read(Logger logger, String format, Object... args) {
        return read(s -> logger.info(s), String.format(format, args));
    }

    private static String read(Consumer<String> consumer, String message) {
        consumer.accept(message);

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String readPassword(String format, Object... args) {
        return readPassword(String.format(format, args));
    }

    public static String readPassword(String message) {
        java.io.Console console = System.console();

        if (console == null) {
            return read(s -> System.out.print(s), message);
        }

        return new String(console.readPassword(message));
    }

}

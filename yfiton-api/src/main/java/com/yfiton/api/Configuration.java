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

package com.yfiton.api;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author lpellegr
 */
public class Configuration {

    private static final String CONFIGURATION_FOLDER_NAME = ".yfiton";

    public static final Path CONFIGURATION_FOLDER_PATH = Paths.get(System.getProperty("user.home"), CONFIGURATION_FOLDER_NAME);

    private static final String CACHE_FOLDER_NAME = "cache";

    public static final Path CACHE_FOLDER_PATH = CONFIGURATION_FOLDER_PATH.resolve(CACHE_FOLDER_NAME);

    private static final String APP_CONFIGURATION_FILE_NAME = "yfiton.ini";

    public static final Path APP_CONFIGURATION_FILE_PATH = CONFIGURATION_FOLDER_PATH.resolve(APP_CONFIGURATION_FILE_NAME);

    private static final String NOTIFIERS_CONFIGURATION_FOLDER_NAME = "notifiers";

    public static final Path NOTIFIERS_CONFIGURATION_FOLDER_PATH = CONFIGURATION_FOLDER_PATH.resolve(NOTIFIERS_CONFIGURATION_FOLDER_NAME);

    public static Path getConfigurationDir() throws IOException {
        return getOrCreateDir(CONFIGURATION_FOLDER_PATH);
    }

    public static Path getCacheDir() throws IOException {
        return getOrCreateDir(CACHE_FOLDER_PATH);
    }

    public static Path getNotifierCacheDirPath(Notifier notifier) throws IOException {
        return getOrCreateDir(CACHE_FOLDER_PATH.resolve(notifier.getKey()));
    }

    public static Path getNotifierConfigurationFilePath(Notifier notifier) throws IOException {
        return getNotifierConfigurationFilePath(notifier.getKey());
    }

    public static Path getNotifierConfigurationFilePath(String notifierKey) throws IOException {
        return getOrCreateDir(NOTIFIERS_CONFIGURATION_FOLDER_PATH).resolve(notifierKey + ".ini");
    }

    private static Path getOrCreateDir(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return path;
    }

    private static Path getOrCreateFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        return path;
    }

    public static void wipeCache() throws IOException {
        Files.walkFileTree(CONFIGURATION_FOLDER_PATH, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

}

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

package com.yfiton.oauth;

import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility functions related to OAuth.
 *
 * @author lpellegr
 */
public class OAuthUtils {

    public static void writeAuthorizationInfo(Path file, AuthorizationData data) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file));
        oos.writeObject(data);
        oos.close();
    }

    public static AuthorizationData readAuthorizationInfo(Path file) throws ConfigurationException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file));
        AuthorizationData data = (AuthorizationData) ois.readObject();
        ois.close();

        Files.delete(file);

        return data;
    }

}

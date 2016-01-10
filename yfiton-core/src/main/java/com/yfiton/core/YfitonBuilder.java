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

package com.yfiton.core;

import com.yfiton.api.Configuration;
import com.yfiton.api.Notifier;
import com.yfiton.api.exceptions.ConversionException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author lpellegr
 */
public class YfitonBuilder {

    private String notifierKey;

    private Notifier notifier;

    private Path configurationFile;

    private boolean displayStackTraces;

    public YfitonBuilder(String notifierKey) {
        this.notifierKey = notifierKey;
    }

    public YfitonBuilder(Notifier notifier) {
        this.notifier = notifier;
    }

    public YfitonBuilder displayStackTraces() {
        return displayStackTraces(true);
    }

    public YfitonBuilder displayStackTraces(boolean value) {
        displayStackTraces = value;
        return this;
    }

    public YfitonBuilder setConfigurationFile(Path path) {
        this.configurationFile = path;
        return this;
    }

    private Notifier resolve(String name) {
        Notifier found = NotifierRegistry.getInstance().find(name);

        if (found == null) {
            throw new IllegalArgumentException("Unrecognized notifier: " + name);
        }

        return found;
    }

    public Yfiton build() throws ConversionException, ConfigurationException {
        Notifier notifier = this.notifier;

        if (notifier == null) {
            notifier = resolve(this.notifierKey);
        }

        if (configurationFile == null) {
            try {
                configurationFile = Configuration.getNotifierConfigurationFilePath(notifier.getKey());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HierarchicalINIConfiguration hierarchicalConfiguration =
                new HierarchicalINIConfiguration(configurationFile.toFile());

        return new Yfiton(notifier, hierarchicalConfiguration, displayStackTraces);
    }

}

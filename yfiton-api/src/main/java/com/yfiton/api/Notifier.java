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


import com.google.common.base.Stopwatch;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A notifier provides basic abstractions to send notifications.
 *
 * @author lpellegr
 */
public abstract class Notifier {

    protected static final Logger log = LoggerFactory.getLogger(Notifier.class);

    protected final Stopwatch stopwatch = Stopwatch.createUnstarted();

    private HierarchicalINIConfiguration configuration;

    public Notifier() {
        try {
            configuration = new HierarchicalINIConfiguration(getConfigurationFilePath().toFile());
        } catch (ConfigurationException | IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    public NotificationResult handle(Parameters parameters) throws NotificationException {
        Optional<String> message = checkParameters(parameters).getMessage();

        if (message.isPresent()) {
            throw new NotificationException(message.get());
        }

        stopwatch.start();

        notify(parameters);

        stopwatch.stop();

        return new NotificationResult(getKey(), stopwatch);
    }

    protected abstract Check checkParameters(Parameters parameters);

    /**
     * Define actions to perform when the notifier is invoked to trigger a
     * notification with the specified parameter values.
     * <p/>
     * Checked exceptions must be caught and wrapped in a {@link NotificationResult}
     * if it must be displayed to users.
     *
     * @param parameters parameters explicitly set.
     * @throws NotificationException exception thrown and handled by the application based on logging level.
     */
    protected abstract void notify(Parameters parameters) throws NotificationException;

    /**
     * Unique key identifying a notifier among others.
     * It is an alphanumeric word starting with a letter.
     *
     * @return unique key identifying a notifier among others.
     */
    public abstract String getKey();

    /**
     * Notifier's name.
     *
     * @return notifier's name.
     */
    public abstract String getName();

    /**
     * Returns a description of the notifier.
     *
     * @return description of the notifier.
     */
    public abstract Optional<String> getDescription();

    /**
     * Returns an URL of the main service used by the notifier.
     *
     * @return an URL of the main service used by the notifier.
     */
    public abstract Optional<String> getUrl();

    protected Path getCacheDirPath() throws IOException {
        return Configuration.getNotifierCacheDirPath(this);
    }

    protected Path getConfigurationFilePath() throws IOException {
        return Configuration.getNotifierConfigurationFilePath(this);
    }

    protected HierarchicalINIConfiguration getConfiguration() {
        return configuration;
    }

    protected static class Check {

        private final Optional<String> message;

        public Check(Optional<String> message) {
            this.message = message;
        }

        public static final Check succeeded() {
            return new Check(Optional.empty());
        }

        public static final Check failed(String message) {
            return new Check(Optional.of(message));
        }

        public Optional<String> getMessage() {
            return message;
        }

    }

}

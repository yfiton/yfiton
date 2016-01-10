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

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.NotificationResult;
import com.yfiton.api.Notifier;
import com.yfiton.api.annotation.AnnotationProcessor;
import com.yfiton.api.exceptions.ConversionException;
import com.yfiton.api.exceptions.ParameterException;
import com.yfiton.api.parameter.Parameter;
import com.yfiton.api.parameter.ParameterValue;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.api.utils.Console;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Predicate.isEqual;

/**
 * Yfiton aims to handle users about command completion. It features several
 * notification methods based on the {@link Notifier} abstraction.
 *
 * @author lpellegr
 */
public class Yfiton {

    private static final Logger log = LoggerFactory.getLogger(Yfiton.class);

    private final Notifier notifier;

    // contains parameters defined in notifier implementations
    private final Map<String, Map<String, Parameter>> supportedParameters;

    // parameters loaded from preferences (i.e. ~/.yfiton)
    private final Map<String, Map<String, String>> configurationParameters;

    private final boolean displayStackTraces;

    private final String configurationFilePath;

    protected Yfiton(Notifier notifier, HierarchicalINIConfiguration configuration, boolean displayStackTraces) throws ConversionException {
        this.configurationParameters = loadPreferences(configuration, notifier);
        this.displayStackTraces = displayStackTraces;

        configurationFilePath = configuration.getFile().getAbsolutePath();

        if (log.isTraceEnabled()) {
            if (!Files.exists(Paths.get(configurationFilePath))) {
                log.trace("No configuration file detected in '{}'", configurationFilePath);
            } else {
                log.trace("Configuration loaded from '{}': {}", configurationFilePath, configurationParameters);
            }
        }

        this.notifier = notifier;
        this.supportedParameters = AnnotationProcessor.analyze(ImmutableSet.of(notifier));
    }

    private Map<String, Map<String, String>> loadPreferences(HierarchicalINIConfiguration configuration, Notifier notifier) {
        Set<String> sections = configuration.getSections();

        return sections.stream().filter(isEqual(null).negate().and(section -> notifier.getKey().equals(section)))
                .collect(Collectors.toMap(Function.identity(),
                        section -> configuration.getSection(section).
                                getRootNode().getChildren().stream().collect(
                                Collectors.toMap(ConfigurationNode::getName, node -> (String) node.getValue()))));
    }

    public NotificationResult notify(Map<String, String> parameters) throws ParameterException, ConversionException {
        Parameters receivedParameters = createReceivedParameters(notifier, parameters);

        injectParameterValues(notifier, receivedParameters);

        if (log.isDebugEnabled()) {
            logReceivedParameters(receivedParameters);
        }

        NotificationResult notificationResult = null;

        try {
            notificationResult = notifier.handle(receivedParameters);

            log.info("Notification sent in {} ms by using {}",
                    notificationResult.getExecutionTime(TimeUnit.MILLISECONDS), notifier.getKey());
        } catch (NotificationException e) {
            for (String message : e.getMessages()) {
                log.error(message);
            }

            if (displayStackTraces && e.getCause() != null) {
                log.error("Stacktrace:", e.getCause());
            }
        }

        return notificationResult;
    }

    private void logReceivedParameters(Parameters receivedParameters) {
        for (Map.Entry<String, ParameterValue> entry : receivedParameters) {
            String toStringValue = entry.getValue().toString();
            String value = toStringValue;

            if (entry.getValue().isHidden()) {
                value = Strings.repeat("*", toStringValue.length());
            }

            log.debug("Using parameter '" + entry.getKey() + "' with value '" + value + "'");
        }
    }

    private Parameters createReceivedParameters(Notifier notifier, Map<String, String> commandLineParameters) throws ParameterException, ConversionException {
        Map<String, ParameterValue> map = new HashMap<>();

        log.trace("Creating parameters to pass to notifier '{}'", notifier.getKey());

        Map<String, String> configurationParameters = this.configurationParameters.get(notifier.getKey());

        // add first parameters from preferences
        if (configurationParameters != null) {
            map.putAll(filterParameters(notifier, configurationParameters, true));
        }

        // add command line parameters
        // override if required parameters from preferences
        map.putAll(filterParameters(notifier, commandLineParameters, false));

        return new Parameters(ImmutableMap.copyOf(map));
    }

    private ImmutableMap<String, ParameterValue> filterParameters(Notifier notifier, Map<String, String> parameters, boolean handleConfigurationFileParameters) throws ConversionException, ParameterException {
        ImmutableMap.Builder<String, ParameterValue> validParameters = ImmutableMap.builder();

        boolean containsInvalidParameters = false;
        boolean lock = true;

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            Parameter parameterFound =
                    supportedParameters.get(notifier.getKey()).get(entry.getKey());

            if (parameterFound != null) {
                Object value = parameterFound.parse(entry.getValue());

                parameterFound.checkValidity(value);

                ParameterValue parameterValue = new ParameterValue(value, parameterFound.isHidden());

                validParameters.put(
                        entry.getKey(), parameterValue);
            } else {
                containsInvalidParameters = true;

                if (lock) {
                    if (handleConfigurationFileParameters) {
                        log.warn("Configuration file " + configurationFilePath + " contains incorrect definitions");
                    }

                    lock = false;
                }

                log.warn("Invalid '" + entry.getKey() + "=" + entry.getValue() + "' parameter");
            }
        }

        if (containsInvalidParameters) {
            log.warn("Option -dn can be used to describe available parameters");
        }

        return validParameters.build();
    }

    private void injectParameterValues(Notifier notifiers, Parameters receivedParameters) throws ParameterException {
        for (Field field : AnnotationProcessor.getAllFields(notifier.getClass())) {
            // annotations available in the class or in its hierarchy
            com.yfiton.api.annotation.Parameter[] annotations =
                    field.getAnnotationsByType(com.yfiton.api.annotation.Parameter.class);

            if (annotations.length == 0) {
                continue;
            }

            com.yfiton.api.annotation.Parameter annotation = annotations[0];

            String parameterName = annotation.name();
            if (parameterName.isEmpty()) {
                parameterName = field.getName();
            }

            // retrieve object representation of the parameter from already parsed annotations
            Parameter parameter = supportedParameters.get(notifier.getKey()).get(parameterName);

            if (parameter != null) {
                try {
                    field.setAccessible(true);

                    Object receivedValue = receivedParameters.getValue(parameter);

                    if (receivedValue != null) {
                        field.set(notifier, receivedValue);
                    } else if (receivedValue == null && parameter.isHidden() && parameter.isRequired()) {
                        field.set(notifier, Console.readParameterValueFromStdin(parameterName));
                    } else if (receivedValue == null && parameter.isRequired()) {
                        throw new ParameterException("Missing required parameter '" + parameterName + "'");
                    }
                } catch (IllegalAccessException e) {
                    log.warn("Cannot inject parameter value for parameter named '{}'", parameter.getName());
                }
            }
        }
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public Map<String, Map<String, Parameter>> getSupportedParameters() {
        return supportedParameters;
    }

}

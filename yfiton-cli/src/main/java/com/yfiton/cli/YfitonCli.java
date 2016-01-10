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

package com.yfiton.cli;

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.yfiton.api.logging.Level;
import com.yfiton.api.Notifier;
import com.yfiton.api.annotation.AnnotationProcessor;
import com.yfiton.api.exceptions.ConversionException;
import com.yfiton.api.exceptions.ParameterException;
import com.yfiton.api.parameter.Parameter;
import com.yfiton.core.NotifierRegistry;
import com.yfiton.core.Yfiton;
import com.yfiton.core.YfitonBuilder;
import com.yfiton.api.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YfitonCli allows to use Yfiton from the command line.
 *
 * @author lpellegr
 */
public class YfitonCli {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Yfiton.class);

    private static int exitCode = 0;

    private JCommander commandLineParser;

    public static void main(String[] args) throws IOException, InterruptedException {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(ch.qos.logback.classic.Level.ERROR);

        YfitonCli cli = new YfitonCli();

        CommandLine mainCommand;

        try {
            mainCommand = cli.parse(args);
        } catch (com.beust.jcommander.ParameterException e) {
            logError(e.getMessage(), "Use -h option to display help");
            System.exit(exitCode);
            return;
        }

        cli.adjustLoggingLevel(mainCommand);

        YfitonBuilder builder = new YfitonBuilder(mainCommand.notifier);
        builder.displayStackTraces(mainCommand.displayStackTraces);
        builder.setConfigurationFile(Configuration.getNotifierConfigurationFilePath(mainCommand.notifier));

        if (cli.handleOptions(mainCommand)) {
            return;
        }

        try {
            builder.build().notify(mainCommand.parameters);
        } catch (ConfigurationException e) {
            logError(e.getMessage());
        } catch (ConversionException e) {
            logError(e.getMessage(), e.getResolution());
        } catch (ParameterException e) {
            logError(e.getMessage());
        }

        System.exit(exitCode);
    }

    private static void logError(String... messages) {
        exitCode = 1;

        for (String msg : messages) {
            log.error(msg);
        }
    }

    private CommandLine parse(String[] args) throws IOException {
        CommandLine mainCommand = new CommandLine();
        commandLineParser = new JCommander(mainCommand);
        commandLineParser.setProgramName("yfiton");
        commandLineParser.setCaseSensitiveOptions(true);
        commandLineParser.parse(args);

        return mainCommand;
    }

    private void adjustLoggingLevel(CommandLine mainCommand) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        mainCommand.logLevel = mainCommand.logLevel.toLowerCase();

        Level loggingLevel =
                Level.from(mainCommand.logLevel);

        if (loggingLevel != null) {
            root.setLevel(loggingLevel.getLogbackLevel());
        } else {
            throw new IllegalArgumentException("Unknown log level: " + mainCommand.logLevel);
        }
    }

    private boolean handleOptions(CommandLine mainCommand) {
        if (mainCommand.help) {
            commandLineParser.usage();
            return true;
        }

        if (mainCommand.displayVersion) {
            System.out.println("Yfiton " + this.getClass().getPackage().getImplementationVersion());
            return true;
        }

        if (mainCommand.describeNotifier != null) {
            printAvailableNotifiers(ImmutableList.of(mainCommand.describeNotifier), true);
            return true;
        }

        if (mainCommand.headless) {
            System.setProperty("yfiton.headless.enforced", "true");
        }


        if (mainCommand.listNotifiers) {
            printAvailableNotifiers(mainCommand.main, false);
            return true;
        }

        if (mainCommand.wipeCache) {
            try {
                Configuration.wipeCache();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        return false;
    }

    private void printAvailableNotifiers(List<String> selectedNotifiersToDescribe, boolean describeParameters) {
        Map<String, Notifier> notifiers = NotifierRegistry.getInstance().getAvailable();
        Map<String, Map<String, Parameter>> parameters = AnnotationProcessor.analyze(notifiers.values());

        List<String> notifierToDescribe = new ArrayList<>(selectedNotifiersToDescribe);

        if (selectedNotifiersToDescribe.isEmpty()) {
            notifiers.values().forEach(n -> notifierToDescribe.add(n.getKey()));
        }

        notifierToDescribe.stream().sorted().forEach(
                key -> {
                    Notifier notifier = notifiers.get(key);

                    if (notifier == null) {
                        log.warn("No notifier found with key '" + key + "'");
                        return;
                    }

                    System.out.println(
                            getNotifierHelp(
                                    notifier, parameters.get(key), describeParameters));
                });
    }

    private String getNotifierHelp(Notifier notifier, Map<String, Parameter> parameters, boolean describeParameters) {
        StringBuilder buf = new StringBuilder();

        buf.append(notifier.getKey());
        buf.append("\n");

        if (notifier.getDescription().isPresent()) {
            buf.append("  ");
            buf.append(Joiner.on("\n  ").join(notifier.getDescription().get().split("\\r?\\n")));
            buf.append("\n");
        }

        if (notifier.getUrl().isPresent()) {
            buf.append("  ");
            buf.append(notifier.getUrl().get());
            buf.append("\n");
        }

        if (describeParameters && parameters.size() > 0) {
            buf.append("\n");
            buf.append("  Accepted parameters:");
            buf.append("\n");

            parameters.forEach((parameterName, parameter) -> {
                buf.append("    ");
                buf.append(parameterName);

                if (parameter.isRequired()) {
                    buf.append(" (required)");
                }

                buf.append("\n");

                if (!parameter.getDescription().isEmpty()) {
                    buf.append("      ");
                    buf.append(WordUtils.wrap(parameter.getDescription(), 66, "\n      ", true));
                    buf.append("\n");
                }

                buf.append("      ");
                buf.append("Type: " + parameter.getType().getSimpleName());
                buf.append("\n");

                if (!parameter.isRequired()) {
                    buf.append("      ");
                    buf.append("Default: ");
                    buf.append(getDefaultValue(parameter));
                    buf.append("\n");
                }
            });
        }

        return buf.toString();
    }

    private String getDefaultValue(Parameter parameter) {
        Object defaultValue = parameter.getDefaultValue();

        if (defaultValue == null) {
            return "undefined";
        } else if (defaultValue instanceof List) {
            return "[" + Joiner.on(", ").join((List) defaultValue) + "]";
        }

        return defaultValue.toString();
    }

}

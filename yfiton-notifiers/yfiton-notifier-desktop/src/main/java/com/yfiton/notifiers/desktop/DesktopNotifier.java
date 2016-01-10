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

package com.yfiton.notifiers.desktop;

import com.yfiton.api.Notifier;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.notifiers.desktop.converters.LineBreakConverter;
import com.yfiton.notifiers.desktop.validators.ParseAsIntegerValidator;
import com.yfiton.notifiers.desktop.validators.PosValidator;
import com.yfiton.notifiers.desktop.validators.TypeValidator;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;


/**
 * @author lpellegr
 */
public class DesktopNotifier extends Notifier {

    @Parameter(description = "The duration that the notification should show, after which it will be hidden", validator = ParseAsIntegerValidator.class)
    private String hideAfter = "5";

    @Parameter(description = "The text to show in the notification", converter = LineBreakConverter.class, required = true)
    private String message;

    @Parameter(description = "The position of the notification on screen", validator = PosValidator.class)
    private String position = Pos.TOP_CENTER.name();

    @Parameter(description = "Notification type: [info, error, success, warning]", validator = TypeValidator.class)
    private String type = "info";

    @Override
    protected Check checkParameters(Parameters parameters) {
        return Check.succeeded();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(
                "java", "-classpath", getClasspath(), DesktopNotification.class.getName(),
                "--hideAfter=" + hideAfter, "--message=" + message,
                "--position=" + position.toUpperCase(), "--type=" + type);

        try {
            Process process = processBuilder.start();
            int returnCode = process.waitFor();

            if (returnCode != 0) {
                throw new NotificationException("Error occurred while waiting for process: return code " + parameters);
            }
        } catch (IOException | InterruptedException e) {
            throw new NotificationException(e.getMessage());
        }
    }

    private String getClasspath() {
        StringBuilder result = new StringBuilder();

        for (URL url : ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs()) {
            result.append(new File(url.getPath()));
            result.append(System.getProperty("path.separator"));
        }

        return result.toString();
    }

    @Override
    public String getKey() {
        return "desktop";
    }

    @Override
    public String getName() {
        return "Desktop Notifier";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Display a rich desktop notification.");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.empty();
    }

}

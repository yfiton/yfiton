/*
 * Copyright 2016 Laurent Pellegrino
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

package com.yfiton.oauth.receiver;

import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthUtils;
import com.yfiton.oauth.receiver.graphical.WebBrowser;
import com.yfiton.oauth.receiver.graphical.WebEngineListener;
import org.apache.commons.configuration.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * OAuth 2.0 authorization code receiver that asks user to fill
 * Web page form from a graphical Windows.
 *
 * @author lpellegr
 */
public class GraphicalReceiver implements AuthorizationDataReceiver {

    private final Class<? extends WebEngineListener> webEngineListenerClazz;

    private final boolean debug;

    public GraphicalReceiver(Class<? extends WebEngineListener> webEngineListenerClazz, boolean debug) {
        this.debug = debug;
        this.webEngineListenerClazz = webEngineListenerClazz;
    }

    @Override
    public AuthorizationData requestAuthorizationData(String authorizationUrl, String authorizationCodeParameterName, String... requestParameterNames) throws NotificationException {
        try {
            File tmpFile = File.createTempFile("yfiton", ".auth");

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.inheritIO();
            processBuilder.command(
                    "java",
                    //"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005",
                    "-classpath", getClasspath(), WebBrowser.class.getName(),
                    "--authorization-code-parameter-name=" + authorizationCodeParameterName,
                    "--authorization-file=" + tmpFile.getAbsolutePath(),
                    "--authorization-url=" + authorizationUrl,
                    "--debug=" + (debug ? "true" : "false"),
                    "--webengine-listener-class=" + webEngineListenerClazz.getName());

            Process process = processBuilder.start();

            int returnCode = process.waitFor();

            switch (returnCode) {
                case 0:
                    return OAuthUtils.readAuthorizationInfo(tmpFile.toPath());
                case 255:
                    throw new NotificationException("Authorization process aborted");
                default:
                    throw new NotificationException(
                            "Error occurred while waiting for process: return code " + returnCode);
            }
        } catch (ClassNotFoundException | ConfigurationException | IOException | InterruptedException e) {
            throw new NotificationException(e.getMessage());
        }
    }

    private String getClasspath() {
        StringBuilder result = new StringBuilder();

        for (URL url : ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs()) {
            result.append(new File(url.getPath()));
            result.append(File.pathSeparatorChar);
        }

        return result.toString();
    }

}

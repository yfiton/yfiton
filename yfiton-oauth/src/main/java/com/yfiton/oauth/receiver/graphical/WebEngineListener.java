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

package com.yfiton.oauth.receiver.graphical;


import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author lpellegr
 */
public abstract class WebEngineListener implements ChangeListener<Worker.State> {

    protected final String authorizationFile;

    protected final String authorizationCodeParameterName;

    protected boolean firstPageLoaded;

    protected final WebEngine webEngine;

    public WebEngineListener(WebEngine webEngine, String authorizationFile, String authorizationCodeParameterName) {
        this.authorizationFile = authorizationFile;
        this.authorizationCodeParameterName = authorizationCodeParameterName;
        this.firstPageLoaded = false;
        this.webEngine = webEngine;
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (!firstPageLoaded) {
            firstPageLoaded = true;
            return;
        }

        doAction(observable, oldValue, newValue);
    }

    public abstract void doAction(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue);

    protected void save(AuthorizationData data) {
        try {
            OAuthUtils.writeAuthorizationInfo(
                    Paths.get(authorizationFile), data);

            Platform.exit();
        } catch (IOException e) {
            System.exit(1);
        }
    }

}

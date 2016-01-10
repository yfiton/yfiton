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

package com.yfiton.notifiers.twitter;

import com.sun.webkit.dom.HTMLElementImpl;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.receiver.graphical.WebEngineListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import org.w3c.dom.NodeList;

/**
 * @author lpellegr
 */
public class TwitterWebEngineListener extends WebEngineListener {

    public TwitterWebEngineListener(WebEngine webEngine, String authorizationFile, String authorizationCodeParameterName) {
        super(webEngine, authorizationFile, authorizationCodeParameterName);
    }

    @Override
    public void doAction(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            NodeList nodeList = webEngine.getDocument().getElementsByTagName("code");

            if (nodeList != null) {
                HTMLElementImpl htmlNode = (HTMLElementImpl) nodeList.item(0);

                if (htmlNode != null) {
                    String authorizationCode = htmlNode.getInnerText();

                    save(new AuthorizationData(authorizationCode));
                }
            }
        }
    }

}

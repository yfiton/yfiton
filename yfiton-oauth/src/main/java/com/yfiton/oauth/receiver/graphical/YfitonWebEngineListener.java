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

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthNotifier;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpellegr
 */
public class YfitonWebEngineListener extends WebEngineListener {

    public YfitonWebEngineListener(WebEngine webEngine, String authorizationFile, String authorizationCodeParameterName) {
        super(webEngine, authorizationFile, authorizationCodeParameterName);
    }

    @Override
    public void doAction(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        String location = webEngine.getLocation();

        if (newValue == Worker.State.SUCCEEDED
                && location.startsWith(OAuthNotifier.YFITON_OAUTH_CALLBACK_URL)) {

            AuthorizationData transformed = getParameters(location);

            save(transformed);
        }
    }

    protected AuthorizationData getParameters(String location) {
        List<String> fragmentOrParameters = getFragmentOrParameters(location);

        Map<String, String> parameters = new HashMap<>();

        for (String string : fragmentOrParameters) {
            if (!string.isEmpty()) {
                parameters.putAll(Splitter.on('&').trimResults().withKeyValueSeparator("=").split(string));
            }
        }

        return new AuthorizationData(
                parameters.get(authorizationCodeParameterName), parameters);
    }

    private List<String> getFragmentOrParameters(String location) {
        List<String> fragmentOrParameters = Splitter.on(CharMatcher.anyOf("#?")).splitToList(location);
        return fragmentOrParameters.subList(1, fragmentOrParameters.size());
    }

}

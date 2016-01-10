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

import com.google.common.collect.ImmutableMap;
import com.yfiton.api.utils.Console;
import com.yfiton.oauth.AuthorizationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth 2.0 authorization code receiver that prompts user to paste
 * authorization data copied from the browser.
 *
 * @author lpellegr
 */
public class PromptReceiver implements AuthorizationDataReceiver {

    private final Logger log;

    public PromptReceiver(Logger log) {
        this.log = log;
    }

    public AuthorizationData requestAuthorizationData(String authorizationUrl, String authorizationCodeParameterName, String... requestParameterNames) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        String authorizationCode = readRequestParameterValue(authorizationCodeParameterName);

        for (String fieldName : requestParameterNames) {
            builder.put(fieldName, readRequestParameterValue(fieldName));
        }

        return new AuthorizationData(authorizationCode, builder.build());
    }

    protected String readRequestParameterValue(String fieldName) {
        return Console.read(log, "Please enter value for request parameter '%s':", fieldName);
    }

}

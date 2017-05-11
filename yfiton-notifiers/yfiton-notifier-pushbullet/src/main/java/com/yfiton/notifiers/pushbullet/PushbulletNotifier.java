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

package com.yfiton.notifiers.pushbullet;

import com.github.sheigutn.pushbullet.Pushbullet;
import com.github.sheigutn.pushbullet.items.file.UploadFile;
import com.github.sheigutn.pushbullet.items.push.sendable.defaults.SendableFilePush;
import com.github.sheigutn.pushbullet.items.push.sendable.defaults.SendableLinkPush;
import com.github.sheigutn.pushbullet.items.push.sendable.defaults.SendableNotePush;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.oauth.AccessTokenData;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthNotifier;
import com.yfiton.oauth.receiver.graphical.YfitonWebEngineListener;
import com.yfiton.oauth.receiver.PromptReceiver;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author lpellegr
 */
public class PushbulletNotifier extends OAuthNotifier {

    private static final String CLIENT_ID = "hjT07gVHUYnzN1iVlWIFU7K1Sxype0bf";

    @Parameter(description = "Access token allowing Pushbullet to know who you are")
    private String accessToken;

    @Parameter(description = "The note's title")
    private String title = "Yfiton";

    @Parameter(description = "A message associated with the push notification", required = true)
    private String body;

    @Parameter(description = "The url to open when link type is used")
    private String url;

    @Parameter
    private String file;

    public PushbulletNotifier() {
        super(CLIENT_ID, null);
    }

    @Override
    protected Check checkParameters(Parameters parameters) {
        return Check.succeeded();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        Pushbullet pushbullet = new Pushbullet(getAccessToken());

        try {
            if (file != null) {
                pushFile(pushbullet);
            } else if (url != null) {
                pushLink(pushbullet);
            } else {
                pushNote(pushbullet);
            }
        } catch (Exception e) {
            throw new NotificationException("Calling Pushbullet API has failed: " + e.getMessage(), e);
        }
    }

    private String getAccessToken() throws NotificationException {
        HierarchicalINIConfiguration configuration = getConfiguration();

        if (accessToken != null) {
            return accessToken;
        }

        return configuration.getString(KEY_ACCESS_TOKEN);
    }

    private void checkNotNull(Object obj, String name) throws NotificationException {
        if (obj == null) {
            throw new NotificationException("Missing " + name + " option required.");
        }
    }

    private void pushFile(Pushbullet pushbullet) throws NotificationException {
        UploadFile uploadFile = pushbullet.uploadFile(new File(file));
        pushbullet.push(new SendableFilePush(body, uploadFile));
    }

    private void pushLink(Pushbullet pushbullet) throws NotificationException {
        checkNotNull(title, "title");
        pushbullet.push(new SendableLinkPush(title, body, url));
    }

    private void pushNote(Pushbullet pushbullet) throws NotificationException {
        checkNotNull(title, "title");
        pushbullet.push(new SendableNotePush(title, body));
    }

    @Override
    protected Optional<String> getAccessTokenUrl(String authorizationCode) {
        return Optional.of("https://api.pushbullet.com/oauth2/token");
    }

    @Override
    protected String getAuthorizationUrl(String stateParameterValue) {
        return "https://www.pushbullet.com/authorize?client_id=" + super.getClientId()
                + "&redirect_uri=" + YFITON_OAUTH_CALLBACK_URL + "&response_type=code";
    }

    @Override
    public String getKey() {
        return "pushbullet";
    }

    @Override
    public String getName() {
        return "Pushbullet";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Send notification on your devices using Pushbullet.");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.of("https://www.pushbullet.com");
    }

    @Override
    protected Optional<String> getStateRequestParameterName() {
        return Optional.empty();
    }

    @Override
    protected AccessTokenData requestAccessTokenData(AuthorizationData authorizationData) throws NotificationException {
        try {
            String authorizationCode = authorizationData.getAuthorizationCode();

            String response =
                    Request.Post(getAccessTokenUrl(authorizationCode).get()).bodyForm(
                            Form.form()
                                    .add("client_id", getClientId())
                                    .add("client_secret", getClientSecret())
                                    .add("code", authorizationCode)
                                    .add("grant_type", "authorization_code").build())
                            .execute().returnContent().asString();

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();

            String accessToken = json.get("access_token").getAsString();
            String tokenType = json.get("token_type").getAsString();

            return new AccessTokenData(accessToken, ImmutableMap.of("tokenType", tokenType));
        } catch (IOException e) {
            throw new NotificationException(e);
        }
    }

}

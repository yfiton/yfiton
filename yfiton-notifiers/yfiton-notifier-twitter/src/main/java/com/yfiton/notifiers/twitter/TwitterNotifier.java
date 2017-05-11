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

import com.google.common.collect.ImmutableMap;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.oauth.AccessTokenData;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthNotifier;
import com.yfiton.oauth.receiver.PromptReceiver;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import twitter4j.*;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Optional;

/**
 * @author lpellegr
 */
public class TwitterNotifier extends OAuthNotifier {

    private static final String KEY_ACCESS_TOKEN = "accessToken";

    private static final String KEY_ACCESS_TOKEN_SECRET = "accessTokenSecret";

    private static final String CLIENT_ID = "XTZptYy7i23qIlkNbapCGy3kA";

    private static final String SECRET_ID = "rP195fVhnVHqMW1qOIlUX1wOFkGIOEKosFAsllGto27CDv4P5w";

    @Parameter(description = "OAuth access token defining what data can be accessed from the service that is contacted")
    private String accessToken;

    @Parameter(description = "OAuth access token secret associated to access token")
    private String accessTokenSecret;

    @Parameter(description = "Twitter status aka \"tweet\" to post", required = true, validator = StatusValidator.class)
    private String status;

    private final Twitter twitter;

    private RequestToken requestToken;

    public TwitterNotifier() throws NotificationException {
        super(CLIENT_ID, SECRET_ID,
                PromptReceiver.class,
                "com.yfiton.notifiers.twitter.TwitterWebEngineListener");

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setGZIPEnabled(true)
                .setDebugEnabled(true)
                .setOAuthConsumerKey(getClientId())
                .setOAuthConsumerSecret(getClientSecret());

        com.yfiton.notifiers.twitter.AccessToken accessToken = getAccessToken();

        if (accessToken != null) {
            configurationBuilder.setOAuthAccessToken(accessToken.getAccessToken());
            configurationBuilder.setOAuthAccessTokenSecret(accessToken.getAccessTokenSecret());
        }

        twitter =
                new TwitterFactory(
                        configurationBuilder.build()).getInstance();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        try {
            StatusUpdate statusUpdate = new StatusUpdate(status);
            Status status = twitter.updateStatus(statusUpdate);

            log.info("https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());
        } catch (TwitterException e) {
            throw new NotificationException(
                    new String[]{
                            e.getErrorMessage(),
                            "Error code " + e.getErrorCode()
                                    + ": https://dev.twitter.com/overview/api/response-codes"}, e);
        }
    }

    @Override
    protected Check checkParameters(Parameters parameters) {
        if (accessToken != null && accessTokenSecret == null) {
            return Check.failed("Access token defined but secret missing.");
        }

        if (accessToken == null && accessTokenSecret != null) {
            return Check.failed("Secret defined but access token missing.");
        }

        return Check.succeeded();
    }

    protected com.yfiton.notifiers.twitter.AccessToken getAccessToken() {
        if (accessToken != null && accessTokenSecret != null) {
            return new com.yfiton.notifiers.twitter.AccessToken(accessToken, accessTokenSecret);
        }

        HierarchicalINIConfiguration section = getConfiguration();
        String accessToken = section.getString(KEY_ACCESS_TOKEN);
        String accessTokenSecret = section.getString(KEY_ACCESS_TOKEN_SECRET);

        if (accessToken == null || accessTokenSecret == null) {
            return null;
        }

        return new com.yfiton.notifiers.twitter.AccessToken(accessToken, accessTokenSecret);
    }

    @Override
    protected String getAuthorizationUrl(String stateParameterValue) throws NotificationException {
        return getRequestToken().getAuthorizationURL();
    }

    public RequestToken getRequestToken() throws NotificationException {
        if (requestToken == null) {
            try {
                requestToken = twitter.getOAuthRequestToken();
            } catch (TwitterException e) {
                throw new NotificationException(e);
            }
        }

        return requestToken;
    }

    @Override
    protected Optional<String> getStateRequestParameterName() {
        return Optional.empty();
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return getAccessToken() == null;
    }

    @Override
    protected AccessTokenData requestAccessTokenData(AuthorizationData authorizationData) throws NotificationException {
        try {
            twitter4j.auth.AccessToken oAuthAccessToken =
                    twitter.getOAuthAccessToken(requestToken, authorizationData.getAuthorizationCode());

            return new AccessTokenData(
                    oAuthAccessToken.getToken(),
                    ImmutableMap.of(KEY_ACCESS_TOKEN_SECRET, oAuthAccessToken.getTokenSecret()));
        } catch (TwitterException e) {
            throw new NotificationException(e);
        }
    }

    @Override
    protected Optional<String> getAccessTokenUrl(String authorizationCode) {
        return Optional.empty();
    }

    @Override
    public String getKey() {
        return "twitter";
    }

    @Override
    public String getName() {
        return "Twitter";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Send short 140-character messages called \"tweets\".");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.of("https://twitter.com");
    }

}

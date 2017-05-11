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

package com.yfiton.notifiers.slack;

import allbegray.slack.RestUtils;
import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.oauth.AccessTokenData;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthNotifier;
import com.yfiton.oauth.receiver.PromptReceiver;
import com.yfiton.oauth.receiver.graphical.YfitonWebEngineListener;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lpellegr
 */
public class SlackNotifier extends OAuthNotifier {

    private static final String KEY_DEFAULT_TEAM_ID = "defaultTeamId";

    @Parameter(description = "Trigger configuration for a new Slack team account if enabled")
    private boolean configureNewTeam = false;

    @Parameter(description = "Channel (e.g. #random), private group, or IM channel (@user) to send message to")
    private String channel = "#general";

    @Parameter(description = "Text of the message to send", required = true)
    private String message;

    @Parameter(description = "Use the specified pre-configured team ID for sending messages")
    private String teamId;

    public SlackNotifier() {
        super("13619498982.13619874391", "8c1846b68f3ca8cd67926c2d85f0f879");

        if (!log.isDebugEnabled() && !log.isTraceEnabled()) {
            Logger.getLogger(RestUtils.class.getName()).setLevel(Level.OFF);
        }
    }

    @Override
    protected Check checkParameters(Parameters parameters) {
        return Check.succeeded();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        SubnodeConfiguration config = retrieveTeamInformation();

        if (config == null) {
            throw new NotificationException("Invalid configuration");
        }

        SlackWebApiClient slackClient =
                SlackClientFactory.createWebApiClient(config.getString("accessToken"));

        ChatPostMessageMethod chatPostMessageMethod =
                new ChatPostMessageMethod(channel, message);
        chatPostMessageMethod.setAs_user(true);

        slackClient.postMessage(chatPostMessageMethod);

        log.info("https://" + config.getString("teamName") + ".slack.com/messages/" + channel + "/");
    }

    private SubnodeConfiguration retrieveTeamInformation() throws NotificationException {
        return getConfiguration().getSection(getTeamId());
    }

    @Override
    protected AccessTokenData requestAccessTokenData(AuthorizationData authorizationData) throws NotificationException {
        try {
            String accessTokenUrl = getAccessTokenUrl(authorizationData.getAuthorizationCode()).get();

            log.trace("Access token URL is {}", accessTokenUrl);

            String response = Request.Get(accessTokenUrl).execute().returnContent().asString();

            JsonParser jsonParser = new JsonParser();
            JsonObject json = jsonParser.parse(response).getAsJsonObject();

            ImmutableMap.Builder<String, String> result = ImmutableMap.builder();

            if (!json.get("ok").getAsBoolean()) {
                throw new NotificationException(json.get("error").getAsString());
            }

            result.put("teamId", json.get("team_id").getAsString());
            result.put("teamName", json.get("team_name").getAsString());

            return new AccessTokenData(json.get("access_token").getAsString(), result.build());
        } catch (IOException e) {
            throw new NotificationException(e.getMessage(), e);
        }
    }

    @Override
    protected String getAuthorizationUrl(String stateParameterValue) {
        StringBuilder result = new StringBuilder("https://slack.com/oauth/authorize");
        result.append("?client_id=");
        result.append(getClientId());
        result.append("&scope=chat%3Awrite%3Auser&state=");
        result.append(stateParameterValue);

        if (teamId != null) {
            result.append("&team=");
            result.append(teamId);
        }

        return result.toString();
    }

    @Override
    protected Optional<String> getAccessTokenUrl(String authorizationCode) {
        return Optional.of("https://slack.com/api/oauth.access?client_id=" + getClientId()
                + "&client_secret=" + getClientSecret() + "&code=" + authorizationCode);
    }

    @Override
    protected Optional<String> getStateRequestParameterName() {
        return Optional.of("state");
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return getTeamId() == null || configureNewTeam;
    }

    private String getTeamId() {
        String defaultTeamId = getConfiguration().getString(KEY_DEFAULT_TEAM_ID);

        if (teamId != null) {
            defaultTeamId = teamId;
        }

        return defaultTeamId;
    }

    @Override
    protected void storeAccessTokenData(AccessTokenData accessTokenData, HierarchicalINIConfiguration configuration) throws NotificationException {
        String teamId = accessTokenData.get("teamId");
        configuration.setProperty(KEY_DEFAULT_TEAM_ID, teamId);

        SubnodeConfiguration section = configuration.getSection(teamId);

        section.setProperty(KEY_ACCESS_TOKEN, accessTokenData.getAccessToken());
        for (Map.Entry<String, String> entry : accessTokenData.getData()) {
            section.setProperty(entry.getKey(), entry.getValue());
        }

        try {
            configuration.save();
        } catch (ConfigurationException e) {
            throw new NotificationException(e);
        }
    }

    @Override
    public String getKey() {
        return "slack";
    }

    @Override
    public String getName() {
        return "Slack";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Send message on Slack channel.");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.of("https://slack.com");
    }

}

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

package com.yfiton.notifiers.facebook;

import com.google.common.collect.ImmutableMap;
import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.api.parameter.converters.PathConverter;
import com.yfiton.api.parameter.validators.FileExistValidator;
import com.yfiton.oauth.AccessTokenData;
import com.yfiton.oauth.AuthorizationData;
import com.yfiton.oauth.OAuthNotifier;
import com.yfiton.oauth.receiver.graphical.YfitonWebEngineListener;
import com.yfiton.oauth.receiver.PromptReceiver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * @author lpellegr
 */
public class FacebookNotifier extends OAuthNotifier {

    private static final String CLIENT_ID = "1664946583777891";

    private static final String CLIENT_SECRET = "38f6dbcedde846e16209df405864b8b7";

    @Parameter(description = "Message to send", required = true)
    private String message;

    @Parameter(description = "Image to publish on your photo album", required = false, converter = PathConverter.class, validator = FileExistValidator.class)
    private Path photo;

    @Parameter(description = "Video to publish on your movie album", required = false, converter = PathConverter.class, validator = FileExistValidator.class)
    private Path movie;

    public FacebookNotifier() {
        super(CLIENT_ID, CLIENT_SECRET, PromptReceiver.class, YfitonWebEngineListener.class);
    }

    @Override
    protected Check checkParameters(Parameters parameters) {
        if (hasPhoto() && hasMovie()) {
            Check.failed("Cannot publish a photo and a movie at the same time. Please select one or the other.");
        }

        return Check.succeeded();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        try {
            FacebookClient facebookClient =
                    createFacebookClient(getConfiguration().getString(KEY_ACCESS_TOKEN));

            FacebookType publishResponse;

            if (hasMovie()) {
                publishResponse = facebookClient.publish("me/videos", FacebookType.class,
                        BinaryAttachment.with(movie.getFileName().toString(),
                                Files.readAllBytes(movie),
                                Files.probeContentType(movie)),
                        com.restfb.Parameter.with("description", message));
            } else if (hasPhoto()) {
                publishResponse = facebookClient.publish("me/photos", FacebookType.class,
                        BinaryAttachment.with(
                                photo.getFileName().toString(),
                                Files.readAllBytes(photo),
                                Files.probeContentType(photo)),
                        com.restfb.Parameter.with("message", message));
            } else {
                publishResponse =
                        facebookClient.publish("me/feed", FacebookType.class,
                                com.restfb.Parameter.with("message", message));
            }

            log.debug("Published content has ID {}", publishResponse.getId());
        } catch (FacebookException | IOException e) {
            throw new NotificationException(e);
        }
    }

    private DefaultFacebookClient createFacebookClient(String accessToken) {
        return new DefaultFacebookClient(
                accessToken, getClientSecret(), Version.LATEST);
    }

    @Override
    public String getKey() {
        return "facebook";
    }

    @Override
    public String getName() {
        return "Facebook";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Post photo, movie or status on your Facebook account.");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.of("https://facebook.com");
    }

    @Override
    protected String getAuthorizationUrl(String stateParameterValue) {
        return "https://graph.facebook.com/oauth/authorize?client_id=" +
                getClientId() + "&state=" + stateParameterValue + "&redirect_uri=" + YFITON_OAUTH_CALLBACK_URL +
                "&response_type=code%20token&scope=publish_actions";
    }

    @Override
    protected Optional<String> getStateRequestParameterName() {
        return Optional.of("state");
    }

    @Override
    protected AccessTokenData requestAccessTokenData(AuthorizationData authorizationData) throws NotificationException {
        LocalDateTime expirationDateTime =
                LocalDateTime.now().plus(
                        Integer.parseInt(authorizationData.get("expires_in")),
                        ChronoUnit.SECONDS);

        String accessToken = authorizationData.get("access_token");

        return new AccessTokenData(
                accessToken,
                ImmutableMap.of("expiration", expirationDateTime.toString()));
    }

    @Override
    protected Optional<String> getAccessTokenUrl(String authorizationCode) {
        return Optional.empty();
    }

    private boolean hasMovie() {
        return movie != null;
    }

    private boolean hasPhoto() {
        return photo != null;
    }

}

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

package com.yfiton.oauth;

import com.yfiton.api.NotificationResult;
import com.yfiton.api.Notifier;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.oauth.receiver.GraphicalReceiver;
import com.yfiton.oauth.receiver.PromptReceiver;
import com.yfiton.oauth.receiver.graphical.WebEngineListener;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides basic abstractions for retrieving access token from third-party
 * service using OAuth 2.0 before sending a notification.
 * <p/>
 * The standard workflow involves two steps. The first step is used to get an
 * authorization code with {@link #requestAuthorizationData(String, String...)}.
 * This last relies on {@link #getAuthorizationUrl(String)}
 * to retrieve the URL to open for getting an authorization code. If the
 * environment is headless, the user will see a message that asks for opening
 * the URL and to copy and paste the code manually. Otherwise, if a graphical
 * environment is available, a windows is opened to forward the user to the
 * required Web page.
 * <p/>
 * The second step usually consists in trading the authorization code for an
 * access token. You need to define your own behaviour for this operation by
 * overriding {@link #requestAccessTokenData(AuthorizationData)}.
 *
 * @author lpellegr
 */
public abstract class OAuthNotifier extends Notifier {

    public static final String YFITON_OAUTH_CALLBACK_URL = "http://oauth.yfiton.com/callback";

    protected static final String KEY_ACCESS_TOKEN = "accessToken";

    protected final String clientId;

    protected final String clientSecret;

    protected final Class<? extends PromptReceiver> promptReceiverClazz;

    protected final String webEngineListenerClazz;

    /**
     * Creates a new notifier instance supporting OAuth authentication.
     *
     * @param clientId               third-party service client identifier.
     * @param clientSecret           third-party service client secret.
     */
    public OAuthNotifier(String clientId, String clientSecret) {
        super();

        this.clientId = clientId;
        this.clientSecret = clientSecret;

        this.promptReceiverClazz = PromptReceiver.class;
        this.webEngineListenerClazz = "com.yfiton.oauth.receiver.graphical.YfitonWebEngineListener";
    }

    /**
     * Creates a new notifier instance supporting OAuth authentication.
     *
     * @param clientId               third-party service client identifier.
     * @param clientSecret           third-party service client secret.
     * @param promptReceiverClazz    the class associated to the receiver used to get authorization data on headless environments.
     * @param webEngineListenerClazz the class listening for the Java FX web engine events.
     */
    public OAuthNotifier(String clientId, String clientSecret,
                         Class<? extends PromptReceiver> promptReceiverClazz,
                         String webEngineListenerClazz) {
        super();

        this.clientId = clientId;
        this.clientSecret = clientSecret;

        this.promptReceiverClazz = promptReceiverClazz;
        this.webEngineListenerClazz = webEngineListenerClazz;
    }

    /**
     * Returns the URL to use for retrieving an authorization code.
     *
     * @param stateParameterValue the value to pass for the state parameter value
     *                            if supported by the third-party service. This one
     *                            is used for security purposes in order to prevent
     *                            Cross Site Request Forgery (XRSF).
     * @return
     */
    protected abstract String getAuthorizationUrl(String stateParameterValue) throws NotificationException;

    /**
     * Returns the name of the request parameter used to get authorization code.
     *
     * @return the name of the request parameter used to get authorization code.
     */
    protected String getCodeRequestParameterName() {
        return "code";
    }

    /**
     * Optionally returns the name of the request parameter used for checking OAuth
     * state in order to prevent Cross Site Request Forgery (XRSF).
     *
     * @return the name of the request parameter used for checking OAuth
     * state in order to prevent Cross Site Request Forgery (XRSF).
     */
    protected abstract Optional<String> getStateRequestParameterName();

    /**
     * Returns a boolean indicating whether it is required or not to execute
     * the flow to get an access token.
     *
     * @return {@code true} if authentication is required, {@code false otherwise},
     */
    protected boolean isAuthenticationRequired() {
        return !getConfiguration().containsKey(KEY_ACCESS_TOKEN);
    }

    @Override
    public NotificationResult handle(Parameters parameters) throws NotificationException {
        if (isAuthenticationRequired()) {
            String[] requestParameterNames = new String[0];

            Optional<String> stateRequestParameterName = getStateRequestParameterName();
            if (stateRequestParameterName.isPresent()) {
                requestParameterNames = new String[]{
                        stateRequestParameterName.get()
                };
            }

            executeOAuthLogin(getCodeRequestParameterName(), requestParameterNames);
        }

        return super.handle(parameters);
    }

    protected String executeOAuthLogin(String authorizationCodeParameterName, String... requestParameterNames) throws NotificationException {
        AuthorizationData authorizationData =
                requestAuthorizationData(
                        authorizationCodeParameterName, requestParameterNames);

        AccessTokenData accessTokenData =
                requestAccessTokenData(authorizationData);

        storeAccessTokenData(accessTokenData, getConfiguration());

        return accessTokenData.getAccessToken();
    }

    protected AuthorizationData requestAuthorizationData(String authorizationCodeParameterName, String... requestParameterNames) throws NotificationException {
        log.trace("First call requires to get authorization");

        String stateParameterValue = UUID.randomUUID().toString();
        String authorizationUrl = getAuthorizationUrl(stateParameterValue);

        log.debug("Opening {} to get authorization", authorizationUrl);

        AuthorizationData authorizationData;

        if (GraphicsEnvironment.isHeadless() || isHeadlessEnforced()) {
            log.debug("Headless mode used");
            authorizationData = getAuthorizationDataHeadless(authorizationUrl, authorizationCodeParameterName, requestParameterNames);
        } else {
            log.debug("Graphical mode used");
            authorizationData = getAuthorizationDataUsingScreen(authorizationUrl, authorizationCodeParameterName);
        }

        checkState(stateParameterValue, authorizationData);

        log.trace("Authorization data obtained with success");

        return authorizationData;
    }

    private void checkState(String stateParameterValue, AuthorizationData authorizationData) throws NotificationException {
        if (getStateRequestParameterName().isPresent() &&
                !stateParameterValue.equals(authorizationData.get(
                        getStateRequestParameterName().get()))) {
            throw new NotificationException(
                    "Invalid state value",
                    "Unauthorized access detected: Cross Site Request Forgery (XRSF)",
                    "https://en.wikipedia.org/wiki/Cross-site_request_forgery");
        }
    }

    protected abstract AccessTokenData requestAccessTokenData(AuthorizationData authorizationCode) throws NotificationException;

    protected void storeAccessTokenData(AccessTokenData accessTokenData, HierarchicalINIConfiguration configuration) throws NotificationException {
        configuration.setProperty(KEY_ACCESS_TOKEN, accessTokenData.getAccessToken());

        for (Map.Entry<String, String> entry : accessTokenData.getData()) {
            configuration.setProperty(entry.getKey(), entry.getValue());
        }

        try {
            configuration.save();
        } catch (ConfigurationException e) {
            throw new NotificationException(e);
        }
    }

    protected abstract Optional<String> getAccessTokenUrl(String authorizationCode);

    private boolean isHeadlessEnforced() {
        String headlessEnforced = System.getProperty("yfiton.headless.enforced");
        return headlessEnforced != null && headlessEnforced.equalsIgnoreCase("true");
    }

    private AuthorizationData getAuthorizationDataHeadless(String authorizationUrl, String authorizationCodeParameterName, String[] requestParameterNames) throws NotificationException {
        log.info("Please open the following URL in a web browser:");
        log.info(authorizationUrl);

        try {
            return promptReceiverClazz.getConstructor(
                    Logger.class).newInstance(log).requestAuthorizationData(authorizationUrl, authorizationCodeParameterName, requestParameterNames);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new NotificationException(e.getMessage());
        }
    }

    protected AuthorizationData getAuthorizationDataUsingScreen(String authorizationUrl, String authorizationCodeParameterName) throws NotificationException {
        try {
            GraphicalReceiver graphicalReceiver = new GraphicalReceiver(
                    (Class<? extends WebEngineListener>) Class.forName(webEngineListenerClazz),
                    log.isDebugEnabled() || log.isTraceEnabled());

            return graphicalReceiver.requestAuthorizationData(
                    authorizationUrl, authorizationCodeParameterName);
        } catch (ClassCastException | ClassNotFoundException e) {
            throw new NotificationException(e);
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

}

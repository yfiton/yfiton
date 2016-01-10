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

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Map;

/**
 * JavaFX application that opens the desired web page to get
 * authorization data from third-party services.
 *
 * @author lpellegr
 */
public class WebBrowser extends Application {

    public static final int EXIT_CODE_ON_CLOSE = 255;

    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        // required to allow CORS
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        BorderPane borderPane = new BorderPane();

        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.setUserAgent("Yfiton");

        Map<String, String> parameters = getParameters().getNamed();
        borderPane.setCenter(browser);
        webEngine.documentProperty().addListener((prop, oldDoc, newDoc) -> {
            String debugMode = parameters.get("debug");
            if (debugMode != null && debugMode.equalsIgnoreCase("true")) {
                enableFirebug(webEngine);
            }
        });
        webEngine.load(parameters.get("authorization-url"));

        Class<?> listenerClass = Class.forName(parameters.get("webengine-listener-class"));

        WebEngineListener listener =
                (WebEngineListener) listenerClass.getConstructor(
                        WebEngine.class, String.class, String.class).newInstance(
                        webEngine, parameters.get("authorization-file"), parameters.get("authorization-code-parameter-name"));

        webEngine.getLoadWorker().stateProperty().addListener(listener);

        stage.setTitle("Yfiton");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        scene =
                new Scene(
                        borderPane,
                        primaryScreenBounds.getWidth() * 0.55,
                        primaryScreenBounds.getHeight() * 0.65);

        stage.setScene(scene);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/yfiton-icon.png")));
        stage.show();
        stage.setOnCloseRequest(event -> System.exit(EXIT_CODE_ON_CLOSE));
    }

    /**
     * Enables Firebug Lite for debugging a webEngine.
     *
     * @param engine the webEngine for which debugging is to be enabled.
     */
    private static void enableFirebug(final WebEngine engine) {
        engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
    }

}
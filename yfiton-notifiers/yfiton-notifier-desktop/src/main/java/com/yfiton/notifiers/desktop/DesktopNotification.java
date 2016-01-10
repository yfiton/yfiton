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

package com.yfiton.notifiers.desktop;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.yfiton.Notifications;

import java.util.Map;

/**
 * @author lpellegr
 */
public class DesktopNotification extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<String, String> parameters = getParameters().getNamed();

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(new VBox(), 1, 1);
        scene.setFill(null);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/yfiton-icon.png")));
        primaryStage.show();

        Notifications.create()
                .darkStyle()
                .graphic(new ImageView(Notifications.class.getResource("/" + parameters.get("type") + ".png").toExternalForm()))
                .hideAfter(Duration.seconds(Integer.parseInt(parameters.get("hideAfter"))))
                .onHideAction(event -> System.exit(0))
                .position(Pos.valueOf(parameters.get("position")))
                .text(parameters.get("message"))
                .show();
    }

}

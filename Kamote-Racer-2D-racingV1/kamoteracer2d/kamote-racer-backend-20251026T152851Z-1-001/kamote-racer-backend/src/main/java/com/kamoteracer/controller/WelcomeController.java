package com.kamoteracer.controller;

import com.kamoteracer.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {
    @FXML private StackPane root;
    @FXML private ImageView leaderboardCard;
    @FXML private ImageView pressEnterCard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure key events go to the root for ENTER to start
        root.setFocusTraversable(true);
        root.requestFocus();
        root.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);

        if (leaderboardCard != null) {
            leaderboardCard.setOnMouseClicked(e -> MainApp.getInstance().showLeaderboardScene());
        }
        if (pressEnterCard != null) {
            pressEnterCard.setOnMouseClicked(e -> MainApp.getInstance().showGameScene());
        }
        // Buttons removed in current layout; no additional handlers needed
    }

    // Binding at the Scene level guarantees ENTER works even if focus changes
    public void bindScene(Scene scene) {
        if (scene == null) return;
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            MainApp.getInstance().showGameScene();
        }
    }
}



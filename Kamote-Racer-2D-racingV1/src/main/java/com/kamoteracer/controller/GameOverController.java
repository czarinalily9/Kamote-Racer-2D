package com.kamoteracer.controller;

import com.kamoteracer.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class GameOverController implements Initializable {
    @FXML private Label scoreLabel;
    @FXML private Button tryAgainButton;
    @FXML private Button saveHighscoreButton;

    private int score;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (tryAgainButton != null) {
            tryAgainButton.setOnAction(e -> {
                javafx.stage.Window w = tryAgainButton.getScene() != null ? tryAgainButton.getScene().getWindow() : null;
                if (w instanceof javafx.stage.Stage s) {
                    s.close();
                }
                MainApp.getInstance().showGameScene();
            });
        }
        if (saveHighscoreButton != null) {
            saveHighscoreButton.setOnAction(e -> InputInitialsController.show());
        }
    }

    public void setScore(int score) {
        this.score = score;
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(score));
        }
    }
}

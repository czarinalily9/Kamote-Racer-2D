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

    private int score = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateScoreLabel();
        tryAgainButton.setOnAction(e -> MainApp.getInstance().showGameScene());
        // Placeholder: hook this up to your repository/service to persist
        saveHighscoreButton.setOnAction(e -> {
            // TODO: save score to DB
            MainApp.getInstance().showWelcomeScene();
        });
    }

    public void setScore(int score) {
        this.score = score;
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText(Integer.toString(score));
        }
    }
}



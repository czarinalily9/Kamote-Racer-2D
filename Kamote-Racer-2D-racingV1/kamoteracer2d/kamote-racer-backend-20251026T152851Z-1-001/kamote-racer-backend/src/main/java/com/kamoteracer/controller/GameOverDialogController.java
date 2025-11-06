package com.kamoteracer.controller;

import com.kamoteracer.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GameOverDialogController implements Initializable {
    @FXML private StackPane root;
    @FXML private Pane dialogRoot;
    @FXML private Label scoreLabel;
    @FXML private ImageView tryAgainImage;
    @FXML private ImageView saveScoreImage;
    @FXML private ImageView titleImage;
    @FXML private ImageView carImage;
    @FXML private ImageView highscoreImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (tryAgainImage != null) {
            tryAgainImage.setOnMouseClicked(e -> {
                closeStage();
                MainApp.getInstance().showGameScene();
            });
        }
        if (saveScoreImage != null) {
            saveScoreImage.setOnMouseClicked(e -> {
                // TODO: persist score if needed
                closeStage();
                MainApp.getInstance().showWelcomeScene();
            });
        }

        // Scale dialogRoot to fit the dialog window while preserving layout
        if (root != null && dialogRoot != null) {
            Runnable scale = () -> { scaleToWindow(); layoutElements(); };
            root.widthProperty().addListener((o, a, b) -> scale.run());
            root.heightProperty().addListener((o, a, b) -> scale.run());
            scale.run();
        }
        dialogRoot.widthProperty().addListener((o,a,b) -> layoutElements());
        dialogRoot.heightProperty().addListener((o,a,b) -> layoutElements());
    }

    public void setScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText(Integer.toString(score));
        }
    }

    private void closeStage() {
        Stage stage = (Stage) tryAgainImage.getScene().getWindow();
        stage.close();
    }

    private void scaleToWindow() {
        double margin = 12.0;
        double baseW = dialogRoot.getPrefWidth();
        double baseH = dialogRoot.getPrefHeight();
        double scale = Math.min((root.getWidth() - margin * 2) / baseW,
                (root.getHeight() - margin * 2) / baseH);
        // Do not upscale to avoid clipping on small title margins
        if (scale > 1.0) scale = 1.0;
        if (scale <= 0 || Double.isNaN(scale) || Double.isInfinite(scale)) return;
        dialogRoot.setScaleX(scale);
        dialogRoot.setScaleY(scale);

        // Center scaled dialogRoot inside the window
        double contentW = baseW * scale;
        double contentH = baseH * scale;
        dialogRoot.setLayoutX((root.getWidth() - contentW) / 2.0);
        dialogRoot.setLayoutY((root.getHeight() - contentH) / 2.0);
    }

    private void layoutElements() {
        double w = dialogRoot.getPrefWidth();
        double h = dialogRoot.getPrefHeight();
        if (w <= 0 || h <= 0) return;

        // Vertically center the whole group using offsets relative to center
        double centerY = h / 2.0;
        double titleY = centerY - 100;      // Title above center
        double bannerY = centerY - 6;       // Gold banner just above center
        double buttonsY = centerY + 90;     // Buttons below center
        // Horizontal nudge to the right to counter left-biased art margins
        double offsetX = 8.0;

        // Horizontal centering for key elements
        if (titleImage != null) {
            titleImage.setLayoutX((w - titleImage.getFitWidth()) / 2.0 + offsetX);
            titleImage.setLayoutY(titleY);
        }
        if (highscoreImage != null) {
            highscoreImage.setLayoutX((w - highscoreImage.getFitWidth()) / 2.0 + offsetX);
            highscoreImage.setLayoutY(bannerY);
        }
        if (scoreLabel != null) {
            scoreLabel.setLayoutX(w / 2.0 - 12 + offsetX);
            scoreLabel.setLayoutY(bannerY + 4);
        }
        if (carImage != null) {
            // Slightly smaller car width and responsive to dialog width
            double targetWidth = Math.min(380.0, Math.max(320.0, w - 140.0));
            carImage.setFitWidth(targetWidth);
            carImage.setLayoutX((w - carImage.getFitWidth()) / 2.0 + offsetX);
            double carH = carImage.getBoundsInLocal() != null ? carImage.getBoundsInLocal().getHeight() : 0.0;
            if (carH <= 0) carH = 170.0; // fallback
            // Center the car image vertically at the window center
            carImage.setLayoutY(centerY - carH / 2.0 + 10); // slight nudge down
        }
        if (tryAgainImage != null && saveScoreImage != null) {
            double gap = 40;
            double btnW = tryAgainImage.getFitWidth();
            double totalW = btnW + gap + saveScoreImage.getFitWidth();
            double startX = (w - totalW) / 2.0 + offsetX;
            tryAgainImage.setLayoutX(startX);
            tryAgainImage.setLayoutY(buttonsY);
            saveScoreImage.setLayoutX(startX + btnW + gap);
            saveScoreImage.setLayoutY(buttonsY);
        }
    }
}



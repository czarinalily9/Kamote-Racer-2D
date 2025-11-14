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

    private int score;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (tryAgainImage != null) {
            tryAgainImage.setOnMouseClicked(e -> {
                closeStage();
                MainApp.getInstance().showGameScene();
            });
        }
        if (saveScoreImage != null) {
            saveScoreImage.setOnMouseClicked(e -> promptAndSave());
        }

        // Crisp image rendering
        if (titleImage != null) { titleImage.setSmooth(false); titleImage.setCache(false); }
        if (carImage != null) { carImage.setSmooth(false); carImage.setCache(false); }
        if (highscoreImage != null) { highscoreImage.setSmooth(false); highscoreImage.setCache(false); }
        if (tryAgainImage != null) { tryAgainImage.setSmooth(false); tryAgainImage.setCache(false); }
        if (saveScoreImage != null) { saveScoreImage.setSmooth(false); saveScoreImage.setCache(false); }

        // Ensure overlays draw on top
        if (titleImage != null) titleImage.toFront();
        if (highscoreImage != null) highscoreImage.toFront();
        if (scoreLabel != null) scoreLabel.toFront();
        if (tryAgainImage != null) tryAgainImage.toFront();
        if (saveScoreImage != null) saveScoreImage.toFront();

        // Listeners for resizing
        if (root != null && dialogRoot != null) {
            Runnable scale = () -> {
                scaleToWindow();
                layoutElements();
            };
            root.widthProperty().addListener((o, a, b) -> scale.run());
            root.heightProperty().addListener((o, a, b) -> scale.run());
            scale.run();
        }
        // Re-layout if dialog content size changes
        if (dialogRoot != null) {
            dialogRoot.widthProperty().addListener((o, a, b) -> layoutElements());
            dialogRoot.heightProperty().addListener((o, a, b) -> layoutElements());
        }
    }

    public void setScore(int score) {
        this.score = score;
        if (scoreLabel != null) {
            scoreLabel.setText(Integer.toString(score));
            scoreLabel.setVisible(true);
            layoutElements();
        }
    }

    private void closeStage() {
        Stage stage = (Stage) tryAgainImage.getScene().getWindow();
        stage.close();
    }

    private void promptAndSave() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Save High Score");
        dialog.setHeaderText("Enter your initials (1-3 letters)");
        dialog.setContentText("Initials:");
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        String initials = result.get().trim();
        if (!initials.matches("^[a-zA-Z]{1,3}$")) return;
        try {
            String json = "{\"initials\":\"" + initials + "\",\"score\":" + score + "}";
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(new java.net.URI("http://localhost:8080/api/leaderboard/submit"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();
            java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {}
        closeStage();
    }

    public static void show() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    GameOverDialogController.class.getResource("/fxml/GameOverDialog.fxml"));
            StackPane root = loader.load();
            GameOverDialogController controller = loader.getController();
            controller.setScore(com.kamoteracer.MainApp.getInstance().getLastScore());
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Game Over");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root, 825, 660));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔧 UPDATED (reverted): scales everything slightly larger and fits the window
    private void scaleToWindow() {
        if (root == null || dialogRoot == null) return;

        double rootW = root.getWidth();
        double rootH = root.getHeight();

        if (rootW <= 0 || rootH <= 0) return;

        // Your FXML's base layout size (design reference)
        double baseW = 800;
        double baseH = 600;

        // Compute scale ratio to fit window but larger for readability (1.3x)
        double scale = Math.min(rootW / baseW, rootH / baseH) * 1.3;
        if (scale > 1.7) scale = 1.7;

        // Apply scaling to entire dialogRoot (resizes all elements)
        dialogRoot.setScaleX(scale);
        dialogRoot.setScaleY(scale);

        // Center scaled layout
        double contentW = baseW * scale;
        double contentH = baseH * scale;
        dialogRoot.setLayoutX((rootW - contentW) / 2.0);
        dialogRoot.setLayoutY((rootH - contentH) / 2.0);

        // Make sure background adjusts
        dialogRoot.setPrefSize(rootW, rootH);
    }

    private void layoutElements() {
        double w = dialogRoot.getPrefWidth();
        double h = dialogRoot.getPrefHeight();
        if (w <= 0 || h <= 0) return;

        final double topMargin = 16.0;
        final double gapY = 10.0;

        // 1) Title centered horizontally
        if (titleImage != null) {
            double titleW = titleImage.getFitWidth() > 0 ? titleImage.getFitWidth() : 260;
            titleImage.setLayoutX((w - titleW) / 2.0);
            titleImage.setLayoutY(topMargin);
        }

        // 2) Highscore banner below title
        if (highscoreImage != null && titleImage != null) {
            double titleH = titleImage.getBoundsInLocal() != null
                    ? titleImage.getBoundsInLocal().getHeight() : 64.0;
            double bannerW = highscoreImage.getFitWidth() > 0 ? highscoreImage.getFitWidth() : 260;
            double bannerY = topMargin + titleH + gapY;
            highscoreImage.setLayoutX((w - bannerW) / 2.0);
            highscoreImage.setLayoutY(bannerY);
        }

        // 3) Center the car in the middle of the window
        if (carImage != null) {
            double carTargetW = Math.min(w * 0.8, 560.0);
            carImage.setFitWidth(carTargetW);
            double carH = carImage.getBoundsInLocal() != null
                    ? carImage.getBoundsInLocal().getHeight() : 200.0;

            carImage.setLayoutX((w - carTargetW) / 2.0);
            carImage.setLayoutY((h - carH) / 2.0);
        }

        // 4) Hide score label if unused
        if (scoreLabel != null) {
            scoreLabel.setVisible(false);
        }

        // 5) Buttons centered horizontally below the car
        if (tryAgainImage != null && saveScoreImage != null) {
            double btnH = tryAgainImage.getBoundsInLocal() != null ? tryAgainImage.getBoundsInLocal().getHeight() : 36.0;
            double gapButtons = 40.0;
            double btnLW = tryAgainImage.getFitWidth();
            double btnRW = saveScoreImage.getFitWidth();
            double totalW = btnLW + gapButtons + btnRW;

            double btnY = (h / 2.0) + 160.0; // slightly below the centered car

            double startX = (w - totalW) / 2.0;
            tryAgainImage.setLayoutX(startX);
            tryAgainImage.setLayoutY(btnY);
            saveScoreImage.setLayoutX(startX + btnLW + gapButtons);
            saveScoreImage.setLayoutY(btnY);
        }

        // (score label suppressed in this layout)
    }
}

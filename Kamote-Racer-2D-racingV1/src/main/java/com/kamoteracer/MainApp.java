package com.kamoteracer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static MainApp instance;
    private Stage primaryStage;
    private int lastScore;

    public MainApp() {
        instance = this;
    }

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Kamote Racer 2D");
        showWelcomeScene();
        primaryStage.show();
    }

    public void showWelcomeScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WelcomePage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 550, 440);
            primaryStage.setScene(scene);
            javafx.scene.Node leaderboard = root.lookup("#leaderboardButton");
            if (leaderboard != null) {
                leaderboard.setOnMouseClicked(e -> showLeaderboardScene());
            }
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    showGameScene();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLeaderboardScene() {
        System.out.println("Showing leaderboard scene...");
        // TODO: Implement loading Leaderboard FXML
    }

    public void showGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Game.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 550, 440);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverScene(int score) {
        try {
            this.lastScore = score;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 825, 660);
            Object controller = loader.getController();
            if (controller instanceof com.kamoteracer.controller.GameOverController c) {
                c.setScore(score);
            }
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverDialog(int score) {
        javafx.application.Platform.runLater(() -> {
            try {
                this.lastScore = score;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
                Parent root = loader.load();
                javafx.stage.Stage dialog = new javafx.stage.Stage();
                dialog.setTitle("Game Over");
                dialog.initOwner(primaryStage);
                dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                javafx.scene.Scene scene = new javafx.scene.Scene(root, 825, 660);
                Object controller = loader.getController();
                if (controller instanceof com.kamoteracer.controller.GameOverController c) {
                    c.setScore(score);
                }
                dialog.setScene(scene);
                Runnable center = () -> {
                    double cw = dialog.getWidth() > 0 ? dialog.getWidth() : scene.getWidth();
                    double ch = dialog.getHeight() > 0 ? dialog.getHeight() : scene.getHeight();
                    double x = primaryStage.getX() + (primaryStage.getWidth() - cw) / 2.0;
                    double y = primaryStage.getY() + (primaryStage.getHeight() - ch) / 2.0;
                    dialog.setX(x);
                    dialog.setY(y);
                };
                primaryStage.xProperty().addListener((o,a,b) -> center.run());
                primaryStage.yProperty().addListener((o,a,b) -> center.run());
                primaryStage.widthProperty().addListener((o,a,b) -> center.run());
                primaryStage.heightProperty().addListener((o,a,b) -> center.run());
                dialog.widthProperty().addListener((o,a,b) -> center.run());
                dialog.heightProperty().addListener((o,a,b) -> center.run());
                dialog.setOnShown(e -> center.run());
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public int getLastScore() {
        return lastScore;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

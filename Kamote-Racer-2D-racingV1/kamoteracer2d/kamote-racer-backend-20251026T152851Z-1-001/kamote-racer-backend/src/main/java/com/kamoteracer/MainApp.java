package com.kamoteracer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static MainApp instance;
    private Stage primaryStage;

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
            // Ensure controller can listen at the Scene level for ENTER
            Object controller = loader.getController();
            if (controller instanceof com.kamoteracer.controller.WelcomeController wc) {
                wc.bindScene(scene);
            }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 550, 440);
            primaryStage.setScene(scene);
            Object controller = loader.getController();
            if (controller instanceof com.kamoteracer.controller.GameOverController goc) {
                goc.setScore(score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverDialog(int score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverDialog.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Game Over");
            dialog.initOwner(primaryStage);
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.setResizable(false);
            // Fixed window size that fits all assets clearly
            Scene scene = new Scene(root, 550, 440);
            dialog.setScene(scene);
            dialog.centerOnScreen();
            Object controller = loader.getController();
            if (controller instanceof com.kamoteracer.controller.GameOverDialogController gdc) {
                gdc.setScore(score);
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.kamoteracer;

import com.kamoteracer.controller.LeaderboardDialogController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Scene scene = new Scene(root, 825, 660);
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

    public void showGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Game.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 825, 660);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverScene(int score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 825, 660);
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
            Scene scene = new Scene(root, 825, 660);
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

    public List<LeaderboardDialogController.LeaderboardRow> fetchLeaderboardEntries() {
        List<LeaderboardDialogController.LeaderboardRow> rows = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/leaderboard/top10"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Matcher matcher = Pattern.compile("\\{[^}]*\"initial\"\\s*:\\s*\"(.*?)\"[^}]*\"score\"\\s*:\\s*(\\d+)[^}]*}"
                        , Pattern.CASE_INSENSITIVE).matcher(response.body());
                while (matcher.find() && rows.size() < 5) {
                    String initials = matcher.group(1).trim();
                    int score = Integer.parseInt(matcher.group(2));
                    rows.add(new LeaderboardDialogController.LeaderboardRow(initials, score));
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to fetch leaderboard entries: " + e.getMessage());
        }

        while (rows.size() < 5) {
            rows.add(new LeaderboardDialogController.LeaderboardRow("RKV", 999999));
        }
        return rows;
    }
}

package com.kamoteracer.controller;

import com.kamoteracer.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {
    @FXML private StackPane root;
    @FXML private ImageView leaderboardCard;
    @FXML private ImageView pressEnterCard;
    @FXML private StackPane leaderboardOverlay;
    @FXML private StackPane overlayScrim;
    @FXML private StackPane leaderboardDialogRoot;
    @FXML private LeaderboardDialogController leaderboardDialogRootController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure key events go to the root for ENTER to start
        root.setFocusTraversable(true);
        root.requestFocus();
        root.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);

        if (leaderboardCard != null) {
            leaderboardCard.setOnMouseClicked(e -> showLeaderboard());
        }
        if (pressEnterCard != null) {
            pressEnterCard.setOnMouseClicked(e -> MainApp.getInstance().showGameScene());
        }

        if (leaderboardOverlay != null) {
            hideLeaderboard();
            leaderboardOverlay.addEventHandler(KeyEvent.KEY_PRESSED, this::onOverlayKeyPressed);
        }

        if (overlayScrim != null) {
            overlayScrim.setOnMouseClicked(e -> hideLeaderboard());
        }

        if (leaderboardDialogRootController != null) {
            leaderboardDialogRootController.setOnClose(this::hideLeaderboard);
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

    private void showLeaderboard() {
        if (leaderboardOverlay == null) {
            return;
        }

        if (leaderboardDialogRootController != null) {
            List<LeaderboardDialogController.LeaderboardRow> rows = MainApp.getInstance().fetchLeaderboardEntries();
            leaderboardDialogRootController.setEntries(rows);
        }

        leaderboardOverlay.setManaged(true);
        leaderboardOverlay.setVisible(true);
        leaderboardOverlay.setMouseTransparent(false);
        leaderboardOverlay.setFocusTraversable(true);
        leaderboardOverlay.requestFocus();
    }

    private void hideLeaderboard() {
        if (leaderboardOverlay == null) {
            return;
        }

        leaderboardOverlay.setVisible(false);
        leaderboardOverlay.setManaged(false);
        leaderboardOverlay.setMouseTransparent(true);
        leaderboardOverlay.setFocusTraversable(false);
        root.requestFocus();
    }

    private void onOverlayKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            hideLeaderboard();
            event.consume();
        }
    }
}



package com.kamoteracer.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Welcome Page screen in Kamote Racer 2D.
 * Handles leaderboard button clicks and start game key events.
 */
public class WelcomePageController implements Initializable {
    @FXML
    private ImageView leaderboardButton;
    /** The root pane of this scene, used to register key events. */
    @FXML
    private StackPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        leaderboardButton.setOnMouseClicked(event -> onLeaderboardClicked());
        // Register a key handler on the root pane so ENTER key works
        rootPane.setOnKeyPressed(this::onKeyPressed);
        // Request focus so key events are captured when loaded
        rootPane.setFocusTraversable(true);
        rootPane.requestFocus();
    }

    /**
     * Called when the leaderboard button is clicked.
     * Switches to the leaderboard scene.
     */
    @FXML
    private void onLeaderboardClicked() {
        System.out.println("Leaderboard button clicked");
        // TODO: Implement this using your scene switching logic:
        // MainApp.getInstance().showLeaderboardScene();
    }

    /**
     * Handles key presses on the welcome page.
     * Starts the game when ENTER is pressed.
     */
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            startGame();
        }
    }

    /**
     * Switches to the main game scene (placeholder method).
     */
    private void startGame() {
        System.out.println("Start game!");
        // TODO: Implement this using your scene switching logic:
        // MainApp.getInstance().showGameScene();
    }
}

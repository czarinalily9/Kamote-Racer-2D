package com.kamoteracer.controller;

import com.kamoteracer.MainApp;
import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Bounds;
 

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML private StackPane root;
    @FXML private Pane gameLayer;
    @FXML private Pane uiLayer;
    @FXML private Pane backgroundLayer;
    @FXML private ImageView bg1;
    @FXML private ImageView bg2;
    @FXML private Label scoreLabel;
    @FXML private Pane gameOverOverlay;
    @FXML private Pane gameOverContent;
    @FXML private Label gameOverScoreLabel;
    @FXML private ImageView tryAgainImage;
    @FXML private ImageView saveScoreImage;

    private final double sceneWidth = 520;
    private final double sceneHeight = 440;

    // Only the 3 center roads (adjusted to fit new visual size)
    private final double[] laneX = new double[]{198, 274, 351};
    private int playerLaneIndex = 1; // center lane

    private ImageView player;
    private Image carImage;
    private Image obstacleImage;
    private final List<ImageView> obstacles = new ArrayList<>();

    private final Random random = new Random();
    private AnimationTimer gameLoop;
    private long lastSpawnNs = 0L;

    private double backgroundScrollSpeed = 150; // px/sec
    private double obstacleSpeed = 200; // px/sec

    private long lastFrameNs = 0L;
    private double score = 0; // continuous score
    private double scoreRatePerSecond = 10; // starting score rate
    private int obstaclesPassed = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carImage = new Image(getClass().getResourceAsStream("/images/car.png"));
        obstacleImage = new Image(getClass().getResourceAsStream("/images/obstacle.png"));
        setupPlayer();
        startGameLoop();
        // Focus to receive key events
        gameLayer.setFocusTraversable(true);
        gameLayer.requestFocus();
        gameLayer.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        updateScoreLabel();

        if (tryAgainImage != null) {
            tryAgainImage.setOnMouseClicked(e -> Platform.runLater(() -> MainApp.getInstance().showGameScene()));
        }
        if (saveScoreImage != null) {
            saveScoreImage.setOnMouseClicked(e -> Platform.runLater(() -> MainApp.getInstance().showWelcomeScene()));
        }

        // Scale and center the game-over content to fit the current window size
        if (root != null && gameOverContent != null) {
            Runnable applyScale = this::scaleGameOverToWindow;
            root.widthProperty().addListener((o, a, b) -> applyScale.run());
            root.heightProperty().addListener((o, a, b) -> applyScale.run());
            applyScale.run();
        }

        // Make overlay always cover the window
        if (root != null && gameOverOverlay != null) {
            gameOverOverlay.prefWidthProperty().bind(root.widthProperty());
            gameOverOverlay.prefHeightProperty().bind(root.heightProperty());
        }
    }

    private void setupPlayer() {
        player = new ImageView(carImage);
        player.setFitWidth(140);
        player.setFitHeight(100);
        updatePlayerPosition();
        player.setLayoutY(sceneHeight - 120);
        gameLayer.getChildren().add(player);
    }

    private void updatePlayerPosition() {
        double x = laneX[playerLaneIndex] - player.getFitWidth() / 2;
        player.setLayoutX(x);
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameNs == 0L) {
                    lastFrameNs = now;
                    lastSpawnNs = now;
                    return;
                }
                double deltaSec = (now - lastFrameNs) / 1_000_000_000.0;
                lastFrameNs = now;
                updateBackground(deltaSec);
                spawnObstaclesIfNeeded(now);
                updateObstacles(deltaSec);
                checkCollisions();
                incrementScore(deltaSec);
            }
        };
        gameLoop.start();
    }

    private void updateBackground(double deltaSec) {
        double dy = backgroundScrollSpeed * deltaSec;
        bg1.setLayoutY(bg1.getLayoutY() + dy);
        bg2.setLayoutY(bg2.getLayoutY() + dy);
        if (bg1.getLayoutY() >= sceneHeight) {
            bg1.setLayoutY(bg2.getLayoutY() - sceneHeight);
        }
        if (bg2.getLayoutY() >= sceneHeight) {
            bg2.setLayoutY(bg1.getLayoutY() - sceneHeight);
        }
    }

    private void spawnObstaclesIfNeeded(long now) {
        // Spawn every 0.8 - 1.2 seconds
        if (now - lastSpawnNs < 600_000_000L + random.nextInt(600_000_000)) {
            return;
        }
        lastSpawnNs = now;
        int lane = random.nextInt(laneX.length);
        ImageView obs = new ImageView(obstacleImage);
        obs.setFitWidth(40);
        obs.setFitHeight(40);
        obs.setLayoutX(laneX[lane] - obs.getFitWidth() / 2);
        obs.setLayoutY(-60);
        obstacles.add(obs);
        gameLayer.getChildren().add(obs);
    }

    private void updateObstacles(double deltaSec) {
        double dy = obstacleSpeed * deltaSec;
        Iterator<ImageView> it = obstacles.iterator();
        while (it.hasNext()) {
            ImageView obs = it.next();
            obs.setLayoutY(obs.getLayoutY() + dy);
            if (obs.getLayoutY() > sceneHeight + 10) {
                it.remove();
                gameLayer.getChildren().remove(obs);
                obstaclesPassed += 1;
                // Every 10 obstacles passed, increase scoring rate
                if (obstaclesPassed % 10 == 0) {
                    scoreRatePerSecond *= 1.25;
                }
            }
        }
    }

    private void checkCollisions() {
        for (ImageView obs : obstacles) {
            // Use a centered hitbox for the player (middle of the car),
            // and a slightly shrunken obstacle hitbox
            Bounds playerCenter = getCenteredBounds(player, 0.30, 0.40);
            Bounds obstacleBox = getShrinkedBounds(obs, 0.85);
            if (playerCenter.intersects(obstacleBox)) {
                onGameOver();
                return;
            }
        }
    }

    private boolean intersectsWithShrink(ImageView a, double shrinkA, ImageView b, double shrinkB) {
        Bounds ba = getShrinkedBounds(a, shrinkA);
        Bounds bb = getShrinkedBounds(b, shrinkB);
        return ba.intersects(bb);
    }

    private Bounds getCenteredBounds(ImageView node, double widthFraction, double heightFraction) {
        Bounds b = node.getBoundsInParent();
        double w = b.getWidth() * widthFraction;
        double h = b.getHeight() * heightFraction;
        double x = b.getMinX() + (b.getWidth() - w) / 2.0;
        double y = b.getMinY() + (b.getHeight() - h) / 2.0;
        return new javafx.geometry.BoundingBox(x, y, w, h);
    }

    private Bounds getShrinkedBounds(ImageView node, double shrink) {
        Bounds b = node.getBoundsInParent();
        double dw = b.getWidth() * (1.0 - shrink);
        double dh = b.getHeight() * (1.0 - shrink);
        return new javafx.geometry.BoundingBox(
            b.getMinX() + dw / 2,
            b.getMinY() + dh / 2,
            b.getWidth() * shrink,
            b.getHeight() * shrink
        );
    }

    private void incrementScore(double deltaSec) {
        score += scoreRatePerSecond * deltaSec;
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.valueOf((int) Math.floor(score)));
    }

    private void onGameOver() {
        gameLoop.stop();
        System.out.println("Game Over. Score: " + (int) score + ", Obstacles passed: " + obstaclesPassed);
        int finalScore = (int) Math.floor(score);
        if (gameOverScoreLabel != null) {
            gameOverScoreLabel.setText(String.valueOf(finalScore));
        }
        // Prefer dialog popup
        Platform.runLater(() -> MainApp.getInstance().showGameOverDialog(finalScore));
    }

    private void scaleGameOverToWindow() {
        double margin = 24.0; // keep some breathing room
        double contentBaseW = gameOverContent.getPrefWidth();  // 420
        double contentBaseH = gameOverContent.getPrefHeight(); // 340
        double scale = Math.min((root.getWidth() - margin * 2) / contentBaseW,
                                (root.getHeight() - margin * 2) / contentBaseH);
        if (scale <= 0 || Double.isNaN(scale) || Double.isInfinite(scale)) return;
        gameOverContent.setScaleX(scale);
        gameOverContent.setScaleY(scale);
        double contentW = contentBaseW * scale;
        double contentH = contentBaseH * scale;
        gameOverContent.setLayoutX((root.getWidth() - contentW) / 2.0);
        gameOverContent.setLayoutY((root.getHeight() - contentH) / 2.0);
    }

    private void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.LEFT) {
            if (playerLaneIndex > 0) {
                playerLaneIndex--;
                updatePlayerPosition();
            }
        } else if (e.getCode() == KeyCode.RIGHT) {
            if (playerLaneIndex < laneX.length - 1) {
                playerLaneIndex++;
                updatePlayerPosition();
            }
        }
    }
}

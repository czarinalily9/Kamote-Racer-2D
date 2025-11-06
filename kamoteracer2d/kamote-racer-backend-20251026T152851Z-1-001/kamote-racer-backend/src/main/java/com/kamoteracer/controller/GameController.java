package com.kamoteracer.controller;

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
import javafx.geometry.Bounds;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML private Pane gameLayer;
    @FXML private Pane uiLayer;
    @FXML private Pane backgroundLayer;
    @FXML private ImageView bg1;
    @FXML private ImageView bg2;
    @FXML private Label scoreLabel;

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
            if (shrinkedIntersects(player, obs, 0.75)) {
                onGameOver();
                return;
            }
        }
    }

    private boolean shrinkedIntersects(ImageView a, ImageView b, double shrink) {
        Bounds ba = getShrinkedBounds(a, shrink);
        Bounds bb = getShrinkedBounds(b, shrink);
        return ba.intersects(bb);
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
        showInputInitialsDialog();
    }

    private void showInputInitialsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InputInitials.fxml"));
            StackPane overlay = loader.load();
            uiLayer.getChildren().add(overlay);
            overlay.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.geometry.Bounds;

import java.net.URL;
import java.util.Objects;
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
        carImage = new Image(Objects.requireNonNull(getClass().getResource("/images/car.png")).toExternalForm());
        obstacleImage = new Image(Objects.requireNonNull(getClass().getResource("/images/obstacle.png")).toExternalForm());
        setupPlayer();
        startGameLoop();
        // Focus to receive key events
        gameLayer.setFocusTraversable(true);
        gameLayer.requestFocus();
        gameLayer.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        gameLayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
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

    /** 
     * Checks if the player's (smaller) internal hitbox intersects 
     * with any of the (full-sized) obstacle hitboxes. 
     */ 
    private void checkCollisions() { 
        Bounds playerHitbox = getPlayerInternalHitbox(); 
        for (ImageView obs : obstacles) { 
            Bounds obstacleHitbox = obs.getBoundsInParent(); 
            if (playerHitbox.intersects(obstacleHitbox)) { 
                onGameOver(); 
                return; 
            } 
        } 
    } 

    /** 
     * Calculates and returns a new, smaller bounding box that is 
     * centered inside the player's main image. 
     * This is the "middle part" of the car you wanted. 
     */ 
    private Bounds getPlayerInternalHitbox() { 
        double horizontalPadding = 40; 
        double verticalPadding = 50; 
        Bounds playerBounds = player.getBoundsInParent(); 
        double newMinX = playerBounds.getMinX() + horizontalPadding; 
        double newMinY = playerBounds.getMinY() + verticalPadding; 
        double newWidth = playerBounds.getWidth() - (horizontalPadding * 2); 
        double newHeight = playerBounds.getHeight() - (verticalPadding * 2); 
        return new javafx.geometry.BoundingBox(newMinX, newMinY, newWidth, newHeight); 
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
        com.kamoteracer.MainApp.getInstance().showGameOverDialog((int) Math.floor(score));
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

    private void onMouseDragged(MouseEvent e) {
        double x = e.getX();
        int nearest = 0;
        double best = Math.abs(x - laneX[0]);
        for (int i = 1; i < laneX.length; i++) {
            double d = Math.abs(x - laneX[i]);
            if (d < best) {
                best = d;
                nearest = i;
            }
        }
        if (nearest != playerLaneIndex) {
            playerLaneIndex = nearest;
            updatePlayerPosition();
        }
    }
}

package com.kamoteracer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.io.IOException;

public class InputInitialsController {
    @FXML private StackPane root;
    @FXML private Label ch1; @FXML private Label ch2; @FXML private Label ch3;
    @FXML private ImageView ul1; @FXML private ImageView ul2; @FXML private ImageView ul3;
    @FXML private ImageView up1; @FXML private ImageView up2; @FXML private ImageView up3;
    @FXML private ImageView down1; @FXML private ImageView down2; @FXML private ImageView down3;
    @FXML private Button submitButton; @FXML private Button closeButton;

    private int index = 0; // 0..2 which letter active
    private int score;

    @FXML
    private void initialize() {
        // Ensure custom font is registered
        try {
            Font.loadFont(getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf"), 12);
        } catch (Exception ignored) {}

        updateSelectionUnderline();
        root.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        root.setFocusTraversable(true);
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
                javafx.application.Platform.runLater(() -> root.requestFocus());
            }
        });
        root.setOnMouseClicked(e -> root.requestFocus());
        if (closeButton != null) {
            closeButton.setOnAction(e -> close());
        }
        if (submitButton != null) {
            submitButton.setOnAction(e -> submit());
        }

        if (up1 != null) { up1.setPickOnBounds(true); up1.setOnMouseClicked(e -> incrementLetterFor(ch1, +1)); }
        if (down1 != null) { down1.setPickOnBounds(true); down1.setOnMouseClicked(e -> incrementLetterFor(ch1, -1)); }
        if (up2 != null) { up2.setPickOnBounds(true); up2.setOnMouseClicked(e -> incrementLetterFor(ch2, +1)); }
        if (down2 != null) { down2.setPickOnBounds(true); down2.setOnMouseClicked(e -> incrementLetterFor(ch2, -1)); }
        if (up3 != null) { up3.setPickOnBounds(true); up3.setOnMouseClicked(e -> incrementLetterFor(ch3, +1)); }
        if (down3 != null) { down3.setPickOnBounds(true); down3.setOnMouseClicked(e -> incrementLetterFor(ch3, -1)); }
    }

    private void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            close();
            e.consume();
            return;
        }
        if (e.getCode() == KeyCode.RIGHT) {
            if (index < 2) { index++; updateSelectionUnderline(); }
            e.consume();
        } else if (e.getCode() == KeyCode.LEFT) {
            if (index > 0) { index--; updateSelectionUnderline(); }
            e.consume();
        } else if (e.getCode() == KeyCode.UP) {
            incrementLetter(1);
            e.consume();
        } else if (e.getCode() == KeyCode.DOWN) {
            incrementLetter(-1);
            e.consume();
        }
    }

    private void incrementLetter(int delta) {
        Label current = getCurrentLabel();
        char c = current.getText().charAt(0);
        int base = 'A';
        int next = ((c - base + delta) % 26 + 26) % 26; // wrap 0..25
        current.setText(String.valueOf((char)(base + next)));
    }

    private void incrementLetterFor(Label label, int delta) {
        char c = label.getText().charAt(0);
        int base = 'A';
        int next = ((c - base + delta) % 26 + 26) % 26;
        label.setText(String.valueOf((char)(base + next)));
    }

    private Label getCurrentLabel() {
        return switch (index) {
            case 0 -> ch1;
            case 1 -> ch2;
            default -> ch3;
        };
    }

    private void updateSelectionUnderline() {
        // show arrows only for selected index; underline for others
        boolean s0 = index == 0;
        boolean s1 = index == 1;
        boolean s2 = index == 2;

        up1.setVisible(s0); down1.setVisible(s0); ul1.setVisible(!s0);
        up2.setVisible(s1); down2.setVisible(s1); ul2.setVisible(!s1);
        up3.setVisible(s2); down3.setVisible(s2); ul3.setVisible(!s2);
    }

    private void close() {
        if (root.getScene() != null && root.getScene().getWindow() instanceof javafx.stage.Stage stage) {
            stage.close();
        } else if (root.getParent() instanceof Pane parent) {
            parent.getChildren().remove(root);
        }
    }

    public static StackPane createOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(InputInitialsController.class.getResource("/fxml/InputInitials.fxml"));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void submit() {
        String initials = (ch1.getText() + ch2.getText() + ch3.getText()).trim();
        if (!initials.matches("^[A-Z]{1,3}$")) {
            return;
        }
        try {
            int s = com.kamoteracer.MainApp.getInstance() != null ? com.kamoteracer.MainApp.getInstance().getLastScore() : score;
            String json = "{\"initials\":\"" + initials + "\",\"score\":" + s + "}";
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(new java.net.URI("http://localhost:8080/api/leaderboard/submit"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();
            java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {}
        close();
    }

    public static void show(javafx.stage.Window ownerWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(InputInitialsController.class.getResource("/fxml/InputInitials.fxml"));
            StackPane root = loader.load();
            InputInitialsController c = loader.getController();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Save High Score");
            if (ownerWindow != null) {
                stage.initOwner(ownerWindow);
                stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            } else if (com.kamoteracer.MainApp.getInstance() != null && com.kamoteracer.MainApp.getInstance().getPrimaryStage() != null) {
                javafx.stage.Stage owner = com.kamoteracer.MainApp.getInstance().getPrimaryStage();
                stage.initOwner(owner);
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            } else {
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            }
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 550, 440);
            stage.setScene(scene);
            stage.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, c::onKeyPressed);
            scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, c::onKeyPressed);
            if (stage.getOwner() instanceof javafx.stage.Stage owner) {
                Runnable center = () -> {
                    double cw = stage.getWidth() > 0 ? stage.getWidth() : scene.getWidth();
                    double ch = stage.getHeight() > 0 ? stage.getHeight() : scene.getHeight();
                    double x = owner.getX() + (owner.getWidth() - cw) / 2.0;
                    double y = owner.getY() + (owner.getHeight() - ch) / 2.0;
                    stage.setX(x);
                    stage.setY(y);
                };
                owner.xProperty().addListener((o,a,b) -> center.run());
                owner.yProperty().addListener((o,a,b) -> center.run());
                owner.widthProperty().addListener((o,a,b) -> center.run());
                owner.heightProperty().addListener((o,a,b) -> center.run());
                stage.widthProperty().addListener((o,a,b) -> center.run());
                stage.heightProperty().addListener((o,a,b) -> center.run());
                stage.setOnShown(e -> center.run());
            }
            stage.setOnShown(e -> root.requestFocus());
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void show() {
        javafx.stage.Window owner = null;
        if (com.kamoteracer.MainApp.getInstance() != null && com.kamoteracer.MainApp.getInstance().getPrimaryStage() != null) {
            owner = com.kamoteracer.MainApp.getInstance().getPrimaryStage();
        }
        show(owner);
    }
}
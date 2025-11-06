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

    @FXML
    private void initialize() {
        // Ensure custom font is registered
        try {
            Font.loadFont(getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf"), 12);
        } catch (Exception ignored) {}

        updateSelectionUnderline();
        root.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        root.setFocusTraversable(true);
        root.requestFocus();
        if (closeButton != null) {
            closeButton.setOnAction(e -> close());
        }
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
        if (root.getParent() instanceof Pane parent) {
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
}

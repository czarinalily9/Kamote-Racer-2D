package com.kamoteracer.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class LeaderboardDialogController implements Initializable {

    @FXML private StackPane root;
    @FXML private Node closeButton;

    @FXML private Label rank1Label; @FXML private Label score1Label; @FXML private Label name1Label;
    @FXML private Label rank2Label; @FXML private Label score2Label; @FXML private Label name2Label;
    @FXML private Label rank3Label; @FXML private Label score3Label; @FXML private Label name3Label;
    @FXML private Label rank4Label; @FXML private Label score4Label; @FXML private Label name4Label;
    @FXML private Label rank5Label; @FXML private Label score5Label; @FXML private Label name5Label;

    private List<Label> rankLabels;
    private List<Label> scoreLabels;
    private List<Label> nameLabels;

    private Runnable onClose;

    private static final List<LeaderboardRow> DEFAULT_ROWS = List.of(
            new LeaderboardRow("RKV", 999999),
            new LeaderboardRow("RKV", 999999),
            new LeaderboardRow("RKV", 999999),
            new LeaderboardRow("RKV", 999999),
            new LeaderboardRow("RKV", 999999)
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rankLabels = Arrays.asList(rank1Label, rank2Label, rank3Label, rank4Label, rank5Label);
        scoreLabels = Arrays.asList(score1Label, score2Label, score3Label, score4Label, score5Label);
        nameLabels = Arrays.asList(name1Label, name2Label, name3Label, name4Label, name5Label);

        populateRows(DEFAULT_ROWS);

        if (closeButton != null) {
            closeButton.setPickOnBounds(true);
            closeButton.setCursor(Cursor.HAND);
            closeButton.setOnMouseClicked(e -> closeStage());
        }
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setEntries(List<LeaderboardRow> rows) {
        if (rows == null || rows.isEmpty()) {
            populateRows(DEFAULT_ROWS);
            return;
        }

        List<LeaderboardRow> normalized = new ArrayList<>(rows);
        while (normalized.size() < rankLabels.size()) {
            normalized.add(new LeaderboardRow("RKV", 999999));
        }
        populateRows(normalized);
    }

    private void populateRows(List<LeaderboardRow> rows) {
        for (int i = 0; i < rankLabels.size(); i++) {
            rankLabels.get(i).setText(ordinalFor(i + 1));
            LeaderboardRow row = i < rows.size() ? rows.get(i) : new LeaderboardRow("RKV", 999999);
            scoreLabels.get(i).setText(String.format("%06d", Math.max(0, row.score())));
            nameLabels.get(i).setText(row.initials());
        }
    }

    private String ordinalFor(int number) {
        return switch (number) {
            case 1 -> "1ST";
            case 2 -> "2ND";
            case 3 -> "3RD";
            default -> number + "TH";
        };
    }

    private void closeStage() {
        if (onClose != null) {
            onClose.run();
            return;
        }
        if (root != null && root.getScene() != null) {
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    public List<LeaderboardRow> createPlaceholderRows() {
        return new ArrayList<>(DEFAULT_ROWS);
    }

    public record LeaderboardRow(String initials, int score) {}
}

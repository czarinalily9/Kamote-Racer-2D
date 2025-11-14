package com.kamoteracer.leaderboardGUI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class LeaderboardApp extends Application {

    private TableView<LeaderboardEntry> table = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TableColumn<LeaderboardEntry, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));

        TableColumn<LeaderboardEntry, String> initialCol = new TableColumn<>("Initial");
        initialCol.setCellValueFactory(new PropertyValueFactory<>("initial"));

        TableColumn<LeaderboardEntry, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        table.getColumns().addAll(rankCol, initialCol, scoreCol);
        table.setItems(getLeaderboardData());

        VBox vbox = new VBox(table);
        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Leaderboard");
        primaryStage.show();
    }

    private ObservableList<LeaderboardEntry> getLeaderboardData() {
        ObservableList<LeaderboardEntry> data = FXCollections.observableArrayList();

        String url = "jdbc:mysql://localhost:3306/gameDB";
        String user = "root";
        String password = "password";

        String sql = "SELECT initial, score FROM leaderboard ORDER BY score DESC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                String initial = rs.getString("initial");
                int score = rs.getInt("score");
                data.add(new LeaderboardEntry(rank++, initial, score));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}

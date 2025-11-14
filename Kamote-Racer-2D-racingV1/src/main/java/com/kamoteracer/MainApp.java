package com.kamoteracer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static MainApp instance;
    private Stage primaryStage;
    private int lastScore;
    private String selectedCarPath = "/images/car.png";

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
            Scene scene = new Scene(root, 550, 440);
            primaryStage.setScene(scene);

            javafx.scene.Node leaderboard = root.lookup("#leaderboardButton");
            if (leaderboard != null) {
                leaderboard.setOnMouseClicked(e -> showLeaderboardScene());
            }

            javafx.scene.image.ImageView previewCar = (javafx.scene.image.ImageView) root.lookup("#previewCar");
            javafx.scene.image.ImageView previewBox = (javafx.scene.image.ImageView) root.lookup("#previewBox");
            javafx.scene.image.ImageView car1 = (javafx.scene.image.ImageView) root.lookup("#car1");
            javafx.scene.image.ImageView car2 = (javafx.scene.image.ImageView) root.lookup("#car2");
            javafx.scene.image.ImageView car3 = (javafx.scene.image.ImageView) root.lookup("#car3");

            if (previewCar != null) {
                previewCar.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(selectedCarPath)));
                previewCar.setMouseTransparent(true);
            }
            if (previewBox != null) {
                previewBox.setPickOnBounds(true);
            }

            java.util.function.BiConsumer<javafx.scene.image.ImageView, String> wireCar = (iv, path) -> {
                if (iv == null) return;
                iv.setOnDragDetected(e -> {
                    javafx.scene.input.Dragboard db = iv.startDragAndDrop(javafx.scene.input.TransferMode.COPY_OR_MOVE);
                    javafx.scene.input.ClipboardContent cc = new javafx.scene.input.ClipboardContent();
                    cc.putString(path);
                    db.setContent(cc);
                    if (iv.getImage() != null) {
                        db.setDragView(iv.getImage(), iv.getImage().getWidth() / 2.0, iv.getImage().getHeight() / 2.0);
                    }
                    e.consume();
                });
            };
            wireCar.accept(car1, "/images/car.png");
            wireCar.accept(car2, "/images/car2.png");
            wireCar.accept(car3, "/images/car3.png");

            java.util.function.Consumer<javafx.scene.input.DragEvent> acceptOver = e -> {
                if (e.getDragboard().hasString()) {
                    e.acceptTransferModes(javafx.scene.input.TransferMode.COPY_OR_MOVE);
                }
                e.consume();
            };
            java.util.function.Consumer<javafx.scene.input.DragEvent> handleDrop = e -> {
                String p = e.getDragboard().getString();
                if (p != null) {
                    selectedCarPath = p;
                    if (previewCar != null) {
                        previewCar.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(p)));
                    }
                    e.setDropCompleted(true);
                }
                e.consume();
            };
            if (previewBox != null) {
                previewBox.setOnDragOver(ev -> acceptOver.accept(ev));
                previewBox.setOnDragDropped(ev -> handleDrop.accept(ev));
            }
            if (previewCar != null) {
                previewCar.setOnDragOver(ev -> acceptOver.accept(ev));
                previewCar.setOnDragDropped(ev -> handleDrop.accept(ev));
            }

            root.setOnDragOver(ev -> {
                if (ev.getDragboard().hasString()) {
                    ev.acceptTransferModes(javafx.scene.input.TransferMode.COPY_OR_MOVE);
                }
                ev.consume();
            });
            root.setOnDragDropped(ev -> {
                String p = ev.getDragboard().getString();
                if (p != null && previewBox != null) {
                    javafx.geometry.Bounds b = previewBox.localToScene(previewBox.getBoundsInLocal());
                    javafx.geometry.Point2D pt = new javafx.geometry.Point2D(ev.getSceneX(), ev.getSceneY());
                    if (b.contains(pt)) {
                        selectedCarPath = p;
                        if (previewCar != null) {
                            previewCar.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(p)));
                        }
                        ev.setDropCompleted(true);
                    }
                }
                ev.consume();
            });

            scene.setOnKeyPressed(e -> {
                if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    showGameScene();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getSelectedCarPath() { return selectedCarPath; }

    public void showLeaderboardScene() {
        System.out.println("Showing leaderboard scene...");
        // TODO: Implement loading Leaderboard FXML
    }

    public void showGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Game.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 550, 440);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverScene(int score) {
        try {
            this.lastScore = score;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 550, 440);
            Object controller = loader.getController();
            if (controller instanceof com.kamoteracer.controller.GameOverController c) {
                c.setScore(score);
            }
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverDialog(int score) {
        javafx.application.Platform.runLater(() -> {
            try {
                this.lastScore = score;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
                Parent root = loader.load();
                javafx.stage.Stage dialog = new javafx.stage.Stage();
                dialog.setTitle("Game Over");
                dialog.initOwner(primaryStage);
                dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                javafx.scene.Scene scene = new javafx.scene.Scene(root, 550, 440);
                Object controller = loader.getController();
                if (controller instanceof com.kamoteracer.controller.GameOverController c) {
                    c.setScore(score);
                }
                dialog.setScene(scene);
                Runnable center = () -> {
                    double cw = dialog.getWidth() > 0 ? dialog.getWidth() : scene.getWidth();
                    double ch = dialog.getHeight() > 0 ? dialog.getHeight() : scene.getHeight();
                    double x = primaryStage.getX() + (primaryStage.getWidth() - cw) / 2.0;
                    double y = primaryStage.getY() + (primaryStage.getHeight() - ch) / 2.0;
                    dialog.setX(x);
                    dialog.setY(y);
                };
                primaryStage.xProperty().addListener((o,a,b) -> center.run());
                primaryStage.yProperty().addListener((o,a,b) -> center.run());
                primaryStage.widthProperty().addListener((o,a,b) -> center.run());
                primaryStage.heightProperty().addListener((o,a,b) -> center.run());
                dialog.widthProperty().addListener((o,a,b) -> center.run());
                dialog.heightProperty().addListener((o,a,b) -> center.run());
                dialog.setOnShown(e -> center.run());
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public int getLastScore() {
        return lastScore;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
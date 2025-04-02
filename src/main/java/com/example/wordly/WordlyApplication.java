package com.example.wordly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class WordlyApplication extends Application {
    private Stage primaryStage;
    private StackPane root;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        root = new StackPane();

        Scene mainScene = createMainScene();
        stage.setTitle("Wordly");
        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            showExitConfirmation(stage);
        });
    }

    private Scene createMainScene() {
        Button btn1 = new Button("Giao diện 1");
        Button btn2 = new Button("Giao diện 2");
        Button btn3 = new Button("Giao diện 3");

        btn1.setOnAction(e -> switchScene("View/TextToSpeechView.fxml"));
        btn2.setOnAction(e -> switchScene("View/Scene2.fxml"));
        btn3.setOnAction(e -> switchScene("View/Scene3.fxml"));

        root.getChildren().addAll(btn1, btn2, btn3);
        btn1.setTranslateY(-40);
        btn3.setTranslateY(40);

        Scene scene = new Scene(root, 1080, 720);
        enableWindowDragging(scene);
        return scene;
    }

    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene newScene = new Scene(fxmlLoader.load(), 1080, 720);
            enableWindowDragging(newScene);
            primaryStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableWindowDragging(Scene scene) {
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }

    private void showExitConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận?");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn thoát ứng dụng không?");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("icons/iconAlert.jpg")));
        alert.getDialogPane().getStylesheets().add(getClass().getResource("styles/alert.css").toExternalForm());

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
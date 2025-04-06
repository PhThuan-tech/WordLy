package com.example.wordly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class WordlyApplication extends Application {
    private Stage primaryStage;
    private StackPane root;

    // cho phep di chuyen cua so sang vi tri su dung keo tha chuot
    private double xOffset = 0;
    private double yOffset = 0;

    // de truy cap vao cac giao dien khac thi can 1 MainController
    // gom cac nut bam de di chuyen
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;  // su dung giao dien chinh

        FXMLLoader fxmlLoader = new FXMLLoader(WordlyApplication.class.getResource("View/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        // Load them CSS trong start
        scene.getStylesheets().add(getClass()
                .getResource("/com/example/wordly/styles/mainsce.css").toExternalForm());

        // dat ten tieu de cua app
        stage.setTitle("Learning With Wordly <3");
        stage.setScene(scene);
        stage.setResizable(false);  // khoa cua so man hinh chinh
        stage.show();

        // hien thi canh bao khi dong cua so
        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();

            // mac dinh la bam Ok se thoat ( chinh sua them o sau TYPE.)
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận?");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc muốn thoát ứng dụng không?");

            // di chuyen cua so o vi tri bat ki
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            // thay doi icon cua xac nhan thoat
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("icons/iconAlert.jpg")));


            // thay doi mau sac giao dien dua tren css
            alert.getDialogPane().getStylesheets().add(getClass().getResource("styles/alert.css").toExternalForm());

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage.close();
            }
        });
    }
}
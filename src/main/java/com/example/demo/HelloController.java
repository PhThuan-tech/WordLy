package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.Objects;


public class HelloController {
    @FXML public HBox topHBOx;
    @FXML public TextField searchBar;
    @FXML public Button searchButton;
    @FXML public VBox centerVBox;
    @FXML public Label wordLabel;
    @FXML public Label wordTypeLabel;
    @FXML public Label pronunciation;
    @FXML public TextArea example;
    @FXML public GridPane bottomGrid;
    @FXML public TextArea usage;
    public Label wordRow;
    public Label wordTypeRow;
    public Label pronunciationRow;
    public Label usingRow;
    public Button listen;
    public VBox mainContainer;
    public Label notFoundLabel;
    public Button favorite;

    /**
     * Action when click searchButton.
     */
    @FXML
    private void searching() {
        String input = searchBar.getText().trim();

        wordDetails details = DictionaryService.search(input);

        if (details != null) {
            centerVBox.setVisible(true);
            bottomGrid.setVisible(false);
            updateCenterVBox(details);

            System.out.println(details.getWord() + " " + details.getPronunciation());

            DictionaryService.addHistory(details.getWord());
        } else {
            Alert notFind = new Alert(Alert.AlertType.INFORMATION);
            notFind.setTitle("Không thể tìm thấy từ");
            notFind.setHeaderText(null);
            notFind.setContentText("Không tìm thấy từ của bạn mong muốn, vui lòng thử lại");
            notFind.showAndWait();
        }

    }

    private void updateCenterVBox(wordDetails details) {
        Platform.runLater(() -> {
            wordLabel.setText(details.getWord());
            wordTypeLabel.setText(details.getType());
            pronunciation.setText(details.getPronunciation());
            usage.setText(details.getUsage());
            example.setText(details.getExamples());
        });
    }

    @FXML
    public void initialize() {

        String imagePath = Objects.requireNonNull(getClass().getResource("BigBenTower.jpg")).toExternalForm();

        BackgroundImage BI = new BackgroundImage(
                new Image(imagePath, mainContainer.getWidth(), mainContainer.getHeight(), false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );

        mainContainer.setBackground(new Background(BI));

        searchButton.setOnAction(_ -> searching());
        bottomGrid.setVisible(true);
        centerVBox.setVisible(false);

        searchBar.setOnKeyPressed(keyEvent ->  {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                searching();
            }
        });
    }
}
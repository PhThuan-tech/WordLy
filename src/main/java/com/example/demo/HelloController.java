package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;


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
        }
    }

    private void updateCenterVBox(wordDetails details) {
        Platform.runLater(() -> { // Đảm bảo các cập nhật UI nằm trên JavaFX thread
            wordLabel.setText(details.getWord());
            wordTypeLabel.setText(details.getType());
            pronunciation.setText(details.getPronunciation());
            usage.setText(details.getUsage());
            example.setText(details.getExamples());
        });
    }

    @FXML
    public void initialize() {

        String imagePath = getClass().getResource("BigBenTower.jpg").toExternalForm();

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
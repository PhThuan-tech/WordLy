package com.example.demo;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

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
    @FXML public Label wordRow; //Use @FXML for elements defined in FXML file
    @FXML public Label wordTypeRow;
    @FXML public Label pronunciationRow;
    @FXML public Label usingRow;
    @FXML public Button listen;
    @FXML public VBox mainContainer;
    @FXML public Label notFoundLabel;
    @FXML public Button favorite;

    /**
     * Action when click searchButton.
     */
    @FXML
    private void searching() {
        String input = searchBar.getText().trim();
        wordDetails details = null; //Initialize to null
        try {
            details = DictionaryService.search(input);
            DictionaryService.readHistory();
        } catch (Exception e) {
            logger.error("Error during search: ", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred during the search. Please try again later.");
            alert.showAndWait();
            return; // Stop the searching() execution
        }


        if (details != null) {
            centerVBox.setVisible(true);
            bottomGrid.setVisible(false);
            updateCenterVBox(details);

            logger.info("Found word: {} with pronunciation: {}", details.getWord(), details.getPronunciation());

            DictionaryService.addHistory(details.getWord()); //Consider handling exceptions in addHistory

            listen.setOnAction(e-> API.pronunciationUsingAPI(input));

        } else {
            showAlert("Không tìm thấy từ của bạn mong muốn, vui lòng thử lại");
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
        //Load background image
        try{
            String imagePath = Objects.requireNonNull(getClass().getResource("BigBenTower.jpg")).toExternalForm();

            BackgroundImage BI = new BackgroundImage(
                    new Image(imagePath, mainContainer.getWidth(), mainContainer.getHeight(), false, true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT
            );

            mainContainer.setBackground(new Background(BI));
        } catch (Exception e){
            logger.error("Error loading background image", e);
        }

        //Search Button Action
        searchButton.setOnAction(e -> searching());

        //Initialize other UI components
        bottomGrid.setVisible(true);
        centerVBox.setVisible(false);

        //Enter Key Press Event on searchBar
        searchBar.setOnKeyPressed(keyEvent ->  {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                searching();
            }
        });
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Không thể tìm thấy từ");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
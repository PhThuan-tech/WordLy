package com.example.wordly.TTS;

import com.example.wordly.controllerForUI.BaseController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.invoke.MethodHandles; // For logger name
import java.util.logging.Level; // For logging levels
import java.util.logging.Logger; // For logging

/**
 * Controller for the Translate and Text-To-Speech view (TranslateTTSView.fxml).
 * Handles user interactions for translating text using LibreTranslator,
 * displaying the translation, providing text-to-speech functionality (placeholder buttons),
 * and navigating to other application views.
 */
public class TranslateAndTTSController extends BaseController {
    // --- Logger ---
    // Use java.util.logging (JUL) for simplicity, or SLF4J if preferred in the project
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    //Ông ko cần comment ở phần FXML đâu á.
    // vì cái này khi tạo hàm view.fxml là nó tự tạo thôi. mình tự hiểu được.

    @FXML private TextArea needToTrans;

    @FXML private TextArea translated;

    @FXML private Button transButton;

    @FXML private Button speak1Button; // Placeholder for future TTS

    @FXML private Button speak2Button; // Placeholder for future TTS

    /**
     * Xử lí chuyển đổi giữa các giao diện.
     */
    @FXML
    public void initialize() {
        LOGGER.info("TranslateAndTTSController initialized.");
    }

    // Tôi sửa phần chuyển đổi giao diện thôi nhé.
    // Phần nào cần sự trợ giúp thì ông hú tôi cái tôi nghĩ cùng.
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/MainView.fxml");
    }

    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml");
    }

    @FXML
    public void handleGoToFavourite(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml");
    }

    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml");
    }

    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml");
    }

    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/HistoryView.fxml");
    }
    // --- Core Functionality Event Handlers ---

    /**
     * Handles the action event when the 'Translate' button is clicked.
     * Retrieves text from the input TextArea, validates it, and initiates
     * an asynchronous translation task using {@link LibreTranslator}.
     * Updates the UI with the result or displays an error message.
     * Disables the translation button during the operation.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleTranslating(ActionEvent actionEvent) {
        String textToTranslate = needToTrans.getText();

        // 1. Input Validation
        if (textToTranslate == null || textToTranslate.isBlank()) {
            LOGGER.warning("Translation attempt with empty input.");
            showInfoAlert("Input Required", "Please enter text in the box before translating.");
            translated.clear(); // Clear previous translation if any
            return;
        }

        // 2. Prepare UI for Background Task
        setTranslationInProgress(true);

        // 3. Create and Configure Background Task
        Task<String> translationTask = createTranslationTask(textToTranslate);

        // 4. Define Task Completion Behavior (on JavaFX Application Thread)
        translationTask.setOnSucceeded(event -> {
            String result = translationTask.getValue();
            if (result != null && !result.isEmpty()) {
                LOGGER.info("Translation successful.");
                translated.setText(result);
            } else {
                // Handle cases where the API returns an empty or null response successfully
                LOGGER.warning("Translation API returned empty or null result for input: " + textToTranslate);
                translated.clear(); // Clear the output area
                showErrorAlert("Translation Result Issue", "The translation service returned an empty result. The input might be unsupported or untranslatable.");
            }
            setTranslationInProgress(false); // Re-enable UI
        });

        translationTask.setOnFailed(event -> {
            Throwable exception = translationTask.getException();
            LOGGER.log(Level.SEVERE, "Translation task failed.", exception);
            translated.clear(); // Clear the output area
            showErrorAlert("Translation Error", "An error occurred during translation:\n" + getConciseErrorMessage(exception));
            setTranslationInProgress(false); // Re-enable UI
        });

        // 5. Start the Task on a Background Thread
        new Thread(translationTask, "TranslationThread").start(); // Give the thread a name
    }


    /**
     * Creates a background task for performing the translation.
     *
     * @param text The text to be translated.
     * @return A {@link Task} that performs the translation and returns the result string.
     */
    private Task<String> createTranslationTask(String text) {
        return new Task<>() {
            @Override
            protected String call() throws Exception { // Can throw Exception for broader catching
                try {
                    LOGGER.fine("Starting translation for: " + text.substring(0, Math.min(text.length(), 50)) + "..."); // Log snippet
                    // Simulate network delay for testing:
                    // Thread.sleep(2000);
                    return LibreTranslator.translate(text);
                } catch (IOException e) {
                    // Network or API communication error
                    LOGGER.log(Level.WARNING, "IOException during translation", e);
                    throw new Exception("Network or API communication failed. Please check your connection.", e); // Re-throw specific user-friendly message
                } catch (Exception e) {
                    // Catch other potential runtime exceptions from the translator library
                    LOGGER.log(Level.SEVERE, "Unexpected error during translation logic", e);
                    throw new Exception("An unexpected error occurred in the translation service.", e); // Re-throw generic user-friendly message
                }
            }
        };
    }

    /**
     * Updates the UI state to indicate whether a translation is in progress.
     * Disables/enables the translation button and shows/hides a progress indicator.
     *
     * @param inProgress true if translation is starting, false if it has finished or failed.
     */
    private void setTranslationInProgress(boolean inProgress) {
        transButton.setDisable(inProgress);
        // Optional: Show/hide progress indicator
        // if (translationProgress != null) {
        //    translationProgress.setVisible(inProgress);
        // }
        LOGGER.fine("Setting translation progress UI state to: " + inProgress);
    }

    // --- Helper Methods ---

    /**
     * Navigates to a different view within the application.
     * Loads the specified FXML file and sets it as the root of the current scene.
     * Handles potential {@link IOException} during FXML loading and displays an error alert.
     *
     * @param event       The {@link ActionEvent} that triggered the navigation (used to find the stage).
     * @param fxmlPath    The resource path to the FXML file for the target view.
     * @param viewName    A user-friendly name for the target view (used in logging/errors).
     */
    private void navigateToView(ActionEvent event, String fxmlPath, String viewName) {
        try {
            LOGGER.info("Navigating to " + viewName + " (" + fxmlPath + ")");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find FXML resource: " + fxmlPath);
            }
            Parent viewRoot = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (stage != null) {
                Scene currentScene = stage.getScene();
                if (currentScene == null) {
                    // Should not happen in typical JavaFX apps, but defensive check
                    Scene newScene = new Scene(viewRoot);
                    stage.setScene(newScene);
                    LOGGER.warning("Current scene was null. Created a new scene for " + viewName);
                } else {
                    currentScene.setRoot(viewRoot);
                }
            } else {
                LOGGER.severe("Could not find stage for navigation event.");
                showErrorAlert("Navigation Error", "Unable to navigate: Could not find the application window.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML for " + viewName + ": " + fxmlPath, e);
            showErrorAlert("Navigation Error", "Could not load the " + viewName + ".\n" + e.getMessage());
        } catch (IllegalStateException e) {
            // FXMLLoader might throw this if the FXML is invalid or controller issues arise
            LOGGER.log(Level.SEVERE, "Failed to load FXML for " + viewName + " due to application state: " + fxmlPath, e);
            showErrorAlert("Navigation Error", "Could not load the " + viewName + ".\nInternal application error occurred.");
        }
    }

    /**
     * Displays an error alert dialog to the user.
     * Ensures the alert is shown on the JavaFX Application Thread.
     *
     * @param title   The title of the alert window.
     * @param content The main message content of the alert.
     */
    private void showErrorAlert(String title, String content) {
        // Ensure alert is shown on the correct thread
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null); // No header text, title is sufficient
            alert.setContentText(content);
            // Consider adding expandable content for stack traces if needed for debugging
            // alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(exceptionStackTrace)));
            alert.showAndWait();
        });
    }

    /**
     * Displays an informational alert dialog to the user.
     * Ensures the alert is shown on the JavaFX Application Thread.
     *
     * @param title   The title of the alert window.
     * @param content The main message content of the alert.
     */
    private void showInfoAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Extracts a concise, user-friendly message from a Throwable.
     * Prefers the Throwable's message, falls back to the class name if the message is null/blank.
     *
     * @param t The throwable exception.
     * @return A non-null, non-blank error message string.
     */
    private String getConciseErrorMessage(Throwable t) {
        if (t == null) {
            return "An unknown error occurred.";
        }
        String message = t.getMessage();
        if (message != null && !message.isBlank()) {
            return message;
        } else {
            // If no message, return the exception type as a basic indicator
            return "Error type: " + t.getClass().getSimpleName();
        }
    }

    // --- Placeholder Methods for TTS ---
    // TODO: Implement actual Text-To-Speech logic for speak1Button and speak2Button

    @FXML
    private void handleSpeakOriginal(ActionEvent event) {
        String text = needToTrans.getText();
        if (text != null && !text.isBlank()) {
            LOGGER.info("TTS requested for original text (length: " + text.length() + ")");
            // Add TTS implementation here
            showInfoAlert("TTS Placeholder", "Speaking original text: \"" + text.substring(0, Math.min(text.length(), 30)) + "...\"");
        } else {
            showInfoAlert("TTS Unavailable", "No original text available to speak.");
        }
    }

    @FXML
    private void handleSpeakTranslated(ActionEvent event) {
        String text = translated.getText();
        if (text != null && !text.isBlank()) {
            LOGGER.info("TTS requested for translated text (length: " + text.length() + ")");
            // Add TTS implementation here
            showInfoAlert("TTS Placeholder", "Speaking translated text: \"" + text.substring(0, Math.min(text.length(), 30)) + "...\"");
        } else {
            showInfoAlert("TTS Unavailable", "No translated text available to speak.");
        }
    }
}
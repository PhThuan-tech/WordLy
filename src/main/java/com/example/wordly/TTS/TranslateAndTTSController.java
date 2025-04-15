package com.example.wordly.TTS;

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
public class TranslateAndTTSController {

    // --- Logger ---
    // Use java.util.logging (JUL) for simplicity, or SLF4J if preferred in the project
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    // --- FXML View Paths (Constants for maintainability) ---
    private static final String MAIN_VIEW_PATH = "/com/example/wordly/view/MainView.fxml";
    private static final String SEARCH_VIEW_PATH = "/com/example/wordly/view/SearchView.fxml"; // Corrected path casing
    private static final String FAVOURITE_VIEW_PATH = "/com/example/wordly/view/FavouriteView.fxml"; // Corrected path casing
    private static final String EDIT_WORD_VIEW_PATH = "/com/example/wordly/view/EditWordView.fxml"; // Corrected path casing
    private static final String GAME_VIEW_PATH = "/com/example/wordly/view/GameView.fxml"; // Corrected path casing
    private static final String HISTORY_VIEW_PATH = "/com/example/wordly/view/HistoryView.fxml"; // Corrected path casing

    // --- FXML Injected Fields ---

    /** TextArea for user to input text to be translated. */
    @FXML private TextArea needToTrans;

    /** TextArea to display the translated text. */
    @FXML private TextArea translated;

    /** Button to trigger the translation process. */
    @FXML private Button transButton;

    /** Button to trigger text-to-speech for the original text (implementation pending). */
    @FXML private Button speak1Button; // Placeholder for future TTS

    /** Button to trigger text-to-speech for the translated text (implementation pending). */
    @FXML private Button speak2Button; // Placeholder for future TTS

    // Optional: Add a progress indicator for visual feedback during translation
    // @FXML private ProgressIndicator translationProgress;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     * Can be used for setup tasks like binding properties or setting initial states.
     */
    @FXML
    public void initialize() {
        // Example: Disable speak buttons initially if no text is present
        LOGGER.info("TranslateAndTTSController initialized.");
    }

    // --- Navigation Event Handlers ---

    /**
     * Handles the action event when the 'Back to Main' button is clicked.
     * Navigates the user back to the main application view.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleBackMain(ActionEvent actionEvent) {
        navigateToView(actionEvent, MAIN_VIEW_PATH, "Main View");
    }

    /**
     * Handles the action event when the 'Go to Search' button is clicked.
     * Navigates the user to the search view.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleGoToSearch(ActionEvent actionEvent) {
        navigateToView(actionEvent, SEARCH_VIEW_PATH, "Search View");
    }

    /**
     * Handles the action event when the 'Go to Favourite' button is clicked.
     * Navigates the user to the favourite words view.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleGoToFavourite(ActionEvent actionEvent) {
        navigateToView(actionEvent, FAVOURITE_VIEW_PATH, "Favourite View");
    }

    /**
     * Handles the action event when the 'Go to Edit' button is clicked.
     * Navigates the user to the word editing view.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleGotoEdit(ActionEvent actionEvent) {
        navigateToView(actionEvent, EDIT_WORD_VIEW_PATH, "Edit Word View");
    }

    /**
     * Handles the action event when the 'Go to Game' button is clicked.
     * Navigates the user to the game view.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleGotoGame(ActionEvent actionEvent) {
        navigateToView(actionEvent, GAME_VIEW_PATH, "Game View");
    }

    /**
     * Handles the action event when the 'Go to History' button is clicked.
     * Navigates the user to the search history view.
     *
     * @param actionEvent The event triggered by clicking the button.
     */
    @FXML
    public void handleGoToHistory(ActionEvent actionEvent) {
        navigateToView(actionEvent, HISTORY_VIEW_PATH, "History View");
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
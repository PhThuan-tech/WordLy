package com.example.wordly.getWord;


import javafx.concurrent.Task;

import java.io.IOException;

/**
 * TODO : Handles the action triggered when the search button is clicked.
 */
public class SearchButtonClickHandle {
    private final SearchUIUpdate uiUpdate;

    public SearchButtonClickHandle(SearchUIUpdate uiUpdate, GetAPI api) {
        this.uiUpdate = uiUpdate;
    }

    public void handleSearch() {
        String word = uiUpdate.getSearchTerm();
        System.out.println("Từ của bro là: " + word);

        if (word.isEmpty()) {
            uiUpdate.updateStatus("Bro nhập sai rồi, nhập lại nhanh");
            uiUpdate.clearResult();
            return;
        }

        uiUpdate.resetSearchButton(true);
        uiUpdate.updateStatus("Đang load nè má");
        uiUpdate.clearResult();

        Task<WordDetails> lookingupTask = new Task<>() {

            @Override
            protected WordDetails call() throws Exception {
                return GetAPI.fetchWordDetails(word);
            }
        };

        lookingupTask.setOnSucceeded(e-> {
            WordDetails details = lookingupTask.getValue();
            try {
                uiUpdate.displayResult(details);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            uiUpdate.updateStatus("Tìm thấy rồi cán bộ ơi!!");
            uiUpdate.resetSearchButton(false);
        });

        lookingupTask.setOnFailed(e-> {
            Throwable exception = lookingupTask.getException();

            System.err.println(exception.getMessage());

            uiUpdate.updateStatus(exception.getMessage());
            uiUpdate.clearResult();
            uiUpdate.resetSearchButton(false);
        });

        new Thread(lookingupTask).start();
    }
}

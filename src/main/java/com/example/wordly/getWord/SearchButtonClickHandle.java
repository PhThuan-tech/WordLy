package com.example.wordly.getWord;


import javafx.concurrent.Task;

/**
 * Xử lí sự kiện khi searchButton được ấn.
 */
public class SearchButtonClickHandle {
    private final SearchUIUpdate uiUpdate;

    public SearchButtonClickHandle(SearchUIUpdate uiUpdate, GetAPI api) {
        this.uiUpdate = uiUpdate;
    }

    public void handleSearch() {
        String word = uiUpdate.getSearchTerm();
        System.out.println("Tu nhan dc la " + word);

        if (word.isEmpty()) {
            uiUpdate.updateStatus("Deo thay");
            uiUpdate.clearResult();
            return;
        }

        uiUpdate.resetSearchButton(true);
        uiUpdate.updateStatus("Searching.......");
        uiUpdate.clearResult();

        Task<WordDetails> lookingupTask = new Task<>() {

            @Override
            protected WordDetails call() throws Exception {
                return GetAPI.fetchWordDetails(word);
            }
        };

        lookingupTask.setOnSucceeded(e-> {
            WordDetails details = lookingupTask.getValue();
            uiUpdate.displayResult(details);
            uiUpdate.updateStatus("Tim thay roi can bo oi");
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

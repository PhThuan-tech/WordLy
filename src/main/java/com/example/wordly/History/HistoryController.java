package com.example.wordly.History;

import com.example.wordly.controllerForUI.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryController extends BaseController implements Initializable {
    // --- HANG SO  ---
    private static final String HISTORY_FILE_PATH = "src/history.txt"; // DUONG DAN DEN FILE LICH SU
    private static final int MAX_ROW = 50; // SO DONG TOI DA

    // --- THANH PHAN FXML ---
    @FXML private TableView<WordEntry> historyTable;
    @FXML private TableColumn<WordEntry, String> wordCol;
    @FXML private TableColumn<WordEntry, String> proCol;
    @FXML private TableColumn<WordEntry, String> typeCol;
    @FXML private TableColumn<WordEntry, String> meanCol;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;

    // THREAD POOL : XU LI CAC TAC VU KHAC O MOT LUONG RIENG
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    // --- Initialization ---
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Configure Table Columns
        setupTableColumns();

        // 2. Set Initial UI State (loading)
        setLoadingState(true, "Đang tải lịch sử...");

        // 3. Start Loading History Data in Background
        startLoadingHistoryTask();
    }

    // --- UI Setup Helper ---
    private void setupTableColumns() {
        wordCol.setCellValueFactory(new PropertyValueFactory<>("word"));
        proCol.setCellValueFactory(new PropertyValueFactory<>("pronunciation"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        meanCol.setCellValueFactory(new PropertyValueFactory<>("definition"));
    }

    /**
     * Cài đặt trạng thái hiển thị cho lúc đang tải hoặc đã tải xong.
     * @param isLoading true nếu đang tải, false nếu đã xong.
     * @param statusMessage Thông báo trạng thái hiển thị.
     */
    private void setLoadingState(boolean isLoading, String statusMessage) {
        if (historyTable != null) historyTable.setVisible(!isLoading);
        if (historyTable != null && !isLoading) historyTable.setPlaceholder(new Label("")); // Clear placeholder khi hiện bảng
        if (loadingIndicator != null) loadingIndicator.setVisible(isLoading);
        if (statusLabel != null) statusLabel.setText(statusMessage);
        if (statusLabel != null) {
            statusLabel.textProperty().isBound();
        }
    }

    /**
     * Cập nhật trạng thái lỗi trên giao diện.
     * @param errorMessage Thông báo lỗi.
     */
    private void setErrorState(String errorMessage) {
        if (historyTable != null) historyTable.setVisible(true); // Hiển thị bảng để hiện placeholder lỗi
        if (historyTable != null) historyTable.setPlaceholder(new Label(errorMessage));
        if (loadingIndicator != null) loadingIndicator.setVisible(false);
        if (statusLabel != null) statusLabel.setText("Lỗi!");
        if (statusLabel != null && statusLabel.textProperty().isBound()) {
            statusLabel.textProperty().unbind();
        }
    }


    /**
     * Khởi tạo và bắt đầu Task để tải dữ liệu lịch sử từ file.
     */
    private void startLoadingHistoryTask() {
        Task<ObservableList<WordEntry>> loadHistoryTask = createLoadHistoryTask();

        loadHistoryTask.setOnSucceeded(_ -> {
            ObservableList<WordEntry> historyData = loadHistoryTask.getValue();
            if (historyTable != null) {
                historyTable.setItems(historyData);
                if (historyData.isEmpty() && historyTable.getPlaceholder() instanceof Label) {
                    ((Label)historyTable.getPlaceholder()).setText("Không có lịch sử nào để hiển thị.");
                }
            }
            setLoadingState(false, loadHistoryTask.getMessage()); // Cập nhật UI khi thành công
        });

        loadHistoryTask.setOnFailed(_ -> {
            Throwable error = loadHistoryTask.getException();
            System.err.println("Task tải lịch sử thất bại.");
            if (error != null) {
                error.printStackTrace();
            }
            setErrorState("Lỗi tải lịch sử. Vui lòng kiểm tra file."); // Cập nhật UI khi thất bại
        });

        if (statusLabel != null) {
            statusLabel.textProperty().bind(loadHistoryTask.messageProperty());
            loadHistoryTask.runningProperty().addListener((obs, wasRunning, isRunning) -> {
                if (!isRunning) {
                    statusLabel.textProperty().unbind();
                }
            });
        }

        backgroundExecutor.submit(loadHistoryTask);
    }

    /**
     * Tạo Task để thực hiện việc đọc file lịch sử.
     * @return Task tải lịch sử.
     */
    private Task<ObservableList<WordEntry>> createLoadHistoryTask() {
        return new Task<>() {
            @Override
            protected ObservableList<WordEntry> call() throws Exception {
                updateMessage("Đang đọc file...");
                Path path = Paths.get(HISTORY_FILE_PATH);
                List<String> allLines;
                ObservableList<WordEntry> data = FXCollections.observableArrayList();

                try {
                    // Đọc tất cả các dòng từ file
                    allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    updateMessage("Đã đọc xong file. Đang xử lý " + allLines.size() + " dòng...");
                } catch (NoSuchFileException e) {
                    updateMessage("Chưa có lịch sử nào.");
                    System.out.println("File lịch sử '" + HISTORY_FILE_PATH + "' không tồn tại.");
                    return data; // Trả về danh sách rỗng, không phải lỗi
                } catch (IOException e) {
                    System.err.println("Lỗi I/O khi đọc file lịch sử: " + e.getMessage());
                    updateMessage("Lỗi đọc file!");
                    throw e;
                }

                // Xử lý chỉ MAX_ROW dòng cuối cùng
                int totalLines = allLines.size();
                int startIndex = Math.max(0, totalLines - MAX_ROW);
                List<WordEntry> recentEntries = new ArrayList<>();

                for (int i = startIndex; i < totalLines; i++) {
                    if (isCancelled()) {
                        updateMessage("Đã hủy tải.");
                        return FXCollections.observableArrayList(); // Trả về danh sách rỗng nếu bị hủy
                    }

                    String line = allLines.get(i).trim();
                    if (line.isEmpty()) continue; // Bỏ qua dòng trống

                    String[] parts = line.split("\t");
                    if (parts.length >= 4) {
                        String word = parts[0].trim();
                        String pronunciation = parts[1].trim();
                        String type = parts[2].trim();

                        StringBuilder definitionBuilder = new StringBuilder(parts[3].trim());
                        for (int j = 4; j < parts.length; j++) {
                            definitionBuilder.append("\t").append(parts[j]);
                        }
                        recentEntries.add(new WordEntry(word, pronunciation, type, definitionBuilder.toString()));
                    } else {
                        System.err.println("Bỏ qua dòng không hợp lệ: " + line);
                    }
                }
                data.addAll(recentEntries);
                updateMessage("Hiển thị " + data.size() + " mục lịch sử gần nhất.");
                return data;
            }
        };
    }

    // --- History Deletion ---
    @FXML
    public void delHistory(ActionEvent event) {
        // Xác nhận trước khi xóa
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn muốn xóa toàn bộ lịch sử tra cứu không?",
                ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Xác nhận Xóa Lịch Sử");
        confirmAlert.setHeaderText(null);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                startDeletingHistoryTask();
            }
        });
    }

    /**
     * Khởi tạo và bắt đầu Task để xóa nội dung file lịch sử.
     */
    private void startDeletingHistoryTask() {
        setLoadingState(true, "Đang xóa lịch sử..."); // Có thể dùng trạng thái loading khác
        if (historyTable != null) historyTable.getItems().clear(); // Xóa bảng ngay lập tức để phản hồi nhanh

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Path path = Paths.get(HISTORY_FILE_PATH);
                try {
                    // Ghi đè file với nội dung trống
                    Files.writeString(path, "", StandardCharsets.UTF_8);
                    System.out.println("Đã xóa nội dung file lịch sử: " + path);
                    updateMessage("Lịch sử đã được xóa.");
                } catch (IOException e) {
                    System.err.println("Lỗi khi xóa file lịch sử: " + e.getMessage());
                    updateMessage("Lỗi xóa lịch sử!");
                    throw e;
                }
                return null;
            }
        };

        deleteTask.setOnSucceeded(_ -> {
            // Cập nhật UI sau khi xóa thành công
            setLoadingState(false, deleteTask.getMessage());
            if(historyTable != null) historyTable.setPlaceholder(new Label("Lịch sử đã được xóa."));
        });

        deleteTask.setOnFailed(failEvent -> {
            setErrorState("Lỗi: Không thể xóa lịch sử.");
            Throwable ex = deleteTask.getException();
            if (ex != null) ex.printStackTrace();
        });

        backgroundExecutor.submit(deleteTask);
    }

    // --- PHUONG THUC CHUYEN TAB ---
    @FXML public void handleBackMain(ActionEvent actionEvent) { shutdownExecutor(); switchScene(actionEvent, "/com/example/wordly/view/MainView.fxml"); }
    @FXML public void handleGoToSearch(ActionEvent actionEvent) { shutdownExecutor(); switchScene(actionEvent, "/com/example/wordly/View/SearchView.fxml"); }
    @FXML public void handleGoToFavourite(ActionEvent actionEvent) { shutdownExecutor(); switchScene(actionEvent, "/com/example/wordly/View/FavouriteView.fxml"); }
    @FXML public void handleGotoEdit(ActionEvent actionEvent) { shutdownExecutor(); switchScene(actionEvent, "/com/example/wordly/View/EditWordView.fxml"); }
    @FXML public void handleGotoGame(ActionEvent actionEvent) { shutdownExecutor(); switchScene(actionEvent, "/com/example/wordly/View/GameView.fxml"); }
    @FXML public void GoToAdvanceFeature(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/Advance_Features.fxml");
    }
    @FXML public void handleGoToChat(ActionEvent actionEvent) {
        switchScene(actionEvent, "/com/example/wordly/View/ChatView.fxml");
    }

    /**
     * TAT EXECUTOR
     */
    private void shutdownExecutor() {
        try {
            backgroundExecutor.shutdown();
        } catch (Exception e) {
            System.err.println("Error shutting down executor in HistoryController: " + e.getMessage());
            backgroundExecutor.shutdownNow();
        }
    }


}
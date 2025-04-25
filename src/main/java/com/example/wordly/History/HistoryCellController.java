package com.example.wordly.History; // Đảm bảo đúng package

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HistoryCellController {

    @FXML private Label wordLabel;
    @FXML private Label pronunciationLabel;
    @FXML private Label typeLabel;
    @FXML private Label meaningLabel;
    @FXML private VBox historyCellContainer; // Tham chiếu đến root VBox để có thể lấy nó ra

    // Phương thức để cập nhật UI của cell với dữ liệu WordEntry
    public void updateCell(WordEntry entry) { // *** THAY ĐỔI KIỂU DỮ LIỆU THAM SỐ ***
        if (entry != null) {
            wordLabel.setText(entry.getWord());
            pronunciationLabel.setText(entry.getPronunciation());
            typeLabel.setText("(" + entry.getType() + ")"); // Thêm dấu ngoặc cho loại từ
            meaningLabel.setText(entry.getDefinition()); // *** SỬ DỤNG getDefinition() ***
        } else {
            // Xóa nội dung nếu entry là null (ví dụ: khi cell trống)
            wordLabel.setText("");
            pronunciationLabel.setText("");
            typeLabel.setText("");
            meaningLabel.setText("");
        }
    }

    // Cần getter cho root VBox để CellFactory có thể lấy node này
    public VBox getLayout() {
        return historyCellContainer;
    }
}
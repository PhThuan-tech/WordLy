package com.example.wordly.controllerForUI;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

// Tạo 1 base cơ bản để chuyển cảnh
/*
    Tuy nhiên các giao diện có thể dùng tất cả các phương thức
    hoặc bỏ 1 ít nên cần khai báo một lớp là abstract
*/
public abstract class BaseController {
    protected void switchScene(ActionEvent  event, String fxmlPath) {
        // thuong se co xay ra ngoai le
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();    // Dùng theo anh Tuyền Hướng dẫn
        }
    }

    protected void applyHoverEffectToAllButtons(Parent root) {
        for (Node node : root.lookupAll(".button")) {
            if (node instanceof Button) {
                addHoverEffect(node);
            }
        }
    }

    protected void addHoverEffect(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}


// Để sử dụng đc cái lớp này
// ở mỗi controller thì extends (hoặc implents thêm các tính năng)
// như API , UI ,CSS,...
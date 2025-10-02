package app.subd;

import javafx.scene.control.Label;

public class MessageController {

    public static void showError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12; -fx-padding: 5 0 0 0;");
    }

    public static void showSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12; -fx-padding: 5 0 0 0;");
    }

    public static void showInfo(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #3498db; -fx-font-size: 12; -fx-padding: 5 0 0 0;");
    }

    public static void clearStatus(Label label) {
        label.setText("");
        label.setStyle("");
    }
}
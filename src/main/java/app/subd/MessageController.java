package app.subd;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class MessageController {

    public static void showError(String message) {
        showAlert(AlertType.ERROR, "Ошибка", message);
    }

    public static void showSuccess(String message) {
        showAlert(AlertType.INFORMATION, "Успех", message);
    }

    public static void showInfo(String message) {
        showAlert(AlertType.INFORMATION, "Информация", message);
    }

    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void showError(Label ignored, String message) {
        showError(message);
    }

    public static void showSuccess(Label ignored, String message) {
        showSuccess(message);
    }

    public static void showInfo(Label ignored, String message) {
        showInfo(message);
    }
}
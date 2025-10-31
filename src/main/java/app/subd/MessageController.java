package app.subd;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MessageController {

    private static void showMessage(Label label, String message, String style) {
        label.setText(message);
        label.setStyle(style);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> clearStatus(label));
        pause.play();
    }

    public static void showError(Label label, String message) {
        showMessage(label, message, "-fx-text-fill: #e74c3c; -fx-font-size: 12; -fx-padding: 5 0 0 0;");
    }

    public static void showSuccess(Label label, String message) {
        showMessage(label, message, "-fx-text-fill: #27ae60; -fx-font-size: 12; -fx-padding: 5 0 0 0;");
    }

    public static void showInfo(Label label, String message) {
        showMessage(label, message, "-fx-text-fill: #3498db; -fx-font-size: 12; -fx-padding: 5 0 0 0;");
    }

    public static void clearStatus(Label label) {
        label.setText("");
        label.setStyle("");
    }
}

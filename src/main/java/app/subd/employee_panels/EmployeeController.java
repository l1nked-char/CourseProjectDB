package app.subd.employee_panels;

import app.subd.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class EmployeeController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Сотрудник: " + Session.getUsername());
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();

            Parent root = FXMLLoader.load(getClass().getResource("/app/subd/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Авторизация");
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

            Stage currentStage = (Stage) mainTabPane.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showNewBooking() {
        // Логика нового бронирования
        System.out.println("Новое бронирование");
    }

    @FXML
    private void showCurrentBookings() {
        // Логика просмотра текущих бронирований
        System.out.println("Текущие бронирования");
    }

    @FXML
    private void showClientRegistration() {
        // Логика регистрации клиента
        System.out.println("Регистрация клиента");
    }
}
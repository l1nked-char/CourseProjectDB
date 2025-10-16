package app.subd.owner_panels;

import app.subd.components.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class OwnerController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Владелец: " + Session.getUsername());
        // Загрузка финансовых данных и аналитики
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
    private void showRevenueReports() {
        // Логика отчетов по доходам
        System.out.println("Отчеты по доходам");
    }

    @FXML
    private void showHotelStats() {
        // Логика статистики отеля
        System.out.println("Статистика отеля");
    }
}
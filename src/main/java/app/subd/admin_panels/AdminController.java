package app.subd.admin_panels;

import app.subd.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AdminController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    private Map<String, String> tableConfigs = new HashMap<>();

    @FXML
    public void initialize() {
        statusLabel.setText("Администратор: " + Session.getUsername());
        initializeTableConfigs();
    }

    private void initializeTableConfigs() {
        // Конфигурация для каждой таблицы: название -> путь к FXML
        tableConfigs.put("Отели", "/app/subd/tables/hotel_management.fxml");
        tableConfigs.put("Пользователи", "/app/subd/admin_panels/user_management.fxml");
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
    private void openTableTab(String tableName) {
        try {

            for (Tab tab : mainTabPane.getTabs()) {
                if (tableName.equals(tab.getText())) {
                    mainTabPane.getSelectionModel().select(tab);
                    return;
                }
            }

            String fxmlPath = tableConfigs.get(tableName);
            if (fxmlPath == null) {
                statusLabel.setText("Конфигурация для таблицы '" + tableName + "' не найдена");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent tableContent = loader.load();

            Tab tableTab = new Tab(tableName);
            tableTab.setContent(tableContent);
            tableTab.setClosable(true);

            mainTabPane.getTabs().add(tableTab);
            mainTabPane.getSelectionModel().select(tableTab);
            statusLabel.setText("Открыта таблица: " + tableName);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка загрузки таблицы '" + tableName + "'");
        }
    }

    @FXML
    private void showUserManagement() {
        openTableTab("Пользователи");
    }

    @FXML
    private void showHotelManagement() {
        openTableTab("Отели");
    }

    @FXML
    private void showRoomManagement() {
        openTableTab("Номера");
    }

    @FXML
    private void showBookingManagement() {
        openTableTab("Бронирования");
    }

    @FXML
    private void showServiceManagement() {
        openTableTab("Услуги");
    }

    @FXML
    private void showGeneralStats() {
        System.out.println("Общая статистика");
    }
}
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

public class AdminController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Администратор: " + Session.getUsername());
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
    private void showUserManagement() {
        try {
            // Проверяем, не открыта ли уже вкладка
            for (Tab tab : mainTabPane.getTabs()) {
                if ("Управление пользователями".equals(tab.getText())) {
                    mainTabPane.getSelectionModel().select(tab);
                    return;
                }
            }

            // Загружаем форму управления пользователями
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/admin_panels/user_management.fxml"));
            Parent userManagementContent = loader.load();

            Tab userTab = new Tab("Управление пользователями");
            userTab.setContent(userManagementContent);
            userTab.setClosable(true);

            mainTabPane.getTabs().add(userTab);
            mainTabPane.getSelectionModel().select(userTab);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка загрузки формы управления пользователями");
        }
    }

    @FXML
    private void showGeneralStats() {
        System.out.println("Общая статистика");
        // Здесь можно добавить загрузку другой формы
    }
}
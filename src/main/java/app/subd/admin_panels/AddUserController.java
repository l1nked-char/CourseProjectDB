package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.components.Session;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.sql.Connection;


import static app.subd.MessageController.*;

public class AddUserController extends BaseFormUserManagement {

    private UserManagementController parentController;

    @FXML
    public void initialize() {
        super.initialize();
    }

    public void setParentController(UserManagementController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleCreateUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();
        String hotelInfo = hotelComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null || hotelInfo == null || hotelInfo.isEmpty()) {
            showError(statusLabel, "Заполните все поля");
            return;
        }

        if (!hotelIdMap.containsKey(hotelInfo)) {
            showError(statusLabel, "Выберите корректный отель из списка");
            return;
        }

        try {
            int hotelId = hotelIdMap.get(hotelInfo);
            Connection connection = Session.getConnection();

            Database_functions.callFunction(connection, "create_user_with_role", username, password, role, hotelId);

            showSuccess(statusLabel, "Пользователь " + username + " успешно создан!");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            clearForm();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка создания пользователя: " + e.getMessage());
        }
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        hotelComboBox.getSelectionModel().clearSelection();
        roleComboBox.getSelectionModel().clearSelection();
        button.setDisable(true);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
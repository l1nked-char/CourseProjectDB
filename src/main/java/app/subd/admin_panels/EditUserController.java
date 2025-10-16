package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditUserController extends BaseFormUserManagement {

    @FXML private Label titleLabel;

    private User user;
    private UserManagementController parentController;

    @FXML
    public void initialize() {
        super.initialize();
    }

    public void setUser(User user) {
        this.user = user;
        populateForm();
    }

    public void setParentController(UserManagementController parentController) {
        this.parentController = parentController;
    }

    private void populateForm() {
        if (user != null) {
            titleLabel.setText("Редактирование пользователя: " + user.getUsername());
            usernameField.setText(user.getUsername());
            roleComboBox.setValue(user.getRole());
            hotelComboBox.setValue(user.getHotelInfo());
        }
    }

    @FXML
    private void handleSaveUser() {
        if (user == null) {
            showError(statusLabel, "Ошибка: пользователь не выбран");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String hotelInfo = hotelComboBox.getValue();
        String role = roleComboBox.getValue();

        try {
            Connection connection = Session.getConnection();

            int hotelId = hotelIdMap.get(hotelInfo);

            if (!password.isEmpty() || !password.equals(confirmPassword)) {
                showError(statusLabel, "Пароли не совпадают");
                return;
            } else
                updatePassword(user.getUsername(), password);

            if (!role.equals(user.getRole()))
                Database_functions.callFunction(connection, "change_user_role", user.getUsername(), user.getRole(), role);

            if (!hotelInfo.equals(user.getHotelInfo()))
                Database_functions.callFunction(connection, "change_hotelId", user.getUsername(), hotelId);

            if (!username.equals(user.getUsername()))
                Database_functions.callFunction(connection, "change_username", user.getUsername(), username);

            if (parentController != null)
                parentController.handleRefresh();

            showSuccess(statusLabel, "Данные пользователя успешно обновлены");

        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления: " + e.getMessage());
        }
    }

    private void updatePassword(String username, String newPassword) {
        try {
            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "change_user_password", username, newPassword);

            passwordField.clear();
            confirmPasswordField.clear();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления пароля: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditUserController {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField passwordRepeated;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;

    private User user;
    private UserManagementController parentController;

    @FXML
    public void initialize() {
        setupRoleComboBox();
    }

    public void setUser(User user) {
        this.user = user;
        populateForm();
    }

    public void setParentController(UserManagementController parentController) {
        this.parentController = parentController;
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList(
                "owner_role", "employee_role"
        ));
    }

    private void populateForm() {
        if (user != null) {
            titleLabel.setText("Редактирование пользователя: " + user.getUsername());
            usernameField.setText(user.getUsername());
            roleComboBox.setValue(user.getRole());
        }
    }

    @FXML
    private void handleSaveUser() {
        if (user == null) {
            showError(statusLabel, "Ошибка: пользователь не выбран");
            return;
        }

        String username = usernameField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String repeatedPassword = passwordRepeated.getText().trim();
        String role = roleComboBox.getValue();

        // Проверка изменений
        if (username.equals(user.getUsername()) && role.equals(user.getRole()) && newPassword.isEmpty()) {
            showError(statusLabel, "Сначала внесите изменения!");
            return;
        }

        try {
            Connection connection = Session.getConnection();

            // Проверка пароля
            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(repeatedPassword)) {
                    showError(statusLabel, "Пароли не совпадают");
                    return;
                }
                updatePassword(user.getUsername(), newPassword);
            }

            // Изменение роли
            if (!role.equals(user.getRole())) {
                Database_functions.callFunction(connection, "change_user_role", user.getUsername(), user.getRole(), role);
            }

            // Изменение логина
            if (!username.equals(user.getUsername())) {
                Database_functions.callFunction(connection, "change_username", user.getUsername(), username);
            }

            // Обновляем родительский контроллер и закрываем окно
            if (parentController != null) {
                parentController.handleRefresh();
            }

            showSuccess(statusLabel, "Данные пользователя успешно обновлены");

            // Закрываем окно через несколько секунд
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                Stage stage = (Stage) saveButton.getScene().getWindow();
                                stage.close();
                            });
                        }
                    },
                    1500
            );

        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления: " + e.getMessage());
        }
    }

    private void updatePassword(String username, String newPassword) {
        try {
            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "change_user_password", username, newPassword);
            // Очищаем поля паролей после успешного обновления
            newPasswordField.clear();
            passwordRepeated.clear();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления пароля: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
package app.subd;

import app.subd.components.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private javafx.scene.control.Button loginButton;

    @FXML
    public void initialize() {
        // Обработка нажатия Enter
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {

            Connection connection = Database_functions.ConnectToDatabase(username, password);

            if (connection == null)
                return;
            String role = getUserRole(connection, username);
            
            if (role != null) {
                Session.setUser(username, role, connection);

                loadRoleForm(role);
            } else {
                errorLabel.setText("Ошибка получения данных пользователя");
                connection.close();
            }

        } catch (Exception e) {
            errorLabel.setText("Неверный логин или пароль");
        }
    }

    private String getUserRole(Connection connection, String username) {
        try {
            ResultSet rs = Database_functions.callFunction(connection, "get_user_role");
            
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadRoleForm(String role) {
        try {
            String fxmlFile = "";
            String title = switch (role) {
                case "admin_role" -> {
                    fxmlFile = "/app/subd/admin_panels/admin.fxml";
                    yield "Панель администратора";
                }
                case "owner_role" -> {
                    fxmlFile = "/app/subd/owner_panels/owner.fxml";
                    yield "Панель владельца";
                }
                case "employee_role" -> {
                    fxmlFile = "/app/subd/employee_panels/employee.fxml";
                    yield "Панель сотрудника";
                }
                default -> "";
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.setScene(new Scene(root, 1000, 700));
            stage.setOnCloseRequest(e -> Session.clear());
            stage.show();

            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
            
        } catch (Exception e) {
            errorLabel.setText("Ошибка загрузки интерфейса");
            e.printStackTrace();
        }
    }
}
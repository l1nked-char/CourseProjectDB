package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;

import static app.subd.MessageController.*;

public class UserManagementController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private Label statusLabel;

    private final ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        usersTable.setItems(usersList);
    }

    private void loadUsers() {
        try {
            usersList.clear();
            Connection connection = Session.getConnection();

            ResultSet rs = Database_functions.callFunction(connection, "get_all_users");

            while (rs.next()) {
                usersList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role_name")
                ));
            }

            showSuccess(statusLabel, "Загружено пользователей: " + usersList.size());

        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки пользователей: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError(statusLabel, "Выберите пользователя для редактирования");
            return;
        }

        try {
            // Открываем форму редактирования пользователя
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/admin_panels/edit_user.fxml"));
            Parent root = loader.load();

            // Передаем выбранного пользователя в контроллер редактирования
            EditUserController controller = loader.getController();
            controller.setUser(selectedUser);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Редактирование пользователя: " + selectedUser.getUsername());
            stage.setMinWidth(400);
            stage.setMinHeight(550);
            stage.setScene(new Scene(root, 400, 550));
            stage.show();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/admin_panels/add_user.fxml"));
            Parent root = loader.load();

            // Передаем ссылку на этот контроллер для обновления списка
            AddUserController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Добавление пользователя");
            stage.setMinWidth(400);
            stage.setMinHeight(550);
            stage.setScene(new Scene(root, 400, 550));
            stage.show();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh() {
        loadUsers();
        showSuccess(statusLabel, "Список обновлен");
    }
}
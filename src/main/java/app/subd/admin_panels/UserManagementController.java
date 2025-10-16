package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;

import static app.subd.MessageController.*;

public class UserManagementController extends BaseFormUserManagement {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> hotelColumn;
    @FXML private TableColumn<User, Boolean> userLockedColumn;
    @FXML private Label statusLabel;

    private final ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        hotelComboBox = new ComboBox<>();
        super.setupHotelComboBox();
        super.loadHotels();
        loadUsers();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        hotelColumn.setCellValueFactory(new PropertyValueFactory<>("hotelInfo"));
        userLockedColumn.setCellValueFactory(new PropertyValueFactory<>("userLocked"));
        usersTable.setItems(usersList);
    }

    private void loadUsers() {
        try {
            usersList.clear();
            Connection connection = Session.getConnection();

            ResultSet rs = Database_functions.callFunction(connection, "get_all_users");

            while (rs.next()) {
                int id = rs.getInt("hotel_id");
                String hotelInfo = hotelInfoMap.get(id);

                usersList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role_name"),
                        hotelInfo,
                        rs.getBoolean("user_locked")
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/admin_panels/edit_user.fxml"));
            Parent root = loader.load();

            EditUserController controller = loader.getController();
            controller.setUser(selectedUser);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Редактирование пользователя: " + selectedUser.getUsername());
            stage.setMinWidth(400);
            stage.setMinHeight(550);
            stage.setScene(new Scene(root, 400, 550));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования: " + e.getMessage());
        }
    }

    @FXML
    private void handleActivationUser()
    {
        Connection connection = Session.getConnection();
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError(statusLabel, "Выберите пользователя из таблицы ниже");
            return;
        }

        try {
            String func_name = !selectedUser.getUserLocked() ? "ban_user" : "unban_user";
            Database_functions.callFunction(connection, func_name, selectedUser.getUsername());

            handleRefresh();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка деактивации пользователя: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/admin_panels/add_user.fxml"));
            Parent root = loader.load();

            AddUserController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Добавление пользователя");
            stage.setMinWidth(400);
            stage.setMinHeight(550);
            stage.setScene(new Scene(root, 400, 550));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы!");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRefresh() {
        loadUsers();
        showSuccess(statusLabel, "Список обновлен");
    }
}
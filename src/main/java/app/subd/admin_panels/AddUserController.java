package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class AddUserController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> hotelComboBox;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;
    @FXML private Button createButton;

    private final Map<String, Integer> hotelIdMap = new HashMap<>();
    private UserManagementController parentController;

    @FXML
    public void initialize() {
        setupRoleComboBox();
        setupHotelComboBox();
        loadHotels();

        // Добавляем обработчики для实时验证
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        hotelComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    public void setParentController(UserManagementController parentController) {
        this.parentController = parentController;
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList(
                "admin_role", "owner_role", "employee_role"
        ));
    }

    private void setupHotelComboBox() {
        hotelComboBox.setEditable(true);

        hotelComboBox.getEditor().setOnKeyPressed(event -> {
            // Обрабатываем пробел - предотвращаем выбор элемента из списка
            switch (event.getCode()) {
                case SPACE:
                    // Поглощаем событие пробела, чтобы он не вызывал выбор элемента
                    event.consume();
                    // Вставляем пробел в текстовое поле
                    hotelComboBox.getEditor().insertText(
                            hotelComboBox.getEditor().getCaretPosition(), " ");
                    break;
                case ENTER:
                    // При нажатии Enter просто скрываем выпадающий список
                    if (hotelComboBox.isShowing()) {
                        hotelComboBox.hide();
                    }
                    event.consume();
                    break;
                default:
                    // Для остальных клавиш не делаем ничего специального
                    break;
            }
        });

        // Добавляем обработчик для ручного поиска
        hotelComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            // Игнорируем изменения, вызванные пробелом
            if (newValue != null && !newValue.equals(oldValue)) {
                String searchText = newValue.toLowerCase();
                if (!searchText.isEmpty() && !searchText.trim().isEmpty()) {
                    hotelComboBox.getItems().clear();
                    for (String hotel : hotelIdMap.keySet()) {
                        if (hotel.toLowerCase().contains(searchText)) {
                            hotelComboBox.getItems().add(hotel);
                        }
                    }
                    hotelComboBox.setVisibleRowCount(7);
                    if (!hotelComboBox.getItems().isEmpty() && !hotelComboBox.isShowing()) {
                        hotelComboBox.show();
                    }
                } else {
                    hotelComboBox.setItems(FXCollections.observableArrayList(hotelIdMap.keySet()));
                    hotelComboBox.setVisibleRowCount(7);
                }
            }
        });
    }

    private void loadHotels() {
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_hotels");

            hotelIdMap.clear();

            while (rs.next()) {
                int hotelId = rs.getInt("hotel_id");
                String hotel_city = rs.getString("hotel_city");
                String address = rs.getString("hotel_address");
                String hotelInfo = String.format("%s, %s", hotel_city, address);

                hotelIdMap.put(hotelInfo, hotelId);
            }

            hotelComboBox.setItems(FXCollections.observableArrayList(hotelIdMap.keySet()));
            showSuccess(statusLabel, "Загружено отелей: " + hotelIdMap.size());

        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка отелей: " + e.getMessage());
        }
    }

    private void validateForm() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String hotelInfo = hotelComboBox.getValue();
        String role = roleComboBox.getValue();

        boolean isValid = !username.isEmpty() &&
                !password.isEmpty() &&
                !confirmPassword.isEmpty() &&
                hotelInfo != null &&
                !hotelInfo.isEmpty() &&
                role != null &&
                password.equals(confirmPassword) &&
                hotelIdMap.containsKey(hotelInfo);

        createButton.setDisable(!isValid);

        if (!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            showError(statusLabel, "Пароли не совпадают");
        } else if (hotelInfo != null && !hotelInfo.isEmpty() && !hotelIdMap.containsKey(hotelInfo)) {
            showError(statusLabel, "Выберите отель из списка");
        } else if (isValid) {
            clearStatus(statusLabel);
        }
    }

    @FXML
    private void handleCreateUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
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

            // Вызываем функцию создания пользователя
            Database_functions.callFunction(connection, "create_user_with_role", username, password, role, hotelId);

            // Показываем сообщение об успехе
            showSuccess(statusLabel, "Пользователь " + username + " успешно создан с наследованием прав роли " + role);

            // Обновляем родительский контроллер
            if (parentController != null) {
                parentController.handleRefresh();
            }

            // Очищаем форму
            clearForm();

            // Закрываем окно через несколько секунд
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                Stage stage = (Stage) createButton.getScene().getWindow();
                                stage.close();
                            });
                        }
                    },
                    1500
            );

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
        createButton.setDisable(true);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
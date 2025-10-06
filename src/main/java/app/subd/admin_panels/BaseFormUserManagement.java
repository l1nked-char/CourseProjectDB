package app.subd.admin_panels;

import app.subd.ComboBoxSearchListener;
import app.subd.Database_functions;
import app.subd.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;
import static app.subd.MessageController.clearStatus;

public class BaseFormUserManagement {

    @FXML protected ComboBox<String> hotelComboBox;
    @FXML protected ComboBox<String> roleComboBox;
    @FXML protected Label statusLabel;
    @FXML protected TextField usernameField;
    @FXML protected PasswordField passwordField;
    @FXML protected PasswordField confirmPasswordField;

    @FXML protected Button button;

    protected final Map<String, Integer> hotelIdMap = new HashMap<>();
    protected final Map<Integer, String> hotelInfoMap = new HashMap<>();

    protected void loadHotels() {
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
                hotelInfoMap.put(hotelId, hotelInfo);
            }

            hotelComboBox.setItems(FXCollections.observableArrayList(hotelIdMap.keySet()));
            showSuccess(statusLabel, "Загружено отелей: " + hotelIdMap.size());

        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка отелей: " + e.getMessage());
        }
    }

    protected void validateForm() {
        clearStatus(statusLabel);

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

        button.setDisable(!isValid);

        if (!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            showError(statusLabel, "Пароли не совпадают");
        } else if (hotelInfo == null || hotelInfo.isEmpty() || !hotelIdMap.containsKey(hotelInfo)) {
            showError(statusLabel, "Выберите отель из списка");
        } else if (role == null || role.isEmpty()) {
            showError(statusLabel, "Выберите роль из списка");
        } else if (isValid) {
            clearStatus(statusLabel);
        }
    }

    @FXML
    public void initialize() {
        setupRoleComboBox();
        loadHotels();
        setupHotelComboBox();

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        hotelComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList(
                "owner_role", "employee_role"
        ));
    }

    protected void setupHotelComboBox()
    {
        new ComboBoxSearchListener(hotelComboBox);
    }
}

package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public abstract class BaseHotelFormController {

    @FXML protected TextField addressField;
    @FXML protected ComboBox<String> cityComboBox;
    @FXML protected Label statusLabel;
    
    protected final Map<String, Integer> cityIdMap = new HashMap<>();
    protected final Map<Integer, String> cityNameMap = new HashMap<>();

    protected void loadCities() {
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_cities");

            cityIdMap.clear();

            while (rs.next()) {
                int cityId = rs.getInt("city_id");
                String cityName = rs.getString("city_name");
                
                cityIdMap.put(cityName, cityId);
                cityNameMap.put(cityId, cityName);
            }

            cityComboBox.getItems().setAll(cityIdMap.keySet());
            
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка городов: " + e.getMessage());
        }
    }

    protected void validateForm() {
        String address = addressField.getText().trim();
        String city = cityComboBox.getValue();

        boolean isValid = !address.isEmpty() && 
                         city != null && 
                         !city.isEmpty();

        // Активируем кнопку сохранения если форма валидна
        getSaveButton().setDisable(!isValid);

        if (address.isEmpty()) {
            showError(statusLabel, "Введите адрес отеля");
        } else if (city == null || city.isEmpty()) {
            showError(statusLabel, "Выберите город из списка");
        } else if (isValid) {
            clearStatus(statusLabel);
        }
    }

    protected abstract javafx.scene.control.Button getSaveButton();

    @FXML
    public void initialize() {
        loadCities();
        setupEventListeners();
    }

    private void setupEventListeners() {
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        cityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }
}
package app.subd.tables;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.admin_panels.AdminController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class AddHotelController {

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML protected TextField addressField;
    @FXML protected ComboBox<String> cityComboBox;
    @FXML protected Label statusLabel;

    private AdminController.RefreshableController parentController;

    public void setParentController(AdminController.RefreshableController parentController) {
        this.parentController = parentController;
    }

    public void initialize() {
        try {
            AllDictionaries.initialiseCitiesMaps();

            cityComboBox.getItems().setAll(AllDictionaries.getCitiesIdMap().keySet());

        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка городов: " + e.getMessage());
        }
        setupEventListeners();
    }

    protected Button getSaveButton() {
        return saveButton;
    }

    private void setupEventListeners() {
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        cityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    protected void validateForm() {
        String address = addressField.getText().trim();
        String city = cityComboBox.getValue();

        boolean isValid = !address.isEmpty() &&
                city != null &&
                !city.isEmpty();

        getSaveButton().setDisable(!isValid);

        if (address.isEmpty())
            showError(statusLabel, "Введите адрес отеля");
        else if (city == null || city.isEmpty())
            showError(statusLabel, "Выберите город из списка");
        else
            clearStatus(statusLabel);
    }

    @FXML
    private void handleSave() {
        String address = addressField.getText().trim();
        String cityName = cityComboBox.getValue();

        if (address.isEmpty() || cityName == null) {
            showError(statusLabel, "Заполните все поля");
            return;
        }

        try {
            int cityId = AllDictionaries.getCitiesIdMap().get(cityName);
            Connection connection = Session.getConnection();

            Database_functions.callFunction(connection, "add_new_hotel", cityId, address);
            clearForm();
            showSuccess(statusLabel, "Отель успешно добавлен");

            parentController.handleRefresh();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка добавления отеля: " + e.getMessage());
        }
    }

    private void clearForm() {
        addressField.clear();
        cityComboBox.getSelectionModel().clearSelection();
        saveButton.setDisable(true);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
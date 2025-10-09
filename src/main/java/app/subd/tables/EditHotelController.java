package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.Hotel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditHotelController {

    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML protected TextField addressField;
    @FXML protected ComboBox<String> cityComboBox;
    @FXML protected Label statusLabel;

    private Hotel hotel;
    private HotelManagementController parentController;

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        populateForm();
    }

    private void setupEventListeners() {
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        cityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    public void setParentController(HotelManagementController parentController) {
        this.parentController = parentController;
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

    protected Button getSaveButton() {
        return saveButton;
    }

    private void populateForm() {
        if (hotel != null) {
            titleLabel.setText("Редактирование отеля: ID " + hotel.getId());
            addressField.setText(hotel.getAddress());

            cityComboBox.setValue(AllDictionaries.getCitiesNameMap().get(hotel.getCityId()));
        }
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

    @FXML
    private void handleSave() {
        String address = addressField.getText().trim();
        String cityName = cityComboBox.getValue();

        try {
            int cityId = AllDictionaries.getCitiesIdMap().get(cityName);
            Connection connection = Session.getConnection();

            Database_functions.callFunction(connection, "edit_hotel", hotel.getId(), cityId, address);

            showSuccess(statusLabel, "Данные отеля успешно обновлены");

            if (parentController != null) {
                parentController.handleRefresh();
            }

        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления отеля: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
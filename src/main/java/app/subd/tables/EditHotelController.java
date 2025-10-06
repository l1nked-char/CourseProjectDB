package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.Hotel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditHotelController extends BaseHotelFormController {

    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Hotel hotel;
    private HotelManagementController parentController;

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        populateForm();
    }

    public void setParentController(HotelManagementController parentController) {
        this.parentController = parentController;
    }

    @Override
    protected Button getSaveButton() {
        return saveButton;
    }

    private void populateForm() {
        if (hotel != null) {
            titleLabel.setText("Редактирование отеля: ID " + hotel.getId());
            addressField.setText(hotel.getAddress());

            cityComboBox.setValue(cityNameMap.get(hotel.getCityId()));
        }
    }

    @FXML
    private void handleSave() {
        if (hotel == null) {
            showError(statusLabel, "Ошибка: отель не выбран");
            return;
        }

        String address = addressField.getText().trim();
        String cityName = cityComboBox.getValue();

        if (address.isEmpty() || cityName == null) {
            showError(statusLabel, "Заполните все поля");
            return;
        }

        try {
            int cityId = cityIdMap.get(cityName);
            Connection connection = Session.getConnection();

            // Обновляем данные отеля
            Database_functions.callFunction(connection, "update_hotel", hotel.getId(), cityId, address);

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
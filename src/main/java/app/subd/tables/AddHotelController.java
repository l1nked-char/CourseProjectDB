package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class AddHotelController extends BaseHotelFormController {

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private HotelManagementController parentController;

    public void setParentController(HotelManagementController parentController) {
        this.parentController = parentController;
    }

    @Override
    protected Button getSaveButton() {
        return saveButton;
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
            int cityId = cityIdMap.get(cityName);
            Connection connection = Session.getConnection();

            Database_functions.callFunction(connection, "add_new_hotel", cityId, address);

            showSuccess(statusLabel, "Отель успешно добавлен");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            clearForm();

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
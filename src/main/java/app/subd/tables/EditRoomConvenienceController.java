package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.RoomConvenience;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;

import static app.subd.MessageController.*;

public class EditRoomConvenienceController {

    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label convenienceLabel;
    @FXML private TextField priceField;
    @FXML private TextField amountField;
    @FXML private DatePicker startDatePicker;
    @FXML private Label statusLabel;

    private RoomConvenience roomConvenience;
    private RoomConvenienceManagementController parentController;

    public void setRoomConvenience(RoomConvenience roomConvenience) {
        this.roomConvenience = roomConvenience;
        populateForm();
    }

    public void setParentController(RoomConvenienceManagementController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        setupEventListeners();
    }

    private void setupEventListeners() {
        priceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        amountField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void validateForm() {
        boolean isValid = !priceField.getText().trim().isEmpty() &&
                !amountField.getText().trim().isEmpty() &&
                startDatePicker.getValue() != null;

        saveButton.setDisable(!isValid);

        if (!isValid) {
            showError(statusLabel, "Заполните все поля");
        } else {
            clearStatus(statusLabel);
        }
    }

    private void populateForm() {
        if (roomConvenience != null) {
            titleLabel.setText("Редактирование удобства");
            convenienceLabel.setText(roomConvenience.getConvName());
            priceField.setText(String.valueOf(roomConvenience.getPricePerOne()));
            amountField.setText(String.valueOf(roomConvenience.getAmount()));
            startDatePicker.setValue(roomConvenience.getStartDate());
        }
    }

    @FXML
    private void handleSave() {
        if (roomConvenience == null) {
            showError(statusLabel, "Ошибка: удобство не выбрано");
            return;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            int amount = Integer.parseInt(amountField.getText().trim());
            LocalDate startDate = startDatePicker.getValue();

            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "update_room_convenience",
                    roomConvenience.getRoomId(), roomConvenience.getConvNameId(), price, amount, startDate);

            showSuccess(statusLabel, "Удобство успешно обновлено");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            closeForm();

        } catch (NumberFormatException e) {
            showError(statusLabel, "Проверьте правильность числовых полей");
        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления удобства: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
package app.subd.tables;

import app.subd.Database_functions;
import app.subd.components.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;

import static app.subd.MessageController.*;

public class AddRoomConvenienceController {

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ComboBox<String> convenienceComboBox;
    @FXML private TextField priceField;
    @FXML private TextField amountField;
    @FXML private DatePicker startDatePicker;
    @FXML private Label statusLabel;

    private RoomConvenienceManagementController parentController;
    private int roomId;

    public void setParentController(RoomConvenienceManagementController parentController) {
        this.parentController = parentController;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @FXML
    public void initialize() {
        try {
            AllDictionaries.initialiseConveniencesMaps();
            convenienceComboBox.getItems().setAll(AllDictionaries.getConveniencesIdMap().keySet());
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка удобств: " + e.getMessage());
        }
        setupEventListeners();
        startDatePicker.setValue(LocalDate.now());
    }

    private void setupEventListeners() {
        convenienceComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        priceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        amountField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void validateForm() {
        boolean isValid = convenienceComboBox.getValue() != null &&
                !priceField.getText().trim().isEmpty() &&
                !amountField.getText().trim().isEmpty() &&
                startDatePicker.getValue() != null;

        saveButton.setDisable(!isValid);

        if (!isValid) {
            showError(statusLabel, "Заполните все поля");
        } else {
            clearStatus(statusLabel);
        }
    }

    @FXML
    private void handleSave() {
        try {
            int convNameId = AllDictionaries.getConveniencesIdMap().get(convenienceComboBox.getValue());
            double price = Double.parseDouble(priceField.getText().trim());
            int amount = Integer.parseInt(amountField.getText().trim());
            LocalDate startDate = startDatePicker.getValue();

            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "add_room_convenience",
                    roomId, convNameId, price, amount, startDate);

            showSuccess(statusLabel, "Удобство успешно добавлено");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            closeForm();

        } catch (NumberFormatException e) {
            showError(statusLabel, "Проверьте правильность числовых полей");
        } catch (Exception e) {
            showError(statusLabel, "Ошибка добавления удобства: " + e.getMessage());
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
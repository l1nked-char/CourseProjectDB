package app.subd.tables;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.admin_panels.AdminController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class AddRoomController {

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField roomNumberField;
    @FXML private TextField maxPeopleField;
    @FXML private TextField pricePerPersonField;
    @FXML private ComboBox<String> typeOfRoomComboBox;
    @FXML private Label statusLabel;
    @FXML private Label hotelLabel;

    private AdminController.RefreshableController parentController;
    private int hotelId;

    public void setParentController(AdminController.RefreshableController parentController) {
        this.parentController = parentController;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
        try {
            String hotelInfo = AllDictionaries.getHotelsNameMap().get(hotelId);
            hotelLabel.setText("Отель: " + hotelInfo);
        } catch (Exception e) {
            hotelLabel.setText("Отель: ID " + hotelId);
        }
    }

    @FXML
    public void initialize() {
        try {
            AllDictionaries.initialiseTypesOfRoomMaps();
            typeOfRoomComboBox.getItems().setAll(AllDictionaries.getTypesOfRoomIdMap().keySet());
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка типов комнат: " + e.getMessage());
        }
        setupEventListeners();
    }

    private void setupEventListeners() {
        roomNumberField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        maxPeopleField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        pricePerPersonField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        typeOfRoomComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void validateForm() {
        boolean isValid = !roomNumberField.getText().trim().isEmpty() &&
                !maxPeopleField.getText().trim().isEmpty() &&
                !pricePerPersonField.getText().trim().isEmpty() &&
                typeOfRoomComboBox.getValue() != null;

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
            int roomNumber = Integer.parseInt(roomNumberField.getText().trim());
            int maxPeople = Integer.parseInt(maxPeopleField.getText().trim());
            double pricePerPerson = Double.parseDouble(pricePerPersonField.getText().trim());
            int typeOfRoomId = AllDictionaries.getTypesOfRoomIdMap().get(typeOfRoomComboBox.getValue());

            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "add_room",
                    hotelId, maxPeople, pricePerPerson, roomNumber, typeOfRoomId);

            showSuccess(statusLabel, "Комната успешно добавлена");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            closeForm();

        } catch (NumberFormatException e) {
            showError(statusLabel, "Проверьте правильность числовых полей");
        } catch (Exception e) {
            showError(statusLabel, "Ошибка добавления комнаты: " + e.getMessage());
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
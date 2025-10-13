package app.subd.tables;

import app.subd.ComboBoxSearchListener;
import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.Room;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditRoomController {

    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField roomNumberField;
    @FXML private TextField maxPeopleField;
    @FXML private TextField pricePerPersonField;
    @FXML private ComboBox<String> typeOfRoomComboBox;
    @FXML private Label statusLabel;

    private Room room;
    private HotelRoomManagementController parentController;

    public void setRoom(Room room) {
        this.room = room;
        populateForm();
    }

    public void setParentController(HotelRoomManagementController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        try {
            AllDictionaries.initialiseTypesOfRoomMaps();
            typeOfRoomComboBox.getItems().setAll(AllDictionaries.getTypesOfRoomIdMap().keySet());
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка типов комнат: " + e.getMessage());
        }
        new ComboBoxSearchListener(typeOfRoomComboBox);
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

    private void populateForm() {
        if (room != null) {
            titleLabel.setText("Редактирование комнаты: ID " + room.getId());
            roomNumberField.setText(String.valueOf(room.getRoomNumber()));
            maxPeopleField.setText(String.valueOf(room.getMaxPeople()));
            pricePerPersonField.setText(String.valueOf(room.getPricePerPerson()));
            
            if (room.getTypeOfRoomId() != null) {
                typeOfRoomComboBox.setValue(AllDictionaries.getTypesOfRoomNameMap().get(room.getTypeOfRoomId()));
            } else {
                typeOfRoomComboBox.setValue(room.getTypeOfRoomName());
            }
        }
    }

    @FXML
    private void handleSave() {
        if (room == null) {
            showError(statusLabel, "Ошибка: комната не выбрана");
            return;
        }

        try {
            int roomNumber = Integer.parseInt(roomNumberField.getText().trim());
            int maxPeople = Integer.parseInt(maxPeopleField.getText().trim());
            double pricePerPerson = Double.parseDouble(pricePerPersonField.getText().trim());
            int typeOfRoomId = AllDictionaries.getTypesOfRoomIdMap().get(typeOfRoomComboBox.getValue());

            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "edit_room",
                room.getId(), room.getHotelId(), maxPeople, pricePerPerson, roomNumber, typeOfRoomId);

            showSuccess(statusLabel, "Комната успешно обновлена");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            closeForm();

        } catch (NumberFormatException e) {
            showError(statusLabel, "Проверьте правильность числовых полей");
        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления комнаты: " + e.getMessage());
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
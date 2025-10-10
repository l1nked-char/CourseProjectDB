package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.TypeOfRoom;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditTypeOfRoomController {

    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField nameField;
    @FXML private Label statusLabel;

    private TypeOfRoom typeOfRoom;
    private TypeOfRoomManagementController parentController;

    public void setTypeOfRoom(TypeOfRoom typeOfRoom) {
        this.typeOfRoom = typeOfRoom;
        populateForm();
    }

    public void setParentController(TypeOfRoomManagementController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        setupEventListeners();
    }

    private void setupEventListeners() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    private void validateForm() {
        String name = nameField.getText().trim();
        boolean isValid = !name.isEmpty();

        saveButton.setDisable(!isValid);

        if (name.isEmpty()) {
            showError(statusLabel, "Введите название типа комнаты");
        } else {
            clearStatus(statusLabel);
        }
    }

    private void populateForm() {
        if (typeOfRoom != null) {
            titleLabel.setText("Редактирование типа комнаты: ID " + typeOfRoom.getId());
            nameField.setText(typeOfRoom.getName());
        }
    }

    @FXML
    private void handleSave() {
        if (typeOfRoom == null) {
            showError(statusLabel, "Ошибка: тип комнаты не выбран");
            return;
        }

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showError(statusLabel, "Заполните поле");
            return;
        }

        try {
            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "edit_type_of_room", typeOfRoom.getId(), name);

            showSuccess(statusLabel, "Тип комнаты успешно обновлен");

            if (parentController != null) {
                parentController.handleRefresh();
            }

        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления типа комнаты: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
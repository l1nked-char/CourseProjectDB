package app.subd.tables;

import app.subd.Database_functions;
import app.subd.components.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class AddTypeOfRoomController {

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField nameField;
    @FXML private Label statusLabel;

    private TypeOfRoomManagementController parentController;

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

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showError(statusLabel, "Заполните поле");
            return;
        }

        try {
            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "add_type_of_room", name);

            if (parentController != null) {
                parentController.handleRefresh();
            }

            clearForm();

            showSuccess(statusLabel, "Тип комнаты успешно добавлен");

        } catch (Exception e) {
            showError(statusLabel, "Ошибка добавления типа комнаты: " + e.getMessage());
        }
    }

    private void clearForm() {
        nameField.clear();
        saveButton.setDisable(true);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
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

public class AddConvenienceController {

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField nameField;
    @FXML private Label statusLabel;

    private ConvenienceManagementController parentController;

    public void setParentController(ConvenienceManagementController parentController) {
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
            showError(statusLabel, "Введите название удобства");
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
            Database_functions.callFunction(connection, "add_new_convenience", name);
            
            showSuccess(statusLabel, "Удобство успешно добавлено");
            
            if (parentController != null) {
                parentController.handleRefresh();
            }

            closeForm();

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
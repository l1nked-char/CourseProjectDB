package app.subd.tables;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.models.Convenience;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

import static app.subd.MessageController.*;

public class EditConvenienceController {

    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField nameField;
    @FXML private Label statusLabel;

    private Convenience convenience;
    private ConvenienceManagementController parentController;

    public void setConvenience(Convenience convenience) {
        this.convenience = convenience;
        populateForm();
    }

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

    private void populateForm() {
        if (convenience != null) {
            titleLabel.setText("Редактирование удобства: ID " + convenience.getId());
            nameField.setText(convenience.getName());
        }
    }

    @FXML
    private void handleSave() {
        if (convenience == null) {
            showError(statusLabel, "Ошибка: удобство не выбрано");
            return;
        }

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showError(statusLabel, "Заполните поле");
            return;
        }

        try {
            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "edit_convenience", convenience.getId(), name);

            showSuccess(statusLabel, "Удобство успешно обновлено");

            if (parentController != null) {
                parentController.handleRefresh();
            }

            closeForm();

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
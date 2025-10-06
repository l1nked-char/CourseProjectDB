package app.subd.tables;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

import static app.subd.MessageController.*;

public abstract class BaseTableController<T> {

    @FXML protected TableView<T> tableView;
    @FXML protected Label statusLabel;
    @FXML protected Button refreshButton;
    @FXML protected Button addButton;
    @FXML protected Button editButton;
    @FXML protected Button deleteButton;

    protected abstract ObservableList<T> loadData() throws Exception;
    protected abstract void setupTableColumns();
    protected abstract void handleAdd();
    protected abstract void handleEdit();
    protected abstract void handleDelete();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupButtonActions();
        handleRefresh();
    }

    protected void setupButtonActions() {
        if (addButton != null) {
            addButton.setOnAction(e -> handleAdd());
        }
        if (editButton != null) {
            editButton.setOnAction(e -> handleEdit());
        }
        if (deleteButton != null) {
            deleteButton.setOnAction(e -> handleDelete());
        }
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> handleRefresh());
        }
    }

    @FXML
    protected void handleRefresh() {
        try {
            ObservableList<T> data = loadData();
            tableView.setItems(data);
            showSuccess(statusLabel, "Данные обновлены");
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки данных: " + e.getMessage());
        }
    }

    protected void showForm(String fxmlPath, String title, Object controllerConfig) {
        try {
            // Здесь будет логика открытия форм для добавления/редактирования
            // Аналогично тому как сделано в UserManagementController
        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы: " + e.getMessage());
        }
    }
}
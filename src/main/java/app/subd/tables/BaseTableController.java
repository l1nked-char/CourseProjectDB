package app.subd.tables;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static app.subd.MessageController.*;

public abstract class BaseTableController<T> {

    @FXML protected TableView<T> tableView;
    @FXML protected Label statusLabel;
    @FXML protected Button refreshButton;
    @FXML protected Button addButton;
    @FXML protected Button editButton;

    protected abstract ObservableList<T> loadData() throws Exception;
    protected abstract void setupTableColumns();
    protected abstract void handleAdd();
    protected abstract void handleEdit();

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

    protected Stage showForm(String title, FXMLLoader loader) throws Exception
    {
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        stage.setScene(new Scene(root, 400, 300));
        stage.initModality(Modality.APPLICATION_MODAL);

        return stage;
    }
}
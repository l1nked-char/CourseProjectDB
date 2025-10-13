package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.Convenience;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;

import static app.subd.MessageController.*;

public class ConvenienceManagementController extends BaseTableController<Convenience> {

    @FXML private TableColumn<Convenience, Integer> idColumn;
    @FXML private TableColumn<Convenience, String> nameColumn;

    @Override
    protected void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @Override
    protected ObservableList<Convenience> loadData() throws Exception {
        ObservableList<Convenience> conveniences = FXCollections.observableArrayList();

        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_conveniences");

        while (rs.next()) {
            conveniences.add(new Convenience(
                rs.getInt("conv_name_id"),
                rs.getString("conv_name")
            ));
        }

        return conveniences;
    }

    @Override
    protected void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_convenience.fxml"));
            Stage stage = showForm("Добавление удобства", loader);
            AddConvenienceController controller = loader.getController();
            controller.setParentController(this);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления удобства: " + e.getMessage());
        }
    }

    @Override
    protected void handleEdit() {
        Convenience selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(statusLabel, "Выберите удобство для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_convenience.fxml"));
            Stage stage = showForm("Редактирование удобства: ID " + selected.getId(), loader);
            EditConvenienceController controller = loader.getController();
            controller.setConvenience(selected);
            controller.setParentController(this);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования удобства: " + e.getMessage());
        }
    }
}
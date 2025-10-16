package app.subd.tables;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.models.TypeOfRoom;
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

public class TypeOfRoomManagementController extends BaseTableController<TypeOfRoom> {

    @FXML private TableColumn<TypeOfRoom, Integer> idColumn;
    @FXML private TableColumn<TypeOfRoom, String> nameColumn;

    @Override
    protected void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @Override
    protected ObservableList<TypeOfRoom> loadData() throws Exception {
        ObservableList<TypeOfRoom> types = FXCollections.observableArrayList();

        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_types_of_room");

        while (rs.next()) {
            types.add(new TypeOfRoom(
                rs.getInt("type_id"),
                rs.getString("type_name")
            ));
        }

        return types;
    }

    @Override
    protected void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_type_of_room.fxml"));
            Stage stage = showForm("Добавление типа комнаты", loader);
            AddTypeOfRoomController controller = loader.getController();
            controller.setParentController(this);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления типа комнаты: " + e.getMessage());
        }
    }

    @Override
    protected void handleEdit() {
        TypeOfRoom selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(statusLabel, "Выберите тип комнаты для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_type_of_room.fxml"));
            Stage stage = showForm("Редактирование типа комнаты: ID " + selected.getId(), loader);
            EditTypeOfRoomController controller = loader.getController();
            controller.setTypeOfRoom(selected);
            controller.setParentController(this);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования типа комнаты: " + e.getMessage());
        }
    }
}
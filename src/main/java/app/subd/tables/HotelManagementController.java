package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.Hotel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;

import static app.subd.MessageController.*;

public class HotelManagementController extends BaseTableController<Hotel> {

    @FXML private TableColumn<Hotel, Integer> idColumn;
    @FXML private TableColumn<Hotel, String> cityNameColumn;
    @FXML private TableColumn<Hotel, String> addressColumn;

    @Override
    protected void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cityNameColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    @Override
    protected ObservableList<Hotel> loadData() throws Exception {
        ObservableList<Hotel> hotels = FXCollections.observableArrayList();

        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_hotels");

        while (rs.next()) {
            hotels.add(new Hotel(
                rs.getInt("hotel_id"),
                rs.getInt("city_id"),
                rs.getString("hotel_address"),
                rs.getString("hotel_city")
            ));
        }

        showSuccess(statusLabel, "Загружено отелей: " + hotels.size());

        
        return hotels;
    }

    @Override
    protected void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_hotel.fxml"));
            Parent root = loader.load();

            AddHotelController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Добавление отеля");
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root, 400, 300));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления отеля: " + e.getMessage());
        }
    }

    @Override
    protected void handleEdit() {
        Hotel selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(statusLabel, "Выберите отель для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_hotel.fxml"));
            Parent root = loader.load();

            EditHotelController controller = loader.getController();
            controller.setHotel(selected);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Редактирование отеля: ID " + selected.getId());
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root, 400, 300));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования отеля: " + e.getMessage());
        }
    }

    @Override
    protected void handleDelete() {
        Hotel selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(statusLabel, "Выберите отель для удаления");
            return;
        }

        try {
            Connection connection = Session.getConnection();
            Database_functions.callFunction(connection, "delete_hotel", selected.getId());

            showSuccess(statusLabel, "Отель успешно удален");
            handleRefresh();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка удаления отеля: " + e.getMessage());
        }
    }
}
package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.RoomConvenience;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class RoomConvenienceManagementController extends BaseTableController<RoomConvenience> {

    @FXML private ComboBox<String> hotelComboBox;
    @FXML private ComboBox<String> roomComboBox;
    @FXML private TableColumn<RoomConvenience, String> convNameColumn;
    @FXML private TableColumn<RoomConvenience, Double> priceColumn;
    @FXML private TableColumn<RoomConvenience, Integer> amountColumn;
    @FXML private TableColumn<RoomConvenience, LocalDate> startDateColumn;

    private Map<String, Integer> roomsIdMap = new HashMap<>();

    @Override
    protected void setupTableColumns() {
        convNameColumn.setCellValueFactory(new PropertyValueFactory<>("convName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerOne"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
    }

    @Override
    protected ObservableList<RoomConvenience> loadData() throws Exception {
        ObservableList<RoomConvenience> conveniences = FXCollections.observableArrayList();

        String selectedRoom = roomComboBox.getValue();
        if (selectedRoom == null) {
            return conveniences;
        }

        int roomId = roomsIdMap.get(selectedRoom);
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_room_conveniences_by_room_id", roomId);

        while (rs.next()) {
            conveniences.add(new RoomConvenience(
                    rs.getInt("room_id"),
                    rs.getInt("conv_name_id"),
                    rs.getDouble("price_per_one"),
                    rs.getInt("amount"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getString("conv_name")
            ));
        }

        return conveniences;
    }

    @Override
    protected void handleAdd() {
        String selectedRoom = roomComboBox.getValue();
        if (selectedRoom == null) {
            showError(statusLabel, "Выберите комнату для добавления удобства");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_room_convenience.fxml"));
            Stage stage = showForm("Добавление удобства", loader);
            AddRoomConvenienceController controller = loader.getController();
            controller.setParentController(this);
            controller.setRoomId(roomsIdMap.get(selectedRoom));
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления удобства: " + e.getMessage());
        }
    }

    @Override
    protected void handleEdit() {
        RoomConvenience selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(statusLabel, "Выберите удобство для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_room_convenience.fxml"));
            Stage stage = showForm("Редактирование удобства", loader);
            EditRoomConvenienceController controller = loader.getController();
            controller.setRoomConvenience(selected);
            controller.setParentController(this);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования удобства: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        super.initialize();
        setupHotelComboBox();
        setupComboBoxListeners();
    }

    private void setupHotelComboBox() {
        try {
            AllDictionaries.initialiseHotelsMaps();
            hotelComboBox.getItems().setAll(AllDictionaries.getHotelsIdMap().keySet());
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка отелей: " + e.getMessage());
        }
    }

    private void setupComboBoxListeners() {
        hotelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    int hotelId = AllDictionaries.getHotelsIdMap().get(newVal);
                    loadRoomsByHotel(hotelId);
                } catch (Exception e) {
                    showError(statusLabel, "Ошибка загрузки комнат: " + e.getMessage());
                }
            } else {
                roomComboBox.getItems().clear();
                tableView.getItems().clear();
            }
        });

        roomComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleRefresh();
            } else {
                tableView.getItems().clear();
            }
        });
    }

    private void loadRoomsByHotel(int hotelId) throws Exception {
        roomsIdMap.clear();
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_rooms_by_hotel", hotelId);

        ObservableList<String> roomDisplayList = FXCollections.observableArrayList();
        while (rs.next()) {
            int roomId = rs.getInt("room_id");
            int roomNumber = rs.getInt("room_number");
            String display = "Номер " + roomNumber;
            roomsIdMap.put(display, roomId);
            roomDisplayList.add(display);
        }
        roomComboBox.getItems().setAll(roomDisplayList);
        roomComboBox.getSelectionModel().clearSelection();
        tableView.getItems().clear();
    }
}
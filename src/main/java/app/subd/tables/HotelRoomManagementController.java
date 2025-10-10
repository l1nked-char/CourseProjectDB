package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;
import app.subd.models.Room;
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

import static app.subd.MessageController.*;

public class HotelRoomManagementController extends BaseTableController<Room> {

    @FXML private ComboBox<String> hotelComboBox;
    @FXML private TableColumn<Room, Integer> idColumn;
    @FXML private TableColumn<Room, String> hotelInfoColumn;
    @FXML private TableColumn<Room, Integer> roomNumberColumn;
    @FXML private TableColumn<Room, Integer> maxPeopleColumn;
    @FXML private TableColumn<Room, Double> pricePerPersonColumn;
    @FXML private TableColumn<Room, String> typeOfRoomColumn;

    @Override
    protected void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        hotelInfoColumn.setCellValueFactory(new PropertyValueFactory<>("hotelInfo"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        maxPeopleColumn.setCellValueFactory(new PropertyValueFactory<>("maxPeople"));
        pricePerPersonColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerPerson"));
        typeOfRoomColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfRoomName"));
    }

    @FXML
    public void initialize() {
        super.initialize();
        setupHotelComboBox();
    }

    private void setupHotelComboBox() {
        try {
            AllDictionaries.initialiseHotelsMaps();
            hotelComboBox.getItems().setAll(AllDictionaries.getHotelsIdMap().keySet());
            hotelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> handleRefresh());

        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки списка отелей: " + e.getMessage());
        }
    }

    @Override
    protected ObservableList<Room> loadData() throws Exception {
        ObservableList<Room> rooms = FXCollections.observableArrayList();

        String selectedHotel = hotelComboBox.getValue();
        if (selectedHotel == null) {
            return rooms;
        }

        int hotelId = AllDictionaries.getHotelsIdMap().get(selectedHotel);
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_rooms_by_hotel", hotelId);

        while (rs.next()) {
            rooms.add(new Room(
                    rs.getInt("room_id"),
                    rs.getInt("hotel_id"),
                    rs.getInt("max_people"),
                    rs.getDouble("price_per_person"),
                    rs.getInt("room_number"),
                    rs.getInt("type_of_room_id"),
                    rs.getString("hotel_info"),
                    rs.getString("room_type_name")
            ));
        }

        return rooms;
    }

    @Override
    protected void handleAdd() {
        String selectedHotel = hotelComboBox.getValue();
        if (selectedHotel == null) {
            showError(statusLabel, "Выберите отель для добавления комнаты");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_room.fxml"));
            Stage stage = showForm("Добавление комнаты", loader);
            AddRoomController controller = loader.getController();
            controller.setParentController(this);
            controller.setHotelId(AllDictionaries.getHotelsIdMap().get(selectedHotel));
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления комнаты: " + e.getMessage());
        }
    }

    @Override
    protected void handleEdit() {
        Room selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError(statusLabel, "Выберите комнату для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_room.fxml"));
            Stage stage = showForm("Редактирование комнаты: ID " + selected.getId(), loader);
            EditRoomController controller = loader.getController();
            controller.setRoom(selected);
            controller.setParentController(this);
            stage.showAndWait();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования комнаты: " + e.getMessage());
        }
    }
}
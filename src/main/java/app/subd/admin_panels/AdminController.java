package app.subd.admin_panels;

import app.subd.components.FormController;
import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.components.UniversalTableController;
import app.subd.config.ConfigFactory;
import app.subd.config.TableConfig;
import app.subd.config.UniversalFormConfig;
import app.subd.components.FormManager;
import app.subd.models.*;
import app.subd.tables.AllDictionaries;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class AdminController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    private final Map<String, TableConfig> tableConfigs = new HashMap<>();

    @FXML
    public void initialize() {
        statusLabel.setText("Администратор: " + Session.getUsername());
        initializeTableConfigs();
    }

    private void initializeTableConfigs() {
        // Используем ConfigFactory для создания конфигураций
        tableConfigs.put("Отели", ConfigFactory.createHotelTableConfig(
                this::loadHotelsData,
                this::handleAddHotel,
                this::handleEditHotel
        ));

        tableConfigs.put("Номера", ConfigFactory.createRoomTableConfig(
                this::loadRoomsData,
                this::handleAddRoom,
                this::handleEditRoom
        ));

        tableConfigs.put("Типы комнат", ConfigFactory.createTypeOfRoomTableConfig(
                this::loadTypesOfRoomData,
                this::handleAddTypeOfRoom,
                this::handleEditTypeOfRoom
        ));

        /*tableConfigs.put("Удобства", ConfigFactory.createConvenienceTableConfig(
                this::loadConveniencesData,
                this::handleAddConvenience,
                this::handleEditConvenience
        ));

        tableConfigs.put("Города", ConfigFactory.createCityTableConfig(
                this::loadCitiesData,
                this::handleAddCity,
                this::handleEditCity
        ));

        tableConfigs.put("Удобства в комнате", ConfigFactory.createRoomConvenienceTableConfig(
                this::loadRoomConveniencesData,
                this::handleAddRoomConvenience,
                this::handleEditRoomConvenience
        ));*/
    }

    // Методы загрузки данных (остаются без изменений)
    private javafx.collections.ObservableList<Object> loadHotelsData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> hotels = FXCollections.observableArrayList();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotels;
    }

    private javafx.collections.ObservableList<Object> loadRoomsData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> rooms = FXCollections.observableArrayList();
        try {
            String selectedHotel = (String) filters.get("hotel");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // Аналогичные методы для других сущностей...
    private javafx.collections.ObservableList<Object> loadTypesOfRoomData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> types = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_room_types");
            while (rs.next()) {
                types.add(new TypeOfRoom(
                        rs.getInt("room_type_id"),
                        rs.getString("room_type_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }

    private javafx.collections.ObservableList<Object> loadConveniencesData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> conveniences = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_conveniences");
            while (rs.next()) {
                conveniences.add(new Convenience(
                        rs.getInt("conv_name_id"),
                        rs.getString("conv_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conveniences;
    }

    private javafx.collections.ObservableList<Object> loadCitiesData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> cities = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_cities");
            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("city_id"),
                        rs.getString("city_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities;
    }

    private javafx.collections.ObservableList<Object> loadRoomConveniencesData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> roomConveniences = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_room_conveniences");
            while (rs.next()) {
                roomConveniences.add(new RoomConvenience(
                        rs.getInt("room_id"),
                        rs.getInt("conv_name_id"),
                        rs.getDouble("price_per_one"),
                        rs.getInt("amount"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getString("conv_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomConveniences;
    }

    // Упрощенные обработчики - теперь они просто вызывают FormManager с конфигурациями из ConfigFactory
    private Void handleAddHotel(Void param) {
        UniversalFormConfig<Hotel> formConfig = ConfigFactory.createHotelFormConfig(
                this::saveHotel,
                hotel -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditHotel(Object hotelObj) {
        if (!(hotelObj instanceof Hotel hotel)) {
            showError(statusLabel, "Неверный тип данных для редактирования отеля");
            return null;
        }
        UniversalFormConfig<Hotel> formConfig = ConfigFactory.createHotelFormConfig(
                this::saveHotel,
                h->refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, hotel, getActiveTableController());
        return null;
    }

    private Void handleAddRoom(Void param) {
        UniversalFormConfig<Room> formConfig = ConfigFactory.createRoomFormConfig(
                this::saveRoom,
                room -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditRoom(Object roomObj) {
        if (!(roomObj instanceof Room)) {
            showError(statusLabel, "Неверный тип данных для редактирования комнаты");
            return null;
        }
        Room room = (Room) roomObj;
        UniversalFormConfig<Room> formConfig = ConfigFactory.createRoomFormConfig(
                this::saveRoom,
                r -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, room, getActiveTableController());
        return null;
    }

    private Void handleAddTypeOfRoom(Void param) {
        UniversalFormConfig<TypeOfRoom> formConfig = ConfigFactory.createTypeOfRoomFormConfig(
                this::saveTypeOfRoom,
                type -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditTypeOfRoom(Object typeObj) {
        if (!(typeObj instanceof TypeOfRoom)) {
            showError(statusLabel, "Неверный тип данных для редактирования типа комнаты");
            return null;
        }
        TypeOfRoom type = (TypeOfRoom) typeObj;
        UniversalFormConfig<TypeOfRoom> formConfig = ConfigFactory.createTypeOfRoomFormConfig(
                this::saveTypeOfRoom,
                t -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, type, getActiveTableController());
        return null;
    }

    // Остальные обработчики по аналогии...

    // Методы сохранения (остаются без изменений)
    private Boolean saveHotel(Hotel hotel) {
        try {
            Connection connection = Session.getConnection();
            if (hotel.getId() == 0) {
                Database_functions.callFunction(connection, "add_hotel", hotel.getCityId(), hotel.getAddress());
            } else {
                Database_functions.callFunction(connection, "edit_hotel", hotel.getId(), hotel.getCityId(), hotel.getAddress());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Boolean saveRoom(Room room) {
        // Реализация сохранения комнаты
        System.out.println("Сохранение комнаты: " + room.getRoomNumber());
        return true;
    }

    private Boolean saveTypeOfRoom(TypeOfRoom type) {
        // Реализация сохранения типа комнаты
        System.out.println("Сохранение типа комнаты: " + type.getName());
        return true;
    }

    // Остальные методы без изменений...

    // Вспомогательные методы (без изменений)
    private void refreshActiveTable() {
        UniversalTableController controller = getActiveTableController();
        if (controller != null) {
            controller.refreshData();
        }
    }

    private UniversalTableController getActiveTableController() {
        Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (activeTab != null && activeTab.getContent() != null) {
            return (UniversalTableController) activeTab.getContent().getProperties().get("controller");
        }
        return null;
    }

    @FXML
    private void openTableTab(String tableName) {
        try {
            for (Tab tab : mainTabPane.getTabs()) {
                if (tableName.equals(tab.getText())) {
                    mainTabPane.getSelectionModel().select(tab);
                    return;
                }
            }

            TableConfig config = tableConfigs.get(tableName);
            if (config == null) {
                statusLabel.setText("Конфигурация для таблицы '" + tableName + "' не найдена");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/components/universal_table.fxml"));
            Parent tableContent = loader.load();

            UniversalTableController controller = loader.getController();
            controller.configure(config);

            tableContent.getProperties().put("controller", controller);

            Tab tableTab = new Tab(tableName);
            tableTab.setContent(tableContent);
            tableTab.setClosable(true);

            mainTabPane.getTabs().add(tableTab);
            mainTabPane.getSelectionModel().select(tableTab);
            statusLabel.setText("Открыта таблица: " + tableName);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка загрузки таблицы '" + tableName + "'");
        }
    }

    // Интерфейс для обновления таблиц из форм
    public interface RefreshableController {
        void handleRefresh();
    }

    // Методы открытия вкладок (без изменений)
    @FXML private void showHotelManagement() { openTableTab("Отели"); }
    @FXML private void showRoomManagement() { openTableTab("Номера"); }
    @FXML private void showTypeOfRoomManagement() { openTableTab("Типы комнат"); }
    @FXML private void showConveniencesManagement() { openTableTab("Удобства"); }
    @FXML private void showCityManagement() { openTableTab("Города"); }
    @FXML private void showRoomConvenienceManagement() { openTableTab("Удобства в комнате"); }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            Parent root = FXMLLoader.load(getClass().getResource("/app/subd/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Авторизация");
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

            Stage currentStage = (Stage) mainTabPane.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showGeneralStats() {
        System.out.println("Общая статистика");
    }
}
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

import java.lang.reflect.Field;
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
        try {
            AllDictionaries.initialiseCitiesMaps();
            AllDictionaries.initialiseHotelsMaps();
            //AllDictionaries.initialiseSocialStatusMaps();
            //AllDictionaries.initialiseServicesMaps();
            AllDictionaries.initialiseTypesOfRoomMaps();
            AllDictionaries.initialiseConveniencesMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeTableConfigs() {
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

        tableConfigs.put("Пользователи", ConfigFactory.createUserTableConfig(
                this::loadUsersData,
                this::handleAddUser,
                this::handleEditUser,
                this::handleToggleUserActive
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
            Hotel selectedHotel = (Hotel) filters.get("hotel");
            if (selectedHotel == null) {
                return rooms;
            }

            int hotelId = selectedHotel.getId();
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
            ResultSet rs = Database_functions.callFunction(connection, "get_all_types_of_room");
            while (rs.next()) {
                types.add(new TypeOfRoom(
                        rs.getInt("type_id"),
                        rs.getString("type_name")
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

    private Void handleAddHotel(Void param) {
        UniversalFormConfig<Hotel> formConfig = ConfigFactory.createHotelFormConfig(
                this::saveHotel,
                h -> refreshActiveTable(),
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
                h -> refreshActiveTable(),
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
        if (!(roomObj instanceof Room room)) {
            showError(statusLabel, "Неверный тип данных для редактирования комнаты");
            return null;
        }
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
        if (!(typeObj instanceof TypeOfRoom type)) {
            showError(statusLabel, "Неверный тип данных для редактирования типа комнаты");
            return null;
        }
        UniversalFormConfig<TypeOfRoom> formConfig = ConfigFactory.createTypeOfRoomFormConfig(
                this::saveTypeOfRoom,
                t -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, type, getActiveTableController());
        return null;
    }


    private Boolean saveHotel(Hotel hotel) {
        try {
            Connection connection = Session.getConnection();

            String cityName = AllDictionaries.getCitiesNameMap().get(hotel.getCityId());
            Integer cityId = AllDictionaries.getCitiesIdMap().get(cityName);

            if (cityId == null) {
                showError(statusLabel, "Неверно выбран город");
                return false;
            }

            if (hotel.getId() == 0) {
                Database_functions.callFunction(connection, "add_new_hotel", cityId, hotel.getAddress());
                showSuccess(statusLabel, "Отель успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_hotel", hotel.getId(), cityId, hotel.getAddress());
                showSuccess(statusLabel, "Отель успешно обновлен");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения отеля: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveRoom(Room room) {
        try {
            Connection connection = Session.getConnection();
            if (room.getId() == 0) {
                Database_functions.callFunction(connection, "add_hotel_room",
                        room.getHotelId(), room.getMaxPeople(), room.getPricePerPerson(),
                        room.getRoomNumber(), room.getTypeOfRoomId());
            } else {
                Database_functions.callFunction(connection, "edit_room",
                        room.getId(), room.getHotelId(), room.getMaxPeople(), room.getPricePerPerson(),
                        room.getRoomNumber(), room.getTypeOfRoomId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения комнаты: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveTypeOfRoom(TypeOfRoom type) {
        try {
            Connection connection = Session.getConnection();
            if (type.getId() == 0) {
                Database_functions.callFunction(connection, "add_type_of_room",
                        type.getName());
            } else {
                Database_functions.callFunction(connection, "edit_type_of_room",
                        type.getId(), type.getName());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения комнаты: " + e.getMessage());
            return false;
        }
    }

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/universal_table.fxml"));
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

    public interface RefreshableController {
        void handleRefresh();
    }

    @FXML private void showHotelManagement() { openTableTab("Отели"); }
    @FXML private void showRoomManagement() { openTableTab("Номера"); }
    @FXML private void showTypeOfRoomManagement() { openTableTab("Типы комнат"); }
    @FXML private void showConveniencesManagement() { openTableTab("Удобства"); }
    @FXML private void showCityManagement() { openTableTab("Города"); }
    @FXML private void showRoomConvenienceManagement() { openTableTab("Удобства в комнате"); }
    @FXML private void showUserManagement() { openTableTab("Пользователи"); }

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

    private javafx.collections.ObservableList<Object> loadUsersData(Map<String, Object> filters) {
        javafx.collections.ObservableList<Object> users = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_users");

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role_name"),
                        AllDictionaries.getHotelsNameMap().get(rs.getInt("hotel_id")),
                        rs.getBoolean("user_locked")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка загрузки пользователей: " + e.getMessage());
        }
        return users;
    }

    // Обработчики для пользователей
    private Void handleAddUser(Void param) {
        UniversalFormConfig<User> formConfig = ConfigFactory.createAddUserFormConfig(
                this::saveUser,
                u -> refreshActiveTable()
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditUser(Object userObj) {
        if (!(userObj instanceof User user)) {
            showError(statusLabel, "Неверный тип данных для редактирования пользователя");
            return null;
        }
        UniversalFormConfig<User> formConfig = ConfigFactory.createEditUserFormConfig(
                this::saveUser,
                u -> refreshActiveTable()
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, user, getActiveTableController());
        return null;
    }

    private Void handleToggleUserActive(Object userObj) {
        if (!(userObj instanceof User user)) {
            showError(statusLabel, "Неверный тип данных для изменения статуса пользователя");
            return null;
        }

        try {
            Connection connection = Session.getConnection();
            Boolean newStatus = !user.getUserLocked();

            Database_functions.callFunction(connection, "toggle_user_lock", user.getUsername(), newStatus);

            showSuccess(statusLabel, "Статус пользователя " + user.getUsername() +
                    " изменен на: " + (newStatus ? "заблокирован" : "активен"));

            refreshActiveTable();

        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка изменения статуса пользователя: " + e.getMessage());
        }
        return null;
    }

    // Метод сохранения пользователя
    private Boolean saveUser(User user) {
        try {
            Connection connection = Session.getConnection();

            String hotelInfo = user.getHotelInfo();
            Integer hotelId = AllDictionaries.getHotelsIdMap().get(hotelInfo);

            if (hotelId == null) {
                showError(statusLabel, "Неверно выбран отель");
                return false;
            }

            String password = getTempPassword(user);

            if (user.getId() == 0) {
                if (password == null || password.isEmpty()) {
                    showError(statusLabel, "Пароль обязателен для нового пользователя");
                    return false;
                }

                Database_functions.callFunction(connection, "create_user_with_role",
                        user.getUsername(), password, user.getRole(), hotelId);
                showSuccess(statusLabel, "Пользователь " + user.getUsername() + " успешно создан");

            } else {
                Database_functions.callFunction(connection, "update_user_profile",
                        user.getId(), user.getUsername(), user.getRole(), hotelId);

                if (password != null && !password.isEmpty()) {
                    Database_functions.callFunction(connection, "change_user_password",
                            user.getUsername(), password);
                }

                Database_functions.callFunction(connection, "set_user_lock_status",
                        user.getUsername(), user.getUserLocked());

                showSuccess(statusLabel, "Данные пользователя " + user.getUsername() + " успешно обновлены");
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения пользователя: " + e.getMessage());
            return false;
        }
    }

    private String getTempPassword(User user) {
        try {
            Field passwordField = user.getClass().getDeclaredField("tempPassword");
            passwordField.setAccessible(true);
            return (String) passwordField.get(user);
        } catch (Exception e) {
            return null;
        }
    }

    private String getTempConfirmPassword(User user) {
        try {
            Field confirmPasswordField = user.getClass().getDeclaredField("tempConfirmPassword");
            confirmPasswordField.setAccessible(true);
            return (String) confirmPasswordField.get(user);
        } catch (Exception e) {
            return null;
        }
    }


}
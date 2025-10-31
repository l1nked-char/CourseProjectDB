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
import javafx.collections.ObservableList;
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
import java.util.*;

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
            AllDictionaries.initialiseServicesMaps();
            AllDictionaries.initialiseTypesOfRoomMaps();
            AllDictionaries.initialiseConveniencesMaps();
            AllDictionaries.initialiseSocialStatusMaps();
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

        tableConfigs.put("Удобства", ConfigFactory.createConvenienceTableConfig(
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
        ));
        
        tableConfigs.put("Сервисы отеля", ConfigFactory.createHotelServiceTableConfig(
                this::loadHotelServicesData,
                this::handleAddHotelService,
                this::handleEditHotelService
        ));

        tableConfigs.put("История сервисов", ConfigFactory.createServiceHistoryTableConfig(
                this::loadServiceHistoryData,
                this::handleAddServiceHistory,
                this::handleEditServiceHistory
        ));

        tableConfigs.put("Социальные статусы", ConfigFactory.createSocialStatusTableConfig(
                this::loadSocialStatusData,
                this::handleAddSocialStatus,
                this::handleEditSocialStatus
        ));

        tableConfigs.put("Услуги", ConfigFactory.createServiceTableConfig(
                this::loadServicesData,
                this::handleAddService,
                this::handleEditService
        ));

        tableConfigs.put("Жильцы", ConfigFactory.createTenantTableConfig(
                this::loadTenantsData,
                this::handleAddTenant,
                this::handleEditTenant
        ));
    }


    private ObservableList<Object> loadHotelsData(Map<String, Object> filters) {
        ObservableList<Object> hotels = FXCollections.observableArrayList();
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

    private ObservableList<Object> loadRoomsData(Map<String, Object> filters) {
        ObservableList<Object> rooms = FXCollections.observableArrayList();
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
                        rs.getBigDecimal("price_per_person"),
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
    private ObservableList<Object> loadTypesOfRoomData(Map<String, Object> filters) {
        ObservableList<Object> types = FXCollections.observableArrayList();
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

    private ObservableList<Object> loadConveniencesData(Map<String, Object> filters) {
        ObservableList<Object> conveniences = FXCollections.observableArrayList();
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

    private ObservableList<Object> loadCitiesData(Map<String, Object> filters) {
        ObservableList<Object> cities = FXCollections.observableArrayList();
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

    private ObservableList<Object> loadRoomConveniencesData(Map<String, Object> filters) {
        ObservableList<Object> roomConveniences = FXCollections.observableArrayList();
        try {
            Integer hotelId = null;
            Integer roomId = null;

            if (filters.get("hotel") instanceof Hotel hotel) {
                hotelId = hotel.getId();
            }
            if (filters.get("room") instanceof Room room) {
                roomId = room.getId();
            }

            Connection connection = Session.getConnection();
            ResultSet rs;

            if (hotelId != null && roomId != null) {
                rs = Database_functions.callFunction(connection, "get_room_conveniences_by_room", roomId);

                while (rs.next()) {
                    roomConveniences.add(new RoomConvenience(
                            rs.getInt("conv_id"),
                            rs.getInt("room_id"),
                            rs.getInt("conv_name_id"),
                            rs.getBigDecimal("price_per_one"),
                            rs.getInt("amount"),
                            rs.getDate("start_date").toLocalDate(),
                            rs.getString("conv_name")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomConveniences;
    }
    
    private ObservableList<Object> loadHotelServicesData(Map<String, Object> filters) {
        ObservableList<Object> hotelServices = FXCollections.observableArrayList();
        try {
            Hotel selectedHotel = (Hotel) filters.get("hotel");
            if (selectedHotel == null) {
                return hotelServices;
            }
            int hotelId = selectedHotel.getId();
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_hotel_services_by_hotel", hotelId);
            while (rs.next()) {
                int serv_name_id = rs.getInt("service_name_id");
                hotelServices.add(new HotelService(
                        rs.getInt("service_id"),
                        hotelId,
                        serv_name_id,
                        rs.getDate("start_of_period").toLocalDate(),
                        rs.getDate("end_of_period").toLocalDate(),
                        rs.getBigDecimal("price_per_one"),
                        rs.getBoolean("can_be_booked"),
                        AllDictionaries.getServicesNameMap().get(serv_name_id)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotelServices;
    }

    private ObservableList<Object> loadServiceHistoryData(Map<String, Object> filters) {
        ObservableList<Object> serviceHistory = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_service_history");
            while (rs.next()) {
                serviceHistory.add(new ServiceHistory(
                        rs.getInt("id"),
                        rs.getString("history_id"),
                        rs.getInt("service_id"),
                        rs.getInt("amount")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceHistory;
    }

    private ObservableList<Object> loadSocialStatusData(Map<String, Object> filters) {
        ObservableList<Object> socialStatuses = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_social_statuses");
            while (rs.next()) {
                socialStatuses.add(new SocialStatus(
                        rs.getInt("status_id"),
                        rs.getString("status_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socialStatuses;
    }

    private ObservableList<Object> loadServicesData(Map<String, Object> filters) {
        ObservableList<Object> services = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_services");
            while (rs.next()) {
                services.add(new Service(
                        rs.getInt("service_name_id"),
                        rs.getString("service_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    private ObservableList<Object> loadTenantsData(Map<String, Object> filters) {
        ObservableList<Object> tenants = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_tenants");
            while (rs.next()) {
                Tenant tenant = new Tenant();
                tenant.setId(rs.getInt("id"));
                tenant.setFirstName(rs.getString("first_name"));
                tenant.setName(rs.getString("name"));
                tenant.setPatronymic(rs.getString("patronymic"));
                tenant.setCityId(rs.getInt("city_id"));
                tenant.setBirthDate(rs.getDate("birth_date").toLocalDate());
                tenant.setSocialStatusId(rs.getInt("social_status_id"));
                tenant.setSeries(rs.getInt("series"));
                tenant.setNumber(rs.getInt("number"));
                tenant.setDocumentType(rs.getString("document_type"));
                tenant.setEmail(rs.getString("email"));
                tenant.setSocialStatus(AllDictionaries.getSocialStatusNameMap().get(tenant.getSocialStatusId()));
                tenants.add(tenant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tenants;
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
    
    private Void handleAddHotelService(Void param) {
        UniversalFormConfig<HotelService> formConfig = ConfigFactory.createHotelServiceFormConfig(
                this::saveHotelService,
                hs -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditHotelService(Object hsObj) {
        if (!(hsObj instanceof HotelService hs)) {
            showError(statusLabel, "Неверный тип данных для редактирования сервиса отеля");
            return null;
        }
        UniversalFormConfig<HotelService> formConfig = ConfigFactory.createHotelServiceFormConfig(
                this::saveHotelService,
                h -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, hs, getActiveTableController());
        return null;
    }

    private Void handleAddServiceHistory(Void param) {
        UniversalFormConfig<ServiceHistory> formConfig = ConfigFactory.createServiceHistoryFormConfig(
                this::saveServiceHistory,
                sh -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditServiceHistory(Object shObj) {
        if (!(shObj instanceof ServiceHistory sh)) {
            showError(statusLabel, "Неверный тип данных для редактирования истории сервиса");
            return null;
        }
        UniversalFormConfig<ServiceHistory> formConfig = ConfigFactory.createServiceHistoryFormConfig(
                this::saveServiceHistory,
                s -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, sh, getActiveTableController());
        return null;
    }

    private Void handleAddSocialStatus(Void param) {
        UniversalFormConfig<SocialStatus> formConfig = ConfigFactory.createSocialStatusFormConfig(
                this::saveSocialStatus,
                ss -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditSocialStatus(Object ssObj) {
        if (!(ssObj instanceof SocialStatus ss)) {
            showError(statusLabel, "Неверный тип данных для редактирования социального статуса");
            return null;
        }
        UniversalFormConfig<SocialStatus> formConfig = ConfigFactory.createSocialStatusFormConfig(
                this::saveSocialStatus,
                s -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, ss, getActiveTableController());
        return null;
    }

    private Void handleAddService(Void param) {
        UniversalFormConfig<Service> formConfig = ConfigFactory.createServiceFormConfig(
                this::saveService,
                s -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditService(Object sObj) {
        if (!(sObj instanceof Service s)) {
            showError(statusLabel, "Неверный тип данных для редактирования услуги");
            return null;
        }
        UniversalFormConfig<Service> formConfig = ConfigFactory.createServiceFormConfig(
                this::saveService,
                serv -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, s, getActiveTableController());
        return null;
    }

    private Void handleAddTenant(Void param) {
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createTenantFormConfig(
                this::saveTenant,
                ten -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditTenant(Object tObj) {
        if (!(tObj instanceof Tenant t)) {
            showError(statusLabel, "Неверный тип данных для редактирования жильца");
            return null;
        }
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createTenantFormConfig(
                this::saveTenant,
                ten -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, t, getActiveTableController());
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
                Database_functions.callFunction(connection, "add_room",
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
    
    private Boolean saveHotelService(HotelService hotelService) {
        try {
            Connection connection = Session.getConnection();
            if (hotelService.getId() == 0) {
                Database_functions.callFunction(connection, "add_hotel_service",
                        hotelService.getHotelId(), hotelService.getServiceNameId(), hotelService.getStartOfPeriod(),
                        hotelService.getEndOfPeriod(), hotelService.getPricePerOne(), hotelService.getCanBeBooked());
            } else {
                Database_functions.callFunction(connection, "edit_hotel_service",
                        hotelService.getId(), hotelService.getHotelId(), hotelService.getServiceNameId(),
                        hotelService.getStartOfPeriod(), hotelService.getEndOfPeriod(), hotelService.getPricePerOne(),
                        hotelService.getCanBeBooked());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения сервиса отеля: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveServiceHistory(ServiceHistory serviceHistory) {
        try {
            Connection connection = Session.getConnection();
            if (serviceHistory.getId() == 0) {
                Database_functions.callFunction(connection, "add_service_history",
                        serviceHistory.getHistoryId(), serviceHistory.getServiceId(), serviceHistory.getAmount());
            } else {
                Database_functions.callFunction(connection, "edit_service_history",
                        serviceHistory.getId(), serviceHistory.getHistoryId(), serviceHistory.getServiceId(),
                        serviceHistory.getAmount());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения истории сервиса: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveSocialStatus(SocialStatus socialStatus) {
        try {
            Connection connection = Session.getConnection();
            if (socialStatus.getId() == 0) {
                Database_functions.callFunction(connection, "add_social_status", socialStatus.getName());
                showSuccess(statusLabel, "Социальный статус успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_social_status", socialStatus.getId(), socialStatus.getName());
                showSuccess(statusLabel, "Социальный статус успешно обновлен");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения социального статуса: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveService(Service service) {
        try {
            Connection connection = Session.getConnection();
            if (service.getId() == 0) {
                Database_functions.callFunction(connection, "add_service", service.getName());
                showSuccess(statusLabel, "Услуга успешно добавлена");
            } else {
                Database_functions.callFunction(connection, "edit_service", service.getId(), service.getName());
                showSuccess(statusLabel, "Услуга успешно обновлена");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения услуги: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveTenant(Tenant tenant) {
        try {
            Connection connection = Session.getConnection();
            if (tenant.getId() == 0) {
                Database_functions.callFunction(connection, "add_tenant",
                        tenant.getName(), tenant.getSeries(), tenant.getSocialStatusId());
                showSuccess(statusLabel, "Жилец успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_tenant",
                        tenant.getId(), tenant.getName(), tenant.getSeries(), tenant.getSocialStatusId());
                showSuccess(statusLabel, "Жилец успешно обновлен");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения жильца: " + e.getMessage());
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
    @FXML private void showHotelServiceManagement() { openTableTab("Сервисы отеля"); }
    @FXML private void showServiceHistoryManagement() { openTableTab("История сервисов"); }
    @FXML private void showSocialStatusManagement() { openTableTab("Социальные статусы"); }
    @FXML private void showServiceManagement() { openTableTab("Услуги"); }
    @FXML private void showTenantManagement() { openTableTab("Жильцы"); }

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

    private ObservableList<Object> loadUsersData(Map<String, Object> filters) {
        ObservableList<Object> users = FXCollections.observableArrayList();
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

            Database_functions.callFunction(connection, user.getUserLocked() ? "unban_user" : "ban_user", user.getUsername());

            showSuccess(statusLabel, "Статус пользователя " + user.getUsername() +
                    " изменен!");

            refreshActiveTable();

        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка изменения статуса пользователя: " + e.getMessage());
        }
        return null;
    }

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
                Database_functions.callFunction(connection, "change_user_hotel",
                        user.getId(), hotelId);

                if (password != null && !password.isEmpty()) {
                    Database_functions.callFunction(connection, "change_user_password",
                            user.getUsername(), password);
                }

                Database_functions.callFunction(connection, "change_username",
                        user.getId(), user.getUsername());

                Database_functions.callFunction(connection, "change_user_role",
                        user.getUsername(), user.getRole());

                showSuccess(statusLabel, "Данные пользователя " + user.getUsername() + " успешно обновлены");
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения пользователя: " + e.getMessage());
            return false;
        }
    }

    private Void handleAddConvenience(Void param) {
        UniversalFormConfig<Convenience> formConfig = ConfigFactory.createConvenienceFormConfig(
                this::saveConvenience,
                c -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditConvenience(Object convObj) {
        if (!(convObj instanceof Convenience conv)) {
            showError(statusLabel, "Неверный тип данных для редактирования удобства");
            return null;
        }
        UniversalFormConfig<Convenience> formConfig = ConfigFactory.createConvenienceFormConfig(
                this::saveConvenience,
                c -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, conv, getActiveTableController());
        return null;
    }

    private Void handleAddCity(Void param) {
        UniversalFormConfig<City> formConfig = ConfigFactory.createCityFormConfig(
                this::saveCity,
                c -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditCity(Object cityObj) {
        if (!(cityObj instanceof City city)) {
            showError(statusLabel, "Неверный тип данных для редактирования города");
            return null;
        }
        UniversalFormConfig<City> formConfig = ConfigFactory.createCityFormConfig(
                this::saveCity,
                c -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, city, getActiveTableController());
        return null;
    }

    private Void handleAddRoomConvenience(Void param) {
        UniversalFormConfig<RoomConvenience> formConfig = ConfigFactory.createRoomConvenienceFormConfig(
                this::saveRoomConvenience,
                rc -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditRoomConvenience(Object rcObj) {
        if (!(rcObj instanceof RoomConvenience rc)) {
            showError(statusLabel, "Неверный тип данных для редактирования удобства в комнате");
            return null;
        }
        UniversalFormConfig<RoomConvenience> formConfig = ConfigFactory.createRoomConvenienceFormConfig(
                this::saveRoomConvenience,
                r -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, rc, getActiveTableController());
        return null;
    }

    // Добавьте методы сохранения:
    private Boolean saveConvenience(Convenience conv) {
        try {
            Connection connection = Session.getConnection();
            if (conv.getId() == 0) {
                Database_functions.callFunction(connection, "add_convenience", conv.getName());
                showSuccess(statusLabel, "Удобство успешно добавлено");
            } else {
                Database_functions.callFunction(connection, "edit_convenience", conv.getId(), conv.getName());
                showSuccess(statusLabel, "Удобство успешно обновлено");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения удобства: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveCity(City city) {
        try {
            Connection connection = Session.getConnection();
            if (city.getCityId() == 0) {
                Database_functions.callFunction(connection, "add_city", city.getCityName());
                showSuccess(statusLabel, "Город успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_city", city.getCityId(), city.getCityName());
                showSuccess(statusLabel, "Город успешно обновлен");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения города: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveRoomConvenience(RoomConvenience rc) {
        try {
            Connection connection = Session.getConnection();
            // При добавлении roomId уже установлен в UniversalFormController
            if (rc.getId() == 0) {
                Database_functions.callFunction(connection, "add_room_convenience",
                        rc.getRoomId(), rc.getConvNameId(), rc.getPricePerOne(), // rc.getRoomId() теперь содержит правильное значение
                        rc.getAmount(), rc.getStartDate());
                showSuccess(statusLabel, "Удобство в комнате успешно добавлено");
            } else {
                Database_functions.callFunction(connection, "edit_room_convenience",
                        rc.getId(), rc.getConvNameId(), rc.getPricePerOne(),
                        rc.getAmount(), rc.getStartDate());
                showSuccess(statusLabel, "Удобство в комнате успешно обновлено");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showError(statusLabel, "Ошибка сохранения удобства в комнате: " + e.getMessage());
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
}

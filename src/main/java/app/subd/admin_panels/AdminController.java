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

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
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
            System.err.println("Ошибка инициализации словарей: " + e.getMessage());
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

        tableConfigs.put("История заселений", ConfigFactory.createTenantHistoryTableConfig(
                this::loadTenantHistoryData,
                this::handleAddTenantHistory,
                this::handleEditTenantHistory
        ));
    }


    private ObservableList<Object> loadHotelsData(Map<String, Object> filters) {
        ObservableList<Object> hotels = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            // Извлекаем параметры фильтрации
            String cityFilter = getStringFilter(filters, "cityName");
            String addressFilter = getStringFilter(filters, "address");

            if (hasActiveFilters(cityFilter, addressFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_hotels_filtered",
                        cityFilter, addressFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_hotels");
            }

            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("hotel_id"),
                        rs.getInt("city_id"),
                        rs.getString("hotel_address"),
                        rs.getString("hotel_city")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки данных отелей: " + e.getMessage());
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

            String roomNumber = (String) filters.get("roomNumber");
            String roomTypeName = (String) filters.get("roomTypeName");
            String maxPeople = (String) filters.get("maxPeople");
            String pricePerPerson = (String) filters.get("pricePerPerson");

            ResultSet rs = Database_functions.callFunction(connection, "get_rooms_by_hotel_filtered",
                    hotelId, roomNumber, roomTypeName, maxPeople, pricePerPerson);

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
            System.err.println("Ошибка загрузки данных о комнатах: " + e.getMessage());
        }
        return rooms;
    }

    private ObservableList<Object> loadTypesOfRoomData(Map<String, Object> filters) {
        ObservableList<Object> types = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            String typeNameFilter = getStringFilter(filters, "name");

            if (hasActiveFilters(typeNameFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_types_of_room_filtered", typeNameFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_types_of_room");
            }

            while (rs.next()) {
                types.add(new TypeOfRoom(
                        rs.getInt("type_id"),
                        rs.getString("type_name")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки типов комнат: " + e.getMessage());
        }
        return types;
    }

    private ObservableList<Object> loadConveniencesData(Map<String, Object> filters) {
        ObservableList<Object> conveniences = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            String convNameFilter = getStringFilter(filters, "name");

            if (hasActiveFilters(convNameFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_conveniences_filtered", convNameFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_conveniences");
            }

            while (rs.next()) {
                conveniences.add(new Convenience(
                        rs.getInt("conv_name_id"),
                        rs.getString("conv_name")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки удобств: " + e.getMessage());
        }
        return conveniences;
    }

    private ObservableList<Object> loadCitiesData(Map<String, Object> filters) {
        ObservableList<Object> cities = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            String cityNameFilter = getStringFilter(filters, "cityName");

            if (hasActiveFilters(cityNameFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_cities_filtered", cityNameFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_cities");
            }

            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("city_id"),
                        rs.getString("city_name")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки городов: " + e.getMessage());
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
            System.err.println("Ошибка загрузки удобств в номере: " + e.getMessage());
        }
        return roomConveniences;
    }
    
    private ObservableList<Object> loadHotelServicesData(Map<String, Object> filters) {
        return ConfigFactory.getHotelServicesForComboBox(filters);
    }

    private ObservableList<Object> loadServiceHistoryData(Map<String, Object> filters) {
        ObservableList<Object> serviceHistory = FXCollections.observableArrayList();

        if (!(filters.get("booking") instanceof TenantHistory selectedBooking)) {
            return serviceHistory;
        }

        try {
            String bookingNumber = selectedBooking.getBookingNumber();
            Connection connection = Session.getConnection();

            ResultSet rs = Database_functions.callFunction(connection, "get_service_history_by_booking", bookingNumber);

            while (rs.next()) {
                int service_name_id = rs.getInt("service_name_id");
                ServiceHistory historyItem = new ServiceHistory(
                        rs.getInt("row_id"),
                        bookingNumber,
                        rs.getInt("service_id"),
                        rs.getInt("amount"),
                        AllDictionaries.getServicesNameMap().get(service_name_id),
                        service_name_id
                );
                serviceHistory.add(historyItem);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки истории услуг: " + e.getMessage());
        }
        return serviceHistory;
    }

    private ObservableList<Object> loadSocialStatusData(Map<String, Object> filters) {
        ObservableList<Object> socialStatuses = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            String statusNameFilter = getStringFilter(filters, "name");

            if (hasActiveFilters(statusNameFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_social_statuses_filtered", statusNameFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_social_statuses");
            }

            while (rs.next()) {
                socialStatuses.add(new SocialStatus(
                        rs.getInt("status_id"),
                        rs.getString("status_name")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки социальных статусов: " + e.getMessage());
        }
        return socialStatuses;
    }

    private ObservableList<Object> loadServicesData(Map<String, Object> filters) {
        ObservableList<Object> services = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            String serviceNameFilter = getStringFilter(filters, "name");

            if (hasActiveFilters(serviceNameFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_services_filtered", serviceNameFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_services");
            }

            while (rs.next()) {
                services.add(new Service(
                        rs.getInt("service_name_id"),
                        rs.getString("service_name")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки услуг: " + e.getMessage());
        }
        return services;
    }

    private ObservableList<Object> loadTenantsData(Map<String, Object> filters) {
        ObservableList<Object> tenants = FXCollections.observableArrayList();
        try {
            Hotel selectedHotel = (Hotel) filters.get("hotel");
            if (selectedHotel == null) {
                return tenants;
            }
            int hotelId = selectedHotel.getId();

            Connection connection = Session.getConnection();
            ResultSet rs;

            // Извлекаем параметры фильтрации для жильцов
            String firstNameFilter = getStringFilter(filters, "firstName");
            String nameFilter = getStringFilter(filters, "name");
            String patronymicFilter = getStringFilter(filters, "patronymic");
            String cityNameFilter = getStringFilter(filters, "cityName");
            String birthDateFilter = getStringFilter(filters, "birthDate");
            String socialStatusFilter = getStringFilter(filters, "socialStatus");
            String seriesFilter = getStringFilter(filters, "series");
            String numberFilter = getStringFilter(filters, "number");
            String documentTypeFilter = getStringFilter(filters, "documentType");
            String emailFilter = getStringFilter(filters, "email");

            if (hasActiveFilters(firstNameFilter, nameFilter, patronymicFilter, cityNameFilter,
                    birthDateFilter, socialStatusFilter, seriesFilter, numberFilter,
                    documentTypeFilter, emailFilter)) {
                rs = Database_functions.callFunction(connection, "get_all_tenants_filtered",
                        firstNameFilter, nameFilter, patronymicFilter, cityNameFilter, birthDateFilter,
                        socialStatusFilter, seriesFilter, numberFilter, documentTypeFilter, emailFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_tenants_by_hotel", hotelId);
            }

            while (rs.next()) {
                Tenant tenant = new Tenant(
                        rs.getInt("tenant_id"),
                        rs.getString("first_name"),
                        rs.getString("name"),
                        rs.getString("patronymic"),
                        rs.getInt("city_id"),
                        rs.getInt("social_status_id"),
                        rs.getInt("series"),
                        rs.getInt("number"),
                        DocumentType.getDocumentType(rs.getString("document_type")),
                        rs.getString("email")
                );
                tenant.setBirthDate(rs.getDate("birth_date").toLocalDate());
                tenant.setHotelId(hotelId);
                tenant.setSocialStatus(AllDictionaries.getSocialStatusNameMap().get(tenant.getSocialStatusId()));
                tenants.add(tenant);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки жильцов: " + e.getMessage());
        }
        return tenants;
    }

    private ObservableList<Object> loadTenantHistoryData(Map<String, Object> filters) {
        ObservableList<Object> tenantHistory = FXCollections.observableArrayList();
        try {
            Hotel selectedHotel = (Hotel) filters.get("hotel");
            if (selectedHotel == null) {
                return tenantHistory;
            }
            int hotelId = selectedHotel.getId();

            Connection connection = Session.getConnection();
            ResultSet rs;

            // Извлекаем параметры фильтрации для истории заселений
            String bookingNumberFilter = getStringFilter(filters, "bookingNumber");
            String roomInfoFilter = getStringFilter(filters, "roomInfo");
            String tenantInfoFilter = getStringFilter(filters, "tenantInfo");
            String bookingDateFilter = getStringFilter(filters, "bookingDate");
            String checkInDateFilter = getStringFilter(filters, "checkInDate");
            String checkInStatusFilter = getStringFilter(filters, "checkInStatus");
            String occupiedSpaceFilter = getStringFilter(filters, "occupiedSpace");
            String amountOfNightsFilter = getStringFilter(filters, "amountOfNights");
            Boolean canBeSplitFilter = getBooleanFilter(filters, "canBeSplit");

            if (hasActiveFilters(bookingNumberFilter, roomInfoFilter, tenantInfoFilter, bookingDateFilter,
                    checkInDateFilter, checkInStatusFilter, occupiedSpaceFilter,
                    amountOfNightsFilter) || canBeSplitFilter != null) {
                rs = Database_functions.callFunction(connection, "get_tenant_history_by_hotel_filtered",
                        hotelId, bookingNumberFilter, roomInfoFilter, tenantInfoFilter, bookingDateFilter,
                        checkInDateFilter, checkInStatusFilter, occupiedSpaceFilter, amountOfNightsFilter,
                        canBeSplitFilter != null ? canBeSplitFilter : false);
            } else {
                rs = Database_functions.callFunction(connection, "get_tenant_history_by_hotel", hotelId);
            }

            while (rs.next()) {
                TenantHistory th = new TenantHistory(
                        rs.getString("booking_number"),
                        rs.getInt("room_id"),
                        rs.getInt("tenant_id"),
                        rs.getDate("booking_date").toLocalDate(),
                        rs.getDate("check_in_date").toLocalDate(),
                        BookingStatus.getBookingStatus(rs.getString("check_in_status")),
                        rs.getInt("occupied_space"),
                        rs.getInt("amount_of_nights"),
                        rs.getBoolean("can_be_split")
                );
                th.setHotelId(hotelId);
                tenantHistory.add(th);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки истории заселений: " + e.getMessage());
        }
        return tenantHistory;
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
        UniversalTableController tableController = getActiveTableController();
        if (tableController != null && tableController.getCurrentFilterValues().containsKey("hotel")) {
            Hotel selectedHotel = (Hotel) tableController.getCurrentFilterValues().get("hotel");
            hs.setHotelId(selectedHotel.getId());
        }
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

    private Void handleAddTenantHistory(Void param) {
        UniversalFormConfig<TenantHistory> formConfig = ConfigFactory.createTenantHistoryFormConfig(
                this::saveTenantHistory,
                th -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditTenantHistory(Object thObj) {
        if (!(thObj instanceof TenantHistory th)) {
            showError(statusLabel, "Неверный тип данных для редактирования истории заселения");
            return null;
        }
        UniversalFormConfig<TenantHistory> formConfig = ConfigFactory.createTenantHistoryFormConfig(
                this::saveTenantHistory,
                t -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        UniversalTableController tableController = getActiveTableController();
        if (tableController != null && tableController.getCurrentFilterValues().containsKey("hotel")) {
            Hotel selectedHotel = (Hotel) tableController.getCurrentFilterValues().get("hotel");
            th.setHotelId(selectedHotel.getId());
        }
        FormManager.showForm(formConfig, FormController.Mode.EDIT, th, getActiveTableController());
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
            showError(statusLabel, "Ошибка сохранения комнаты: " + e.getMessage());
            return false;
        }
    }
    
    private Boolean saveHotelService(HotelService hotelService) {
        try {
            Connection connection = Session.getConnection();
            int hotelId = hotelService.getHotelId();
            if (hotelId == 0) {
                UniversalTableController tableController = getActiveTableController();
                if (tableController != null && tableController.getCurrentFilterValues().get("hotel") instanceof Hotel hotel) {
                    hotelId = hotel.getId();
                }
            }

            if (hotelService.getId() == 0) {
                Database_functions.callFunction(connection, "add_hotel_service",
                        hotelId, hotelService.getServiceNameId(), hotelService.getStartOfPeriod(),
                        hotelService.getEndOfPeriod(), hotelService.getPricePerOne(), hotelService.getCanBeBooked());
            } else {
                Database_functions.callFunction(connection, "edit_hotel_service",
                        hotelService.getId(), hotelId, hotelService.getServiceNameId(),
                        hotelService.getStartOfPeriod(), hotelService.getEndOfPeriod(), hotelService.getPricePerOne(),
                        hotelService.getCanBeBooked());
            }
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения сервиса отеля: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveServiceHistory(ServiceHistory serviceHistory) {
        try {
            Connection connection = Session.getConnection();
            String historyId = serviceHistory.getHistoryId();
            if (historyId == null || historyId.isEmpty()) {
                UniversalTableController tableController = getActiveTableController();
                if (tableController != null && tableController.getCurrentFilterValues().get("booking") instanceof TenantHistory th) {
                    historyId = th.getBookingNumber();
                }
            }

            if (serviceHistory.getId() == 0) {
                Database_functions.callFunction(connection, "add_service_history",
                        historyId, serviceHistory.getServiceId(), serviceHistory.getAmount());
                showSuccess(statusLabel, "Заказ услуги успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_service_history",
                        serviceHistory.getId(), historyId, serviceHistory.getServiceId(), serviceHistory.getAmount());
                showSuccess(statusLabel, "Заказ услуги успешно обновлен");
            }
            return true;
        } catch (Exception e) {
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
            showError(statusLabel, "Ошибка сохранения услуги: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveTenant(Tenant tenant) {
        try {
            Connection connection = Session.getConnection();
            String doc_type = tenant.getDocumentType();
            Integer series = tenant.getSeries();
            Integer number = tenant.getNumber();
            if (tenant.getDocumentType().equals("Не указан")) {
                doc_type = null;
                series = null;
                number = null;
            }
            if (tenant.getId() == 0) {
                Database_functions.callFunction(connection, "add_tenant",
                        tenant.getFirstName(), tenant.getName(), tenant.getPatronymic(), tenant.getCityId(),
                        tenant.getBirthDate(), tenant.getSocialStatusId(), tenant.getEmail(), series, number, doc_type);
                showSuccess(statusLabel, "Жилец успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_tenant",
                        tenant.getId(), tenant.getFirstName(), tenant.getName(), tenant.getPatronymic(), tenant.getCityId(),
                        tenant.getBirthDate(), tenant.getSocialStatusId(), tenant.getEmail(), series, number, doc_type);
                showSuccess(statusLabel, "Жилец успешно обновлен");
            }
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения жильца: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveTenantHistory(TenantHistory tenantHistory) {
        try {
            Connection connection = Session.getConnection();
            if (tenantHistory.getBookingNumber().isEmpty()) {
                Database_functions.callFunction(connection, "add_tenant_history",
                        tenantHistory.getTenantId(), tenantHistory.getRoomId(), tenantHistory.getBookingDate(),
                        tenantHistory.getCheckInDate(), tenantHistory.getCheckInStatus(), tenantHistory.getOccupiedSpace(),
                        tenantHistory.getAmountOfNights(), tenantHistory.isCanBeSplit());
                showSuccess(statusLabel, "История заселения успешно добавлена");
            } else {
                Database_functions.callFunction(connection, "edit_tenant_history",
                        tenantHistory.getBookingNumber(), tenantHistory.getTenantId(), tenantHistory.getRoomId(),
                        tenantHistory.getBookingDate(), tenantHistory.getCheckInDate(), tenantHistory.getCheckInStatus(),
                        tenantHistory.getOccupiedSpace(), tenantHistory.getAmountOfNights(), tenantHistory.isCanBeSplit());
                showSuccess(statusLabel, "История заселения успешно обновлена");
            }
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения истории заселения: " + e.getMessage());
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

            URL resource = getClass().getResource("/app/subd/tables/universal_table.fxml");
            if (resource == null) {
                statusLabel.setText("Не удалось найти FXML-файл для таблицы");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
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
            statusLabel.setText("Ошибка загрузки таблицы '" + tableName + "'");
            System.err.println("Ошибка загрузки таблицы: " + e.getMessage());
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
    @FXML private void showTenantHistoryManagement() { openTableTab("История заселений"); }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            URL resource = getClass().getResource("/app/subd/login.fxml");
            if (resource == null) {
                showError(statusLabel, "Не удалось найти FXML-файл для входа");
                return;
            }
            Parent root = FXMLLoader.load(resource);
            Stage stage = new Stage();
            stage.setTitle("Авторизация");
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

            Stage currentStage = (Stage) mainTabPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError(statusLabel, "Ошибка выхода: " + e.getMessage());
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
            ResultSet rs;

            String usernameFilter = getStringFilter(filters, "username");
            String roleFilter = getStringFilter(filters, "role");
            String hotelInfoFilter = getStringFilter(filters, "hotelInfo");
            Boolean userLockedFilter = getBooleanFilter(filters, "userLocked");

            if (hasActiveFilters(usernameFilter, roleFilter, hotelInfoFilter) || userLockedFilter != null) {
                rs = Database_functions.callFunction(connection, "get_all_users_filtered",
                        usernameFilter, roleFilter, hotelInfoFilter, userLockedFilter != null ? userLockedFilter : false);
            } else {
                rs = Database_functions.callFunction(connection, "get_all_users");
            }

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
            showError(statusLabel, "Ошибка сохранения города: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveRoomConvenience(RoomConvenience rc) {
        try {
            Connection connection = Session.getConnection();
            if (rc.getId() == 0) {
                Database_functions.callFunction(connection, "add_room_convenience",
                        rc.getRoomId(), rc.getConvNameId(), rc.getPricePerOne(),
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

    private String getStringFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof String) ? (String) value : "";
    }

    private Boolean getBooleanFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof Boolean) ? (Boolean) value : null;
    }

    private boolean hasActiveFilters(String... filters) {
        for (String filter : filters) {
            if (filter != null && !filter.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasActiveFilters(String filter1, String filter2) {
        return (filter1 != null && !filter1.isEmpty()) || (filter2 != null && !filter2.isEmpty());
    }

    private boolean hasActiveFilters(String filter1, String filter2, String filter3) {
        return (filter1 != null && !filter1.isEmpty()) ||
                (filter2 != null && !filter2.isEmpty()) ||
                (filter3 != null && !filter3.isEmpty());
    }
}
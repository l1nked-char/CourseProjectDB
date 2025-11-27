package app.subd.employee_panels;

import app.subd.components.FormController;
import app.subd.components.Session;
import app.subd.config.ConfigFactory;
import app.subd.config.TableConfig;
import app.subd.config.UniversalFormConfig;
import app.subd.components.FormManager;
import app.subd.components.UniversalTableController;
import app.subd.Database_functions;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class EmployeeController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    private final Map<String, TableConfig> tableConfigs = new HashMap<>();
    private Integer currentHotelId;

    @FXML
    public void initialize() {
        statusLabel.setText("Сотрудник: " + Session.getUsername());
        initializeTableConfigs();
        loadCurrentHotelId();

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

    private void loadCurrentHotelId() {
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_current_hotel_id");
            if (rs.next()) {
                currentHotelId = rs.getInt(1);
                statusLabel.setText("Сотрудник: " + Session.getUsername() + " | Отель: " + getCurrentHotelName());
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки данных отеля: " + e.getMessage());
        }
    }

    private String getCurrentHotelName() {
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_hotels");
            while (rs.next()) {
                if (rs.getInt("hotel_id") == currentHotelId) {
                    return rs.getString("hotel_city") + " - " + rs.getString("hotel_address");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Неизвестный отель";
    }

    private void initializeTableConfigs() {
        tableConfigs.put("Бронирования", ConfigFactory.createEmployeeBookingsTableConfig(
                this::loadBookingsData,
                this::handleCheckIn,      // Заселение в номер
                this::handleBooking,      // Бронирование номера
                this::handleEditBooking   // Редактирование
        ));

        tableConfigs.put("Счета на оплату", ConfigFactory.createEmployeeInvoicesTableConfig(
                this::loadInvoicesData,
                this::handleGenerateInvoices,  // Формирование счетов
                this::handleEditInvoice,
                this::handleToggleInvoiceStatus
        ));

        // Остальные конфигурации остаются без изменений
        tableConfigs.put("Клиенты", ConfigFactory.createEmployeeClientsTableConfig(
                this::loadClientsData,
                this::handleAddClient,
                this::handleEditClient
        ));

        tableConfigs.put("Информация о бронировании", ConfigFactory.createBookingInfoTableConfig(
                this::loadBookingInfoData
        ));

        tableConfigs.put("Свободные комнаты", ConfigFactory.createAvailableRoomsTableConfig(
                this::loadAvailableRoomsData
        ));
        
        tableConfigs.put("Дополнительные услуги", ConfigFactory.createEmployeeServiceHistoryTableConfig(
                this::loadAdditionalServicesData,
                this::handleAddAdditionalService,
                this::handleEditAdditionalService
        ));

        tableConfigs.put("Услуги отеля", ConfigFactory.createHotelServiceTableConfig(
                this::loadHotelServicesData,
                null,
                null
        ));
    }

    private ObservableList<Object> loadHotelServicesData(Map<String, Object> filters) {
        ObservableList<Object> services = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            String serviceNameFilter = getStringFilter(filters, "serviceName");
            String priceFilter = getStringFilter(filters, "pricePerOne");
            Map.Entry<Integer, Integer> pagination = getPaginationParams(filters);
            Integer lastId = pagination.getKey();
            Integer limit = pagination.getValue();

            ResultSet rs = Database_functions.callFunctionWithPagination(connection, "get_hotel_services_by_hotel_filtered",
                    "service_id", lastId, limit, currentHotelId, serviceNameFilter, "", "", priceFilter, null);

            while (rs.next()) {
                int serviceNameId = rs.getInt("service_name_id");
                services.add(new HotelService(
                        rs.getInt("service_id"),
                        currentHotelId,
                        serviceNameId,
                        rs.getDate("start_of_period").toLocalDate(),
                        rs.getDate("end_of_period").toLocalDate(),
                        rs.getBigDecimal("price_per_one"),
                        rs.getBoolean("can_be_booked"),
                        AllDictionaries.getServicesNameMap().get(serviceNameId)
                ));
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки услуг отеля: " + e.getMessage());
        }
        return services;
    }

    private ObservableList<Object> loadAdditionalServicesData(Map<String, Object> filters) {
        ObservableList<Object> services = FXCollections.observableArrayList();
        Object roomObj = filters.get("room");
        Object clientObj = filters.get("client");

        if (roomObj instanceof Room room && clientObj instanceof Tenant client) {
            try {
                Connection connection = Session.getConnection();
                ResultSet rsBooking = Database_functions.callFunction(connection, "get_active_booking",
                        room.getId(), client.getId());

                if (rsBooking.next()) {
                    String bookingNumber = rsBooking.getString(1);
                    if (bookingNumber != null) {
                        String serviceNameFilter = getStringFilter(filters, "serviceName");
                        String amountFilter = getStringFilter(filters, "amount");
                        Map.Entry<Integer, Integer> pagination = getPaginationParams(filters);
                        Integer lastId = pagination.getKey();
                        Integer limit = pagination.getValue();

                        ResultSet rsServices = Database_functions.callFunctionWithPagination(connection, "get_service_history_by_booking_filtered",
                                "row_id", lastId, limit, bookingNumber, serviceNameFilter, amountFilter);
                        while (rsServices.next()) {
                            ServiceHistory service = new ServiceHistory();
                            service.setId(rsServices.getInt("row_id"));
                            service.setHistoryId(bookingNumber);
                            service.setServiceId(rsServices.getInt("service_id"));
                            service.setAmount(rsServices.getInt("amount"));
                            int a = rsServices.getInt("service_name_id");
                            service.setServiceNameId(a);
                            service.setServiceName(AllDictionaries.getServicesNameMap().get(a));
                            services.add(service);
                        }
                    }
                } else {
                    showInfo(statusLabel, "Активное бронирование для выбранных комнаты и клиента не найдено.");
                }
            } catch (Exception e) {
                showError(statusLabel, "Ошибка загрузки дополнительных услуг: " + e.getMessage());
            }
        }
        return services;
    }
    
    private Void handleAddAdditionalService(Void param) {
        UniversalTableController tableController = getActiveTableController();
        if (tableController == null) return null;
        Map<String, Object> filters = tableController.getCurrentFilterValues();
        Object roomObj = filters.get("room");
        Object clientObj = filters.get("client");

        if (roomObj instanceof Room room && clientObj instanceof Tenant client) {
            try {
                Connection connection = Session.getConnection();
                ResultSet rs = Database_functions.callFunction(connection, "get_active_booking", room.getId(), client.getId());
                if (rs.next()) {
                    String bookingNumber = rs.getString(1);
                    if (bookingNumber != null) {
                        ServiceHistory newService = new ServiceHistory();
                        newService.setHistoryId(bookingNumber);
                        
                        UniversalFormConfig<ServiceHistory> formConfig = ConfigFactory.createServiceHistoryFormConfig(
                                this::saveAdditionalService,
                                sh -> refreshActiveTable(),
                                UniversalFormConfig.Mode.ADD
                        );
                        FormManager.showForm(formConfig, FormController.Mode.ADD, newService, getActiveTableController());
                    } else {
                        showError(statusLabel, "Не удалось найти активное бронирование для добавления услуги.");
                    }
                }
            } catch (Exception e) {
                showError(statusLabel, "Ошибка при добавлении услуги: " + e.getMessage());
            }
        } else {
            showError(statusLabel, "Пожалуйста, выберите комнату и клиента для добавления услуги.");
        }
        return null;
    }

    private Void handleEditAdditionalService(Object serviceObj) {
        if (!(serviceObj instanceof ServiceHistory service)) {
            showError(statusLabel, "Неверный тип данных для редактирования услуги.");
            return null;
        }
        UniversalFormConfig<ServiceHistory> formConfig = ConfigFactory.createServiceHistoryFormConfig(
                this::saveAdditionalService,
                sh -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, service, getActiveTableController());
        return null;
    }

    private Boolean saveAdditionalService(ServiceHistory service) {
        try {
            Connection connection = Session.getConnection();
            if (service.getId() == 0) { // Новая услуга
                Database_functions.callFunction(connection, "add_service_history",
                        service.getHistoryId(), service.getServiceId(), service.getAmount());
                showSuccess(statusLabel, "Услуга успешно добавлена.");
            } else { // Редактирование существующей
                Database_functions.callFunction(connection, "edit_service_history",
                        service.getId(), service.getHistoryId(), service.getServiceId(), service.getAmount());
                showSuccess(statusLabel, "Услуга успешно обновлена.");
            }
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения услуги: " + e.getMessage());
            return false;
        }
    }

    // Обработчик бронирования номера
    private Void handleBooking(Void param) {
        CheckInWizardController.startWizard(CheckInWizardController.WizardMode.BOOKING);
        return null;
    }

    // Обработчик формирования счетов
    private Void handleGenerateInvoices(Void param) {
        showSuccess(statusLabel, "Список счетов обновлен");
        refreshActiveTable();
        return null;
    }

    private Boolean saveBooking(TenantHistory booking) {
        try {
            Connection connection = Session.getConnection();

            // Для бронирования статус уже установлен в форме
            if (booking.getBookingNumber() == null || booking.getBookingNumber().isEmpty()) {
                Database_functions.callFunction(connection, "add_tenant_history",
                        booking.getTenantId(), booking.getRoomId(), booking.getBookingDate(),
                        booking.getCheckInDate(), booking.getCheckInStatus(),
                        booking.getOccupiedSpace(), booking.getAmountOfNights(), booking.isCanBeSplit());
                showSuccess(statusLabel, "Бронирование успешно создано");
            } else {
                Database_functions.callFunction(connection, "edit_tenant_history",
                        booking.getBookingNumber(), booking.getTenantId(), booking.getRoomId(),
                        booking.getBookingDate(), booking.getCheckInDate(), booking.getCheckInStatus(),
                        booking.getOccupiedSpace(), booking.getAmountOfNights(), booking.isCanBeSplit());
                showSuccess(statusLabel, "Бронирование успешно обновлено");
            }
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения бронирования: " + e.getMessage());
            return false;
        }
    }

    // Методы загрузки данных
    private Void handleToggleInvoiceStatus(Object invoiceObj) {
        if (invoiceObj instanceof Invoice invoice) {
            invoice.setPaid(!invoice.isPaid());
            UniversalTableController controller = getActiveTableController();
            if (controller != null) {
                controller.getTableView().refresh();
            }
            showSuccess(statusLabel, "Статус счета изменен (клиент)");
        }
        return null;
    }

    private ObservableList<Object> loadBookingsData(Map<String, Object> filters) {
        ObservableList<Object> bookings = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            
            String bookingNumberFilter = getStringFilter(filters, "bookingNumber");
            String checkInStatusFilter = getStringFilter(filters, "checkInStatus");
            // For Employee, we always filter by currentHotelId
            String roomInfo = getStringFilter(filters, "roomInfo");
            String tenantInfo = getStringFilter(filters, "tenantInfo");
            String bookingDate = getStringFilter(filters, "bookingDate");
            String checkInDate = getStringFilter(filters, "checkInDate");
            String occupiedSpace = getStringFilter(filters, "occupiedSpace");
            String amountOfNights = getStringFilter(filters, "amountOfNights");
            Boolean canBeSplit = getBooleanFilter(filters, "canBeSplit");
            Map.Entry<Integer, Integer> pagination = getOffsetLimitParams(filters);
            Integer offset = pagination.getKey();
            Integer limit = pagination.getValue();

            ResultSet rs = Database_functions.callFunctionWithPagination(connection, "get_tenant_history_by_hotel_filtered",
                    offset, limit, currentHotelId, bookingNumberFilter, roomInfo, tenantInfo,
                    bookingDate, checkInDate, checkInStatusFilter, occupiedSpace, amountOfNights, canBeSplit);

            while (rs.next()) {
                TenantHistory booking = new TenantHistory(
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
                booking.setHotelId(currentHotelId);
                bookings.add(booking);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки бронирований: " + e.getMessage());
        }
        return bookings;
    }

    private ObservableList<Object> loadInvoicesData(Map<String, Object> filters) {
        ObservableList<Object> invoices = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            
            String invoiceNumberFilter = getStringFilter(filters, "invoiceNumber");
            String bookingNumberFilter = getStringFilter(filters, "bookingNumber");
            String totalAmountFilter = getStringFilter(filters, "totalAmount");
            String issueDateFilter = getStringFilter(filters, "issueDate");
            Boolean isPaidFilter = getBooleanFilter(filters, "isPaid");
            Map.Entry<Integer, Integer> pagination = getOffsetLimitParams(filters);
            Integer offset = pagination.getKey();
            Integer limit = pagination.getValue();

            ResultSet rs = Database_functions.callFunctionWithPagination(connection, "get_daily_invoices_by_hotel_filtered",
                    offset, limit, currentHotelId, invoiceNumberFilter, bookingNumberFilter,
                    totalAmountFilter, issueDateFilter, isPaidFilter);

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getString("invoice_number"),
                        rs.getString("booking_number"),
                        rs.getBigDecimal("total_amount"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getBoolean("is_paid")
                );
                invoices.add(invoice);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки счетов: " + e.getMessage());
        }
        return invoices;
    }

    private ObservableList<Object> loadClientsData(Map<String, Object> filters) {
        ObservableList<Object> clients = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            
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
            Map.Entry<Integer, Integer> pagination = getPaginationParams(filters);
            Integer lastId = pagination.getKey();
            Integer limit = pagination.getValue();

            ResultSet rs = Database_functions.callFunctionWithPagination(connection, "get_all_tenants_filtered",
                    "tenant_id", lastId, limit, currentHotelId, firstNameFilter, nameFilter, patronymicFilter,
                    cityNameFilter, birthDateFilter, socialStatusFilter, seriesFilter, numberFilter, documentTypeFilter, emailFilter);

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
                tenant.setHotelId(currentHotelId);
                tenant.setSocialStatus(AllDictionaries.getSocialStatusNameMap().get(tenant.getSocialStatusId()));
                clients.add(tenant);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки клиентов: " + e.getMessage());
        }
        return clients;
    }

    private ObservableList<Object> loadBookingInfoData(Map<String, Object> filters) {
        ObservableList<Object> bookingInfo = FXCollections.observableArrayList();
        try {
            String bookingNumberFilter = getStringFilter(filters, "bookingNumber");
            String tenantNameFilter = getStringFilter(filters, "tenantName");
            String roomNumberFilter = getStringFilter(filters, "roomNumber");
            String checkInDateFilter = getStringFilter(filters, "checkInDate");
            String checkOutDateFilter = getStringFilter(filters, "checkOutDate");
            String statusFilter = getStringFilter(filters, "status");
            String totalCostFilter = getStringFilter(filters, "totalCost");
            Map.Entry<Integer, Integer> pagination = getPaginationParams(filters);
            Integer lastId = pagination.getKey();
            Integer limit = pagination.getValue();

            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_booking_details",
                    bookingNumberFilter, tenantNameFilter, roomNumberFilter, checkInDateFilter,
                    checkOutDateFilter, statusFilter, totalCostFilter, lastId, limit);

            while (rs.next()) {
                BookingInfo info = new BookingInfo(
                        rs.getString("booking_number"),
                        rs.getString("tenant_name"),
                        rs.getString("room_number"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getString("status"),
                        rs.getBigDecimal("total_cost")
                );
                bookingInfo.add(info);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки информации о бронировании: " + e.getMessage());
        }
        return bookingInfo;
    }

    private ObservableList<Object> loadAvailableRoomsData(Map<String, Object> filters) {
        ObservableList<Object> availableRooms = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            LocalDate checkInDate = (LocalDate) filters.get("checkInDate");
            LocalDate checkOutDate = (LocalDate) filters.get("checkOutDate");
            Integer numberOfPeople = (Integer) filters.getOrDefault("numberOfPeople", 1);

            String roomNumberFilter = getStringFilter(filters, "roomNumber");
            String roomTypeNameFilter = getStringFilter(filters, "roomType"); // Renamed from roomType to roomTypeName for consistency with DB function
            String maxPeopleFilter = getStringFilter(filters, "maxPeople");
            String pricePerPersonFilter = getStringFilter(filters, "pricePerNight"); // Mapped to pricePerPerson
            String statusFilter = getStringFilter(filters, "available"); // Mapped to status
            String availableSpaceFilter = getStringFilter(filters, "availableSpace");
            Map.Entry<Integer, Integer> pagination = getPaginationParams(filters);
            Integer lastId = pagination.getKey();
            Integer limit = pagination.getValue();

            if (checkInDate == null || checkOutDate == null) {
                rs = Database_functions.callFunction(connection, "get_current_room_statuses_view_filtered",
                        currentHotelId, roomNumberFilter, roomTypeNameFilter, maxPeopleFilter, pricePerPersonFilter, statusFilter, availableSpaceFilter, lastId, limit);
            } else {
                rs = Database_functions.callFunction(connection, "get_rooms_statuses_on_period_filtered",
                        currentHotelId, checkInDate, checkOutDate, numberOfPeople,
                        roomNumberFilter, roomTypeNameFilter, maxPeopleFilter, pricePerPersonFilter, statusFilter, availableSpaceFilter, lastId, limit);
            }

            while (rs.next()) {
                String status = rs.getString("status");
                boolean isAvailable = !status.equals("занят");

                AvailableRoom room = new AvailableRoom(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type_name"),
                        rs.getInt("max_people"),
                        rs.getBigDecimal("price_per_person"),
                        isAvailable,
                        rs.getInt("available_space")
                );
                availableRooms.add(room);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки свободных комнат: " + e.getMessage());
            e.printStackTrace();
        }
        return availableRooms;
    }

    private Void handleEditBooking(Object bookingObj) {
        if (!(bookingObj instanceof TenantHistory booking)) {
            showError(statusLabel, "Неверный тип данных для редактирования бронирования");
            return null;
        }
        UniversalFormConfig<TenantHistory> formConfig = ConfigFactory.createEmployeeBookingFormConfig(
                this::saveBooking,
                b -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, booking, getActiveTableController());
        return null;
    }

    private Void handleEditInvoice(Object invoiceObj) {
        if (!(invoiceObj instanceof Invoice selectedInvoice)) {
            showError(statusLabel, "Выберите счет для просмотра деталей.");
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/employee_panels/invoice_details.fxml"));
            Parent root = loader.load();

            InvoiceDetailsController controller = loader.getController();
            controller.loadInvoiceData(selectedInvoice);

            Stage stage = new Stage();
            stage.setTitle("Детали счета");
            stage.setScene(new Scene(root));
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.show();

        } catch (Exception e) {
            showError(statusLabel, "Не удалось открыть детали счета: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Void handleAddClient(Void param) {
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createEmployeeClientFormConfig(
                this::saveClient,
                client -> refreshActiveTable(),
                UniversalFormConfig.Mode.ADD
        );
        FormManager.showForm(formConfig, FormController.Mode.ADD, null, getActiveTableController());
        return null;
    }

    private Void handleEditClient(Object clientObj) {
        if (!(clientObj instanceof Tenant client)) {
            showError(statusLabel, "Неверный тип данных для редактирования клиента");
            return null;
        }
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createEmployeeClientFormConfig(
                this::saveClient,
                c -> refreshActiveTable(),
                UniversalFormConfig.Mode.EDIT
        );
        FormManager.showForm(formConfig, FormController.Mode.EDIT, client, getActiveTableController());
        return null;
    }

    private Boolean saveClient(Tenant client) {
        try {
            Connection connection = Session.getConnection();
            String doc_type = client.getDocumentType();
            Integer series = client.getSeries();
            Integer number = client.getNumber();

            if (client.getDocumentType().equals("Не указан")) {
                doc_type = null;
                series = null;
                number = null;
            }

            if (client.getId() == 0) {
                Database_functions.callFunction(connection, "add_tenant",
                        client.getFirstName(), client.getName(), client.getPatronymic(), client.getCityId(),
                        client.getBirthDate(), client.getSocialStatusId(), client.getEmail(),
                        series, number, doc_type);
                showSuccess(statusLabel, "Клиент успешно добавлен");
            } else {
                Database_functions.callFunction(connection, "edit_tenant",
                        client.getId(), client.getFirstName(), client.getName(), client.getPatronymic(),
                        client.getCityId(), client.getBirthDate(), client.getSocialStatusId(),
                        client.getEmail(), series, number, doc_type);
                showSuccess(statusLabel, "Клиент успешно обновлен");
            }
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения клиента: " + e.getMessage());
            return false;
        }
    }

    // Вспомогательные методы
    private void refreshActiveTable() {
        UniversalTableController controller = getActiveTableController();
        if (controller != null) {
            controller.handleRefresh();
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
            statusLabel.setText("Ошибка загрузки таблицы '" + tableName + "'");
            System.err.println("Ошибка загрузки таблицы: " + e.getMessage());
        }
    }

    // Обработчики меню (уже есть в FXML)
    @FXML
    private void showBookings() {
        openTableTab("Бронирования");
    }

    @FXML
    private void showPaymentInvoices() {
        openTableTab("Счета на оплату");
    }

    @FXML
    private void showClients() {
        openTableTab("Клиенты");
    }

    @FXML
    private void showRoomsStatus() {
        openTableTab("Свободные комнаты");
    }

    @FXML
    private void showAdditionalServices() {
        openTableTab("Дополнительные услуги");
    }

    // Обработчик выхода (уже есть в FXML)
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

    private Void handleCheckIn(Void param) {
        CheckInWizardController.startWizard(CheckInWizardController.WizardMode.CHECK_IN);
        return null;
    }

    private String getStringFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof String) ? (String) value : "";
    }

    private Boolean getBooleanFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof Boolean) ? (Boolean) value : null;
    }

    private Map.Entry<Integer, Integer> getPaginationParams(Map<String, Object> filters) {
        Integer lastId = (Integer) filters.getOrDefault("lastId", 0);
        Integer limit = (Integer) filters.getOrDefault("limit", 30);
        return new AbstractMap.SimpleEntry<>(lastId, limit);
    }

    private Map.Entry<Integer, Integer> getOffsetLimitParams(Map<String, Object> filters) {
        Integer page = (Integer) filters.getOrDefault("page", 0);
        Integer limit = (Integer) filters.getOrDefault("limit", 30);
        Integer offset = page * limit;
        return new AbstractMap.SimpleEntry<>(offset, limit);
    }
}
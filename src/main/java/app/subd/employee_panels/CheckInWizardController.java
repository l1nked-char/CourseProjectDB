package app.subd.employee_panels;

import app.subd.components.FormController;
import app.subd.components.Session;
import app.subd.config.*;
import app.subd.components.FormManager;
import app.subd.components.UniversalTableController;
import app.subd.Database_functions;
import app.subd.models.*;
import app.subd.tables.AllDictionaries;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static app.subd.MessageController.*;

public class CheckInWizardController {

    public enum WizardMode {
        CHECK_IN("Прямое заселение", "занят"),
        BOOKING("Бронирование номера", "забронирован");

        private final String title;
        private final String dbStatus;

        WizardMode(String title, String dbStatus) {
            this.title = title;
            this.dbStatus = dbStatus;
        }

        public String getTitle() {
            return title;
        }

        public String getDbStatus() {
            return dbStatus;
        }
    }

    @FXML private TabPane wizardTabPane;
    @FXML private Tab roomTab;
    @FXML private Tab confirmTab;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button cancelButton;
    @FXML private Button selectClientButton;
    @FXML private Button editClientButton;
    @FXML private Button selectRoomButton;
    @FXML private Label statusLabel;
    @FXML private Label stepLabel;
    @FXML private VBox clientTableContainer;
    @FXML private VBox roomTableContainer;
    @FXML private VBox confirmationInfo;

    private WizardMode mode;
    private final List<Tenant> selectedTenants = new ArrayList<>();
    private AvailableRoom selectedRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer peopleCount;
    private Integer currentHotelId;
    private CheckBox occupyRoomCheckBox;

    private UniversalTableController clientsController;
    private UniversalTableController roomsController;

    public void setMode(WizardMode mode) {
        this.mode = mode;
    }

    @FXML
    public void initialize() {
        try {
            AllDictionaries.initialiseSocialStatusMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadCurrentHotelId();
        setupWizardTabs();
        setupTableSelectionListeners();
        updateNavigation();
    }

    private void loadCurrentHotelId() {
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_current_hotel_id");
            if (rs.next()) {
                currentHotelId = rs.getInt(1);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки данных отеля: " + e.getMessage());
        }
    }

    private void setupWizardTabs() {
        try {
            FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/app/subd/tables/universal_table.fxml"));
            Parent clientContent = clientLoader.load();
            clientsController = clientLoader.getController();

            TableConfig clientConfig = ConfigFactory.createEmployeeClientsTableConfig(this::loadClientsData, null, null, true);
            clientsController.configure(clientConfig);

            hideTableButtons(clientsController);
            clientTableContainer.getChildren().add(clientContent);

            FXMLLoader roomLoader = new FXMLLoader(getClass().getResource("/app/subd/tables/universal_table.fxml"));
            Parent roomContent = roomLoader.load();
            roomsController = roomLoader.getController();
            TableConfig roomConfig = ConfigFactory.createAvailableRoomsTableConfig(this::loadAvailableRoomsData);
            roomsController.configure(roomConfig);

            hideTableButtons(roomsController);
            occupyRoomCheckBox = new CheckBox("Занять номер целиком (без подселения)");
            occupyRoomCheckBox.setStyle("-fx-padding: 5 0 10 5;");
            roomTableContainer.getChildren().addFirst(occupyRoomCheckBox);
            roomTableContainer.getChildren().add(roomContent);
        } catch (Exception e) {
            showError(statusLabel, "Ошибка инициализации мастера заселения: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void hideTableButtons(UniversalTableController controller) {
        javafx.scene.Node addButton = controller.getAddButton();
        javafx.scene.Node editButton = controller.getEditButton();
        javafx.scene.Node deleteButton = controller.getDeleteButton();

        if (addButton != null) addButton.setVisible(false);
        if (editButton != null) editButton.setVisible(false);
        if (deleteButton != null) deleteButton.setVisible(false);
    }

    private void setupTableSelectionListeners() {
        if (clientsController != null) {
            clientsController.getTableView().getSelectionModel().getSelectedItems().addListener((ListChangeListener<Object>) c -> {
                int selectedCount = c.getList().size();
                selectClientButton.setDisable(selectedCount == 0);
                editClientButton.setDisable(selectedCount != 1);
            });
        }

        if (roomsController != null) {
            roomsController.getTableView().getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldValue, newValue) -> selectRoomButton.setDisable(newValue == null)
            );
        }
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
                    "tenant_id", lastId, limit, firstNameFilter, nameFilter, patronymicFilter,
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

    private ObservableList<Object> loadAvailableRoomsData(Map<String, Object> filters) {
        ObservableList<Object> availableRooms = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            LocalDate checkInDate = (LocalDate) filters.get("checkInDate");
            LocalDate checkOutDate = (LocalDate) filters.get("checkOutDate");

            String roomNumberFilter = getStringFilter(filters, "roomNumber");
            String roomTypeNameFilter = getStringFilter(filters, "roomType");
            Integer maxPeopleFilter = getIntFilter(filters, "maxPeople");
            BigDecimal pricePerPersonFilter = getNumericFilter(filters, "pricePerNight");
            String statusFilter = getStringFilter(filters, "available");
            Integer availableSpaceFilter = getIntFilter(filters, "availableSpace");

            if (checkInDate == null || checkOutDate == null) {
                rs = Database_functions.callFunction(connection, "get_current_room_statuses_view_filtered",
                        currentHotelId, roomNumberFilter, roomTypeNameFilter, maxPeopleFilter,
                        pricePerPersonFilter, statusFilter, availableSpaceFilter);
            } else {
                rs = Database_functions.callFunction(connection, "get_rooms_statuses_on_period_filtered",
                        currentHotelId, checkInDate, checkOutDate, roomNumberFilter, roomTypeNameFilter,
                        maxPeopleFilter, pricePerPersonFilter, statusFilter, availableSpaceFilter);
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

    @FXML
    private void handleAddClientInWizard() {
        boolean documentsRequired = (this.mode == WizardMode.CHECK_IN);
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createEmployeeClientFormConfig(
                this::saveClientInWizard,
                client -> {
                    clientsController.handleRefresh();
                    showSuccess(statusLabel, "Клиент добавлен. Теперь выберите его из списка.");
                },
                UniversalFormConfig.Mode.ADD,
                documentsRequired
        );

        FormManager.showForm(formConfig, FormController.Mode.ADD, null, clientsController);
    }

    @FXML
    private void handleEditClientInWizard() {
        Object selected = clientsController.getSelectedItem();
        if (!(selected instanceof Tenant clientToEdit)) {
            showError(statusLabel, "Выберите клиента для редактирования");
            return;
        }

        boolean documentsRequired = (this.mode == WizardMode.CHECK_IN);
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createEmployeeClientFormConfig(
                this::saveEditedClientInWizard,
                updatedClient -> {
                    clientsController.handleRefresh();
                    showSuccess(statusLabel, "Данные клиента обновлены");

                    // Обновляем клиента в списке, если он был выбран
                    for (int i = 0; i < selectedTenants.size(); i++) {
                        if (selectedTenants.get(i).getId() == updatedClient.getId()) {
                            selectedTenants.set(i, updatedClient);
                            break;
                        }
                    }
                },
                UniversalFormConfig.Mode.EDIT,
                documentsRequired
        );

        FormManager.showForm(formConfig, FormController.Mode.EDIT, clientToEdit, clientsController);
    }

    @FXML
    private void handleSelectClient() {
        this.selectedTenants.clear();
        ObservableList<Object> selectedItems = clientsController.getTableView().getSelectionModel().getSelectedItems();
        for (Object item : selectedItems) {
            if (item instanceof Tenant) {
                this.selectedTenants.add((Tenant) item);
            }
        }

        if (!this.selectedTenants.isEmpty()) {
            showSuccess(statusLabel, "Выбрано клиентов: " + this.selectedTenants.size());
            roomTab.setDisable(false);
            updateNavigation();
        } else {
            showError(statusLabel, "Клиенты не выбраны");
        }
    }

    @FXML
    private void handleSelectRoom() {
        Object selected = roomsController.getSelectedItem();
        if (selected instanceof AvailableRoom room) {
            Map<String, Object> filters = roomsController.getCurrentFilterValues();
            this.checkInDate = (LocalDate) filters.get("checkInDate");
            this.checkOutDate = (LocalDate) filters.get("checkOutDate");

            if (this.checkInDate == null || this.checkOutDate == null) {
                showError(statusLabel, "Пожалуйста, выберите дату заезда и выезда в фильтрах.");
                return;
            }

            // Проверка на пересечение бронирований
            List<String> conflictingTenants = new ArrayList<>();
            for (Tenant tenant : selectedTenants) {
                try {
                    Connection connection = Session.getConnection();
                    ResultSet rs = Database_functions.callFunction(connection, "check_tenant_booking_overlap",
                            tenant.getId(),
                            java.sql.Date.valueOf(checkInDate),
                            java.sql.Date.valueOf(checkOutDate));
                    if (rs.next()) {
                        conflictingTenants.add(String.format("%s %s (бронь %s)",
                                tenant.getFirstName(), tenant.getName(), rs.getString("booking_number")));
                    }
                } catch (Exception e) {
                    showError(statusLabel, "Ошибка проверки пересекающихся бронирований: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }

            if (!conflictingTenants.isEmpty()) {
                showError(statusLabel, "У следующих клиентов есть пересекающиеся бронирования: " + String.join(", ", conflictingTenants));
                return;
            }

            peopleCount = selectedTenants.size();

            if (peopleCount > room.getAvailableSpace()) {
                showError(statusLabel, "В комнате недостаточно места для " + peopleCount + " гостей.");
                return;
            }

            this.selectedRoom = room;
            showSuccess(statusLabel, "Комната выбрана: №" + selectedRoom.getRoomNumber());

            // Активируем следующую вкладку
            confirmTab.setDisable(false);
            updateConfirmationInfo();
            updateNavigation();
        }
    }

    private Boolean saveClientInWizard(Tenant client) {
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

            Database_functions.callFunction(connection, "add_tenant",
                    client.getFirstName(), client.getName(), client.getPatronymic(), client.getCityId(),
                    client.getBirthDate(), client.getSocialStatusId(), client.getEmail(),
                    series, number, doc_type);

            showSuccess(statusLabel, "Клиент успешно добавлен");
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения клиента: " + e.getMessage());
            return false;
        }
    }

    private Boolean saveEditedClientInWizard(Tenant client) {
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

            Database_functions.callFunction(connection, "edit_tenant",
                    client.getId(), client.getFirstName(), client.getName(), client.getPatronymic(),
                    client.getCityId(), client.getBirthDate(), client.getSocialStatusId(),
                    client.getEmail(), series, number, doc_type);

            showSuccess(statusLabel, "Данные клиента обновлены");
            return true;
        } catch (Exception e) {
            showError(statusLabel, "Ошибка обновления клиента: " + e.getMessage());
            return false;
        }
    }

    private void updateConfirmationInfo() {
        confirmationInfo.getChildren().clear();
        confirmationInfo.setStyle("-fx-padding: 10;");

        HBox mainLayout = new HBox(20);

        // --- Левая панель: Список клиентов ---
        VBox clientsBox = new VBox(10);
        clientsBox.setMinWidth(300);
        Label clientsTitle = new Label("Выбранные клиенты:");
        clientsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<String> clientsListView = new ListView<>();
        if (!selectedTenants.isEmpty()) {
            ObservableList<String> tenantNames = FXCollections.observableArrayList();
            for (Tenant tenant : selectedTenants) {
                tenantNames.add(String.format("%s %s %s (%s)",
                        tenant.getFirstName(), tenant.getName(), tenant.getPatronymic(), tenant.getBirthDate()));
            }
            clientsListView.setItems(tenantNames);
        }
        clientsBox.getChildren().addAll(clientsTitle, clientsListView);

        // --- Правая панель: Информация о номере и стоимость ---
        VBox detailsBox = new VBox(10);

        if (selectedRoom != null) {
            Label roomTitle = new Label("Данные комнаты:");
            roomTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label roomNumber = new Label("Номер комнаты: " + selectedRoom.getRoomNumber());
            Label roomType = new Label("Тип комнаты: " + selectedRoom.getRoomType());
            Label maxPeople = new Label("Максимум людей: " + selectedRoom.getMaxPeople());
            Label pricePerNight = new Label("Базовая цена за ночь (за 1 чел): " + selectedRoom.getPricePerNight() + " руб.");
            detailsBox.getChildren().addAll(roomTitle, roomNumber, roomType, maxPeople, pricePerNight);
        }

        if (checkInDate != null && checkOutDate != null && selectedRoom != null && peopleCount != null && peopleCount > 0) {
            Label bookingTitle = new Label("Детали проживания:");
            bookingTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");

            Label checkInLabel = new Label("Дата заезда: " + checkInDate);
            Label checkOutLabel = new Label("Дата выезда: " + checkOutDate);
            long nightsCount = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            Label nightsLabel = new Label("Количество ночей: " + nightsCount);
            Label peopleLabel = new Label("Количество людей: " + peopleCount);

            BigDecimal totalCost = java.math.BigDecimal.ZERO;
            try {
                Connection connection = Session.getConnection();
                ResultSet rs = Database_functions.callFunction(connection, "calculate_booking_cost",
                        selectedRoom.getRoomId(),
                        checkInDate,
                        checkOutDate,
                        peopleCount,
                        occupyRoomCheckBox.isSelected());
                while (rs.next())
                    totalCost = rs.getBigDecimal(1);
            } catch (Exception e) {
                showError(statusLabel, "Ошибка расчета стоимости: " + e.getMessage());
            }

            Label costLabel = new Label("Общая стоимость: " + totalCost + " руб.");
            costLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e74c3c;");

            detailsBox.getChildren().addAll(bookingTitle, checkInLabel, checkOutLabel, nightsLabel, peopleLabel, new Separator(), costLabel);
        }

        mainLayout.getChildren().addAll(clientsBox, new Separator(Orientation.VERTICAL), detailsBox);
        confirmationInfo.getChildren().add(mainLayout);
    }

    @FXML
    private void handleNext() {
        int currentIndex = wizardTabPane.getSelectionModel().getSelectedIndex();
        if (currentIndex < wizardTabPane.getTabs().size() - 1) {
            wizardTabPane.getSelectionModel().select(currentIndex + 1);
            updateNavigation();
        }
    }

    @FXML
    private void handlePrev() {
        int currentIndex = wizardTabPane.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            wizardTabPane.getSelectionModel().select(currentIndex - 1);
            updateNavigation();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleConfirmCheckIn() {
        Connection connection = null;
        if (selectedTenants.isEmpty() || selectedRoom == null || checkInDate == null || checkOutDate == null) {
            showError(statusLabel, "Заполните все данные для оформления");
            return;
        }

        try {
            connection = Session.getConnection();
            long nightsCount = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (nightsCount <= 0) {
                showError(statusLabel, "Некорректный период проживания");
                return;
            }

            boolean canBeSplit = !occupyRoomCheckBox.isSelected();
            int totalTenants = selectedTenants.size();
            int spaceTakenByPreviousTenants = 0;

            connection.setAutoCommit(false);

            for (int i = 0; i < totalTenants; i++) {
                Tenant currentTenant = selectedTenants.get(i);
                int occupiedSpace;

                if (occupyRoomCheckBox.isSelected() && i == totalTenants - 1) {
                    occupiedSpace = selectedRoom.getMaxPeople() - spaceTakenByPreviousTenants;
                } else {
                    occupiedSpace = 1;
                }
                spaceTakenByPreviousTenants++;

                Database_functions.callFunction(connection, "add_tenant_history",
                        currentTenant.getId(),
                        selectedRoom.getRoomId(),
                        java.sql.Date.valueOf(LocalDate.now()),
                        java.sql.Date.valueOf(checkInDate),
                        mode.getDbStatus(),
                        occupiedSpace,
                        (int) nightsCount,
                        canBeSplit
                );
            }

            showSuccess(statusLabel, mode.getTitle() + " успешно оформлено для " + totalTenants + " гостей!");

            connection.commit();

            Stage stage = (Stage) wizardTabPane.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {

                }
            }
            showError(statusLabel, "Ошибка оформления: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateNavigation() {
        int currentIndex = wizardTabPane.getSelectionModel().getSelectedIndex();

        String[] stepNames = {"Выбор клиента", "Выбор комнаты", "Подтверждение"};
        stepLabel.setText("Шаг " + (currentIndex + 1) + " из 3: " + stepNames[currentIndex]);

        prevButton.setDisable(currentIndex == 0);
        nextButton.setDisable(true);

        switch (currentIndex) {
            case 0:
                nextButton.setDisable(selectedTenants.isEmpty());
                break;
            case 1:
                nextButton.setDisable(selectedRoom == null);
                break;
            case 2:
                nextButton.setDisable(true);
                break;
        }

        if (currentIndex == 2) {
            updateConfirmationInfo();
        }
    }

    public static void startWizard(WizardMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(CheckInWizardController.class.getResource("/app/subd/employee_panels/checkin_wizard.fxml"));
            Parent root = loader.load();

            CheckInWizardController controller = loader.getController();
            controller.setMode(mode);

            Stage stage = new Stage();
            stage.setTitle("Мастер - " + mode.getTitle());
            stage.setScene(new Scene(root, 900, 700));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Не удалось открыть мастер: " + e.getMessage());
        }
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getStringFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof String) ? (String) value : "";
    }

    private Integer getIntFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof String) ? Integer.parseInt((String) value) : null;
    }

    private BigDecimal getNumericFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        return (value instanceof BigDecimal) ? new BigDecimal((String) value) : null;
    }

    private Map.Entry<Integer, Integer> getPaginationParams(Map<String, Object> filters) {
        Integer lastId = (Integer) filters.getOrDefault("lastId", 0);
        Integer limit = (Integer) filters.getOrDefault("limit", 30);
        return new AbstractMap.SimpleEntry<>(lastId, limit);
    }
}
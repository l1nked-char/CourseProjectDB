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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static app.subd.MessageController.*;

public class CheckInWizardController {

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

    // Данные процесса заселения
    private Tenant currentTenant;
    private AvailableRoom selectedRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer peopleCount;
    private Integer currentHotelId;

    // Контроллеры таблиц
    private UniversalTableController clientsController;
    private UniversalTableController roomsController;

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
            // Настраиваем таблицу клиентов
            FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/app/subd/tables/universal_table.fxml"));
            Parent clientContent = clientLoader.load();
            clientsController = clientLoader.getController();

            // Специальная конфигурация без стандартных кнопок действий
            TableConfig clientConfig = createClientSelectionConfig();
            clientsController.configure(clientConfig);

            // Скрываем стандартные кнопки таблицы
            hideTableButtons(clientsController);

            clientTableContainer.getChildren().add(clientContent);

            // Настраиваем таблицу комнат
            FXMLLoader roomLoader = new FXMLLoader(getClass().getResource("/app/subd/tables/universal_table.fxml"));
            Parent roomContent = roomLoader.load();
            roomsController = roomLoader.getController();

            TableConfig roomConfig = createRoomSelectionConfig();
            roomsController.configure(roomConfig);

            // Скрываем стандартные кнопки таблицы
            hideTableButtons(roomsController);

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
        // Слушатель выбора в таблице клиентов
        if (clientsController != null) {
            clientsController.getTableView().getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldValue, newValue) -> {
                        boolean hasSelection = newValue != null;
                        selectClientButton.setDisable(!hasSelection);
                        editClientButton.setDisable(!hasSelection);
                    }
            );
        }

        // Слушатель выбора в таблице комнат
        if (roomsController != null) {
            roomsController.getTableView().getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldValue, newValue) -> {
                        selectRoomButton.setDisable(newValue == null);
                    }
            );
        }
    }

    private TableConfig createClientSelectionConfig() {
        return new TableConfig("Клиенты",
                this::loadClientsForCheckIn,
                null, // Убираем стандартные обработчики
                null,
                null,
                Arrays.asList(
                        new ColumnConfig("firstName", "Фамилия", 120),
                        new ColumnConfig("name", "Имя", 120),
                        new ColumnConfig("patronymic", "Отчество", 120),
                        new ColumnConfig("birthDate", "Дата рождения", 110),
                        new ColumnConfig("passport", "Паспорт", 120),
                        new ColumnConfig("socialStatus", "Соц. статус", 120),
                        new ColumnConfig("email", "Email", 150),
                        new ColumnConfig("documentType", "Тип документа", 130)
                ),
                null,
                null
        );
    }

    private TableConfig createRoomSelectionConfig() {
        return new TableConfig("Доступные комнаты",
                this::loadAvailableRoomsForCheckIn,
                null,
                null,
                null,
                Arrays.asList(
                        new ColumnConfig("roomNumber", "Номер", 80),
                        new ColumnConfig("roomType", "Тип комнаты", 150),
                        new ColumnConfig("maxPeople", "Макс. людей", 100),
                        new ColumnConfig("pricePerNight", "Цена за ночь", 120),
                        new ColumnConfig("available", "Доступна", 80),
                        new ColumnConfig("availableSpace", "Свободных мест", 150)
                ),
                Arrays.asList(
                        new FilterConfig("checkInDate", "Дата заезда", FilterConfig.FilterType.DATE, true),
                        new FilterConfig("checkOutDate", "Дата выезда", FilterConfig.FilterType.DATE, true),
                        new FilterConfig("peopleCount", "Количество людей", FilterConfig.FilterType.NUMBER, false)
                ),
                null
        );
    }

    private ObservableList<Object> loadClientsForCheckIn(Map<String, Object> filters) {
        ObservableList<Object> clients = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_tenants");

            while (rs.next()) {
                int status_id = rs.getInt("social_status_id");
                Tenant tenant = new Tenant(
                        rs.getInt("tenant_id"),
                        rs.getString("first_name"),
                        rs.getString("name"),
                        rs.getString("patronymic"),
                        rs.getInt("city_id"),
                        status_id,
                        rs.getInt("series"),
                        rs.getInt("number"),
                        DocumentType.getDocumentType(rs.getString("document_type")),
                        rs.getString("email")
                );
                tenant.setBirthDate(rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null);
                tenant.setSocialStatus(AllDictionaries.getSocialStatusNameMap().get(status_id));
                clients.add(tenant);
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки клиентов: " + e.getMessage());
            e.printStackTrace();
        }
        return clients;
    }

    private ObservableList<Object> loadAvailableRoomsForCheckIn(Map<String, Object> filters) {
        ObservableList<Object> rooms = FXCollections.observableArrayList();

        // Получаем параметры из фильтров
        checkInDate = (LocalDate) filters.get("checkInDate");
        checkOutDate = (LocalDate) filters.get("checkOutDate");
        peopleCount = (Integer) filters.get("peopleCount");

        if (checkInDate == null || checkOutDate == null) {
            showInfo(statusLabel, "Укажите даты заезда и выезда для поиска комнат");
            return rooms;
        }

        if (checkInDate.isAfter(checkOutDate)) {
            showError(statusLabel, "Дата заезда не может быть позже даты выезда");
            return rooms;
        }

        try {
            Connection connection = Session.getConnection();
            ResultSet rs;

            if (peopleCount != null) {
                // Вызываем функцию с фильтрацией по количеству людей
                rs = Database_functions.callFunction(connection, "get_rooms_statuses_on_period",
                        currentHotelId, checkInDate, checkOutDate, peopleCount);
            } else {
                // Вызываем функцию без фильтрации по количеству людей
                rs = Database_functions.callFunction(connection, "get_rooms_statuses_on_period",
                        currentHotelId, checkInDate, checkOutDate);
            }

            while (rs.next()) {
                String status = rs.getString("status");
                boolean isAvailable = !"занят".equals(status);

                AvailableRoom room = new AvailableRoom(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type_name"),
                        rs.getInt("max_people"),
                        rs.getBigDecimal("price_per_person"),
                        isAvailable,
                        rs.getInt("available_space")
                );

                if (isAvailable) {
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка загрузки доступных комнат: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    @FXML
    private void handleAddClientInWizard() {
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createEmployeeClientFormConfig(
                this::saveClientInWizard,
                client -> {
                    this.currentTenant = client;
                    clientsController.refreshData();
                    showSuccess(statusLabel, "Клиент добавлен. Теперь выберите его из списка.");
                },
                UniversalFormConfig.Mode.ADD
        );

        FormManager.showForm(formConfig, FormController.Mode.ADD, null, clientsController);
    }

    @FXML
    private void handleEditClientInWizard() {
        Object selected = clientsController.getSelectedItem();
        if (!(selected instanceof Tenant)) {
            showError(statusLabel, "Выберите клиента для редактирования");
            return;
        }

        Tenant clientToEdit = (Tenant) selected;
        UniversalFormConfig<Tenant> formConfig = ConfigFactory.createEmployeeClientFormConfig(
                this::saveEditedClientInWizard,
                updatedClient -> {
                    clientsController.refreshData();
                    showSuccess(statusLabel, "Данные клиента обновлены");

                    // Если редактировали выбранного клиента, обновляем текущего
                    if (currentTenant != null && currentTenant.getId() == updatedClient.getId()) {
                        currentTenant = updatedClient;
                    }
                },
                UniversalFormConfig.Mode.EDIT
        );

        FormManager.showForm(formConfig, FormController.Mode.EDIT, clientToEdit, clientsController);
    }

    @FXML
    private void handleSelectClient() {
        Object selected = clientsController.getSelectedItem();
        if (selected instanceof Tenant) {
            this.currentTenant = (Tenant) selected;
            showSuccess(statusLabel, "Клиент выбран: " + currentTenant.getFirstName() + " " + currentTenant.getName());

            // Активируем следующую вкладку
            roomTab.setDisable(false);
            updateNavigation();
        }
    }

    @FXML
    private void handleSelectRoom() {
        Object selected = roomsController.getSelectedItem();
        if (selected instanceof AvailableRoom) {
            this.selectedRoom = (AvailableRoom) selected;
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

        if (currentTenant != null) {
            Label clientTitle = new Label("Данные клиента:");
            clientTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            Label clientName = new Label("ФИО: " + currentTenant.getFirstName() + " " +
                    currentTenant.getName() + " " + currentTenant.getPatronymic());
            Label clientPassport = new Label("Паспорт: " + currentTenant.getPassport());
            Label clientEmail = new Label("Email: " + currentTenant.getEmail());
            Label clientBirthDate = new Label("Дата рождения: " + currentTenant.getBirthDate());
            Label clientSocialStatus = new Label("Социальный статус: " + currentTenant.getSocialStatus());
            Label clientDocumentType = new Label("Тип документа: " + currentTenant.getDocumentType());

            VBox clientBox = new VBox(5, clientTitle, clientName, clientPassport, clientEmail,
                    clientBirthDate, clientSocialStatus, clientDocumentType);
            clientBox.setStyle("-fx-padding: 0 0 10 0;");
            confirmationInfo.getChildren().add(clientBox);
        }

        if (selectedRoom != null) {
            Label roomTitle = new Label("Данные комнаты:");
            roomTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            Label roomNumber = new Label("Номер комнаты: " + selectedRoom.getRoomNumber());
            Label roomType = new Label("Тип комнаты: " + selectedRoom.getRoomType());
            Label maxPeople = new Label("Максимум людей: " + selectedRoom.getMaxPeople());
            Label pricePerNight = new Label("Цена за ночь: " + selectedRoom.getPricePerNight() + " руб.");

            VBox roomBox = new VBox(5, roomTitle, roomNumber, roomType, maxPeople, pricePerNight);
            roomBox.setStyle("-fx-padding: 0 0 10 0;");
            confirmationInfo.getChildren().add(roomBox);
        }

        if (checkInDate != null && checkOutDate != null && selectedRoom != null) {
            Label bookingTitle = new Label("Детали проживания:");
            bookingTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            Label checkInLabel = new Label("Дата заезда: " + checkInDate);
            Label checkOutLabel = new Label("Дата выезда: " + checkOutDate);

            long nightsCount = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            Label nightsLabel = new Label("Количество ночей: " + nightsCount);

            if (peopleCount != null) {
                Label peopleLabel = new Label("Количество людей: " + peopleCount);

                // Расчет стоимости
                java.math.BigDecimal totalCost = selectedRoom.getPricePerNight()
                        .multiply(java.math.BigDecimal.valueOf(peopleCount))
                        .multiply(java.math.BigDecimal.valueOf(nightsCount));
                Label costLabel = new Label("Общая стоимость: " + totalCost + " руб.");
                costLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e74c3c;");

                VBox bookingBox = new VBox(5, bookingTitle, checkInLabel, checkOutLabel,
                        nightsLabel, peopleLabel, costLabel);
                confirmationInfo.getChildren().add(bookingBox);
            } else {
                // Расчет стоимости без учета количества людей (минимальная стоимость)
                java.math.BigDecimal minCost = selectedRoom.getPricePerNight()
                        .multiply(java.math.BigDecimal.valueOf(nightsCount));
                Label costLabel = new Label("Минимальная стоимость: " + minCost + " руб.");
                costLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e74c3c;");

                VBox bookingBox = new VBox(5, bookingTitle, checkInLabel, checkOutLabel,
                        nightsLabel, costLabel);
                confirmationInfo.getChildren().add(bookingBox);
            }
        }
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
        if (currentTenant == null || selectedRoom == null || checkInDate == null || checkOutDate == null) {
            showError(statusLabel, "Заполните все данные для заселения");
            return;
        }

        try {
            Connection connection = Session.getConnection();

            // Рассчитываем количество ночей
            long nightsCount = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (nightsCount <= 0) {
                showError(statusLabel, "Некорректный период проживания");
                return;
            }

            // Если количество людей не указано, используем 1 по умолчанию
            int occupiedSpace = (peopleCount != null) ? peopleCount : 1;

            // Создаем бронирование (заселение) со статусом "занят"
            Database_functions.callFunction(connection, "add_tenant_history_direct_checkin",
                    currentTenant.getId(),
                    selectedRoom.getRoomId(),
                    java.sql.Date.valueOf(LocalDate.now()), // Дата бронирования = сегодня
                    java.sql.Date.valueOf(checkInDate),     // Дата заселения
                    "занят",                               // Статус - занят
                    occupiedSpace,
                    (int) nightsCount,
                    false                                  // canBeSplit
            );

            showSuccess(statusLabel, "Заселение успешно оформлено!");

            // Закрываем окно мастера
            Stage stage = (Stage) wizardTabPane.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showError(statusLabel, "Ошибка оформления заселения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateNavigation() {
        int currentIndex = wizardTabPane.getSelectionModel().getSelectedIndex();

        // Обновляем текст шага
        String[] stepNames = {"Выбор клиента", "Выбор комнаты", "Подтверждение"};
        stepLabel.setText("Шаг " + (currentIndex + 1) + " из 3: " + stepNames[currentIndex]);

        // Управление кнопками
        prevButton.setDisable(currentIndex == 0);
        nextButton.setDisable(true); // По умолчанию отключена

        // Активируем кнопку "Далее" только при выполнении условий
        switch (currentIndex) {
            case 0: // Выбор клиента
                nextButton.setDisable(currentTenant == null);
                break;
            case 1: // Выбор комнаты
                nextButton.setDisable(selectedRoom == null);
                break;
            case 2: // Подтверждение
                nextButton.setDisable(true);
                break;
        }

        // Обновляем подтверждение на последнем шаге
        if (currentIndex == 2) {
            updateConfirmationInfo();
        }
    }

    // Статический метод для запуска мастера заселения
    public static void startCheckInWizard() {
        try {
            FXMLLoader loader = new FXMLLoader(CheckInWizardController.class.getResource("/app/subd/employee_panels/checkin_wizard.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Мастер заселения - Прямое заселение");
            stage.setScene(new Scene(root, 900, 700));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть мастер заселения: " + e.getMessage());
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Геттер для таблицы
    public UniversalTableController getClientsController() {
        return clientsController;
    }

    public UniversalTableController getRoomsController() {
        return roomsController;
    }
}
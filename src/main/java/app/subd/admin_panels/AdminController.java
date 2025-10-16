package app.subd.admin_panels;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.components.UniversalTableController;
import app.subd.config.TableConfig;
import app.subd.config.ColumnConfig;
import app.subd.config.FilterConfig;
import app.subd.models.Hotel;
import app.subd.models.Room;
import app.subd.tables.*;
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
import java.util.Arrays;
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
        tableConfigs.put("Отели", createHotelConfig());
        tableConfigs.put("Номера", createRoomConfig());
    }

    private TableConfig createHotelConfig() {
        return new TableConfig(
                "Отели",
                this::loadHotelsData,
                this::handleAddHotel,
                this::handleEditHotel,
                this::handleRefreshHotels,
                Arrays.asList(
                        new ColumnConfig("id", "ID", 80),
                        new ColumnConfig("cityName", "Город", 150),
                        new ColumnConfig("address", "Адрес", 250)
                ),
                null
        );
    }

    private TableConfig createRoomConfig() {
        return new TableConfig(
                "Номера",
                this::loadRoomsData,
                this::handleAddRoom,
                this::handleEditRoom,
                this::handleRefreshRooms,
                Arrays.asList(
                        new ColumnConfig("id", "ID", 80),
                        new ColumnConfig("hotelInfo", "Отель", 200),
                        new ColumnConfig("roomNumber", "Номер комнаты", 120),
                        new ColumnConfig("maxPeople", "Макс. людей", 100),
                        new ColumnConfig("pricePerPerson", "Цена за человека", 120),
                        new ColumnConfig("typeOfRoomName", "Тип комнаты", 150)
                ),
                Arrays.asList(
                        new FilterConfig(
                                "hotel",
                                "Отель",
                                () -> {
                                    try {
                                        AllDictionaries.initialiseHotelsMaps();
                                        return FXCollections.observableArrayList(AllDictionaries.getHotelsIdMap().keySet());
                                    } catch (Exception e) {
                                        return FXCollections.observableArrayList();
                                    }
                                }
                        )
                )
        );
    }

    // Методы загрузки данных для отелей
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

    // Методы загрузки данных для комнат
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

    // Обработчики для отелей
    private Void handleAddHotel(Void param) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_hotel.fxml"));
            Stage stage = showForm("Добавление отеля", loader);
            AddHotelController controller = loader.getController();
            controller.setParentController(new RefreshableController() {
                @Override
                public void handleRefresh() {
                    refreshActiveTable();
                }
            });
            stage.showAndWait();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления отеля: " + e.getMessage());
        }
        return null;
    }

    private Void handleEditHotel(Object hotelObj) {
        if (!(hotelObj instanceof Hotel)) {
            showError(statusLabel, "Неверный тип данных для редактирования отеля");
            return null;
        }

        Hotel hotel = (Hotel) hotelObj;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_hotel.fxml"));
            Stage stage = showForm("Редактирование отеля: ID " + hotel.getId(), loader);
            EditHotelController controller = loader.getController();
            controller.setHotel(hotel);
            controller.setParentController(new RefreshableController() {
                @Override
                public void handleRefresh() {
                    refreshActiveTable();
                }
            });
            stage.showAndWait();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования отеля: " + e.getMessage());
        }
        return null;
    }

    private Void handleRefreshHotels(Void param) {
        refreshActiveTable();
        return null;
    }

    // Обработчики для комнат
    private Void handleAddRoom(Void param) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/add_room.fxml"));
            Stage stage = showForm("Добавление комнаты", loader);
            AddRoomController controller = loader.getController();
            controller.setParentController(new RefreshableController() {
                @Override
                public void handleRefresh() {
                    refreshActiveTable();
                }
            });

            // Получаем выбранный отель из активной таблицы
            Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getContent() != null) {
                UniversalTableController tableController = (UniversalTableController) activeTab.getContent()
                        .getProperties().get("controller");
                if (tableController != null) {
                    Map<String, Object> filters = getCurrentFiltersFromController(tableController);
                    String selectedHotel = (String) filters.get("hotel");
                    if (selectedHotel != null) {
                        controller.setHotelId(AllDictionaries.getHotelsIdMap().get(selectedHotel));
                    }
                }
            }

            stage.showAndWait();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы добавления комнаты: " + e.getMessage());
        }
        return null;
    }

    private Void handleEditRoom(Object roomObj) {
        if (!(roomObj instanceof Room)) {
            showError(statusLabel, "Неверный тип данных для редактирования комнаты");
            return null;
        }

        Room room = (Room) roomObj;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/edit_room.fxml"));
            Stage stage = showForm("Редактирование комнаты: ID " + room.getId(), loader);
            EditRoomController controller = loader.getController();
            controller.setRoom(room);
            controller.setParentController(new RefreshableController() {
                @Override
                public void handleRefresh() {
                    refreshActiveTable();
                }
            });
            stage.showAndWait();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы редактирования комнаты: " + e.getMessage());
        }
        return null;
    }

    private Void handleRefreshRooms(Void param) {
        refreshActiveTable();
        return null;
    }

    // Вспомогательные методы
    private void refreshActiveTable() {
        Tab activeTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (activeTab != null && activeTab.getContent() != null) {
            UniversalTableController tableController = (UniversalTableController) activeTab.getContent()
                    .getProperties().get("controller");
            if (tableController != null) {
                tableController.refreshData();
            }
        }
    }

    private Map<String, Object> getCurrentFiltersFromController(UniversalTableController controller) {
        try {
            java.lang.reflect.Field field = UniversalTableController.class.getDeclaredField("currentFilterValues");
            field.setAccessible(true);
            return (Map<String, Object>) field.get(controller);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @FXML
    private void openTableTab(String tableName) {
        try {
            // Проверяем, не открыта ли уже вкладка
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

            // Сохраняем контроллер в свойствах контента для доступа later
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

    // Методы показа форм (остаются без изменений)
    private Stage showForm(String title, FXMLLoader loader) {
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            return stage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Существующие методы управления вкладками
    @FXML private void showHotelManagement() { openTableTab("Отели"); }
    @FXML private void showRoomManagement() { openTableTab("Номера"); }
    @FXML private void showUserManagement() { openTableTab("Пользователи"); }
    @FXML private void showTypeOfRoomManagement() { openTableTab("Типы комнат"); }
    @FXML private void showConveniencesManagement() { openTableTab("Удобства"); }
    @FXML private void showBookingManagement() { openTableTab("Бронирования"); }
    @FXML private void showServiceManagement() { openTableTab("Услуги"); }
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
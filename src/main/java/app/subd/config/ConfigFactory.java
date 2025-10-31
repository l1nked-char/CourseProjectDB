package app.subd.config;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.models.*;
import app.subd.tables.AllDictionaries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigFactory {

    public static TableConfig createUserTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit,
            Callback<Object, Void> onToggleActive) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("username", "Логин", 150),
                new ColumnConfig("role", "Роль", 120),
                new ColumnConfig("hotelInfo", "Отель", 200),
                new ColumnConfig("userLocked", "Заблокирован", 100)
        );

        return new TableConfig("Пользователи", dataLoader, onAdd, onEdit, null, columns, null, onToggleActive);
    }

    // Конфигурация формы добавления пользователя
    public static UniversalFormConfig<User> createAddUserFormConfig(
            Function<User, Boolean> saveFunction,
            java.util.function.Consumer<User> onSuccess) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("username", "Логин", FieldConfig.FieldType.TEXT, true, "Введите логин"),
                new FieldConfig("password", "Пароль", FieldConfig.FieldType.TEXT, true, "Введите пароль"),
                new FieldConfig("confirmPassword", "Подтверждение пароля", FieldConfig.FieldType.TEXT, true, "Подтвердите пароль"),
                new FieldConfig("role", "Роль", FieldConfig.FieldType.COMBOBOX, true,
                        () -> FXCollections.observableArrayList("owner_role", "employee_role"), "Выберите роль", 200),
                new FieldConfig("hotelInfo", "Отель", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getHotelsForComboBox, "Выберите отель", 250)
        );

        return new UniversalFormConfig<>("Пользователь", fields, saveFunction, onSuccess,
                UniversalFormConfig.Mode.ADD, User.class);
    }

    // Конфигурация формы редактирования пользователя
    public static UniversalFormConfig<User> createEditUserFormConfig(
            Function<User, Boolean> saveFunction,
            java.util.function.Consumer<User> onSuccess) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("username", "Логин", FieldConfig.FieldType.TEXT, true, "Введите логин"),
                new FieldConfig("password", "Новый пароль", FieldConfig.FieldType.TEXT, false, "Оставьте пустым, если не меняется"),
                new FieldConfig("confirmPassword", "Подтверждение пароля", FieldConfig.FieldType.TEXT, false, "Подтвердите новый пароль"),
                new FieldConfig("role", "Роль", FieldConfig.FieldType.COMBOBOX, true,
                        () -> FXCollections.observableArrayList("owner_role", "employee_role"), "Выберите роль", 200),
                new FieldConfig("hotelInfo", "Отель", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getHotelsForComboBox, "Выберите отель", 250)
        );

        return new UniversalFormConfig<>("Пользователь", fields, saveFunction, onSuccess,
                UniversalFormConfig.Mode.EDIT, User.class);
    }

    public static TableConfig createHotelTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("cityName", "Город", 150),
                new ColumnConfig("address", "Адрес", 250)
        );

        return new TableConfig("Отели", dataLoader, onAdd, onEdit, null, columns, null, null);
    }

    public static TableConfig createRoomTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("hotelInfo", "Отель", 200),
                new ColumnConfig("roomNumber", "Номер комнаты", 120),
                new ColumnConfig("maxPeople", "Макс. людей", 100),
                new ColumnConfig("pricePerPerson", "Цена за человека", 120),
                new ColumnConfig("typeOfRoomName", "Тип комнаты", 150)
        );

        List<FilterConfig> filters = List.of(
                new FilterConfig(
                        "hotel",
                        "Отель",
                        (map) -> ConfigFactory.getHotelsForComboBox()
                )
        );

        return new TableConfig("Номера", dataLoader, onAdd, onEdit, null, columns, filters, null);
    }

    public static TableConfig createTypeOfRoomTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("name", "Название типа", 200)
        );

        return new TableConfig("Типы комнат", dataLoader, onAdd, onEdit, null, columns, null, null);
    }

    public static TableConfig createConvenienceTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("name", "Название удобства", 200)
        );

        return new TableConfig("Удобства", dataLoader, onAdd, onEdit, null, columns, null, null);
    }

    // Обновите метод createCityTableConfig:
    public static TableConfig createCityTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("cityId", "ID", 80),
                new ColumnConfig("cityName", "Название города", 200)
        );

        return new TableConfig("Города", dataLoader, onAdd, onEdit, null, columns, null, null);
    }

    public static TableConfig createRoomConvenienceTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("convName", "Удобство", 150),
                new ColumnConfig("pricePerOne", "Цена за единицу", 120),
                new ColumnConfig("amount", "Количество", 100),
                new ColumnConfig("startDate", "Дата начала", 120)
        );

        List<FilterConfig> filters = List.of(
                new FilterConfig(
                        "hotel",
                        "Отель",
                        (map) -> ConfigFactory.getHotelsForComboBox()
                ),
                new FilterConfig(
                        "room",
                        "Комната",
                        ConfigFactory::getRoomsByHotelForComboBox,
                        "hotel"
                )
        );

        return new TableConfig("Удобства в комнате", dataLoader, onAdd, onEdit, null, columns, filters, null);
    }

    public static TableConfig createHotelServiceTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {
        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("serviceName", "Название сервиса", 200),
                new ColumnConfig("startOfPeriod", "Начало периода", 120),
                new ColumnConfig("endOfPeriod", "Конец периода", 120),
                new ColumnConfig("pricePerOne", "Цена", 100),
                new ColumnConfig("canBeBooked", "Можно забронировать", 150)
        );
        List<FilterConfig> filters = List.of(
                new FilterConfig(
                        "hotel",
                        "Отель",
                        (map) -> ConfigFactory.getHotelsForComboBox()
                )
        );
        return new TableConfig("Сервисы отеля", dataLoader, onAdd, onEdit, null, columns, filters, null);
    }

    public static TableConfig createServiceHistoryTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {
        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("historyId", "ID Истории", 150),
                new ColumnConfig("serviceName", "Сервис", 200),
                new ColumnConfig("amount", "Количество", 100)
        );
        List<FilterConfig> filters = List.of(
                new FilterConfig(
                        "hotel",
                        "Отель",
                        (map) -> ConfigFactory.getHotelsForComboBox()
                ),
                new FilterConfig(
                        "booking",
                        "Бронирование",
                        ConfigFactory::getTenantHistoryByHotelForComboBox,
                        "hotel"
                )
        );
        return new TableConfig("История заказов услуг", dataLoader, onAdd, onEdit, null, columns, filters, null);
    }
    
    public static TableConfig createSocialStatusTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("name", "Название статуса", 200)
        );

        return new TableConfig("Социальные статусы", dataLoader, onAdd, onEdit, null, columns, null, null);
    }

    public static UniversalFormConfig<SocialStatus> createSocialStatusFormConfig(
            Function<SocialStatus, Boolean> saveFunction,
            java.util.function.Consumer<SocialStatus> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = List.of(
                new FieldConfig("name", "Название статуса", FieldConfig.FieldType.TEXT, true, "Введите название статуса")
        );

        return new UniversalFormConfig<>("Социальный статус", fields, saveFunction, onSuccess, mode, SocialStatus.class);
    }

    public static TableConfig createServiceTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("name", "Название услуги", 200)
        );

        return new TableConfig("Услуги", dataLoader, onAdd, onEdit, null, columns, null, null);
    }

    public static UniversalFormConfig<Service> createServiceFormConfig(
            Function<Service, Boolean> saveFunction,
            java.util.function.Consumer<Service> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = List.of(
                new FieldConfig("name", "Название услуги", FieldConfig.FieldType.TEXT, true, "Введите название услуги")
        );

        return new UniversalFormConfig<>("Услуга", fields, saveFunction, onSuccess, mode, Service.class);
    }

    public static TableConfig createTenantTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit,
            boolean isAdmin) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("firstName", "Фамилия", 150),
                new ColumnConfig("name", "Имя", 150),
                new ColumnConfig("patronymic", "Отчество", 150),
                new ColumnConfig("birthDate", "Дата рождения", 120),
                new ColumnConfig("passport", "Паспорт", 150),
                new ColumnConfig("socialStatus", "Социальный статус", 150),
                new ColumnConfig("email", "Email", 200),
                new ColumnConfig("documentType", "Тип документа", 150)
        );

        List<FilterConfig> filters = null;
        if (isAdmin) {
            filters = List.of(
                    new FilterConfig("hotel", "Отель", (map) -> ConfigFactory.getHotelsForComboBox())
            );
        }

        return new TableConfig("Жильцы", dataLoader, onAdd, onEdit, null, columns, filters, null);
    }

    public static UniversalFormConfig<Tenant> createTenantFormConfig(
            Function<Tenant, Boolean> saveFunction,
            java.util.function.Consumer<Tenant> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("firstName", "Фамилия", FieldConfig.FieldType.TEXT, true, "Введите фамилию"),
                new FieldConfig("name", "Имя", FieldConfig.FieldType.TEXT, true, "Введите имя"),
                new FieldConfig("patronymic", "Отчество", FieldConfig.FieldType.TEXT, false, "Введите отчество"),
                new FieldConfig("birthDate", "Дата рождения", FieldConfig.FieldType.DATE, true),
                new FieldConfig("series", "Серия паспорта", FieldConfig.FieldType.NUMBER, true, "4 цифры"),
                new FieldConfig("number", "Номер паспорта", FieldConfig.FieldType.NUMBER, true, "6 цифр"),
                new FieldConfig("documentType", "Тип документа", FieldConfig.FieldType.TEXT, true, "Например, Паспорт РФ"),
                new FieldConfig("email", "Email", FieldConfig.FieldType.TEXT, false, "example@mail.com"),
                new FieldConfig("cityId", "Город", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getCitiesForComboBox, "Выберите город", 200),
                new FieldConfig("socialStatusId", "Социальный статус", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getSocialStatusForComboBox, "Выберите социальный статус", 200)
        );

        return new UniversalFormConfig<>("Жилец", fields, saveFunction, onSuccess, mode, Tenant.class);
    }
    
    public static UniversalFormConfig<HotelService> createHotelServiceFormConfig(
            Function<HotelService, Boolean> saveFunction,
            java.util.function.Consumer<HotelService> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("serviceNameId", "Услуга", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getServicesForComboBox, "Выберите услугу", 200),
                new FieldConfig("startOfPeriod", "Начало периода", FieldConfig.FieldType.DATE, true),
                new FieldConfig("endOfPeriod", "Конец периода", FieldConfig.FieldType.DATE, true),
                new FieldConfig("pricePerOne", "Цена", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("canBeBooked", "Можно забронировать", FieldConfig.FieldType.CHECKBOX, true)
        );

        return new UniversalFormConfig<>("Услуги отеля", fields, saveFunction, onSuccess, mode, HotelService.class);
    }

    public static UniversalFormConfig<ServiceHistory> createServiceHistoryFormConfig(
            Function<ServiceHistory, Boolean> saveFunction,
            java.util.function.Consumer<ServiceHistory> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields;
        if (mode == UniversalFormConfig.Mode.ADD) {
            fields = Arrays.asList(
                new FieldConfig("serviceId", "Услуга", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getServicesForComboBox, "Выберите услугу", 200),
                new FieldConfig("amount", "Количество", FieldConfig.FieldType.NUMBER, true)
            );
        } else { // EDIT
            fields = Arrays.asList(
                    new FieldConfig("historyId", "ID Истории", FieldConfig.FieldType.COMBOBOX, true,
                            ConfigFactory::getTenantHistoryForComboBox, "Выберите историю", 200),
                    new FieldConfig("serviceId", "Услуга", FieldConfig.FieldType.COMBOBOX, true,
                            ConfigFactory::getServicesForComboBox, "Выберите услугу", 200),
                    new FieldConfig("amount", "Количество", FieldConfig.FieldType.NUMBER, true)
            );
        }
        return new UniversalFormConfig<>("История заказа услуг", fields, saveFunction, onSuccess, mode, ServiceHistory.class);
    }


    public static UniversalFormConfig<Hotel> createHotelFormConfig(
            Function<Hotel, Boolean> saveFunction,
            java.util.function.Consumer<Hotel> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("address", "Адрес", FieldConfig.FieldType.TEXT, true, "Введите адрес отеля"),
                new FieldConfig("cityId", "Город", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getCitiesForComboBox, "Выберите город", 200)
        );

        return new UniversalFormConfig<>("Отель", fields, saveFunction, onSuccess, mode, Hotel.class);
    }

    public static UniversalFormConfig<Room> createRoomFormConfig(
            Function<Room, Boolean> saveFunction,
            java.util.function.Consumer<Room> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("roomNumber", "Номер комнаты", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("maxPeople", "Максимум людей", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("pricePerPerson", "Цена за человека", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("hotelId", "Отель", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getHotelsForComboBox, "Выберите отель", 200),
                new FieldConfig("typeOfRoomId", "Тип комнаты", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getRoomTypesForComboBox, "Выберите тип", 200)
        );

        return new UniversalFormConfig<>("Комната", fields, saveFunction, onSuccess, mode, Room.class);
    }

    public static UniversalFormConfig<TypeOfRoom> createTypeOfRoomFormConfig(
            Function<TypeOfRoom, Boolean> saveFunction,
            java.util.function.Consumer<TypeOfRoom> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = List.of(
                new FieldConfig("name", "Название типа", FieldConfig.FieldType.TEXT, true, "Введите название типа комнаты")
        );

        return new UniversalFormConfig<>("Тип комнаты", fields, saveFunction, onSuccess, mode, TypeOfRoom.class);
    }

    public static UniversalFormConfig<Convenience> createConvenienceFormConfig(
            Function<Convenience, Boolean> saveFunction,
            java.util.function.Consumer<Convenience> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = List.of(
                new FieldConfig("name", "Название удобства", FieldConfig.FieldType.TEXT, true, "Введите название удобства")
        );

        return new UniversalFormConfig<>("Удобство", fields, saveFunction, onSuccess, mode, Convenience.class);
    }

    public static UniversalFormConfig<City> createCityFormConfig(
            Function<City, Boolean> saveFunction,
            java.util.function.Consumer<City> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = List.of(
                new FieldConfig("cityName", "Название города", FieldConfig.FieldType.TEXT, true, "Введите название города")
        );

        return new UniversalFormConfig<>("Город", fields, saveFunction, onSuccess, mode, City.class);
    }

    public static UniversalFormConfig<RoomConvenience> createRoomConvenienceFormConfig(
            Function<RoomConvenience, Boolean> saveFunction,
            java.util.function.Consumer<RoomConvenience> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("convNameId", "Удобство", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getConveniencesForComboBox, "Выберите удобство", 200),
                new FieldConfig("pricePerOne", "Цена за единицу", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("amount", "Количество", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("startDate", "Дата начала", FieldConfig.FieldType.DATE, true)
        );

        return new UniversalFormConfig<>("Удобство в комнате", fields, saveFunction, onSuccess, mode, RoomConvenience.class);
    }

    public static TableConfig createTenantHistoryTableConfig(
            Function<Map<String, Object>, ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("bookingNumber", "Номер брони", 150),
                new ColumnConfig("roomId", "ID комнаты", 100),
                new ColumnConfig("tenantId", "ID жильца", 100),
                new ColumnConfig("bookingDate", "Дата брони", 120),
                new ColumnConfig("checkInDate", "Дата заезда", 120),
                new ColumnConfig("checkInStatus", "Статус заезда", 120),
                new ColumnConfig("occupiedSpace", "Занято мест", 100),
                new ColumnConfig("amountOfNights", "Кол-во ночей", 100),
                new ColumnConfig("canBeSplit", "Разделяемая", 100)
        );

        List<FilterConfig> filters = List.of(
                new FilterConfig(
                        "hotel",
                        "Отель",
                        (map) -> ConfigFactory.getHotelsForComboBox()
                )
        );

        return new TableConfig("История заселений", dataLoader, onAdd, onEdit, null, columns, filters, null);
    }

    public static UniversalFormConfig<TenantHistory> createTenantHistoryFormConfig(
            Function<TenantHistory, Boolean> saveFunction,
            java.util.function.Consumer<TenantHistory> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("hotel", "Отель", FieldConfig.FieldType.COMBOBOX, true, ConfigFactory::getHotelsForComboBox, "Выберите отель", 400),
                new FieldConfig("roomId", "Комната", FieldConfig.FieldType.COMBOBOX, true, ConfigFactory::getRoomsByHotelForComboBox, "Выберите комнату", 200, "hotel"),
                new FieldConfig("tenantId", "Жилец", FieldConfig.FieldType.COMBOBOX, true, ConfigFactory::getTenantsForComboBox, "Выберите жильца", 400, "hotel"),
                new FieldConfig("bookingDate", "Дата бронирования", FieldConfig.FieldType.DATE, true),
                new FieldConfig("checkInDate", "Дата заезда", FieldConfig.FieldType.DATE, true),
                new FieldConfig("checkInStatus", "Статус заезда", FieldConfig.FieldType.TEXT, true, "Введите статус"),
                new FieldConfig("occupiedSpace", "Занято мест", FieldConfig.FieldType.NUMBER, true, "Введите количество"),
                new FieldConfig("amountOfNights", "Количество ночей", FieldConfig.FieldType.NUMBER, true, "Введите количество"),
                new FieldConfig("canBeSplit", "Разделяемое бронирование", FieldConfig.FieldType.CHECKBOX, true)
        );

        return new UniversalFormConfig<>("История заселений", fields, saveFunction, onSuccess, mode, TenantHistory.class);
    }

    public static ObservableList<Object> getCitiesForComboBox() {
        try {
            AllDictionaries.initialiseCitiesMaps();
            return FXCollections.observableArrayList(
                    AllDictionaries.getCitiesIdMap().entrySet().stream()
                            .map(entry -> new City(entry.getValue(), entry.getKey()))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке городов для ComboBox: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    public static ObservableList<Object> getHotelsForComboBox() {
         try {
            return FXCollections.observableArrayList(
                    AllDictionaries.getHotelsIdMap().entrySet().stream()
                            .map(entry -> {
                                if (entry.getKey() == null) {
                                    return null; // Пропускаем некорректные записи
                                }
                                String[] parts = entry.getKey().split(" - ");
                                if (parts.length == 2) {
                                    return new Hotel(entry.getValue(), 0, parts[1], parts[0]);
                                } else {
                                    return new Hotel(entry.getValue(), 0, entry.getKey());
                                }
                            })
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке отелей для ComboBox: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    public static ObservableList<Object> getRoomTypesForComboBox() {
        try {
            AllDictionaries.initialiseTypesOfRoomMaps();
            return FXCollections.observableArrayList(
                    AllDictionaries.getTypesOfRoomIdMap().entrySet().stream()
                            .map(entry -> new TypeOfRoom(entry.getValue(), entry.getKey()))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке типов комнат для ComboBox: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    public static ObservableList<Object> getConveniencesForComboBox() {
        try {
            AllDictionaries.initialiseConveniencesMaps();
            return FXCollections.observableArrayList(
                    AllDictionaries.getConveniencesIdMap().entrySet().stream()
                            .map(entry -> new Convenience(entry.getValue(), entry.getKey()))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке удобств для ComboBox: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
    
    public static ObservableList<Object> getServicesForComboBox() {
        try {
            // Assuming AllDictionaries can fetch services
            AllDictionaries.initialiseServicesMaps();
            return FXCollections.observableArrayList(
                    AllDictionaries.getServicesIdMap().entrySet().stream()
                            .map(entry -> new Service(entry.getValue(), entry.getKey()))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке сервисов для ComboBox: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    public static ObservableList<Object> getSocialStatusForComboBox() {
        try {
            AllDictionaries.initialiseSocialStatusMaps();
            return FXCollections.observableArrayList(
                    AllDictionaries.getSocialStatusIdMap().entrySet().stream()
                            .map(entry -> new SocialStatus(entry.getValue(), entry.getKey()))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке социальных статусов для ComboBox: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
    
    public static ObservableList<Object> getTenantHistoryForComboBox() {
        ObservableList<Object> tenantHistory = FXCollections.observableArrayList();
        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_all_tenant_history");
            while (rs.next()) {
                tenantHistory.add(new TenantHistory(
                        rs.getString("booking_number"),
                        rs.getInt("room_id"),
                        rs.getInt("tenant_id"),
                        rs.getDate("booking_date").toLocalDate(),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getString("check_in_status"),
                        rs.getInt("occupied_space"),
                        rs.getInt("amount_of_nights"),
                        rs.getBoolean("can_be_split")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке истории жильцов для ComboBox: " + e.getMessage());
        }
        return tenantHistory;
    }

    public static ObservableList<Object> getTenantHistoryByHotelForComboBox(Map<String, Object> currentFilters) {
        ObservableList<Object> tenantHistory = FXCollections.observableArrayList();
        Object hotelFilterValue = currentFilters.get("hotel");
        if (!(hotelFilterValue instanceof Hotel selectedHotel)) {
            return tenantHistory;
        }

        try {
            int hotelId = selectedHotel.getId();
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_tenant_history_by_hotel", hotelId);
            while (rs.next()) {
                tenantHistory.add(new TenantHistory(
                        rs.getString("booking_number"),
                        rs.getInt("room_id"),
                        rs.getInt("tenant_id"),
                        rs.getDate("booking_date").toLocalDate(),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getString("check_in_status"),
                        rs.getInt("occupied_space"),
                        rs.getInt("amount_of_nights"),
                        rs.getBoolean("can_be_split")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке истории жильцов для отеля ID " + selectedHotel.getId() + ": " + e.getMessage());
        }
        return tenantHistory;
    }

    public static ObservableList<Object> getTenantsForComboBox(Map<String, Object> currentFilters) {
        ObservableList<Object> tenants = FXCollections.observableArrayList();
        Object hotelFilterValue = currentFilters.get("hotel");
        if (!(hotelFilterValue instanceof Hotel selectedHotel)) {
            return tenants;
        }

        try {
            int hotelId = selectedHotel.getId();
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_tenants_by_hotel", hotelId);
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
                        rs.getString("document_type"),
                        rs.getString("email")
                );
                tenant.setBirthDate(rs.getDate("birth_date").toLocalDate());
                tenants.add(tenant);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке жильцов для ComboBox: " + e.getMessage());
        }
        return tenants;
    }

    public static ObservableList<Object> getRoomsByHotelForComboBox(Map<String, Object> currentFilters) {
        ObservableList<Object> rooms = FXCollections.observableArrayList();
        Object hotelFilterValue = currentFilters.get("hotel");
        if (!(hotelFilterValue instanceof Hotel selectedHotel)) {
            return rooms;
        }

        try {
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
            System.err.println("Ошибка при загрузке комнат для отеля ID " + selectedHotel.getId() + ": " + e.getMessage());

        }
        return rooms;
    }
}

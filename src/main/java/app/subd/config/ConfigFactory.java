package app.subd.config;

import app.subd.models.*;
import app.subd.tables.AllDictionaries;
import javafx.collections.FXCollections;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

        return new TableConfig("Пользователи", dataLoader, onAdd, onEdit, null, columns, null);
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
                        ConfigFactory::getHotelsForComboBox, "Выберите отель", 250),
                new FieldConfig("userLocked", "Заблокировать аккаунт", FieldConfig.FieldType.CHECKBOX, false)
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

        return new TableConfig("Отели", dataLoader, onAdd, onEdit, null, columns, null);
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
                        ConfigFactory::getHotelsForComboBox
                )
        );

        return new TableConfig("Номера", dataLoader, onAdd, onEdit, null, columns, filters);
    }

    public static TableConfig createTypeOfRoomTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("name", "Название типа", 200)
        );

        return new TableConfig("Типы комнат", dataLoader, onAdd, onEdit, null, columns, null);
    }

    public static TableConfig createConvenienceTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("id", "ID", 80),
                new ColumnConfig("name", "Название удобства", 200)
        );

        return new TableConfig("Удобства", dataLoader, onAdd, onEdit, null, columns, null);
    }

    public static TableConfig createCityTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("cityId", "ID", 80),
                new ColumnConfig("cityName", "Название города", 200)
        );

        return new TableConfig("Города", dataLoader, onAdd, onEdit, null, columns, null);
    }

    public static TableConfig createRoomConvenienceTableConfig(
            Function<Map<String, Object>, javafx.collections.ObservableList<Object>> dataLoader,
            Callback<Void, Void> onAdd,
            Callback<Object, Void> onEdit) {

        List<ColumnConfig> columns = Arrays.asList(
                new ColumnConfig("roomId", "ID комнаты", 100),
                new ColumnConfig("convName", "Удобство", 150),
                new ColumnConfig("pricePerOne", "Цена за единицу", 120),
                new ColumnConfig("amount", "Количество", 100),
                new ColumnConfig("startDate", "Дата начала", 120)
        );

        return new TableConfig("Удобства в комнате", dataLoader, onAdd, onEdit, null, columns, null);
    }

    // Конфигурации для форм
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
                new FieldConfig("roomId", "ID комнаты", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("convNameId", "Удобство", FieldConfig.FieldType.COMBOBOX, true,
                        ConfigFactory::getConveniencesForComboBox, "Выберите удобство", 200),
                new FieldConfig("pricePerOne", "Цена за единицу", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("amount", "Количество", FieldConfig.FieldType.NUMBER, true),
                new FieldConfig("startDate", "Дата начала", FieldConfig.FieldType.DATE, true)
        );

        return new UniversalFormConfig<>("Удобство в комнате", fields, saveFunction, onSuccess, mode, RoomConvenience.class);
    }

    // Вспомогательные методы для получения данных для комбобоксов
    public static javafx.collections.ObservableList<String> getCitiesForComboBox() {
        try {
            AllDictionaries.initialiseCitiesMaps();
            return FXCollections.observableArrayList(AllDictionaries.getCitiesIdMap().keySet());
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static javafx.collections.ObservableList<String> getHotelsForComboBox() {
        try {
            AllDictionaries.initialiseHotelsMaps();
            return FXCollections.observableArrayList(AllDictionaries.getHotelsIdMap().keySet());
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static javafx.collections.ObservableList<String> getRoomTypesForComboBox() {
        try {
            AllDictionaries.initialiseTypesOfRoomMaps();
            return FXCollections.observableArrayList(AllDictionaries.getTypesOfRoomIdMap().keySet());
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static javafx.collections.ObservableList<String> getConveniencesForComboBox() {
        try {
            AllDictionaries.initialiseConveniencesMaps();
            return FXCollections.observableArrayList(AllDictionaries.getConveniencesIdMap().keySet());
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}
package app.subd.config;

import app.subd.models.*;
import javafx.collections.FXCollections;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigFactory {

    // Конфигурации для таблиц
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

        List<FilterConfig> filters = Arrays.asList(
                new FilterConfig(
                        "hotel",
                        "Отель",
                        () -> {
                            try {
                                // Здесь нужно будет заменить на вашу реализацию
                                return FXCollections.observableArrayList("Отель 1", "Отель 2");
                            } catch (Exception e) {
                                return FXCollections.observableArrayList();
                            }
                        }
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

    // Аналогично для других сущностей...

    // Конфигурации для форм
    public static UniversalFormConfig<Hotel> createHotelFormConfig(
            Function<Hotel, Boolean> saveFunction,
            java.util.function.Consumer<Hotel> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("address", "Адрес", FieldConfig.FieldType.TEXT, true, "Введите адрес отеля"),
                new FieldConfig("cityId", "Город", FieldConfig.FieldType.COMBOBOX, true,
                        () -> FXCollections.observableArrayList(getCities()), "Выберите город", 200)
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
                        () -> FXCollections.observableArrayList(getHotels()), "Выберите отель", 200),
                new FieldConfig("typeOfRoomId", "Тип комнаты", FieldConfig.FieldType.COMBOBOX, true,
                        () -> FXCollections.observableArrayList(getRoomTypes()), "Выберите тип", 200)
        );

        return new UniversalFormConfig<>("Комната", fields, saveFunction, onSuccess, mode, Room.class);
    }

    public static UniversalFormConfig<TypeOfRoom> createTypeOfRoomFormConfig(
            Function<TypeOfRoom, Boolean> saveFunction,
            java.util.function.Consumer<TypeOfRoom> onSuccess,
            UniversalFormConfig.Mode mode) {

        List<FieldConfig> fields = Arrays.asList(
                new FieldConfig("name", "Название типа", FieldConfig.FieldType.TEXT, true, "Введите название типа комнаты")
        );

        return new UniversalFormConfig<>("Тип комнаты", fields, saveFunction, onSuccess, mode, TypeOfRoom.class);
    }

    // Вспомогательные методы для получения данных для комбобоксов
    private static java.util.List<City> getCities() {
        // Заглушка - нужно реализовать получение городов из БД
        return Arrays.asList(
                new City(1, "Москва"),
                new City(2, "Санкт-Петербург")
        );
    }

    private static java.util.List<Hotel> getHotels() {
        // Заглушка - нужно реализовать получение отелей из БД
        return Arrays.asList(
                new Hotel(1, 1, "ул. Ленина, 1", "Москва"),
                new Hotel(2, 2, "Невский пр., 10", "Санкт-Петербург")
        );
    }

    private static java.util.List<TypeOfRoom> getRoomTypes() {
        // Заглушка - нужно реализовать получение типов комнат из БД
        return Arrays.asList(
                new TypeOfRoom(1, "Стандарт"),
                new TypeOfRoom(2, "Люкс")
        );
    }
}
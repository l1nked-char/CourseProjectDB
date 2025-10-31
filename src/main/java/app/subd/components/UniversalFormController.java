package app.subd.components;

import app.subd.admin_panels.AdminController;
import app.subd.config.UniversalFormConfig;
import app.subd.config.FieldConfig;
import app.subd.models.*;
import app.subd.tables.AllDictionaries;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class UniversalFormController<T> implements FormController<T> {

    @FXML private VBox formContainer;
    @FXML private Label titleLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;

    private UniversalFormConfig<T> config;
    private T item;
    private Runnable onSaveSuccess;
    private Stage stage;
    private UniversalTableController parentController;

    private FormController.Mode controllerMode;

    private final Validator validator = new Validator();
    private final Map<String, Control> formControls = new HashMap<>();

    @Override
    public void setMode(FormController.Mode mode) {
        this.controllerMode = mode;
    }

    @Override
    public void setItem(T item) {
        this.item = item;
    }

    @Override
    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    @Override
    public void setParentController(AdminController.RefreshableController parentController) {
        this.parentController = (UniversalTableController) parentController;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    public void configure(UniversalFormConfig<T> config) {
        this.config = config;
        initializeForm();
    }

    @FXML
    public void initialize() {

    }

    private void initializeForm() {
        if (config == null) return;

        String title = config.getFormTitle();
        if (controllerMode != null) {
            title = (controllerMode == FormController.Mode.ADD ? "Добавление" : "Редактирование") +
                    " " + config.getFormTitle().toLowerCase();
        }
        titleLabel.setText(title);

        formContainer.getChildren().clear();
        formControls.clear();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 10;");

        createFormFields(grid);
        createChecks();

        formContainer.getChildren().add(grid);

        if (item != null && controllerMode == FormController.Mode.EDIT) {
            populateForm();
        }

        saveButton.disableProperty().bind(validator.containsErrorsProperty());
    }

    private void createFormFields(GridPane grid) {
        int row = 0;
        for (FieldConfig fieldConfig : config.getFields()) {
            Label label = new Label(fieldConfig.getLabel() + (fieldConfig.isRequired() ? " *" : ""));
            label.setStyle("-fx-font-weight: bold;");

            Control control = createControl(fieldConfig);

            formControls.put(fieldConfig.getPropertyName(), control);

            grid.add(label, 0, row);
            grid.add(control, 1, row);

            row++;
        }
    }

    private void createChecks() {
        for (FieldConfig fieldConfig : config.getFields()) {
            if (fieldConfig.isRequired()) {
                Control control = formControls.get(fieldConfig.getPropertyName());
                Check check = validator.createCheck();
                if (control instanceof TextInputControl) {
                    check.dependsOn("value", ((TextInputControl) control).textProperty());
                    check.withMethod(c -> {
                        String text = c.get("value");
                        if (text == null || text.trim().isEmpty()) {
                            c.error("Поле не должно быть пустым");
                        }
                    });
                } else if (control instanceof ComboBox) {
                    check.dependsOn("value", ((ComboBox<?>) control).valueProperty());
                    check.withMethod(c -> {
                        if (c.get("value") == null) {
                            c.error("Необходимо выбрать значение");
                        }
                    });
                } else if (control instanceof DatePicker) {
                    check.dependsOn("value", ((DatePicker) control).valueProperty());
                    check.withMethod(c -> {
                        if (c.get("value") == null) {
                            c.error("Необходимо выбрать дату");
                        }
                    });
                }
            }
        }

        Class<?> entityClass = config.getEntityClass();
        if (entityClass == Tenant.class) {
            validator.createCheck()
                    .dependsOn("email", ((TextField) formControls.get("email")).textProperty())
                    .withMethod(c -> {
                        String email = c.get("email");
                        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                            c.error("Неверный формат email");
                        }
                    });
        } else if (entityClass == HotelService.class) {
            validator.createCheck()
                    .dependsOn("start", ((DatePicker) formControls.get("startOfPeriod")).valueProperty())
                    .dependsOn("end", ((DatePicker) formControls.get("endOfPeriod")).valueProperty())
                    .withMethod(c -> {
                        LocalDate start = c.get("start");
                        LocalDate end = c.get("end");
                        if (start != null && end != null && !start.isBefore(end)) {
                            c.error("Начало должно быть раньше конца");
                        }
                    });
            validator.createCheck()
                    .dependsOn("price", ((TextField) formControls.get("pricePerOne")).textProperty())
                    .withMethod(c -> {
                        try {
                            if (new BigDecimal((String) c.get("price")).compareTo(BigDecimal.ZERO) <= 0) {
                                c.error("Цена должна быть больше нуля");
                            }
                        } catch (Exception e) {
                            c.error("Неверный формат цены");
                        }
                    });
        } else if (entityClass == TenantHistory.class) {
            validator.createCheck()
                    .dependsOn("booking", ((DatePicker) formControls.get("bookingDate")).valueProperty())
                    .dependsOn("checkin", ((DatePicker) formControls.get("checkInDate")).valueProperty())
                    .withMethod(c -> {
                        LocalDate booking = c.get("booking");
                        LocalDate checkin = c.get("checkin");
                        if (booking != null && checkin != null && !booking.isBefore(checkin)) {
                            c.error("Бронь должна быть раньше заезда");
                        }
                    });
        }
    }

    private Control createControl(FieldConfig fieldConfig) {
        switch (fieldConfig.getType()) {
            case TEXT:
                TextField textField = new TextField();
                if (fieldConfig.getPromptText() != null) {
                    textField.setPromptText(fieldConfig.getPromptText());
                }
                if (fieldConfig.getWidth() > 0) {
                    textField.setPrefWidth(fieldConfig.getWidth());
                }
                return textField;

            case NUMBER:
                TextField numberField = new TextField();
                numberField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        numberField.setText(newValue.replaceAll("[^\\d.]", ""));
                    }
                });
                if (fieldConfig.getPromptText() != null) {
                    numberField.setPromptText(fieldConfig.getPromptText());
                }
                if (fieldConfig.getWidth() > 0) {
                    numberField.setPrefWidth(fieldConfig.getWidth());
                }
                return numberField;

            case COMBOBOX:
                ComboBox<Object> comboBox = new ComboBox<>();
                if (fieldConfig.getItemsSupplier() != null) {
                    comboBox.setItems(fieldConfig.getItemsSupplier().get());
                }
                if (fieldConfig.getPromptText() != null) {
                    comboBox.setPromptText(fieldConfig.getPromptText());
                }
                if (fieldConfig.getWidth() > 0) {
                    comboBox.setPrefWidth(fieldConfig.getWidth());
                }

                comboBox.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(getItemDisplayText(item));
                        }
                    }
                });

                comboBox.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(getItemDisplayText(item));
                        }
                    }
                });

                return comboBox;

            case DATE:
                DatePicker datePicker = new DatePicker();
                if (fieldConfig.getPromptText() != null) {
                    datePicker.setPromptText(fieldConfig.getPromptText());
                }
                if (fieldConfig.getWidth() > 0) {
                    datePicker.setPrefWidth(fieldConfig.getWidth());
                }
                return datePicker;

            case TEXTAREA:
                TextArea textArea = new TextArea();
                textArea.setPrefRowCount(3);
                if (fieldConfig.getPromptText() != null) {
                    textArea.setPromptText(fieldConfig.getPromptText());
                }
                if (fieldConfig.getWidth() > 0) {
                    textArea.setPrefWidth(fieldConfig.getWidth());
                }
                return textArea;

            case CHECKBOX:
                return new CheckBox();

            default:
                return new TextField();
        }
    }

    private String getItemDisplayText(Object item) {
        if (item instanceof City) {
            return ((City) item).getCityName();
        } else if (item instanceof Hotel hotel) {
            if (hotel.getCityName() != null && !hotel.getCityName().isEmpty()) {
                return hotel.getCityName() + " - " + hotel.getAddress();
            }
            return hotel.getAddress();
        } else if (item instanceof TypeOfRoom) {
            return ((TypeOfRoom) item).getName();
        } else if (item instanceof Convenience) {
            return ((Convenience) item).getName();
        } else if (item instanceof Room room) {
            return room.getHotelInfo() != null ? room.getHotelInfo() : "Комната " + room.getId();
        } else if (item instanceof SocialStatus) {
            return ((SocialStatus) item).getName();
        } else if (item instanceof Service) {
            return ((Service) item).getName();
        } else if (item instanceof Tenant) {
            return ((Tenant) item).getFirstName() + " " + ((Tenant) item).getName() + " " + ((Tenant) item).getPatronymic();
        }
        return item.toString();
    }

    private void populateForm() {
        if (item == null) return;

        try {
            for (FieldConfig fieldConfig : config.getFields()) {
                Control control = formControls.get(fieldConfig.getPropertyName());
                if (control != null) {

                    Field field = item.getClass().getDeclaredField(fieldConfig.getPropertyName());
                    field.setAccessible(true);
                    Object value = field.get(item);

                    setControlValue(control, value);
                }
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка заполнения формы: " + e.getMessage());
        }
    }


    private void setControlValue(Control control, Object value) {
        if (control instanceof TextField && value != null) {
            ((TextField) control).setText(value.toString());
        } else if (control instanceof ComboBox && value != null) {
            ComboBox<Object> comboBox = (ComboBox<Object>) control;

            Object itemToSelect = null;

            if (value instanceof Integer) {
                // Поиск по ID для обычных сущностей
                itemToSelect = findItemById(comboBox, (Integer) value);
            } else if (value instanceof String) {
                // Поиск по строковому представлению для User.hotelInfo
                String stringValue = (String) value;
                for (Object item : comboBox.getItems()) {
                    String itemDisplay = getItemDisplayText(item);
                    if (stringValue.equals(itemDisplay)) {
                        itemToSelect = item;
                        break;
                    }
                }
            } else {
                // Прямое сравнение объектов
                for (Object item : comboBox.getItems()) {
                    if (value.equals(item)) {
                        itemToSelect = item;
                        break;
                    }
                }
            }

            if (itemToSelect != null) {
                comboBox.getSelectionModel().select(itemToSelect);
            } else {
                // Если не нашли, устанавливаем значение напрямую (для обратной совместимости)
                comboBox.setValue(value);
            }
        } else if (control instanceof DatePicker && value != null) {
            if (value instanceof LocalDate) {
                ((DatePicker) control).setValue((LocalDate) value);
            } else if (value instanceof java.sql.Date) {
                ((DatePicker) control).setValue(((java.sql.Date) value).toLocalDate());
            }
        } else if (control instanceof CheckBox && value != null) {
            ((CheckBox) control).setSelected(Boolean.TRUE.equals(value));
        } else if (control instanceof TextArea && value != null) {
            ((TextArea) control).setText(value.toString());
        }
    }

    private Object findItemById(ComboBox<Object> comboBox, Integer id) {
        for (Object item : comboBox.getItems()) {
            if (item instanceof City && ((City) item).getCityId() == id) {
                return item;
            } else if (item instanceof Hotel && ((Hotel) item).getId() == id) {
                return item;
            } else if (item instanceof TypeOfRoom && ((TypeOfRoom) item).getId().equals(id)) {
                return item;
            } else if (item instanceof Convenience && ((Convenience) item).getId() == id) {
                return item;
            } else if (item instanceof SocialStatus && ((SocialStatus) item).getId() == id) {
                return item;
            } else if (item instanceof Service && ((Service) item).getId() == id) {
                return item;
            } else if (item instanceof Tenant && ((Tenant) item).getId() == id) {
                return item;
            } else if (item instanceof HotelService && ((HotelService) item).getServiceNameId() == id) {
                return item;
            } else if (item instanceof ServiceHistory && ((ServiceHistory) item).getServiceId() == id) {
                return item;
            }
        }
        return null;
    }

    @FXML
    private void handleSave() {
        if (validator.containsErrors()) {
            showError(statusLabel, "Пожалуйста, исправьте ошибки в форме.");
            return;
        }

        try {
            T entity = item != null ? item : createNewInstance();

            populateEntityFromForm(entity);

            boolean success = config.getSaveFunction().apply(entity);

            if (success) {
                String successMessage = (controllerMode == FormController.Mode.ADD ?
                        config.getMode().getSuccessMessage() : "Данные успешно обновлены");

                showSuccess(statusLabel, successMessage);

                if (onSaveSuccess != null) {
                    onSaveSuccess.run();
                }

                if (config.getOnSuccess() != null) {
                    config.getOnSuccess().accept(entity);
                }

                if (controllerMode == FormController.Mode.ADD) {
                    clearForm();
                    showSuccess(statusLabel, successMessage);
                } else {
                    closeWindow();
                }
            } else {
                showError(statusLabel, "Ошибка сохранения данных");
            }

        } catch (Exception e) {
            showError(statusLabel, "Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private T createNewInstance() throws Exception {
        Class<?> entityClass = config.getEntityClass();
        return (T) entityClass.getConstructor().newInstance();
    }

    private void populateEntityFromForm(T entity) throws Exception {
        if (entity instanceof User) {
            populateUserFromForm((User) entity);
            return;
        }

        if (entity instanceof RoomConvenience && controllerMode == Mode.ADD) {
            if (parentController != null) {
                Map<String, Object> filters = parentController.getCurrentFilterValues();
                Object roomObj = filters.get("room");
                if (roomObj instanceof Room selectedRoom) {
                    ((RoomConvenience) entity).setRoomId(selectedRoom.getId());
                } else {
                    showError(statusLabel, "Ошибка: Комната не выбрана в фильтре.");
                    return;
                }
            }
        }

        if (entity instanceof HotelService && controllerMode == Mode.ADD) {
            if (parentController != null) {
                Map<String, Object> filters = parentController.getCurrentFilterValues();
                if (filters.get("hotel") instanceof Hotel selectedHotel) {
                    ((HotelService) entity).setHotelId(selectedHotel.getId());
                }
            }
        }

        if (entity instanceof ServiceHistory && controllerMode == Mode.ADD) {
            if (parentController != null) {
                Map<String, Object> filters = parentController.getCurrentFilterValues();
                if (filters.get("booking") instanceof TenantHistory selectedBooking) {
                    ((ServiceHistory) entity).setHistoryId(selectedBooking.getBookingNumber());
                } else {
                    showError(statusLabel, "Ошибка: Бронирование не выбрано в фильтре.");
                    return;
                }
            }
        }

        for (FieldConfig fieldConfig : config.getFields()) {
            Control control = formControls.get(fieldConfig.getPropertyName());
            if (control != null) {
                Object value = getControlValue(control, fieldConfig.getType(), fieldConfig.getPropertyName());

                Field field = entity.getClass().getDeclaredField(fieldConfig.getPropertyName());
                field.setAccessible(true);

                value = convertValueToFieldType(value, field.getType());
                field.set(entity, value);
            }
        }
    }

    private void populateUserFromForm(User user) {

        for (FieldConfig fieldConfig : config.getFields()) {
            Control control = formControls.get(fieldConfig.getPropertyName());
            if (control != null) {
                Object value = getControlValue(control, fieldConfig.getType(), fieldConfig.getPropertyName());

                switch (fieldConfig.getPropertyName()) {
                    case "username":
                        user.setUsername(value != null ? value.toString() : "");
                        break;
                    case "password":
                        user.setPassword(value != null ? value.toString() : null);
                        break;
                    case "confirmPassword":
                        user.setConfirmPassword(value != null ? value.toString() : null);
                        break;
                    case "role":
                        user.setRole(value != null ? value.toString() : "");
                        break;
                    case "hotelInfo":
                        user.setHotelInfo(value != null ? AllDictionaries.getHotelsNameMap().get(value) : "");
                        break;
                }
            }
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty() && user.getPassword().equals(user.getConfirmPassword())) {
            user.setTempPassword(user.getPassword());
        } else {
            showError(statusLabel, "Пароли должны совпадать");
        }
    }

    private Object convertValueToFieldType(Object value, Class<?> fieldType) {
        if (value == null) return null;

        if (fieldType.isInstance(value)) {
            return value;
        }

        if (fieldType == String.class) {
            return value.toString();
        } else if (fieldType == Integer.class || fieldType == int.class) {
            if (value instanceof Double) { // Преобразование из Double в Integer
                return ((Double) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } else if (fieldType == BigDecimal.class) {
            return new BigDecimal(value.toString());
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value.toString());
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (fieldType == LocalDate.class) {
            return value;
        }

        return value;
    }

    private Object getControlValue(Control control, FieldConfig.FieldType fieldType, String propertyName) {
        if (control instanceof TextField) {
            String text = ((TextField) control).getText();
            if (fieldType == FieldConfig.FieldType.NUMBER && !text.isEmpty()) {
                try {
                    if (text.contains(".")) {
                        return Double.parseDouble(text);
                    } else {
                        return Integer.parseInt(text);
                    }
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return text;
        } else if (control instanceof ComboBox) {
            ComboBox<Object> comboBox = (ComboBox<Object>) control;
            Object selectedValue = comboBox.getValue();

            // Универсальная обработка выбранного значения
            if (selectedValue != null) {
                // Для сущностей с методом getId()
                try {
                    Method getIdMethod = selectedValue.getClass().getMethod("getId");
                    Object idValue = getIdMethod.invoke(selectedValue);
                    if (idValue instanceof Integer) {
                        return idValue;
                    }
                } catch (Exception e) {
                    // Игнорируем, если нет метода getId
                }

                // Для сущностей с методом getCityId() (City)
                try {
                    Method getCityIdMethod = selectedValue.getClass().getMethod("getCityId");
                    Object idValue = getCityIdMethod.invoke(selectedValue);
                    if (idValue instanceof Integer) {
                        return idValue;
                    }
                } catch (Exception e) {
                    // Игнорируем, если нет метода getCityId
                }

                // Для TenantHistory
                if (selectedValue instanceof TenantHistory) {
                    return ((TenantHistory) selectedValue).getBookingNumber();
                }

                // Для строковых значений
                if (selectedValue instanceof String) {
                    return selectedValue;
                }
            }

            return selectedValue;
        } else if (control instanceof DatePicker) {
            return ((DatePicker) control).getValue();
        } else if (control instanceof CheckBox) {
            return ((CheckBox) control).isSelected();
        } else if (control instanceof TextArea) {
            return ((TextArea) control).getText();
        }
        return null;
    }



    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void clearForm() {
        for (Control control : formControls.values()) {
            if (control instanceof TextInputControl) {
                ((TextInputControl) control).clear();
            } else if (control instanceof ComboBox) {
                ((ComboBox<?>) control).setValue(null);
            } else if (control instanceof DatePicker) {
                ((DatePicker) control).setValue(null);
            } else if (control instanceof CheckBox) {
                ((CheckBox) control).setSelected(false);
            }
        }
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
    }
}
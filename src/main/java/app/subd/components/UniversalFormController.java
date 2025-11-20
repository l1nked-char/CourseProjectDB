package app.subd.components;

import app.subd.Database_functions;
import app.subd.admin_panels.AdminController;
import app.subd.config.UniversalFormConfig;
import app.subd.config.FieldConfig;
import app.subd.models.*;
import javafx.beans.value.ObservableValue;
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
import java.sql.Connection;
import java.sql.ResultSet;
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
    private final Map<String, Label> formLabels = new HashMap<>();
    private final Map<Control, Check> validationChecks = new HashMap<>();

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

        // Очищаем предыдущие проверки валидатора
        validator.clear();
        validationChecks.clear();

        String title = config.getFormTitle();
        if (controllerMode != null) {
            title = (controllerMode == FormController.Mode.ADD ? "Добавление" : "Редактирование") +
                    " " + config.getFormTitle().toLowerCase();
        }
        titleLabel.setText(title);

        formContainer.getChildren().clear();
        formControls.clear();
        formLabels.clear();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 10;");

        createFormFields(grid);

        formContainer.getChildren().add(grid);

        if (item != null && controllerMode == FormController.Mode.EDIT) {
            populateForm();
        }

        // Биндим кнопку сохранения к состоянию валидатора
        saveButton.disableProperty().bind(validator.containsErrorsProperty());

        // Настраиваем визуальное отображение ошибок
        setupValidationVisuals();
    }

    // Новая упрощенная система визуализации ошибок
    private void setupValidationVisuals() {
        // Слушаем изменения результатов валидации
        validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // Сбрасываем стили для всех контролов
            formControls.values().forEach(control -> control.setStyle(""));

            // Для каждого сообщения об ошибке находим соответствующий контрол и подсвечиваем его
            newValue.getMessages().forEach(message -> {
                // Ищем контрол, к которому относится это сообщение об ошибке
                validationChecks.entrySet().stream()
                        .filter(entry -> {
                            // Проверяем, относится ли это сообщение к данной проверке
                            // Это упрощенный подход - мы предполагаем, что каждая проверка связана с одним контролом
                            Check check = entry.getValue();
                            // Проверяем, содержит ли текст ошибки информацию о поле
                            String errorText = message.getText();
                            String fieldName = getFieldNameFromError(errorText);

                            if (fieldName != null) {
                                // Если можем определить поле по тексту ошибки
                                FieldConfig fieldConfig = config.getFields().stream()
                                        .filter(f -> f.getLabel().contains(fieldName))
                                        .findFirst()
                                        .orElse(null);
                                if (fieldConfig != null) {
                                    Control expectedControl = formControls.get(fieldConfig.getPropertyName());
                                    return expectedControl != null && expectedControl.equals(entry.getKey());
                                }
                            }

                            // Если не можем определить по тексту, используем эвристику:
                            // считаем, что проверка связана с контролом, если они в одной паре
                            return true;
                        })
                        .findFirst()
                        .ifPresent(entry -> {
                            // Подсвечиваем контрол с ошибкой
                            Control errorControl = entry.getKey();
                            errorControl.setStyle("-fx-border-color: #d9534f; -fx-border-width: 1.5px;");
                        });
            });
        });
    }

    // Вспомогательный метод для извлечения имени поля из текста ошибки
    private String getFieldNameFromError(String errorText) {
        if (errorText.contains("'")) {
            String[] parts = errorText.split("'");
            if (parts.length >= 2) {
                return parts[1]; // Часть между кавычками - это имя поля
            }
        }
        return null;
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
                if (fieldConfig.getItemsLoader() != null) {
                    comboBox.setItems(fieldConfig.getItemsLoader().apply(parentController.getCurrentFilterValues()));
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
        } else if (item instanceof SocialStatus) {
            return ((SocialStatus) item).getName();
        } else if (item instanceof Service) {
            return ((Service) item).getName();
        } else if (item instanceof DocumentType) {
            return ((DocumentType) item).getDescription();
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


    @SuppressWarnings("unchecked")
    private void setControlValue(Control control, Object value) {
        if (control instanceof TextField && value != null) {
            ((TextField) control).setText(value.toString());
        } else if (control instanceof ComboBox && value != null) {
            ComboBox<Object> comboBox = (ComboBox<Object>) control;

            Object itemToSelect = null;

            if (value instanceof Integer) {
                itemToSelect = findItemById(comboBox, (Integer) value);
            } else if (value instanceof String stringValue) {
                for (Object item : comboBox.getItems()) {
                    String itemDisplay = getItemDisplayText(item);
                    if (stringValue.equals(itemDisplay)) {
                        itemToSelect = item;
                        break;
                    }
                }
            } else {
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
            } else if (item instanceof Room && ((Room) item).getId() == id) {
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
            } else if (item instanceof HotelService && ((HotelService) item).getId() == id) {
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

        if (entity instanceof Room && controllerMode == FormController.Mode.ADD) {
            Map<String, Object> filters = parentController.getCurrentFilterValues();
            Object hotelObj = filters.get("hotel");
            if (hotelObj instanceof Hotel selectedHotel) {
                ((Room) entity).setHotelId(selectedHotel.getId());
            }
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
                Object roomObj = filters.get("room");
                Object clientObj = filters.get("client");

                if (roomObj instanceof Room selectedRoom && clientObj instanceof Tenant selectedClient) {
                    try {
                        Connection connection = Session.getConnection();
                        ResultSet rs = Database_functions.callFunction(connection, "get_active_booking_by_room_and_tenant",
                                selectedRoom.getId(), selectedClient.getId());

                        if (rs.next()) {
                            String bookingNumber = rs.getString(1);
                            if (bookingNumber != null && !bookingNumber.isEmpty()) {
                                ((ServiceHistory) entity).setHistoryId(bookingNumber);
                            } else {
                                throw new Exception("Активное бронирование для данной комнаты и клиента не найдено.");
                            }
                        } else {
                            throw new Exception("Активное бронирование не найдено.");
                        }
                    } catch (Exception e) {
                        showError(statusLabel, "Ошибка: " + e.getMessage());
                    }
                } else {
                    showError(statusLabel, "Ошибка: Комната или клиент не выбраны в фильтре.");
                }
            }
        }

        for (FieldConfig fieldConfig : config.getFields()) {
            Control control = formControls.get(fieldConfig.getPropertyName());
            if (control != null) {
                Object value = getControlValue(control, fieldConfig.getType());

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
                Object value = getControlValue(control, fieldConfig.getType());

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
                        user.setHotelInfo(value != null ? value.toString() : "");
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
            if (value instanceof Double) {
                return ((Double) value).intValue();
            }
            if (value.toString().isEmpty()) return null;
            return Integer.parseInt(value.toString());
        } else if (fieldType == BigDecimal.class) {
            return new BigDecimal(value.toString());
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value.toString());
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (fieldType == LocalDate.class) {
            return value;
        } else if (fieldType == BookingStatus.class) {
            if (value instanceof BookingStatus) {
                return value;
            } else if (value instanceof String) {
                return BookingStatus.getBookingStatus((String) value);
            }
        } else if (fieldType == DocumentType.class) {
            if (value instanceof DocumentType) {
                return value;
            } else if (value instanceof String) {
                return DocumentType.getDocumentType((String) value);
            }
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private Object getControlValue(Control control, FieldConfig.FieldType fieldType) {
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
                    return null;
                }
            }
            return text;
        } else if (control instanceof ComboBox) {
            ComboBox<Object> comboBox = (ComboBox<Object>) control;
            Object selectedValue = comboBox.getValue();

            if (selectedValue != null) {
                try {
                    Method getIdMethod = selectedValue.getClass().getMethod("getId");
                    Object idValue = getIdMethod.invoke(selectedValue);
                    if (idValue instanceof Integer) {
                        return idValue;
                    }
                } catch (Exception e) {
                    // Ignore
                }
                try {
                    Method getCityIdMethod = selectedValue.getClass().getMethod("getCityId");
                    Object idValue = getCityIdMethod.invoke(selectedValue);
                    if (idValue instanceof Integer) {
                        return idValue;
                    }
                } catch (Exception e) {
                    // Ignore
                }

                if (selectedValue instanceof TenantHistory) {
                    return ((TenantHistory) selectedValue).getBookingNumber();
                }

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

    private void createFormFields(GridPane grid) {
        int row = 0;
        for (FieldConfig fieldConfig : config.getFields()) {
            Label label = new Label(fieldConfig.getLabel() + (fieldConfig.isRequired() ? " *" : ""));
            label.setStyle("-fx-font-weight: bold;");
            formLabels.put(fieldConfig.getPropertyName(), label);

            Control control = createControl(fieldConfig);

            formControls.put(fieldConfig.getPropertyName(), control);

            grid.add(label, 0, row);
            grid.add(control, 1, row);

            // Добавляем валидацию для поля
            createFieldValidation(fieldConfig, control);

            row++;
        }
    }

    private void createFieldValidation(FieldConfig fieldConfig, Control control) {
        Check check = validator.createCheck();

        // Настраиваем зависимость от значения контрола
        ObservableValue<?> observableValue = getObservableValue(control);
        if (observableValue != null) {
            check.dependsOn(fieldConfig.getPropertyName(), observableValue);
        }

        // Настраиваем проверки в зависимости от типа поля
        configureValidationRules(check, fieldConfig, control);

        // Декорируем контрол
        check.decorates(control);
        check.immediate();

        // Сохраняем проверку для последующего использования
        validationChecks.put(control, check);
    }

    private void configureValidationRules(Check check, FieldConfig fieldConfig, Control control) {
        check.withMethod(context -> {
            Object value = context.get(fieldConfig.getPropertyName());
            boolean hasError = false;
            String errorMessage = "";

            // Проверка обязательности
            if (fieldConfig.isRequired()) {
                if (value == null ||
                        (value instanceof String && ((String) value).trim().isEmpty()) ||
                        (value instanceof Number && ((Number) value).doubleValue() == 0)) {
                    hasError = true;
                    errorMessage = "Поле '" + fieldConfig.getLabel() + "' обязательно для заполнения";
                }
            }

            // Проверка по регулярному выражению
            if (!hasError && fieldConfig.getValidationRegex() != null && value instanceof String text) {
                if (!text.trim().isEmpty() && !text.matches(fieldConfig.getValidationRegex())) {
                    hasError = true;
                    errorMessage = "Некорректный формат для поля '" + fieldConfig.getLabel() + "'";
                }
            }

            // Специфичные проверки по типу поля
            if (!hasError) {
                switch (fieldConfig.getType()) {
                    case EMAIL:
                        if (value instanceof String email && !email.trim().isEmpty()) {
                            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
                            if (!email.matches(emailRegex)) {
                                hasError = true;
                                errorMessage = "Некорректный формат email";
                            }
                        }
                        break;
                    case NUMBER:
                        if (value instanceof String text && !text.trim().isEmpty()) {
                            try {
                                Double.parseDouble(text);
                            } catch (NumberFormatException e) {
                                hasError = true;
                                errorMessage = "Введите корректное число";
                            }
                        }
                        break;
                    case COMBOBOX:
                        if (fieldConfig.isRequired() && value == null) {
                            hasError = true;
                            errorMessage = "Выберите значение из списка";
                        }
                        break;
                    case DATE:
                        if (fieldConfig.isRequired() && value == null) {
                            hasError = true;
                            errorMessage = "Выберите дату";
                        }
                        break;
                }
            }

            if (hasError) {
                context.error(errorMessage);
            }
        });
    }

    private ObservableValue<?> getObservableValue(Control control) {
        if (control instanceof TextInputControl) {
            return ((TextInputControl) control).textProperty();
        } else if (control instanceof ComboBox) {
            return ((ComboBox<?>) control).valueProperty();
        } else if (control instanceof DatePicker) {
            return ((DatePicker) control).valueProperty();
        } else if (control instanceof CheckBox) {
            return ((CheckBox) control).selectedProperty();
        }
        return null;
    }

}

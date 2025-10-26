package app.subd.components;

import app.subd.admin_panels.AdminController;
import app.subd.config.UniversalFormConfig;
import app.subd.config.FieldConfig;
import app.subd.models.User;
import app.subd.tables.AllDictionaries;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Field;
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

    private FormController.Mode controllerMode;

    private final Map<String, Control> formControls = new HashMap<>();
    private final Map<String, Label> fieldLabels = new HashMap<>();

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
        fieldLabels.clear();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 10;");

        createFormFields(grid);

        formContainer.getChildren().add(grid);

        if (item != null && controllerMode == FormController.Mode.EDIT) {
            populateForm();
        }

        setupEventListeners();
        validateForm();
    }

    private void createFormFields(GridPane grid) {
        int row = 0;
        for (FieldConfig fieldConfig : config.getFields()) {
            Label label = new Label(fieldConfig.getLabel() + (fieldConfig.isRequired() ? " *" : ""));
            label.setStyle("-fx-font-weight: bold;");

            Control control = createControl(fieldConfig);

            formControls.put(fieldConfig.getPropertyName(), control);
            fieldLabels.put(fieldConfig.getPropertyName(), label);

            grid.add(label, 0, row);
            grid.add(control, 1, row);

            row++;
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
                ComboBox<String> comboBox = new ComboBox<>();
                if (fieldConfig.getItemsSupplier() != null) {
                    comboBox.setItems((ObservableList<String>) fieldConfig.getItemsSupplier().get());
                }
                if (fieldConfig.getPromptText() != null) {
                    comboBox.setPromptText(fieldConfig.getPromptText());
                }
                if (fieldConfig.getWidth() > 0) {
                    comboBox.setPrefWidth(fieldConfig.getWidth());
                }
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

    private void setupEventListeners() {
        // Валидация формы при изменении любых полей
        for (Control control : formControls.values()) {
            if (control instanceof TextInputControl) {
                ((TextInputControl) control).textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            } else if (control instanceof ComboBox) {
                ((ComboBox<?>) control).valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
            } else if (control instanceof DatePicker) {
                ((DatePicker) control).valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
            } else if (control instanceof CheckBox) {
                ((CheckBox) control).selectedProperty().addListener((observable, oldValue, newValue) -> validateForm());
            }
        }
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

                    setControlValue(control, value, fieldConfig);
                }
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка заполнения формы: " + e.getMessage());
        }
    }

    private void setControlValue(Control control, Object value, FieldConfig fieldConfig) {
        if (control instanceof TextField && value != null) {
            ((TextField) control).setText(value.toString());
        } else if (control instanceof ComboBox && value != null) {
            ComboBox<String> comboBox = (ComboBox<String>) control;

            if (value instanceof Integer) {
                String name = convertIdToName((Integer) value, fieldConfig.getPropertyName());
                if (name != null) {
                    comboBox.setValue(name);
                }
            } else if (value instanceof String) {
                comboBox.setValue((String) value);
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

    private String convertIdToName(Integer id, String propertyName) {
        if (id == null) return null;

        try {
            return switch (propertyName) {
                case "cityId" -> {
                    AllDictionaries.initialiseCitiesMaps();
                    yield AllDictionaries.getCitiesNameMap().get(id);
                }
                case "hotelId" -> {
                    AllDictionaries.initialiseHotelsMaps();
                    yield AllDictionaries.getHotelsNameMap().get(id);
                }
                case "typeOfRoomId" -> {
                    AllDictionaries.initialiseTypesOfRoomMaps();
                    yield AllDictionaries.getTypesOfRoomNameMap().get(id);
                }
                case "convNameId" -> {
                    AllDictionaries.initialiseConveniencesMaps();
                    yield AllDictionaries.getConveniencesNameMap().get(id);
                }
                default -> null;
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        for (FieldConfig fieldConfig : config.getFields()) {
            if (fieldConfig.isRequired()) {
                Control control = formControls.get(fieldConfig.getPropertyName());
                boolean fieldValid = validateField(control, fieldConfig);

                if (!fieldValid) {
                    isValid = false;
                    fieldLabels.get(fieldConfig.getPropertyName()).setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else {
                    fieldLabels.get(fieldConfig.getPropertyName()).setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
                }
            }
        }

        saveButton.setDisable(!isValid);

        if (isValid) {
            clearStatus(statusLabel);
        } else {
            showError(statusLabel, "Заполните все обязательные поля");
        }
        return isValid;
    }

    private boolean validateField(Control control, FieldConfig fieldConfig) {
        switch (control) {
            case null -> {
                return false;
            }
            case TextInputControl textInputControl -> {
                String text = textInputControl.getText();
                return text != null && !text.trim().isEmpty();
            }
            case ComboBox comboBox -> {
                Object value = comboBox.getValue();
                return value != null;
            }
            case DatePicker datePicker -> {
                LocalDate value = datePicker.getValue();
                return value != null;
            }
            case CheckBox ignored -> {
                return true;
            }
            default -> {
            }
        }

        return false;
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showError(statusLabel, "Заполните все обязательные поля");
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
        return (T) entityClass.newInstance();
    }

    private void populateEntityFromForm(T entity) throws Exception {
        if (entity instanceof User) {
            populateUserFromForm((User) entity);
            return;
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

    private void populateUserFromForm(User user) throws Exception {
        String password = null;
        String confirmPassword = null;

        for (FieldConfig fieldConfig : config.getFields()) {
            Control control = formControls.get(fieldConfig.getPropertyName());
            if (control != null) {
                Object value = getControlValue(control, fieldConfig.getType(), fieldConfig.getPropertyName());

                switch (fieldConfig.getPropertyName()) {
                    case "username":
                        user.setUsername(value != null ? value.toString() : "");
                        break;
                    case "password":
                        password = value != null ? value.toString() : null;
                        break;
                    case "confirmPassword":
                        confirmPassword = value != null ? value.toString() : null;
                        break;
                    case "role":
                        user.setRole(value != null ? value.toString() : "");
                        break;
                    case "hotelInfo":
                        // Сохраняем hotelInfo как строку, ID получим при сохранении
                        user.setHotelInfo(value != null ? value.toString() : "");
                        break;
                    case "userLocked":
                        if (value != null) {
                            user.setUserLocked(Boolean.TRUE.equals(value));
                        }
                        break;
                }
            }
        }

        if (password != null && !password.isEmpty()) {
            try {
                Field passwordField = user.getClass().getDeclaredField("tempPassword");
                passwordField.setAccessible(true);
                passwordField.set(user, password);
            } catch (NoSuchFieldException ignored) {
            }
        }
    }

    private Object convertValueToFieldType(Object value, Class<?> fieldType) {
        if (value == null) return null;

        if (fieldType == String.class) {
            return value.toString();
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(value.toString());
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
            String selectedValue = ((ComboBox<String>) control).getValue();
            return convertNameToId(selectedValue, propertyName);
        } else if (control instanceof DatePicker) {
            return ((DatePicker) control).getValue();
        } else if (control instanceof CheckBox) {
            return ((CheckBox) control).isSelected();
        } else if (control instanceof TextArea) {
            return ((TextArea) control).getText();
        }
        return null;
    }

    private Integer convertNameToId(String name, String propertyName) {
        if (name == null) return null;

        try {
            switch (propertyName) {
                case "cityId":
                    AllDictionaries.initialiseCitiesMaps();
                    return AllDictionaries.getCitiesIdMap().get(name);
                case "hotelId":
                    AllDictionaries.initialiseHotelsMaps();
                    return AllDictionaries.getHotelsIdMap().get(name);
                case "typeOfRoomId":
                    AllDictionaries.initialiseTypesOfRoomMaps();
                    return AllDictionaries.getTypesOfRoomIdMap().get(name);
                case "convNameId":
                    AllDictionaries.initialiseConveniencesMaps();
                    return AllDictionaries.getConveniencesIdMap().get(name);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        validateForm();
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
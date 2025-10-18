package app.subd.components;

import app.subd.admin_panels.AdminController;
import app.subd.config.UniversalFormConfig;
import app.subd.config.FieldConfig;
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
    private AdminController.RefreshableController parentController;
    private Stage stage;

    private FormController.Mode controllerMode;

    private Map<String, Control> formControls = new HashMap<>();
    private Map<String, Label> fieldLabels = new HashMap<>();

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
        this.parentController = parentController;
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

        // Устанавливаем заголовок
        String title = config.getFormTitle();
        if (controllerMode != null) {
            title = (controllerMode == FormController.Mode.ADD ? "Добавление" : "Редактирование") +
                    " " + config.getFormTitle().toLowerCase();
        }
        titleLabel.setText(title);

        // Очищаем контейнер
        formContainer.getChildren().clear();
        formControls.clear();
        fieldLabels.clear();

        // Создаем GridPane для формы
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 10;");

        // Создаем поля формы
        createFormFields(grid);

        formContainer.getChildren().add(grid);

        // Заполняем форму данными, если это редактирование
        if (item != null && controllerMode == FormController.Mode.EDIT) {
            populateForm();
        }

        // Настраиваем обработчики событий
        setupEventListeners();
        validateForm();
    }

    private void createFormFields(GridPane grid) {
        int row = 0;
        for (FieldConfig fieldConfig : config.getFields()) {
            // Создаем Label для поля
            Label label = new Label(fieldConfig.getLabel() + (fieldConfig.isRequired() ? " *" : ""));
            label.setStyle("-fx-font-weight: bold;");

            // Создаем элемент управления в зависимости от типа
            Control control = createControl(fieldConfig);

            // Сохраняем ссылки на элементы
            formControls.put(fieldConfig.getPropertyName(), control);
            fieldLabels.put(fieldConfig.getPropertyName(), label);

            // Добавляем в сетку
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
                ComboBox<Object> comboBox = new ComboBox<>();
                if (fieldConfig.getItemsSupplier() != null) {
                    comboBox.setItems((ObservableList<Object>) fieldConfig.getItemsSupplier().get());
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
                CheckBox checkBox = new CheckBox();
                return checkBox;

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
                    // Получаем значение поля из объекта через reflection
                    Field field = item.getClass().getDeclaredField(fieldConfig.getPropertyName());
                    field.setAccessible(true);
                    Object value = field.get(item);

                    setControlValue(control, value, fieldConfig.getType());
                }
            }
        } catch (Exception e) {
            showError(statusLabel, "Ошибка заполнения формы: " + e.getMessage());
        }
    }

    private void setControlValue(Control control, Object value, FieldConfig.FieldType fieldType) {
        if (control instanceof TextField && value != null) {
            ((TextField) control).setText(value.toString());
        } else if (control instanceof ComboBox && value != null) {
            ((ComboBox<Object>) control).setValue(value);
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

    private boolean validateForm() {
        boolean isValid = true;

        for (FieldConfig fieldConfig : config.getFields()) {
            if (fieldConfig.isRequired()) {
                Control control = formControls.get(fieldConfig.getPropertyName());
                boolean fieldValid = validateField(control, fieldConfig);

                if (!fieldValid) {
                    isValid = false;
                    // Подсвечиваем невалидное поле
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
        if (control == null) return false;

        if (control instanceof TextInputControl) {
            String text = ((TextInputControl) control).getText();
            return text != null && !text.trim().isEmpty();
        } else if (control instanceof ComboBox) {
            Object value = ((ComboBox<?>) control).getValue();
            return value != null;
        } else if (control instanceof DatePicker) {
            LocalDate value = ((DatePicker) control).getValue();
            return value != null;
        } else if (control instanceof CheckBox) {
            // CheckBox всегда валиден, даже если не выбран
            return true;
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
            // Создаем или используем существующий объект
            T entity = item != null ? item : createNewInstance();

            // Заполняем объект данными из формы
            populateEntityFromForm(entity);

            // Сохраняем объект
            boolean success = config.getSaveFunction().apply(entity);

            if (success) {
                String successMessage = (controllerMode == FormController.Mode.ADD ?
                        config.getMode().getSuccessMessage() : "Данные успешно обновлены");

                showSuccess(statusLabel, successMessage);

                // Вызываем callback при успешном сохранении
                if (onSaveSuccess != null) {
                    onSaveSuccess.run();
                }

                // Вызываем внешний обработчик успеха
                if (config.getOnSuccess() != null) {
                    config.getOnSuccess().accept(entity);
                }

                // Если это добавление - очищаем форму для следующего ввода
                if (controllerMode == FormController.Mode.ADD) {
                    clearForm();
                } else {
                    // Если редактирование - закрываем окно
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
        Class<?> entityClass = config.getClass().getMethod("getEntityClass").getReturnType();
        return (T) entityClass.newInstance();
    }

    private void populateEntityFromForm(T entity) throws Exception {
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
                    return 0;
                }
            }
            return text;
        } else if (control instanceof ComboBox) {
            return ((ComboBox<?>) control).getValue();
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
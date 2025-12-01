package app.subd.components;

import app.subd.config.ColumnConfig;
import app.subd.config.FieldConfig;
import app.subd.config.TableConfig;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;


public class ColumnFilterController {

    @FXML private VBox filtersContainer;
    @FXML private Button applyButton;
    @FXML private Button clearButton;
    @FXML private Button cancelButton;

    private TableConfig tableConfig;
    private UniversalTableController parentController;
    private Stage stage;
    private final Map<String, Control> filterControls = new HashMap<>();

    public void configure(TableConfig config, UniversalTableController parent) {
        this.tableConfig = config;
        this.parentController = parent;
        initializeFilters();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Инициализация, если нужна
    }

    private void initializeFilters() {
        filtersContainer.getChildren().clear();
        filterControls.clear();

        if (tableConfig == null || !tableConfig.hasFilterableColumns()) {
            Label noFiltersLabel = new Label("Нет доступных столбцов для фильтрации");
            noFiltersLabel.setStyle("-fx-font-style: italic;");
            filtersContainer.getChildren().add(noFiltersLabel);
            applyButton.setDisable(true);
            clearButton.setDisable(true);
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 15;");

        int row = 0;
        for (ColumnConfig column : tableConfig.getColumns()) {
            if (column.isFilterable()) {
                Label label = new Label(column.getColumnName());
                label.setStyle("-fx-font-weight: bold;");

                Control filterControl = createFilterControl(column);
                filterControls.put(column.getPropertyName(), filterControl);

                grid.add(label, 0, row);
                grid.add(filterControl, 1, row);
                row++;
            }
        }

        filtersContainer.getChildren().add(grid);
    }

    private Control createFilterControl(ColumnConfig column) {
        switch (column.getFilterType()) {
            case TEXT:
                TextField textField = new TextField();
                textField.setPromptText(column.getFilterPrompt() != null ?
                        column.getFilterPrompt() : "Введите текст...");
                textField.setPrefWidth(250);

                textField.setOnAction(e -> handleApply());
                return textField;

            case NUMBER:
                TextField numberField = new TextField();
                numberField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        numberField.setText(newValue.replaceAll("[^\\d.]", ""));
                    }
                });
                numberField.setPromptText(column.getFilterPrompt() != null ?
                        column.getFilterPrompt() : "Введите число...");
                numberField.setPrefWidth(250);
                numberField.setOnAction(e -> handleApply());
                return numberField;

            case DATE:
                DatePicker datePicker = new DatePicker();
                datePicker.setPromptText("Выберите дату...");
                datePicker.setPrefWidth(250);
                return datePicker;

            case COMBOBOX:
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.setPromptText("Выберите значение...");
                comboBox.setPrefWidth(250);
                return comboBox;

            case CHECKBOX:
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(false);
                return checkBox;

            default:
                TextField defaultField = new TextField();
                defaultField.setPromptText("Введите значение...");
                defaultField.setPrefWidth(250);
                return defaultField;
        }
    }

    @FXML
    private void handleApply() {
        Map<String, Object> columnFilters = new HashMap<>();

        for (ColumnConfig column : tableConfig.getColumns()) {
            if (column.isFilterable()) {
                Control control = filterControls.get(column.getPropertyName());
                Object value = getFilterValue(control, column.getFilterType());

                if (value != null) {
                    if (value instanceof String) {
                        String stringValue = ((String) value).trim();
                        if (!stringValue.isEmpty()) {
                            columnFilters.put(column.getPropertyName(), stringValue);
                        }
                    } else {
                        columnFilters.put(column.getPropertyName(), value);
                    }
                }
            }
        }

        parentController.applyColumnFilters(columnFilters);
        closeWindow();
    }

    @FXML
    private void handleClear() {
        for (Control control : filterControls.values()) {
            clearControl(control);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private Object getFilterValue(Control control, FieldConfig.FieldType ignored) {
        if (control instanceof TextField) {
            return ((TextField) control).getText();
        } else if (control instanceof DatePicker) {
            return ((DatePicker) control).getValue();
        } else if (control instanceof ComboBox) {
            return ((ComboBox<?>) control).getValue();
        } else if (control instanceof CheckBox) {
            return ((CheckBox) control).isSelected();
        }
        return null;
    }

    private void clearControl(Control control) {
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

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
    }
}
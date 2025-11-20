package app.subd.components;

import app.subd.admin_panels.AdminController;
import app.subd.config.TableConfig;
import app.subd.config.ColumnConfig;
import app.subd.config.FilterConfig;
import app.subd.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class UniversalTableController implements AdminController.RefreshableController {

    @FXML private VBox filtersContainer;
    @FXML private TableView<Object> tableView;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Button clearFiltersButton;
    @FXML private Button toggleActiveButton;
    @FXML private Label statusLabel;

    private TableConfig currentConfig;
    private ObservableList<Object> originalData;
    private SortedList<Object> sortedData;
    private Map<String, Node> activeFilterControls = new HashMap<>();
    private final Map<String, Object> currentFilterValues = new HashMap<>();
    private final Map<String, FilterConfig> filterConfigs = new HashMap<>();

    @FXML
    public void initialize() {
        setupTable();
        setupEventHandlers();
        setupPaginationStub();
    }

    private void setupPaginationStub() {
        Pagination pagination = new Pagination();
        pagination.setPageCount(1); // Stub
        pagination.setCurrentPageIndex(0); // Stub
        pagination.setMaxPageIndicatorCount(5);

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            // TODO: Implement pagination logic. This is a stub.
            // It should pass the new page index to the data loader.
            if (statusLabel != null) {
                showSuccess(statusLabel, "Переход на страницу " + (newIndex.intValue() + 1) + ". Пагинация еще не реализована.");
            }
            // In a real implementation, you would call:
            // refreshData();
        });

        // Add the pagination control to the layout, typically below the table.
        Node parentNode = tableView.getParent();
        if (parentNode instanceof VBox) {
            ((VBox) parentNode).getChildren().add(pagination);
        } else if (parentNode != null && parentNode.getParent() instanceof VBox) {
            ((VBox) parentNode.getParent()).getChildren().add(pagination);
        }
    }

    public void configure(TableConfig config) {
        this.currentConfig = config;
        applyConfiguration();
    }

    private void applyConfiguration() {
        if (currentConfig == null) return;

        clearPreviousConfiguration();
        tableView.getSelectionModel().setSelectionMode(currentConfig.isMultiSelect() ? SelectionMode.MULTIPLE : SelectionMode.SINGLE);
        setupTableColumns();
        setupFilters();
        refreshData();
        setupActionHandlers();
    }

    private void clearPreviousConfiguration() {
        tableView.getColumns().clear();
        filtersContainer.getChildren().clear();
        activeFilterControls.clear();
        currentFilterValues.clear();
        filterConfigs.clear();
    }

    private void setupTable() {
        originalData = FXCollections.observableArrayList();
        FilteredList<Object> filteredData = new FilteredList<>(originalData, p -> true);
        sortedData = new SortedList<>(filteredData);
        tableView.setItems(sortedData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
    }

    private void setupTableColumns() {
        for (ColumnConfig columnConfig : currentConfig.getColumns()) {
            TableColumn<Object, Object> column = new TableColumn<>(columnConfig.getColumnName());
            column.setCellValueFactory(new PropertyValueFactory<>(columnConfig.getPropertyName()));

            if (columnConfig.getWidth() > 0) {
                column.setPrefWidth(columnConfig.getWidth());
            }

            tableView.getColumns().add(column);
        }
    }

    private void setupFilters() {
        filtersContainer.getChildren().clear();
        if (currentConfig.getFilters() != null && !currentConfig.getFilters().isEmpty()) {
            FlowPane filtersPane = new FlowPane(15, 5); // hgap, vgap
            filtersPane.setPadding(new Insets(5));
            for (FilterConfig filterConfig : currentConfig.getFilters()) {
                createFilter(filterConfig, filtersPane);
            }
            filtersContainer.getChildren().add(filtersPane);
        }
    }


    private void createFilter(FilterConfig filterConfig, Pane container) {
        VBox filterBox = new VBox(5);
        Label label = new Label(filterConfig.getLabel() + (filterConfig.isRequired() ? " *" : ""));

        Node control;

        switch (filterConfig.getFilterType()) {
            case COMBOBOX:
                ComboBox<Object> comboBox = new ComboBox<>();
                comboBox.setPromptText("Выберите...");
                updateComboBoxItems(comboBox, filterConfig);

                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    handleFilterChange(filterConfig.getFilterKey(), newValue);
                    updateDependentFilters(filterConfig.getFilterKey());
                });

                control = comboBox;
                break;

            case DATE:
                DatePicker datePicker = new DatePicker();
                datePicker.setPromptText("Выберите дату...");

                datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                    handleFilterChange(filterConfig.getFilterKey(), newValue);
                });

                control = datePicker;
                break;

            case TEXT:
                TextField textField = new TextField();
                textField.setPromptText("Введите текст...");

                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    handleFilterChange(filterConfig.getFilterKey(), newValue.isEmpty() ? null : newValue);
                });

                control = textField;
                break;

            case NUMBER:
                TextField numberField = new TextField();
                numberField.setPromptText("Введите число...");

                numberField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.matches("\\d*")) {
                        handleFilterChange(filterConfig.getFilterKey(),
                                newValue.isEmpty() ? null : Integer.parseInt(newValue));
                    } else {
                        numberField.setText(oldValue);
                    }
                });

                control = numberField;
                break;

            default:
                ComboBox<Object> defaultComboBox = new ComboBox<>();
                defaultComboBox.setPromptText("Выберите...");
                control = defaultComboBox;
                break;
        }

        filterBox.getChildren().addAll(label, control);
        container.getChildren().add(filterBox);
        activeFilterControls.put(filterConfig.getFilterKey(), control);
        filterConfigs.put(filterConfig.getFilterKey(), filterConfig);
    }

    private void updateComboBoxItems(ComboBox<Object> comboBox, FilterConfig filterConfig) {
        try {
            ObservableList<?> rawItems = filterConfig.getItemsFunction().apply(currentFilterValues);
            ObservableList<Object> items = FXCollections.observableArrayList(rawItems);

            Object currentValue = comboBox.getValue();
            comboBox.setItems(items);

            if (currentValue != null && items.contains(currentValue)) {
                comboBox.setValue(currentValue);
            } else {
                comboBox.setValue(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            comboBox.setItems(FXCollections.observableArrayList());
        }
    }

    private void updateDependentFilters(String changedFilterKey) {
        for (FilterConfig filterConfig : filterConfigs.values()) {
            if (changedFilterKey.equals(filterConfig.getDependsOnFilter()) &&
                    filterConfig.getFilterType() == FilterConfig.FilterType.COMBOBOX) {

                Node control = activeFilterControls.get(filterConfig.getFilterKey());
                if (control instanceof ComboBox) {
                    ComboBox<Object> dependentComboBox = (ComboBox<Object>) control;
                    dependentComboBox.setValue(null);
                    updateComboBoxItems(dependentComboBox, filterConfig);
                }
            }
        }
    }

    private void handleFilterChange(String filterKey, Object newValue) {
        if (newValue != null) {
            currentFilterValues.put(filterKey, newValue);
        } else {
            currentFilterValues.remove(filterKey);
        }
        refreshData();
    }

    private void refreshAllFilters() {
        for (Map.Entry<String, Node> entry : activeFilterControls.entrySet()) {
            String filterKey = entry.getKey();
            Node control = entry.getValue();
            FilterConfig config = filterConfigs.get(filterKey);

            if (config != null && control instanceof ComboBox) {
                ComboBox<Object> comboBox = (ComboBox<Object>) control;
                updateComboBoxItems(comboBox, config);
            }
        }
    }

    private void setupEventHandlers() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateButtonsState(newValue != null);
                    if (toggleActiveButton != null && toggleActiveButton.isVisible()) {
                        if (newValue instanceof User user) {
                            toggleActiveButton.setText(user.getUserLocked() ? "Разблокировать" : "Заблокировать");
                        }/* else if (currentConfig != null && !"Пользователи".equals(currentConfig.getTableName())) {
                            // Do nothing, keep the text set in setupActionHandlers
                        } else {
                            toggleActiveButton.setText("Блокировка");
                        }*/
                    }
                }
        );
    }

    private void setupActionHandlers() {
        if (addButton != null) {
            addButton.setVisible(currentConfig.getOnAdd() != null);
            if (currentConfig != null) {
                if ("Бронирования".equals(currentConfig.getTableName())) {
                    addButton.setText("Заселение в номер");
                } else if ("Счета на оплату".equals(currentConfig.getTableName())) {
                    addButton.setText("Формирование счетов");
                }
            }
        }

        if (editButton != null) {
            editButton.setVisible(currentConfig.getOnEdit() != null);
            if (currentConfig != null && "Счета на оплату".equals(currentConfig.getTableName())) {
                editButton.setText("Детальная информация");
            }
        }

        if (deleteButton != null) {
            deleteButton.setVisible(false);
        }

        if (toggleActiveButton != null) {
            toggleActiveButton.setVisible(currentConfig.getOnToggleActive() != null);
            if (currentConfig != null && "Бронирования".equals(currentConfig.getTableName())) {
                toggleActiveButton.setText("Бронирование номера");
                toggleActiveButton.setVisible(true);
            }
            if (currentConfig != null && "Счета на оплату".equals(currentConfig.getTableName())) {
                toggleActiveButton.setText("Изменить статус оплаты");
                toggleActiveButton.setVisible(currentConfig.getOnToggleActive() != null);
            }
        }
    }

    private void loadData() {
        if (currentConfig != null && currentConfig.getDataLoader() != null) {
            try {
                ObservableList<Object> newData = currentConfig.getDataLoader().apply(currentFilterValues);
                originalData.clear();
                originalData.addAll(newData);
                if (statusLabel != null) {
                    showSuccess(statusLabel, "Загружено записей: " + originalData.size());
                }
            } catch (Exception e) {
                if (statusLabel != null) {
                    showError(statusLabel, "Ошибка загрузки данных: " + e.getMessage());
                }
                e.printStackTrace();
            }
        }
    }

    private void updateButtonsState(boolean hasSelection) {
        if (editButton != null) {
            editButton.setDisable(!hasSelection);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(!hasSelection);
        }
        if (toggleActiveButton != null) {
            if (currentConfig != null && "Бронирования".equals(currentConfig.getTableName()) && currentConfig.getOnBooking() != null) {
                toggleActiveButton.setDisable(false);
            } else {
                toggleActiveButton.setDisable(!hasSelection);
            }
        }
    }

    @FXML
    private void handleToggleActive() {
        if (currentConfig != null && "Бронирования".equals(currentConfig.getTableName()) && currentConfig.getOnBooking() != null) {
            currentConfig.getOnBooking().call(null);
            return;
        }
        Object selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null && currentConfig != null && currentConfig.getOnToggleActive() != null) {
            currentConfig.getOnToggleActive().call(selected);
        }
    }

    @FXML
    private void handleAdd() {
        if (currentConfig != null && currentConfig.getOnAdd() != null) {
            currentConfig.getOnAdd().call(null);
        }
    }

    @FXML
    private void handleEdit() {
        Object selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null && currentConfig != null && currentConfig.getOnEdit() != null) {
            currentConfig.getOnEdit().call(selected);
        }
    }

    @FXML
    private void handleDelete() {
        Object selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showError(statusLabel, "Функция удаления пока не реализована");
        }
    }

    @FXML
    public void handleRefresh() {
        refreshData();
    }

    @FXML
    private void handleClearFilters() {
        clearFilters();
        refreshData();
    }

    private void clearFilters() {
        currentFilterValues.clear();

        for (Node control : activeFilterControls.values()) {
            if (control instanceof ComboBox) {
                ((ComboBox<?>) control).setValue(null);
            } else if (control instanceof DatePicker) {
                ((DatePicker) control).setValue(null);
            } else if (control instanceof TextField) {
                ((TextField) control).setText("");
            }
        }

        refreshAllFilters();
    }

    public void refreshData() {
        loadData();
    }

    public Object getSelectedItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    public Map<String, Object> getCurrentFilterValues() {
        return currentFilterValues;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getToggleActiveButton() {
        return toggleActiveButton;
    }

    public TableView<Object> getTableView() {
        return tableView;
    }
}
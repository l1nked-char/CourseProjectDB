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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

import static app.subd.MessageController.*;

public class UniversalTableController implements AdminController.RefreshableController {

    @FXML private VBox filtersContainer;
    @FXML private TextField searchField;
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
    private FilteredList<Object> filteredData;
    private SortedList<Object> sortedData;
    private final Map<String, ComboBox<Object>> activeFilters = new HashMap<>();
    private final Map<String, Object> currentFilterValues = new HashMap<>();
    private final Map<String, FilterConfig> filterConfigs = new HashMap<>();

    @FXML
    public void initialize() {
        setupTable();
        setupEventHandlers();
    }

    public void configure(TableConfig config) {
        this.currentConfig = config;
        applyConfiguration();
    }

    private void applyConfiguration() {
        if (currentConfig == null) return;

        clearPreviousConfiguration();
        setupTableColumns();
        setupFilters();
        refreshData();
        setupActionHandlers();
    }

    private void clearPreviousConfiguration() {
        tableView.getColumns().clear();
        filtersContainer.getChildren().clear();
        activeFilters.clear();
        currentFilterValues.clear();
        if (searchField != null) searchField.clear();
    }

    private void setupTable() {
        originalData = javafx.collections.FXCollections.observableArrayList();
        filteredData = new FilteredList<>(originalData);
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
        if (currentConfig.getFilters() != null) {
            for (FilterConfig filterConfig : currentConfig.getFilters()) {
                createFilter(filterConfig);
            }
        }
    }

    private void createFilter(FilterConfig filterConfig) {
        VBox filterBox = new VBox(5);
        Label label = new Label(filterConfig.getLabel());

        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setPromptText("Выберите...");

        // Инициализируем комбобокс с текущими значениями фильтров
        updateComboBoxItems(comboBox, filterConfig);

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleFilterChange(filterConfig.getFilterKey(), newValue);

            // Обновляем зависимые фильтры
            updateDependentFilters(filterConfig.getFilterKey());
        });

        filterBox.getChildren().addAll(label, comboBox);
        filtersContainer.getChildren().add(filterBox);
        activeFilters.put(filterConfig.getFilterKey(), comboBox);
        filterConfigs.put(filterConfig.getFilterKey(), filterConfig);
    }

    private void updateComboBoxItems(ComboBox<Object> comboBox, FilterConfig filterConfig) {
        try {
            ObservableList<?> rawItems = filterConfig.getItemsFunction().apply(currentFilterValues);
            ObservableList<Object> items;
            if (rawItems instanceof ObservableList) {
                items = (ObservableList<Object>) rawItems;
            } else {
                items = FXCollections.observableArrayList();
            }

            Object currentValue = comboBox.getValue();

            comboBox.setItems(items);

            if (currentValue != null && items.contains(currentValue)) {
                comboBox.setValue(currentValue);
            } else if (items.isEmpty()) {
                comboBox.setValue(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            comboBox.setItems(FXCollections.observableArrayList());
        }
    }

    private void updateDependentFilters(String changedFilterKey) {
        for (FilterConfig filterConfig : currentConfig.getFilters()) {
            if (changedFilterKey.equals(filterConfig.getDependsOnFilter())) {
                ComboBox<Object> dependentComboBox = activeFilters.get(filterConfig.getFilterKey());
                if (dependentComboBox != null) {
                    // Сбрасываем значение зависимого фильтра
                    dependentComboBox.setValue(null);
                    // Обновляем список значений
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
        for (Map.Entry<String, ComboBox<Object>> entry : activeFilters.entrySet()) {
            String filterKey = entry.getKey();
            ComboBox<Object> comboBox = entry.getValue();
            FilterConfig config = filterConfigs.get(filterKey);

            if (config != null) {
                updateComboBoxItems(comboBox, config);
            }
        }
    }

    private void setupEventHandlers() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateFilter();
            });
        }

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateButtonsState(newValue != null);
                    if (toggleActiveButton != null && toggleActiveButton.isVisible()) {
                        if (newValue instanceof User user) {
                            toggleActiveButton.setText(user.getUserLocked() ? "Разблокировать" : "Заблокировать");
                        } else {
                            // Сбрасываем текст, если выбран не пользователь или ничего не выбрано
                            toggleActiveButton.setText("Блокировка");
                        }
                    }
                }
        );
    }

    private void setupActionHandlers() {
        if (addButton != null) {
            addButton.setVisible(currentConfig.getOnAdd() != null);
        }
        if (editButton != null) {
            editButton.setVisible(currentConfig.getOnEdit() != null);
        }
        if (deleteButton != null) {
            deleteButton.setVisible(false);
        }
        if (toggleActiveButton != null) {
            toggleActiveButton.setVisible(currentConfig.getOnToggleActive() != null);
        }

        if (currentConfig != null) {
            // Для таблицы "Бронирования" меняем текст кнопки добавления
            if ("Бронирования".equals(currentConfig.getTableName()) && addButton != null) {
                addButton.setText("Заселение в номер");
            }

            // Для таблицы "Счета на оплату" меняем текст кнопки добавления
            if ("Счета на оплату".equals(currentConfig.getTableName()) && addButton != null) {
                addButton.setText("Формирование счетов");
            }

            // Настраиваем видимость кнопки бронирования (используем toggleActiveButton для этой цели)
            if (toggleActiveButton != null && "Бронирования".equals(currentConfig.getTableName())) {
                toggleActiveButton.setText("Бронирование номера");
                toggleActiveButton.setVisible(true);
            }
        }

    }

    private void loadData() {
        if (currentConfig != null && currentConfig.getDataLoader() != null) {
            try {
                ObservableList<Object> newData = currentConfig.getDataLoader().apply(currentFilterValues);
                originalData.clear();
                originalData.addAll(newData);
                updateFilter();
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

    private void updateFilter() {
        if (searchField == null) return;

        String searchText = searchField.getText().toLowerCase();

        filteredData.setPredicate(item -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return item.toString().toLowerCase().contains(searchText);
        });
    }

    private void updateButtonsState(boolean hasSelection) {
        if (editButton != null) {
            editButton.setDisable(!hasSelection);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(!hasSelection);
        }
        if (toggleActiveButton != null) {
            toggleActiveButton.setDisable(!hasSelection);
        }
    }

    @FXML
    private void handleToggleActive() {
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
            // Реализация удаления будет добавлена позже
            showError(statusLabel, "Функция удаления пока не реализована");
        }
    }

    @FXML
    public void handleRefresh() {
        refreshData();
    }

    @FXML
    private void handleClearFilters() {
        currentFilterValues.clear();

        if (searchField != null) {
            searchField.clear();
        }

        refreshAllFilters();
        refreshData();
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
}
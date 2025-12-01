package app.subd.components;

import app.subd.admin_panels.AdminController;
import app.subd.config.FieldConfig;
import app.subd.config.TableConfig;
import app.subd.config.ColumnConfig;
import app.subd.config.FilterConfig;
import app.subd.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import app.subd.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @FXML private Button filterButton;
    @FXML private Button toggleActiveButton;
    @FXML private Label statusLabel;

    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private TableConfig currentConfig;
    private ObservableList<Object> currentData;
    private final Map<String, Node> activeFilterControls = new HashMap<>();
    private final Map<String, Object> currentFilterValues = new HashMap<>();
    private final Map<String, FilterConfig> filterConfigs = new HashMap<>();
    private final Map<String, Object> columnFilters = new HashMap<>();

    private Integer lastLoadedId = 0;
    private final int itemsPerPage = 30;
    private int currentPageIndex = 0;
    private boolean hasMorePages = true;
    private final List<Integer> pageLastIds = new ArrayList<>(); // Stores the lastId for each page loaded

    @FXML
    public void initialize() {
        setupTable();
        setupEventHandlers();
        setupPaginationControls();
        setupFilterButton();
        pageLastIds.add(0); // Initial lastId for the first page
        updatePaginationButtonsState();
    }

    private void updatePaginationButtonsState() {
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPageIndex == 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(!hasMorePages);
        }
        if (pageInfoLabel != null) {
            pageInfoLabel.setText("Страница " + (currentPageIndex + 1));
        }
    }

    private void setupPaginationControls() {
        // This method will now be called by initialize, assuming the FXML elements are already injected.
        if (prevPageButton != null) {
            prevPageButton.setOnAction(event -> handlePreviousPage());
        }
        if (nextPageButton != null) {
            nextPageButton.setOnAction(event -> handleNextPage());
        }
        updatePaginationButtonsState();
    }

    private void handleNextPage() {
        if (hasMorePages) {
            currentPageIndex++;
            refreshData();
        }
    }

    private void handlePreviousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            lastLoadedId = (currentPageIndex == 0) ? 0 : pageLastIds.get(currentPageIndex - 1);
            refreshData();
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
        columnFilters.clear(); // Ensure column filters are also cleared
        lastLoadedId = 0;
        currentPageIndex = 0;
        hasMorePages = true;
        updatePaginationButtonsState();
    }

    private void setupTable() {
        currentData = FXCollections.observableArrayList();
        tableView.setItems(currentData);
    }

    private void setupTableColumns() {
        for (ColumnConfig columnConfig : currentConfig.getColumns()) {
            TableColumn<Object, Object> column = new TableColumn<>(columnConfig.getColumnName());
            column.setCellValueFactory(new PropertyValueFactory<>(columnConfig.getPropertyName()));

            if (columnConfig.getWidth() > 0) {
                column.setPrefWidth(columnConfig.getWidth());
            }

            if (columnConfig.getFieldType() == FieldConfig.FieldType.NUMBER) {
                column.setCellFactory(col -> new TableCell<>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else if (item instanceof Number) {
                            DecimalFormat df = new DecimalFormat("#,##0.000");
                            df.setRoundingMode(RoundingMode.HALF_UP);
                            setText(df.format(item));
                        } else {
                            setText(item.toString());
                        }
                    }
                });
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
        resetPaginationAndRefresh();
    }

    private void resetPaginationAndRefresh() {
        lastLoadedId = 0;
        currentPageIndex = 0;
        pageLastIds.clear();
        pageLastIds.add(0);
        hasMorePages = true;
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
        resetPaginationAndRefresh();
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

    private void setupFilterButton() {
        // Заменяем searchField на кнопку фильтра
        filterButton = new Button("Фильтр");
        filterButton.setOnAction(e -> openColumnFilterDialog());

        // Находим HBox с кнопками и заменяем searchField на filterButton
        Node parentNode = tableView.getParent();
        if (parentNode instanceof VBox vbox) {
            for (Node node : vbox.getChildren()) {
                if (node instanceof HBox buttonBox) {
                    // Удаляем searchField если есть
                    buttonBox.getChildren().removeIf(n -> n instanceof TextField &&
                            n.getId() != null &&
                            n.getId().equals("searchField"));
                    // Добавляем кнопку фильтра
                    buttonBox.getChildren().add(0, filterButton);
                    break;
                }
            }
        }
    }

    private void openColumnFilterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/column_filter.fxml"));
            Parent root = loader.load();

            ColumnFilterController controller = loader.getController();
            controller.configure(currentConfig, this);

            Stage stage = new Stage();
            stage.setTitle("Фильтрация - " + currentConfig.getTableName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.setMinHeight(400);
            stage.setMinWidth(400);

            controller.setStage(stage);
            stage.showAndWait();
        } catch (Exception e) {
            showError(statusLabel, "Ошибка открытия формы фильтрации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshData() {
        Map<String, Object> allFilters = new HashMap<>();
        allFilters.putAll(currentFilterValues);
        allFilters.putAll(columnFilters);

        allFilters.put("page", currentPageIndex);
        allFilters.put("lastId", lastLoadedId);
        allFilters.put("limit", itemsPerPage);

        loadData(allFilters);
    }


    private void loadData(Map<String, Object> allFilters) {
        if (currentConfig != null && currentConfig.getDataLoader() != null) {
            try {
                ObservableList<Object> newData = currentConfig.getDataLoader().apply(allFilters);
                currentData.clear();
                currentData.addAll(newData);

                if (!newData.isEmpty()) {
                    Object lastItem = newData.getLast();
                    if (lastItem instanceof Hotel) {
                        lastLoadedId = ((Hotel) lastItem).getId();
                    } else if (lastItem instanceof Room) {
                        lastLoadedId = ((Room) lastItem).getId();
                    } else if (lastItem instanceof TypeOfRoom) {
                        lastLoadedId = ((TypeOfRoom) lastItem).getId();
                    } else if (lastItem instanceof Convenience) {
                        lastLoadedId = ((Convenience) lastItem).getId();
                    } else if (lastItem instanceof City) {
                        lastLoadedId = ((City) lastItem).getCityId();
                    } else if (lastItem instanceof RoomConvenience) {
                        lastLoadedId = ((RoomConvenience) lastItem).getId();
                    } else if (lastItem instanceof HotelService) {
                        lastLoadedId = ((HotelService) lastItem).getId();
                    } else if (lastItem instanceof ServiceHistory) {
                        lastLoadedId = ((ServiceHistory) lastItem).getId();
                    } else if (lastItem instanceof SocialStatus) {
                        lastLoadedId = ((SocialStatus) lastItem).getId();
                    } else if (lastItem instanceof Service) {
                        lastLoadedId = ((Service) lastItem).getId();
                    } else if (lastItem instanceof Tenant) {
                        lastLoadedId = ((Tenant) lastItem).getId();
                    } else if (lastItem instanceof User) {
                        lastLoadedId = ((User) lastItem).getId();
                    } else if (lastItem instanceof TenantHistory) {
                        lastLoadedId = 0;
                    } else if (lastItem instanceof BookingInfo) {
                        lastLoadedId = 0;
                    } else if (lastItem instanceof AvailableRoom) {
                         lastLoadedId = ((AvailableRoom) lastItem).getRoomId();
                    }
                    else {
                        lastLoadedId = 0;
                    }
                } else {
                    lastLoadedId = 0;
                }
                hasMorePages = newData.size() == itemsPerPage;

                // Update pageLastIds for backward navigation
                if (currentPageIndex >= pageLastIds.size()) {
                    pageLastIds.add(lastLoadedId);
                } else {
                    pageLastIds.set(currentPageIndex, lastLoadedId);
                }

                updatePaginationButtonsState();

            } catch (Exception e) {
                if (statusLabel != null) {
                    showError(statusLabel, "Ошибка загрузки данных: " + e.getMessage());
                }
                e.printStackTrace();
            }
        }
    }

    public void applyColumnFilters(Map<String, Object> filters) {
        this.columnFilters.clear();
        this.columnFilters.putAll(filters);
        resetPaginationAndRefresh();
    }

    private void applyColumnFiltering() {
        // Client-side filtering is removed, this method is no longer relevant for applying filters.
        // The filtering is done directly by the data loader with updated filter values.
        showSuccess(statusLabel, "Фильтры применены.");
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

    @FXML
    private void handleClearFilters() {
        clearFilters();
        columnFilters.clear();
        resetPaginationAndRefresh();
    }
}
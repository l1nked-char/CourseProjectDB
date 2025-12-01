package app.subd.config;

import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TableConfig {
    private final String tableName;
    private final Function<Map<String, Object>, ObservableList<Object>> dataLoader;
    private final Callback<Void, Void> onAdd;
    private final Callback<Object, Void> onEdit;
    private final Callback<Void, Void> onRefresh;
    private final List<ColumnConfig> columns;
    private final List<FilterConfig> filters;
    private final Callback<Object, Void> onToggleActive;
    private final Callback<Void, Void> onBooking;
    private final boolean multiSelect;
    private final String idPropertyName;

    public TableConfig(String tableName,
                       Function<Map<String, Object>, ObservableList<Object>> dataLoader,
                       Callback<Void, Void> onAdd,
                       Callback<Object, Void> onEdit,
                       Callback<Void, Void> onRefresh,
                       List<ColumnConfig> columns,
                       List<FilterConfig> filters,
                       Callback<Object, Void> onToggleActive) {
        this(tableName, dataLoader, onAdd, onEdit, onRefresh, null, columns, filters, onToggleActive, false, "id");
    }

    public TableConfig(String tableName,
                       Function<Map<String, Object>, ObservableList<Object>> dataLoader,
                       Callback<Void, Void> onAdd,
                       Callback<Object, Void> onEdit,
                       Callback<Void, Void> onRefresh,
                       Callback<Void, Void> onBooking,
                       List<ColumnConfig> columns,
                       List<FilterConfig> filters,
                       Callback<Object, Void> onToggleActive) {
        this(tableName, dataLoader, onAdd, onEdit, onRefresh, onBooking, columns, filters, onToggleActive, false, "id");
    }

    public TableConfig(String tableName,
                       Function<Map<String, Object>, ObservableList<Object>> dataLoader,
                       Callback<Void, Void> onAdd,
                       Callback<Object, Void> onEdit,
                       Callback<Void, Void> onRefresh,
                       Callback<Void, Void> onBooking,
                       List<ColumnConfig> columns,
                       List<FilterConfig> filters,
                       Callback<Object, Void> onToggleActive,
                       boolean multiSelect) {
        this(tableName, dataLoader, onAdd, onEdit, onRefresh, onBooking, columns, filters, onToggleActive, multiSelect, "id");
    }

    public TableConfig(String tableName,
                       Function<Map<String, Object>, ObservableList<Object>> dataLoader,
                       Callback<Void, Void> onAdd,
                       Callback<Object, Void> onEdit,
                       Callback<Void, Void> onRefresh,
                       Callback<Void, Void> onBooking,
                       List<ColumnConfig> columns,
                       List<FilterConfig> filters,
                       Callback<Object, Void> onToggleActive,
                       boolean multiSelect,
                       String idPropertyName) {
        this.tableName = tableName;
        this.dataLoader = dataLoader;
        this.onAdd = onAdd;
        this.onEdit = onEdit;
        this.onRefresh = onRefresh;
        this.onBooking = onBooking;
        this.columns = columns;
        this.filters = filters;
        this.onToggleActive = onToggleActive;
        this.multiSelect = multiSelect;
        this.idPropertyName = idPropertyName;
    }

    public Function<Map<String, Object>, ObservableList<Object>> getDataLoader() {
        return dataLoader;
    }

    public String getTableName() {
        return tableName;
    }

    public Callback<Object, Void> getOnToggleActive() {
        return onToggleActive;
    }

    public Callback<Void, Void> getOnAdd() {
        return onAdd;
    }

    public Callback<Object, Void> getOnEdit() {
        return onEdit;
    }

    public Callback<Void, Void> getOnRefresh() {
        return onRefresh;
    }

    public List<ColumnConfig> getColumns() {
        return columns;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public Callback<Void, Void> getOnBooking() {
        return onBooking;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public String getIdPropertyName() {
        return idPropertyName;
    }

    public List<ColumnConfig> getFilterableColumns() {
        return columns.stream()
                .filter(ColumnConfig::isFilterable)
                .collect(Collectors.toList());
    }

    public boolean hasFilterableColumns() {
        return columns.stream().anyMatch(ColumnConfig::isFilterable);
    }
}
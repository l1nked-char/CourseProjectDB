package app.subd.config;

import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TableConfig {
    private final String tableName;
    private final Function<Map<String, Object>, ObservableList<Object>> dataLoader;
    private final Callback<Void, Void> onAdd;
    private final Callback<Object, Void> onEdit;
    private final Callback<Void, Void> onRefresh;
    private final List<ColumnConfig> columns;
    private final List<FilterConfig> filters;

    public TableConfig(String tableName,
                       Function<Map<String, Object>, ObservableList<Object>> dataLoader,
                       Callback<Void, Void> onAdd,
                       Callback<Object, Void> onEdit,
                       Callback<Void, Void> onRefresh,
                       List<ColumnConfig> columns,
                       List<FilterConfig> filters) {
        this.tableName = tableName;
        this.dataLoader = dataLoader;
        this.onAdd = onAdd;
        this.onEdit = onEdit;
        this.onRefresh = onRefresh;
        this.columns = columns;
        this.filters = filters;
    }

    // Getters
    public String getTableName() { return tableName; }
    public Function<Map<String, Object>, ObservableList<Object>> getDataLoader() { return dataLoader; }
    public Callback<Void, Void> getOnAdd() { return onAdd; }
    public Callback<Object, Void> getOnEdit() { return onEdit; }
    public Callback<Void, Void> getOnRefresh() { return onRefresh; }
    public List<ColumnConfig> getColumns() { return columns; }
    public List<FilterConfig> getFilters() { return filters; }
}
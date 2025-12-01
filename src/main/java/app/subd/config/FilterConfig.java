package app.subd.config;

import javafx.collections.ObservableList;
import java.util.function.Function;
import java.util.Map;

public class FilterConfig {
    public enum FilterType {
        COMBOBOX,
        DATE,
        TEXT,
        NUMBER
    }

    private final String filterKey;
    private final String label;
    private final FilterType filterType;
    private final boolean required;
    private final Function<Map<String, Object>, ObservableList<?>> itemsFunction;
    private final String dependsOnFilter;

    // Конструктор для комбо-боксов
    public FilterConfig(String filterKey, String label,
                        Function<Map<String, Object>, ObservableList<?>> itemsFunction) {
        this(filterKey, label, FilterType.COMBOBOX, false, itemsFunction, null);
    }

    public FilterConfig(String filterKey, String label,
                        Function<Map<String, Object>, ObservableList<?>> itemsFunction,
                        String dependsOnFilter) {
        this(filterKey, label, FilterType.COMBOBOX, false, itemsFunction, dependsOnFilter);
    }

    // Конструктор для других типов полей
    public FilterConfig(String filterKey, String label, FilterType filterType, boolean required) {
        this(filterKey, label, filterType, required, null, null);
    }

    public FilterConfig(String filterKey, String label, FilterType filterType, boolean required,
                        Function<Map<String, Object>, ObservableList<?>> itemsFunction,
                        String dependsOnFilter) {
        this.filterKey = filterKey;
        this.label = label;
        this.filterType = filterType;
        this.required = required;
        this.itemsFunction = itemsFunction;
        this.dependsOnFilter = dependsOnFilter;
    }

    public String getFilterKey() { return filterKey; }
    public String getLabel() { return label; }
    public FilterType getFilterType() { return filterType; }
    public boolean isRequired() { return required; }
    public Function<Map<String, Object>, ObservableList<?>> getItemsFunction() { return itemsFunction; }
    public String getDependsOnFilter() { return dependsOnFilter; }
}
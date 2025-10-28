package app.subd.config;

import javafx.collections.ObservableList;
import java.util.function.Function;
import java.util.Map;

public class FilterConfig {
    private final String filterKey;
    private final String label;
    private final Function<Map<String, Object>, ObservableList<?>> itemsFunction;
    private final String dependsOnFilter;

    public FilterConfig(String filterKey, String label,
                        Function<Map<String, Object>, ObservableList<?>> itemsFunction) {
        this(filterKey, label, itemsFunction, null);
    }

    public FilterConfig(String filterKey, String label,
                        Function<Map<String, Object>, ObservableList<?>> itemsFunction,
                        String dependsOnFilter) {
        this.filterKey = filterKey;
        this.label = label;
        this.itemsFunction = itemsFunction;
        this.dependsOnFilter = dependsOnFilter;
    }

    public String getFilterKey() { return filterKey; }
    public String getLabel() { return label; }
    public Function<Map<String, Object>, ObservableList<?>> getItemsFunction() { return itemsFunction; }
    public String getDependsOnFilter() { return dependsOnFilter; }
}
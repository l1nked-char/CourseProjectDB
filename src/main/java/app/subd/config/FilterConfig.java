package app.subd.config;

import javafx.collections.ObservableList;
import java.util.function.Supplier;

public class FilterConfig {
    private final String filterKey;
    private final String label;
    private final Supplier<ObservableList<?>> itemsSupplier;

    public FilterConfig(String filterKey, String label, Supplier<ObservableList<?>> itemsSupplier) {
        this.filterKey = filterKey;
        this.label = label;
        this.itemsSupplier = itemsSupplier;
    }

    public String getFilterKey() { return filterKey; }
    public String getLabel() { return label; }
    public Supplier<ObservableList<?>> getItemsSupplier() { return itemsSupplier; }
}
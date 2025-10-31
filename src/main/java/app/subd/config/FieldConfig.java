package app.subd.config;

import javafx.collections.ObservableList;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FieldConfig {
    private final String propertyName;
    private final String label;
    private final FieldType type;
    private final boolean required;
    private final Supplier<ObservableList<Object>> itemsSupplier;
    private final Function<Map<String, Object>, ObservableList<Object>> itemsLoader;
    private final String dependentOn;
    private final String promptText;
    private final double width;

    public enum FieldType {
        TEXT, NUMBER, COMBOBOX, DATE, TEXTAREA, CHECKBOX
    }

    // Existing constructor for simple fields
    public FieldConfig(String propertyName, String label, FieldType type, boolean required) {
        this(propertyName, label, type, required, null, null, -1, null, null);
    }

    // Existing constructor for fields with prompt text
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       String promptText) {
        this(propertyName, label, type, required, null, promptText, -1, null, null);
    }

    // Existing constructor for simple ComboBox
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       Supplier<ObservableList<Object>> itemsSupplier, String promptText, double width) {
        this(propertyName, label, type, required, itemsSupplier, promptText, width, null, null);
    }

    // New constructor for dependent ComboBox
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       Function<Map<String, Object>, ObservableList<Object>> itemsLoader, String promptText, double width, String dependentOn) {
        this(propertyName, label, type, required, null, promptText, width, itemsLoader, dependentOn);
    }

    // Private master constructor
    private FieldConfig(String propertyName, String label, FieldType type, boolean required,
                        Supplier<ObservableList<Object>> itemsSupplier, String promptText, double width,
                        Function<Map<String, Object>, ObservableList<Object>> itemsLoader, String dependentOn) {
        this.propertyName = propertyName;
        this.label = label;
        this.type = type;
        this.required = required;
        this.itemsSupplier = itemsSupplier;
        this.promptText = promptText;
        this.width = width;
        this.itemsLoader = itemsLoader;
        this.dependentOn = dependentOn;
    }

    // Getters
    public String getPropertyName() { return propertyName; }
    public String getLabel() { return label; }
    public FieldType getType() { return type; }
    public boolean isRequired() { return required; }
    public Supplier<ObservableList<Object>> getItemsSupplier() { return itemsSupplier; }
    public Function<Map<String, Object>, ObservableList<Object>> getItemsLoader() { return itemsLoader; }
    public String getDependentOn() { return dependentOn; }
    public String getPromptText() { return promptText; }
    public double getWidth() { return width; }
}

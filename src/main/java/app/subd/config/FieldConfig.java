package app.subd.config;

import javafx.collections.ObservableList;
import java.util.function.Supplier;

public class FieldConfig {
    private final String propertyName;
    private final String label;
    private final FieldType type;
    private final boolean required;
    private final Supplier<ObservableList<?>> itemsSupplier;
    private final String promptText;
    private final double width;

    public enum FieldType {
        TEXT, NUMBER, COMBOBOX, DATE, TEXTAREA, CHECKBOX
    }

    public FieldConfig(String propertyName, String label, FieldType type, boolean required) {
        this(propertyName, label, type, required, null, null, -1);
    }

    public FieldConfig(String propertyName, String label, FieldType type, boolean required, 
                      String promptText) {
        this(propertyName, label, type, required, null, promptText, -1);
    }

    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                      Supplier<ObservableList<?>> itemsSupplier, String promptText, double width) {
        this.propertyName = propertyName;
        this.label = label;
        this.type = type;
        this.required = required;
        this.itemsSupplier = itemsSupplier;
        this.promptText = promptText;
        this.width = width;
    }

    // Getters
    public String getPropertyName() { return propertyName; }
    public String getLabel() { return label; }
    public FieldType getType() { return type; }
    public boolean isRequired() { return required; }
    public Supplier<ObservableList<?>> getItemsSupplier() { return itemsSupplier; }
    public String getPromptText() { return promptText; }
    public double getWidth() { return width; }
}
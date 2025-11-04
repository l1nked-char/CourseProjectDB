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
    private final String validationRegex;

    public enum FieldType {
        TEXT, NUMBER, COMBOBOX, DATE, TEXTAREA, CHECKBOX, EMAIL
    }

    public static FieldConfig createEmailField(String propertyName, String label, boolean required) {
        return new FieldConfig(propertyName, label, FieldType.EMAIL, required,
                "example@mail.com", "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static FieldConfig createPhoneField(String propertyName, String label, boolean required) {
        return new FieldConfig(propertyName, label, FieldType.TEXT, required,
                "+7 (XXX) XXX-XX-XX", "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$");
    }

    public static FieldConfig createRequiredNumberField(String propertyName, String label, String promptText) {
        return new FieldConfig(propertyName, label, FieldType.NUMBER, true, promptText);
    }

    // Existing constructor for simple fields
    public FieldConfig(String propertyName, String label, FieldType type, boolean required) {
        this(propertyName, label, type, required, null, null, -1, null, null, null);
    }

    // Existing constructor for fields with prompt text
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       String promptText) {
        this(propertyName, label, type, required, null, promptText, -1, null, null, null);
    }

    // Existing constructor for fields with prompt text and validation
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       String promptText, String validationRegex) {
        this(propertyName, label, type, required, null, promptText, -1, null, null, validationRegex);
    }

    // Existing constructor for simple ComboBox
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       Supplier<ObservableList<Object>> itemsSupplier, String promptText, double width) {
        this(propertyName, label, type, required, itemsSupplier, promptText, width, null, null, null);
    }

    // New constructor for dependent ComboBox
    public FieldConfig(String propertyName, String label, FieldType type, boolean required,
                       Function<Map<String, Object>, ObservableList<Object>> itemsLoader, String promptText, double width, String dependentOn) {
        this(propertyName, label, type, required, null, promptText, width, itemsLoader, dependentOn, null);
    }

    // Private master constructor
    private FieldConfig(String propertyName, String label, FieldType type, boolean required,
                        Supplier<ObservableList<Object>> itemsSupplier, String promptText, double width,
                        Function<Map<String, Object>, ObservableList<Object>> itemsLoader, String dependentOn, String validationRegex) {
        this.propertyName = propertyName;
        this.label = label;
        this.type = type;
        this.required = required;
        this.itemsSupplier = itemsSupplier;
        this.promptText = promptText;
        this.width = width;
        this.itemsLoader = itemsLoader;
        this.dependentOn = dependentOn;
        this.validationRegex = validationRegex;
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
    public String getValidationRegex() { return validationRegex; }
}

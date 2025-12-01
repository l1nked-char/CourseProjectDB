package app.subd.config;

public class ColumnConfig {
    private String propertyName;
    private String columnName;
    private double width;
    private boolean filterable; // Можно ли фильтровать по этому столбцу
    private FieldConfig.FieldType filterType; // Тип фильтра
    private String filterPrompt; // Подсказка для поля фильтра

    private final FieldConfig.FieldType fieldType;

    public ColumnConfig(String propertyName, String columnName) {
        this(propertyName, columnName, -1, false, FieldConfig.FieldType.TEXT, null, FieldConfig.FieldType.TEXT);
    }

    public ColumnConfig(String propertyName, String columnName, double width) {
        this(propertyName, columnName, width, false, FieldConfig.FieldType.TEXT, null, FieldConfig.FieldType.TEXT);
    }
    
    public ColumnConfig(String propertyName, String columnName, double width, FieldConfig.FieldType fieldType) {
        this(propertyName, columnName, width, false, FieldConfig.FieldType.TEXT, null, fieldType);
    }

    public ColumnConfig(String propertyName, String columnName, double width, boolean filterable, FieldConfig.FieldType filterType) {
        this(propertyName, columnName, width, filterable, filterType, null, FieldConfig.FieldType.TEXT);
    }

    public ColumnConfig(String propertyName, String columnName, double width, boolean filterable, FieldConfig.FieldType filterType, String filterPrompt) {
        this(propertyName, columnName, width, filterable, filterType, filterPrompt, FieldConfig.FieldType.TEXT);
    }

    public ColumnConfig(String propertyName, String columnName, double width, boolean filterable, FieldConfig.FieldType filterType, String filterPrompt, FieldConfig.FieldType fieldType) {
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.width = width;
        this.filterable = filterable;
        this.filterType = filterType;
        this.filterPrompt = filterPrompt;
        this.fieldType = fieldType;
    }

    public String getPropertyName() { return propertyName; }
    public String getColumnName() { return columnName; }
    public double getWidth() { return width; }
    public boolean isFilterable() { return filterable; }
    public FieldConfig.FieldType getFilterType() { return filterType; }
    public String getFilterPrompt() { return filterPrompt; }
    public FieldConfig.FieldType getFieldType() { return fieldType; }

}
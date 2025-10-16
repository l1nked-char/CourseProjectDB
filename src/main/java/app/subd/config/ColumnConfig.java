package app.subd.config;

public class ColumnConfig {
    private final String propertyName;
    private final String columnName;
    private final double width;

    public ColumnConfig(String propertyName, String columnName) {
        this(propertyName, columnName, -1);
    }

    public ColumnConfig(String propertyName, String columnName, double width) {
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.width = width;
    }

    public String getPropertyName() { return propertyName; }
    public String getColumnName() { return columnName; }
    public double getWidth() { return width; }
}
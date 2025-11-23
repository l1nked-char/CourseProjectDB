package app.subd.config;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class UniversalFormConfig<T> {
    private final String formTitle;
    private final String fxmlPath;
    private final List<FieldConfig> fields;
    private final Function<T, Boolean> saveFunction;
    private final Consumer<T> onSuccess;
    private final Mode mode;
    private final Class<T> entityClass;

    public enum Mode {
        ADD("Добавление"),
        EDIT("Редактирование");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        public String getTitle() { return title; }
    }

    public UniversalFormConfig(String formTitle, List<FieldConfig> fields,
                               Function<T, Boolean> saveFunction, Consumer<T> onSuccess,
                               Mode mode, Class<T> entityClass) {
        this.formTitle = formTitle;
        this.fxmlPath = "/app/subd/components/universal_form.fxml";
        this.fields = fields;
        this.saveFunction = saveFunction;
        this.onSuccess = onSuccess;
        this.mode = mode;
        this.entityClass = entityClass;
    }

    // Добавляем геттер для класса сущности
    public Class<T> getEntityClass() {
        return entityClass;
    }

    // Getters
    public String getFormTitle() { return formTitle; }
    public String getFxmlPath() { return fxmlPath; }
    public List<FieldConfig> getFields() { return fields; }
    public Function<T, Boolean> getSaveFunction() { return saveFunction; }
    public Consumer<T> getOnSuccess() { return onSuccess; }
    public Mode getMode() { return mode; }
}
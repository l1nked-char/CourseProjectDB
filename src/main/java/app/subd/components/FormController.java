package app.subd.components;

import app.subd.admin_panels.AdminController;
import javafx.stage.Stage;

public interface FormController<T> {
    void setMode(Mode mode);
    void setItem(T item);
    void setOnSaveSuccess(Runnable onSaveSuccess);
    void setParentController(AdminController.RefreshableController parentController);
    void setStage(Stage stage);
    Stage getStage();

    enum Mode {
        ADD, EDIT
    }
}
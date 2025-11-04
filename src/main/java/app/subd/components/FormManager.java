package app.subd.components;

import app.subd.config.UniversalFormConfig;
import app.subd.admin_panels.AdminController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class FormManager {
    
    public static <T> void showForm(UniversalFormConfig<T> config, 
                                   FormController.Mode mode, 
                                   T item,
                                   AdminController.RefreshableController parentController) {
        try {
            FXMLLoader loader = new FXMLLoader(FormManager.class.getResource("/app/subd/tables/universal_form.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
            
            UniversalFormController<T> controller = loader.getController();
            controller.setMode(mode);
            controller.setItem(item);
            controller.setParentController(parentController);
            controller.configure(config);
            
            Stage stage = new Stage();
            stage.setTitle(getStageTitle(config, mode));
            stage.setMinWidth(700);
            stage.setMinHeight(600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setStage(stage);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static <T> String getStageTitle(UniversalFormConfig<T> config, FormController.Mode mode) {
        return (mode == FormController.Mode.ADD ? "Добавление " : "Редактирование ") + 
               config.getFormTitle().toLowerCase();
    }
}
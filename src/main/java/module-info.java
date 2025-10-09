module app.subd {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;

    opens app.subd to javafx.fxml, org.controlsfx.controls;
    opens app.subd.admin_panels to javafx.fxml;
    opens app.subd.owner_panels to javafx.fxml;
    opens app.subd.employee_panels to javafx.fxml;
    opens app.subd.models;
    opens app.subd.tables;

    exports app.subd;
    exports app.subd.admin_panels;
    exports app.subd.owner_panels;
    exports app.subd.employee_panels;
    exports app.subd.models;
    exports app.subd.tables;
}
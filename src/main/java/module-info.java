module ro.ubbcluj.map.proiectraokko4 {
    requires javafx.controls;
    requires com.jfoenix;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens ro.ubbcluj.map.proiectraokko4 to javafx.fxml;
    exports ro.ubbcluj.map.proiectraokko4;

    opens ro.ubbcluj.map.proiectraokko4.domain to javafx.base;
    exports ro.ubbcluj.map.proiectraokko4.controller;

}
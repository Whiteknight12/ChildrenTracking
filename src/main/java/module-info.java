module com.example.childrentracking {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.xml.crypto;
    requires transitive mysql.connector.j;
    requires java.desktop;

    opens com.example.childrentracking to javafx.fxml;
    exports com.example.childrentracking;
    exports com.example.childrentracking.Controllers to javafx.fxml;
    exports com.example.childrentracking.Database;
    opens com.example.childrentracking.Database to javafx.fxml;
    opens com.example.childrentracking.Controllers to javafx.fxml;
}
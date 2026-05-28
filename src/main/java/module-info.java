module org.example.juegofinalsupremo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens org.example.juegofinalsupremo to javafx.fxml;
    exports org.example.juegofinalsupremo;
    exports org.example.juegofinalsupremo.model;
    exports org.example.juegofinalsupremo.data;
    exports org.example.juegofinalsupremo.contracts;
    exports org.example.juegofinalsupremo.exceptions;
    exports org.example.juegofinalsupremo.io;
}

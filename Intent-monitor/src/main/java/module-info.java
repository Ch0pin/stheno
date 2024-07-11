module org.medusa.intentmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;
    requires java.rmi;
    requires org.json;
    requires javafx.web;


    opens org.medusa.intentmonitor to javafx.fxml;
    opens org.medusa.intentmonitor.controllers to javafx.fxml;
    opens org.medusa.intentmonitor.helpers to javafx.base;
    exports org.medusa.intentmonitor;
    exports org.medusa.intentmonitor.controllers;

}
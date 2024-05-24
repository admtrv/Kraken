module main.nebula {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    exports client;
    exports client.controllers;
    opens client.controllers to javafx.fxml;
    opens client.entities to com.google.gson;
}

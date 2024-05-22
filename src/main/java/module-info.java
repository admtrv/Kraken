module main.nebula {
    requires javafx.controls;
    requires javafx.fxml;

    exports client;
    exports client.controllers;
    opens client.controllers to javafx.fxml;
}

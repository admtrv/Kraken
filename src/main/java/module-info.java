module main.nebula {
    requires javafx.controls;
    requires javafx.fxml;


    exports controllers;
    opens controllers to javafx.fxml;
}
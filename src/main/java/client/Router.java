package client;

import client.controllers.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Router {
    private static MainController mainController;

    private static String MAIN_LAYOUT_PATH = "/fxml/main-layout.fxml";
    public static void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Router.class.getResource(MAIN_LAYOUT_PATH));
            Scene scene = new Scene(fxmlLoader.load());
            mainController = fxmlLoader.getController();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error managers main layout");
            e.printStackTrace();
        }
    }

    public static void loadContent(String fxmlFile) {
        if (mainController == null) {
            System.err.println("Error: MainController is NULL. Make sure init method is called first.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Router.class.getResource("/fxml/" + fxmlFile));
            Node content = fxmlLoader.load();
            mainController.setContent(content);
        } catch (IOException e) {
            System.err.println("Error managers content: " + fxmlFile);
            e.printStackTrace();
        }
    }
}

package client;

import client.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Router {
    private static MainController mainController;

    public static void init(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Router.class.getResource("/fxml/main-layout.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            mainController = fxmlLoader.getController();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading main layout");
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
            System.err.println("Error loading content: " + fxmlFile);
            e.printStackTrace();
        }
    }
}

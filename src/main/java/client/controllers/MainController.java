package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class MainController {

    @FXML
    private VBox contentArea;

    @FXML
    private void onProfileButton() {
        //Router.loadContent("profile-content.fxml");
    }

    @FXML
    private void onAddNewGameButton() {
        //Router.loadContent("add-new-game-content.fxml");
    }

    @FXML
    private void onLibraryButton() {
        //Router.loadContent("initial-content.fxml");
    }

    @FXML
    private void onSettingsButton() {
        //Router.loadContent("settings-content.fxml");
    }

    public void setContent(Node content) {
        contentArea.getChildren().setAll(content);
    }
}

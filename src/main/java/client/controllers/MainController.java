package client.controllers;

import client.entities.*;
import client.session.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

public class MainController {

    @FXML private Label appNameLabel;
    @FXML private Label versionLabel;
    @FXML private VBox contentArea;

    @FXML
    public void initialize() {
        Config config = CurrentConfig.getInstance().getConfig();
        if (config != null) {
            appNameLabel.setText(config.getAppName());
            versionLabel.setText(config.getVersion());
        } else {
            appNameLabel.setText("Not Found");
            versionLabel.setText("Not Found");
        }
    }
    @FXML
    private void onProfileButton() {
        //Router.loadContent("profile-content.fxml");
    }

    @FXML
    private void onAddGameButton() {
        //Router.loadContent("add-game-content.fxml");
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

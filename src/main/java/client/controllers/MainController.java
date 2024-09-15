package client.controllers;

import client.*;
import client.entities.*;
import client.session.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.File;
import java.io.IOException;

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
    private void onAddGameButton() {
        // Инициализация окна проводника
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Game Folder");
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            String gameFolderPath = selectedDirectory.getAbsolutePath();
            System.out.println("Selected directory: " + gameFolderPath);

            try {
                if (Game.addGame(gameFolderPath)){

                    // Обновление интерфейса
                    Platform.runLater(() -> Router.loadContent("library-content.fxml"));
                } else {

                    System.out.println("Game info not found.");
                }
            } catch (IOException e) {

                System.out.println("Failed to fetch game cover." + e.getMessage());
            } catch (Exception e) {

                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        } else {

            System.out.println("No directory selected.");
        }
    }

    @FXML
    private void onLibraryButton() {
        Router.loadContent("library-content.fxml");
    }

    @FXML
    private void onSettingsButton() {
        Router.loadContent("settings-content.fxml");
    }

    @FXML
    private void onProfileButton() {
    }

    public void setContent(Node content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }
}

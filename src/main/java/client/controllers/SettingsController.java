package client.controllers;

import client.scripts.*;

import javafx.fxml.FXML;
import java.io.IOException;

public class SettingsController {

    @FXML
    private void onUpdateGameCatalogueButton() {
        // Поток для выполнения задачи обновления данных
        Thread updateThread = new Thread(() -> {
            try {

                SteamCoverFetcher.updateSteamGames();
                System.out.println("Steam games file updated successfully.");
            } catch (IOException e) {

                System.out.println("Failed to update Steam games file: " + e.getMessage());
            }
        });

        // Поток - демон, чтобы завершился при закрытии приложения
        updateThread.setDaemon(true);
        updateThread.start();
    }
}

package client.entities;

import client.managers.*;
import client.scripts.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Game {
    private Integer steamId;
    private String title;
    private String coverUrl;
    private Path gameFolderPath;
    private Path stateFilePath;

    public Game(Integer steamId, String title, String coverUrl, Path gameFolderPath, Path stateFilePath) {
        this.steamId = steamId;
        this.title = title;
        this.coverUrl = coverUrl;
        this.gameFolderPath = gameFolderPath;
        this.stateFilePath = stateFilePath;
    }

    @Override
    public String toString() {
        return "Game{" +
                "steamId='" + steamId + '\'' +
                ", title='" + title + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", gameFolderPath='" + gameFolderPath + '\'' +
                ", stateFilePath='" + stateFilePath + '\'' +
                '}';
    }

    public Integer getSteamId() {
        return steamId;
    }

    public void setSteamId(Integer steamId) {
        this.steamId = steamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Path getGameFolderPath() {
        return gameFolderPath;
    }

    public void setGameFolderPath(Path gameFolderPath) {
        this.gameFolderPath = gameFolderPath;
    }

    public Path getStateFilePath() {
        return stateFilePath;
    }

    public void setStateFilePath(Path stateFilePath) {
        this.stateFilePath = stateFilePath;
    }

    public static boolean addGame(String gameFolderPath) throws IOException, SQLException {
        // Извлечение названия игры из папки
        String gameTitle = Game.extractGameTitle(gameFolderPath);
        System.out.println("Extracted game title: " + gameTitle);

        // Поиск обложки игры
        Game game = SteamCoverFetcher.fetchGameInfo(gameTitle);
        if (game != null) {
            System.out.println("Fetched cover URL: " + game.getCoverUrl());

            // Преобразование названия игры для имени файла состояния
            String stateFileName = gameTitle.toLowerCase().replaceAll("\\s+", "_") + "_state.dat";

            // Установка пути к папке с игрой
            Path gamePath = Paths.get(gameFolderPath);
            game.setGameFolderPath(gamePath);
            System.out.println("Game folder path set: " + gamePath);

            // Установка пути к файлу состояния
            Path stateFilePath = Paths.get("src/main/resources/states", stateFileName);
            Files.createDirectories(stateFilePath.getParent());
            game.setStateFilePath(stateFilePath);
            System.out.println("State file path set: " + stateFilePath);

            // Запуск мониторинга папки
            DirectoryChangeMonitor monitor = new DirectoryChangeMonitor(gamePath, stateFilePath);
            monitor.monitor();

            // Сохранение игры в JSON
            DataBaseManager.addGame(game);

            return true;
        } else {
            return false;
        }
    }

    public static String extractGameTitle(String gameFolderPath) {
        File gameFolder = new File(gameFolderPath);

        if (gameFolder.exists() && gameFolder.isDirectory()) {

            // Пока прредполагаем, что имя папки - название игры, потом может поменяю логику
            return gameFolder.getName();
        }
        return null;
    }
}

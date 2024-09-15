package client.managers;

import client.entities.*;

import java.sql.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private static String URL = "jdbc:sqlite:src/main/resources/sqlite.db";

    // Метод для получения соединения с базой данных
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Метод для добавления новой игры в базу данных
    public static void addGame(Game game) throws SQLException {
        String sql = "INSERT INTO library (steamId, title, coverUrl, gameFolderPath, stateFilePath) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, game.getSteamId());
            preparedStatement.setString(2, game.getTitle());
            preparedStatement.setString(3, game.getCoverUrl());
            preparedStatement.setString(4, game.getGameFolderPath().toString());
            preparedStatement.setString(5, game.getStateFilePath().toString());

            preparedStatement.executeUpdate();
        }
    }

    // Метод для загрузки всех игр из базы данных
    public static List<Game> loadGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT steamId, title, coverUrl, gameFolderPath, stateFilePath FROM library";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Integer steamId = resultSet.getInt("steamId");
                String title = resultSet.getString("title");
                String coverUrl = resultSet.getString("coverUrl");
                String gameFolderPath = resultSet.getString("gameFolderPath");
                String stateFilePath = resultSet.getString("stateFilePath");

                Game game = new Game(
                        steamId,
                        title,
                        coverUrl,
                        Paths.get(gameFolderPath),
                        Paths.get(stateFilePath)
                );

                games.add(game);
            }
        }

        return games;
    }

    // Метод для обновления информации об игре в базе данных
    public static void updateGame(Game game) throws SQLException {
        String sql = "UPDATE library SET title = ?, coverUrl = ?, gameFolderPath = ?, stateFilePath = ? WHERE steamId = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, game.getTitle());
            preparedStatement.setString(2, game.getCoverUrl());
            preparedStatement.setString(3, game.getGameFolderPath().toString());
            preparedStatement.setString(4, game.getStateFilePath().toString());
            preparedStatement.setInt(5, game.getSteamId());

            preparedStatement.executeUpdate();
        }
    }

    // Метод для удаления игры из базы данных
    public static void deleteGame(int steamId) throws SQLException {
        String sql = "DELETE FROM library WHERE steamId = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, steamId);

            preparedStatement.executeUpdate();
        }
    }

    // Метод для проверки существования игры в базе данных по Steam ID
    public static boolean gameExists(int steamId) throws SQLException {
        String sql = "SELECT 1 FROM library WHERE steamId = ? LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, steamId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}

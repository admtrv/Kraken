package client;

import client.entities.*;
import client.managers.*;
import client.session.*;
import client.scripts.*;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class Application extends javafx.application.Application {
    private final String ICON_PATH = "/images/logo/logo.png";
    @Override
    public void start(Stage stage) throws SQLException {
        // Чтение конфигурации
        Config config = CurrentConfig.getInstance().getConfig();

        // Инициализация начального состояния окна приложения
        stage.setTitle(config.getAppName());
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON_PATH)));
        stage.setMinWidth(955);
        stage.setMinHeight(540);

        // Проверка, пуста ли база данных Redis
        RedisManager.checkRedis();

        // Инициализация роутера и загрузка основного содержимого
        Router.init(stage);
        Router.loadContent("library-content.fxml");

        // Загрузка сохраненных игр из JSON
        List<Game> games = DataBaseManager.loadGames();

        // Запуск мониторинга для каждой сохраненной игры
        for (Game game : games) {
            Path gameFolderPath = game.getGameFolderPath();
            Path stateFilePath = game.getStateFilePath();

            // Создание и запуск мониторинга в отдельном потоке
            new Thread(() -> {
                DirectoryChangeMonitor monitor = new DirectoryChangeMonitor(gameFolderPath, stateFilePath);
                monitor.monitor();
            }).start();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}

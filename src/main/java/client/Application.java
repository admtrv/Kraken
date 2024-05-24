package client;

import client.session.*;
import client.entities.*;
import client.monitoring.*;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application extends javafx.application.Application {
    private final String ICON_PATH = "/images/logo/logo.png";
    @Override
    public void start(Stage stage) {
        // Чтение конфигурации
        Config config = CurrentConfig.getInstance().getConfig();

        // Инициализация начального состояния окна приложения
        stage.setTitle(config.getAppName());
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON_PATH)));
        stage.setMinWidth(1020);
        stage.setMinHeight(540);

        // Инициализация роутера и загрузка основного макета
        Router.init(stage);
        Router.loadContent("initial-content.fxml");

        // Указываем путь к главной папке игры
        Path dontStarveDirectory = Paths.get("C:/Users/DAnto/Downloads/Dont Starve");
        Path dontStarveState = Paths.get("dont_starve_state.dat");
        // Создаем наблюдателя за изменениями
        DirectoryChangeMonitor dontStarveMonitor = new DirectoryChangeMonitor(dontStarveDirectory, dontStarveState);
        // Запускаем наблюдателя за изменениями в отдельном потоке
        Thread monitorThread = new Thread(dontStarveMonitor);
        // Делаем поток демоном, чтобы он завершался при закрытии приложения
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public static void main(String[] args) {
        launch();
    }
}

package client;

import client.monitoring.DirectoryChangeMonitor;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        // Инициализация начального состояния окна приложения
        stage.setTitle("Kraken");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icons/icon.png")));
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

package client;

import client.monitoring.*;

import javafx.scene.image.*;
import javafx.stage.*;
import java.nio.file.*;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        // Инициализация начального состояния окна приложения
        stage.initStyle(StageStyle.UNIFIED);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icons/icon.png")));
        stage.setTitle("");

        // Инициализация роутера и загрузка начальной сцены
        Router.init(stage);
        Router.loadScene("hello-view.fxml");

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

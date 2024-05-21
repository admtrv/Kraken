package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import monitoring.DirectoryChangeMonitor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationController extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationController.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        // Указываем путь к главной папке игры
        Path dontStarveDirectory = Paths.get("C:/Users/DAnto/Downloads/Dont Starve");
        Path dontStarveState = Paths.get("file_state.dat");
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

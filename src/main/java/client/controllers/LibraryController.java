package client.controllers;

import client.entities.*;
import client.loading.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class LibraryController {

    @FXML private GridPane gamesGrid;

    private int row = 0;
    private int column = 0;
    private int maxColumn = 4;
    private final int leftBarWidth = 220;
    private final int scrollBarWidth = 20;
    private final int imageWidth = 190;
    private final int imageHeight = (int) (imageWidth * 1.5);
    private final int padding = 15; // Отступы вокруг изображения
    private final int margin = 10; // Отступы между карточками

    private final List<VBox> cards = new ArrayList<>(); // Список для хранения карточек
    private List<Game> games = new ArrayList<>(); // Список для хранения игр

    private final GameLoader gameLoader = new GameLoader();

    @FXML
    public void initialize() {
        // Загрузка игр в отдельном потоке
        new Thread(this::initializeGames).start();

        Platform.runLater(() -> {
            Scene scene = gamesGrid.getScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow();
                stage.widthProperty().addListener((observable, oldValue, newValue) -> updateColumnsNumber(newValue.doubleValue()));

                updateColumnsNumber(stage.getWidth()); // Обновление колонок и перестройка карточек при изменении ширины окна
            }
        });
    }

    private void initializeGames() {
        games = gameLoader.loadGames();
        if (games != null) {
            for (Game game : games) {
                // Запуск потока для добавления карточки игры
                new Thread(() -> addGameCard(game.getTitle(), game.getCoverUrl())).start();
            }
        }
    }

    private void updateColumnsNumber(double newWidth) {
        double totalCardWidth = imageWidth + (2 * padding) + margin;
        maxColumn = (int) Math.floor((newWidth - leftBarWidth - scrollBarWidth - margin) / totalCardWidth);
        rearrangeGameCards();
    }

    private void rearrangeGameCards() {
        gamesGrid.getChildren().clear();

        column = 0;
        row = 0;

        for (VBox card : cards) {
            gamesGrid.add(card, column, row);
            column += 1;
            if (column == maxColumn) {
                column = 0;
                row += 1;
            }
        }
    }

    public void addGameCard(String gameName, String coverUrl) {
        VBox card = new VBox();
        card.setPadding(new Insets(padding));
        card.setStyle("-fx-background-color: #252525; -fx-background-radius: 5;");

        ImageView coverImageView = new ImageView(new Image(coverUrl));
        coverImageView.setFitWidth(imageWidth);
        coverImageView.setFitHeight(imageHeight);
        coverImageView.setPreserveRatio(true);

        Label titleLabel = new Label(gameName);
        titleLabel.setMaxWidth(imageWidth);
        titleLabel.setStyle("-fx-font-family: 'Fira Mono'; -fx-text-fill: #c0c0c0; -fx-font-size: 14px;");
        VBox.setMargin(titleLabel, new Insets(padding, 0, 0, 0));

        card.getChildren().addAll(coverImageView, titleLabel);

        // Добавление карточки и обновление UI
        Platform.runLater(() -> {
            cards.add(card);
            rearrangeGameCards();
        });
    }
}

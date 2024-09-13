package client.loading;

import client.entities.*;
import client.adapters.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameLoader {
    private static final String LIBRARY_FILE_PATH = "src/main/resources/library.json";

    private final Gson gson;

    public GameLoader() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Game.class, new GameAdapter())
                .create();
    }

    public List<Game> loadGames() {
        List<Game> games = new ArrayList<>();
        Path path = Paths.get(LIBRARY_FILE_PATH);

        if (!Files.exists(path)) {

            System.out.println("Library file not found, creating a new one.");
            saveGames(new ArrayList<>());
        }

        try (Reader reader = new FileReader(path.toFile())) {
            Type listType = new TypeToken<ArrayList<Game>>() {}.getType();
            games = gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Error reading games file: " + e.getMessage());
        }

        return games;
    }

    public void saveGames(List<Game> games) {
        try (Writer writer = new FileWriter(LIBRARY_FILE_PATH)) {

            gson.toJson(games, writer);
        } catch (IOException e) {

            System.err.println("Error writing games file: " + e.getMessage());
        }
    }

    public void addGame(Game newGame) {
        List<Game> games = loadGames();
        boolean isFound = false;

        for (int i = 0; i < games.size(); i++) {
            Game existingGame = games.get(i);
            if (existingGame.getTitle().equalsIgnoreCase(newGame.getTitle())) {
                games.set(i, newGame);
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            games.add(newGame);
        }

        saveGames(games);
    }
}

package client.adapters;

import client.entities.*;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameAdapter extends TypeAdapter<Game> {

    @Override
    public void write(JsonWriter out, Game game) throws IOException {
        out.beginObject();
        out.name("title").value(game.getTitle());
        out.name("coverUrl").value(game.getCoverUrl());
        out.name("gameFolderPath").value(game.getGameFolderPath().toString());
        out.name("stateFilePath").value(game.getStateFilePath().toString());
        out.endObject();
    }

    @Override
    public Game read(JsonReader in) throws IOException {
        in.beginObject();
        String title = null;
        String coverUrl = null;
        Path gameFolderPath = null;
        Path stateFilePath = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "title":
                    title = in.nextString();
                    break;
                case "coverUrl":
                    coverUrl = in.nextString();
                    break;
                case "gameFolderPath":
                    gameFolderPath = Paths.get(in.nextString());
                    break;
                case "stateFilePath":
                    stateFilePath = Paths.get(in.nextString());
                    break;
            }
        }
        in.endObject();
        return new Game(title, coverUrl, gameFolderPath, stateFilePath);
    }
}

package client.loading;

import client.entities.*;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

public class ConfigLoader {
    private static final String CONFIG_FILE_PATH = "/config.json";

    public static Config loadConfig() {
        Config config = null;
        try (Reader reader = new InputStreamReader(ConfigLoader.class.getResourceAsStream(CONFIG_FILE_PATH))) {

            Gson gson = new Gson();
            config = gson.fromJson(reader, Config.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {

            System.out.println("Failed to load config: " + e.getMessage());
        }
        return config;
    }
}

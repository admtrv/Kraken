package client.session;

import client.entities.*;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

public class CurrentConfig {
    private final String CONFIG_FILE_PATH = "/config.json";
    private static CurrentConfig instance;
    private Config config;

    private CurrentConfig() {
        loadConfig(CONFIG_FILE_PATH);
    }

    public static CurrentConfig getInstance() {
        if (instance == null) {
            synchronized (CurrentConfig.class) {
                if (instance == null) {
                    instance = new CurrentConfig();
                }
            }
        }
        return instance;
    }

    // Methods to set and to get current entities
    private void loadConfig(String configFilePath) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(configFilePath))) {
            Gson gson = new Gson();
            config = gson.fromJson(reader, Config.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public Config getConfig() {
        return config;
    }
}
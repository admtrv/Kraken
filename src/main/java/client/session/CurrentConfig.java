package client.session;

import client.entities.*;
import client.loading.*;

public class CurrentConfig {
    private static volatile CurrentConfig instance;
    private Config config;

    private CurrentConfig() {
        this.config = ConfigLoader.loadConfig();
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

    public Config getConfig() {
        return config;
    }
}

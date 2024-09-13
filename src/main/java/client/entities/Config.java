package client.entities;

import java.util.List;

public class Config {
    private String appName;
    private String version;
    private List<String> supportedLanguages;
    private String releaseDate;

    // Getters and setters

    public String getAppName() {
        return appName;
    }


    public String getVersion() {
        return version;
    }


    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }


    public String getReleaseDate() {
        return releaseDate;
    }

}

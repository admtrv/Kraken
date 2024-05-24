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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String inceptionDate) {
        this.releaseDate = inceptionDate;
    }
}

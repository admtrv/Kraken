package client.scripts;

import client.entities.*;
import client.loading.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.apache.commons.text.similarity.LevenshteinDistance;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class SteamCoverFetcher {
    private static final String STEAM_API_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v2/?key="; // URL к Steam API
    private static final String GAMES_FILE_PATH = "src/main/resources/steam-games.json"; // Путь к файлу с играми
    private static final int MAX_LEVENSHTEIN_DISTANCE = 7; // Максимально допустимое расстояние Левенштейна
    private static final Gson gson = new Gson();

    // Метод для обновления файла с играми
    public static void updateGamesFile() throws IOException {
        // Получение ключа API из файла окружения
        String apiKey = EnvLoader.getProperty("STEAM_API_KEY");

        // Подключение к Steam API для получения списка игр
        System.out.println("Connecting to Steam API...");
        URL url = new URL(STEAM_API_URL + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // Проверка кода ответа
        int responseCode = connection.getResponseCode();
        System.out.println("Steam API response code: " + responseCode);

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        // Чтение данных из ответа API
        Scanner scanner = new Scanner(url.openStream());
        StringBuilder inline = new StringBuilder();

        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }

        scanner.close();
        System.out.println("Received data from Steam API");

        // Сохранение данных в файл
        try (FileWriter writer = new FileWriter(GAMES_FILE_PATH)) {
            writer.write(inline.toString());
            System.out.println("Games data saved to " + GAMES_FILE_PATH);
        }
    }

    // Метод для поиска информации об игре
    public static Game fetchGameInfo(String gameTitle) {
        // Загрузка данных из файла с играми
        JsonArray apps = loadGamesData();

        if (apps == null) {
            System.out.println("Failed to load Steam games data.");
            return null;
        }

        // Инициализация алгоритма Левенштейна для поиска наиболее близкого совпадения
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int bestMatchDistance = Integer.MAX_VALUE;
        String bestMatchAppId = null;
        String bestMatchTitle = null;

        // Поиск игры с наименьшим расстоянием Левенштейна до искомого названия
        System.out.println("Parsing game cover: ");
        for (int i = 0; i < apps.size(); i++) {
            JsonObject app = apps.get(i).getAsJsonObject();
            String appTitle = app.get("name").getAsString();
            int distance = levenshtein.apply(gameTitle.toLowerCase(), appTitle.toLowerCase());

            if (distance < bestMatchDistance && distance <= MAX_LEVENSHTEIN_DISTANCE) {
                bestMatchDistance = distance;
                bestMatchAppId = app.get("appid").getAsString();
                bestMatchTitle = appTitle;
                System.out.println("New best match found: " + appTitle + " with distance " + distance + " and ID: " + bestMatchAppId);
            }
        }

        // Если найдено подходящее совпадение, формируем URL обложки
        if (bestMatchAppId != null) {
            String coverUrl = "https://steamcdn-a.akamaihd.net/steam/apps/" + bestMatchAppId + "/library_600x900_2x.jpg";
            System.out.println("Best match for game " + gameTitle + ": " + bestMatchTitle + " (ID: " + bestMatchAppId + ")");
            return new Game(bestMatchTitle, coverUrl,null,null);
        }

        // Если совпадение не найдено
        System.out.println("Game not found in Steam list: " + gameTitle);
        return null;
    }

    // Метод для загрузки данных из файла с играми
    private static JsonArray loadGamesData() {
        try {
            // Чтение содержимого файла
            String content = new String(Files.readAllBytes(Paths.get(GAMES_FILE_PATH)));
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
            return jsonObject.getAsJsonObject("applist").getAsJsonArray("apps");
        } catch (IOException e) {

            System.out.println("Error loading Steam games data from file: " + e.getMessage());
            return null;
        }
    }
}

package client.scripts;

import client.entities.*;
import client.managers.*;

import com.google.gson.stream.JsonReader;
import org.apache.commons.text.similarity.LevenshteinDistance;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class SteamCoverFetcher {
    private static final String STEAM_API_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v2/?key="; // URL к Steam API
    private static final int MAX_LEVENSHTEIN_DISTANCE = 7; // Максимально допустимое расстояние Левенштейна

    // Метод для обновления файла с играми
    public static void updateSteamGames() throws IOException {
        String apiKey = EnvManager.getProperty("STEAM_API_KEY");

        RedisManager redisManager = RedisManager.getInstance();
        Jedis jedis = redisManager.getJedis();

        if (jedis == null) {
            System.err.println("Redis connection is not available.");
            return;
        }

        System.out.println("Connecting to Steam API...");
        URL url = new URL(STEAM_API_URL + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Steam API response code: " + responseCode);

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        // Используем JsonReader для потокового чтения JSON данных
        try (InputStreamReader isr = new InputStreamReader(connection.getInputStream());
             JsonReader jsonReader = new JsonReader(isr)) {

            // Переходим к массиву apps
            jsonReader.beginObject(); // Начинаем чтение объекта

            while (jsonReader.hasNext()) {

                String name = jsonReader.nextName();
                if (name.equals("applist")) {
                    jsonReader.beginObject();

                    while (jsonReader.hasNext()) {
                        String innerName = jsonReader.nextName();
                        if (innerName.equals("apps")) {

                            jsonReader.beginArray();

                            Pipeline pipeline = jedis.pipelined();
                            int batchSize = 10000;
                            int counter = 0;

                            // Обрабатываем массив apps
                            while (jsonReader.hasNext()) {

                                jsonReader.beginObject();
                                String appId = null;
                                String appName = null;

                                while (jsonReader.hasNext()) {
                                    String key = jsonReader.nextName();

                                    if (key.equals("appid")) {
                                        appId = jsonReader.nextString();
                                    } else if (key.equals("name")) {
                                        appName = jsonReader.nextString();
                                    } else {
                                        jsonReader.skipValue();
                                    }
                                }

                                jsonReader.endObject();

                                pipeline.set(appName, appId);
                                counter++;

                                if (counter % batchSize == 0) {
                                    pipeline.sync();
                                    pipeline = jedis.pipelined();
                                    System.out.println("Inserted " + counter + " records into Redis...");
                                }
                            }
                            // Заканчиваем обработку массива apps
                            jsonReader.endArray();

                            // Выполняем оставшиеся команды
                            pipeline.sync();
                            System.out.println("Total records inserted into Redis: " + counter);

                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            System.err.println("Error processing JSON data: " + e.getMessage());
        }
    }


    // Метод для поиска информации об игре
    public static Game fetchGameInfo(String gameTitle) {
        // Получение экземпляра RedisManager
        RedisManager redisManager = RedisManager.getInstance();
        Jedis jedis = redisManager.getJedis();

        if (jedis == null) {
            System.err.println("Redis connection is not available.");
            return null;
        }

        try {
            // Попытка получить App ID по точному названию игры из Redis
            String appId = jedis.get(gameTitle.toLowerCase());

            if (appId != null) {
                // Формируем URL обложки
                String coverUrl = "https://steamcdn-a.akamaihd.net/steam/apps/" + appId + "/library_600x900_2x.jpg";
                System.out.println("Found game in Redis: " + gameTitle + " (ID: " + appId + ")");
                return new Game(Integer.parseInt(appId), gameTitle, coverUrl, null, null);
            } else {
                // Если точное совпадение не найдено, выполняем поиск по схожести
                Set<String> gameNames = jedis.keys("*");
                LevenshteinDistance levenshtein = new LevenshteinDistance();
                int bestMatchDistance = Integer.MAX_VALUE;
                String bestMatchAppId = null;
                String bestMatchTitle = null;

                for (String name : gameNames) {
                    int distance = levenshtein.apply(gameTitle.toLowerCase(), name.toLowerCase());

                    if (distance < bestMatchDistance && distance <= MAX_LEVENSHTEIN_DISTANCE) {
                        bestMatchDistance = distance;
                        bestMatchAppId = jedis.get(name);
                        bestMatchTitle = name;
                    }
                }

                if (bestMatchAppId != null) {
                    String coverUrl = "https://steamcdn-a.akamaihd.net/steam/apps/" + bestMatchAppId + "/library_600x900_2x.jpg";
                    System.out.println("Best match for game " + gameTitle + ": " + bestMatchTitle + " (ID: " + bestMatchAppId + ")");
                    return new Game(Integer.parseInt(bestMatchAppId), bestMatchTitle, coverUrl, null, null);
                } else {
                    System.out.println("Game not found in Redis: " + gameTitle);
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching game info from Redis: " + e.getMessage());
            return null;
        }
    }
}

package client.loading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvLoader {
    private static final Map<String, String> env = new HashMap<>();

    static {
        try (Stream<String> stream = Files.lines(Paths.get(".env"))) {
            stream.forEach(line -> {
                String[] keyValue = line.split("=", 2);
                if (keyValue.length == 2) {
                    env.put(keyValue[0].trim(), keyValue[1].trim());
                }
            });
        } catch (IOException e) {

            System.out.println("Failed to load from env file: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return env.get(key);
    }
}

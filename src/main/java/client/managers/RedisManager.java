package client.managers;

import client.scripts.*;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import java.io.IOException;

public class RedisManager {
    private static volatile RedisManager instance;
    private Jedis jedis;

    private RedisManager() {
        initializeConnection();
    }

    private void initializeConnection() {
        String redisHost = EnvManager.getProperty("REDIS_HOST");
        int redisPort = Integer.parseInt(EnvManager.getProperty("REDIS_PORT"));

        try {
            jedis = new Jedis(redisHost, redisPort);
            jedis.ping();
            System.out.println("Connected to Redis successfully.");
        } catch (JedisException e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
            jedis = null;
        }
    }

    public static RedisManager getInstance() {
        if (instance == null) {
            synchronized (RedisManager.class) {
                if (instance == null) {
                    instance = new RedisManager();
                }
            }
        }
        return instance;
    }

    public Jedis getJedis() {
        if (jedis == null || !jedis.isConnected()) {
            initializeConnection();
        }
        return jedis;
    }

    public void close() {
        if (jedis != null) {
            try {
                jedis.close();
                System.out.println("Redis connection closed successfully.");
            } catch (JedisException e) {
                System.err.println("Error closing Redis connection: " + e.getMessage());
            }
        }
    }

    // Метод для проверки и инициализации данных в Redis
    public static void checkRedis() {
        RedisManager redisManager = RedisManager.getInstance();
        Jedis jedis = redisManager.getJedis();

        if (jedis == null) {
            System.err.println("Redis connection is not available.");
            return;
        }

        long redisSize = jedis.dbSize();
        System.out.println("Redis database size: " + redisSize);

        if (redisSize == 0) {
            System.out.println("Redis is empty. Fetching games from Steam API...");

            try {
                SteamCoverFetcher.updateSteamGames();
            } catch (IOException e) {
                System.err.println("Error updating games from Steam API: " + e.getMessage());
            }
        } else {
            System.out.println("Redis already contains data.");
        }
    }
}

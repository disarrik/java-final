package ru.hse.de.vkcli.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
    public static String getToken() {
        return System.getenv("vk.api.token");
    }
    public static String getApiVersion() {
        return properties.getProperty("vk.api.version");
    }
    public static int getSleepDuration() {
        String sleepDuration = properties.getProperty("vk.api.sleep.duration", "350");
        return Integer.parseInt(sleepDuration);
    }
}

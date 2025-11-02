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
        return properties.getProperty("vk.api.token");
    }
    public static String getApiVersion() {
        return properties.getProperty("vk.api.version");
    }
}

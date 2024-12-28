package ru.practicum.loader;

import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

    public static Properties loadProperties() {
        Properties properties = new Properties();

        try (var is = PropertiesLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
package ru.hse.de.vkcli;

import ru.hse.de.vkcli.config.ConfigReader;

public class Main {
    public static void main(String[] args) {
        System.out.println(ConfigReader.getApiVersion());
    }
}

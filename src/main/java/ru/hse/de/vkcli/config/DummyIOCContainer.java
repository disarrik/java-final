package ru.hse.de.vkcli.config;

import ru.hse.de.vkcli.api.VkApiImpl;
import ru.hse.de.vkcli.api.VkApi;
import ru.hse.de.vkcli.statistic.StatisticService;
import ru.hse.de.vkcli.statistic.StatisticServiceImpl;

public class DummyIOCContainer {
    
    public static StatisticService createStatisticService() {
        VkApi vkApi = new VkApiImpl();
        return new StatisticServiceImpl(vkApi);
    }
}

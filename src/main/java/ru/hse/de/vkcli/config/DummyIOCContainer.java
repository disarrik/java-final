package ru.hse.de.vkcli.config;

import ru.hse.de.vkcli.api.StubVkApi;
import ru.hse.de.vkcli.api.VkApi;
import ru.hse.de.vkcli.statistic.StatisticService;
import ru.hse.de.vkcli.statistic.StatisticServiceImpl;

public class DummyIOCContainer {
    
    public static StatisticService createStatisticService() {
        VkApi vkApi = new StubVkApi();
        return new StatisticServiceImpl(vkApi);
    }
}

package ru.hse.de.vkcli.statistic;

import ru.hse.de.vkcli.statistic.model.StatisticReport;

public interface StatisticService {

    StatisticReport getMostPopularUserInCityReport(int cityId);

}

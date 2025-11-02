package ru.hse.de.vkcli;

import ru.hse.de.vkcli.cli.ReportFormatter;
import ru.hse.de.vkcli.config.DummyIOCContainer;

public class Main {
    public static void main(String[] args) {
        var staticService = DummyIOCContainer.createStatisticService();
        new ReportFormatter().print(staticService.getMostPopularUserInCityReport(1), System.out);
    }
}

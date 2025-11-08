package ru.hse.de.vkcli.statistic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.de.vkcli.api.StubVkApi;
import ru.hse.de.vkcli.api.VkApi;
import ru.hse.de.vkcli.statistic.model.StatisticReport;

import static org.junit.jupiter.api.Assertions.*;

class StatisticServiceTest {

    private StatisticService statisticService;
    private VkApi stubVkApi;

    @BeforeEach
    void setUp() {
        stubVkApi = new StubVkApi();
        statisticService = new StatisticServiceImpl(stubVkApi);
    }

    @Test
    void testGetMostPopularUserInCityReport_SelectsUserWithMostFriends() {
        // Given
        int cityId = 1;

        // When
        StatisticReport report = statisticService.getMostPopularUserInCityReport(cityId);

        // Then
        assertNotNull(report, "Report should not be null");
        assertNotNull(report.mostFriendlyUser(), "Most friendly user should not be null");
        assertEquals("user_1", report.mostFriendlyUser().id(),
                "The user with the most friends (user_10 with 5 friends) should be selected");
    }
}

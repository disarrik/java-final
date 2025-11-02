package ru.hse.de.vkcli.statistic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ru.hse.de.vkcli.api.VkApi;
import ru.hse.de.vkcli.statistic.model.StatisticReport;

public class StatisticServiceImpl implements StatisticService {
    private static final int API_LIMIT = 100;
    private final VkApi vkApi;

    public StatisticServiceImpl(VkApi vkApi) {
        this.vkApi = vkApi;
    }

    @Override
    public StatisticReport getMostPopularUserInCityReport(int cityId) {

    }

    private <T> Stream<T> asStream(Function<Integer, List<T>> nextPageProvider) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            final Deque<T> deque = new ArrayDeque<>();

            @Override
            public boolean hasNext() {
                if (deque.isEmpty()) {
                    deque.addAll(nextPageProvider.apply(API_LIMIT));
                }
                return deque.isEmpty();
            }

            @Override
            public T next() {
                return deque.poll();
            }
        }, Spliterator.IMMUTABLE), false);
    }
}

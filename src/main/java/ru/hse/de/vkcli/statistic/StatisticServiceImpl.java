package ru.hse.de.vkcli.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ru.hse.de.vkcli.api.VkApi;
import ru.hse.de.vkcli.api.model.friend.Friend;
import ru.hse.de.vkcli.api.model.friend.FriendsResponse;
import ru.hse.de.vkcli.api.model.group.Group;
import ru.hse.de.vkcli.api.model.group.GroupsResponse;
import ru.hse.de.vkcli.api.model.search.User;
import ru.hse.de.vkcli.api.model.search.UserSearchResponse;
import ru.hse.de.vkcli.api.model.wall.Post;
import ru.hse.de.vkcli.api.model.wall.PostType;
import ru.hse.de.vkcli.api.model.wall.WallResponse;
import ru.hse.de.vkcli.statistic.model.StatisticReport;

public class StatisticServiceImpl implements StatisticService {
    private static final int MIN_USERS_REQUIRED = 5000;
    private static final int MIN_FRIEND_OF_FRIEND_REQUIRED = 50;
    private final VkApi vkApi;

    public StatisticServiceImpl(VkApi vkApi) {
        this.vkApi = vkApi;
    }


    @Override
    public StatisticReport getMostPopularUserInCityReport(int cityId) {
        Stream<User> usersStream = asStream(offset -> {
            UserSearchResponse response = vkApi.getUsersFromCity(cityId, offset);
            return response.users();
        });

        User mostPopularUser = usersStream
                .limit(MIN_USERS_REQUIRED)
                .filter(User::openProfile)
                .max(Comparator.comparingLong(user ->
                        asStream(offset -> vkApi.getFriends(user.id(), offset).friends())
                                .count()
                ))
                .orElseThrow(() -> new RuntimeException("No open profiles found"));

        StatisticReport.FriendsReport friendsReport = calculateFriendsReport(mostPopularUser.id());
        StatisticReport.GroupsReport groupsReport = calculateGroupsReport(mostPopularUser.id());
        StatisticReport.WallStatistic wallStatistic = calculateWallStatistic(mostPopularUser.id());

        return new StatisticReport(
                mostPopularUser,
                friendsReport,
                groupsReport,
                wallStatistic
        );
    }

    private StatisticReport.FriendsReport calculateFriendsReport(String userId) {
        List<Friend> friends = asStream(offset -> {
            FriendsResponse response = vkApi.getFriends(userId, offset);
            return response.friends();
        }).toList();

        int friendsCount = friends.size();

        List<Integer> friendsOfFriendsCounts = friends.stream()
                .limit(MIN_FRIEND_OF_FRIEND_REQUIRED)
                .mapToInt(Friend::friendsCount)
                .boxed()
                .toList();

        double avgFriendsOfFriends = friendsOfFriendsCounts.isEmpty() ? 0 : friendsOfFriendsCounts.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        List<String> top5Cities = friends.stream()
                .filter(friend -> friend.city() != null && !friend.city().isEmpty())
                .collect(Collectors.groupingBy(
                        Friend::city,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        long closedFriendsCount = friends.stream()
                .filter(friend -> !friend.openProfile())
                .count();
        BigDecimal percentOfClosedFriends = friendsCount > 0
                ? BigDecimal.valueOf(closedFriendsCount)
                .divide(BigDecimal.valueOf(friendsCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return new StatisticReport.FriendsReport(
                friendsCount,
                top5Cities,
                percentOfClosedFriends,
                avgFriendsOfFriends
        );
    }

    private StatisticReport.GroupsReport calculateGroupsReport(String userId) {
        List<Group> groups = asStream(offset -> {
            GroupsResponse response = vkApi.getGroups(userId, offset);
            return response.group();
        }).toList();

        int groupsCount = groups.size();

        List<Group> top10Groups = groups.stream()
                .sorted(Comparator.comparingInt(Group::membersCount).reversed())
                .limit(10)
                .collect(Collectors.toList());

        double avgGroupParticipants = groups.isEmpty() ? 0.0 : groups.stream()
                .mapToInt(Group::membersCount)
                .average()
                .orElse(0.0);

        Group bestGroup = groups.stream()
                .max(Comparator.comparingInt(Group::membersCount))
                .orElse(null);

        Group worseGroup = groups.stream()
                .min(Comparator.comparingInt(Group::membersCount))
                .orElse(null);

        return new StatisticReport.GroupsReport(
                groupsCount,
                top10Groups,
                avgGroupParticipants,
                bestGroup,
                worseGroup
        );
    }

    private StatisticReport.WallStatistic calculateWallStatistic(String userId) {
        List<Post> posts = asStream(offset -> {
            WallResponse response = vkApi.getWall(userId, offset);
            return response.posts();
        })
                .limit(100)
                .toList();

        int postsAmount = posts.size();

        List<Post> top3Posts = posts.stream()
                .sorted(Comparator.comparingInt(post ->
                        -(post.likes() + post.reposts() + post.commentsAmount())
                ))
                .limit(3)
                .collect(Collectors.toList());

        double avgTextLength = posts.stream()
                .filter(post -> post.text() != null)
                .mapToInt(post -> post.text().length())
                .average()
                .orElse(0.0);

        Map<PostType, Integer> postTypesDistribution = posts.stream()
                .collect(Collectors.groupingBy(
                        Post::type,
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));

        return new StatisticReport.WallStatistic(
                postsAmount,
                new StatisticReport.WallStatistic.PostsStatistic(
                        posts.stream().mapToInt(Post::likes).average().orElse(0.0),
                        posts.stream().mapToInt(Post::reposts).average().orElse(0.0),
                        posts.stream().mapToInt(Post::commentsAmount).average().orElse(0.0)
                ),
                top3Posts,
                avgTextLength,
                postTypesDistribution
        );
    }

    private <T> Stream<T> asStream(Function<Integer, List<T>> nextPageProvider) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            final Deque<T> deque = new ArrayDeque<>();
            int offset = 0;

            @Override
            public boolean hasNext() {
                if (deque.isEmpty()) {
                    List<T> page = nextPageProvider.apply(offset);
                    if (page.isEmpty()) {
                        return false;
                    }
                    deque.addAll(page);
                    offset += page.size();
                }
                return !deque.isEmpty();
            }

            @Override
            public T next() {
                return deque.poll();
            }
        }, Spliterator.IMMUTABLE), false);
    }
}

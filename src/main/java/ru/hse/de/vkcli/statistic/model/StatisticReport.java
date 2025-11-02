package ru.hse.de.vkcli.statistic.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ru.hse.de.vkcli.api.model.group.Group;
import ru.hse.de.vkcli.api.model.search.User;
import ru.hse.de.vkcli.api.model.wall.Post;
import ru.hse.de.vkcli.api.model.wall.PostType;

public record StatisticReport(
        User mostFriendlyUSer,
        FriendsReport friendsReport
        ) {

    record FriendsReport(
            int friendsCount,
            BigDecimal percentOfOpenFriends,
            Integer avgFriendsOfFriendsAmount
    ) {}

    record GroupsReport(
            int groupsCount,
            List<Group> top10Groups,
            BigDecimal avgGroupParticipants,
            Group bestGroup,
            Group worseGroup
    ) {}

    record WallStatistic(
            int postsAmount,
            PostsStatistic postsStatistic,
            List<Post> top3Posts,
            int avgPostCharsAmount,
            Map<PostType, Integer> postTypesDistribution
    ) {
        record PostsStatistic(
                int avgLikes,
                int avgReposts,
                int avgComments
        ) {}
    }
}

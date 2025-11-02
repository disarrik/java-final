package ru.hse.de.vkcli.statistic.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ru.hse.de.vkcli.api.model.group.Group;
import ru.hse.de.vkcli.api.model.search.User;
import ru.hse.de.vkcli.api.model.wall.Post;
import ru.hse.de.vkcli.api.model.wall.PostType;

public record StatisticReport(
        User mostFriendlyUser,
        FriendsReport friendsReport,
        GroupsReport groupsReport,
        WallStatistic wallStatistic
        ) {

    public record FriendsReport(
            int friendsCount,
            List<String> top5Cities,
            BigDecimal percentOfClosedFriends,
            double avgFriendsOfFriendsAmount
    ) {}

    public record GroupsReport(
            int groupsCount,
            List<Group> top10Groups,
            double avgGroupParticipants,
            Group bestGroup,
            Group worseGroup
    ) {}

    public record WallStatistic(
            int postsAmount,
            PostsStatistic postsStatistic,
            List<Post> top3Posts,
            double avgPostCharsAmount,
            Map<PostType, Integer> postTypesDistribution
    ) {
        public record PostsStatistic(
                double avgLikes,
                double avgReposts,
                double avgComments
        ) {}
    }
}

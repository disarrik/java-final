package ru.hse.de.vkcli.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.hse.de.vkcli.api.model.friend.Friend;
import ru.hse.de.vkcli.api.model.friend.FriendsResponse;
import ru.hse.de.vkcli.api.model.group.Group;
import ru.hse.de.vkcli.api.model.group.GroupsResponse;
import ru.hse.de.vkcli.api.model.search.User;
import ru.hse.de.vkcli.api.model.search.UserSearchResponse;
import ru.hse.de.vkcli.api.model.wall.Post;
import ru.hse.de.vkcli.api.model.wall.PostType;
import ru.hse.de.vkcli.api.model.wall.WallResponse;

public class StubVkApi implements VkApi {
    private static final int STUB_COUNT = 5;
    private final Random random = new Random();

    @Override
    public UserSearchResponse getUsersFromCity(int cityCode, int offset) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < STUB_COUNT; i++) {
            users.add(new User(
                    "user_" + (offset + i),
                    "Name" + (offset + i),
                    "Surname" + (offset + i),
                    random.nextBoolean()
            ));
        }
        return new UserSearchResponse(users);
    }

    @Override
    public FriendsResponse getFriends(String userId, int offset) {
        List<Friend> friends = new ArrayList<>();
        String[] cities = {"Moscow", "Saint-Petersburg", "Kazan", "Novosibirsk", "Ekaterinburg"};
        for (int i = 0; i < STUB_COUNT; i++) {
            friends.add(new Friend(
                    "friend_" + (offset + i),
                    "Friend" + (offset + i),
                    "Surname" + (offset + i),
                    cities[random.nextInt(cities.length)],
                    random.nextBoolean(),
                    random.nextInt(1000) + 1
            ));
        }
        return new FriendsResponse(friends);
    }

    @Override
    public GroupsResponse getGroups(String userId, int offset) {
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < STUB_COUNT; i++) {
            groups.add(new Group(
                    "group_" + (offset + i),
                    "Group " + (offset + i),
                    random.nextInt(100000) + 100
            ));
        }
        return new GroupsResponse(groups);
    }

    @Override
    public WallResponse getWall(String userId, int offset) {
        List<Post> posts = new ArrayList<>();
        PostType[] types = PostType.values();
        String[] texts = {
                "Interesting post about life",
                "Discussion of important topics",
                "Photo from vacation",
                "Thoughts out loud",
                "Shared a link"
        };
        for (int i = 0; i < STUB_COUNT; i++) {
            posts.add(new Post(
                    "post_" + (offset + i),
                    userId,
                    Instant.now().minusSeconds(random.nextInt(86400 * 30)),
                    texts[random.nextInt(texts.length)],
                    types[random.nextInt(types.length)],
                    random.nextInt(1000),
                    random.nextInt(500),
                    random.nextInt(200)
            ));
        }
        return new WallResponse(posts);
    }
}

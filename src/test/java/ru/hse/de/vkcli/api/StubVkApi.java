package ru.hse.de.vkcli.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public UserSearchResponse getUsersFromCity(int cityCode, int offset) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int userOffset = offset + i;
            users.add(new User(
                    "user_" + userOffset,
                    "Name" + userOffset,
                    "Surname" + userOffset,
                    true
            ));
        }
        return new UserSearchResponse(users);
    }

    @Override
    public FriendsResponse getFriends(String userId, int offset) {
        List<Friend> friends = new ArrayList<>();
        
        int friendCount;
        if ("user_1".equals(userId)) {
            friendCount = 10;
        } else {
            friendCount = 1;
        }
        
        if (offset > 0) {
            return new FriendsResponse(List.of());
        }
        
        for (int i = 0; i < friendCount && i < STUB_COUNT; i++) {
            friends.add(new Friend(
                    "friend_" + (offset + i),
                    "Friend" + (offset + i),
                    "Surname" + (offset + i),
                    "Moscow",
                    true,
                    100
            ));
        }
        return new FriendsResponse(friends);
    }

    @Override
    public GroupsResponse getGroups(String userId, int offset) {
        List<Group> groups = new ArrayList<>();
        if (offset > 0) {
            return new GroupsResponse(List.of());
        }
        for (int i = 0; i < STUB_COUNT; i++) {
            groups.add(new Group(
                    "group_" + (offset + i),
                    "Group " + (offset + i),
                    1000
            ));
        }
        return new GroupsResponse(groups);
    }

    @Override
    public WallResponse getWall(String userId, int offset) {
        List<Post> posts = new ArrayList<>();
        if (offset > 0) {
            return new WallResponse(List.of());
        }
        for (int i = 0; i < STUB_COUNT; i++) {
            posts.add(new Post(
                    "post_" + (offset + i),
                    userId,
                    Instant.now().minusSeconds(86400L * i),
                    "Post text " + i,
                    PostType.POST,
                    10,
                    5,
                    3
            ));
        }
        return new WallResponse(posts);
    }
}

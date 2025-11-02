package ru.hse.de.vkcli.api;

import ru.hse.de.vkcli.api.model.friend.FriendsResponse;
import ru.hse.de.vkcli.api.model.group.GroupsResponse;
import ru.hse.de.vkcli.api.model.search.UserSearchResponse;
import ru.hse.de.vkcli.api.model.wall.WallResponse;

public interface VkApi {
    UserSearchResponse getUsersFromCity(int cityCode, int offset);

    FriendsResponse getFriends(int offset);

    GroupsResponse getGroups(String userId, int offset);

    WallResponse getWall(String userId, int offset);
}

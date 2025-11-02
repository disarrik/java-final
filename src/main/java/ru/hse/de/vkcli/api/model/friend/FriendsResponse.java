package ru.hse.de.vkcli.api.model.friend;

import java.util.List;

public record FriendsResponse(
        List<Friend> friends
) {
}

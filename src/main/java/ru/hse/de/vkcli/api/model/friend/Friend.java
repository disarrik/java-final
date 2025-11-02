package ru.hse.de.vkcli.api.model.friend;

public record Friend(
        String id,
        String name,
        String surname,
        String city,
        boolean openProfile,
        int friendsCount
) {
}

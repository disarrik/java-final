package ru.hse.de.vkcli.api.model.search;

import java.util.List;

public record UserSearchResponse(
        List<User> users
) {
}

package ru.hse.de.vkcli.api.model.wall;

import java.util.List;

public record WallResponse(
        List<Post> posts
) {
}

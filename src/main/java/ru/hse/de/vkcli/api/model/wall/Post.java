package ru.hse.de.vkcli.api.model.wall;

import java.time.Instant;

public record Post(
    String id,
    String ownerId,
    Instant date,
    String text,
    PostType type,
    int likes,
    int reposts,
    int commentsAmount
) {}

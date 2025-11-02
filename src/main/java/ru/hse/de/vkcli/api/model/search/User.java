package ru.hse.de.vkcli.api.model.search;

public record User(
        String id,
        String name,
        String surname,
        boolean openProfile
) {
}

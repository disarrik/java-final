package ru.hse.de.vkcli.api.model.group;

import java.util.List;

public record GroupsResponse(
    List<Group> group
) {}

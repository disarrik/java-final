package ru.hse.de.vkcli.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.de.vkcli.api.model.friend.Friend;
import ru.hse.de.vkcli.api.model.friend.FriendsResponse;
import ru.hse.de.vkcli.api.model.group.Group;
import ru.hse.de.vkcli.api.model.group.GroupsResponse;
import ru.hse.de.vkcli.api.model.search.User;
import ru.hse.de.vkcli.api.model.search.UserSearchResponse;
import ru.hse.de.vkcli.api.model.wall.Post;
import ru.hse.de.vkcli.api.model.wall.PostType;
import ru.hse.de.vkcli.api.model.wall.WallResponse;
import ru.hse.de.vkcli.config.ConfigReader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class VkApiImpl  implements VkApi {

    private static final Logger LOG = LoggerFactory.getLogger(VkApiImpl.class);
    private static final int FRIENDS_PAGE_SIZE = 5;
    private static final int GROUPS_COUNT = 10;
    private static final int WALL_POSTS_COUNT = 100;
    private static final int USERS_FROM_CITY_COUNT = 10;

    private static final String accessToken = ConfigReader.getToken();
    private static final String BASE_URL = "https://api.vk.com/method/";
    private static final String API_VERSION = ConfigReader.getApiVersion();
    private static final int SLEEP_DURATION = ConfigReader.getSleepDuration();
    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public VkApiImpl(){
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public FriendsResponse getFriends(String userId, int offset){
        LOG.info("GetFriends");
        String url = BASE_URL + "friends.get" +
                "?user_id=" + userId +
                "&count=" + FRIENDS_PAGE_SIZE +
                "&offset=" + offset +
                "&fields=city,counters,is_closed" +
                "&access_token=" + accessToken +
                "&v=" + API_VERSION;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            LOG.debug("Response body: {}", response.body());
            FriendsResponse friendsResponse = mapFriendsResponse(response.body());
            LOG.info("Mapped friends response: {}", friendsResponse);
            Thread.sleep(SLEEP_DURATION);

            return friendsResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserSearchResponse getUsersFromCity(int cityCode, int offset) {
        LOG.info("getUsersFromCity");
        String url = BASE_URL + "users.search" +
                "?city=" + cityCode +
                "&count=" + USERS_FROM_CITY_COUNT +
                "&offset=" + offset +
                "&fields=is_closed" +
                "&access_token=" + accessToken +
                "&v=" + API_VERSION;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            LOG.debug("Response body: {}", response.body());
            UserSearchResponse userSearchResponse = mapUserSearchResponse(response.body());
            LOG.info("Mapped user search response: {}", userSearchResponse);

            Thread.sleep(SLEEP_DURATION);
            return userSearchResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GroupsResponse getGroups(String userId, int offset) {
        LOG.info("getGroups");
        String url = BASE_URL + "groups.get" +
                "?user_id=" + userId +
                "&count=" + GROUPS_COUNT +
                "&offset=" + offset +
                "&fields=members_count" +
                "&extended=1" +
                "&access_token=" + accessToken +
                "&v=" + API_VERSION;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            LOG.debug("Response body: {}", response.body());
            GroupsResponse groupsResponse = mapGroupsResponse(response.body());
            LOG.info("Mapped groups response: {}", groupsResponse);
            Thread.sleep(SLEEP_DURATION);

            return groupsResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WallResponse getWall(String userId, int offset) {
        LOG.info("getWall");
        String url = BASE_URL + "wall.get" +
                "?owner_id=" + userId +
                "&count=" + WALL_POSTS_COUNT +
                "&offset=" + offset +
                "&access_token=" + accessToken +
                "&v=" + API_VERSION;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            LOG.debug("Response body: {}", response.body());
            WallResponse wallResponse = mapWallResponse(response.body());
            LOG.info("Mapped wall response: {}", wallResponse);
            Thread.sleep(SLEEP_DURATION);

            return wallResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FriendsResponse mapFriendsResponse(String body) {
        JsonObject root = gson.fromJson(body, JsonObject.class);
        if (root == null) {
            return new FriendsResponse(List.of());
        }

        JsonObject error = asObject(root.get("error"));
        if (error != null) {
            int code = asInt(error.get("error_code"), 0);
            String message = asString(error.get("error_msg"), "Unknown error");
            throw new RuntimeException("VK API error %d: %s".formatted(code, message));
        }

        JsonObject responseObj = asObject(root.get("response"));
        JsonArray items = responseObj != null ? asArray(responseObj.get("items")) : null;
        if (items == null) {
            return new FriendsResponse(List.of());
        }

        List<Friend> friends = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject item = asObject(element);
            if (item == null) {
                continue;
            }

            String id = asString(item.get("id"), "");
            String firstName = asString(item.get("first_name"), "");
            String lastName = asString(item.get("last_name"), "");

            JsonObject city = asObject(item.get("city"));
            String cityTitle = city != null ? asString(city.get("title"), null) : null;

            boolean openProfile = !asBoolean(item.get("is_closed"), false);
            JsonObject counters = asObject(item.get("counters"));
            int friendsCount = counters != null ? asInt(counters.get("friends"), 0) : 0;

            friends.add(new Friend(id, firstName, lastName, cityTitle, openProfile, friendsCount));
        }

        return new FriendsResponse(friends);
    }

    private UserSearchResponse mapUserSearchResponse(String body) {
        JsonObject root = gson.fromJson(body, JsonObject.class);
        if (root == null) {
            return new UserSearchResponse(List.of());
        }

        JsonObject error = asObject(root.get("error"));
        if (error != null) {
            int code = asInt(error.get("error_code"), 0);
            String message = asString(error.get("error_msg"), "Unknown error");
            throw new RuntimeException("VK API error %d: %s".formatted(code, message));
        }

        JsonObject responseObj = asObject(root.get("response"));
        JsonArray items = responseObj != null ? asArray(responseObj.get("items")) : null;
        if (items == null) {
            return new UserSearchResponse(List.of());
        }

        List<User> users = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject item = asObject(element);
            if (item == null) {
                continue;
            }

            String id = asString(item.get("id"), "");
            String firstName = asString(item.get("first_name"), "");
            String lastName = asString(item.get("last_name"), "");
            boolean openProfile = !asBoolean(item.get("is_closed"), false);

            users.add(new User(id, firstName, lastName, openProfile));
        }

        return new UserSearchResponse(users);
    }

    private GroupsResponse mapGroupsResponse(String body) {
        JsonObject root = gson.fromJson(body, JsonObject.class);
        if (root == null) {
            return new GroupsResponse(List.of());
        }

        JsonObject error = asObject(root.get("error"));
        if (error != null) {
            int code = asInt(error.get("error_code"), 0);
            String message = asString(error.get("error_msg"), "Unknown error");
            throw new RuntimeException("VK API error %d: %s".formatted(code, message));
        }

        JsonObject responseObj = asObject(root.get("response"));
        JsonArray items = responseObj != null ? asArray(responseObj.get("items")) : null;
        if (items == null) {
            return new GroupsResponse(List.of());
        }

        List<Group> groups = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject item = asObject(element);
            if (item == null) {
                continue;
            }

            String id = asString(item.get("id"), "");
            String title = asString(item.get("name"), "");
            int membersCount = asInt(item.get("members_count"), 0);

            groups.add(new Group(id, title, membersCount));
        }

        return new GroupsResponse(groups);
    }

    private WallResponse mapWallResponse(String body) {
        JsonObject root = gson.fromJson(body, JsonObject.class);
        if (root == null) {
            return new WallResponse(List.of());
        }

        JsonObject error = asObject(root.get("error"));
        if (error != null) {
            int code = asInt(error.get("error_code"), 0);
            String message = asString(error.get("error_msg"), "Unknown error");
            throw new RuntimeException("VK API error %d: %s".formatted(code, message));
        }

        JsonObject responseObj = asObject(root.get("response"));
        JsonArray items = responseObj != null ? asArray(responseObj.get("items")) : null;
        if (items == null) {
            return new WallResponse(List.of());
        }

        List<Post> posts = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject item = asObject(element);
            if (item == null) {
                continue;
            }

            String id = asString(item.get("id"), "");
            String ownerId = asString(item.get("owner_id"), "");
            
            long dateSeconds = asInt(item.get("date"), 0);
            Instant date = Instant.ofEpochSecond(dateSeconds);
            
            String text = asString(item.get("text"), "");
            
            String postTypeStr = asString(item.get("post_type"), "post");
            PostType type = mapPostType(postTypeStr);
            
            JsonObject likes = asObject(item.get("likes"));
            int likesCount = likes != null ? asInt(likes.get("count"), 0) : 0;
            
            JsonObject reposts = asObject(item.get("reposts"));
            int repostsCount = reposts != null ? asInt(reposts.get("count"), 0) : 0;
            
            JsonObject comments = asObject(item.get("comments"));
            int commentsCount = comments != null ? asInt(comments.get("count"), 0) : 0;

            posts.add(new Post(id, ownerId, date, text, type, likesCount, repostsCount, commentsCount));
        }

        return new WallResponse(posts);
    }

    private PostType mapPostType(String postTypeStr) {
        if (postTypeStr == null) {
            return PostType.POST;
        }
        return switch (postTypeStr.toLowerCase()) {
            case "post" -> PostType.POST;
            case "copy" -> PostType.COPY;
            case "reply" -> PostType.REPLY;
            case "postpone" -> PostType.POSTPONE;
            case "suggest" -> PostType.SUGGEST;
            default -> PostType.POST;
        };
    }

    private JsonObject asObject(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private JsonArray asArray(JsonElement element) {
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
    }

    private boolean asBoolean(JsonElement element, boolean defaultValue) {
        return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : defaultValue;
    }

    private int asInt(JsonElement element, int defaultValue) {
        return element != null && element.isJsonPrimitive() ? element.getAsInt() : defaultValue;
    }

    private String asString(JsonElement element, String defaultValue) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : defaultValue;
    }
}

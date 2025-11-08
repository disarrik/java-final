package ru.hse.de.vkcli.cli;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.hse.de.vkcli.api.model.search.User;
import ru.hse.de.vkcli.api.model.wall.PostType;
import ru.hse.de.vkcli.statistic.model.StatisticReport;

public class ReportFormatter {
    private final DecimalFormat decimalFormat;

    public ReportFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat = new DecimalFormat("#.##", symbols);
    }

    public void print(StatisticReport report, OutputStream out) {
        PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);
        User user = report.mostFriendlyUser();
        writer.printf("=== СТАТИСТИКА ПОЛЬЗОВАТЕЛЯ: %s %s (id: %s) ===%n",
                user.name(), user.surname(), user.id());
        writer.println();
        
       printFriendsSection(writer, report.friendsReport());
       writer.println();
        
        printGroupsSection(writer, report.groupsReport());
        writer.println();
        
        printPostsSection(writer, report.wallStatistic());
    }

    private void printFriendsSection(PrintWriter writer, StatisticReport.FriendsReport friendsReport) {
        writer.println("[ДРУЗЬЯ]");
        writer.printf("Всего друзей: %d%n", friendsReport.friendsCount());
        
        Map<String, Long> top5Cities = friendsReport.top5Cities();
        if (!top5Cities.isEmpty()) {
            writer.println("Топ-5 городов:");
            int totalFriendsCount = friendsReport.friendsCount();
            AtomicInteger index = new AtomicInteger(1);
            top5Cities.entrySet().stream()
                    .forEach(entry -> {
                        String city = entry.getKey();
                        Long count = entry.getValue();
                        double percentage = totalFriendsCount > 0 ? (count.doubleValue() / totalFriendsCount) * 100.0 : 0.0;
                        writer.printf(" %d. %s: %d друзей (%.1f%%)%n",
                                index.getAndIncrement(), city, count, percentage);
                    });
        }
    }

    private void printGroupsSection(PrintWriter writer, StatisticReport.GroupsReport groupsReport) {
        writer.println("[ГРУППЫ]");
        writer.printf("Всего групп: %d%n", groupsReport.groupsCount());
        writer.printf("Средний размер группы: %.0f участников%n",
                groupsReport.avgGroupParticipants());
        
        if (groupsReport.bestGroup() != null) {
            writer.printf("Самая большая группа: %s (%d участников)%n",
                    groupsReport.bestGroup().title(),
                    groupsReport.bestGroup().membersCount());
        }
        
        if (groupsReport.worseGroup() != null) {
            writer.printf("Самая маленькая группа: %s (%d участников)%n",
                    groupsReport.worseGroup().title(),
                    groupsReport.worseGroup().membersCount());
        }
        
        if (!groupsReport.top10Groups().isEmpty()) {
            writer.println("Топ-10 групп:");
            AtomicInteger index = new AtomicInteger(1);
            groupsReport.top10Groups().stream()
                    .forEach(group -> writer.printf(" %d. %s (%d участников)%n",
                            index.getAndIncrement(), group.title(), group.membersCount()));
        }
    }

    private void printPostsSection(PrintWriter writer, StatisticReport.WallStatistic wallStatistic) {
        writer.println("[ПОСТЫ]");
        writer.printf("Всего постов: %d%n", wallStatistic.postsAmount());
        
        StatisticReport.WallStatistic.PostsStatistic postsStat = wallStatistic.postsStatistic();
        double avgActivity = postsStat.avgLikes() + postsStat.avgReposts() + postsStat.avgComments();
        writer.printf("Средняя активность: %s реакций на пост%n", decimalFormat.format(avgActivity));
        
        writer.printf("Средняя длина текста: %.0f символов%n", wallStatistic.avgPostCharsAmount());
        
        if (!wallStatistic.top3Posts().isEmpty()) {
            writer.println("Топ-3 поста:");
            AtomicInteger index = new AtomicInteger(1);
            wallStatistic.top3Posts().stream()
                    .forEach(post -> {
                        String text = post.text() != null ? post.text() : "(без текста)";
                        writer.printf(" %d. \"%s\" (%d лайков, %d репостов)%n",
                                index.getAndIncrement(), text,
                                post.likes(), post.reposts());
                    });
        }
        
        Map<PostType, Integer> distribution = wallStatistic.postTypesDistribution();
        if (!distribution.isEmpty()) {
            writer.println("Распределение по типам:");
            distribution.entrySet().stream()
                    .forEach(entry -> writer.printf(" - %s: %d%n", entry.getKey().name(), entry.getValue()));
        }
    }
}

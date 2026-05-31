package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;

public class StarboardList implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        Guild guild = Objects.requireNonNull(event.getGuild());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Starboard List for Server " + guild.getName());

        ArrayList<Starboard> starboards = getStarboards(Objects.requireNonNull(event.getGuild()).getIdLong());
        if (starboards.isEmpty()) {
            embed.setDescription("No starboards found!");
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        int numRemaining = 0;
        Starboard first =  starboards.get(0);

        if (starboards.size() > 5) {
            numRemaining = starboards.size() - 5;
            starboards.subList(5, starboards.size()).clear();
        }

        StringBuilder starboardList = new StringBuilder();
        starboardList.append("**\"").append(first.starboardName).append("\"**\n")
                .append("Emoji: ").append(first.starboardEmoji).append("\n")
                .append("Starboard ID: **").append(first.starboardId).append("**\n")
                .append("Channel: **#").append(Objects.requireNonNull(event.getGuild().getTextChannelById(first.channelId)).getName()).append("**\n")
                .append("Minimum Reactions: **").append(first.minReactions).append("**\n")
                .append("Messages Pinned: **").append(getPinCount(first.starboardId)).append("**");

        for (int i = 1; i < starboards.size(); i++) {
            Starboard current = starboards.get(i);
            starboardList.append("\n\n")
                    .append("**\"").append(current.starboardName).append("\"**\n")
                    .append("Emoji: ").append(current.starboardEmoji).append("\n")
                    .append("Starboard ID: **").append(current.starboardId).append("**\n")
                    .append("Channel: **#").append(Objects.requireNonNull(event.getGuild().getTextChannelById(current.channelId)).getName()).append("**\n")
                    .append("Minimum Reactions: **").append(current.minReactions).append("**\n")
                    .append("Messages Pinned: **").append(getPinCount(current.starboardId)).append("**");
        }

        if (numRemaining > 0) {
            starboardList.append("\n\n+ ").append(numRemaining).append(" more");
        }
        embed.setDescription(starboardList.toString());
        embed.setColor(mainColor);
        event.replyEmbeds(embed.build()).queue();
    }

    public static int getPinCount(int starboardId) {
        String query =
                """
                SELECT COUNT(*) FROM starboard_messages
                WHERE starboard_id = ?;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, starboardId);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Starboard> getStarboards(long guildId) {
        String getStarboardsQuery =
                """
                SELECT * FROM starboards
                WHERE guild_id = ?;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(getStarboardsQuery)) {
                stmt.setLong(1, guildId);
                ResultSet rs = stmt.executeQuery();
                ArrayList<Starboard> starboards = new ArrayList<>();
                while (rs.next()) {
                    starboards.add(new Starboard(
                            rs.getLong("guild_id"),
                            rs.getLong("channel_id"),
                            rs.getInt("starboard_id"),
                            rs.getInt("min_reactions"),
                            rs.getString("starboard_name"),
                            rs.getString("starboard_emoji")
                    ));
                }
                return starboards;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

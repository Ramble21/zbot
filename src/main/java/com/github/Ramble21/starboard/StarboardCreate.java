package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.*;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;

public class StarboardCreate implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("Invalid permissions! You need *Manage Server* in order to use this command.").setEphemeral(true).queue();
            return;
        }

        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        GuildChannel channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel();
        int minReactions = Objects.requireNonNull(event.getOption("min_reactions")).getAsInt();
        String emoji = event.getOption("emoji") != null ? Objects.requireNonNull(event.getOption("emoji")).getAsString().trim() : ":star:";
        String name = event.getOption("name") != null ? Objects.requireNonNull(event.getOption("name")).getAsString().trim() : channel.getName();

        if (!isValidEmoji(emoji, event.getChannel().asTextChannel())) {
            event.reply("Invalid emoji: \"" + emoji + "\"").queue();
            return;
        }

        Starboard board = new Starboard(guildId, channel.getIdLong(), -1, minReactions, name, emoji);
        if (starboardExists(guildId, channel.getIdLong(), emoji)) {
            event.reply("A starboard with that emoji already exists in that channel! Use `/starboard modify` to modify it or `/starboard delete` to delete it.").queue();
            return;
        }

        createStarboard(board);
        event.reply("Starboard \"" + name + "\" successfully created!").queue();
    }

    public static boolean isValidEmoji(String emoji, TextChannel channel) {
        try {
            Emoji emojiObj;
            if (emoji.matches("<a?:[a-zA-Z0-9_]+:\\d+>")) {
                emojiObj = Emoji.fromFormatted(emoji); // custom emoji format <:name:id>
            } else {
                emojiObj = Emoji.fromUnicode(emoji);
            }

            Message msg = channel.sendMessage("_ _").complete();
            try {
                msg.addReaction(emojiObj).complete();
                msg.delete().queue();
                return true;
            } catch (Exception e) {
                msg.delete().queue();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean starboardExists(long guildId, long channelId, String starboardEmoji) {
        String checkQuery =
                """
                SELECT 1 FROM starboards
                WHERE guild_id = ? AND channel_id = ? AND starboard_emoji = ?
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                stmt.setLong(1, guildId);
                stmt.setLong(2, channelId);
                stmt.setString(3, starboardEmoji);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createStarboard(Starboard board) {
        String createStarboardQuery =
                """
                INSERT INTO starboards (guild_id, channel_id, min_reactions, starboard_name, starboard_emoji)
                VALUES (?, ?, ?, ?, ?)
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(createStarboardQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, board.guildId);
                stmt.setLong(2, board.channelId);
                stmt.setInt(3, board.minReactions);
                stmt.setString(4, board.starboardName);
                stmt.setString(5, board.starboardEmoji);
                stmt.executeUpdate();

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    board.starboardId = keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
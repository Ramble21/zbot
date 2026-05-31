package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.starboard.StarboardCreate.isValidEmoji;
import static com.github.Ramble21.starboard.StarboardCreate.starboardExists;

public class SMEmoji implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        GuildChannel channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel();
        String oldEmoji = Objects.requireNonNull(event.getOption("old_emoji")).getAsString().trim();
        String newEmoji = Objects.requireNonNull(event.getOption("new_emoji")).getAsString().trim();

        if (!starboardExists(guildId, channel.getIdLong(), oldEmoji)) {
            event.reply("Failed to modify starboard: This starboard does not exist!").queue();
            return;
        }

        else if (starboardExists(guildId, channel.getIdLong(), newEmoji)) {
            event.reply("Failed to modify starboard: This starboard already exists!").queue();
            return;
        }

        if (!isValidEmoji(newEmoji, event.getChannel().asTextChannel())) {
            event.reply("Invalid emoji: \"" + newEmoji + "\"").queue();
            return;
        }

        String starboardName = updateStarboardEmoji(channel.getIdLong(), oldEmoji, newEmoji);
        event.reply("Starboard \"" + starboardName + "\" successfully modified!").queue();
    }

    public static String updateStarboardEmoji(long channelId, String oldEmoji, String newEmoji) {
        String updateEmojiQuery =
                """
                UPDATE starboards
                SET starboard_emoji = ?
                WHERE channel_id = ? AND starboard_emoji = ?
                RETURNING starboard_name;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(updateEmojiQuery)) {
                stmt.setString(1, newEmoji);
                stmt.setLong(2, channelId);
                stmt.setString(3, oldEmoji);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getString("starboard_name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

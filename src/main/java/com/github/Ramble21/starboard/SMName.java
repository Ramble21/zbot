package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.starboard.StarboardCreate.starboardExists;

public class SMName implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        GuildChannel channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel();
        String emoji = Objects.requireNonNull(event.getOption("emoji")).getAsString().trim();
        String newName = Objects.requireNonNull(event.getOption("name")).getAsString().trim();

        if (!starboardExists(guildId, channel.getIdLong(), emoji)) {
            event.reply("Failed to modify starboard: This starboard does not exist!").queue();
            return;
        }

        String starboardName = updateStarboardName(channel.getIdLong(), emoji, newName);
        event.reply("Starboard \"~~" + starboardName + "~~\" \"" + newName + "\" successfully modified!").queue();
    }

    public static String updateStarboardName(long channelId, String emoji, String newName) {
        String updateNameQuery =
                """
                WITH old AS (SELECT starboard_name FROM starboards WHERE channel_id = ? AND starboard_emoji = ?)
                UPDATE starboards
                SET starboard_name = ?
                WHERE channel_id = ? AND starboard_emoji = ?
                RETURNING (SELECT starboard_name FROM old);
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(updateNameQuery)) {
                stmt.setLong(1, channelId);
                stmt.setString(2, emoji);
                stmt.setString(3, newName);
                stmt.setLong(4, channelId);
                stmt.setString(5, emoji);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getString("starboard_name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

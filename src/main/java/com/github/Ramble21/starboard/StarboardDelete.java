package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.Zbot.local_user;
import static com.github.Ramble21.Zbot.prod_password;
import static com.github.Ramble21.Zbot.prod_user;

public class StarboardDelete implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        GuildChannel channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel();
        String emoji = event.getOption("emoji") != null ? Objects.requireNonNull(event.getOption("emoji")).getAsString().trim() : ":star:";
        String name = deleteStarboard(channel.getIdLong(), emoji);
        if (name == null) {
            event.reply("Failed to remove starboard: This starboard doesn't exist!").queue();
            return;
        }
        event.reply("Starboard \"" + name + "\" successfully deleted!").queue();
    }

    public static String deleteStarboard(long channelId, String starboardEmoji) {
        String deleteStarboardQuery =
                """
                DELETE FROM starboards
                WHERE channel_id = ? AND starboard_emoji = ?
                RETURNING starboard_name;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteStarboardQuery)) {
                stmt.setLong(1, channelId);
                stmt.setString(2, starboardEmoji);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getString("starboard_name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

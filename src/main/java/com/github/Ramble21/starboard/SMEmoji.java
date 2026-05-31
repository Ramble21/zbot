package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.Ramble21.Zbot.*;

public class SMEmoji implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {

    }

    public static void updateStarboardEmoji(long channelId, String oldEmoji, String newEmoji) {
        String updateEmojiQuery =
                """
                UPDATE starboards
                SET starboard_emoji = ?
                WHERE channel_id = ? AND starboard_emoji = ?;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(updateEmojiQuery)) {
                stmt.setString(1, newEmoji);
                stmt.setLong(2, channelId);
                stmt.setString(3, oldEmoji);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

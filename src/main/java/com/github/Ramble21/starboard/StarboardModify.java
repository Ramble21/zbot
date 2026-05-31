package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.Ramble21.Zbot.*;

public class StarboardModify {

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

    public static void updateStarboardChannel(String channelEmoji, long oldChannelId, long newChannelId) {
        String updateEmojiQuery =
                """
                UPDATE starboards
                SET channel_id = ?
                WHERE channel_id = ? AND starboard_emoji = ?;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(updateEmojiQuery)) {
                stmt.setLong(1, newChannelId);
                stmt.setLong(2, oldChannelId);
                stmt.setString(3, channelEmoji);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

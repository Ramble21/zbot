package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.Zbot.local_user;
import static com.github.Ramble21.Zbot.prod_password;
import static com.github.Ramble21.Zbot.prod_user;

public class StarboardDelete {

    public static void deleteStarboard(long channelId, String starboardEmoji) {
        String deleteStarboardQuery =
                """
                DELETE FROM starboards
                WHERE channel_id = ? AND starboard_emoji = ?;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteStarboardQuery)) {
                stmt.setLong(1, channelId);
                stmt.setString(2, starboardEmoji);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import java.sql.*;

import static com.github.Ramble21.Zbot.*;

public class StarboardCreate {

    public static void createStarboard(Starboard board) {
        String createStarboardQuery =
                """
                INSERT INTO starboards (starboard_id, guild_id, channel_id, starboard_name, starboard_emoji)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (starboard_id) DO NOTHING;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement insertLevelStmt = conn.prepareStatement(createStarboardQuery)) {
                insertLevelStmt.setLong(1, board.starboardId);
                insertLevelStmt.setLong(2, board.guildId);
                insertLevelStmt.setLong(3, board.channelId);
                insertLevelStmt.setString(4, board.starboardName);
                insertLevelStmt.setString(5, board.starboardEmoji);
                insertLevelStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

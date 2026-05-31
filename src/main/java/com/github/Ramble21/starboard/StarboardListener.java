package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.Zbot.local_user;
import static com.github.Ramble21.Zbot.prod_password;
import static com.github.Ramble21.Zbot.prod_user;

public class StarboardListener extends ListenerAdapter {

    public static void logMessage(StarboardMessage message) {
        String createStarboardQuery =
                """
                INSERT INTO starboard_messages (author_id, message_id, starboard_id)
                VALUES (?, ?, ?)
                ON CONFLICT (author_id, message_id, starboard_id) DO NOTHING;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement insertLevelStmt = conn.prepareStatement(createStarboardQuery)) {
                insertLevelStmt.setLong(1, message.authorId);
                insertLevelStmt.setLong(2, message.messageId);
                insertLevelStmt.setInt(3, message.starboardId);
                insertLevelStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

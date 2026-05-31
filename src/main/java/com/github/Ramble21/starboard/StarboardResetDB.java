package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.Zbot.local_user;
import static com.github.Ramble21.Zbot.prod_password;
import static com.github.Ramble21.Zbot.prod_user;

public class StarboardResetDB implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {

    }

    public static void resetDatabase() {
        String init =
                """
                BEGIN;
                
                DROP SCHEMA public CASCADE;
                CREATE SCHEMA public;
                
                CREATE TABLE starboards (
                    starboard_id SERIAL PRIMARY KEY,
                    guild_id     BIGINT NOT NULL,
                    channel_id   BIGINT NOT NULL,
                    min_reactions INT NOT NULL,
                    starboard_name TEXT NOT NULL,
                    starboard_emoji TEXT NOT NULL,
                    UNIQUE (channel_id, starboard_emoji)
                );
                
                CREATE TABLE starboard_messages (
                    author_id BIGINT NOT NULL,
                    message_id BIGINT NOT NULL,
                    starboard_id INT NOT NULL REFERENCES starboards(starboard_id) ON DELETE CASCADE,
                    PRIMARY KEY (author_id, message_id, starboard_id)
                );
                
                CREATE INDEX idx_starboards_guild_id ON starboards(guild_id);
                CREATE INDEX idx_starboard_messages_starboard_id ON starboard_messages(starboard_id);
                
                COMMIT;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute(init);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Database successfully reset!");
    }
}

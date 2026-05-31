package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.starboard.StarboardCreate.starboardExists;

public class SMMinReactions implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("Invalid permissions! You need *Manage Server* in order to use this command.").setEphemeral(true).queue();
            return;
        }

        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        GuildChannel channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel();
        String emoji = Objects.requireNonNull(event.getOption("emoji")).getAsString().trim();
        int minReactions = Objects.requireNonNull(event.getOption("min_reactions")).getAsInt();

        if (!starboardExists(guildId, channel.getIdLong(), emoji)) {
            event.reply("Failed to modify starboard: This starboard does not exist!").queue();
            return;
        }

        String starboardName = updateMinReactions(emoji, channel.getIdLong(), minReactions);
        event.reply("Starboard \"" + starboardName + "\" successfully modified!").queue();
    }

    public static String updateMinReactions(String channelEmoji, long channelId, int newMinReactions) {
        String updateEmojiQuery =
                """
                UPDATE starboards
                SET min_reactions = ?
                WHERE channel_id = ? AND starboard_emoji = ?
                RETURNING starboard_name;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(updateEmojiQuery)) {
                stmt.setInt(1, newMinReactions);
                stmt.setLong(2, channelId);
                stmt.setString(3, channelEmoji);
                ResultSet rs = stmt.executeQuery();
                return rs.next() ? rs.getString("starboard_name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.Ramble21.starboard;

import com.github.Ramble21.Zbot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.components.buttons.*;
import net.dv8tion.jda.api.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.Ramble21.Zbot.*;
import static com.github.Ramble21.Zbot.local_user;
import static com.github.Ramble21.Zbot.prod_password;
import static com.github.Ramble21.Zbot.prod_user;
import static com.github.Ramble21.starboard.StarboardList.getStarboards;

public class StarboardListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {

        assert event.getMember() != null;
        if (event.getMember().getUser().isBot()) {
            return;
        }

        String emoji = event.getEmoji().getFormatted();
        ArrayList<Starboard> starboards = getStarboards(Objects.requireNonNull(event.getGuild()).getIdLong());

        for (Starboard board : starboards) {
            if (emoji.equals(board.starboardEmoji) && event.getChannel().getIdLong() != board.channelId) {
                Message message = event.retrieveMessage().complete();
                int count = message.getReactions().stream()
                        .filter(r -> r.getEmoji().getAsReactionCode().equals(emoji))
                        .findFirst()
                        .map(MessageReaction::getCount)
                        .orElse(0);

                if (count >= board.minReactions) {
                    StarboardMessage sbm = new StarboardMessage(message.getAuthor().getIdLong(), message.getIdLong(), board.starboardId);
                    if (!logMessage(sbm)) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setAuthor(message.getAuthor().getName(), message.getAuthor().getAvatarUrl(), message.getAuthor().getAvatarUrl());
                        eb.setDescription(message.getContentRaw());
                        eb.setFooter(Objects.requireNonNull(message.getTimeCreated()).format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm a")));
                        eb.setColor(mainColor);

                        message.getAttachments().stream()
                                .filter(Message.Attachment::isImage)
                                .findFirst()
                                .ifPresent(img -> eb.setImage(img.getUrl()));

                        MessageChannel channel = event.getGuild().getTextChannelById(board.channelId);
                        assert channel != null;
                        channel.sendMessageEmbeds(eb.build())
                                .setContent(board.pingRole == null ? "" : board.pingRole)
                                .setComponents(ActionRow.of(
                                        Button.of(ButtonStyle.LINK, message.getJumpUrl(), " ")
                                                .withEmoji(Emoji.fromUnicode("🔗")))
                                )
                                .queue(sent -> sent.addReaction(Emoji.fromFormatted(emoji)).queue());
                    }
                }
            }
        }
    }

    public static boolean logMessage(StarboardMessage message) {
        String createStarboardQuery =
                """
                INSERT INTO starboard_messages (author_id, message_id, starboard_id)
                VALUES (?, ?, ?)
                ON CONFLICT (author_id, message_id, starboard_id) DO NOTHING
                RETURNING message_id;
                """;

        String url = Zbot.isRunningLocally() ? local_url : prod_url;
        String password = Zbot.isRunningLocally() ? local_password : prod_password;
        String user = Zbot.isRunningLocally() ? local_user : prod_user;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(createStarboardQuery)) {
                stmt.setLong(1, message.authorId);
                stmt.setLong(2, message.messageId);
                stmt.setInt(3, message.starboardId);
                ResultSet rs = stmt.executeQuery();
                return !rs.next(); // RETURNING gives a row only if inserted; no row means conflict
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

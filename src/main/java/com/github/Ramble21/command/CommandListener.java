package com.github.Ramble21.command;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("coinflip", "Flip a coin"));

        commandData.add(Commands.slash("starboard", "Commands related to Starboard functionality")
                .addSubcommands(
                        new SubcommandData("create", "Create a new Starboard")
                                .addOptions(
                                        new OptionData(OptionType.CHANNEL, "channel", "Channel where messages are pinned", true),
                                        new OptionData(OptionType.INTEGER, "min_reactions", "Minimum reactions to pin a message", true),
                                        new OptionData(OptionType.STRING, "emoji", "Emoji tracked for pinning messages", true),
                                        new OptionData(OptionType.STRING, "name", "Name of the starboard (defaults to channel name)", false)
                                ),
                        new SubcommandData("delete", "Delete a Starboard")
                                .addOptions(
                                        new OptionData(OptionType.CHANNEL, "channel", "Channel where this starboard pins messages", true),
                                        new OptionData(OptionType.STRING, "emoji", "Emoji this starboard tracks for pinning messages", true)
                                ),

                        new SubcommandData("reset_db", "Delete all Starboards and reset the database")
                )
                .addSubcommandGroups(
                        new SubcommandGroupData("modify", "Modify an existing Starboard")
                                .addSubcommands(
                                        new SubcommandData("emoji", "Modify the emoji used for an existing Starboard")
                                                .addOptions(
                                                        new OptionData(OptionType.CHANNEL, "channel", "Channel where this starboard pins messages", true),
                                                        new OptionData(OptionType.STRING, "old_emoji", "Emoji this starboard currently tracks for pinning messages", true),
                                                        new OptionData(OptionType.STRING, "new_emoji", "New emoji this starboard tracks for pinning messages", true)
                                                ),
                                        new SubcommandData("channel", "Modify the channel used for pinning messages of an existing Starboard")
                                                .addOptions(
                                                        new OptionData(OptionType.CHANNEL, "old_channel", "Channel where this starboard currently pins messages", true),
                                                        new OptionData(OptionType.STRING, "new_channel", "New channel where this starboard will pin messages", true),
                                                        new OptionData(OptionType.STRING, "emoji", "Emoji this starboard currently tracks for pinning messages", true)
                                                ),
                                        new SubcommandData("min_reactions", "Modify the minimum reactions needed for an existing Starboard")
                                                .addOptions(
                                                        new OptionData(OptionType.CHANNEL, "channel", "Channel where this starboard pins messages", true),
                                                        new OptionData(OptionType.STRING, "emoji", "Emoji this starboard currently tracks for pinning messages", true),
                                                        new OptionData(OptionType.STRING, "min_reactions", "New minimum reactions to pin a message", true)
                                                )
                                )
                )
        );

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("Command executed by user " + event.getUser().getGlobalName() + ": /" + event.getFullCommandName());
        CommandManager commandManager = new CommandManager();
        try {
            commandManager.executeCommand(event.getName(), event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

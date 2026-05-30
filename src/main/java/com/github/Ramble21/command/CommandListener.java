package com.github.Ramble21.command;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

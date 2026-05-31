package com.github.Ramble21.command;

import com.github.Ramble21.CoinFlip;
import com.github.Ramble21.starboard.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CommandManager {
    public static final Map<String, Command> commands = new HashMap<>();

    public CommandManager() {
        commands.put("coinflip", new CoinFlip());
        commands.put("starboard", new StarboardManager());
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public void executeCommand(String commandName, SlashCommandInteractionEvent event) throws IOException {
        Command command = getCommand(commandName);
        if (command != null) {
            command.execute(event);
        } else {
            System.out.println("Bug: Command " + commandName + " not found!");
        }
    }
}

class StarboardManager implements Command {
    public static final Map<String, Command> subcommands = new HashMap<>();

    public StarboardManager() {
        subcommands.put("create", new StarboardCreate());
        subcommands.put("delete", new StarboardDelete());
        subcommands.put("reset_db", new StarboardResetDB());
        subcommands.put("list", new StarboardList());
        subcommands.put("modify", new StarboardModifyManager());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String group = event.getSubcommandGroup(); // for nested subcommands
        String sub = event.getSubcommandName(); // for regular subcommands

        if (group != null) {
            Command groupHandler = subcommands.get(group);
            if (groupHandler != null) {
                groupHandler.execute(event);
                return;
            }
        }

        Command subcommand = subcommands.get(sub);
        if (subcommand != null) {
            subcommand.execute(event);
        } else {
            event.reply("Unknown subcommand").queue();
        }
    }
}

class StarboardModifyManager implements Command {
    public static final Map<String, Command> subcommands = new HashMap<>();

    public StarboardModifyManager() {
        subcommands.put("emoji", new SMEmoji());
        subcommands.put("channel", new SMChannel());
        subcommands.put("min_reactions", new SMMinReactions());
        subcommands.put("name", new SMName());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String group = event.getSubcommandGroup(); // for nested subcommands
        String sub = event.getSubcommandName(); // for regular subcommands

        if (group != null) {
            Command groupHandler = subcommands.get(group);
            if (groupHandler != null) {
                groupHandler.execute(event);
                return;
            }
        }

        Command subcommand = subcommands.get(sub);
        if (subcommand != null) {
            subcommand.execute(event);
        } else {
            event.reply("Unknown subcommand").queue();
        }
    }
}
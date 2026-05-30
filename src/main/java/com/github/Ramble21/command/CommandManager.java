package com.github.Ramble21.command;

import com.github.Ramble21.CoinFlip;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CommandManager{
    public static final Map<String, Command> commands = new HashMap<>();

    public CommandManager(){
        commands.put("coinflip", new CoinFlip());
    }

    public Command getCommand(String name){
        return commands.get(name);
    }

    public void executeCommand(String commandName, SlashCommandInteractionEvent event) throws IOException {
        Command command = getCommand(commandName);
        if (command != null) {
            command.execute(event);
        }
        else {
            System.out.println("Bug: Command " + commandName + " not found!");
        }
    }

}
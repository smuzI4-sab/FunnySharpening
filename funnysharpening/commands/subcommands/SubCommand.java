package me.funnysharpening.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract void onCommand(CommandSender sender, Command command, String label, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
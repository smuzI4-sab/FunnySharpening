package me.funnysharpening.commands;

import me.funnysharpening.FunnySharpening;
import me.funnysharpening.commands.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunnySharpeningCommand implements TabExecutor {

    private final FunnySharpening plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public FunnySharpeningCommand(FunnySharpening plugin) {
        this.plugin = plugin;
    }

    public void registerSubCommand(String name, SubCommand subCommand) {
        subCommands.put(name, subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("funnysharpening.use")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.permission-message")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /funnysharpening <give|reload>");
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage(ChatColor.RED + "Unknown sub-command: " + subCommandName);
            return true;
        }

        subCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String subCommandName : subCommands.keySet()) {
                if (subCommandName.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommandName);
                }
            }
        } else if (args.length > 1) {
            String subCommandName = args[0].toLowerCase();
            SubCommand subCommand = subCommands.get(subCommandName);
            if (subCommand != null) {
                completions = subCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return completions;
    }
}
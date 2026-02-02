package me.funnysharpening.commands.subcommands;

import me.funnysharpening.FunnySharpening;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends SubCommand {

    private final FunnySharpening plugin;

    public ReloadSubCommand(FunnySharpening plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin configuration.";
    }

    @Override
    public String getSyntax() {
        return "/funnysharpening reload";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfigData();
        sender.sendMessage(ChatColor.GREEN + "FunnySharpening configuration reloaded.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
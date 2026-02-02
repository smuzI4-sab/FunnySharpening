package me.funnysharpening.commands.subcommands;

import me.funnysharpening.FunnySharpening;
import me.funnysharpening.models.SharpeningItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveSubCommand extends SubCommand {

    private final FunnySharpening plugin;
    private final SharpeningItem sharpeningItem;

    public GiveSubCommand(FunnySharpening plugin) {
        this.plugin = plugin;
        this.sharpeningItem = plugin.getSharpeningItem();
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives a sharpening item to a player.";
    }

    @Override
    public String getSyntax() {
        return "/funnysharpening give <player> <amount> <level>";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getSyntax());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " not found.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Amount must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[1]);
            return;
        }

        String level = args[2].toUpperCase();
        if (!plugin.getLevelDamageMap().containsKey(level)) {
            sender.sendMessage(ChatColor.RED + "Invalid level: " + level);
            return;
        }

        ItemStack sharpening = plugin.getSharpeningItem().createSharpening(level);
        sharpening.setAmount(amount);
        target.getInventory().addItem(sharpening);

        String message = plugin.getMessage("give_success")
                .replace("%amount%", String.valueOf(amount))
                .replace("%name%", plugin.getSharpeningItem().name)
                .replace("%level%", level)
                .replace("%player%", target.getName());
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(partialName)) {
                    completions.add(onlinePlayer.getName());
                }
            }
        } else if (args.length == 3) {
            for (String level : plugin.getLevelDamageMap().keySet()) {
                if (level.startsWith(args[2].toUpperCase())) {
                    completions.add(level);
                }
            }
        }

        return completions;
    }
}
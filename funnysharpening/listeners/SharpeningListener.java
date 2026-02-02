package me.funnysharpening.listeners;

import me.funnysharpening.FunnySharpening;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SharpeningListener implements Listener {

    private final FunnySharpening plugin;
    private final NamespacedKey sharpeningKey;

    public SharpeningListener(FunnySharpening plugin) {
        this.plugin = plugin;
        this.sharpeningKey = new NamespacedKey(plugin, "sharpening-level");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (isSharpening(cursorItem) && isSword(clickedItem)) {
            event.setCancelled(true);
            applySharpening(player, cursorItem, clickedItem);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof LivingEntity) {
            ItemStack weapon = damager.getInventory().getItemInMainHand();

            if (isSword(weapon)) {
                double damageBonus = getDamageBonus(weapon);
                event.setDamage(event.getDamage() + damageBonus);
            }
        }
    }

    private double getDamageBonus(ItemStack sword) {
        return Optional.ofNullable(sword)
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getPersistentDataContainer)
                .map(container -> container.getOrDefault(sharpeningKey, PersistentDataType.INTEGER, 0))
                .map(level -> {
                    String romanLevel = toRoman(level);
                    return plugin.getLevelDamageMap().getOrDefault(romanLevel, 0.0);
                })
                .orElse(0.0);
    }

    private int getCurrentSharpeningLevel(ItemStack sword) {
        return Optional.ofNullable(sword)
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getPersistentDataContainer)
                .map(container -> container.getOrDefault(sharpeningKey, PersistentDataType.INTEGER, 0))
                .orElse(0);
    }


    private boolean isSharpening(ItemStack item) {
        return Optional.ofNullable(item)
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getDisplayName)
                .map(ChatColor::stripColor)
                .filter(displayName -> displayName.startsWith(ChatColor.stripColor(plugin.getSharpeningItem().name)))
                .isPresent();
    }


    private boolean isSword(ItemStack item) {
        return Optional.ofNullable(item)
                .map(ItemStack::getType)
                .map(type -> type == Material.WOODEN_SWORD || type == Material.STONE_SWORD || type == Material.IRON_SWORD ||
                        type == Material.GOLDEN_SWORD || type == Material.DIAMOND_SWORD || type == Material.NETHERITE_SWORD)
                .orElse(false);
    }


    private void applySharpening(Player player, ItemStack sharpening, ItemStack sword) {
        Optional.ofNullable(sharpening)
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getDisplayName)
                .map(ChatColor::stripColor)
                .map(displayName -> displayName.split(" ")[1])
                .ifPresentOrElse(levelString -> {
                    int levelToAdd = romanToInt(levelString);
                    Double damageBonus = plugin.getLevelDamageMap().get(levelString);

                    if (damageBonus == null) {
                        player.sendMessage(plugin.getMessage("invalid_level"));
                        return;
                    }

                    int currentLevel = getCurrentSharpeningLevel(sword);
                    int maxLevel = plugin.getMaxLevel();

                    if (currentLevel >= maxLevel) {
                        player.sendMessage(plugin.getMessage("max_level_reached"));
                        return;
                    }
                    int newLevel = Math.min(currentLevel + 1, maxLevel);

                    int successChance = plugin.getSuccessChance();
                    if (new Random().nextInt(100) < successChance) {

                        ItemMeta meta = sword.getItemMeta();
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        container.set(sharpeningKey, PersistentDataType.INTEGER, newLevel);
                        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                        lore.removeIf(line -> line.contains("Заточка"));
                        lore.add(ChatColor.GRAY + "Заточка " + toRoman(newLevel));
                        meta.setLore(lore);
                        sword.setItemMeta(meta);

                        if (player.getGameMode() != GameMode.CREATIVE) {
                            if (sharpening.getAmount() > 1) {
                                sharpening.setAmount(sharpening.getAmount() - 1);
                            } else {
                                player.setItemOnCursor(null);
                            }
                        }
                        String successMessage = plugin.getMessage("success").replace("%level%", toRoman(newLevel));
                        player.sendMessage(successMessage);

                    } else {
                        player.sendMessage(plugin.getMessage("failure"));
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            if (sharpening.getAmount() > 1) {
                                sharpening.setAmount(sharpening.getAmount() - 1);
                            } else {
                                player.setItemOnCursor(null);
                            }
                        }
                    }
                }, () -> player.sendMessage(plugin.getMessage("no_sharpening")));
    }


    private int romanToInt(String s) {
        return switch (s) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            default -> throw new IllegalArgumentException("Invalid Roman numeral: " + s);
        };
    }


    private String toRoman(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(num);
        };
    }
}
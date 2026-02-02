package me.funnysharpening.models;

import me.funnysharpening.FunnySharpening;
import me.funnysharpening.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class SharpeningItem {

    private final Material material;
    public String name;
    private final List<String> lore;

    public SharpeningItem(FunnySharpening plugin) {
        FileConfiguration config = plugin.getConfig();
        this.material = Material.valueOf(config.getString("sharpening.material"));
        this.name = ColorUtils.translateColorCodes(config.getString("sharpening.name"));
        this.lore = config.getStringList("sharpening.lore").stream()
                .map(ColorUtils::translateColorCodes)
                .collect(Collectors.toList());
    }

    public ItemStack createSharpening(String level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name + " " + level);
        List<String> updatedLore = lore.stream().collect(Collectors.toList());
        meta.setLore(updatedLore);
        item.setItemMeta(meta);
        return item;
    }
}
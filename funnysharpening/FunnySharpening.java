package me.funnysharpening;

import me.funnysharpening.commands.FunnySharpeningCommand;
import me.funnysharpening.commands.subcommands.GiveSubCommand;
import me.funnysharpening.commands.subcommands.ReloadSubCommand;
import me.funnysharpening.listeners.SharpeningListener;
import me.funnysharpening.models.SharpeningItem;
import me.funnysharpening.utils.ColorUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.PluginCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FunnySharpening extends JavaPlugin {

    private FileConfiguration config;
    private final Map<String, Double> levelDamageMap = new HashMap<>();
    private SharpeningItem sharpeningItem;
    private int maxLevel = 4;
    private int successChance;

    @Override
    public void onEnable() {
        // Загрузка конфигурации
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        config = loadConfig(configFile);

        if (config == null) {
            getLogger().severe("Failed to load config.yml. Plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadConfigValues();
        sharpeningItem = new SharpeningItem(this);

        getServer().getPluginManager().registerEvents(new SharpeningListener(this), this);

        FunnySharpeningCommand funnySharpeningCommand = new FunnySharpeningCommand(this);
        PluginCommand command = getCommand("funnysharpening");
        if (command != null) {
            command.setExecutor(funnySharpeningCommand);
            command.setTabCompleter(funnySharpeningCommand);
        }

        funnySharpeningCommand.registerSubCommand("give", new GiveSubCommand(this));
        funnySharpeningCommand.registerSubCommand("reload", new ReloadSubCommand(this));

        getLogger().info("FunnySharpening plugin enabled!");
    }

    private FileConfiguration loadConfig(File configFile) {
        try {
            return YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            getLogger().severe("Failed to load config.yml: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FunnySharpening plugin disabled!");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfigData() {
        reloadConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        config = loadConfig(configFile);
        loadConfigValues();
        sharpeningItem = new SharpeningItem(this);
        getLogger().info("FunnySharpening configuration reloaded.");
    }

    private void loadConfigValues() {
        loadLevelDamageMap();
        maxLevel = getConfig().getInt("max-level", 4);
        successChance = getConfig().getInt("sharpening.success_chance", 75);
    }

    private void loadLevelDamageMap() {
        levelDamageMap.clear();
        if (config.getConfigurationSection("levels") == null) {
            getLogger().severe("The 'levels' section is missing in config.yml. Plugin functionality may be limited.");
            return;
        }

        config.getConfigurationSection("levels").getKeys(false).forEach(level -> {
            double damage = config.getDouble("levels." + level);
            levelDamageMap.put(level.toUpperCase(), damage);
        });
    }

    public Map<String, Double> getLevelDamageMap() {
        return levelDamageMap;
    }

    public SharpeningItem getSharpeningItem() {
        return sharpeningItem;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getSuccessChance() {
        return successChance;
    }

    public String getMessage(String path) {
        return ColorUtils.translateColorCodes(config.getString("messages." + path, ""));
    }
}
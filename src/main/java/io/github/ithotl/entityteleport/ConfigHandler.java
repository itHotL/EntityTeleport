package io.github.ithotl.entityteleport;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Provides the (possibly empty) list of Multiverse
 * portals that should allow entity teleporting,
 * and the duration of time the portal should stay
 * open for entities after a button has been pressed.
 */
public class ConfigHandler {

    private static Main plugin;
    private static File configFile;
    private static FileConfiguration config;

    public ConfigHandler() {
        plugin = Main.getInstance();
        loadFile();
    }

    public static void reload() {
        reloadFile();
    }

    public List<String> getPortalList() {
        return config.getStringList("enabled-portals");
    }

    public int getAmountOfSecondsToActivatePortal() {
        return config.getInt("time-to-keep-portal-open-for-entities", 30);
    }

    public int getPercentageOfPortalToAnimate() {
        int percentage = config.getInt("particle-amount", 20);
        if (percentage < 0 || percentage > 100) {
            Bukkit.getLogger().warning("particle-amount cannot be less than 0 or more than 100, please check your config settings!");
            return 20;
        }
        return percentage;
    }

    public @NotNull Color getParticleColor() {
        String colorName = getColorCodeString();
        int colorCode = getColorCode(colorName);
        return getColor(colorCode);
    }

    private @NotNull String getColorCodeString() {
        String colorName = config.getString("particle-color", "0xFF00FF");
        return colorName.toUpperCase().replaceFirst("#", "0x");
    }

    private int getColorCode(String colorName) {
        try {
            return Integer.parseInt(colorName);
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().warning("NumberFormatException occurred - please check your config settings for particle-color!");
            return 0xFF00FF;
        }
    }

    private Color getColor(int colorCode) {
        try {
            return Color.fromRGB(colorCode);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("IllegalArgumentException occurred - please check your config settings for particle-color!");
            return Color.PURPLE;
        }
    }

    private void loadFile() {
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private static void reloadFile() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
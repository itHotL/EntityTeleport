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
        if (percentage < 0) {
            Bukkit.getLogger().warning("particle-amount cannot be less than 0, please check your config settings!");
            return 0;
        }
        return percentage;
    }

    public @NotNull Color getParticleColor() {
        String colorName = getColorCodeString();
        return getColor(colorName);
    }

    private @NotNull String getColorCodeString() {
        String colorName = config.getString("particle-color", "#FF00FF");
        return colorName.replaceFirst("#", "");
    }

    private Color getColor(@NotNull String colorName) {
        try {
            int r = Integer.valueOf(colorName.substring(0, 2),16);
            int g = Integer.valueOf(colorName.substring(2, 4),16);
            int b = Integer.valueOf(colorName.substring(4, 6),16);
            return Color.fromRGB(r, g, b);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("NumberFormatException occurred - please check your config settings for particle-color!");
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
package io.github.ithotl.entityteleport;

import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

    public Color getParticleColor() {
        int colorName = config.getInt("particle-color", 0x800080);
        try {
            return Color.fromRGB(colorName);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
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

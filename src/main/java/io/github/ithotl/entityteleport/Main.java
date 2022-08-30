package io.github.ithotl.entityteleport;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("hello world!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("goodbye world!");
    }
}

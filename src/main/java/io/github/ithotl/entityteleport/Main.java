package io.github.ithotl.entityteleport;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginCommand entityTeleport = this.getCommand("entityteleport");
        ReloadCommand reloadCommand = new ReloadCommand();
        if (entityTeleport != null) {
            entityTeleport.setExecutor(reloadCommand);
            entityTeleport.setTabCompleter(reloadCommand);
        }
        Bukkit.getLogger().info("EntityTeleport enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("EntityTeleport disabled!");
    }
}
package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        prepareCommands();
        loadMainClasses();

        Bukkit.getLogger().info("EntityTeleport enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("EntityTeleport disabled!");
    }

    public static Main getInstance() throws IllegalStateException {
        if (instance != null) {
            return instance;
        }
        throw new IllegalStateException("Cannot access EntityTeleport, it is not enabled!");
    }

    private void prepareCommands() {
        PluginCommand pluginReloadCommand = this.getCommand("entityteleport");
        ReloadCommand reloadCommand = new ReloadCommand();
        if (pluginReloadCommand != null) {
            pluginReloadCommand.setExecutor(reloadCommand);
            pluginReloadCommand.setTabCompleter(reloadCommand);
        }
    }

    private void loadMainClasses() {
        ConfigHandler config = new ConfigHandler();
        PortalManager mvPortals = getMVPortalManager();
        MyPortalManager portalManager = new MyPortalManager(config, mvPortals);
    }

    private PortalManager getMVPortalManager() throws IllegalStateException {
        MultiversePortals mvPortals = (MultiversePortals) Bukkit.getPluginManager().getPlugin("Multiverse-Portals");
        if (mvPortals != null) {
            return mvPortals.getPortalManager();
        }
        throw new IllegalStateException("Multiverse-Portals is not loaded, EntityTeleport will not work without it!");
    }
}
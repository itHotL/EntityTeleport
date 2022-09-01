package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is in charge of everything portal
 * related. It uses the Multiverse Portals API to
 * get the exact location of Multiverse portals that
 * should teleport entities, and the location to
 * teleport the entities to.
 */
public class MyPortalManager {

    private static MyPortalManager instance;
    private static ConfigHandler configHandler;
    private static PortalManager mvPortalManager;
    private static Set<ActivePortal> entityTeleportingPortals;

    public MyPortalManager(@NotNull ConfigHandler config, PortalManager mvPortalManager) {
        instance = this;
        MyPortalManager.configHandler = config;
        MyPortalManager.mvPortalManager = mvPortalManager;

        entityTeleportingPortals = getRelevantPortals(config.getPortalList());
        if (!entityTeleportingPortals.isEmpty()) {
            new PlayerInteractListener(this);
        }
    }

    public static void updateSettings() {
        entityTeleportingPortals = getRelevantPortals(configHandler.getPortalList());
        if (!entityTeleportingPortals.isEmpty() && !PlayerInteractListener.isRunning()) {
            new PlayerInteractListener(instance);
        }
    }

    public boolean isButtonOnRelevantPortal(@NotNull Location location) {
        World world = location.getWorld();
        if (world != null) {
            Set<ActivePortal> portalsInThisWorld = getPortalsInWorld(world);
            return portalsInThisWorld.stream()
                    .anyMatch(portal -> portal.containsLocation(location));
        }
        return false;
    }

    private static Set<ActivePortal> getRelevantPortals(@NotNull List<String> portalNames) {
        return portalNames.stream()
                .map(MyPortalManager::getMVPortal)
                .filter(Objects::nonNull)
                .map(ActivePortal::fromMVPortal)
                .collect(Collectors.toSet());
    }

    private static @Nullable MVPortal getMVPortal(@NotNull String portalName) {
        MVPortal portal = mvPortalManager.getPortal(portalName);
        if (portal != null) {
            return portal;
        }
        Bukkit.getLogger().warning("No Multiverse portal found with name " + portalName + "!" +
                "Please check your config file and ensure the names of your specified portals are correct");
        return null;
    }

    private Set<ActivePortal> getPortalsInWorld(World world) {
        return entityTeleportingPortals.stream()
                .filter(portal -> portal.world() == world)
                .collect(Collectors.toSet());
    }
}
package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
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

    private static ConfigHandler configHandler;
    private static PortalManager mvPortalManager;
    private static Set<EntityPortal> entityTeleportingPortals;

    public MyPortalManager(@NotNull ConfigHandler config, PortalManager mvPortalManager) {
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
            new PlayerInteractListener(Main.getPortalManager());
        }
    }

    public EntityPortal getRelevantPortal(@NotNull Block block) {
        World world = block.getLocation().getWorld();
        if (world != null) {
            Set<EntityPortal> portalsInThisWorld = getPortalsInWorld(world);
            return portalsInThisWorld.stream()
                    .filter(portal -> portal.isBlockOnMVPortal(block))
                    .findAny()
                    .orElse(null);
        }
        return null;
    }

    public void activatePortal(@NotNull EntityPortal entityPortal) {
        long duration = configHandler.getAmountOfSecondsToActivatePortal() * 20L;
        if (entityPortal.isActivated()) {
            entityPortal.deactivateImmediately();
        }
        entityPortal.activate(duration);
    }

    private static Set<EntityPortal> getRelevantPortals(@NotNull List<String> portalNames) {
        return portalNames.stream()
                .map(MyPortalManager::getMVPortal)
                .filter(Objects::nonNull)
                .map(EntityPortal::new)
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

    private Set<EntityPortal> getPortalsInWorld(World world) {
        return entityTeleportingPortals.stream()
                .filter(portal -> portal.world == world)
                .collect(Collectors.toSet());
    }
}
package io.github.ithotl.entityteleport;

import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
    private static EffectManager effectManager;
    private static Set<EntityPortal> entityTeleportingPortals;

    public MyPortalManager(@NotNull ConfigHandler config, PortalManager mvPortalManager) {
        MyPortalManager.configHandler = config;
        MyPortalManager.mvPortalManager = mvPortalManager;
        effectManager = new EffectManager(config);

        entityTeleportingPortals = getRelevantPortals(config.getPortalList());
        if (!entityTeleportingPortals.isEmpty()) {
            new PlayerInteractListener(this);
        }
    }

    public static void updateSettings() {
        entityTeleportingPortals = getRelevantPortals(configHandler.getPortalList());
        effectManager = new EffectManager(configHandler);

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

    public void activatePortal(EntityPortal entityPortal) {
        if (entityPortal.isActivated()) {
            return;
        }
        long duration = configHandler.getAmountOfSecondsToActivatePortal() * 20L;
        Bukkit.getLogger().info("(myPortalManager) duration: " + (duration / 20L));

        entityPortal.activate();
        BukkitTask animator = effectManager.provideParticles(entityPortal);
        BukkitTask teleporter = teleportEntities(entityPortal);
        new BukkitRunnable() {
            @Override
            public void run() {
                animator.cancel();
                teleporter.cancel();
                entityPortal.deactivate();
                Bukkit.getLogger().info("(cancel-runnable): animator cancelled");
            }
        }.runTaskLaterAsynchronously(Main.getInstance(), duration);
    }

    private @NotNull BukkitTask teleportEntities(@NotNull EntityPortal entityPortal) {
        MVDestination destination = entityPortal.destination;
        return new BukkitRunnable() {
            @Override
            public void run() {
                entityPortal.getEntitiesCurrentlyInsidePortal()
                        .forEach(entity -> {
                            entity.teleport(destination.getLocation(entity));
                            entity.setVelocity(destination.getVelocity());
                            Bukkit.getLogger().info(entity.getName() + " teleported!");
                        });
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
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
package io.github.ithotl.entityteleport;

import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiversePortals.MVPortal;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityPortal {

    private final EffectManager effectManager;

    protected String name;
    protected World world;
    protected BoundingBox boundingBox;
    protected ArrayList<Vector> portalInside;
    protected MVDestination destination;
    private BukkitTask particleTask;
    private BukkitTask teleportTask;
    private BukkitTask deactivator;

    public EntityPortal(@NotNull MVPortal portal) {
        effectManager = Main.getEffectManager();

        name = portal.getName();
        world = portal.getWorld();
        Vector minimumPoint = portal.getLocation().getMinimum();
        Vector maximumPoint = portal.getLocation().getMaximum();

        BoundingBox portalBox = BoundingBox.of(minimumPoint, maximumPoint);
        boundingBox = portalBox.expand(1.0, 1.0, 1.0, 1.0);

        portalInside = getPortalInside(maximumPoint, minimumPoint);
        destination = portal.getDestination();
    }

    public boolean isBlockOnMVPortal(@NotNull Block block) {
        BoundingBox portalWithFrame = boundingBox.clone().expand(2.0);
        return portalWithFrame.contains(block.getLocation().toVector());
    }

    public boolean isActivated() {
        return deactivator != null && !deactivator.isCancelled();
    }

    public void activate(long timer) {
        particleTask = startParticleTask();
        teleportTask = startEntityTeleportingTask();
        deactivator = scheduleDeactivation(timer);
    }

    public void deactivateImmediately() {
        if (deactivator != null && !deactivator.isCancelled()) {
            deactivator.cancel();
            deactivator = null;
        }
        deactivateRunningTasks();
    }

    private void deactivateRunningTasks() {
        if (particleTask != null && !particleTask.isCancelled()) {
            particleTask.cancel();
            particleTask = null;
        }
        if (teleportTask != null && !teleportTask.isCancelled()) {
            teleportTask.cancel();
            teleportTask = null;
        }
    }

    private @NotNull BukkitTask scheduleDeactivation(long timer) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                deactivateRunningTasks();
                deactivator = null;
            }
        }.runTaskLater(Main.getInstance(), timer);
    }

    private @NotNull BukkitTask startParticleTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                effectManager.spawnParticles(world, portalInside);
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    private @NotNull BukkitTask startEntityTeleportingTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                getEntitiesCurrentlyInsidePortal()
                        .forEach(entity -> {
                            entity.teleport(destination.getLocation(entity));
                            entity.setVelocity(destination.getVelocity());
                        });
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private @NotNull Collection<Entity> getEntitiesCurrentlyInsidePortal() {
        return world.getNearbyEntities(boundingBox);
    }

    private static @NotNull ArrayList<Vector> getPortalInside(@NotNull Vector maximumPoint, @NotNull Vector minimumPoint) {
        ArrayList<Vector> portalVectors = new ArrayList<>();
        double maxX = maximumPoint.getX() + 0.5;
        double maxY = maximumPoint.getY() + 0.5;
        double maxZ = maximumPoint.getZ() + 0.5;

        double minX = minimumPoint.getX() + 0.5;
        double minY = minimumPoint.getY() + 0.5;
        double minZ = minimumPoint.getZ() + 0.5;

        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    portalVectors.add(new Vector(x, y, z));
                }
            }
        }
        return portalVectors;
    }
}
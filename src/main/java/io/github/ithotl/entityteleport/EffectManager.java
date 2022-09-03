package io.github.ithotl.entityteleport;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Provides visual effects for portals to indicate
 * entities can currently go through.
 */
public class EffectManager {

    public EffectManager() {
    }

    /**
     * @param portal the EntityPortal to provide visual animation for
     * @param duration the amount of time in ticks the portal should
     *                 be animated
     */
    public void animatePortal(@NotNull EntityPortal portal, long duration) {
        BukkitTask animator = provideParticles(portal);
        new BukkitRunnable() {
            @Override
            public void run() {
                animator.cancel();
            }
        }.runTaskLaterAsynchronously(Main.getInstance(), duration);
    }

    private @NotNull BukkitTask provideParticles(@NotNull EntityPortal portal) {
        World world = portal.world();
        Vector topLeftCorner = portal.getTopLeftPortalCorner();
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (portal.isAlongZAxis()) {
                    for (int i = topLeftCorner.getBlockZ(); i <= portal.getTopRightPortalCorner().getBlockZ(); i++) {
                        world.spawnParticle(Particle.DRAGON_BREATH, topLeftCorner.toLocation(world), 1);
                        topLeftCorner.add(new Vector(0, 0, 1));
                    }
                }
                else {
                    for (int i = topLeftCorner.getBlockX(); i <= portal.getTopRightPortalCorner().getBlockX(); i++) {
                        world.spawnParticle(Particle.DRAGON_BREATH, topLeftCorner.toLocation(world), 1);
                        topLeftCorner.add(new Vector(1, 0, 0));
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }
}
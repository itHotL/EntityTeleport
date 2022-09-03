package io.github.ithotl.entityteleport;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
                Bukkit.getLogger().info("(cancel-runnable): animator cancelled");
            }
        }.runTaskLaterAsynchronously(Main.getInstance(), duration);
    }

    private @NotNull BukkitTask provideParticles(@NotNull EntityPortal portal) {
        World world = portal.world();

        return new BukkitRunnable() {
            @Override
            public void run() {
                portal.getTopRow().stream()
                        .parallel()
                        .forEach(vector -> spawnParticles(vector.toLocation(world)));
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    private void spawnParticles(@NotNull Location location) {
        Bukkit.getLogger().info("(spawnParticles) particles at: " + location);
        World world = location.getWorld();
        if (world != null) {
            world.spawnParticle(Particle.REDSTONE, location, 3, getDustOptions());
            world.spawnParticle(Particle.DRIPPING_OBSIDIAN_TEAR, location, 3);
        }
    }

    private @NotNull Particle.DustOptions getDustOptions() {
        Color color = Main.getConfigHandler().getParticleColor();
        return new Particle.DustOptions(color, 5);
    }
}
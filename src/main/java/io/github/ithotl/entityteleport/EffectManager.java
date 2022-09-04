package io.github.ithotl.entityteleport;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

/**
 * Provides visual effects for portals to indicate
 * entities can currently go through.
 */
public class EffectManager {

    private final Random random = new Random();
    private final Color particleColor;
    private final float particleAmount;

    public EffectManager(ConfigHandler config) {
        particleColor = config.getParticleColor();
        particleAmount = config.getPercentageOfPortalToAnimate() / 100F;
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
        ArrayList<Vector> portalBlocks = portal.getPortalInside();
        int totalBlocks = portalBlocks.size();
        Bukkit.getLogger().info("(provideParticles) totalBlocks: " + totalBlocks);
        int blocksToAnimate = getAmountOfPortalBlocksToAnimate(totalBlocks);
        Bukkit.getLogger().info("(provideParticles) blocksToAnimate: " + blocksToAnimate);

        return new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < blocksToAnimate; i++) {
                    Vector chosenBlock = portalBlocks.get(random.nextInt(totalBlocks));
                    spawnParticles(chosenBlock.toLocation(world));
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
    }

    private void spawnParticles(@NotNull Location location) {
        Bukkit.getLogger().info("(spawnParticles) particles at: " + location);
        World world = location.getWorld();
        if (world != null) {
            world.spawnParticle(Particle.REDSTONE, location, 1, 0.33, 0.5, 0.33, getDustOptions());
        }
    }

    private int getAmountOfPortalBlocksToAnimate(int totalAmount) {
        return Math.round(totalAmount * particleAmount);
    }

    private @NotNull Particle.DustOptions getDustOptions() {
        float size = random.nextFloat(0.25F, 2F);
        return new Particle.DustOptions(particleColor, size);
    }
}
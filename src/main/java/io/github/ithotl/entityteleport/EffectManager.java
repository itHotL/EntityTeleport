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

    public EffectManager(@NotNull ConfigHandler config) {
        particleColor = config.getParticleColor();
        particleAmount = config.getPercentageOfPortalToAnimate() / 10F;
    }

    /**
     * @param portal the EntityPortal to provide visual animation for
     */
    public @NotNull BukkitTask provideParticles(@NotNull EntityPortal portal) {
        World world = portal.world;
        ArrayList<Vector> portalBlocks = portal.portalInside;
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
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    private void spawnParticles(@NotNull Location location) {
        World world = location.getWorld();
        if (world != null) {
            world.spawnParticle(Particle.REDSTONE, location, 1, 0.33, 0.5, 0.33, getDustOptions());
        }
    }

    private int getAmountOfPortalBlocksToAnimate(int totalAmount) {
        return Math.round(totalAmount * particleAmount);
    }

    private @NotNull Particle.DustOptions getDustOptions() {
        float size = random.nextFloat(0.25F, 2.25F);
        return new Particle.DustOptions(particleColor, size);
    }
}
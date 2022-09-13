package io.github.ithotl.entityteleport;

import org.bukkit.*;
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
    private static ConfigHandler config;
    private static Color particleColor;
    private static float particleAmount;

    public EffectManager(@NotNull ConfigHandler configHandler) {
        EffectManager.config = configHandler;
        particleColor = config.getParticleColor();
        particleAmount = config.getPercentageOfPortalToAnimate() / 10F;
    }

    public static void updateSettings() {
        particleColor = config.getParticleColor();
        particleAmount = config.getPercentageOfPortalToAnimate() / 10F;
    }

    public void spawnParticles(World world, @NotNull ArrayList<Vector> particleLocations) {
        int totalBlocks = particleLocations.size();
        int blocksToAnimate = getAmountOfPortalBlocksToAnimate(totalBlocks);

        for (int i = 0; i < blocksToAnimate; i++) {
            Vector chosenBlock = particleLocations.get(random.nextInt(totalBlocks));
            spawnParticles(chosenBlock.toLocation(world));
        }
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
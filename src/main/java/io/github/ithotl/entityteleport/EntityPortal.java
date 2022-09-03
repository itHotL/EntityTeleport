package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record EntityPortal(String portalName, Vector minimumPoint, Vector maximumPoint, World world) {

    public static @NotNull EntityPortal fromMVPortal(@NotNull MVPortal portal) {
        String name = portal.getName();
        World world = portal.getWorld();
        Vector minimumPoint = portal.getLocation().getMinimum();
        Vector maximumPoint = portal.getLocation().getMaximum();

        return new EntityPortal(name, minimumPoint, maximumPoint, world);
    }

    public boolean containsLocation(@NotNull Location location) {
        Vector minWithPortalFrame = minimumPoint.clone().add(new Vector(-1, -1, -1));
        Vector maxWithPortalFrame = maximumPoint.clone().add(new Vector(1, 1, 1));

        return location.toVector().isInAABB(minWithPortalFrame, maxWithPortalFrame);
    }

    @Contract(" -> new")
    public @NotNull Vector getTopLeftPortalCorner() {
        if (isAlongZAxis()) {
            return new Vector(
                    maximumPoint.getBlockX(),
                    maximumPoint.getY(),
                    getLowestZCorner());
        }
        return new Vector(
                getLowestXCorner(),
                maximumPoint.getY(),
                maximumPoint.getBlockZ());
    }

    @Contract(" -> new")
    public @NotNull Vector getTopRightPortalCorner() {
        if (isAlongZAxis()) {
            return new Vector(
                    maximumPoint.getBlockX(),
                    maximumPoint.getY(),
                    getHighestZCorner());
        }
        return new Vector(
                getHighestXCorner(),
                maximumPoint.getY(),
                maximumPoint.getBlockZ());
    }

    /**
     * Checks if the portal's orientation is along the Z axis,
     * meaning the x-coordinates of the portal are no more than
     * 1 apart.
     */
    public boolean isAlongZAxis() {
        if (maximumPoint.getBlockX() > minimumPoint.getBlockX()) {
            return maximumPoint.getBlockX() - minimumPoint.getBlockX() <= 1;
        }
        return minimumPoint.getBlockX() - maximumPoint.getBlockX() <= 1;
    }

    private double getLowestZCorner() {
        return Math.min(maximumPoint.getZ(), minimumPoint.getZ());
    }

    private double getHighestZCorner() {
        return Math.max(maximumPoint.getZ(), minimumPoint.getZ());
    }

    private double getLowestXCorner() {
        return Math.min(maximumPoint.getX(), minimumPoint.getX());
    }

    private double getHighestXCorner() {
        return Math.max(maximumPoint.getX(), minimumPoint.getX());
    }
}
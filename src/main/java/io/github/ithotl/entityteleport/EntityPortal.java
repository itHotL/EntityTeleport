package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

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
                    maximumPoint.getX(),
                    maximumPoint.getY(),
                    getLowestZ());
        }
        return new Vector(
                getLowestX(),
                maximumPoint.getY(),
                maximumPoint.getZ());
    }

    public @NotNull Set<Vector> getTopRow() {
        if (isAlongZAxis()) {
            return getZAxisRow(maximumPoint.getY());
        }
        return getXAxisRow(maximumPoint.getY());
    }

    @Contract(" -> new")
    public @NotNull Vector getTopRightPortalCorner() {
        if (isAlongZAxis()) {
            return new Vector(
                    maximumPoint.getX(),
                    maximumPoint.getY(),
                    getHighestZ());
        }
        return new Vector(
                getHighestX(),
                maximumPoint.getY(),
                maximumPoint.getZ());
    }

    /**
     * Checks if the portal's orientation is along the Z axis,
     * meaning the x-coordinates of the portal are no more than
     * 1 apart.
     */
    public boolean isAlongZAxis() {
        boolean result;
        if (maximumPoint.getX() > minimumPoint.getX()) {
            result = maximumPoint.getX() - minimumPoint.getX() <= 1;
        }
        else {
            result = minimumPoint.getX() - maximumPoint.getX() <= 1;
        }
        Bukkit.getLogger().info("(entityPortal) is along z axis: " + result);
        return result;
    }


    private @NotNull Set<Vector> getZAxisRow(double y) {
        Set<Vector> row = new HashSet<>();
        Vector topLeftCorner = new Vector(maximumPoint.getX(), y, getLowestZ());
        row.add(topLeftCorner.clone());

        double width = getHighestZ() - getLowestZ();
        for (int i = 0; i <= width; i++) {
            row.add(topLeftCorner.add(new Vector(0, 0, 1)));
        }
        return row;
    }

    private @NotNull Set<Vector> getXAxisRow(double y) {
        Set<Vector> row = new HashSet<>();
        Vector topLeftCorner = new Vector(getLowestX(), y, maximumPoint.getZ());
        row.add(topLeftCorner.clone());

        double width = getHighestX() - getLowestX();
        for (int i = 0; i <= width; i++) {
            row.add(topLeftCorner.add(new Vector(1, 0, 0)));
        }
        return row;
    }

    private double getLowestZ() {
        return Math.min(maximumPoint.getZ(), minimumPoint.getZ());
    }

    private double getHighestZ() {
        return Math.max(maximumPoint.getZ(), minimumPoint.getZ());
    }

    private double getLowestX() {
        return Math.min(maximumPoint.getX(), minimumPoint.getX());
    }

    private double getHighestX() {
        return Math.max(maximumPoint.getX(), minimumPoint.getX());
    }
}
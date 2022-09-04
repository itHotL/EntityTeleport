package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    public @NotNull ArrayList<Vector> getPortalInside() {
        ArrayList<Vector> portalVectors = new ArrayList<>();
        double maxY = maximumPoint.getY() + 0.5;
        double minY = minimumPoint.getY() + 0.5;
        if (isAlongZAxis()) {
            for (double i = maxY; i >= minY; i--) {
                portalVectors.addAll(getZAxisRow(i));
            }
        }
        else {
            for (double i = maxY; i >= minY; i--) {
                portalVectors.addAll(getXAxisRow(i));
            }
        }
        return portalVectors;
    }

    /**
     * Checks if the portal's orientation is along the Z axis,
     * meaning the x-coordinates of the portal are no more than
     * 1 apart.
     */
    public boolean isAlongZAxis() {
        boolean result = maximumPoint.getX() == minimumPoint.getX();
        Bukkit.getLogger().info("(entityPortal) is along z axis: " + result);
        return result;
    }


    private @NotNull ArrayList<Vector> getZAxisRow(double y) {
        ArrayList<Vector> row = new ArrayList<>();
        Vector topLeftCorner = new Vector(maximumPoint.getX() + 0.5, y, getLowestZ() + 0.5);
        row.add(topLeftCorner.clone());

        double width = getHighestZ() - getLowestZ();
        for (int i = 0; i < width; i++) {
            row.add(topLeftCorner.add(new Vector(0, 0, 1)));
        }
        return row;
    }

    private @NotNull Set<Vector> getXAxisRow(double y) {
        Set<Vector> row = new HashSet<>();
        Vector topLeftCorner = new Vector(getLowestX() + 0.5, y, maximumPoint.getZ() + 0.5);
        row.add(topLeftCorner.clone());

        double width = getHighestX() - getLowestX();
        for (int i = 0; i < width; i++) {
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
package io.github.ithotl.entityteleport;

import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiversePortals.MVPortal;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityPortal {

    protected String name;
    protected World world;
    protected BoundingBox boundingBox;
    protected ArrayList<Vector> portalInside;
    protected MVDestination destination;
    private boolean isActivated;

    public EntityPortal(@NotNull MVPortal portal) {
        name = portal.getName();
        world = portal.getWorld();
        Vector minimumPoint = portal.getLocation().getMinimum();
        Vector maximumPoint = portal.getLocation().getMaximum();
        BoundingBox portalBox = BoundingBox.of(minimumPoint, maximumPoint);
        Bukkit.getLogger().info("bounding box: " + portalBox);
        boundingBox = portalBox.expand(1.0, 1.0, 1.0, 1.0);
        Bukkit.getLogger().info("new bounding box: " + boundingBox);
        portalInside = getPortalInside(maximumPoint, minimumPoint);
        destination = portal.getDestination();
    }

    public boolean isBlockOnMVPortal(@NotNull Block block) {
        BoundingBox portalWithFrame = boundingBox.clone().expand(2.0);
        Bukkit.getLogger().info("Block vector: " + block.getLocation().toVector());
        Bukkit.getLogger().info("Bounding box: " + portalWithFrame);
        Bukkit.getLogger().info("___________________________________________________________");
        return portalWithFrame.contains(block.getLocation().toVector());
    }

    public @NotNull Collection<Entity> getEntitiesCurrentlyInsidePortal() {
        return world.getNearbyEntities(boundingBox);
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void activate() {
        isActivated = true;
    }

    public void deactivate() {
        isActivated = false;
    }

    private static @NotNull ArrayList<Vector> getPortalInside(@NotNull Vector maximumPoint, @NotNull Vector minimumPoint) {
        ArrayList<Vector> portalVectors = new ArrayList<>();
        double maxX = maximumPoint.getX() + 0.5;
        double maxY = maximumPoint.getY() + 0.5;
        double maxZ = maximumPoint.getZ() + 0.5;
        Bukkit.getLogger().info("portal corner max: " + maxX + ", " + maxY + ", " + maxZ);

        double minX = minimumPoint.getX() + 0.5;
        double minY = minimumPoint.getY() + 0.5;
        double minZ = minimumPoint.getZ() + 0.5;
        Bukkit.getLogger().info("portal corner min: " + minX + ", " + minY + ", " + minZ);

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
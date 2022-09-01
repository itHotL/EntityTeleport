package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public record ActivePortal(String portalName, Vector minimumPoint, Vector maximumPoint, World world) {

    public static @NotNull ActivePortal fromMVPortal(@NotNull MVPortal portal) {
        String name = portal.getName();
        World world = portal.getWorld();
        Vector minimumPoint = portal.getLocation().getMinimum();
        Vector maximumPoint = portal.getLocation().getMaximum();
        Vector minWithPortalFrame = minimumPoint.add(new Vector(-1, -1, -1));
        Vector maxWithPortalFrame = maximumPoint.add(new Vector(1, 1, 1));

        return new ActivePortal(name, minWithPortalFrame, maxWithPortalFrame, world);
    }

    public boolean containsLocation(@NotNull Location location) {
        return location.toVector().isInAABB(minimumPoint, maximumPoint);
    }
}
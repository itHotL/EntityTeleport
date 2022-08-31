package io.github.ithotl.entityteleport;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.PortalLocation;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is in charge of everything portal
 * related. It uses the Multiverse Portals API to
 * get the exact location of Multiverse portals that
 * should teleport entities, and the location to
 * teleport the entities to.
 */
public class MyPortalManager {

    private final PortalManager mvPortalManager;
    private final MVWorldManager mvWorldManager;
    private final int entityTeleportWindow;

    private Set<MVPortal> relevantPortals;
    private Set<PortalLocation> portalLocations;

    public MyPortalManager(@NotNull ConfigHandler config, MVWorldManager mvWorldManager, PortalManager mvPortalManager) {
        this.mvPortalManager = mvPortalManager;
        this.mvWorldManager = mvWorldManager;
        entityTeleportWindow = config.getAmountOfSecondsToActivatePortal();

        relevantPortals = getRelevantPortals(config.getPortalList());
        if (!relevantPortals.isEmpty()) {
            portalLocations = getRelevantPortalLocations(relevantPortals);
            new PlayerInteractListener(this);
        }
    }

    public boolean isButtonOnRelevantPortal(Location location) {
        World world = location.getWorld();
        if (world != null) {
            MultiverseWorld mvWorld = mvWorldManager.getMVWorld(world);
            Set<MVPortal> portalsInThisWorld = getPortalsInWorld(mvWorld);
            return portalsInThisWorld.stream()
                    .anyMatch(mvPortal -> mvPortal.getLocation().getRegion().containsVector(location));
        }
        return false;
    }

    private Set<MVPortal> getPortalsInWorld(MultiverseWorld mvWorld) {
        return relevantPortals.stream()
                .filter(mvPortal -> mvPortal.getLocation().getMVWorld() == mvWorld)
                .collect(Collectors.toSet());
    }

    private Set<MVPortal> getRelevantPortals(@NotNull List<String> portalNames) {
        return portalNames.stream()
                .map(this::getMVPortal)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<PortalLocation> getRelevantPortalLocations(@NotNull Set<MVPortal> mvPortals) {
        return mvPortals.stream()
                .map(MVPortal::getLocation)
                .collect(Collectors.toSet());
    }

    private @Nullable MVPortal getMVPortal(@NotNull String portalName) {
        MVPortal portal = mvPortalManager.getPortal(portalName);
        if (portal != null) {
            return portal;
        }
        Bukkit.getLogger().warning("No Multiverse portal found with name " + portalName + "!" +
                "Please check your config file and ensure the names of your specified portals are correct");
        return null;
    }
}
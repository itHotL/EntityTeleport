package io.github.ithotl.entityteleport;

import com.onarandombox.MultiversePortals.MVPortal;

/**
 * This class is in charge of everything portal
 * related. It uses the Multiverse Portals API to
 * get the exact location of Multiverse portals that
 * should teleport entities, and the location to
 * teleport the entities to.
 */
public class PortalManager {

    private final MVPortal mvPortalHandler;

    public PortalManager(MVPortal mvPortalHandler) {
        this.mvPortalHandler = mvPortalHandler;
    }
}

package io.github.ithotl.entityteleport;

/**
 * Provides the (possibly empty) list of Multiverse
 * portals that should allow entity teleporting,
 * and the duration of time the portal should stay
 * open for entities after a button has been pressed.
 */
public class ConfigHandler {

    private final Main plugin;

    public ConfigHandler(Main plugin) {
        this.plugin = plugin;
    }
}

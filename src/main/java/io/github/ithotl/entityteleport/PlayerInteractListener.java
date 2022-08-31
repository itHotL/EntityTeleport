package io.github.ithotl.entityteleport;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for button-presses on Multiverse portal
 * frames.
 */
public class PlayerInteractListener implements Listener {

    private final MyPortalManager portalManager;

    public PlayerInteractListener(MyPortalManager portalManager) {
        this.portalManager = portalManager;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if (!clickedBlock.getType().getKey().getKey().contains("button")) {
            return;
        }


    }
}

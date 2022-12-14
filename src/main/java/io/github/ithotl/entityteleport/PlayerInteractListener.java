package io.github.ithotl.entityteleport;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
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
    private static boolean isRunning = false;

    public PlayerInteractListener(MyPortalManager portalManager) {
        this.portalManager = portalManager;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());

        isRunning = true;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @EventHandler
    public void onPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !clickedBlock.getType().getKey().getKey().contains("button")) {
            return;
        }

        Block supportingBlock = getBlockButtonIsOn(clickedBlock);
        EntityPortal entityPortal = portalManager.getRelevantPortal(supportingBlock);
        if (entityPortal != null) {
            portalManager.activatePortal(entityPortal);
        }
    }

    private @NotNull Block getBlockButtonIsOn(@NotNull Block clickedButton) {
        Directional buttonFace = (Directional) clickedButton.getBlockData();
        BlockFace attachedSide = buttonFace.getFacing().getOppositeFace();
        return clickedButton.getRelative(attachedSide);
    }
}
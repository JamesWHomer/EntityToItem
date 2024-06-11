package net.uber.entitytoitem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RightClickListener implements Listener {

    private EntityToItem main;

    public RightClickListener(EntityToItem main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() != null) {
            player.sendMessage("You right-clicked an entity!");
        }
    }

}

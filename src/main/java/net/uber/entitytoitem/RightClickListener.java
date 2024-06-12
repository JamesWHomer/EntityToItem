package net.uber.entitytoitem;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RightClickListener implements Listener {

    private final @NotNull List<String> allowedEntityList;

    public RightClickListener(@NotNull List<String> allowedEntityList) {

        this.allowedEntityList = allowedEntityList;

    }

    public boolean isAllowedEntity(Entity entity) {

        return allowedEntityList.contains(entity.getType().toString());

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (isAllowedEntity(entity)) {
            player.sendMessage("You right-clicked an allowed entity!");
        }
    }

}

package net.uber.entitytoitem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class RightClickListener implements Listener {

    private final List<String> allowedEntityList;
    private final String allowedItemString;

    public RightClickListener(List<String> allowedEntityList, String allowedItemString, boolean drop) {

        this.allowedEntityList = allowedEntityList;
        this.allowedItemString = allowedItemString;

    }

    public boolean isAllowedEntity(Entity entity) {

        return allowedEntityList.contains(entity.getType().toString());

    }

    public boolean isAllowedPlayerItem(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (Objects.equals(this.allowedItemString, "EMPTY")) {
            return itemInMainHand.getType() == Material.AIR && itemInOffHand.getType() == Material.AIR;
        } else {
            return isAllowedItem(itemInMainHand);
        }
    }

    public boolean isAllowedItem(ItemStack item) {

        if (ItemInfoExtractor.isOraxen(this.allowedItemString)) {
            /*
            if (!OraxenItems.exists(item)) {
                return false;
            }
            String allowedID = OraxenItems.getIdByItem(item);
            String itemID = ItemInfoExtractor.extractOraxen(this.allowedItemString);
            return Objects.equals(allowedID, itemID);
             */
            return false;
        }

        if (ItemInfoExtractor.isCMD(this.allowedItemString)) {
            int cmd = ItemInfoExtractor.extractCMD(this.allowedItemString);
            ItemMeta meta = item.getItemMeta();
            if (meta.hasCustomModelData()) {
                int itemCMD = meta.getCustomModelData();
                return cmd == itemCMD;
            } else {
                return false;
            }
        }

        return Objects.equals(item.getType().toString(), allowedItemString);

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (isAllowedEntity(entity) && isAllowedPlayerItem(player)) {
            ItemStack itemStack = EntityConverter.getEntityEgg(entity);
            Location location = entity.getLocation();
            location.getWorld().dropItemNaturally(location, itemStack);
            entity.remove();
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 0.1f, 2f);
        }
    }

}

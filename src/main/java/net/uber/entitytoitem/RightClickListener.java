package net.uber.entitytoitem;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RightClickListener implements Listener {

    private final List<String> allowedEntityList;
    private final List<String> allowedItemStringList;
    private final boolean drop;
    private final boolean consumable;

    ConfigManager configManager;

    public RightClickListener(ConfigManager configManager) {

        this.configManager = configManager;

        FileConfiguration config = configManager.getConfig();

        this.allowedEntityList = config.getStringList("allowedEntityList");
        this.allowedItemStringList = config.getStringList("item");
        this.drop = config.getBoolean("drop");
        this.consumable = config.getBoolean("consumable");

    }

    public boolean isAllowedEntity(Entity entity) {

        return allowedEntityList.contains(entity.getType().toString());

    }

    public boolean isPlayerHoldingAllowedItem(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        return isAllowedItem(itemInMainHand);
    }

    public boolean isAllowedItem(ItemStack item) {
        for (String itemString : this.allowedItemStringList) {
            if (isSameItem(item, itemString)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSameItem(ItemStack item, String allowedItemString) {

        if (ItemInfoExtractor.isOraxen(allowedItemString)) {
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

        if (ItemInfoExtractor.isCMD(allowedItemString)) {
            int cmd = ItemInfoExtractor.extractCMD(allowedItemString);
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasCustomModelData()) {
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
        if (isAllowedEntity(entity) && isPlayerHoldingAllowedItem(player)) {
            ItemStack itemStack = EntityConverter.getEntityEgg(entity);
            Location location = entity.getLocation();

            if (consumable) {
                removeItemFromHand(player);
            }

            if (drop) {
                location.getWorld().dropItemNaturally(location, itemStack);
            } else {
                /*
                HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(itemStack);
                if (!leftoverItems.isEmpty()) {
                    //Should be only 1 idgaf
                    int amount = leftoverItems.keySet().
                    Collection<ItemStack> itemstack = leftoverItems.values();
                    location.getWorld().dropItemNaturally(location, itemStack);
                }
                 */

                //additem return value is a better method but I don't have internet rn and Idk how to deal with the hashmaps

                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(itemStack);
                } else {
                    location.getWorld().dropItem(player.getLocation(), itemStack);
                }

            }


            entity.remove();
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 0.1f, 2f);
        }
    }

    private void removeItemFromHand(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        int currentAmount = mainHand.getAmount();
        if (currentAmount == 1) {
            inventory.setItemInMainHand(null);
        } else {
            mainHand.setAmount(currentAmount - 1);
        }
    }

}

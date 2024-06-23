package net.uber.entitytoitem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RightClickListener implements Listener {

    private final List<String> allowedEntityList;
    private final List<String> allowedItemStringList;
    private final boolean drop;
    private final boolean consumable;
    private final boolean tamedProtection;
    private final boolean loseInventory;

    private final String alreadyTamedMessage;

    ConfigManager configManager;

    public RightClickListener(ConfigManager configManager) {

        this.configManager = configManager;

        FileConfiguration config = configManager.getConfig();

        this.allowedEntityList = config.getStringList("allowedEntityList");
        this.allowedItemStringList = config.getStringList("item");
        this.drop = config.getBoolean("drop");
        this.consumable = config.getBoolean("consumable");
        this.tamedProtection = config.getBoolean("tamed-protection");
        this.loseInventory = config.getBoolean("lose-inventory");

        this.alreadyTamedMessage = config.getString("messages.already-tamed");

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

            List<ItemStack> itemsToDrop = new ArrayList<>();

            if (tamedProtection && entity instanceof Tameable tameable) {
                if (tameable.getOwner() != null && player.getUniqueId() != tameable.getOwner().getUniqueId()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyTamedMessage));
                    return;
                }
            }

            Location location = entity.getLocation();

            if (loseInventory && entity instanceof InventoryHolder inventoryHolder) {
                Inventory inventory = inventoryHolder.getInventory();
                ItemStack[] itemList = inventory.getContents();
                itemsToDrop.addAll(List.of(itemList));
                inventory.clear();
            }

            ItemStack entityEgg = EntityConverter.getEntityEgg(entity);

            if (consumable) {
                removeItemFromHand(player);
            }

            if (drop) {
                itemsToDrop.add(entityEgg);
            } else {
                /*
                HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(entityEgg);
                if (!leftoverItems.isEmpty()) {
                    //Should be only 1 idgaf
                    int amount = leftoverItems.keySet().
                    Collection<ItemStack> itemstack = leftoverItems.values();
                    location.getWorld().dropItemNaturally(location, entityEgg);
                }
                 */

                //additem return value is a better method but I don't have internet rn and Idk how to deal with the hashmaps

                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(entityEgg);
                } else {
                    itemsToDrop.add(entityEgg);
                }

            }

            entity.remove();

            for (ItemStack toDrop : itemsToDrop) {
                if (toDrop != null) {
                    location.getWorld().dropItemNaturally(location, toDrop);
                }
            }

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

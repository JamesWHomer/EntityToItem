package net.uber.entitytoitem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RightClickListener implements Listener {

    private final List<String> allowedEntityTypes;
    private final List<String> allowedItemStrings;
    private final boolean shouldDrop;
    private final boolean isConsumable;
    private final boolean hasTamedProtection;
    private final boolean shouldLoseInventory;
    private final String alreadyTamedMessage;

    public RightClickListener(ConfigManager configManager) {
        FileConfiguration config = configManager.getConfig();
        this.allowedEntityTypes = config.getStringList("allowedEntityList");
        this.allowedItemStrings = config.getStringList("item");
        this.shouldDrop = config.getBoolean("drop");
        this.isConsumable = config.getBoolean("consumable");
        this.hasTamedProtection = config.getBoolean("tamed-protection");
        this.shouldLoseInventory = config.getBoolean("lose-inventory");
        this.alreadyTamedMessage = config.getString("messages.already-tamed");
    }

    public boolean isAllowedEntity(Entity entity) {
        return allowedEntityTypes.contains(entity.getType().toString());
    }

    public boolean isPlayerHoldingAllowedItem(Player player) {
        return isAllowedItem(player.getInventory().getItemInMainHand());
    }

    public boolean isAllowedItem(ItemStack item) {
        return allowedItemStrings.stream().anyMatch(itemString -> isSameItem(item, itemString));
    }

    private boolean isSameItem(ItemStack item, String allowedItemString) {
        if (ItemInfoExtractor.isOraxen(allowedItemString)) {
            // Oraxen item check logic here
            return false;
        }

        if (ItemInfoExtractor.isCMD(allowedItemString)) {
            int cmd = ItemInfoExtractor.extractCMD(allowedItemString);
            return item.getItemMeta() != null &&
                    item.getItemMeta().hasCustomModelData() &&
                    item.getItemMeta().getCustomModelData() == cmd;
        }

        return Objects.equals(item.getType().toString(), allowedItemString);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (!isAllowedEntity(entity) || !isPlayerHoldingAllowedItem(player)) {
            return;
        }

        if (hasTamedProtection && !canInteractWithTamedEntity(player, entity)) {
            return;
        }

        List<ItemStack> itemsToDrop = collectItemsToDrop(entity);
        ItemStack entityEgg = EntityConverter.getEntityEgg(entity);
        itemsToDrop.add(entityEgg);

        if (isConsumable) {
            removeItemFromHand(player);
        }

        entity.remove();

        handleItemDistribution(player, entity.getLocation(), itemsToDrop);

        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 0.1f, 2f);
    }

    private boolean canInteractWithTamedEntity(Player player, Entity entity) {
        if (entity instanceof Tameable tameable && tameable.getOwner() != null) {
            if (!player.getUniqueId().equals(tameable.getOwner().getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyTamedMessage));
                return false;
            }
        }
        return true;
    }

    private List<ItemStack> collectItemsToDrop(Entity entity) {
        List<ItemStack> itemsToDrop = new ArrayList<>();
        if (shouldLoseInventory && entity instanceof InventoryHolder inventoryHolder) {
            for (ItemStack item : inventoryHolder.getInventory().getContents()) {
                if (item != null) {
                    itemsToDrop.add(item);
                }
            }
            inventoryHolder.getInventory().clear();
        }
        return itemsToDrop;
    }

    private void handleItemDistribution(Player player, Location location, List<ItemStack> itemsToDrop) {
        if (shouldDrop) {
            dropItems(location, itemsToDrop);
        } else {
            addItemsToPlayerInventory(player, location, itemsToDrop);
        }
    }

    private void dropItems(Location location, List<ItemStack> items) {
        for (ItemStack item : items) {
            if (item != null) {
                Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, item);
            }
        }
    }

    private void addItemsToPlayerInventory(Player player, Location location, List<ItemStack> items) {
        for (ItemStack item : items) {
            if (item != null) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                } else {
                    Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, item);
                }
            }
        }
    }

    private void removeItemFromHand(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        if (mainHand.getAmount() == 1) {
            inventory.setItemInMainHand(null);
        } else {
            mainHand.setAmount(mainHand.getAmount() - 1);
        }
    }
}

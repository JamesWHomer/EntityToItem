package net.uber.entitytoitem;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Locale;

public class EntityConverter {

    public static ItemStack getEntityEgg(Entity entity) {

        EntitySnapshot entitySnapshot = entity.createSnapshot();
        EntityType entityType = entity.getType();
        Material spawnEggMaterial = getSpawnEggMaterialExperimental(entityType);

        if (spawnEggMaterial == null) {
            throw new IllegalArgumentException("No spawn egg found for entity type: " + entityType);
        }

        ItemStack spawnEgg = new ItemStack(spawnEggMaterial);
        SpawnEggMeta meta = (SpawnEggMeta) spawnEgg.getItemMeta();
        if (meta != null) {
            meta.setSpawnedEntity(entitySnapshot);
            spawnEgg.setItemMeta(meta);
        }

        ItemMeta itemMeta = (SpawnEggMeta) spawnEgg.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(entity.getCustomName());
            spawnEgg.setItemMeta(itemMeta);
        }

        return spawnEgg;
    }

    private static Material getSpawnEggMaterial(EntityType entityType) {
        switch (entityType) {
            case CREEPER: return Material.CREEPER_SPAWN_EGG;
            case SKELETON: return Material.SKELETON_SPAWN_EGG;
            case SPIDER: return Material.SPIDER_SPAWN_EGG;
            case ZOMBIE: return Material.ZOMBIE_SPAWN_EGG;
            case SLIME: return Material.SLIME_SPAWN_EGG;
            case GHAST: return Material.GHAST_SPAWN_EGG;
            case PIG: return Material.PIG_SPAWN_EGG;
            case SHEEP: return Material.SHEEP_SPAWN_EGG;
            case COW: return Material.COW_SPAWN_EGG;
            case HORSE: return Material.HORSE_SPAWN_EGG;
            case DONKEY: return Material.DONKEY_SPAWN_EGG;
            case CHICKEN: return Material.CHICKEN_SPAWN_EGG;
            case ALLAY: return Material.ALLAY_SPAWN_EGG;
            case VILLAGER: return Material.VILLAGER_SPAWN_EGG;
            case CAT: return Material.CAT_SPAWN_EGG;
            case WOLF: return Material.WOLF_SPAWN_EGG;
            case LLAMA: return Material.LLAMA_SPAWN_EGG;
            case CAMEL: return Material.CAMEL_SPAWN_EGG;
            case PARROT: return Material.PARROT_SPAWN_EGG;
            // Add other entity types as needed
            // ...
            default: return null;
        }
    }

    private static Material getSpawnEggMaterialExperimental(EntityType entityType) {
        String eggName = entityType.name() + "_SPAWN_EGG";
        try {
            return Material.valueOf(eggName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
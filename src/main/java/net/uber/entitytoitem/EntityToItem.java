package net.uber.entitytoitem;

import org.bukkit.plugin.java.JavaPlugin;

public final class EntityToItem extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new RightClickListener(this), this);
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

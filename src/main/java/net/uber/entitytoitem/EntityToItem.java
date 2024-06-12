package net.uber.entitytoitem;

import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.text.html.parser.Entity;

public final class EntityToItem extends JavaPlugin {

    private static EntityToItem instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new RightClickListener(), this);

        configManager = new ConfigManager(this);
        configManager.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EntityToItem getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}

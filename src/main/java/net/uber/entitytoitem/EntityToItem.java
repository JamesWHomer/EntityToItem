package net.uber.entitytoitem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.text.html.parser.Entity;

public final class EntityToItem extends JavaPlugin {

    private static EntityToItem instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.saveDefaultConfig();

        FileConfiguration config = configManager.getConfig();

        getServer().getPluginManager().registerEvents(
                new RightClickListener(config.getStringList("allowedEntityList"), config.getString("item"), config.getBoolean("drop")), this);

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

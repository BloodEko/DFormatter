package de.bloodeko.dformatter.bukkit;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.bloodeko.dformatter.DFormatter;
import de.bloodeko.dformatter.core.Extensions;

/**
 * Holds settings which are used in the bukkit layer.
 */
public class Config {
    private DFormatter plugin;
    
    public File source;
    public File target;
    public boolean async;
    public String command;
    public Extensions filter;
    
    public Config(DFormatter plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Loads the settings from the config.yml file or creates
     * a new file in the plugins folder, if it doesn't exist.
     */
    public void reload() {
        plugin.saveDefaultConfig();
        File pluginsFolder = plugin.getDataFolder().getParentFile();
        File configFile = new File(plugin.getDataFolder() + "/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        loadValues(config, pluginsFolder);
    }
    
    /**
     * Reads the values from the FileConfiguration.
     * Uses default values for non existing entries.
     */
    private void loadValues(FileConfiguration config, File plugins) {
        source = new File(plugins + config.getString("sourceFolder", "/denizen/source"));
        target = new File(plugins + config.getString("targetFolder", "/denizen/scripts/generated"));
        async = config.getBoolean("async", true);
        command = config.getString("command", "ex reload");
        filter = new Extensions(config.getStringList("extensions"), config.getString("extension"));
    }
}

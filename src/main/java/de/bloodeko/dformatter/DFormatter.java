package de.bloodeko.dformatter;

import static de.bloodeko.dformatter.core.Util.EMPTY;

import org.bukkit.plugin.java.JavaPlugin;

import de.bloodeko.dformatter.bukkit.BukkitFactory;
import de.bloodeko.dformatter.bukkit.CmdHandler;
import de.bloodeko.dformatter.bukkit.Config;
import de.bloodeko.dformatter.bukkit.ReloadHandler;
import de.bloodeko.dformatter.bukkit.Results;

/**
 * Synchronizes a source/target folder. The script files
 * will be parsed and converted to colon syntax.
 */
public class DFormatter extends JavaPlugin {

    private Config config;
    private ReloadHandler handler;
    private CmdHandler cmdHandler;
    
    /**
     * Loads setting, initializes components
     * and mirrors folders synchronously.
     */
    @Override
    public void onEnable() {
        config = BukkitFactory.newConfig(this);
        handler = BukkitFactory.newReloadHandler(this);
        cmdHandler = BukkitFactory.newCmdHandler(this);
        loadCommand();
        syncFolders();
    }
    
    public ReloadHandler getHandler() { 
        return handler; 
    }
    
    public Config getSettings() { 
        return config; 
    }
    
    private void loadCommand() {
        getCommand("formatter").setExecutor(cmdHandler);
        getCommand("formatter").setTabCompleter(cmdHandler);
    }
    
    private void syncFolders() {
        handler.syncFolders(EMPTY, EMPTY, result -> {
            getLogger().info(Results.getMessage(result));
        }, false);
    }
}

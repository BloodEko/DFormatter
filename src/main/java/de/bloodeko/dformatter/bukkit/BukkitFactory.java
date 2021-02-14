package de.bloodeko.dformatter.bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import de.bloodeko.dformatter.DFormatter;

public class BukkitFactory {
    
    /**
     * Creates a new handler containing the ReloadCmd.
     */
    public static CmdHandler newCmdHandler(DFormatter plugin) {
        Map<String, FmCommand> cmds = new HashMap<>();
        cmds.put("reload", new ReloadCmd(plugin));
        return new CmdHandler(cmds);
    }
    
    /**
     * Creates settings object and initializes it.
     */
    public static Config newConfig(DFormatter plugin) {
        Config config = new Config(plugin);
        config.reload();
        return config;
    }
    
    /**
     * Creates a new handler which holds the 
     * settings and a new SingleThreadExecutor.
     */
    public static ReloadHandler newReloadHandler(DFormatter plugin) {
        return new ReloadHandler(plugin, plugin.getSettings(), 
          Executors.newSingleThreadExecutor());
    }
}

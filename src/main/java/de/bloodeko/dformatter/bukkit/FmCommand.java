package de.bloodeko.dformatter.bukkit;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.bloodeko.dformatter.DFormatter;

/**
 * Base for commands within the plugin.
 * Gives access to used dependencies.
 */
public abstract class FmCommand {
    DFormatter plugin;
    ReloadHandler handler;
    Config config;
    
    public FmCommand(DFormatter plugin) {
        this.plugin = plugin;
        this.handler = plugin.getHandler();
        this.config = plugin.getSettings();
    }
    
    public abstract void execute(CommandSender sender, String[] args);
    
    public abstract List<String> completeTab(String[] args);
}

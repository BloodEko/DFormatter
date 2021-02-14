package de.bloodeko.dformatter.bukkit;

import static de.bloodeko.dformatter.core.Util.contains;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.bloodeko.dformatter.DFormatter;
import de.bloodeko.dformatter.bukkit.Results.ResultConsumer;
import de.bloodeko.dformatter.core.Util;

/**
 * Synchronizes folders via the syntax "fm reload (simple) (silent)". 
 * With arguments to to suppress the command or the result message.
 */
public class ReloadCmd extends FmCommand {
    
    public ReloadCmd(DFormatter plugin) {
        super(plugin);
    }

    /**
     * Reloads settings. Synchronizes folders based 
     * on arguments and the settings specified.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        config.reload();
        
        boolean simple = contains(args, "simple");
        boolean silent = contains(args, "silent");
        boolean async = config.async;
        String command = config.command;
        
        Runnable onLock = Results.newOnLocked(sender);
        Runnable onSucess = Results.newOnSuccess(sender, command, simple);
        ResultConsumer consumer = Results.newConsumer(sender, silent);
        
        handler.syncFolders(onLock, onSucess, consumer, async);
    }

    /**
     * Filters tab-completion for the Reload command.
     * Will complete with either "simple" or "silent".
     */
    @Override
    public List<String> completeTab(String[] args) {
        List<String> tokens = Arrays.asList("simple", "silent");
        return Util.filterList(tokens, args[args.length-1]);
    }
}

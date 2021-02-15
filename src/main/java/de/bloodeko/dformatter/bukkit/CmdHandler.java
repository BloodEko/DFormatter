package de.bloodeko.dformatter.bukkit;

import static de.bloodeko.dformatter.core.Util.strip0;

import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.bloodeko.dformatter.core.Util;

/**
 * Dispatches registered FmCommands.
 * Also handles their tab completions.
 */
public class CmdHandler implements CommandExecutor, TabCompleter {
    private Map<String, FmCommand> cmds;
    
    public CmdHandler(Map<String, FmCommand> cmds) {
        this.cmds = cmds;
    }
    
    /**
     * Dispatches the first argument to a FmCommand
     * and executes it, with the first argument cut off.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        FmCommand fmCmd = cmds.get(args[0]);
        if (fmCmd == null) {
            return false;
        }
        fmCmd.execute(sender, strip0(args));
        return true;
    }
    
    /**
     * Dispatches tab-completion to a FmCommand
     * and returns the resulting filtered List.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Util.filterList(cmds.keySet(), args[0]);
        }
        FmCommand fmCmd = cmds.get(args[0]);
        if (fmCmd == null) {
            return null;
        }
        return fmCmd.completeTab(strip0(args));
    }
}

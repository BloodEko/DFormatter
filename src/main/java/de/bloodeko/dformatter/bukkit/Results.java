package de.bloodeko.dformatter.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.bloodeko.dformatter.core.Util;
import de.bloodeko.dformatter.core.Util.SyncResult;

/**
 * Holds utilities which are used to create, format and 
 * send messages and functions used for various outcomes.
 */
public class Results {
    private static ChatColor green = ChatColor.GREEN;
    private static ChatColor gold = ChatColor.GOLD;
    private static ChatColor red = ChatColor.RED;
    
    /**
     * Checks settings whether to print the result to the sender.
     * Also checks the values, to skip empty/non-relevant entries.
     */
    public static void print(SyncResult result, CommandSender sender) {
        sender.sendMessage(green + getMessage(result));
    }
    
    /**
     * Formats the result to a message. It displays the sum of changes.
     * When an error occurred for the operation, display that instead.
     */
    public static String getMessage(SyncResult result) {
        if (result.error) {
            return "Precompiling. " + red + "ERROR found! (check console)";
        } else {
            String changes = result.sum() == 1 ? " change" : " changes";
            return "Precompiling " + result.sum() + changes + ".";
        }
    }
    
    /**
     * Returns a Runnable that sends a cancel message.
     */
    public static Runnable newOnLocked(CommandSender sender) {
        return () -> sender.sendMessage(gold + "Formatter is already running.");
    }
    
    /**
     * Returns a Runnable that prints a formatted result.
     * When silent is specified, nothing will be performed.
     */
    public static ResultConsumer newConsumer(CommandSender sender, boolean silent) {
        return (result) -> {
            if (!silent) Results.print(result, sender);
        };
    }
    
    /**
     * Returns a Runnable that runs a command as console/player.
     */
    public static Runnable newRunCommand(CommandSender sender, String command) {
        return () -> {
            Bukkit.dispatchCommand(sender, command);
        };
    }
    
    /**
     * Returns a Runnable that runs a configured command.
     * When silent is specified, nothing will be performed.
     */
    public static Runnable newOnSuccess(CommandSender sender, String command, boolean simple) {
        return simple ? Util.EMPTY : Results.newRunCommand(sender, command);
    }
    
    public static interface ResultConsumer {
        public void consume(SyncResult result);
    }
}
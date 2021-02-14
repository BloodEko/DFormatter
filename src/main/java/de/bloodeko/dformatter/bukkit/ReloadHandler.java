package de.bloodeko.dformatter.bukkit;

import java.io.File;
import java.util.concurrent.Executor;

import org.bukkit.Bukkit;

import de.bloodeko.dformatter.DFormatter;
import de.bloodeko.dformatter.bukkit.Results.ResultConsumer;
import de.bloodeko.dformatter.core.Extensions;
import de.bloodeko.dformatter.core.Util;
import de.bloodeko.dformatter.core.Util.SyncResult;

/**
 * Entry point for synchronization in the bukkit layer.
 * It ensures only 1 process is running simultaneously:
 */
public class ReloadHandler {
    private final Executor executor;
    private final DFormatter plugin;
    private final Config config;
    
    private boolean locked;
    
    public ReloadHandler(DFormatter plugin, Config config, Executor executor) {
        this.plugin = plugin;
        this.executor = executor;
        this.config = config;
    }
    
    /**
     * Synchronizes the configured folders. Run onLocked in case a 
     * process is still running. The consumer consumes the SyncResult 
     * in any case. When no error is present, onSucess will be run.
     */
    public void syncFolders(Runnable onLocked, Runnable onSuccess, ResultConsumer consumer, boolean async) {
        if (locked) {
            onLocked.run();
            return;
        }
        locked = true;
        SyncOperation op = new SyncOperation(config.source, config.target,
                config.filter, onSuccess, consumer, async);
        if (async) {
            executor.execute(op);
        } else {
            op.run();
        }
    }

    /**
     * Object to be schedules and for performing the task.
     * For an exception, or when done, unlocks the ReloadHandler.
     */
    private class SyncOperation implements Runnable {
        private File source;
        private File target;
        private Extensions filter;
        private Runnable onSuccess;
        private ResultConsumer consumer;
        private boolean delay;
        
        public SyncOperation(File source, File target, Extensions filter,
          Runnable onSuccess, ResultConsumer consumer, boolean delay) {
            this.source = source;
            this.target = target;
            this.filter = filter;
            this.onSuccess = onSuccess;
            this.consumer = consumer;
            this.delay = delay;
        }
        
        @Override
        public void run() {
            try {
                runInternally();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                locked = false;
            }
        }
        
        private void runInternally() {
            SyncResult result = Util.syncFolders(source, target, filter);
            if (delay) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    runCallback(result);
                });
            } else {
                runCallback(result);
            }
        }
        
        private void runCallback(SyncResult result) {
            consumer.consume(result);
            if (!result.error) {
                onSuccess.run();
            }
        }
    }
    
}

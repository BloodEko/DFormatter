package de.bloodeko.dformatter.core;

import static de.bloodeko.dformatter.core.Util.checkFolder;

import java.io.File;

/**
 * Deletes files from the target-folder,
 * which don't exist in the source-folder.
 */
public class FileDeleter {
    private File source;
    private File target;
    private String sourcePath;
    private String targetPath;
    int filesCount;
    int folderCount;
    
    public FileDeleter(File source, File target) {
        this.source = source;
        this.target = target;
    }
    
    /**
     * Validates the source/target folder. Otherwise throws an exception.
     * Deletes all files in the target folder, which don't exist in source.
     */
    public void cleanTarget() {
        sourcePath = checkFolder(source).getAbsolutePath();
        targetPath = checkFolder(target).getAbsolutePath();
        resetCounter();
        cleanFolder(target);
    }
    
    private void cleanFolder(File folder) {
        if (!existsInSource(folder)) {
            delete(folder);
            return;
        }
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                cleanFile(file);
            } else {
                cleanFolder(file);
            }
        }
    }
    
    private void cleanFile(File file) {
        if (!existsInSource(file)) {
            delete(file);
        }
    }
    
    private boolean existsInSource(File file) {
        return Util.getRelativeFile(file, targetPath, sourcePath).exists();
    }

    private void delete(File path) {
        if (path.isFile()) {
            path.delete();
            filesCount++;
        } else {
            for (File file : path.listFiles()) {
                delete(file);
            }
            path.delete();
            folderCount++;
        }
    }
    
    private void resetCounter() {
        filesCount = 0;
        folderCount = 0;
    }
    
    public int getDeletedFiles() {
        return filesCount;
    }
    
    public int getDeletedFolders() {
        return folderCount;
    }
}

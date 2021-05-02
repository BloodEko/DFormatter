package de.bloodeko.dformatter.core;

import static de.bloodeko.dformatter.core.Util.checkFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Reads files in a customized format and pastes them to the destination.
 * Checks their existence/modification date, to check whether to paste.
 * Parses the content of the file, its extension matches the filter.
 */
public class FilePaster {
    private final File source;
    private final File target;
    private final Extensions filter;
    private String sourcePath;
    private String targetPath;
    private int mkdirCount;
    private int pasteCount;
    
    public FilePaster(File source, File target, Extensions filter) {
        this.source = source;
        this.target = target;
        this.filter = filter;
    }
    
    /**
     * Validates the source/target folder. Otherwise throws an exception.
     * Copies and parses modified files from the source to the target folder.
     */
    public void pasteFiles() {
        sourcePath = checkFolder(source).getAbsolutePath();
        targetPath = checkFolder(target).getAbsolutePath();
        resetCounter();
        pasteFolder(source);
    }
    
    private void pasteFolder(File folder) {
        mkdir(getTargetFile(folder));
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                pasteFile(file);
            } else {
                pasteFolder(file);
            }
        }
    }
    
    private void mkdir(File folder) {
        if (folder.exists()) {
            return;
        }
        folder.mkdir();
        mkdirCount++;
    }
    
    private void pasteFile(File file) {
        File to = getTargetFile(file);
        if (!to.exists() || isModified(file, to)) {
            try {
                if (!to.exists()) {
                    to.createNewFile();
                }
                if (filter.accepts(file)) {
                    Util.convertFile(file, to);
                } else {
                    Files.copy(file.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                to.setLastModified(file.lastModified());
                pasteCount++;
            }
            catch(IOException ex) {
                System.out.println("Could not pasteFile: " + ex.getMessage());
            }
        }
    }
    
    private File getTargetFile(File sourceFile) {
        return Util.getTargetFile(sourceFile, filter, sourcePath, targetPath);
    }
    
    private boolean isModified(File from, File to) {
        return from.lastModified() != to.lastModified();
    }
    
    private void resetCounter() {
        mkdirCount = 0;
        pasteCount = 0;
    }
    
    public int getMkdirCount() {
        return mkdirCount;
    }
    
    public int getPasteCount() {
        return pasteCount;
    }
}

package de.bloodeko.dformatter.core;

import java.io.File;
import java.util.List;

/**
 * Holds a list of allowed file extensions
 * and a single output extension.
 */
public class Extensions {
    private final List<String> extensions;
    private final String extension;
    
    public Extensions(List<String> extensions, String extension) {
        this.extensions = extensions;
        this.extension = extension;
    }
    
    /**
     * Returns true if the file has the output extension.
     */
    public boolean isOutputExtension(File file) {
        return file.getName().endsWith(extension);
    }
    
    /**
     * Returns false, if the file name starts with a dot.
     * Returns true if a file exists with any of the extensions.
     */
    public boolean existsWithAny(File file) {
        if (file.getName().startsWith(".")) {
            return false;
        }
        for (String extension : extensions) {
            if (withExtension(file, extension).exists()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns false if the file name is starting with a dot.
     * Returns true, if any of the extensions matches the files extension.
     */
    public boolean pasteFile(File file) {
        String name = file.getName();
        if (name.startsWith(".")) {
            return false;
        }
        for (String extension : extensions) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if the folder name is not 
     * starting with a dot.
     */
    public boolean pasteFolder(File folder) {
        return !folder.getName().startsWith(".");
    }

    /**
     * Returns a new File with the output extension applied.
     */
    public File withExtension(File file) {
        return withExtension(file, extension);
    }
    
    private File withExtension(File file, String extension) {
        String name = file.getAbsolutePath();
        return new File(getBasename(name) + extension);
    }
    
    private String getBasename(String name) {
        int dot = name.lastIndexOf('.');
        return name.substring(0, dot);
    }
}

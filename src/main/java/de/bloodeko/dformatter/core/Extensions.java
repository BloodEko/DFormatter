package de.bloodeko.dformatter.core;

import java.io.File;
import java.util.List;

/**
 * Holds a list of allowed file extensions
 * and a single output extension.
 */
public class Extensions {
    private String[] extensions;
    private String extension;
    
    public Extensions(List<String> extensions, String extension) {
        this.extensions = extensions.toArray(new String[0]);
        this.extension = extension;
    }
    
    /**
     * Returns true if the files extension matches
     * the single extension.
     */
    public boolean isOutputExtension(File file) {
        return file.getName().endsWith(extension);
    }
    
    /**
     * Returns true if a file exists with any of
     * the extensions.
     */
    public boolean existsWithAny(File file) {
        for (String extension : extensions) {
            if (withExtension(file, extension).exists()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if the files extension
     * matches any of the extensions.
     */
    public boolean accepts(File file) {
        String name = file.getName();
        for (String extension : extensions) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a new file with the single
     * extension applied.
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

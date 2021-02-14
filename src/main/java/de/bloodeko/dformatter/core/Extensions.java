package de.bloodeko.dformatter.core;

import java.io.File;
import java.util.List;

/**
 * Holds a list of allowed file extensions.
 */
public class Extensions {
    private String[] extensions;
    
    public Extensions(List<String> extensions) {
        this.extensions = extensions.toArray(new String[0]);
    }
    
    /**
     * Returns true if the input 
     * file has a valid extension.
     */
    public boolean accepts(File file) {
        return accepts(file.getName());
    }
    
    /**
     * Returns true if the input
     * string is a valid extension.
     */
    public boolean accepts(String name) {
        for (String extension : extensions) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}

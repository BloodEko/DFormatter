package de.bloodeko.dformatter.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Util {
    public static final Runnable EMPTY = () -> {};
    
    /**
     * Ensures that the input is an existing 
     * folder. Otherwise throws an exception.
     */
    public static File checkFolder(File file) {
        if (file == null) {
            throw new RuntimeException("Folder is null.");
        }
        if (file.isFile()) {
            throw new RuntimeException("File is not a folder.");
        }
        if (!file.exists()) {
            throw new RuntimeException("Folder does not exist.");
        }
        return file;
    }
    
    /**
     * Returns true for any of these scenarios: <br>
     * - input is a directory and relative exists. <br>
     * - input is extension and a relative with any of the extensions exist. <br>
     * - input has not any of the extensions and a relative exists.
     */
    public static boolean existsInSource(File input, Extensions filter, String target, String source) {
        File relative = getRelative(input, target, source);
        if (input.isDirectory()) {
            return relative.exists();
        }
        if (filter.isOutputExtension(input) && filter.existsWithAny(relative)) {
            return true;
        }
        return !filter.accepts(input) && relative.exists();
    }
    
    /**
     * If the input is no directory and the filter matches it,
     * returns the relative with the output extension applied.
     * Otherwise returns the relative.
     */
    public static File getTargetFile(File input, Extensions filter, String source, String target) {
        File relative = getRelative(input, source, target);
        if (!input.isDirectory() && filter.accepts(input)) {
            return filter.withExtension(relative);
        }
        return relative;
    }
    
    /**
     * The input file is a child of the baseFrom, calculates its relative path. <br>
     * Appends the relative path to the baseTo and returns this file.
     */
    private static File getRelative(File file, String baseFrom, String baseTo) {
        String relative = file.getAbsolutePath().substring(baseFrom.length());
        return new File(baseTo + relative);
    }
    
    /**
     * Overrides the File at the path the specified path.
     * Uses a buffered writer in the UTF8 format.
     */
    public static void writeFile(Path to, List<String> lines) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(to)) {
            for (Iterator<String> it = lines.iterator(); it.hasNext();) {
                bw.write(it.next());
                if (it.hasNext()) {
                    bw.write(System.lineSeparator());
                }
            }
            bw.close();
        }
    }
    
    /**
     * Converts the custom script lines to a general format.
     */
    public static List<String> convertLines(List<String> lines) {
        Parser parser = new Parser(lines);
        return parser.convert();
    }
    
    /**
     * Reads a file written in a customized format and
     * writes the converted content to the destination.
     */
    public static void convertFile(File from, File to) throws IOException {
        List<String> lines = Files.readAllLines(from.toPath());
        Util.writeFile(to.toPath(), convertLines(lines));
    }
    
    /**
     * Returns a list of entries that start with the argument.
     */
    public static List<String> filterList(Collection<String> filter, String arg) {
        List<String> list = new ArrayList<>();
        for (String key : filter) {
            if (key.startsWith(arg)) {
                list.add(key);
            }
        }
        return list;
    }
    
    /**
     * Checks if the input array contains an 
     * entry which equals the specified value.
     */
    public static boolean contains(String[] args, String val) {
        for (String arg : args) {
            if (arg.equals(val)) return true;
        }
        return false;
    }
    
    /**
     * If the length is 0 returns the input. Returns
     * a new array with the first entry cut off.
     */
    public static String[] strip0(String[] args) {
        if (args.length == 0) return args;
        return Arrays.copyOfRange(args, 1, args.length);
    }
    
    /**
     * Syncs the folders and provides information about the process.
     * Creates necessary source/target folder, if they don't exist.
     */
    public static SyncResult syncFolders(File source, File target, Extensions filter) {
        try {
            source.mkdirs();
            target.mkdirs();
            
            FileDeleter deleter = new FileDeleter(source, target, filter);
            deleter.cleanTarget();
            
            FilePaster paster = new FilePaster(source, target, filter);
            paster.pasteFiles();
            
            return SyncResult.from(deleter, paster);
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            return SyncResult.withError();
        }
    }
    
    /**
     * The result of a synchronization operation. 
     * Holds data about the performed actions.
     */
    public static class SyncResult {
        public int deletedFolders;
        public int deletedFiles;
        public int mkdirCount;
        public int pasteCount;
        public boolean error;
        
        private void consume(FileDeleter deleter) {
            deletedFolders = deleter.getDeletedFolders();
            deletedFiles = deleter.getDeletedFiles();
        }
        
        private void consume(FilePaster paster) {
            mkdirCount = paster.getMkdirCount();
            pasteCount = paster.getPasteCount();
        }
        
        public int sum() {
            return deletedFolders + deletedFiles + mkdirCount + pasteCount;
        }
        
        public static SyncResult from(FileDeleter deleter, FilePaster paster) {
            SyncResult result = new SyncResult();
            result.consume(deleter);
            result.consume(paster);
            return result;
        }
        
        public static SyncResult withError() {
            SyncResult result = new SyncResult();
            result.error = true;
            return result;
        }
    }
}

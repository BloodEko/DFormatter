package de.bloodeko.dformatter;

import java.io.File;
import java.util.Arrays;

import de.bloodeko.dformatter.core.Extensions;
import de.bloodeko.dformatter.core.Util;
import de.bloodeko.dformatter.core.Util.SyncResult;

public class CopyDemo {
    
    public static void main(String[] args) {
        String desktop = System.getProperty("user.home") + "/Desktop";
        File source = new File(desktop + "/mc/server-1-15-2-work/plugins/dformatter/source");
        File target = new File(desktop + "/mc/server-1-15-2-work/plugins/Denizen/scripts/generated");
        
        Extensions filter = new Extensions(Arrays.asList(".yml", ".dsc"), ".yml");
        SyncResult result = Util.syncFolders(source, target, filter);
        CopyDemoPrinter.print(result);
    }

    private static class CopyDemoPrinter {
        
        public static void print(SyncResult result) {
            if (result.error) return;
            System.out.println("Deleted folders: " + result.deletedFolders);
            System.out.println("Deleted files: " + result.deletedFiles);
            System.out.println("MkdirCount: " + result.mkdirCount);
            System.out.println("PasteCount: " + result.pasteCount);
        }
    }
}

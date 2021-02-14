package de.bloodeko.dformatter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import de.bloodeko.dformatter.core.Util;

/**
 * Checks functionality of the the parser.
 * Compares a result file with valid data.
 */
public class TestParser {
    
    @Test
    public void test() throws IOException {
        File source = new File("src/test/resources/source.yml");
        File target = new File("src/test/resources/target.yml");
        File temp = new File("src/test/resources/temp.yml");
        
        Util.convertFile(source, temp);
        assertTrue("File does not exist.", temp.exists());
        
        Object[] a1 = Files.readAllLines(target.toPath()).toArray();
        Object[] a2 = Files.readAllLines(temp.toPath()).toArray();
        assertArrayEquals("File content differs.", a1, a2);
        
        assertTrue("File size differs.", target.length() == temp.length());
    }
    
}

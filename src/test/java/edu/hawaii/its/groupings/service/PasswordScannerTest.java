package edu.hawaii.its.groupings.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class PasswordScannerTest {

    File file1;
    File dirname = new File("src/main/resources");

    @Before
    public void setUp() {
        try {
            file1 = File.createTempFile("temp", ".properties", dirname);
            FileWriter fw1 = new FileWriter(file1);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write("password=hello");
            bw1.close();
            fw1.close();
        }
        catch(IOException ioe) {
            System.err.println( "error creating file");
        }
    }

    @Test
    public void testCheckForPassWords() {
        PasswordScanner passwordScanner = new PasswordScanner();
        assertTrue(file1.exists());
        try {
            passwordScanner.init();
            //not working. need to find way to delete temp file
//            file1.delete();
        }
        catch(Exception e) {
            System.err.println("error finding password locations");
        }

    }
}

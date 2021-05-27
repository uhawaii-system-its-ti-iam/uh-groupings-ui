package edu.hawaii.its.groupings.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.Test;

import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

public class PasswordScannerTest {
    File dirname = new File("src/main/resources");

    @Before
    public void setUp() {
        assertTrue("Resource directory does not exist.", dirname.exists());
    }

    private File createFile(File path, String pwdValue) throws Exception {
        File file = File.createTempFile("temp", ".properties", path);
        try (FileWriter w = new FileWriter(file)) {
            w.write("password=" + pwdValue);
        }
        return file;
    }

    @Test
    public void testCheckForPassWordsFound() throws Exception {
        PasswordScanner passwordScanner = new PasswordScanner();
        File file = createFile(dirname, "hello");
        assertTrue(file.exists());
        try {
            passwordScanner.init();
            fail("Should have found password in temp file.");
        } catch (Exception e) {
            assertThat(e, instanceOf(PasswordFoundException.class));
            assertThat(e.toString(),
                    containsString(file.getPath() + " on line: 1"));
        } finally {
            file.delete();
        }
    }

    @Test
    public void testCheckForPassWordsNotFound() throws Exception {
        PasswordScanner passwordScanner = new PasswordScanner();
        File file = createFile(dirname, "");
        assertTrue(file.exists());
        try {
            passwordScanner.init();
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        } finally {
            file.delete();
        }
    }

    @Test
    public void testCheckForPassWordsMultipleFilesFound() throws Exception {
        PasswordScanner passwordScanner = new PasswordScanner();
        File file1 = createFile(dirname, "pwd1");
        File file2 = createFile(dirname, "pwd2");
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        try {
            passwordScanner.init();
            fail("Should have found password in temp file.");
        } catch (Exception e) {
            assertThat(e, instanceOf(PasswordFoundException.class));
            e.getMessage();
        } finally {
            file1.delete();
            file2.delete();
        }
    }
}

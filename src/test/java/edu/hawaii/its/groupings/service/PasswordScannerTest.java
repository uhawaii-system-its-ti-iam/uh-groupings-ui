package edu.hawaii.its.groupings.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.Test;

import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

public class PasswordScannerTest {
    File resourcesPath = new File("src/main/resources");

    @Before
    public void setUp() { assertTrue("Resource directory does not exist.", resourcesPath.exists());
    }
    @Test
    public void testGetLocation() throws Exception {
        PasswordScanner passwordScanner = new PasswordScanner();
        List<String> locations = new ArrayList<String>();
        locations.add("src/main/resources");
        locations.add("testDirectory");
        passwordScanner.addLocation("testDirectory");
        assertTrue(locations.equals(passwordScanner.getLocations()));
    }

    @Test
    public void scanForPasswords() throws PasswordFoundException {
        PasswordScanner passwordScanner = new PasswordScanner();
        passwordScanner.init();
    }

    private File createFile(String pwdValue) throws Exception {
        File file = File.createTempFile("temp", ".properties");
        file.deleteOnExit();
        try (FileWriter w = new FileWriter(file)) {
            w.write("password=" + pwdValue);
        }
        return file;
    }

    @Test
    public void testCheckForPassWordsFound() throws Exception {
        // Create a file that has a password-like expression in it.
        File file = createFile("hello");
        assertTrue(file.exists());

        PasswordScanner passwordScanner = new PasswordScanner();
        passwordScanner.addLocation(file.getParent());
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
        File file = createFile("");
        assertTrue(file.exists());
        PasswordScanner passwordScanner = new PasswordScanner();
        passwordScanner.addLocation(file.getParent());
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
        File file1 = createFile("pwd1");
        File file2 = createFile("pwd2");
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertThat(file1.getParent(), equalTo(file2.getParent()));
        PasswordScanner passwordScanner = new PasswordScanner();
        passwordScanner.addLocation(file1.getParent());
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

    @Test
    public void testSetLocations() throws Exception {
        PasswordScanner passwordScanner = new PasswordScanner();
        List<String> locations = new ArrayList<String>();

        passwordScanner.setLocations(null);
        assertThat(passwordScanner.getLocations(), equalTo(locations));

        passwordScanner.addLocation(null);
        assertThat(passwordScanner.getLocations(), equalTo(locations));

        locations.add("src/main/resources");
        locations.add("testDirectory");
        passwordScanner.setLocations(locations);
        assertThat(passwordScanner.getLocations(), equalTo(locations));

    }
}

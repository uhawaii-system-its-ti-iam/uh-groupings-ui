package edu.hawaii.its.groupings.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import edu.hawaii.its.groupings.exceptions.PatternFoundException;

import javax.validation.constraints.Null;

public class CheckForPatternTest {
    File resourcesPath = new File("src/main/resources");

    @Before
    public void setUp() {
        assertTrue("Resource directory does not exist.", resourcesPath.exists());
    }

    private File createFile(String pwdValue) throws Exception {
        File file = File.createTempFile("temp", ".properties", resourcesPath);
        file.deleteOnExit();
        try (FileWriter w = new FileWriter(file)) {
            w.write("password=" + pwdValue);
        }
        return file;
    }

    @Test
    public void testCheckForPatternFound() throws Exception {
        File file = createFile("hello");
        assertTrue(file.exists());

        CheckForPattern checkForPattern = new CheckForPattern();

        String pattern = "^.*password.*\\=(?!\\s*$).+";
        List<String> fileLocations = checkForPattern.fileLocations(".properties", resourcesPath.toString(), pattern);
        assertTrue(fileLocations.size() > 0);
        for (String f : fileLocations) {
            assertThat(f, containsString(file.getPath() + " on line: 1"));
        }
        file.delete();
    }

    @Test
    public void testCheckForPatternNotFound() throws Exception {
        File file = createFile("");
        assertTrue(file.exists());

        CheckForPattern checkForPattern = new CheckForPattern();

        String pattern = "^.*password.*\\=(?!\\s*$).+";
        List<String> fileLocations = checkForPattern.fileLocations(".properties", resourcesPath.toString(), pattern);
        assertTrue(fileLocations.size() == 0);
        file.delete();
    }

    @Test
    public void testCheckForMultiplePatternsFound() throws Exception {
        File file1 = createFile("pwd1");
        File file2 = createFile("pwd2");
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertThat(file1.getParent(), equalTo(file2.getParent()));

        CheckForPattern checkForPattern = new CheckForPattern();

        String pattern = "^.*password.*\\=(?!\\s*$).+";
        List<String> fileLocations = checkForPattern.fileLocations(".properties", resourcesPath.toString(), pattern);
        assertTrue(fileLocations.size() > 0);
        for (String f : fileLocations) {
            assertThat(f, containsString( " on line: 1"));
        }
        file1.delete();
        file2.delete();
    }

    @Test
    public void testExceptionThrown() throws Exception {
        CheckForPattern checkForPattern = new CheckForPattern();
        String pattern = "^.*password.*\\=(?!\\s*$).+";
        try {
            List<String> fileLocations = checkForPattern.fileLocations(".properties", null, pattern);
        }
        catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }

    @Test
    public void testExceptionThrown2() throws Exception {
        CheckForPattern checkForPattern = new CheckForPattern();
        String pattern = "^.*password.*\\=(?!\\s*$).+";
        List<String> dummy = new ArrayList<>();
        try {
            List<String> fileLocations = checkForPattern.fileLocations(".properties", "madeUpDirectory", pattern);
            assertThat(fileLocations, equalTo(dummy));
        }
        catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }
}

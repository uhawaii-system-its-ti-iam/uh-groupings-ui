package edu.hawaii.its.groupings.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

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
    public void testCheckForPassWordsFound() throws Exception {
        // Create a file that has a password-like expression in it.
        File file = createFile("hello");
        assertTrue(file.exists());

        CheckForPattern checkForPattern = new CheckForPattern();

        String pattern = "^.*password.*\\=(?!\\s*$).+";
        List<String> fileLocations = checkForPattern.fileLocations(".properties", resourcesPath.toString(), pattern);
        assertTrue(file.exists());
        assertTrue(fileLocations.size() > 0);
        for (String f : fileLocations) {
            assertThat(f, containsString(file.getPath() + " on line: 1"));
        }
        file.delete();
    }
}

package edu.hawaii.its.groupings.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class PasswordScannerTest {
    File resourcesPath = new File("src/main/resources");

    //    @Autowired
    private PasswordScanner passwordScanner;

    @Before
    public void setUp() {
        assertTrue("Resource directory does not exist.", resourcesPath.exists());
        passwordScanner = new PasswordScanner();
    }

    public File createFile(String pwdValue) throws Exception {
        File file = File.createTempFile("temp", ".properties");
        file.deleteOnExit();
        try (FileWriter w = new FileWriter(file)) {
            w.write("password=" + pwdValue);
        }
        return file;
    }

    @Test
    public void testMe() throws Exception {
        assertThat(passwordScanner, not(equalTo(null)));
    }

    @Test
    public void testCheckForPasswordsFound() throws Exception {
        File file = createFile("hello");
        assertTrue(file.exists());
        passwordScanner.addLocation(file.getParent());
        try {
            passwordScanner.init();
            fail("Should have found password in temp file.");
        }
        catch (Exception e) {
            assertThat(e, instanceOf(PasswordFoundException.class));
            assertThat(e.toString(),
                    containsString(file.getPath() + " on line: 1"));
            e.getMessage();
        }
        file.delete();
    }

    @Test
    public void testCheckForPasswordsNotFound() throws Exception {
        File file = createFile("");
        assertTrue(file.exists());
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

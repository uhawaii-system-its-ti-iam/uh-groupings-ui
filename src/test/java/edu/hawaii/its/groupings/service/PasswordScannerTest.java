package edu.hawaii.its.groupings.service;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedWriter;
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
        FileWriter fw1 = new FileWriter(file);
        BufferedWriter bw1 = new BufferedWriter(fw1);
        bw1.write("password=" + pwdValue);
        bw1.close();
        fw1.close();
        return file;
    }
    @Test
    public void testCheckForPassWordsFound() throws Exception {
        PasswordScanner passwordScanner = new PasswordScanner();
        File file = createFile(dirname, "hello");
        assertTrue(file.exists());
        try {
            passwordScanner.init();
            fail("Should not reach here.");
        } catch (Exception e) {
            assertThat(e, instanceOf(PasswordFoundException.class));
        }
        file.delete();
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
        }
        file.delete();
    }
}

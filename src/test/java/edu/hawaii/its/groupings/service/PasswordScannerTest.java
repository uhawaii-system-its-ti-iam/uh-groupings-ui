package edu.hawaii.its.groupings.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class PasswordScannerTest {

    @Autowired
    private PasswordScanner passwordScanner;

    @Test
    public void construction() {
        assertNotNull(passwordScanner);
    }

    @Test
    public void testTwoPatternDiffFile() {
        String dirname = "src/test/resources/pattern-property-checker/test2";
        passwordScanner.setDirname(dirname);
        assertThrows(PasswordFoundException.class,
                () -> passwordScanner.init());
    }

}

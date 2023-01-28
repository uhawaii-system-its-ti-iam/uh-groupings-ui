package edu.hawaii.its.groupings.service;

import org.junit.jupiter.api.Test;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

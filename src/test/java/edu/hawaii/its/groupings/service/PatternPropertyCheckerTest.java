package edu.hawaii.its.groupings.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class PatternPropertyCheckerTest {

    private PatternPropertyChecker patternPropertyChecker;
    private final Path resourceDir = Paths.get("src", "test", "resources", "pattern-property-checker");
    private final String dirname = resourceDir.toString();

    @BeforeEach
    public void setUp() {
        patternPropertyChecker = new PatternPropertyChecker();
    }

    @Test
    public void testNullParam() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(null, null);
        assertEquals(0, fileLocations.size());
    }

    @Test
    public void testNoPattern() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname + "/test2", ".txt");
        assertEquals(0, fileLocations.size());
    }

    @Test
    public void testNoFile() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".properties");
        assertEquals(0, fileLocations.size());
    }

    @Test
    public void testEmptyFile() {
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".txt");
        assertEquals(0, fileLocations.size());
    }

    @Test
    public void testOnePatternFound() {
        String dirname = Paths.get(resourceDir.toString(), "test1").toString();
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".properties");
        assertEquals(1, fileLocations.size());
        Path path = Paths.get(resourceDir.toString(), "test1", "PatternPropertyCheckerTestFile.properties");
        assertThat(fileLocations.get(0), endsWith(path + " on line: 2"));
    }

    @Test
    public void testTwoPatternSameFile() {
        String dirname = Paths.get(resourceDir.toString(), "test2").toString();
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".properties");
        assertEquals(2, fileLocations.size());
        Path path = Paths.get(resourceDir.toString(), "test2", "PatternPropertyCheckerTestFile2.properties");
        assertThat(fileLocations.get(0), endsWith(path + " on line: 2"));
        assertThat(fileLocations.get(1), endsWith(path + " on line: 5"));
    }

    @Test
    public void testTwoPatternDiffFile() {
        String dirname = Paths.get(resourceDir.toString(), "test1").toString();
        List<String> fileLocations = patternPropertyChecker.getPatternLocation(dirname, ".txt");
        assertEquals(2, fileLocations.size());
        Path path1 = Paths.get(resourceDir.toString(), "test1", "PatternPropertyCheckerTwoPatterns.txt");
        Path path2 = Paths.get(resourceDir.toString(), "test1", "PatternPropertyCheckerTwoPatterns2.txt");

        assertThat(fileLocations.get(0), endsWith(path1 + " on line: 1"));
        assertThat(fileLocations.get(1), endsWith(path2 + " on line: 3"));
    }

    @Test
    public void testBadDirectory() {
        List<String> results = patternPropertyChecker.getPatternLocation(null, ".txt");
        assertEquals(0, results.size());

        results = patternPropertyChecker.getPatternLocation("_no_way_", ".txt");
        assertEquals(0, results.size());
    }

    @Test
    public void testBadExtension() {
        List<String> results = patternPropertyChecker.getPatternLocation(dirname + "/test1", null);
        assertEquals(0, results.size());
    }

}

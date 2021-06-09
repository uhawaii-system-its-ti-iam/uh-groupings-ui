package edu.hawaii.its.groupings.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckForPattern {

    private static final Log logger = LogFactory.getLog(CheckForPattern.class);

    /**
     * checkPattern: checks a file(s) and given file naming convention(.properties, .java, .pom)
     * for a pattern.
     *
     * @param fileExtension
     *            The file type(.java, .properties, .pom, etc).
     * @param folderLocation
     *            The folder location(/src/main/resources).
     * @param pattern
     *            The string pattern to look for in the source code.
     *
     * @return A list of strings containing locations of the found patterns.
     */
    public List<String> fileLocations(String fileExtension, String folderLocation, String pattern) {

        logger.info("fileLocations;  fileExtension: " + fileExtension);
        logger.info("fileLocations; folderLocation: " + folderLocation);
        logger.info("fileLocations;        pattern: " + pattern);

        List<String> patternLocation = new ArrayList<>();

        try {
            File dir = new File(folderLocation);

            File[] fileResources = dir.listFiles((dir1, name) -> name.endsWith(fileExtension));
            if (fileResources != null) {
                Pattern pat = Pattern.compile(pattern);
                Matcher matcher;

                for (File fr : fileResources) {
                    logger.info("fileLocations; scan file: " + fr);

                    int lineId = 0;
                    List<Integer> lineNumbers = new ArrayList<>();

                    try (Scanner fileScanner = new Scanner(fr)) {
                        while (fileScanner.hasNextLine()) {
                            String line = fileScanner.nextLine();
                            lineId++;

                            matcher = pat.matcher(line);

                            if (matcher.find()) {
                                lineNumbers.add(lineId);
                            }
                        }

                        if (!lineNumbers.isEmpty()) {
                            for (int li : lineNumbers) {
                                patternLocation.add(fr.toString() + " on line: " + li);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }

        return patternLocation;
    }
}

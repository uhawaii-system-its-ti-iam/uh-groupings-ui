package edu.hawaii.its.groupings.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class CheckForPattern {

    /**
     * checkPattern: checks a file(s) and given file naming convention(.properties, .java, .pom) for a pattern.
     *
     * @param fileExtension The file type(.java, .properties, .pom, etc).
     * @param folderLocation The folder location(/src/main/resources).
     * @param pattern The string pattern to look for in the source code.
     *
     * @return A list of strings containing locations of the found patterns.
     */
    public List<String> fileLocations(String fileExtension, String folderLocation, String pattern) throws IOException {

        List<Integer> lineNumbers = new ArrayList<>();
        List<String> patternLocation = new ArrayList<>();

        File dir = new File(folderLocation);
        File[] fileResources = dir.listFiles((dir1, name) -> name.endsWith(fileExtension));

        Pattern pat = Pattern.compile(pattern);
        Matcher matcher;

        for (File fr : fileResources) {
            int lineId = 0;

            try (Scanner fileScanner = new Scanner(fr)) {
                while (fileScanner.hasNextLine()) { String line = fileScanner.nextLine();
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

                lineNumbers.removeAll(lineNumbers);
            }
        }

        return patternLocation;
    }
}
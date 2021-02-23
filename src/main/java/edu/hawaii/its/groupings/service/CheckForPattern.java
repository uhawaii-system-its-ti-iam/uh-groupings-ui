
package edu.hawaii.its.groupings.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.groupings.controller.ErrorRestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class CheckForPattern {

    private static final Log logger = LogFactory.getLog(ErrorRestController.class);

    public ArrayList<String> checkPattern(String fileConvention, String folderLocation, String pattern) throws IOException {

        ArrayList<Integer> lineNumbers = new ArrayList<>();
        ArrayList<String> patternLocation = new ArrayList<>();

        File dir = new File(folderLocation);
        File[] fileResources = dir.listFiles((dir1, name) -> name.endsWith(fileConvention));

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

                    logger.info("\n------------------------------------------------------\n\n");
                    System.out.println("Pattern detected in file: " + fr + " in lines:");

                    for (int li : lineNumbers) {
                        logger.info("Line" + li);
                        patternLocation.add(fr.toString() + " on line: " + li);
                    }

                    logger.info("\n------------------------------------------------------\n\n");
                }


                lineNumbers.removeAll(lineNumbers);
            }
        }

        return patternLocation;
    }
}
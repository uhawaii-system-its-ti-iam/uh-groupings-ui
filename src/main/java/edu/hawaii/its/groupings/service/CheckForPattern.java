
package edu.hawaii.its.groupings.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class CheckForPattern {

    @PostConstruct
    private void check() throws IOException {
        boolean detected = false;
        File dir = new File("src/main/resources");
        File[] fileResources = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".properties");
            }
        });

        ArrayList<Integer> lineNumbers = new ArrayList<>();

        Pattern pattern = Pattern.compile("grouperClient.webService.password");
        Matcher matcher;

        for (File fr : fileResources) {
            int lineId = 0;
            Scanner fileScanner = new Scanner(fr);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lineId++;

                matcher = pattern.matcher(line);

                if (matcher.find()) {
                    lineNumbers.add(lineId);
                }
            }

            if (!lineNumbers.isEmpty()) {
                detected = true;

                System.out.print("\n------------------------------------------------------\n\n");
                System.out.println("Pattern detected in file: " + fr + " in lines:");

                for (int li : lineNumbers) {
                    System.out.println("Line" + li);
                }

                System.out.print("\n------------------------------------------------------\n\n");
            }
            lineNumbers.removeAll(lineNumbers);
        }
    }
}
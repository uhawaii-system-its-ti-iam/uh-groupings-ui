package edu.hawaii.its.groupings.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

@Service
public class PasswordScanner {

    private static final Log logger = LogFactory.getLog(PasswordScanner.class);
    private final String pattern = "^.*password.*\\=(?!\\s*$).+"; //might put in custom.properties

//    @Value("#{'${pwd.scanner.locations}'.split(',')}")
    private List<String> locations = new ArrayList<>();

    //For some reason, this Boolean enabled is being changed to null when running the test, so I manually set this to true for now.
//    @Value("${pwd.scanner.enabled}")
    private Boolean enabled = true;

    @PostConstruct
    public void init() throws PasswordFoundException {
        logger.info("init; starting...");
        try {
            String path = "src/main/resources";
            File file = new File(path);
            String absolutePath = file.getAbsolutePath();
            locations.add(absolutePath);
            System.out.println("---------------------------------------");
            System.out.println("LOCATION: " + locations);
            if (enabled) {
                checkForPasswords();
            }
            logger.info("init; check for passwords finished.");
            logger.info("init; started.");
        } catch (PasswordFoundException pfe) {
            throw pfe;
        }
    }

    private void checkForPasswords() throws PasswordFoundException {

        CheckForPattern checkForPattern = new CheckForPattern();

        String patternResult = "";
        for (String location : locations) {
            List<String> fileLocations = checkForPattern.fileLocations(".properties", location, pattern);
            for (String list : fileLocations) {
                patternResult += "\n" + list;
            }
        }

        if (patternResult.length() > 0) {
            throw new PasswordFoundException(patternResult);
        }
    }

    public List<String> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    public void setLocations(List<String> locations) {
        this.locations = locations != null ? locations : Collections.emptyList();
    }

    public void addLocation(String location) {
        if (location != null) {
            this.locations.add(location);
        }
    }
}

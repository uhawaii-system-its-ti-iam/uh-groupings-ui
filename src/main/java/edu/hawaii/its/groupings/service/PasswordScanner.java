package edu.hawaii.its.groupings.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

@Service
public class PasswordScanner {

    private static final Log logger = LogFactory.getLog(PasswordScanner.class);
    private String dirname = "src/main/resources";

    @PostConstruct
    public void init() throws PasswordFoundException {
        logger.info("init; starting...");
        checkForPasswords();
        logger.info("init; check for passwords finished.");
        logger.info("init; started.");
    }

    private void checkForPasswords() throws PasswordFoundException {
        CheckForPattern checkForPattern = new CheckForPattern();

        String patternResult = "";
        String pattern = "^.*password.*\\=(?!\\s*$).+";
        List<String> fileLocations = checkForPattern.fileLocations(".properties", getDirname(), pattern);
        if (fileLocations != null && !fileLocations.isEmpty()) {
            for (String list : fileLocations) {
                patternResult += "\n" + list;
            }
        }

        if (patternResult.length() > 0) {
            throw new PasswordFoundException(patternResult);
        }
    }

    public String getDirname() {
        return dirname;
    }

    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

}

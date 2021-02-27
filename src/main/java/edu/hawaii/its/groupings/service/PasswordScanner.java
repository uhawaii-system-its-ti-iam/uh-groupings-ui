package edu.hawaii.its.groupings.service;

import edu.hawaii.its.exceptions.PasswordFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.groupings.controller.ErrorRestController;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.List;

@Service
public class PasswordScanner {

    private static final Log logger = LogFactory.getLog(ErrorRestController.class);

    @PostConstruct
    public void init() throws IOException, PasswordFoundException {

        CheckForPattern checkForPattern = new CheckForPattern();

        List<String> result = checkForPattern.fileLocations(".properties", "src/main/resources", "hello");

        String patternResult = "";

        logger.info("PasswordScanner init");

        if (!result.isEmpty()) {

            for (String list: result) {
                patternResult += "\n" + list;
            }

            throw new PasswordFoundException(patternResult);
        }
    }
}

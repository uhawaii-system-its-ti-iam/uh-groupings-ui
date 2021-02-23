package edu.hawaii.its.groupings.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.groupings.controller.ErrorRestController;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class PasswordScanner {

    CheckForPattern checkForPattern = new CheckForPattern();

    private static final Log logger = LogFactory.getLog(ErrorRestController.class);

    @PostConstruct
    public void dummyScan() throws IOException {

        ArrayList<String> result;

        result = checkForPattern.checkPattern(".properties", "src/main/resources", "hello");

        logger.info(result);
    }
}

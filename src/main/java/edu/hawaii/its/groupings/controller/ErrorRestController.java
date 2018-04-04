package edu.hawaii.its.groupings.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;

import edu.hawaii.its.groupings.type.Feedback;

@RestController
public class ErrorRestController {

    private static final Log logger = LogFactory.getLog(ErrorRestController.class);

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback/error")
    public ResponseEntity<> feedbackError(@RequestBody Map<String, String> body, HttpSession session) {
        logger.info("Entered feedback error...");
        String exceptionError = body.get("exceptionError");
        session.setAttribute("feedback", new Feedback(exceptionError));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package edu.hawaii.its.groupings.controller;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.groupings.exceptions.ExceptionForTesting;
import edu.hawaii.its.groupings.type.Feedback;

@RestController
public class ErrorRestController {
    private static final Log logger = LogFactory.getLog(ErrorRestController.class);
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback/error")
    public ResponseEntity<Void> feedbackError(@RequestBody Map<String, String> body, HttpSession session) {
        logger.info("Entered feedback error...");
        String exceptionMessage = body.get("exceptionMessage");
        session.setAttribute("feedback", new Feedback(exceptionMessage));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/testing/exception")
    public ResponseEntity<String> throwException() {
        logger.info("Entered REST throwException...");
        throw new ExceptionForTesting("Exception thrown intentionally");
    }
}

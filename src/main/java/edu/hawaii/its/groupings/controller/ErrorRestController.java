package edu.hawaii.its.groupings.controller;

import edu.hawaii.its.groupings.type.Feedback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

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
}

package edu.hawaii.its.groupings.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ui.Model;

import jakarta.mail.MessagingException;
import java.io.IOException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Log logger = LogFactory.getLog(ErrorControllerAdvice.class);
    private final UserContextService userContextService;
    private final EmailService emailService;

    public ErrorControllerAdvice(UserContextService userContextService, EmailService emailService) {
        this.userContextService = userContextService;
        this.emailService = emailService;
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientResponseException(WebClientResponseException wcre) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = attributes.getRequest().getRequestURI();

        log(wcre);

        emailService.sendWithStack(wcre, "Web Client Response Exception", path);

        Map<String, Object> body = new HashMap<>();

        body.put("status", wcre.getStatusCode());
        body.put("message", "Web Client Response Exception");
        body.put("path", path);
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, wcre.getStatusCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleIllegalArgumentException(IllegalArgumentException iae, Model model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = attributes.getRequest().getRequestURI();

        log(iae);

        emailService.sendWithStack(iae, "Illegal Argument Exception", path);

        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("message", "Resource not available");
        model.addAttribute("path", path);
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error";
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleMessagingException(MessagingException me, Model model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = attributes.getRequest().getRequestURI();

        log(me);

        emailService.sendWithStack(me, "Messaging Exception", path);

        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("message", "Mail service exception");
        model.addAttribute("path", path);
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error";
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleIOException(IOException ioe, Model model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = attributes.getRequest().getRequestURI();

        log(ioe);

        emailService.sendWithStack(ioe, "IO Exception", path);

        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("message", "IO exception");
        model.addAttribute("path", path);
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error";
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public String handleUnsupportedOperationException(UnsupportedOperationException ex, Model model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = attributes.getRequest().getRequestURI();

        log(ex);

        emailService.sendWithStack(ex, "Unsupported Operation Exception", path);

        model.addAttribute("status", HttpStatus.NOT_IMPLEMENTED);
        model.addAttribute("message", "Method not implemented");
        model.addAttribute("path", path);
        model.addAttribute("timestamp", LocalDateTime.now());

        return "error";
    }

    /**
     * Helper function to log exception cause and user involved.
     * @param cause Exception object.
     */
    public void log(Throwable cause) {

        String uid = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        logger.error("uid: " + uid + "; Exception: ", cause);
    }
}

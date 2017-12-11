package edu.hawaii.its.holiday.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.holiday.access.User;
import edu.hawaii.its.holiday.access.UserContextService;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Log logger = LogFactory.getLog(HomeController.class);

    @Autowired
    private UserContextService userContextService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<IllegalArgumentException> handelIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }
        IllegalArgumentException e = new IllegalArgumentException("http status: " + 404, iae.getCause());
        e.setStackTrace(iae.getStackTrace());

        logger.error("username: " + username + "; Exception: ", e);
        System.out.println("username: " + username + "; Exception: " + e.getStackTrace());

        return ResponseEntity.status(404).body(e);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Exception> handelRuntimeException(RuntimeException re) {
        return error(re, 500);
    }


    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<Exception> handelNotImplementedException(NotImplementedException nie) {
        return error(nie, 501);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Exception> handleException(Exception exception) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }
        Exception e = new Exception("http status: " + 500 + "\n" + exception.getMessage(), exception.getCause());
        e.setStackTrace(exception.getStackTrace());

        logger.error("username: " + username + "; Exception: ", e.getCause());
        System.out.println("username: " + username + "; Exception: " + e.getStackTrace());

        return ResponseEntity.status(500).body(e);
    }

    //todo this is for the HolidayRestControllerTest test (should we really have this behavior?)
    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(Exception ex) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }
        logger.error("username: " + username + "; Exception: ", ex);
        System.out.println("username: " + username + "; Exception: " + ex.getStackTrace());

        return "redirect:/error";
    }

    private ResponseEntity<Exception> error(Exception exception, int status) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }

        Exception e = new Exception("http status: " + status + "\n" + exception.getMessage(), exception.getCause());
        e.setStackTrace(exception.getStackTrace());

        logger.error("username: " + username + "; Exception: ", e.getCause());
        System.out.println("username: " + username + "; Status: " + status + "; Exception: " + e.getStackTrace());

        return ResponseEntity.status(status).body(e);
    }
}
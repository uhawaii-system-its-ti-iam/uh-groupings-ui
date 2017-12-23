package edu.hawaii.its.holiday.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.api.type.GroupingsHTTPException;
import edu.hawaii.its.holiday.access.User;
import edu.hawaii.its.holiday.access.UserContextService;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.ws.http.HTTPException;

@ControllerAdvice
public class ErrorControllerAdvice {


    //todo change this to HATEOAS style
    //  do we need to use @ResponseBody for the methods or is this already being handled?
    //  do we need to use @ResponseStatus for the methods or is the HTTP status already being added?
    //  should we use VndErrors() type and get rid of the Groupings specific error types?

    private static final Log logger = LogFactory.getLog(HomeController.class);

    @Autowired
    private UserContextService userContextService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GroupingsHTTPException> handelIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        return error("Resource not available", iae, 404);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GroupingsHTTPException> handelRuntimeException(RuntimeException re) {
        return error("runtime exception", re, 500);
    }


    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<GroupingsHTTPException> handelNotImplementedException(NotImplementedException nie) {
        return error("Method not implemented", nie, 501);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GroupingsHTTPException> handleException(Exception exception) {
        return error("Exception", exception, 500);
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

    private ResponseEntity<GroupingsHTTPException> error(String message, Throwable cause, int status) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }

        GroupingsHTTPException httpException = new GroupingsHTTPException(message, cause, status);
        httpException.fillInStackTrace();

        logger.error("username: " + username + "; Exception: ", httpException.getCause());

        return ResponseEntity.status(status).body(httpException);
    }
}
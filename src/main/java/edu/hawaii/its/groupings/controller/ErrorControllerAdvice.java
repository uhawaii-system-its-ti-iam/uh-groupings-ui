package edu.hawaii.its.groupings.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import edu.hawaii.its.api.type.GroupingsHTTPException;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;

@ControllerAdvice
public class ErrorControllerAdvice {

    // TODO: change this to HATEOAS style
    //  Do we need to use @ResponseBody for the methods or is this already being handled?
    //  Do we need to use @ResponseStatus for the methods or is the HTTP status already being added?
    //  Should we use VndErrors() type and get rid of the Groupings specific error types?

    private static final Log logger = LogFactory.getLog(ErrorControllerAdvice.class);

    @Autowired
    private UserContextService userContextService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GroupingsHTTPException> handleIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        return exceptionResponse("Resource not available", iae, 404);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GroupingsHTTPException> handleRuntimeException(RuntimeException re) {
        return exceptionResponse("runtime exception", re, 500);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<GroupingsHTTPException> handleUnsupportedOperationException(UnsupportedOperationException nie) {
        return exceptionResponse("Method not implemented", nie, 501);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GroupingsHTTPException> handleException(Exception exception) {
        return exceptionResponse("Exception", exception, 500);
    }

    @ExceptionHandler(GroupingsServiceResultException.class)
    public ResponseEntity<GroupingsHTTPException> handleGroupingsServiceResultException(GroupingsServiceResultException gsre) {
        ResponseEntity<GroupingsHTTPException> re = exceptionResponse("Groupings Service resulted in FAILURE", gsre, 400);
        return re;
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

        return "redirect:/error";
    }

    private ResponseEntity<GroupingsHTTPException> exceptionResponse(String message, Throwable cause, int status) {
        String username = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            username = user.getUsername();
        }

        GroupingsHTTPException httpException = new GroupingsHTTPException(message, cause, status);

        logger.error("username: " + username + "; Exception: ", httpException.getCause());

        return ResponseEntity.status(status).body(httpException);
    }
}
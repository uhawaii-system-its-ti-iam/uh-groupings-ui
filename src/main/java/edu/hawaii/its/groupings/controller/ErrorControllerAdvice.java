package edu.hawaii.its.groupings.controller;

import java.io.IOException;

import jakarta.mail.MessagingException;

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
import edu.hawaii.its.groupings.service.EmailService;

import edu.internet2.middleware.grouperClient.ws.GcWebServiceError;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Log logger = LogFactory.getLog(ErrorControllerAdvice.class);

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private EmailService emailService;

    @ExceptionHandler(GroupingsServiceResultException.class)
    public ResponseEntity<GroupingsHTTPException> handleGroupingsServiceResultException(GroupingsServiceResultException gsre) {
        emailService.sendWithStack(gsre, "Groupings Service Result Exception");
      return exceptionResponse("Groupings Service resulted in FAILURE", gsre, 400);
    }

    @ExceptionHandler (GcWebServiceError.class)
    public ResponseEntity<GroupingsHTTPException> handleGcWebServiceError(GcWebServiceError gce) {
        emailService.sendWithStack(gce, "Gc Web Service Error");
        return exceptionResponse(gce.getMessage(), gce, 404);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GroupingsHTTPException> handleIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        emailService.sendWithStack(iae, "Illegal Argument Exception");
        return exceptionResponse("Resource not available", iae, 404);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GroupingsHTTPException> handleException(Exception exception) {
        emailService.sendWithStack(exception, "Exception");
        return exceptionResponse("Exception", exception, 500);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GroupingsHTTPException> handleRuntimeException(Exception exception) {
        emailService.sendWithStack(exception, "Runtime Exception");
      return exceptionResponse("Runtime Exception", exception, 500);
    }

    @ExceptionHandler({MessagingException.class, IOException.class})
    public ResponseEntity<GroupingsHTTPException> handleMessagingException(Exception e) {
        emailService.sendWithStack(e, "Messaging Exception");
      return exceptionResponse("Mail service exception", e, 500);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<GroupingsHTTPException> handleUnsupportedOperationException(UnsupportedOperationException nie) {
        emailService.sendWithStack(nie, "Unsupported Operation Exception");
      return exceptionResponse("Method not implemented", nie, 501);
    }

    //todo this is for the HolidayRestControllerTest test (should we really have this behavior?)
    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(Exception ex) {
        String uid = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        logger.error("uid: " + uid + "; Exception: ", ex);
        emailService.sendWithStack(ex, "TypeMismatchException");
        return "redirect:/error";
    }

    private ResponseEntity<GroupingsHTTPException> exceptionResponse(String message, Throwable cause, int status) {
        String uid = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        GroupingsHTTPException httpException = new GroupingsHTTPException(message, cause, status);

        logger.error("uid: " + uid + "; Exception: ", httpException.getCause());
        return ResponseEntity.status(status).body(httpException);
    }
}

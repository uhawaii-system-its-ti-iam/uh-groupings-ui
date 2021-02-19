package edu.hawaii.its.groupings.controller;

import edu.hawaii.its.api.type.GroupingsHTTPException;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;
import edu.hawaii.its.groupings.type.Feedback;

import edu.internet2.middleware.grouperClient.ws.GcWebServiceError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.mail.MessagingException;
import java.io.IOException;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Log logger = LogFactory.getLog(ErrorControllerAdvice.class);

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private EmailService emailService;

    @ExceptionHandler(GroupingsServiceResultException.class)
    public ResponseEntity<GroupingsHTTPException> handleGroupingsServiceResultException(GroupingsServiceResultException gsre) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, gsre, "GroupingsServiceResultException");
      return exceptionResponse("Groupings Service resulted in FAILURE", gsre, 400);
    }

    @ExceptionHandler (GcWebServiceError.class)
    public ResponseEntity<GroupingsHTTPException> handleGcWebServiceError(GcWebServiceError gce) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, gce, "GcWebServiceError");
        return exceptionResponse(gce.getMessage(), gce, 404);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GroupingsHTTPException> handleIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, iae, "IllegalArgumentException");
        return exceptionResponse("Resource not available", iae, 404);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GroupingsHTTPException> handleException(Exception exception) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, exception, "Exception");
        return exceptionResponse("Exception", exception, 500);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GroupingsHTTPException> handleRuntimeException(Exception exception) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, exception, "RuntimeException");
      return exceptionResponse("Runtime Exception", exception, 500);
    }

    @ExceptionHandler({MessagingException.class, IOException.class})
    public ResponseEntity<GroupingsHTTPException> handleMessagingException(Exception e) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, e, "MessagingException");
      return exceptionResponse("Mail service exception", e, 500);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<GroupingsHTTPException> handleUnsupportedOperationException(UnsupportedOperationException nie) {
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, nie, "UnsupportedOperationException");
      return exceptionResponse("Method not implemented", nie, 501);
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
        Feedback errorFeedback = new Feedback();
        emailService.sendWithStack(errorFeedback, ex, "TypeMismatchException");
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
        Feedback errorFeedback = new Feedback();
        return ResponseEntity.status(status).body(httpException);
    }
}

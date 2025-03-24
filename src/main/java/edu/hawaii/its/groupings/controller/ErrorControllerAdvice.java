package edu.hawaii.its.groupings.controller;

import java.io.IOException;

import jakarta.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;
import edu.hawaii.its.api.type.ApiError;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Log logger = LogFactory.getLog(ErrorControllerAdvice.class);
    private final UserContextService userContextService;
    private final EmailService emailService;

    public ErrorControllerAdvice(UserContextService userContextService, EmailService emailService) {
        this.userContextService = userContextService;
        this.emailService = emailService;
    }

    public ResponseEntity<ApiError> buildResponseEntity(ApiError apiError, Throwable cause) {

        String uid = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        logger.error("uid: " + uid + "; Exception: ", cause);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleWebClientResponseException(WebClientResponseException wcre) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        emailService.sendWithStack(wcre, "Web Client Response Exception");
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status((HttpStatus) wcre.getStatusCode())
                .message("Web Client Response Exception")
                .stackTrace(ExceptionUtils.getStackTrace(wcre))
                .resultCode("FAILURE")
                .path(attributes.getRequest().getRequestURI());


        ApiError apiError = errorBuilder.build();
        return buildResponseEntity(apiError, wcre);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        String path = request.getDescription(false);


        emailService.sendWithStack(iae, "Illegal Argument Exception");
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.NOT_FOUND)
                .message("Resource not available")
                .stackTrace(ExceptionUtils.getStackTrace(iae))
                .resultCode("FAILURE")
                .path(path);
        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, iae);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        emailService.sendWithStack(e, "Exception");
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Exception")
                .stackTrace(ExceptionUtils.getStackTrace(e))
                .resultCode("FAILURE")
                .path(attributes.getRequest().getRequestURI());

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, e);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(Exception re) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        emailService.sendWithStack(re, "Runtime Exception");
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Runtime Exception")
                .stackTrace(ExceptionUtils.getStackTrace(re))
                .resultCode("FAILURE")
                .path(attributes.getRequest().getRequestURI());

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, re);
    }

    @ExceptionHandler({MessagingException.class, IOException.class})
    public ResponseEntity<ApiError> handleMessagingException(Exception me) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        emailService.sendWithStack(me, "Messaging Exception");
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Mail service exception")
                .stackTrace(ExceptionUtils.getStackTrace(me))
                .resultCode("FAILURE")
                .path(attributes.getRequest().getRequestURI());

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, me);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiError> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        emailService.sendWithStack(ex, "Unsupported Operation Exception");
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.NOT_IMPLEMENTED)
                .message("Method not implemented")
                .stackTrace(ExceptionUtils.getStackTrace(ex))
                .resultCode("FAILURE")
                .path(attributes.getRequest().getRequestURI());;

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, ex);
    }
}

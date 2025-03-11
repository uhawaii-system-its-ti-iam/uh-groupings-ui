package edu.hawaii.its.groupings.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.GcWebServiceError;
import jakarta.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;
import edu.hawaii.its.api.type.ApiError;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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
    public ResponseEntity<ApiError> handleWebClientResponseException
            (WebClientResponseException wcre, WebRequest request) {
        String path = request.getDescription(false);
        emailService.sendWithStack(wcre, "Web Client Response Exception", path);
        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status((HttpStatus) wcre.getStatusCode())
                .message("Web Client Response Exception")
                .debugMessage(wcre.getMessage())
                .timestamp(LocalDateTime.now());

        ApiError apiError = errorBuilder.build();
        return buildResponseEntity(apiError, wcre);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException iae, WebRequest request) {
        String path = request.getDescription(false);
        emailService.sendWithStack(iae, "Illegal Argument Exception", path);

        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.NOT_FOUND)
                .message("Resource not available")
                .debugMessage(iae.getMessage())
                .timestamp(LocalDateTime.now());

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, iae);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception exception, WebRequest request) {
        String path = request.getDescription(false);
        emailService.sendWithStack(exception, "Exception", path);

        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Exception")
                .debugMessage(exception.getMessage())
                .timestamp(LocalDateTime.now());

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, exception);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(Exception exception, WebRequest request) {
        String path = request.getDescription(false);
        emailService.sendWithStack(exception, "Runtime Exception", path);

        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Runtime Exception")
                .debugMessage(exception.getMessage())
                .timestamp(LocalDateTime.now());

        ApiError apiError = errorBuilder.build();
      return buildResponseEntity(apiError, exception);
    }

    @ExceptionHandler({MessagingException.class, IOException.class})
    public ResponseEntity<ApiError> handleMessagingException(Exception e, WebRequest request) {
        String path = request.getDescription(false);
        emailService.sendWithStack(e, "Messaging Exception", path);

        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Mail service exception")
                .debugMessage(e.getMessage())
                .timestamp(LocalDateTime.now());

        ApiError apiError = errorBuilder.build();
      return buildResponseEntity(apiError, e);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiError> handleUnsupportedOperationException(UnsupportedOperationException nie, WebRequest request) {
        String path = request.getDescription(false);
        emailService.sendWithStack(nie, "Unsupported Operation Exception", path);

        ApiError.Builder errorBuilder = new ApiError.Builder()
                .status(HttpStatus.NOT_IMPLEMENTED)
                .message("Method not implemented")
                .debugMessage(nie.getMessage())
                .timestamp(LocalDateTime.now());

        ApiError apiError = errorBuilder.build();

        return buildResponseEntity(apiError, nie);
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError, Throwable cause) {
        String uid = null;
        User user = userContextService.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        logger.error("uid: " + uid + "; Exception: ", cause);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}

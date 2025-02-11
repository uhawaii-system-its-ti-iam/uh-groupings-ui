package edu.hawaii.its.groupings.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.GcWebServiceError;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorControllerAdviceTest {

    @Autowired
    private ErrorControllerAdvice errorControllerAdvice;

    @Test
    public void nullTest() {
        assertNotNull(errorControllerAdvice);
    }

    @Test
    public void webClientResponseTest() {
        WebClientResponseException wcre = new WebClientResponseException(409, "CONFLICT", null, null, null);
        String statusCode = errorControllerAdvice.handleWebClientResponseException(wcre).getStatusCode().toString();
        assertThat(statusCode, is("409 CONFLICT"));
    }

    @Test
    public void illegalArgumentTest() {
        IllegalArgumentException iae = new IllegalArgumentException();
        String statusCode = errorControllerAdvice.handleIllegalArgumentException(iae, null).getStatusCode().toString();
        assertThat(statusCode, is("404 NOT_FOUND"));
    }

    @Test
    public void unsupportedOpTest() {
        UnsupportedOperationException uoe = new UnsupportedOperationException();
        String statusCode = errorControllerAdvice.handleUnsupportedOperationException(uoe).getStatusCode().toString();
        assertThat(statusCode, is("501 NOT_IMPLEMENTED"));
    }

    @Test
    public void runtimeExceptionTest() {
        RuntimeException re = new RuntimeException();
        String statusCode = errorControllerAdvice.handleException(re).getStatusCode().toString();
        assertThat(statusCode, is("500 INTERNAL_SERVER_ERROR"));
    }

    @Test
    public void exceptionTest() {
        Exception e = new Exception();
        String statusCode = errorControllerAdvice.handleException(e).getStatusCode().toString();
        assertThat(statusCode, is("500 INTERNAL_SERVER_ERROR"));
    }
}

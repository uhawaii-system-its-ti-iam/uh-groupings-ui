package edu.hawaii.its.groupings.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import jakarta.mail.MessagingException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import edu.hawaii.its.api.type.ApiError;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorControllerAdviceTest {

    @Autowired
    private ErrorControllerAdvice errorControllerAdvice;

    @MockitoBean
    private WebRequest webRequest;

    @Test
    public void nullTest() {
        assertNotNull(errorControllerAdvice);
    }

    @Test
    public void testWebClientResponse() {
        WebClientResponseException wcre = new WebClientResponseException(409, "CONFLICT", null, null, null);
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleWebClientResponseException(wcre);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
    }

    @Test
    public void testIllegalArgument() {
        IllegalArgumentException iae = new IllegalArgumentException();
        when(webRequest.getDescription(false)).thenReturn("/ui/test-illegal-arg-exception");
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleIllegalArgumentException(iae, webRequest);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void testException() {
        Exception e = new Exception();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleException(e);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testRuntimeException() {
        RuntimeException re = new RuntimeException();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleRuntimeException(re);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testMessagingException() {
        MessagingException me = new MessagingException();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleMessagingException(me);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testUnsupportedOp() {
        UnsupportedOperationException uoe = new UnsupportedOperationException();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleUnsupportedOperationException(uoe);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NOT_IMPLEMENTED));
    }
}

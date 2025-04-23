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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

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
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleWebClientResponseException(wcre, webRequest);
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
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleException(e, webRequest);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testRuntimeException() {
        RuntimeException re = new RuntimeException();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleRuntimeException(re,  webRequest);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testMessagingException() {
        MessagingException me = new MessagingException();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleMessagingException(me, webRequest);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testUnsupportedOp() {
        UnsupportedOperationException uoe = new UnsupportedOperationException();
        ResponseEntity<ApiError> responseEntity = errorControllerAdvice.handleUnsupportedOperationException(uoe, webRequest);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NOT_IMPLEMENTED));
    }

    @Test
    public void extractValidPathTest() throws Exception {

        java.lang.reflect.Method method = ErrorControllerAdvice.class.getDeclaredMethod("extractValidPath", WebRequest.class);
        method.setAccessible(true);


        MockHttpServletRequest validRequest = new MockHttpServletRequest();
        validRequest.setRequestURI("/uhgroupings/api/groups");
        WebRequest validWebRequest = new ServletWebRequest(validRequest);
        String validResult = (String) method.invoke(errorControllerAdvice, validWebRequest);
        assertThat(validResult, is("/uhgroupings/api/groups"));


        MockHttpServletRequest bogusRequest = new MockHttpServletRequest();
        bogusRequest.setRequestURI("/uhgroupings/bogus");
        WebRequest bogusWebRequest = new ServletWebRequest(bogusRequest);
        String bogusResult = (String) method.invoke(errorControllerAdvice, bogusWebRequest);
        assertThat(bogusResult, is((String) null));


        WebRequest nullWebRequest = new ServletWebRequest(new MockHttpServletRequest());
        String nullResult = (String) method.invoke(errorControllerAdvice, nullWebRequest);

        assertThat(nullResult, is(""));
    }
}

package edu.hawaii.its.groupings.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

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
    public void gcWebServiceTest() {
        Object obj = new Object();
        GcWebServiceError gc = new GcWebServiceError(obj);
        assertThat(errorControllerAdvice.handleGcWebServiceError(gc).getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void illegalArgumentTest() {
        IllegalArgumentException iae = new IllegalArgumentException();
        assertThat(errorControllerAdvice.handleIllegalArgumentException(iae, null).getStatusCode(),
                is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void unsupportedOpTest() {
        UnsupportedOperationException uoe = new UnsupportedOperationException();
        assertThat(errorControllerAdvice.handleUnsupportedOperationException(uoe).getStatusCode(),
                is(HttpStatus.NOT_IMPLEMENTED));
    }

    @Test
    public void runtimeExceptionTest() {
        RuntimeException re = new RuntimeException();
        assertThat(errorControllerAdvice.handleException(re).getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void exceptionTest() {
        Exception e = new Exception();
        assertThat(errorControllerAdvice.handleException(e).getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void exceptionHandleTest() throws GroupingsServiceResultException {
        GroupingsServiceResultException gsre = new GroupingsServiceResultException();
        assertThat(errorControllerAdvice.handleGroupingsServiceResultException(gsre).getStatusCode(),
                is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void typeMismatchTest() {
        Exception e = new Exception();
        assertThat(errorControllerAdvice.handleTypeMismatchException(e), is("redirect:/error"));
    }
}

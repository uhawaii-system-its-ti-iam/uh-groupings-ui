package edu.hawaii.its.groupings.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.service.EmailService;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class ErrorControllerAdviceTest {

    @Autowired
    private ErrorControllerAdvice errorControllerAdvice;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private UserContextService userContextService;

    @BeforeEach
    void setUp() {
        // Bind fake request so that the RequestContextHolder returns it
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/bad/request");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Stub out a logged-in user
        User mockUser = mock(User.class);
        when(mockUser.getUid()).thenReturn("winnie-pooh");
        when(userContextService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    public void nullTest() {
        assertNotNull(errorControllerAdvice);
    }

    @Test
    public void testIllegalArgumentException() {
        Model model = new ExtendedModelMap();

        IllegalArgumentException iae = new IllegalArgumentException("illegal argument exception");

        String view = errorControllerAdvice.handleIllegalArgumentException(iae, model);

        assertEquals("error", view, "should return the error view name");

        // verify model attributes
        assertEquals(HttpStatus.NOT_FOUND, model.getAttribute("status"));
        assertEquals("Resource not available", model.getAttribute("message"));
        assertEquals("/test/bad/request", model.getAttribute("path"));
        assertTrue(model.getAttribute("timestamp") instanceof LocalDateTime);

        verify(userContextService).getCurrentUser();
        verify(emailService).sendWithStack(iae, "Illegal Argument Exception", "/test/bad/request");
    }

    @Test
    public void testMessagingException() {
        Model model = new ExtendedModelMap();

        MessagingException me = new MessagingException("messaging exception");

        String view = errorControllerAdvice.handleMessagingException(me, model);

        assertEquals("error", view, "should return the error view name");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,  model.getAttribute("status"));
        assertEquals("Mail service exception",  model.getAttribute("message"));
        assertEquals("/test/bad/request",  model.getAttribute("path"));
        assertTrue(model.getAttribute("timestamp") instanceof LocalDateTime);

        verify(userContextService).getCurrentUser();
        verify(emailService).sendWithStack(me, "Messaging Exception", "/test/bad/request");
    }

    @Test
    public void testIOException() {
        Model model = new ExtendedModelMap();

        IOException ioe = new IOException("io exception");

        String view = errorControllerAdvice.handleIOException(ioe, model);

        assertEquals("error", view, "should return the error view name");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,  model.getAttribute("status"));
        assertEquals("IO exception",  model.getAttribute("message"));
        assertEquals("/test/bad/request",  model.getAttribute("path"));
        assertTrue(model.getAttribute("timestamp") instanceof LocalDateTime);

        verify(userContextService).getCurrentUser();
        verify(emailService).sendWithStack(ioe, "IO Exception", "/test/bad/request");
    }

    @Test
    public void testUnsupportedOperationException() {
        Model model = new ExtendedModelMap();

        UnsupportedOperationException uoe = new UnsupportedOperationException("unsupported operation exception");

        String view = errorControllerAdvice.handleUnsupportedOperationException(uoe, model);

        assertEquals("error", view, "should return the error view name");

        assertEquals(HttpStatus.NOT_IMPLEMENTED,  model.getAttribute("status"));
        assertEquals("Method not implemented",  model.getAttribute("message"));
        assertEquals("/test/bad/request",  model.getAttribute("path"));
        assertTrue(model.getAttribute("timestamp") instanceof LocalDateTime);

        verify(userContextService).getCurrentUser();
        verify(emailService).sendWithStack(uoe, "Unsupported Operation Exception", "/test/bad/request");
    }
}

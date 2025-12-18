package edu.hawaii.its.groupings.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.request.ServletWebRequest;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.EmailService;

class DefaultErrorControllerTest {

    @Mock
    private DefaultErrorAttributes errorAttributes;

    @Mock
    private EmailService emailService;

    @Mock
    private UserContextService userContextService;

    private DefaultErrorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new DefaultErrorController(errorAttributes, emailService, userContextService);
    }

    @Test
    void onError() {
        // Arrange
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/some/path");
        Model model = new ExtendedModelMap();
        RuntimeException ex = new RuntimeException("Runtime Exception");
        when(errorAttributes.getError(any(ServletWebRequest.class)))
                .thenReturn(ex);

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("message", "Runtime Exception");
        attrs.put("path", "/some/path");
        attrs.put("status", 500);
        attrs.put("error", "Internal Server Error");
        when(errorAttributes.getErrorAttributes (
                any(ServletWebRequest.class),
                any(ErrorAttributeOptions.class)))
                .thenReturn(attrs);

        User mockUser = mock(User.class);
        when(mockUser.getUid()).thenReturn("winnie-pooh");
        when(userContextService.getCurrentUser()).thenReturn(mockUser);

        // Act
        String view = controller.onError(servletRequest, model);

        // Assert
        assertEquals("error", view, "should return the error view name");

        assertEquals("Runtime Exception",       model.getAttribute("message"));
        assertEquals("/some/path",              model.getAttribute("path"));
        assertEquals(500,                       model.getAttribute("status"));
        assertEquals("Internal Server Error",   model.getAttribute("error"));
        assertTrue(model.getAttribute("timestamp") instanceof LocalDateTime);

        // confirm we looked up the user and emailed the exception
        verify(userContextService).getCurrentUser();
        verify(emailService).sendWithStack(ex, "RuntimeException", "/some/path");
    }
}

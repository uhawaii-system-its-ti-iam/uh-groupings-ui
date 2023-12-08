package edu.hawaii.its.groupings.access;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailureHandler
        implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    private static final Log logger = LogFactory.getLog(AuthenticationFailureHandler.class);

    private final String redirectUrl;

    public AuthenticationFailureHandler(String appUrlError) {
        this.redirectUrl = appUrlError;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        logger.warn("onAuthenticationFailure; exception: ", exception);
        request.getSession().setAttribute("login.error.message", "It appears that you have not logged in with your personal account. Please check. You may have to log back in with your personal UH account to access this service.");
        request.getSession().setAttribute("login.error.exception.message", exception.getMessage());
        response.sendRedirect(redirectUrl);
    }
}

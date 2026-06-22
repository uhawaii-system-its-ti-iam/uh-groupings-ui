package edu.hawaii.its.groupings.configuration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.api.service.OotbHttpRequestService;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
public class OotbStaticUserAuthenticationFilterTest {

    @Autowired
    private OotbStaticUserAuthenticationFilter filter;

    @MockitoBean
    private OotbHttpRequestService ootbHttpRequestService;

    @MockitoBean
    private OotbActiveUserProfileService ootbActiveUserProfileService;

    private UserDetails userDetails;

    private ServletRequest request;

    private ServletResponse response;

    private FilterChain chain;

    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        userDetails = mock(UserDetails.class);
        request = mock(ServletRequest.class);
        response = mock(ServletResponse.class);
        chain = mock(FilterChain.class);
        securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        filter.setUserProfile("ADMIN");
        when(ootbActiveUserProfileService.loadUserByUsername("ADMIN")).thenReturn(userDetails);

        when(ootbHttpRequestService.makeApiRequestWithActiveProfileBody(any(), any(), any(), any()))
                .thenReturn(null);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterWithOOTBServerTypeAndNoExistingAuth() throws IOException, ServletException {
        when(securityContext.getAuthentication()).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testDoFilterWithOOTBServerTypeAndExistingAuth() throws IOException, ServletException {
        when(securityContext.getAuthentication()).thenReturn(
                mock(org.springframework.security.core.Authentication.class));

        filter.doFilter(request, response, chain);

        verify(securityContext, never()).setAuthentication(any());
        verify(chain).doFilter(request, response);
    }
}

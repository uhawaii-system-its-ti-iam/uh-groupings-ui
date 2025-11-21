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
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.api.service.OotbHttpRequestService;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles({"ootb", "localTest"})
public class OotbStaticUserAuthenticationFilterTest {

    @Autowired
    private OotbStaticUserAuthenticationFilter filter;

    @MockBean
    private OotbHttpRequestService ootbHttpRequestService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private ServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.setContext(securityContext);
        when(userDetailsService.loadUserByUsername("ADMIN")).thenReturn(userDetails);

        when(ootbHttpRequestService.makeApiRequestWithActiveProfileBody(any(), any(), any(), any()))
                .thenReturn(null);
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

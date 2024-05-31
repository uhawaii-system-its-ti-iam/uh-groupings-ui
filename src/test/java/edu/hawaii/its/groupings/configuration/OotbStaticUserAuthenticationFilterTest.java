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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
public class OotbStaticUserAuthenticationFilterTest {

    @InjectMocks
    private OotbStaticUserAuthenticationFilter filter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.setContext(securityContext);
        when(userDetailsService.loadUserByUsername("ADMIN")).thenReturn(userDetails);
        filter = new OotbStaticUserAuthenticationFilter(userDetailsService, "ADMIN");
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

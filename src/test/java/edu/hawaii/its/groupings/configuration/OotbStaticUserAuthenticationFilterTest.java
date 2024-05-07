package edu.hawaii.its.groupings.configuration;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class OotbStaticUserAuthenticationFilterTest {

    @InjectMocks
    private OotbStaticUserAuthenticationFilter filter;

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
        List<String> authorities = List.of("ROLE_USER");
        filter = new OotbStaticUserAuthenticationFilter("OOTB", "John", "testiwa", "Smith", "testiwa@hawaii.edu",
                authorities);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testDoFilterWithOOTBServerTypeAndNoExistingAuth() throws IOException, ServletException {
        when(securityContext.getAuthentication()).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(securityContext).setAuthentication(any());
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

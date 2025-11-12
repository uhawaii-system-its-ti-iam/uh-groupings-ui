package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.api.controller.OotbRestController;
import edu.hawaii.its.api.service.OotbHttpRequestService;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles({"ootb", "localTest"})
class OotbSecurityConfigTest {

    @Autowired
    private OotbSecurityConfig ootbSecurityConfig;

    @MockBean
    private OotbHttpRequestService ootbHttpRequestService;
    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @BeforeEach
    public void setUp() {
         when(ootbHttpRequestService.makeApiRequestWithActiveProfileBody(any(),any(),any(),any()))
        .thenReturn(null);
    }

    @Test
    public void testPasswordEncoder() {
        PasswordEncoder encoder = ootbSecurityConfig.passwordEncoder();
        assertNotNull(encoder, "Password Encoder should not be null");
    }

    @Test
    public void testFilterChain() throws Exception {
        SecurityFilterChain chain = ootbSecurityConfig.filterChain(mock(HttpSecurity.class));
        assertNotNull(chain, "Security Filter Chain should not be null");
    }

    @Test
    public void testAuthenticationFailureHandler() {
        assertNotNull(ootbSecurityConfig.authenticationFailureHandler(),
                "Authentication Failure Handler should not be null");
    }

}

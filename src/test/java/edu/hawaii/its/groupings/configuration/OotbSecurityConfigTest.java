package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
@ExtendWith(MockitoExtension.class)
class OotbSecurityConfigTest {

    @Autowired
    private OotbSecurityConfig ootbSecurityConfig;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;


    @Test
    public void testAuthenticationManager() throws Exception {
        AuthenticationManager manager =
                ootbSecurityConfig.authenticationManager(authenticationConfiguration, mock(UserDetailsService.class));
        assertNotNull(manager, "Authentication Manager should not be null");
    }

    @Test
    public void testPasswordEncoder() {
        PasswordEncoder encoder = ootbSecurityConfig.passwordEncoder();
        assertNotNull(encoder, "Password Encoder should not be null");
    }

    @Test
    public void testUserDetailsService() {
        UserDetailsService userDetailsService = ootbSecurityConfig.userDetailsService();
        assertNotNull(userDetailsService, "User Details Service should not be null");
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

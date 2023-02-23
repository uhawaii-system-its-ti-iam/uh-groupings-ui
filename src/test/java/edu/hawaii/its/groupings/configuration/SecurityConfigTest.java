package edu.hawaii.its.groupings.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    public void construction() {
        assertNotNull(securityConfig);
        assertNotNull(securityConfig.singleLogoutFilter());
        assertNotNull(securityConfig.logoutFilter());
    }

}

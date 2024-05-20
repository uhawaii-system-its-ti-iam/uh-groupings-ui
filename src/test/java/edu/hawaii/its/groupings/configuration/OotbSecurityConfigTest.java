package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
public class OotbSecurityConfigTest {

    @Autowired
    private OotbSecurityConfig ootbSecurityConfig;

    @Test
    public void construction() {
        assertNotNull(ootbSecurityConfig);
        assertNotNull(ootbSecurityConfig.ootbStaticUserAuthenticationFilter());
    }

}

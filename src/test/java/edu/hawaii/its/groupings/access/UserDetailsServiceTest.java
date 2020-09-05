package edu.hawaii.its.groupings.access;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class UserDetailsServiceTest {

    @Autowired
    private UserBuilder userBuilder;

    @Autowired
    private UserContextService userContextService;

    @Test
    @WithMockUhUser(username = "admin", roles = { "ROLE_ADMIN" })
    public void testAdminUsers() {
        User user = userContextService.getCurrentUser();
        assertTrue(user.hasRole(Role.ADMIN));
    }

    @Test
    public void loadUserDetailsExceptionOne() {
        Assertion assertion = new AssertionDummy();
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userBuilder);
        try {
            userDetailsService.loadUserDetails(assertion);
            fail("Should not have reached here.");
        } catch (Exception e) {
            assertThat(UsernameNotFoundException.class, equalTo(e.getClass()));
            assertThat(e.getMessage(), containsString("principal is null"));
        }
    }
}

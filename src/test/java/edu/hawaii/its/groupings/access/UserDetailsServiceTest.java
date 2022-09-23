package edu.hawaii.its.groupings.access;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class UserDetailsServiceTest {

    @Autowired
    private UserBuilder userBuilder;

    @MockBean
    private GroupingsRestController groupingsRestController;

    @Test
    public void construction() {
        assertThat(userBuilder, not(equalTo(null)));
    }

    @Test
    public void testNonOwnerAdmin() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", "1");
        map.put("uhUuid", "j");
        AttributePrincipal principal = new AttributePrincipalImpl("j", map);
        Assertion assertion = new AssertionImpl(principal);
        CasUserDetailsServiceImpl userDetailsService = new CasUserDetailsServiceImpl(userBuilder);
        User user = (User) userDetailsService.loadUserDetails(assertion);
        assertFalse(user.hasRole(Role.OWNER));
        assertFalse(user.hasRole(Role.ADMIN));
    }

    @Test
    public void testAdminUser() {
        final String uhUuid = "89999999";

        // Make up a user.
        Map<String, Object> map = new HashMap<>();
        map.put("uid", "levia");
        map.put("uhUuid", uhUuid);
        AttributePrincipal principal = new AttributePrincipalImpl("levia", map);
        Assertion assertion = new AssertionImpl(principal);
        CasUserDetailsServiceImpl userDetailsService = new CasUserDetailsServiceImpl(userBuilder);

        // Mock the role fetching from the Grouper server.
        final Principal p = new SimplePrincipal(uhUuid);
        given(groupingsRestController.hasOwnerPrivs(p))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));
        given(groupingsRestController.hasAdminPrivs(p))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));

        // What we are testing.
        User user = (User) userDetailsService.loadUserDetails(assertion);

        // Basics.
        assertThat(user.getUsername(), equalTo("levia"));
        assertThat(user.getUid(), equalTo("levia"));
        assertThat(user.getUhUuid(), equalTo("89999999"));

        // Granted Authorities.
        assertThat(user.getAuthorities().size(), equalTo(4));
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.OWNER));
        assertTrue(user.hasRole(Role.ADMIN));

        // Make sure the mocks were called.
        verify(groupingsRestController, times(1)).hasOwnerPrivs(p);
        verify(groupingsRestController, times(1)).hasAdminPrivs(p);
    }

    @Test
    public void testOwner() {
        final String uid = "jjcale";
        final String uhUuid = "90000000";
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("uhUuid", uhUuid);

        AttributePrincipal principal = new AttributePrincipalImpl("jjcale", map);
        Assertion assertion = new AssertionImpl(principal);
        CasUserDetailsServiceImpl userDetailsService = new CasUserDetailsServiceImpl(userBuilder);

        // Mock the role fetching from the Grouper server.
        final Principal p = new SimplePrincipal(uhUuid);
        given(groupingsRestController.hasOwnerPrivs(p))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));
        given(groupingsRestController.hasAdminPrivs(p))
                .willReturn(new ResponseEntity<>("false", HttpStatus.OK)); // Note.

        // What we are testing.
        User user = (User) userDetailsService.loadUserDetails(assertion);

        // Basics.
        assertThat(user.getUsername(), equalTo("jjcale"));
        assertThat(user.getUid(), equalTo("jjcale"));
        assertThat(user.getUhUuid(), equalTo("90000000"));

        // Granted Authorities.
        assertThat(user.getAuthorities().size(), equalTo(3));
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.OWNER));

        assertFalse(user.hasRole(Role.ADMIN));

        // Make sure the mocks were called.
        verify(groupingsRestController, times(1)).hasOwnerPrivs(p);
        verify(groupingsRestController, times(1)).hasAdminPrivs(p);
    }

    @Test
    public void loadUserDetailsExceptionOne() {
        Assertion assertion = new AssertionDummy();
        CasUserDetailsServiceImpl userDetailsService = new CasUserDetailsServiceImpl(userBuilder);
        try {
            userDetailsService.loadUserDetails(assertion);
            fail("Should not have reached here.");
        } catch (Exception e) {
            assertThat(UsernameNotFoundException.class, equalTo(e.getClass()));
            assertThat(e.getMessage(), containsString("principal is null"));
        }
    }
}

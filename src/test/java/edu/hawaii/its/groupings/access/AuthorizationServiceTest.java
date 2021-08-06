package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.security.Principal;

import org.jasig.cas.client.authentication.SimplePrincipal;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class AuthorizationServiceTest {

    @Autowired
    private AuthorizationService authorizationService;

    @MockBean
    private GroupingsRestController groupingsRestController;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void basics() {
        assertNotNull(authorizationService);
    }

    @Test
    @WithMockUhUser
    public void fetchDefaultTest() {
        // Setup for the mocking.
        User user = userContextService.getCurrentUser();
        String uhUuid = user.getUhUuid();

        Principal principal = new SimplePrincipal(uhUuid);

        given(groupingsRestController.hasOwnerPrivs(principal))
                .willReturn(new ResponseEntity<>(null, HttpStatus.OK));
        given(groupingsRestController.hasAdminPrivs(principal))
                .willReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // What we are testing.
        RoleHolder roleHolder = authorizationService.fetchRoles(uhUuid, "test");

        // Check results.
        assertThat(roleHolder.size(), equalTo(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));
        assertFalse(roleHolder.contains(Role.OWNER));
    }

    @Test
    @WithMockUhUser
    public void fetchPrivilegesTest() {
        // Setup for the mocking.
        User user = userContextService.getCurrentUser();
        String uhUuid = user.getUhUuid();

        Principal principal = new SimplePrincipal(uhUuid);

        given(groupingsRestController.hasOwnerPrivs(principal))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));
        given(groupingsRestController.hasAdminPrivs(principal))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));

        // What we are testing.
        RoleHolder roleHolder = authorizationService.fetchRoles(uhUuid, "test");

        // Check results.
        assertThat(roleHolder.size(), equalTo(4));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertTrue(roleHolder.contains(Role.OWNER));
        assertTrue(roleHolder.contains(Role.ADMIN));
    }

    @Ignore
    @Test
    public void fetch() {
        RoleHolder roleHolder = authorizationService.fetchRoles("10000001", "test");
        assertThat(roleHolder.size(), equalTo(4));
        assertTrue(roleHolder.contains(Role.ANONYMOUS)); // ???
        assertTrue(roleHolder.contains(Role.UH));
        assertTrue(roleHolder.contains(Role.EMPLOYEE));
        assertTrue(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("10000004", "test");
        assertThat(roleHolder.size(), equalTo(3));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertTrue(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("10000004", "test");
        assertThat(roleHolder.size(), equalTo(3));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertTrue(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

        // 10000005
        roleHolder = authorizationService.fetchRoles("10000005", "test");
        assertThat(roleHolder.size(), equalTo(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("90000009", "test");
        assertThat(roleHolder.size(), equalTo(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

    }
}

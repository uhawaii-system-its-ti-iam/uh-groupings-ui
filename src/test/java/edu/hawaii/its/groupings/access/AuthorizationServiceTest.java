package edu.hawaii.its.groupings.access;

import org.jasig.cas.client.authentication.SimplePrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class AuthorizationServiceTest {

    @Autowired
    private AuthorizationService authorizationService;

    @MockBean
    private GroupingsRestController groupingsRestController;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
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
        assertThat(roleHolder.size(), is(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
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
        assertThat(roleHolder.size(), is(4));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertTrue(roleHolder.contains(Role.OWNER));
        assertTrue(roleHolder.contains(Role.ADMIN));

        // Reassign uhUuid value
        uhUuid = "test";
        roleHolder = authorizationService.fetchRoles(uhUuid, "test");
        assertTrue(roleHolder.contains(Role.DEPARTMENT));
    }

    @Test
    public void fetchRolesTestUH() {
        RoleHolder roleHolder = authorizationService.fetchRoles("10000001", "test");
        assertThat(roleHolder.size(), is(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertFalse(roleHolder.contains(Role.OWNER));
        assertFalse(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("1", "test");
        assertThat(roleHolder.size(), is(1));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertFalse(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertFalse(roleHolder.contains(Role.OWNER));
        assertFalse(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("test", "test");
        assertThat(roleHolder.size(), is(3));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertTrue(roleHolder.contains(Role.DEPARTMENT));
        assertFalse(roleHolder.contains(Role.OWNER));
        assertFalse(roleHolder.contains(Role.ADMIN));
    }

    @Disabled
    @Test
    public void fetch() {
        RoleHolder roleHolder = authorizationService.fetchRoles("10000001", "test");
        assertThat(roleHolder.size(), is(4));
        assertTrue(roleHolder.contains(Role.ANONYMOUS)); // ???
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertTrue(roleHolder.contains(Role.EMPLOYEE));
        assertTrue(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("10000004", "test");
        assertThat(roleHolder.size(), is(3));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertTrue(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("10000004", "test");
        assertThat(roleHolder.size(), is(3));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertTrue(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

        // 10000005
        roleHolder = authorizationService.fetchRoles("10000005", "test");
        assertThat(roleHolder.size(), is(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertFalse(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

        roleHolder = authorizationService.fetchRoles("90000009", "test");
        assertThat(roleHolder.size(), is(2));
        assertTrue(roleHolder.contains(Role.ANONYMOUS));
        assertTrue(roleHolder.contains(Role.UH));
        assertFalse(roleHolder.contains(Role.DEPARTMENT));
        assertFalse(roleHolder.contains(Role.EMPLOYEE));
        assertFalse(roleHolder.contains(Role.ADMIN));

    }
}

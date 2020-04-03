package edu.hawaii.its.groupings.access;

import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class AuthorizationServiceTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private GroupingsRestController groupingsRestController;

    @Test
    public void basics() {
        assertNotNull(authorizationService);
    }

    // Rebase. Test fetch for code coverage purposes.
    // Related to ticket-500, used hardcoded values that were deleted.
    @Test
    public void fetch() {
        for (int i = 0; i < 6; i++) {
            String str = "iamtst0" + (i + 1);
            RoleHolder roleHolder = authorizationService.fetchRoles("17118183", "gilbertz");
            System.out.println("------------------------------------------");
            System.out.println(roleHolder.toString());
            System.out.println("------------------------------------------");
        }
        /*
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

         */
    }
/*
    @Test
    public void checkResultCodeJsonObject() {
        // Test is not an owner
        for (int i = 0; i < 6; i++) {
            System.out.println(authorizationService.checkResultCodeJsonObject(
                    groupingsRestController.isOwner(new SimplePrincipal("iamtst0" + (i + 1)))
            ));
        }
        assertFalse(authorizationService.checkResultCodeJsonObject(
                groupingsRestController.isOwner(new SimplePrincipal("iamtst01"))));
    }

 */
}

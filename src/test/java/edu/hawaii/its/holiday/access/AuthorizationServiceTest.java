package edu.hawaii.its.holiday.access;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class AuthorizationServiceTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Test
    public void basics() {
        assertTrue(true);
        assertNotNull(authorizationService);
    }

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

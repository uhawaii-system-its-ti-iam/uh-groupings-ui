package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class UserContextServiceTest {

    @Autowired
    private UserContextService userContextService;

    @Test
    @WithMockUhUser(uid = "admin", roles = { "ROLE_ADMIN" })
    public void basics() {
        assertThat("12345678", is(userContextService.getCurrentUhUuid()));
        assertThat("admin", is(userContextService.getCurrentUid()));
        assertTrue(userContextService.toString().startsWith("UserContextServiceImpl"));

        User user = userContextService.getCurrentUser();
        assertNotNull(user);
        assertThat("12345678", is(user.getUhUuid()));
        assertThat("admin", is(user.getUid()));

        userContextService.setCurrentUhUuid("87654321");
        assertThat("87654321", is(userContextService.getCurrentUhUuid()));
    }
    @Test
    @WithMockUhUser(uid = "Owner", roles = { "ROLE_OWNER"})
    public void testOwner(){
        User user = userContextService.getCurrentUser();
        assertFalse(user.hasRole(Role.ADMIN));
        assertTrue(user.hasRole(Role.OWNER));
    }
}



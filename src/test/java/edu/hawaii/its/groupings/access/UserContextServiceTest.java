package edu.hawaii.its.groupings.access;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class UserContextServiceTest {

    @Autowired
    private UserContextService userContextService;

    @Test
    @WithMockUhUser(username = "admin", roles = { "ROLE_ADMIN" })
    public void basics() {
        assertThat(userContextService.getCurrentUhUuid(), equalTo("12345678"));
        assertThat(userContextService.getCurrentUsername(), equalTo("admin"));
        assertThat(userContextService.toString(), startsWith("UserContextServiceImpl"));

        User user = userContextService.getCurrentUser();
        assertNotNull(user);
        assertThat(user.getUhUuid(), equalTo("12345678"));
        assertThat(user.getUsername(), equalTo("admin"));

        userContextService.setCurrentUhUuid("87654321");
        assertThat(userContextService.getCurrentUhUuid(), equalTo("87654321"));
    }
    @Test
    @WithMockUhUser(username = "Owner", roles = { "ROLE_OWNER"})
    public void testOwner(){
        User user = userContextService.getCurrentUser();
        assertThat(user.hasRole(Role.ADMIN), equalTo(false));
        assertThat(user.hasRole(Role.OWNER), equalTo(true));
    }
}



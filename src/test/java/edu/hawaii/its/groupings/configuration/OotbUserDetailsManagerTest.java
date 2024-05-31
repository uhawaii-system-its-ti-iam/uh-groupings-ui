package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.groupings.access.User;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
class OotbUserDetailsManagerTest {

    private OotbUserDetailsManager manager;

    @BeforeEach
    public void setUp() {
        manager = new OotbUserDetailsManager();
    }

    @Test
    public void testUserUserDetails() {
        UserDetails userDetails = manager.loadUserByUsername("USER");
        assertNotNull(userDetails, "UserDetails should not be null");

        User user = manager.getUsers().get("USER");
        assertNotNull(user, "User should not be null");
        assertEquals("user0123", user.getUid(), "Username should match");
        assertEquals("11111111", user.getUhUuid(), "UhUuid should match");
        assertEquals(1, user.getAuthorities().size(), "Should have 1 authority");
    }

    @Test
    public void testOwnerUserDetails() {
        UserDetails userDetails = manager.loadUserByUsername("OWNER");
        assertNotNull(userDetails, "UserDetails should not be null");

        User user = manager.getUsers().get("OWNER");
        assertNotNull(user, "User should not be null");
        assertEquals("owner0123", user.getUid(), "Username should match");
        assertEquals("22222222", user.getUhUuid(), "UhUuid should match");
        assertEquals(2, user.getAuthorities().size(), "Should have 2 authorities");
    }

    @Test
    public void testAdminUserDetails() {
        UserDetails userDetails = manager.loadUserByUsername("ADMIN");
        assertNotNull(userDetails, "UserDetails should not be null");

        User user = manager.getUsers().get("ADMIN");
        assertNotNull(user, "User should not be null");
        assertEquals("admin0123", user.getUid(), "Username should match");
        assertEquals("33333333", user.getUhUuid(), "UhUuid should match");
        assertEquals(3, user.getAuthorities().size(), "Should have 3 authorities");
    }

    @Test
    public void testLoadUserWithNonExistentUser() {
        // Test for non-existent user profile
        assertThrows(UsernameNotFoundException.class, () -> {
            manager.loadUserByUsername("NON_EXISTENT");
        }, "Expected UsernameNotFoundException for non-existent user profile");
    }
}
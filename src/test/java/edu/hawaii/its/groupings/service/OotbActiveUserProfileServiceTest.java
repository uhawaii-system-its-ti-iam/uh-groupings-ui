package edu.hawaii.its.groupings.service;

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
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
class OotbActiveUserProfileServiceTest {

    private OotbActiveUserProfileService manager;

    @BeforeEach
    public void setUp() {
        manager = new OotbActiveUserProfileService();
    }

    @Test
    public void testUserUserDetails() {
        UserDetails userDetails = manager.loadUserByUsername("MEMBER");
        assertNotNull(userDetails, "UserDetails should not be null");

        User user = manager.getUsers().get("MEMBER");
        assertNotNull(user, "Member should not be null");
        assertEquals("member0123", user.getUid(), "Username should match");
        assertEquals("11111111", user.getUhUuid(), "UhUuid should match");
        assertEquals(2, user.getAuthorities().size(), "Should have 1 authority");
    }

    @Test
    public void testOwnerUserDetails() {
        UserDetails userDetails = manager.loadUserByUsername("OWNER");
        assertNotNull(userDetails, "UserDetails should not be null");

        User user = manager.getUsers().get("OWNER");
        assertNotNull(user, "User should not be null");
        assertEquals("owner0123", user.getUid(), "Username should match");
        assertEquals("22222222", user.getUhUuid(), "UhUuid should match");
        assertEquals(3, user.getAuthorities().size(), "Should have 2 authorities");
    }

    @Test
    public void testAdminUserDetails() {
        UserDetails userDetails = manager.loadUserByUsername("ADMIN");
        assertNotNull(userDetails, "UserDetails should not be null");

        User user = manager.getUsers().get("ADMIN");
        assertNotNull(user, "User should not be null");
        assertEquals("admin0123", user.getUid(), "Username should match");
        assertEquals("33333333", user.getUhUuid(), "UhUuid should match");
        assertEquals(4, user.getAuthorities().size(), "Should have 3 authorities");
    }

    @Test
    public void testLoadUserWithNonExistentUser() {
        // Test for non-existent user profile
        assertThrows(UsernameNotFoundException.class, () -> {
            manager.loadUserByUsername("NON_EXISTENT");
        }, "Expected UsernameNotFoundException for non-existent user profile");
    }
}
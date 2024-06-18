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

    private OotbActiveUserProfileService ootbActiveUserProfileService;

    @BeforeEach
    public void setUp() {
        ootbActiveUserProfileService = new OotbActiveUserProfileService("ootb.active.user.profiles.json");
    }

    @Test
    public void testAdminUserDetails() {
        User user = ootbActiveUserProfileService.getUsers().get(ootbActiveUserProfileService.findGivenNameForAdminRole());
        UserDetails userDetails = ootbActiveUserProfileService.loadUserByUsername(user.getGivenName());
        assertNotNull(userDetails, "UserDetails should not be null");

        assertNotNull(user, "User should not be null");
        assertEquals("admin0123", user.getUid(), "Username should match");
        assertEquals("33333333", user.getUhUuid(), "UhUuid should match");
        assertEquals(3, user.getAuthorities().size(), "Should have 3 authorities");
    }

    @Test
    public void testLoadUserWithNonExistentUser() {
        // Test for non-existent user profile
        assertThrows(UsernameNotFoundException.class, () -> {
            ootbActiveUserProfileService.loadUserByUsername("NON_EXISTENT");
        }, "Expected UsernameNotFoundException for non-existent user profile");
    }
}
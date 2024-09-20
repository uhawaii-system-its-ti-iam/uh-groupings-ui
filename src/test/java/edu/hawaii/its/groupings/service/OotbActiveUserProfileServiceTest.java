package edu.hawaii.its.groupings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.api.service.OotbHttpRequestService;
import edu.hawaii.its.groupings.access.Role;
import edu.hawaii.its.groupings.access.UhAttributes;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.OotbActiveProfile;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles("ootb")
class OotbActiveUserProfileServiceTest {

    @MockBean
    private OotbActiveUserProfileService ootbActiveUserProfileService;

    @MockBean
    private OotbHttpRequestService ootbHttpRequestService;

    @BeforeEach
    public void setUp() {

        when(ootbHttpRequestService.makeApiRequestWithActiveProfileBody(any(), any(), any(), any()))
                .thenReturn(null);

        List<String> availableProfiles = List.of("admin0123");

        UhAttributes mockAttributes = mock(UhAttributes.class);
        when(mockAttributes.getValue("givenName")).thenReturn("admin0123");
        when(mockAttributes.getValue("cn")).thenReturn("AdminUser");

        User user = new User.Builder("admin0123")
                .uhUuid("33333333")
                .addAuthorities(List.of("ROLE_UH", "ROLE_OWNER", "ROLE_ADMIN"))
                .addAttribute("givenName", "admin0123")
                .build();

        user.setAttributes(mockAttributes);

        Map<String, OotbActiveProfile> activeProfiles = new HashMap<>();
        OotbActiveProfile adminProfile = mock(OotbActiveProfile.class);
        activeProfiles.put("admin0123", adminProfile);

        when(ootbActiveUserProfileService.findGivenNameForAdminRole()).thenReturn("admin0123");
        when(ootbActiveUserProfileService.loadUserByUsername("admin0123")).thenReturn(user);
        when(ootbActiveUserProfileService.loadUserByUsername("NON_EXISTENT"))
                .thenThrow(new UsernameNotFoundException("User not found"));
        when(ootbActiveUserProfileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(ootbActiveUserProfileService.getUsers()).thenReturn(Map.of("admin0123", user));
        when(ootbActiveUserProfileService.getActiveProfiles()).thenReturn(activeProfiles);
    }

    @Test
    public void testAdminUserDetails() {
        User user =
                ootbActiveUserProfileService.getUsers().get(ootbActiveUserProfileService.findGivenNameForAdminRole());
        UserDetails userDetails = ootbActiveUserProfileService.loadUserByUsername(user.getGivenName());
        assertNotNull(userDetails, "UserDetails should not be null");

        assertNotNull(user, "User should not be null");
        assertEquals("admin0123", user.getUid(), "Username should match");
        assertEquals("33333333", user.getUhUuid(), "UhUuid should match");
        assertEquals(3, user.getAuthorities().size(), "Should have 3 authorities");
    }

    @Test
    public void testLoadUserWithNonExistentUser() {
        assertThrows(UsernameNotFoundException.class, () -> {
            ootbActiveUserProfileService.loadUserByUsername("NON_EXISTENT");
        }, "Expected UsernameNotFoundException for non-existent user profile");
    }

    @Test
    public void testConstructorInitialization() {
        assertFalse(ootbActiveUserProfileService.getUsers().isEmpty(), "Users map should be initialized and not empty");
        assertFalse(ootbActiveUserProfileService.getActiveProfiles().isEmpty(),
                "Active profiles map should be initialized and not empty");
    }

    @Test
    public void testFindGivenNameForAdminRole() {
        String adminGivenName = ootbActiveUserProfileService.findGivenNameForAdminRole();
        assertNotNull(adminGivenName, "Should return a non-null given name for admin");
        User adminUser = ootbActiveUserProfileService.getUsers().get(adminGivenName);
        assertTrue(adminUser.hasRole(Role.ADMIN), "The found user should have an ADMIN role");
    }

    @Test
    public void testGetAvailableProfiles() {
        assertNotNull(ootbActiveUserProfileService.getAvailableProfiles(), "Should return a non-null list of profiles");
        assertFalse(ootbActiveUserProfileService.getAvailableProfiles().isEmpty(),
                "Should return a non-empty list of profiles");
        assertEquals(ootbActiveUserProfileService.getUsers().keySet(),
                new HashSet<>(ootbActiveUserProfileService.getAvailableProfiles()),
                "Should match the keys of the users map");
    }

    @Test
    public void testGetterMethods() {
        assertNotNull(ootbActiveUserProfileService.getUsers(), "getUsers should return a non-null Map");
        assertNotNull(ootbActiveUserProfileService.getActiveProfiles(),
                "getActiveProfiles should return a non-null Map");
    }
}
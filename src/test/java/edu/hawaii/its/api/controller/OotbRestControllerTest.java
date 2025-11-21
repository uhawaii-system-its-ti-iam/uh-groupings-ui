package edu.hawaii.its.api.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.api.service.OotbHttpRequestService;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;
import edu.hawaii.its.groupings.type.OotbActiveProfile;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@ActiveProfiles({"ootb", "localTest"})
public class OotbRestControllerTest {
    private static final String REST_CONTROLLER_BASE = "/api/groupings/ootb";

    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OotbRestController ootbRestController;

    @MockBean
    private OotbActiveUserProfileService ootbActiveUserProfileService;

    @MockBean
    private OotbHttpRequestService ootbHttpRequestService;

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUhUser
    public void getAvailableProfilesTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "/availableProfiles";
        List<String> expectedProfiles = List.of("Profile1", "Profile2");
        Map<String, User> orderedUsers = new HashMap<>();
        expectedProfiles.forEach(profile -> orderedUsers.put(profile, mock(User.class)));
        when(ootbActiveUserProfileService.getAvailableProfiles()).thenReturn(expectedProfiles);

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());
    }

    @Test
    @WithMockUhUser
    public void updateActiveDefaultUserTest() throws Exception {
        String activeProfile = "AdminUser";
        String uri = REST_CONTROLLER_BASE + "/" + activeProfile;
        String currentUserUid = "testUser";
        OotbActiveProfile profileData = new OotbActiveProfile();

        when(ootbActiveUserProfileService.getActiveProfiles()).thenReturn(Map.of(activeProfile, profileData));

        ResultActions result = mockMvc.perform(post(uri)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        verify(ootbHttpRequestService, times(1))
                .makeApiRequestWithActiveProfileBody(eq("user"), anyString(), eq(profileData), eq(HttpMethod.POST));
    }
}

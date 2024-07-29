package edu.hawaii.its.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.nimbusds.jose.shaded.gson.JsonIOException;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.api.service.OotbHttpRequestService;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;
import edu.hawaii.its.groupings.type.OotbActiveProfile;

@ActiveProfiles("ootb")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class OotbRestControllerTest {
    private static final String REST_CONTROLLER_BASE = "/api/groupings/ootb";

    private static final String ADMIN_GIVEN_NAME = "AdminUser";

    @MockBean
    OotbRestController ootbRestController;

    @MockBean
    private OotbHttpRequestService ootbHttpRequestService;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private OotbActiveUserProfileService ootbActiveUserProfileService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(ootbHttpRequestService.makeApiRequestWithActiveProfileBody(anyString(), anyString(),
                any(OotbActiveProfile.class), any(HttpMethod.class)))
                .thenReturn(new ResponseEntity(HttpStatus.OK));
        when(ootbActiveUserProfileService.findGivenNameForAdminRole()).thenReturn(ADMIN_GIVEN_NAME);
    }

    @Test
    public void testGetAvailableProfiles() throws Exception {
        String uri = REST_CONTROLLER_BASE + "/availableProfiles";
        List<String> expectedProfiles = ootbActiveUserProfileService.getAvailableProfiles();
        Map<String, User> orderedUsers = new LinkedHashMap<>();
        expectedProfiles.forEach(profile -> orderedUsers.put(profile, mock(User.class)));

        when(ootbActiveUserProfileService.getUsers()).thenReturn(orderedUsers);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON));

        IntStream.range(0, expectedProfiles.size()).forEach(index -> {
            try {
                resultActions.andExpect(jsonPath("$[" + index + "]").value(expectedProfiles.get(index)));
            } catch (Exception e) {
                throw new JsonIOException(e);
            }
        });

        verify(ootbRestController).getAvailableProfiles();
    }

    @Test
    public void testUpdateActiveDefaultUser() throws Exception {
        // Given
        String activeProfile = ADMIN_GIVEN_NAME;
        String uri = REST_CONTROLLER_BASE + "/" + activeProfile;
        String currentUserUid = "testUser";
        OotbActiveProfile profileData = new OotbActiveProfile();

        when(ootbActiveUserProfileService.getActiveProfiles()).thenReturn(Map.of(activeProfile, profileData));
        when(ootbHttpRequestService.makeApiRequestWithActiveProfileBody(eq(currentUserUid), anyString(), eq(profileData),
                eq(HttpMethod.POST)))
                .thenReturn(new ResponseEntity<>(profileData, HttpStatus.OK));

        ResultActions result = mockMvc.perform(post(uri)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        verify(ootbRestController).updateActiveDefaultUser(activeProfile);
    }
}

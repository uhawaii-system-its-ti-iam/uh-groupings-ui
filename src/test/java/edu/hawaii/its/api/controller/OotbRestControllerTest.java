package edu.hawaii.its.api.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;

@ActiveProfiles("ootb")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class OotbRestControllerTest {
    private static final String REST_CONTROLLER_BASE = "/api/groupings/ootb";

    private static final String ADMIN_GIVEN_NAME = "AdminUser";

    @MockBean
    OotbRestController ootbRestController;

    @MockBean
    private HttpRequestService httpRequestService;

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

        when(httpRequestService.makeApiRequestWithBody(anyString(), anyString(), anyList(), any(HttpMethod.class)))
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
    public void updateActiveProfileTest() throws Exception {
        Map<String, User> orderedUsers = new LinkedHashMap<>();
        String adminGivenName = ootbActiveUserProfileService.findGivenNameForAdminRole();
        orderedUsers.put(adminGivenName, mock(User.class));

        String uri = REST_CONTROLLER_BASE + "/" + adminGivenName;

        when(ootbActiveUserProfileService.getUsers()).thenReturn(orderedUsers);
        given(httpRequestService.makeApiRequestWithBody(eq(ADMIN_GIVEN_NAME), anyString(), anyList(),
                eq(HttpMethod.POST)))
                .willReturn(new ResponseEntity<>(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(ootbRestController).updateActiveDefaultUser(ADMIN_GIVEN_NAME);
    }
}

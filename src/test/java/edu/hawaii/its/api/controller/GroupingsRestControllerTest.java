package edu.hawaii.its.api.controller;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.configuration.Realm;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import edu.hawaii.its.groupings.exceptions.ApiServerHandshakeException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class GroupingsRestControllerTest {

    private static final String GROUPING = "grouping1";
    private static final String GROUPING2 = "grouping2";
    private static final String GROUPING3 = "grouping3";
    private static final String USERNAME = "user";
    private static final String REST_CONTROLLER_BASE = "/api/groupings/";
    private static final String ADMIN_USERNAME = "admin";

    @MockBean
    private HttpRequestService httpRequestService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(httpRequestService.makeApiRequest(anyString(), anyString(), any(HttpMethod.class)))
                .thenReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }

    @Test
    @WithMockUhUser
    public void rootTest() throws Exception {
        assertNotNull(mockMvc.perform(get(REST_CONTROLLER_BASE).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());
    }

    @Test
    @WithMockUhUser
    public void helloTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "/";
        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());
    }

    @Test
    @WithMockUhUser
    public void currentUsernameTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "/currentUser";
        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{'username':" + USERNAME + "}"))
                .andReturn());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void adminListsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "adminLists";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void hasAdminPrivsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "admins";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void addAdminTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "newAdmin/addAdmin";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.POST)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.POST));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void removeAdminTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "newAdmin/removeAdmin";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void removeFromGroupsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING3 + "/user/removeFromGroups";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void resetGroupTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/user1/user2" + "/resetGroup";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE));
    }

    @Test
    @WithMockUhUser
    public void memberAttributesTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "members/0000";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void membershipResultsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "members/groupings";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void numberOfMembershipsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "/members/memberships";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void membershipAssignmentTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "members/0000/groupings";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void optInGroupsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "groupings/optInGroups";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void optInTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/optIn";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void optOutTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/optOut";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void addMembersToIncludeGroupTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/" + USERNAME + "/addMembersToIncludeGroup";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));

    }

    @Test
    @WithMockUhUser
    public void addMembersToExcludeGroupTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/" + USERNAME + "/addMembersToExcludeGroup";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void removeMembersFromIncludeGroupTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/" + USERNAME + "/removeMembersFromIncludeGroup";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE));
    }

    @Test
    @WithMockUhUser
    public void removeMembersFromExcludeGroupTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/" + USERNAME + "/removeMembersFromExcludeGroup";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE));
    }

    @Test
    @WithMockUhUser
    public void groupingsOwnedTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "owners/groupings";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void numberOfGroupingsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "owners/grouping";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void hasOwnerPrivsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "owners";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void groupingsOwnedUidTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "owners/" + USERNAME + "/groupings";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void assignOwnershipTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/user/addOwnerships";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void removeOwnershipsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/user/removeOwnerships";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity<>(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE));
    }

    @Test
    @WithMockUhUser
    public void groupingTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "groupings/" + GROUPING;

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void updateDescriptionTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "groupings/path/description";

        given(httpRequestService.makeApiRequestWithBody(eq(USERNAME), anyString(), eq(null), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(put(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequestWithBody(eq(USERNAME), anyString(), eq(null), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void enableSyncDestTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "groupings/" + GROUPING + "/syncDests/listserv/enable";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void disableSyncDestTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "groupings/" + GROUPING + "/syncDests/listserv/disable";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void setOptInTrueTest() throws Exception {
        String uri_true = REST_CONTROLLER_BASE + GROUPING + "/true/setOptIn";
        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri_true).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));

    }

    @Test
    @WithMockUhUser
    public void setOptInFalseTest() throws Exception {
        String uri_false = REST_CONTROLLER_BASE + GROUPING + "/false/setOptIn";
        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri_false).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));

    }

    @Test
    @WithMockUhUser
    public void setOptOut() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/true/setOptOut";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void isSoleOwner() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/owners/" + USERNAME;

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void setOptOutFalseTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/false/setOptOut";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(post(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT));
    }

    @Test
    @WithMockUhUser
    public void allSyncDestinationsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "/groupings/" + GROUPING + "/syncDestinations";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        assertNotNull(mockMvc.perform(get(uri).with(csrf()))
                .andExpect(status().isOk())
                .andReturn());

        verify(httpRequestService, times(1))
                .makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET));
    }

    @Test
    @WithMockUhUser
    public void shouldDoGrouperHandshake() throws Exception {
        GroupingsRestController controller = applicationContext.getBean(GroupingsRestController.class);

        Realm realm = controller.getRealm();
        assertFalse(realm.isAnyProfileActive("default"));
        assertTrue(realm.isAnyProfileActive("localTest"));

        // What we are testing.
        assertFalse(controller.shouldDoApiHandshake());

        Realm realmMock = mock(Realm.class);
        controller.setRealm(realmMock); // Swap in mock.
        assertTrue(mockingDetails(controller.getRealm()).isMock());
        given(realmMock.isAnyProfileActive("default", "localTest"))
                .willReturn(false);

        // What we are testing.
        assertTrue(controller.shouldDoApiHandshake());

        // Mock real realm back.
        controller.setRealm(realm);
        assertFalse(mockingDetails(controller.getRealm()).isMock());

        // Let's cheat a little to get at some private fields.
        Class<?> c0 = controller.getClass();
        Field field0 = c0.getDeclaredField("API_HANDSHAKE_ENABLED");
        field0.setAccessible(true);
        Boolean existingValue = (Boolean) field0.get(controller);
        assertTrue(existingValue);
        field0.set(controller, Boolean.FALSE);

        // What we are testing.
        assertFalse(controller.shouldDoApiHandshake());

        // Put property value back.
        field0.set(controller, Boolean.TRUE);
    }

    @Test
    @WithMockUhUser
    public void doGrouperHandshake() {
        GroupingsRestController controller = applicationContext.getBean(GroupingsRestController.class);
        assertFalse(controller.shouldDoApiHandshake());
        try {
            controller.doApiHandshake();
        } catch (Exception e) {
            fail("Should not reach here.");
        }

        Realm realm = controller.getRealm();
        Realm realmMock = mock(Realm.class);
        controller.setRealm(realmMock); // Swap in mock.
        assertTrue(mockingDetails(controller.getRealm()).isMock());
        given(realmMock.isAnyProfileActive("default", "localTest"))
                .willReturn(false);

        try {
            controller.doApiHandshake();
            fail("Should not reach here.");
        } catch (Exception e) {
            assertThat(e, instanceOf(ApiServerHandshakeException.class));
        }

        // Cause in an internal exception.
        HttpRequestService httpRequestServiceOriginal = controller.getHttpRequestService();
        controller.setHttpRequestService(null);

        try {
            controller.doApiHandshake();
            fail("Should not reach here.");
        } catch (Exception e) {
            assertThat(e, instanceOf(ApiServerHandshakeException.class));
        }

        // Put stuff back.
        controller.setHttpRequestService(httpRequestServiceOriginal);
        controller.setRealm(realm);
        assertFalse(mockingDetails(controller.getRealm()).isMock());
    }

}
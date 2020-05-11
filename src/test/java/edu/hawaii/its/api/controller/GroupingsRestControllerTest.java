package edu.hawaii.its.api.controller;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class GroupingsRestControllerTest {

    private static final String GROUPING = "grouping";
    private static final String USERNAME = "user";
    private static final String REST_CONTROLLER_BASE = "/api/groupings/";
    private static final String ADMIN_USERNAME = "admin";

    @Value("${app.iam.request.form}")
    private String requestForm;

    @MockBean
    private HttpRequestService httpRequestService;

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

        mockMvc.perform(get(REST_CONTROLLER_BASE))
                .andExpect(status().isOk());

    }

    // TODO: the URLs being called are matching any String, but they should be changed to the specific URL that the API
    // is using. Maybe this can be done by using the openApiSpecification file.

    @Test
    @WithMockUhUser
    public void getGrouping() throws Exception {
        //        String uri = REST_CONTROLLER_BASE + GROUPING + "/grouping";
        String uri = REST_CONTROLLER_BASE + "groupings/" + GROUPING;

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(get(uri))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getOwnedGroupingsTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "owners/groupings";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(get(uri))
                .andExpect(status().isOk());

        uri = REST_CONTROLLER_BASE + "owners/" + USERNAME + "/groupings";

        mockMvc.perform(get(uri))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getMembershipAssignmentTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "members/groupings";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(get(uri))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void addAdminTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "newAdmin/addAdmin";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.POST)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void deleteAdminTest() throws Exception {
        String uri = REST_CONTROLLER_BASE + "newAdmin/deleteAdmin";

        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void postAddMember() throws Exception {
        String uri_include = REST_CONTROLLER_BASE + GROUPING + "/user/addMemberToIncludeGroup";
        String uri_exclude = REST_CONTROLLER_BASE + GROUPING + "/user/addMemberToExcludeGroup";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri_include)
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post(uri_exclude)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getDeleteMember() throws Exception {
        String uri_include = REST_CONTROLLER_BASE + "grouping/user/removeMembersFromIncludeGroup";
        String uri_exclude = REST_CONTROLLER_BASE + GROUPING + "/user/removeMembersFromExcludeGroup";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri_include)
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post(uri_exclude)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getAssignOwnership() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/user/assignOwnership";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getRemoveOwnership() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/user/removeOwnership";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.DELETE)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUhUser
    public void getSetListserv() throws Exception {
        String uri_true = REST_CONTROLLER_BASE + "groupings/" + GROUPING + "/syncDests/listserv/enable";
        String uri_false = REST_CONTROLLER_BASE + "groupings/" + GROUPING + "/syncDests/listserv/disable";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri_true)
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post(uri_false)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getSetLdap() throws Exception {
        String uri_true = REST_CONTROLLER_BASE + "groupings/" + GROUPING + "/syncDests/uhReleasedGrouping/disable";
        String uri_false = REST_CONTROLLER_BASE + "groupings/" + GROUPING + "/syncDests/uhReleasedGrouping/disable";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri_true)
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post(uri_false)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getSetOptIn() throws Exception {
        String uri_true = REST_CONTROLLER_BASE + GROUPING + "/true/setOptIn";
        String uri_false = REST_CONTROLLER_BASE + GROUPING + "/false/setOptIn";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri_true)
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post(uri_false)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getSetOptOut() throws Exception {
        String uri_true = REST_CONTROLLER_BASE + GROUPING + "/true/setOptOut";
        String uri_false = REST_CONTROLLER_BASE + GROUPING + "/false/setOptOut";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri_true)
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post(uri_false)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getOptIn() throws Exception {
        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(REST_CONTROLLER_BASE + GROUPING + "/optIn")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getOptOut() throws Exception {
        String uri = REST_CONTROLLER_BASE + GROUPING + "/optOut";

        given(httpRequestService.makeApiRequest(eq(USERNAME), anyString(), eq(HttpMethod.PUT)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void adminListsTest() throws Exception {
        given(httpRequestService.makeApiRequest(eq(ADMIN_USERNAME), anyString(), eq(HttpMethod.GET)))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(get(REST_CONTROLLER_BASE + "adminLists"))
                .andExpect(status().isOk());
    }
}

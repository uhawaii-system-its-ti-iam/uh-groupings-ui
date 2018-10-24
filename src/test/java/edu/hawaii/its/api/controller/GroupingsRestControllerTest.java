package edu.hawaii.its.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@RunWith(SpringRunner.class)
@ActiveProfiles("localTest")
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsRestControllerTest {

    private static final String GROUPING = "grouping";
    private static final String USERNAME = "user";
    private static final String API_BASE = "/api/groupings/";
    private static final String ADMIN_USERNAME = "admin";

    @Value("${app.iam.request.form}")
    private String requestForm;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private GroupingsRestController groupingsRestController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        given(groupingsRestController.makeApiRequest(anyString(), anyString(), any(HttpMethod.class), any(Class.class)))
                .willReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }

    // Test data.
    private Grouping grouping() {
        Grouping grouping = new Grouping("test:ing:me:bob");

        Group basisGroup = new Group();
        basisGroup.addMember(new Person("b0-name", "b0-uuid", "b0-username"));
        basisGroup.addMember(new Person("b1-name", "b1-uuid", "b1-username"));
        basisGroup.addMember(new Person("b2-name", "b2-uuid", "b2-username"));
        grouping.setBasis(basisGroup);

        Group exclude = new Group();
        exclude.addMember(new Person("e0-name", "e0-uuid", "e0-username"));
        grouping.setExclude(exclude);

        Group include = new Group();
        include.addMember(new Person("i0-name", "i0-uuid", "i0-username"));
        include.addMember(new Person("i1-name", "i1-uuid", "i1-username"));
        grouping.setInclude(include);

        Group owners = new Group();
        owners.addMember(new Person("o0-name", "o0-uuid", "o0-username"));
        owners.addMember(new Person("o1-name", "o1-uuid", "o1-username"));
        owners.addMember(new Person("o2-name", "o2-uuid", "o2-username"));
        owners.addMember(new Person("o3-name", "o3-uuid", "o3-username"));
        grouping.setOwners(owners);

        grouping.setListservOn(true);
        grouping.setLdapOn(true);

        return grouping;
    }

    //Test data.
    private GroupingAssignment myGroupings() {
        GroupingAssignment mg = new GroupingAssignment();
        List<Grouping> groupings = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            groupings.add(grouping());
            groupings.get(i).setPath(GROUPING + i);
        }

        mg.setGroupingsIn(groupings);
        mg.setGroupingsOwned(groupings);
        mg.setGroupingsOptedOutOf(groupings);
        mg.setGroupingsOptedInTo(groupings);
        mg.setGroupingsToOptOutOf(groupings);
        mg.setGroupingsToOptInTo(groupings);

        return mg;
    }

    @Test
    @WithMockUhUser
    public void rootTest() throws Exception {
        given(groupingsRestController.hello())
                .willReturn(new ResponseEntity<String>(HttpStatus.OK));

        mockMvc.perform(get(API_BASE))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUhUser
    public void getGrouping() throws Exception {
        String uri = API_BASE + GROUPING + "/grouping";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.GET, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(get(uri))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void addAdminTest() throws Exception {
        String uri = API_BASE + "newAdmin/addAdmin";

        given(groupingsRestController.makeApiRequest(ADMIN_USERNAME, uri, HttpMethod.POST, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void deleteAdminTest() throws Exception {
        String uri = API_BASE + "newAdmin/deleteAdmin";

        given(groupingsRestController.makeApiRequest(ADMIN_USERNAME, uri, HttpMethod.POST, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void addByUsernameTest() throws Exception {
        String uri = API_BASE + GROUPING + "/user/addGroupingMemberByUsername";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void addByUuIDTest() throws Exception {
        String uri = API_BASE + GROUPING + "/user/addGroupingMemberByUuid";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void postAddMember() throws Exception {
        String uri_include = API_BASE + GROUPING + "/user/addMemberToIncludeGroup";
        String uri_exclude = API_BASE + GROUPING + "/user/addMemberToExcludeGroup";

        given(groupingsRestController.makeApiRequest(USERNAME, uri_include, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));
        given(groupingsRestController.makeApiRequest(USERNAME, uri_exclude, HttpMethod.POST, List.class))
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
    public void deleteByUsernameTest() throws Exception {
        String uri = API_BASE + GROUPING + "/user/deleteGroupingMemberByUsername";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void deleteByUuIDTest() throws Exception {
        String uri = API_BASE + GROUPING + "/user/deleteGroupingMemberByUuid";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getDeleteMember() throws Exception {
        String uri_include = API_BASE + "grouping/user/deleteMemberFromIncludeGroup";
        String uri_exclude = API_BASE + GROUPING + "/user/deleteMemberFromExcludeGroup";

        given(groupingsRestController.makeApiRequest(USERNAME, uri_include, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));
        given(groupingsRestController.makeApiRequest(USERNAME, uri_exclude, HttpMethod.POST, List.class))
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
        String uri = API_BASE + GROUPING + "/user/assignOwnership";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getRemoveOwnership() throws Exception {
        String uri = API_BASE + GROUPING + "/user/removeOwnership";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getMyGroupings() throws Exception {
        String uri = API_BASE + "groupingAssignment";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.GET, GroupingAssignment.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(get(API_BASE + "groupingAssignment"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUhUser
    public void getSetListserv() throws Exception {
        String uri_true = API_BASE + GROUPING + "/true/setListserv";
        String uri_false = API_BASE + GROUPING + "/false/setListserv";

        given(groupingsRestController.makeApiRequest(USERNAME, uri_true, HttpMethod.POST, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));
        given(groupingsRestController.makeApiRequest(USERNAME, uri_false, HttpMethod.POST, GroupingsServiceResult.class))
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
        String uri_true = API_BASE + GROUPING + "/true/setLdap";
        String uri_false = API_BASE + GROUPING + "/false/setLdap";

        given(groupingsRestController.makeApiRequest(USERNAME, uri_true, HttpMethod.POST, GroupingsServiceResult.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));
        given(groupingsRestController.makeApiRequest(USERNAME, uri_false, HttpMethod.POST, GroupingsServiceResult.class))
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
        String uri_true = API_BASE + GROUPING + "/true/setOptIn";
        String uri_false = API_BASE + GROUPING + "/false/setOptIn";

        given(groupingsRestController.makeApiRequest(USERNAME, uri_true, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));
        given(groupingsRestController.makeApiRequest(USERNAME, uri_false, HttpMethod.POST, List.class))
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
        String uri_true = API_BASE + GROUPING + "/true/setOptOut";
        String uri_false = API_BASE + GROUPING + "/false/setOptOut";

        given(groupingsRestController.makeApiRequest(USERNAME, uri_true, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));
        given(groupingsRestController.makeApiRequest(USERNAME, uri_false, HttpMethod.POST, List.class))
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
        String uri = API_BASE + GROUPING + "/optIn";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(API_BASE + GROUPING + "/optIn")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getOptOut() throws Exception {
        String uri = API_BASE + GROUPING + "/optOut";

        given(groupingsRestController.makeApiRequest(USERNAME, uri, HttpMethod.POST, List.class))
                .willReturn(new ResponseEntity(HttpStatus.OK));

        mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void adminListsTest() throws Exception {
        mockMvc.perform(get(API_BASE + "adminLists"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getAddGrouping() throws Exception {

        mockMvc.perform(post(API_BASE + "fakeGroup/fakeBasis/fakeInclude/fakeExclude/fakeOwners/addGrouping")
                .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUhUser
    public void getDeleteGrouping() throws Exception {
        mockMvc.perform(delete(API_BASE + "fakeGroup/deleteGrouping")
                .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

}

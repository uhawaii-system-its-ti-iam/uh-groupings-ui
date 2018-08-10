package edu.hawaii.its.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hawaii.its.api.service.GroupAttributeService;
import edu.hawaii.its.api.service.GroupingAssignmentService;
import edu.hawaii.its.api.service.GroupingFactoryService;
import edu.hawaii.its.api.service.HelperService;
import edu.hawaii.its.api.service.MemberAttributeService;
import edu.hawaii.its.api.service.MembershipService;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@RunWith(SpringRunner.class)
@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class GroupingsRestControllerTest {

    @Value("${app.iam.request.form}")
    private String requestForm;

    @MockBean
    private GroupAttributeService groupAttributeService;

    @MockBean
    private GroupingAssignmentService groupingAssignmentService;

    @MockBean
    private GroupingFactoryService groupingFactoryService;

    @MockBean
    private HelperService helperService;

    @MockBean
    private MemberAttributeService memberAttributeService;

    @MockBean
    private MembershipService membershipService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
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
            groupings.get(i).setPath("grouping" + i);
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
        MvcResult result = mockMvc.perform(get("/api/groupings/"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("University of Hawaii Groupings API", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUhUser
    public void getGrouping() throws Exception {
        final String grouping = "grouping";
        final String username = "user";

        given(groupingAssignmentService.getGrouping(grouping, username))
                .willReturn(grouping());

        mockMvc.perform(get("/api/groupings/grouping/grouping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("bob"))
                .andExpect(jsonPath("path").value("test:ing:me:bob"))
                .andExpect(jsonPath("listservOn").value("true"))
                .andExpect(jsonPath("basis.members", hasSize(3)))
                .andExpect(jsonPath("basis.members[0].name").value("b0-name"))
                .andExpect(jsonPath("basis.members[0].uuid").value("b0-uuid"))

                .andExpect(jsonPath("basis.members[0].username").value("b0-username"))
                .andExpect(jsonPath("basis.members[1].name").value("b1-name"))
                .andExpect(jsonPath("basis.members[1].uuid").value("b1-uuid"))
                .andExpect(jsonPath("basis.members[1].username").value("b1-username"))
                .andExpect(jsonPath("basis.members[2].name").value("b2-name"))
                .andExpect(jsonPath("basis.members[2].uuid").value("b2-uuid"))
                .andExpect(jsonPath("basis.members[2].username").value("b2-username"))
                .andExpect(jsonPath("exclude.members", hasSize(1)))
                .andExpect(jsonPath("exclude.members[0].name").value("e0-name"))
                .andExpect(jsonPath("exclude.members[0].name").value("e0-name"))
                .andExpect(jsonPath("exclude.members[0].uuid").value("e0-uuid"))
                .andExpect(jsonPath("include.members", hasSize(2)))
                .andExpect(jsonPath("include.members[1].name").value("i1-name"))
                .andExpect(jsonPath("include.members[1].name").value("i1-name"))
                .andExpect(jsonPath("include.members[1].uuid").value("i1-uuid"))
                .andExpect(jsonPath("owners.members", hasSize(4)))
                .andExpect(jsonPath("owners.members[3].name").value("o3-name"))
                .andExpect(jsonPath("owners.members[3].uuid").value("o3-uuid"))
                .andExpect(jsonPath("owners.members[3].username").value("o3-username"))
                .andExpect(jsonPath("composite.members", hasSize(0)));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void addAdminTest() throws Exception {
        given(membershipService.addAdmin("admin", "newAdmin"))
                .willReturn(new GroupingsServiceResult("SUCCESS", "add admin"));

        mockMvc.perform(post("/api/groupings/newAdmin/addAdmin")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("add admin"));

    }

    @Test
    @WithMockUhUser(username = "admin")
    public void deleteAdminTest() throws Exception {
        given(membershipService.deleteAdmin("admin", "newAdmin"))
                .willReturn(new GroupingsServiceResult("SUCCESS", "delete admin"));

        mockMvc.perform(post("/api/groupings/newAdmin/deleteAdmin")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("delete admin"));
    }

    @Test
    @WithMockUhUser
    public void addByUsernameTest() throws Exception {

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        gsrList.add(new GroupingsServiceResult("SUCCESS", "add grouping member by username"));

        given(membershipService.addGroupingMemberByUsername("user", "grouping", "user"))
                .willReturn(gsrList);

        mockMvc.perform(post("/api/groupings/grouping/user/addGroupingMemberByUsername")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("add grouping member by username"));
    }

    @Test
    @WithMockUhUser
    public void addByUuIDTest() throws Exception {

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        gsrList.add(new GroupingsServiceResult("SUCCESS", "add grouping member by uuid"));

        given(membershipService.addGroupingMemberByUuid("user", "grouping", "user"))
                .willReturn(gsrList);

        mockMvc.perform(post("/api/groupings/grouping/user/addGroupingMemberByUuid")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("add grouping member by uuid"));
    }

    @Test
    @WithMockUhUser
    public void getAddMember() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        List<GroupingsServiceResult> gsrList2 = new ArrayList<>();
        gsrList.add(new GroupingsServiceResult("SUCCESS", "add member to include group"));
        gsrList2.add(new GroupingsServiceResult("SUCCESS", "add member to exclude group"));

        given(membershipService.addGroupMemberByUsername(username, grouping + ":include", username))
                .willReturn(gsrList);
        given(membershipService.addGroupMemberByUsername(username, grouping + ":exclude", username))
                .willReturn(gsrList2);

        mockMvc.perform(post("/api/groupings/grouping/user/addMemberToIncludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("add member to include group"));

        mockMvc.perform(post("/api/groupings/grouping/user/addMemberToExcludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("add member to exclude group"));
    }

    @Test
    @WithMockUhUser
    public void deleteByUsernameTest() throws Exception {

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        gsrList.add(new GroupingsServiceResult("SUCCESS", "delete grouping member by username"));

        //new GroupingsServiceResult("SUCCESS", "delete grouping member by username")

        given(membershipService.deleteGroupingMemberByUsername("user", "grouping", "user"))
                .willReturn(gsrList);

        mockMvc.perform(post("/api/groupings/grouping/user/deleteGroupingMemberByUsername")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("delete grouping member by username"));
    }

    @Test
    @WithMockUhUser
    public void deleteByUuIDTest() throws Exception {

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        gsrList.add(new GroupingsServiceResult("SUCCESS", "delete grouping member by uuid"));

        //new GroupingsServiceResult("SUCCESS", "delete grouping member by username")

        given(membershipService.deleteGroupingMemberByUuid("user", "grouping", "user"))
                .willReturn(gsrList);

        mockMvc.perform(post("/api/groupings/grouping/user/deleteGroupingMemberByUuid")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("delete grouping member by uuid"));
    }

    @Test
    @WithMockUhUser
    public void getDeleteMember() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "delete member from include group");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "delete member from exclude group");

        given(membershipService.deleteGroupMemberByUsername(username, grouping + ":include", username))
                .willReturn(gsr);
        given(membershipService.deleteGroupMemberByUsername(username, grouping + ":exclude", username))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/user/deleteMemberFromIncludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("delete member from include group"));

        mockMvc.perform(post("/api/groupings/grouping/user/deleteMemberFromExcludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("delete member from exclude group"));
    }

    @Test
    @WithMockUhUser
    public void getAssignOwnership() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        GroupingsServiceResult gsr;

        gsr = new GroupingsServiceResult("SUCCESS", "give user ownership of grouping");

        given(memberAttributeService.assignOwnership(grouping, username, username))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/user/assignOwnership")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("give user ownership of grouping"));
    }

    @Test
    @WithMockUhUser
    public void getRemoveOwnership() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        GroupingsServiceResult gsr;

        gsr = new GroupingsServiceResult("SUCCESS", "remove user's ownership privilege for grouping");

        given(memberAttributeService.removeOwnership(grouping, username, username))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/user/removeOwnership")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("remove user's ownership privilege for grouping"));
    }

    @Test
    @WithMockUhUser
    public void getMyGroupings() throws Exception {
        ObjectMapper om = new ObjectMapper();
        final String username = "user";
        List<Grouping> groupings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            groupings.add(grouping());
            groupings.get(i).setPath("grouping" + i);
        }

        given(groupingAssignmentService.getGroupingAssignment(username))
                .willReturn(myGroupings());

        String mvcResult = mockMvc.perform(get("/api/groupings/groupingAssignment"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GroupingAssignment mg = om.readValue(mvcResult, GroupingAssignment.class);

        Assert.assertTrue(mg.getGroupingsIn().get(0).getName().equals(groupings.get(0).getName()));
        Assert.assertTrue(mg.getGroupingsIn().get(0).getPath().equals(groupings.get(0).getPath()));
        Assert.assertTrue(
                mg.getGroupingsIn().get(0).getOwners().getNames().equals(groupings.get(0).getOwners().getNames()));
        Assert.assertTrue(mg.getGroupingsIn().get(0).getOwners().getUsernames()
                .equals(groupings.get(0).getOwners().getUsernames()));
        Assert.assertTrue(
                mg.getGroupingsIn().get(0).getOwners().getUuids().equals(groupings.get(0).getOwners().getUuids()));
        Assert.assertTrue(mg.getGroupingsOwned().get(0).getName().equals(groupings.get(0).getName()));
        Assert.assertTrue(mg.getGroupingsOwned().get(0).getPath().equals(groupings.get(0).getPath()));
        Assert.assertTrue(
                mg.getGroupingsOwned().get(0).getOwners().getNames().equals(groupings.get(0).getOwners().getNames()));
        Assert.assertTrue(mg.getGroupingsOwned().get(0).getOwners().getUsernames()
                .equals(groupings.get(0).getOwners().getUsernames()));
        Assert.assertTrue(
                mg.getGroupingsOwned().get(0).getOwners().getUuids().equals(groupings.get(0).getOwners().getUuids()));
        Assert.assertTrue(mg.getGroupingsToOptInTo().get(0).getName().equals(groupings.get(0).getName()));
        Assert.assertTrue(mg.getGroupingsToOptInTo().get(0).getPath().equals(groupings.get(0).getPath()));
        Assert.assertTrue(mg.getGroupingsToOptInTo().get(0).getOwners().getNames()
                .equals(groupings.get(0).getOwners().getNames()));
        Assert.assertTrue(mg.getGroupingsToOptInTo().get(0).getOwners().getUsernames()
                .equals(groupings.get(0).getOwners().getUsernames()));
        Assert.assertTrue(mg.getGroupingsToOptInTo().get(0).getOwners().getUuids()
                .equals(groupings.get(0).getOwners().getUuids()));
        Assert.assertTrue(mg.getGroupingsToOptOutOf().get(0).getName().equals(groupings.get(0).getName()));
        Assert.assertTrue(mg.getGroupingsToOptOutOf().get(0).getPath().equals(groupings.get(0).getPath()));
        Assert.assertTrue(mg.getGroupingsToOptOutOf().get(0).getOwners().getNames()
                .equals(groupings.get(0).getOwners().getNames()));
        Assert.assertTrue(mg.getGroupingsToOptOutOf().get(0).getOwners().getUsernames()
                .equals(groupings.get(0).getOwners().getUsernames()));
        Assert.assertTrue(mg.getGroupingsToOptOutOf().get(0).getOwners().getUuids()
                .equals(groupings.get(0).getOwners().getUuids()));
        Assert.assertTrue(mg.getGroupingsOptedInTo().get(0).getName().equals(groupings.get(0).getName()));
        Assert.assertTrue(mg.getGroupingsOptedInTo().get(0).getPath().equals(groupings.get(0).getPath()));
        Assert.assertTrue(mg.getGroupingsOptedInTo().get(0).getOwners().getNames()
                .equals(groupings.get(0).getOwners().getNames()));
        Assert.assertTrue(mg.getGroupingsOptedInTo().get(0).getOwners().getUsernames()
                .equals(groupings.get(0).getOwners().getUsernames()));
        Assert.assertTrue(mg.getGroupingsOptedInTo().get(0).getOwners().getUuids()
                .equals(groupings.get(0).getOwners().getUuids()));
        Assert.assertTrue(mg.getGroupingsOptedOutOf().get(0).getName().equals(groupings.get(0).getName()));
        Assert.assertTrue(mg.getGroupingsOptedOutOf().get(0).getPath().equals(groupings.get(0).getPath()));
        Assert.assertTrue(mg.getGroupingsOptedOutOf().get(0).getOwners().getNames()
                .equals(groupings.get(0).getOwners().getNames()));
        Assert.assertTrue(mg.getGroupingsOptedOutOf().get(0).getOwners().getUsernames()
                .equals(groupings.get(0).getOwners().getUsernames()));
        Assert.assertTrue(mg.getGroupingsOptedOutOf().get(0).getOwners().getUuids()
                .equals(groupings.get(0).getOwners().getUuids()));
    }

    @Test
    @WithMockUhUser
    public void getSetListserv() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "listserv has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "listserv has been removed from grouping");

        given(groupAttributeService.changeListservStatus(grouping, username, true))
                .willReturn(gsr);
        given(groupAttributeService.changeListservStatus(grouping, username, false))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/true/setListserv")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("listserv has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/false/setListserv")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("listserv has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getSetLdap() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "LDAP has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "LDAP has been removed from grouping");

        given(groupAttributeService.changeLdapStatus(grouping, username, true))
                .willReturn(gsr);
        given(groupAttributeService.changeLdapStatus(grouping, username, false))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/true/setLdap")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("LDAP has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/false/setLdap")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("LDAP has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getSetOptIn() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        List<GroupingsServiceResult> gsResults = new ArrayList<>();
        List<GroupingsServiceResult> gsResults2 = new ArrayList<>();
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "OptIn has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "OptIn has been removed from grouping");
        gsResults.add(gsr);
        gsResults2.add(gsr2);

        given(groupAttributeService.changeOptInStatus(grouping, username, true))
                .willReturn(gsResults);
        given(groupAttributeService.changeOptInStatus(grouping, username, false))
                .willReturn(gsResults2);

        mockMvc.perform(post("/api/groupings/grouping/true/setOptIn")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("OptIn has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/false/setOptIn")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("OptIn has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getSetOptOut() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        List<GroupingsServiceResult> gsResults = new ArrayList<>();
        List<GroupingsServiceResult> gsResults2 = new ArrayList<>();
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "OptOut has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "OptOut has been removed from grouping");
        gsResults.add(gsr);
        gsResults2.add(gsr2);

        given(groupAttributeService.changeOptOutStatus(grouping, username, true))
                .willReturn(gsResults);
        given(groupAttributeService.changeOptOutStatus(grouping, username, false))
                .willReturn(gsResults2);

        mockMvc.perform(post("/api/groupings/grouping/true/setOptOut")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("OptOut has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/false/setOptOut")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("OptOut has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getOptIn() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        List<GroupingsServiceResult> gsr = new ArrayList<>();

        gsr.add(new GroupingsServiceResult("SUCCESS", "delete member from exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add member to include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add self-opted attribute to include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove self-opted attribute to exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for include group"));

        given(membershipService.optIn(username, grouping))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/optIn")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("delete member from exclude group"))
                .andExpect(jsonPath("$[1].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[1].action").value("add member to include group"))
                .andExpect(jsonPath("$[2].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[2].action").value("add self-opted attribute to include group"))
                .andExpect(jsonPath("$[3].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[3].action").value("remove self-opted attribute to exclude group"))
                .andExpect(jsonPath("$[4].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[4].action").value("update last-modified attribute for exclude group"))
                .andExpect(jsonPath("$[5].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[5].action").value("update last-modified attribute for include group"));
    }

    @Test
    @WithMockUhUser
    public void getOptOut() throws Exception {
        final String grouping = "grouping";
        final String username = "user";
        List<GroupingsServiceResult> gsr = new ArrayList<>();

        gsr.add(new GroupingsServiceResult("SUCCESS", "delete member from include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add member to exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add self-opted attribute to exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove self-opted attribute to include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for exclude group"));

        given(membershipService.optOut(username, grouping))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/optOut")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("delete member from include group"))
                .andExpect(jsonPath("$[1].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[1].action").value("add member to exclude group"))
                .andExpect(jsonPath("$[2].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[2].action").value("add self-opted attribute to exclude group"))
                .andExpect(jsonPath("$[3].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[3].action").value("remove self-opted attribute to include group"))
                .andExpect(jsonPath("$[4].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[4].action").value("update last-modified attribute for include group"))
                .andExpect(jsonPath("$[5].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[5].action").value("update last-modified attribute for exclude group"));
    }

    @Test
    @WithMockUhUser(username = "admin")
    public void adminListsTest() throws Exception {
        mockMvc.perform(get("/api/groupings/adminLists"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUhUser
    public void getAddGrouping() throws Exception {

        mockMvc.perform(post("/api/groupings/fakeGroup/fakeBasis/fakeIncldue/fakeExclude/fakeOwners/addGrouping")
                .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUhUser
    public void getDeleteGrouping() throws Exception {
        mockMvc.perform(delete("/api/groupings/fakeGroup/deleteGrouping")
                .with(csrf()))
                .andExpect(status().is5xxServerError());
    }
}

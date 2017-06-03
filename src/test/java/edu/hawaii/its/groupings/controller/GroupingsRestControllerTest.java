package edu.hawaii.its.groupings.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.hawaii.its.groupings.api.type.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.groupings.api.GroupingsService;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsRestControllerTest {

    @Value("${app.iam.request.form}")
    private String requestForm;

    @MockBean
    private GroupingsService groupingsService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUhUser
    public void getGrouping() throws Exception {
        final String grouping = "grouping";
        final String username = "username";

        given(groupingsService.getGrouping(grouping, username))
                .willReturn(grouping());

        mockMvc.perform(get("/api/groupings/grouping/username/grouping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("bob"))
                .andExpect(jsonPath("path").value("test:ing:me:bob"))
                .andExpect(jsonPath("hasListserv").value("true"))
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
                .andExpect(jsonPath("basisPlusIncludeMinusExclude.members", hasSize(0)));
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

        grouping.setListserveOn(true);

        return grouping;
    }

    //Test data.
    private MyGroupings myGroupings() {
        MyGroupings mg = new MyGroupings();
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
    public void getAddMember() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "add member to include group");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "add member to exclude group");

        given(groupingsService.addMemberAs(username, grouping + ":include", username))
                .willReturn(gsr);
        given(groupingsService.addMemberAs(username, grouping + ":exclude", username))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/username/username/addMemberToIncludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("add member to include group"));

        mockMvc.perform(post("/api/groupings/grouping/username/username/addMemberToExcludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("add member to exclude group"));
    }

    @Test
    @WithMockUhUser
    public void getDeleteMember() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "delete member from include group");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "delete member from exclude group");

        given(groupingsService.deleteMemberAs(username, grouping + ":include", username))
                .willReturn(gsr);
        given(groupingsService.deleteMemberAs(username, grouping + ":exclude", username))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/username/username/deleteMemberFromIncludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("delete member from include group"));

        mockMvc.perform(post("/api/groupings/grouping/username/username/deleteMemberFromExcludeGroup")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("delete member from exclude group"));
    }

    @Test
    @WithMockUhUser
    public void getAssignOwnership() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        List<GroupingsServiceResult> gsr = new ArrayList<>();

        gsr.add(new GroupingsServiceResult("SUCCESS", "give user ownership privileges for grouping:basis"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "give user ownership privileges for grouping:basis+include"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "give user ownership privileges for grouping:exclude"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "give user ownership privileges for grouping:include"));

        given(groupingsService.assignOwnership(grouping, username, username))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/username/username/assignOwnership")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("give user ownership privileges for grouping:basis"))
                .andExpect(jsonPath("$[1].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[1].action").value("give user ownership privileges for grouping:basis+include"))
                .andExpect(jsonPath("$[2].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[2].action").value("give user ownership privileges for grouping:exclude"))
                .andExpect(jsonPath("$[3].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[3].action").value("give user ownership privileges for grouping:include"));
    }

    @Test
    @WithMockUhUser
    public void getRemoveOwnership() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        List<GroupingsServiceResult> gsr = new ArrayList<>();

        gsr.add(new GroupingsServiceResult("SUCCESS", "remove ownership privileges for user from grouping:basis"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove ownership privileges for user from grouping:basis+include"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove ownership privileges for user from grouping:exclude"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove ownership privileges for user from grouping:include"));

        given(groupingsService.removeOwnership(grouping, username, username))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/username/username/removeOwnership")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("remove ownership privileges for user from grouping:basis"))
                .andExpect(jsonPath("$[1].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[1].action").value("remove ownership privileges for user from grouping:basis+include"))
                .andExpect(jsonPath("$[2].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[2].action").value("remove ownership privileges for user from grouping:exclude"))
                .andExpect(jsonPath("$[3].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[3].action").value("remove ownership privileges for user from grouping:include"));
    }

//    @Test
//    @WithMockUhUser
//    public void getMyGroupings() throws Exception {
//        final String username = "username";
//        List<Grouping> groupings = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            groupings.add(grouping());
//            groupings.get(i).setPath("grouping" + i);
//        }
//
//        given(groupingsService.getMyGroupings(username))
//                .willReturn(myGroupings());
//
//        mockMvc.perform(get("/api/groupings/username/myGroupings"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("groupingsIn").value(groupings))
//                .andExpect(jsonPath("groupingsOwned").value(groupings))
//                .andExpect(jsonPath("groupingsOptedOutOf").value(groupings))
//                .andExpect(jsonPath("groupingsOptedInTo").value(groupings))
//                .andExpect(jsonPath("groupingsToOptOutOf").value(groupings))
//                .andExpect(jsonPath("groupingsToOptInTo").value(groupings));
//
//
//    }

    @Test
    @WithMockUhUser
    public void getSetListserve() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "listserve has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "listserve has been removed from grouping");

        given(groupingsService.changeListServeStatus(grouping, username, true))
                .willReturn(gsr);
        given(groupingsService.changeListServeStatus(grouping, username, false))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/username/true/setListserve")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("listserve has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/username/false/setListserve")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("listserve has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getSetOptIn() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "OptIn has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "OptIn has been removed from grouping");

        given(groupingsService.changeOptInStatus(grouping, username, true))
                .willReturn(gsr);
        given(groupingsService.changeOptInStatus(grouping, username, false))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/username/true/setOptIn")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("OptIn has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/username/false/setOptIn")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("OptIn has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getSetOptOut() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        GroupingsServiceResult gsr = new GroupingsServiceResult("SUCCESS", "OptOut has been added to grouping");
        GroupingsServiceResult gsr2 = new GroupingsServiceResult("SUCCESS", "OptOut has been removed from grouping");

        given(groupingsService.changeOptOutStatus(grouping, username, true))
                .willReturn(gsr);
        given(groupingsService.changeOptOutStatus(grouping, username, false))
                .willReturn(gsr2);

        mockMvc.perform(post("/api/groupings/grouping/username/true/setOptOut")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("OptOut has been added to grouping"));

        mockMvc.perform(post("/api/groupings/grouping/username/false/setOptOut")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(jsonPath("action").value("OptOut has been removed from grouping"));
    }

    @Test
    @WithMockUhUser
    public void getOptIn() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        List<GroupingsServiceResult> gsr = new ArrayList<>();

        gsr.add(new GroupingsServiceResult("SUCCESS", "delete member from exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add member to include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add self-opted attribute to include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove self-opted attribute to exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for include group"));

        given(groupingsService.optIn(username, grouping))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/username/optIn")
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
        final String username = "username";
        List<GroupingsServiceResult> gsr = new ArrayList<>();

        gsr.add(new GroupingsServiceResult("SUCCESS", "delete member from include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add member to exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "add self-opted attribute to exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "remove self-opted attribute to include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for exclude group"));

        given(groupingsService.optOut(username, grouping))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/username/optOut")
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
    @WithMockUhUser
    public void getCancelOptOut() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        List<GroupingsServiceResult> gsr = new ArrayList<>();
        gsr.add(new GroupingsServiceResult("SUCCESS", "delete memeber from exclude group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for exclude group"));

        given(groupingsService.cancelOptOut(grouping, username))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/username/cancelOptOut")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("delete memeber from exclude group"))
                .andExpect(jsonPath("$[1].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[1].action").value("update last-modified attribute for exclude group"));
    }

    @Test
    @WithMockUhUser
    public void getCancelOptIn() throws Exception {
        final String grouping = "grouping";
        final String username = "username";
        List<GroupingsServiceResult> gsr = new ArrayList<>();
        gsr.add(new GroupingsServiceResult("SUCCESS", "delete memeber from include group"));
        gsr.add(new GroupingsServiceResult("SUCCESS", "update last-modified attribute for include group"));

        given(groupingsService.cancelOptIn(grouping, username))
                .willReturn(gsr);

        mockMvc.perform(post("/api/groupings/grouping/username/cancelOptIn")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[0].action").value("delete memeber from include group"))
                .andExpect(jsonPath("$[1].resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$[1].action").value("update last-modified attribute for include group"));
    }

    @Test
    @WithMockUhUser
    public void getAddGrouping() throws Exception {
        mockMvc.perform(get("/api/groupings/addGrouping"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URI.create(requestForm).toString()));
    }

    @Test
    @WithMockUhUser
    public void getDeleteGrouping() throws Exception {
        mockMvc.perform(get("/api/groupings/deleteGrouping"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URI.create(requestForm).toString()));
    }
}

package edu.hawaii.its.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hawaii.its.api.service.GroupAttributeService;
import edu.hawaii.its.api.service.GroupingAssignmentService;
import edu.hawaii.its.api.service.GroupingFactoryService;
import edu.hawaii.its.api.service.HelperService;
import edu.hawaii.its.api.service.MemberAttributeService;
import edu.hawaii.its.api.service.MembershipService;
import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsHTTPException;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupingsRestController {

    @Value("${groupings.api.test.student_test_username}")
    private String STUDENT_TEST_USERNAME;

    @Value("${groupings.api.test.grouping_many}")
    private String GROUPING;

    @Value("${groupings.api.test.grouping_many_include}")
    private String GROUPING_INCLUDE;

    @Value("${groupings.api.test.grouping_many_exclude}")
    private String GROUPING_EXCLUDE;

    @Value("${groupings.api.test.grouping_many_indirect_basis}")
    private String GROUPING_BASIS;

    @Value("${groupings.api.test.grouping_store_empty}")
    private String GROUPING_STORE_EMPTY;

    @Value("${groupings.api.test.grouping_store_empty_include}")
    private String GROUPING_STORE_EMPTY_INCLUDE;

    @Value("${groupings.api.test.grouping_store_empty_exclude}")
    private String GROUPING_STORE_EMPTY_EXCLUDE;

    @Value("${groupings.api.test.grouping_true_empty}")
    private String GROUPING_TRUE_EMPTY;

    @Value("${groupings.api.test.grouping_true_empty_include}")
    private String GROUPING_TRUE_EMPTY_INCLUDE;

    @Value("${groupings.api.test.grouping_true_empty_exclude}")
    private String GROUPING_TRUE_EMPTY_EXCLUDE;

    @Value("${groupings.api.basis}")
    private String BASIS;

    @Value("${groupings.api.basis_plus_include}")
    private String BASIS_PLUS_INCLUDE;

    @Value("${groupings.api.test.usernames}")
    private String[] tst;

    @Value("${groupings.api.test.names}")
    private String[] tstName;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Value("${grouperClient.webService.login}")
    private String API_ACCOUNT;

    @Autowired
    private GroupAttributeService groupAttributeService;

    @Autowired
    private GroupingAssignmentService groupingAssignmentService;

    @Autowired
    private GroupingFactoryService groupingFactoryService;

    @Autowired
    private HelperService helperService;

    @Autowired
    private MemberAttributeService memberAttributeService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private GroupingsRestController gc;

    @Autowired
    public Environment env; // Just for the settings check.

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @PostConstruct
    public void init() {
        Assert.hasLength(env.getProperty("grouperClient.webService.url"),
                "property 'grouperClient.webService.url' is required");
        Assert.hasLength(env.getProperty("grouperClient.webService.login"),
                "property 'grouperClient.webService.login' is required");
        Assert.hasLength(env.getProperty("grouperClient.webService.password"),
                "property 'grouperClient.webService.password' is required");
    }

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        //put in include
        membershipService.addGroupMemberByUsername(tst[0], GROUPING_INCLUDE, tst[0]);
        membershipService.addGroupMemberByUsername(tst[0], GROUPING_INCLUDE, tst[1]);
        membershipService.addGroupMemberByUsername(tst[0], GROUPING_INCLUDE, tst[2]);

        //add to exclude
        membershipService.addGroupMemberByUsername(tst[0], GROUPING_EXCLUDE, tst[3]);

        //remove from exclude
        membershipService.addGroupMemberByUsername(tst[0], GROUPING_INCLUDE, tst[4]);
        membershipService.addGroupMemberByUsername(tst[0], GROUPING_INCLUDE, tst[5]);

        groupAttributeService.changeOptOutStatus(GROUPING, tst[0], true);
        groupAttributeService.changeOptInStatus(GROUPING, tst[0], true);
    }

    @Test
    public void testConstruction() {
        assertNotNull(groupAttributeService);
        assertNotNull(groupingAssignmentService);
        assertNotNull(groupingFactoryService);
        assertNotNull(helperService);
        assertNotNull(memberAttributeService);
        assertNotNull(membershipService);
        assertNotNull(gc);
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void assignAndRemoveOwnershipTest() throws Exception {

        Grouping g = mapGrouping(GROUPING);

        assertFalse(g.getOwners().getUsernames().contains(tst[1]));

        mapGSR("/api/groupings/" + GROUPING + "/" + tst[1] + "/assignOwnership");

        g = mapGrouping(GROUPING);

        assertTrue(g.getOwners().getUsernames().contains(tst[1]));

        mapGSR("/api/groupings/" + GROUPING + "/" + tst[1] + "/removeOwnership");

        g = mapGrouping(GROUPING);

        assertFalse(g.getOwners().getUsernames().contains(tst[1]));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void addMemberTest() throws Exception {

        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));

        mapGSRs("/api/groupings/" + GROUPING + "/" + tst[3] + "/addMemberToIncludeGroup");

        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));
        //tst[3] is in basis and will go into include
        assertTrue(memberAttributeService.isMember(GROUPING_INCLUDE, tst[3]));

        //add tst[3] back to exclude
        mapGSRs("/api/groupings/" + GROUPING + "/" + tst[3] + "/addMemberToExcludeGroup");
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));

        //add tst[3] to Grouping
        mapGSRs("/api/groupings/" + GROUPING + "/" + tst[3] + "/addGroupingMemberByUsername");
        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));
        //tst[3] is in basis, so will not go into include
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, tst[3]));

        //todo add other test cases
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void deleteMemberTest() throws Exception {

        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));
        mapGSR("/api/groupings/" + GROUPING + "/" + tst[3] + "/deleteMemberFromExcludeGroup");

        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));
        assertTrue(memberAttributeService.isMember(GROUPING, tst[3]));

        assertTrue(memberAttributeService.isMember(GROUPING_INCLUDE, tst[1]));
        mapGSR("/api/groupings/" + GROUPING + "/" + tst[1] + "/deleteMemberFromIncludeGroup");

        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[1]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, tst[1]));

        assertTrue(memberAttributeService.isMember(GROUPING, tst[2]));
        assertTrue(memberAttributeService.isMember(GROUPING, tst[5]));
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, tst[5]));
        assertTrue(memberAttributeService.isMember(GROUPING_INCLUDE, tst[2]));
        mapGSRs("/api/groupings/" + GROUPING + "/" + tst[2] + "/deleteGroupingMemberByUsername");
        mapGSRs("/api/groupings/" + GROUPING + "/" + tst[5] + "/deleteGroupingMemberByUsername");

        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[5]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, tst[2]));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void getGroupingTest() throws Exception {
        Grouping grouping = mapGrouping(GROUPING);
        Group basis = grouping.getBasis();
        Group composite = grouping.getComposite();
        Group exclude = grouping.getExclude();
        Group include = grouping.getInclude();

        //basis
        assertTrue(basis.getUsernames().contains(tst[3]));
        assertTrue(basis.getUsernames().contains(tst[4]));
        assertTrue(basis.getUsernames().contains(tst[5]));
        assertTrue(basis.getNames().contains(tstName[3]));
        assertTrue(basis.getNames().contains(tstName[4]));
        assertTrue(basis.getNames().contains(tstName[5]));

        //composite
        assertTrue(composite.getUsernames().contains(tst[0]));
        assertTrue(composite.getUsernames().contains(tst[1]));
        assertTrue(composite.getUsernames().contains(tst[2]));
        assertTrue(composite.getUsernames().contains(tst[4]));
        assertTrue(composite.getUsernames().contains(tst[5]));
        assertTrue(composite.getNames().contains(tstName[0]));
        assertTrue(composite.getNames().contains(tstName[1]));
        assertTrue(composite.getNames().contains(tstName[2]));
        assertTrue(composite.getNames().contains(tstName[4]));
        assertTrue(composite.getNames().contains(tstName[5]));

        //exclude
        assertTrue(exclude.getUsernames().contains(tst[3]));
        assertTrue(exclude.getNames().contains(tstName[3]));

        //include
        assertTrue(include.getUsernames().contains(tst[0]));
        assertTrue(include.getUsernames().contains(tst[1]));
        assertTrue(include.getUsernames().contains(tst[2]));
        assertTrue(include.getNames().contains(tstName[0]));
        assertTrue(include.getNames().contains(tstName[1]));
        assertTrue(include.getNames().contains(tstName[2]));

        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
        mapGSR("/api/groupings/" + grouping.getPath() + "/" + tst[5] + "/assignOwnership");
        grouping = mapGrouping(GROUPING);

        assertTrue(grouping.getOwners().getNames().contains(tstName[5]));
        mapGSR("/api/groupings/" + grouping.getPath() + "/" + tst[5] + "/removeOwnership");
        grouping = mapGrouping(GROUPING);

        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
    }

    @Test
    @WithMockUhUser(username = "iamtst05")
    public void groupingsAssignmentEmptyTest() throws Exception {
        GroupingAssignment groupings = mapGroupingAssignment();

        assertEquals(groupings.getGroupingsIn().size(), groupings.getGroupingsToOptOutOf().size());

        for(Grouping grouping : groupings.getGroupingsIn()) {
            mapGSRs("/api/groupings/" + grouping.getPath() + "/optOut");
        }

        groupings = mapGroupingAssignment();

        assertEquals(0, groupings.getGroupingsIn().size());
        assertEquals(0, groupings.getGroupingsToOptOutOf().size());
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void groupingAssignmentTest() throws Exception {
        GroupingAssignment groupings = mapGroupingAssignment();

        boolean inGrouping = false;
        for (Grouping grouping : groupings.getGroupingsIn()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertTrue(inGrouping);

        boolean canOptin = false;
        for (Grouping grouping : groupings.getGroupingsToOptInTo()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                canOptin = true;
                break;
            }
        }
        assertFalse(canOptin);

        boolean canOptOut = false;
        for (Grouping grouping : groupings.getGroupingsToOptOutOf()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                canOptOut = true;
                break;
            }
        }
        assertTrue(canOptOut);

        boolean ownsGrouping = false;
        for (Grouping grouping : groupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertTrue(ownsGrouping);

    }

    @Test
    @WithMockUhUser(username = "iamtst04")
    public void myGroupingsTest2() throws Exception {
        GroupingAssignment groupings = mapGroupingAssignment();

        boolean inGrouping = false;
        for (Grouping grouping : groupings.getGroupingsIn()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertFalse(inGrouping);

        boolean ownsGrouping = false;
        for (Grouping grouping : groupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertFalse(ownsGrouping);
    }

    @Test
    @WithMockUhUser(username = "iamtst04")
    public void myGroupingsTest3() throws Exception {
        boolean optedIn = false;

        GroupingAssignment tst4Groupings = mapGroupingAssignment();
        assertEquals(tst4Groupings.getGroupingsOptedInTo().size(), 0);
        mapGSRs("/api/groupings/" + GROUPING + "/optIn");
        tst4Groupings = mapGroupingAssignment();
        for (Grouping grouping : tst4Groupings.getGroupingsOptedInTo()) {
            if (grouping.getPath().contains(GROUPING)) {
                optedIn = true;
            }
        }
        //in basis
        assertFalse(optedIn);
    }

    @Test
    @WithMockUhUser(username = "iamtst06")
    public void myGroupingsTest4() throws Exception {
        boolean optedOut = false;

        GroupingAssignment tst5Groupings = mapGroupingAssignment();
        assertEquals(tst5Groupings.getGroupingsOptedOutOf().size(), 0);
        mapGSRs("/api/groupings/" + GROUPING + "/optOut");
        tst5Groupings = mapGroupingAssignment();

        for (Grouping grouping : tst5Groupings.getGroupingsOptedOutOf()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                optedOut = true;
            }
        }
        assertTrue(optedOut);

        membershipService.deleteGroupMemberByUsername(tst[0], GROUPING_EXCLUDE, tst[5]);
    }

    @Test
    @WithMockUhUser(username = "iamtst04")
    public void optInTest() throws Exception {
        //tst[3] is not in Grouping, but is in basis and exclude
        assertFalse(memberAttributeService.isMember(GROUPING, tst[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, tst[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, tst[3]));

        //tst[3] opts into Grouping
        mapGSRs("/api/groupings/" + GROUPING + "/optIn");

        //tst[3] is now in composite, still in basis and not in exclude
        assertTrue(memberAttributeService.isMember(GROUPING, tst[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, tst[3]));
        assertFalse(memberAttributeService.isSelfOpted(GROUPING_EXCLUDE, tst[3]));
    }

    @Test
    @WithMockUhUser(username = "iamtst06")
    public void optOutTest() throws Exception {
        //tst[5] is in the Grouping and in the basis
        assertTrue(memberAttributeService.isMember(GROUPING, tst[5]));
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, tst[5]));

        //tst[5] opts out of Grouping
        mapGSRs("/api/groupings/" + GROUPING + "/optOut");

        //tst[5] is now in exclude, not in include or Grouping
        assertTrue(memberAttributeService.isSelfOpted(GROUPING_EXCLUDE, tst[5]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, tst[5]));
        assertFalse(memberAttributeService.isMember(GROUPING, tst[5]));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void changeListservStatusTest() throws Exception {
        assertTrue(groupAttributeService.hasListserv(GROUPING));

        mapGSR("/api/groupings/" + GROUPING + "/false/setListserv");

        assertFalse(groupAttributeService.hasListserv(GROUPING));

        mapGSR("/api/groupings/" + GROUPING + "/true/setListserv");
        assertTrue(groupAttributeService.hasListserv(GROUPING));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void changeLdapStatusTest() throws Exception {
        assertTrue(groupAttributeService.hasLdap(GROUPING));

        mapGSR("/api/groupings/" + GROUPING + "/false/setLDAP");

        assertFalse(groupAttributeService.hasLdap(GROUPING));

        mapGSR("/api/groupings/" + GROUPING + "/true/setLDAP");

        assertTrue(groupAttributeService.hasLdap(GROUPING));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void changeOptInTest() throws Exception {
        assertTrue(groupAttributeService.optInPermission(GROUPING));

        mapGSRs("/api/groupings/" + GROUPING + "/false/setOptIn");
        assertFalse(groupAttributeService.optInPermission(GROUPING));

        mapGSRs("/api/groupings/" + GROUPING + "/true/setOptIn");
        assertTrue(groupAttributeService.optInPermission(GROUPING));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void changeOptOutTest() throws Exception {
        assertTrue(groupAttributeService.optOutPermission(GROUPING));

        mapGSRs("/api/groupings/" + GROUPING + "/false/setOptOut");
        assertFalse(groupAttributeService.optOutPermission(GROUPING));

        mapGSRs("/api/groupings/" + GROUPING + "/true/setOptOut");
        assertTrue(groupAttributeService.optOutPermission(GROUPING));
    }

    @Test
    @WithMockUhUser(username = "aaronvil")
    public void aaronTest() throws Exception {
        //This test often fails because the test server is very slow.
        //Because the server caches some results and gets quicker the more times
        //it is run, we let it run a few times if it starts failing

        int i = 0;
        while (i < 5) {
            try {
                GroupingAssignment aaronsGroupings = mapGroupingAssignment();
                assertNotNull(aaronsGroupings);
                break;
            } catch (AssertionError ae) {
                i++;
            }
        }
        assertTrue(i < 5);
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void getEmptyGroupingTest() throws Exception {

        Grouping storeEmpty = mapGrouping(GROUPING_STORE_EMPTY);
        Grouping trueEmpty = mapGrouping(GROUPING_TRUE_EMPTY);

        assertTrue(storeEmpty.getBasis().getMembers().size() == 1);
        assertTrue(storeEmpty.getComposite().getMembers().size() == 0);
        assertTrue(storeEmpty.getExclude().getMembers().size() == 0);
        assertTrue(storeEmpty.getInclude().getMembers().size() == 0);
        assertTrue(storeEmpty.getOwners().getUsernames().contains(tst[0]));

        assertTrue(trueEmpty.getBasis().getMembers().size() == 0);
        assertTrue(trueEmpty.getComposite().getMembers().size() == 0);
        assertTrue(trueEmpty.getExclude().getMembers().size() == 0);
        assertTrue(trueEmpty.getInclude().getMembers().size() == 0);
        assertTrue(trueEmpty.getOwners().getUsernames().contains(tst[0]));

    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void adminListsFailTest() throws Exception {
        AdminListsHolder infoFail = mapAdminListsHolder();

        assertEquals(infoFail.getAdminGroup().getMembers().size(), 0);
        assertEquals(infoFail.getAllGroupings().size(), 0);
    }

    @Test
    @WithMockUhUser(username = "_groupings_api_2")
    public void adminListsPassTest() throws Exception {
        AdminListsHolder infoSuccess = mapAdminListsHolder();

        //STUDENT_TEST_USERNAME can be replaced with any account that has admin access
        assertTrue(infoSuccess.getAdminGroup().getUsernames().contains(STUDENT_TEST_USERNAME));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void addDeleteAdminTest() throws Exception {
        GroupingsServiceResult addAdminResults;
        GroupingsServiceResult deleteAdminResults;

        try {
            //            addAdminResults = gc.addAdmin(tst[0], tst[0]).getBody();
            addAdminResults = mapGSR("/api/groupings/" + tst[0] + "/addAdmin");
        } catch (GroupingsHTTPException ghe) {
            addAdminResults = new GroupingsServiceResult();
            addAdminResults.setResultCode(FAILURE);
        }

        try {
            //            deleteAdminResults = gc.deleteAdmin(tst[0], tst[0]).getBody();
            deleteAdminResults = mapGSR("/api/groupings/" + tst[0] + "/deleteAdmin");
        } catch (GroupingsHTTPException ghe) {
            deleteAdminResults = new GroupingsServiceResult();
            deleteAdminResults.setResultCode(FAILURE);
        }

        assertTrue(addAdminResults.getResultCode().startsWith(FAILURE));
        assertTrue(deleteAdminResults.getResultCode().startsWith(FAILURE));
    }

    ///////////////////////////////////////////////////////////////////////
    // MVC mapping
    //////////////////////////////////////////////////////////////////////

    private Grouping mapGrouping(String groupingPath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(get("/api/groupings/" + groupingPath + "/grouping"))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), Grouping.class);
    }

    private GroupingsServiceResult mapGSR(String uri) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(post(uri)
                .with(csrf()))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            return objectMapper.readValue(result.getResponse().getContentAsByteArray(), GroupingsServiceResult.class);
        } else {
            throw new GroupingsHTTPException();
        }
    }

    private List<GroupingsServiceResult> mapGSRs(String uri) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(post(uri)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), List.class);
    }

    private GroupingAssignment mapGroupingAssignment() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(get("/api/groupings/groupingAssignment")
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), GroupingAssignment.class);
    }

    private AdminListsHolder mapAdminListsHolder() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(get("/api/groupings/adminLists")
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), AdminListsHolder.class);
    }
}

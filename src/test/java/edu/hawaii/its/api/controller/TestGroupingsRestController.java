package edu.hawaii.its.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hawaii.its.api.service.GroupingsService;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
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

import javax.annotation.PostConstruct;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
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
    private GroupingsService gs;

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

        gs.addMemberAs(tst[0], GROUPING_INCLUDE, tst[0]);
        gs.deleteMemberAs(tst[0], GROUPING_EXCLUDE, tst[0]);

        gs.addMemberAs(tst[0], GROUPING_INCLUDE, tst[1]);
        gs.deleteMemberAs(tst[0], GROUPING_EXCLUDE, tst[1]);

        gs.addMemberAs(tst[0], GROUPING_INCLUDE, tst[2]);
        gs.deleteMemberAs(tst[0], GROUPING_EXCLUDE, tst[2]);

        gs.addMemberAs(tst[0], GROUPING_EXCLUDE, tst[3]);
        gs.deleteMemberAs(tst[0], GROUPING_INCLUDE, tst[3]);

        gs.addMemberAs(tst[0], GROUPING_EXCLUDE, tst[4]);
        gs.deleteMemberAs(tst[0], GROUPING_INCLUDE, tst[4]);

        gs.removeOwnership(GROUPING, tst[0], tst[5]);

        gs.changeOptOutStatus(GROUPING, tst[0], true);
    }

    @Test
    public void testConstruction() {
        assertNotNull(gs);
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

        assertTrue(gs.inGroup(GROUPING_EXCLUDE, tst[4]));

        mapGSR("/api/groupings/" + GROUPING + "/" + tst[4] + "/addMemberToIncludeGroup");

        assertFalse(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING_INCLUDE, tst[4]));

        mapGSR("/api/groupings/" + GROUPING + "/" + tst[4] + "/addMemberToExcludeGroup");
        assertFalse(gs.inGroup(GROUPING_INCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void deleteMemberTest() throws Exception {

        assertTrue(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
        mapGSR("/api/groupings/" + GROUPING + "/" + tst[4] + "/deleteMemberFromExcludeGroup");

        assertFalse(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING, tst[4]));

        assertTrue(gs.inGroup(GROUPING_INCLUDE, tst[1]));
        mapGSR("/api/groupings/" + GROUPING + "/" + tst[1] + "/deleteMemberFromIncludeGroup");

        assertFalse(gs.inGroup(GROUPING_EXCLUDE, tst[1]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, tst[1]));

        //reset Grouping
        mapGSR("/api/groupings/" + GROUPING + "/" + tst[4] + "/addMemberToExcludeGroup");
        mapGSR("/api/groupings/" + GROUPING + "/" + tst[1] + "/addMemberToIncludeGroup");
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void getGroupingTest() throws Exception {
        Grouping grouping = mapGrouping(GROUPING);

        assertTrue(grouping.getInclude().getNames().contains(tstName[0]));
        assertTrue(grouping.getInclude().getNames().contains(tstName[1]));
        assertTrue(grouping.getInclude().getNames().contains(tstName[2]));
        assertTrue(grouping.getExclude().getNames().contains(tstName[3]));
        assertTrue(grouping.getExclude().getNames().contains(tstName[4]));

        assertTrue(grouping.getInclude().getUsernames().contains(tst[0]));
        assertTrue(grouping.getInclude().getUsernames().contains(tst[1]));
        assertTrue(grouping.getInclude().getUsernames().contains(tst[2]));
        assertTrue(grouping.getExclude().getUsernames().contains(tst[3]));
        assertTrue(grouping.getExclude().getUsernames().contains(tst[4]));

        assertTrue(grouping.getInclude().getUuids().contains(tst[0]));
        assertTrue(grouping.getInclude().getUuids().contains(tst[1]));
        assertTrue(grouping.getInclude().getUuids().contains(tst[2]));
        assertTrue(grouping.getExclude().getUuids().contains(tst[3]));
        assertTrue(grouping.getExclude().getUuids().contains(tst[4]));

        assertTrue(grouping.getComposite().getUsernames().contains(tst[0]));
        assertTrue(grouping.getComposite().getUsernames().contains(tst[1]));
        assertTrue(grouping.getComposite().getUsernames().contains(tst[2]));
        assertFalse(grouping.getComposite().getUsernames().contains(tst[3]));
        assertFalse(grouping.getComposite().getUsernames().contains(tst[4]));

        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
        mapGSR("/api/groupings/" + grouping.getPath() + "/" + tst[5] + "/assignOwnership");
        grouping = mapGrouping(GROUPING);

        assertTrue(grouping.getOwners().getNames().contains(tstName[5]));
        mapGSR("/api/groupings/" + grouping.getPath() + "/" + tst[5] + "/removeOwnership");
        grouping = mapGrouping(GROUPING);

        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
    }

    @Test
    @WithMockUhUser(username = "iamtst01")
    public void groupingAssignmentTest() throws Exception {
//        GroupingAssignment groupings = gc.groupingAssignment(tst[0]).getBody();
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
    @WithMockUhUser(username = "iamtst05")
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
    @WithMockUhUser(username = "iamtst05")
    public void myGroupingsTest3() throws Exception {
        boolean optedIn = false;

        GroupingAssignment tst4Groupings = mapGroupingAssignment();
        assertEquals(tst4Groupings.getGroupingsOptedInTo().size(), 0);
        mapGSRs("/api/groupings/" + GROUPING + "/optIn");
        tst4Groupings = mapGroupingAssignment();
        for (Grouping grouping : tst4Groupings.getGroupingsOptedInTo()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                optedIn = true;
            }
        }
        assertTrue(optedIn);

        //reset Grouping
        gs.cancelOptIn(GROUPING, tst[4]);
        gs.addMemberAs(tst[0], GROUPING_EXCLUDE, tst[4]);
    }

    @Test
    @WithMockUhUser(username = "iamtst06")
    public void myGroupingsTest4() throws Exception{
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

        // reset Grouping
        gs.cancelOptOut(GROUPING, tst[5]);
    }


    @Test
    @WithMockUhUser(username = "iamtst05")
    public void optInTest() throws Exception{
        assertFalse(gs.inGroup(GROUPING, tst[4]));
        assertTrue(gs.inGroup(GROUPING + BASIS, tst[5]));

        mapGSRs("/api/groupings/" + GROUPING + "/optIn");
        assertTrue(gs.checkSelfOpted(GROUPING_INCLUDE, tst[4]));
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING, tst[4]));

        mapGSRs("/api/groupings/" + GROUPING + "/cancelOptIn");
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[4]));
        assertFalse(gs.checkSelfOpted(GROUPING_INCLUDE, tst[4]));

        assertTrue(gs.inGroup(GROUPING, tst[5]));

        //reset Grouping
        gs.addMemberAs(tst[0], GROUPING_EXCLUDE, tst[4]);
        assertFalse(gs.inGroup(GROUPING, tst[4]));
    }

//    @Test
//    public void optOutTest() {
//        assertTrue(gs.inGroup(GROUPING, tst[5]));
//
//        gc.optOut(GROUPING, tst[5]);
//        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[5]));
//        assertFalse(gs.checkSelfOpted(GROUPING_INCLUDE, tst[5]));
//        assertFalse(gs.inGroup(GROUPING, tst[5]));
//
//        gc.cancelOptOut(GROUPING, tst[5]);
//        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[5]));
//        assertFalse(gs.checkSelfOpted(GROUPING_INCLUDE, tst[5]));
//
//        assertTrue(gs.inGroup(GROUPING + BASIS_PLUS_INCLUDE, tst[5]));
//    }
//
//    @Test
//    public void changeListservStatusTest() {
//        assertTrue(gs.hasListserv(GROUPING));
//
//        gc.setListserv(GROUPING, tst[0], false);
//        assertFalse(gs.hasListserv(GROUPING));
//
//        gc.setListserv(GROUPING, tst[0], true);
//        assertTrue(gs.hasListserv(GROUPING));
//    }
//
//    @Test
//    public void changeOptInTest() {
//        assertTrue(gs.optInPermission(GROUPING));
//
//        gc.setOptIn(GROUPING, tst[0], false);
//        assertFalse(gs.optInPermission(GROUPING));
//
//        gc.setOptIn(GROUPING, tst[0], true);
//        assertTrue(gs.optInPermission(GROUPING));
//    }
//
//    @Test
//    public void changeOptOutTest() {
//        assertTrue(gs.optOutPermission(GROUPING));
//
//        gc.setOptOut(GROUPING, tst[0], false);
//        assertFalse(gs.optOutPermission(GROUPING));
//
//        gc.setOptOut(GROUPING, tst[0], true);
//        assertTrue(gs.optOutPermission(GROUPING));
//    }
//
//    @Test
//    public void aaronTest() {
//        GroupingAssignment aaronsGroupings = gc.groupingAssignment(STUDENT_TEST_USERNAME).getBody();
//        assertNotNull(aaronsGroupings);
//    }

//    @Test
//    public void getEmptyGroupingTest() {
//
//        Grouping storeEmpty = gc.grouping(GROUPING_STORE_EMPTY, tst[0]).getBody();
//        Grouping trueEmpty = gc.grouping(GROUPING_TRUE_EMPTY, tst[0]).getBody();
//
//        assertTrue(storeEmpty.getBasis().getMembers().size() == 1);
//        assertTrue(storeEmpty.getComposite().getMembers().size() == 0);
//        assertTrue(storeEmpty.getExclude().getMembers().size() == 0);
//        assertTrue(storeEmpty.getInclude().getMembers().size() == 0);
//        assertTrue(storeEmpty.getOwners().getUsernames().contains(tst[0]));
//
//        assertTrue(trueEmpty.getBasis().getMembers().size() == 0);
//        assertTrue(trueEmpty.getComposite().getMembers().size() == 0);
//        assertTrue(trueEmpty.getExclude().getMembers().size() == 0);
//        assertTrue(trueEmpty.getInclude().getMembers().size() == 0);
//        assertTrue(trueEmpty.getOwners().getUsernames().contains(tst[0]));
//
//    }

//    @Test
//    public void adminListsTest() {
//        AdminListsHolder infoFail = gc.adminLists(tst[0]).getBody();
//
//        assertEquals(infoFail.getAdminGroup().getMembers().size(), 0);
//        assertEquals(infoFail.getAllGroupings().size(), 0);
//
//        AdminListsHolder infoSuccess = gc.adminLists(API_ACCOUNT).getBody();
//
//        //STUDENT_TEST_USERNAME can be replaced with any account that has admin access
//        assertTrue(infoSuccess.getAdminGroup().getUsernames().contains(STUDENT_TEST_USERNAME));
//    }
//
//    @Test
//    public void addDeleteAdminTest() {
//        GroupingsServiceResult addAdminResults;
//        GroupingsServiceResult deleteAdminResults;
//
//        try {
//            addAdminResults = gc.addAdmin(tst[0], tst[0]).getBody();
//        } catch (GroupingsServiceResultException gsre) {
//            addAdminResults = gsre.getGsr();
//        }
//
//        deleteAdminResults = gc.deleteAdmin(API_ACCOUNT, tst[0]).getBody();
//        assertNotNull(deleteAdminResults);
//
//        try {
//            deleteAdminResults = gc.deleteAdmin(tst[0], tst[0]).getBody();
//        } catch (GroupingsServiceResultException gsre) {
//            deleteAdminResults = gsre.getGsr();
//        }
//
//        assertTrue(deleteAdminResults.getResultCode().startsWith(FAILURE));
//
//    }


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
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), GroupingsServiceResult.class);
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
}

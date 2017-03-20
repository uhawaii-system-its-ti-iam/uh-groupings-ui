package edu.hawaii.its.holiday.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.holiday.api.Grouping;
import edu.hawaii.its.holiday.api.GroupingsService;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.api.GcGetGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupingsController {
    private String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private String include = grouping + ":include";
    private String exclude = grouping + ":exclude";
    private String aaron = "aaronvil";
    private String zac = "zknoebel";

    private WsSubjectLookup lookupAaron = new WsSubjectLookup();

    @Autowired
    private GroupingsService gs;

    @Autowired
    private GroupingsController gc;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    public Environment env;

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
        mockMvc = webAppContextSetup(context).build();
        lookupAaron.setSubjectIdentifier(aaron);
    }

    @Test
    public void testConstruction() {
        assertNotNull(gc);
    }

    @Test
    public void addGroupingTest() {
        //add actual test when addGrouping method gets implemented
        assertTrue(true);
    }

    @Test
    public void addMemberTest() {
        Object[] addMemberResults = gc.addMember(grouping, zac, aaron);
        WsAddMemberResults wsAddMemberResults = (WsAddMemberResults) addMemberResults[0];
        WsDeleteMemberResults wsDeleteMemberResults = (WsDeleteMemberResults) addMemberResults[1];
        assertEquals("SUCCESS", wsAddMemberResults.getResultMetadata().getResultCode());
        assertEquals("SUCCESS", wsDeleteMemberResults.getResultMetadata().getResultCode());
    }

    @Test
    public void assignOwnershipTest() {
        gc.assignOwnership(grouping, zac, aaron);
        WsGetGrouperPrivilegesLiteResult updateInclude =
                new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":include").assignPrivilegeName("update")
                        .assignSubjectLookup(lookupAaron).execute();
        WsGetGrouperPrivilegesLiteResult updateExclude =
                new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":exclude").assignPrivilegeName("update")
                        .assignSubjectLookup(lookupAaron).execute();

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        //reset Grouping
        gc.removeOwnership(grouping, zac, aaron);
    }

    @Test
    public void deleteGroupingTest() {
        //add actual test when deleteGrouping method gets implemented
        assertTrue(true);
    }

    @Test
    public void deleteMemberTest() {
        Object[] addMemberResults = gc.deleteMember(grouping, zac, aaron);
        WsAddMemberResults wsAddMemberResults = (WsAddMemberResults) addMemberResults[1];
        WsDeleteMemberResults wsDeleteMemberResults = (WsDeleteMemberResults) addMemberResults[0];
        assertEquals("SUCCESS", wsAddMemberResults.getResultMetadata().getResultCode());
        assertEquals("SUCCESS", wsDeleteMemberResults.getResultMetadata().getResultCode());

        //reset Grouping
        gc.addMember(grouping, zac, aaron);
    }

    @Test
    public void removeOwnershipTest() {
        gc.assignOwnership(grouping, zac, aaron);
        WsGetGrouperPrivilegesLiteResult updateInclude =
                new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":include").assignPrivilegeName("update")
                        .assignSubjectLookup(lookupAaron).execute();
        WsGetGrouperPrivilegesLiteResult updateExclude =
                new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":exclude").assignPrivilegeName("update")
                        .assignSubjectLookup(lookupAaron).execute();

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        gc.removeOwnership(grouping, zac, aaron);
        updateInclude = new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":include").assignPrivilegeName("update")
                .assignSubjectLookup(lookupAaron).execute();
        updateExclude = new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":exclude").assignPrivilegeName("update")
                .assignSubjectLookup(lookupAaron).execute();

        assertFalse(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertFalse(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
    }

    @Test
    public void getMembersTest() {
        Grouping groupMembers = gc.getMembers(grouping, zac);

        ArrayList<String> basisMembers = new ArrayList<>();
        ArrayList<String> excludeMembers = new ArrayList<>();
        ArrayList<String> includeMembers = new ArrayList<>();

        basisMembers.addAll(
                Arrays.asList(groupMembers.getBasis()).stream().map(WsSubject::getName).collect(Collectors.toList()));
        excludeMembers.addAll(
                Arrays.asList(groupMembers.getExclude()).stream().map(WsSubject::getName).collect(Collectors.toList()));
        includeMembers.addAll(
                (Arrays.asList(groupMembers.getInclude()).stream().map(WsSubject::getName).collect(Collectors.toList())));

        assertTrue(basisMembers.contains("Kalani P Sanidad"));
        assertTrue(excludeMembers.contains("Zachery S Knoebel"));
        assertTrue(excludeMembers.contains("Frank R Duckart"));
        assertTrue(includeMembers.contains("Aaron Jhumar B Villanueva"));
        assertTrue(includeMembers.contains("Julio C Polo"));
        assertTrue(includeMembers.contains("Michael S Hodges"));
    }

    @Test
    public void getOwnersTest() {
        ArrayList<WsSubject> owners = gc.getOwners(grouping, zac);
        ArrayList<String> ownerNames = new ArrayList<>();
        ownerNames.addAll(owners.stream().map(WsSubject::getName).collect(Collectors.toList()));

        assertTrue(ownerNames.contains("Zachery S Knoebel"));
        assertTrue(ownerNames.contains("UH Groupings API"));

    }

    @Test
    public void groupingsInTest() {
        ArrayList<String> groupings = gc.groupingsIn(aaron);
        assertTrue(groupings.contains(grouping));
    }

    @Test
    public void groupingsOwnedTest() {
        ArrayList<String> groupings = gc.groupingsOwned(zac);
        assertTrue(groupings.contains(grouping));
    }

    @Test
    public void optInTest() {
        gc.optIn(aaron, grouping);
        assertTrue(gs.checkSelfOpted(include, lookupAaron));
        assertFalse(gs.checkSelfOpted(exclude, lookupAaron));
        assertTrue(gs.inGroup(grouping + ":basis+include", aaron));
    }

    @Test
    public void optOutTest() {
        gc.optOut(aaron, grouping);
        assertTrue(gs.checkSelfOpted(exclude, lookupAaron));
        assertFalse(gs.checkSelfOpted(include, lookupAaron));
        assertFalse(gs.inGroup(grouping + ":basis+include", aaron));

        //reset Grouping
        gc.addMember(grouping, zac, aaron);
    }

    @Test
    public void cancelOptOutTest() {
        gc.optOut(aaron, grouping);
        assertTrue(gs.checkSelfOpted(exclude, lookupAaron));
        gc.cancelOptOut(grouping, aaron);
        assertFalse(gs.checkSelfOpted(exclude, lookupAaron));

        //reset Grouping
        gc.addMember(grouping, zac, aaron);
    }

    @Test
    public void cancelOptInTest() {
        gc.optIn(aaron, grouping);
        assertTrue(gs.checkSelfOpted(include, lookupAaron));
        gc.cancelOptIn(grouping, aaron);
        assertFalse(gs.checkSelfOpted(include, lookupAaron));

        //reset Grouping
        gc.addMember(grouping, zac, aaron);
    }

    @Test
    public void optOutPermissionTest() {
        assertTrue(gc.optOutPermission(aaron, grouping));
    }

    @Test
    public void optInPerissionTest() {
        assertTrue(gc.optInPermission(aaron, grouping));
    }

    @Test
    public void groupingsToOptOutOfTest() {
        ArrayList<String> groupings = gc.groupingsToOptOutOf(aaron);
        assertTrue(groupings.contains(grouping));
    }

    @Test
    public void groupingsToOptIntoTest() {
        ArrayList<String> groupings = gc.groupingsToOptInto(aaron);
        assertTrue(groupings.contains(grouping));
    }

    @Test
    public void hasListServeTest() {
        assertTrue(gc.hasListServe(grouping));
    }
}

package edu.hawaii.its.holiday.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import edu.hawaii.its.holiday.api.GrouperMethods;
import edu.hawaii.its.holiday.api.Grouping;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.controller.GroupingsController;

import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsControllerTest {
    private String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private String include = grouping + ":include";
    private String exclude = grouping + ":exclude";
    private String aaron = "aaronvil";
    private String zac = "zknoebel";

    WsSubjectLookup lookupAaron = new WsSubjectLookup();

    private GrouperMethods gm = new GrouperMethods();

    @Autowired
    private GroupingsController groupingsController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
        lookupAaron.setSubjectIdentifier(aaron);
    }

    @Test
    public void testConstruction(){
        assertNotNull(groupingsController);
    }

    @Test
    public void addGroupingTest(){
        //add actual test when addGrouping method gets implemented
        assertTrue(true);
    }

    @Test
    public void addMemberTest(){
        Object[] addMemberResults = groupingsController.addMember(grouping, zac, aaron);
        WsAddMemberResults wsAddMemberResults = (WsAddMemberResults) addMemberResults[0];
        WsDeleteMemberResults wsDeleteMemberResults = (WsDeleteMemberResults) addMemberResults[1];
        assertEquals("SUCCESS", wsAddMemberResults.getResultMetadata().getResultCode());
        assertEquals("SUCCESS", wsDeleteMemberResults.getResultMetadata().getResultCode());
    }

    @Test
    public void assignOwnershipTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void deleteGroupingTest(){
        //add actual test when deleteGrouping method gets implemented
        assertTrue(true);
    }

    @Test
    public void deleteMemberTest(){
        Object[] addMemberResults = groupingsController.deleteMember(grouping, zac, aaron);
        WsAddMemberResults wsAddMemberResults = (WsAddMemberResults) addMemberResults[1];
        WsDeleteMemberResults wsDeleteMemberResults = (WsDeleteMemberResults) addMemberResults[0];
        assertEquals("SUCCESS", wsAddMemberResults.getResultMetadata().getResultCode());
        assertEquals("SUCCESS", wsDeleteMemberResults.getResultMetadata().getResultCode());

        //reset Grouping
        groupingsController.addMember(grouping, zac, aaron);
    }

    @Test
    public void removeOwnershipTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void getMembersTest(){
        Grouping groupMembers = groupingsController.getMembers(grouping, zac);

        ArrayList<String> basisMembers = new ArrayList<>();
        ArrayList<String> excludeMembers = new ArrayList<>();
        ArrayList<String> includeMembers = new ArrayList<>();

        for(WsSubject member: Arrays.asList(groupMembers.getBasis())){
            basisMembers.add(member.getName());
        }
        for(WsSubject member: Arrays.asList(groupMembers.getExclude())){
            excludeMembers.add(member.getName());
        }
        for(WsSubject member: Arrays.asList(groupMembers.getInclude())){
            includeMembers.add(member.getName());
        }

        assertTrue(basisMembers.contains("Kalani P Sanidad"));
        assertTrue(excludeMembers.contains("Zachery S Knoebel"));
        assertTrue(excludeMembers.contains("Frank R Duckart"));
        assertTrue(includeMembers.contains("Aaron Jhumar B Villanueva"));
        assertTrue(includeMembers.contains("Julio C Polo"));
        assertTrue(includeMembers.contains("Michael S Hodges"));
    }

    @Test
    public void getOwnersTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void groupingsInTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void groupingsOwnedTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void optInTest(){
        groupingsController.optIn(aaron, grouping);
        assertTrue(gm.checkSelfOpted(include, lookupAaron));
        assertFalse(gm.checkSelfOpted(exclude, lookupAaron));
        //todo
    }

    @Test
    public void optOutTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void cancelOptOutTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void cancelOptInTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void optOutPermissionTest(){
        assertTrue(groupingsController.optOutPermission(aaron, grouping));
    }

    @Test
    public void optInPerissionTest(){
        assertTrue(groupingsController.optInPermission(aaron, grouping));
    }

    @Test
    public void groupingsToOptOutOfTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void groupingsToOptIntoTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void hasListServeTest(){
        assertTrue(groupingsController.hasListServe(grouping));
    }
}

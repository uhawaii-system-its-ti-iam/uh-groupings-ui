package edu.hawaii.its.holiday.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.controller.GroupingsController;

import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsControllerTest {
    String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";

    @Autowired
    private GroupingsController groupingsController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
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
        Object[] addMemberResults = groupingsController.addMember(grouping, "zknoebel", "aaronvil");
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
        Object[] addMemberResults = groupingsController.deleteMember(grouping, "zknoebel", "aaronvil");
        WsAddMemberResults wsAddMemberResults = (WsAddMemberResults) addMemberResults[1];
        WsDeleteMemberResults wsDeleteMemberResults = (WsDeleteMemberResults) addMemberResults[0];
        assertEquals("SUCCESS", wsAddMemberResults.getResultMetadata().getResultCode());
        assertEquals("SUCCESS", wsDeleteMemberResults.getResultMetadata().getResultCode());

        //reset Grouping
        groupingsController.addMember(grouping, "zknoebel", "aaronvil");
    }

    @Test
    public void removeOwnershipTest(){
        assertTrue(true);
        //todo
    }

    @Test
    public void getMembersTest(){
        assertTrue(true);
        //todo
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
        assertTrue(true);
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
        assertTrue(true);
        //todo
    }

    @Test
    public void optInPerissionTest(){
        assertTrue(true);
        //todo
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

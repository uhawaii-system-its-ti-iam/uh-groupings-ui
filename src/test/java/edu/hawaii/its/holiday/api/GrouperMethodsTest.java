package edu.hawaii.its.holiday.api;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.controller.GroupingsController;
import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.api.GcGetMemberships;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GrouperMethodsTest {
    GrouperMethods gm = new GrouperMethods();
    String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";

    @Test
    public void test001_addSelfOptedTest(){
        gm.addSelfOpted(grouping + ":include", "aaronvil");
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier("aaronvil");
        WsGetMembershipsResults wsGetMembershipsResults = new GcGetMemberships().addWsSubjectLookup(wsSubjectLookup).addGroupName(grouping + ":include").execute();
        String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().assignAttributeAssignType("imm_mem").addAttributeDefNameUuid("ef62bf0473614b379695ecec6cb8b3b5").addOwnerMembershipId(membershipID).execute();
        WsAttributeAssign[] selfOpted = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();
        ArrayList<String> attributeList = new ArrayList<String>();
        for(WsAttributeAssign att: Arrays.asList(selfOpted)){
            attributeList.add(att.getAttributeDefNameName());
        }
        assertTrue(attributeList.contains("uh-settings:attributes:for-memberships:uh-grouping:self-opted"));
    }

    @Test
    public void test002_checkSelfOptedTest(){
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier("aaronvil");
        assertTrue(gm.checkSelfOpted(grouping + ":include", wsSubjectLookup));
    }

    @Test
    public void test003_removeSelfOptedTest(){
        gm.removeSelfOpted(grouping + ":include", "aaronvil");
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier("aaronvil");
        WsGetMembershipsResults wsGetMembershipsResults = new GcGetMemberships().addWsSubjectLookup(wsSubjectLookup).addGroupName(grouping + ":include").execute();
        String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().assignAttributeAssignType("imm_mem").addAttributeDefNameUuid("ef62bf0473614b379695ecec6cb8b3b5").addOwnerMembershipId(membershipID).execute();
        WsAttributeAssign[] selfOpted = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();
        assertTrue(selfOpted == null || !Arrays.asList(selfOpted).contains("uh-settings:attributes:for-memberships:uh-grouping:self-opted"));
    }

    @Test
    public void test004_checkSelfOptedTest2(){
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier("aaronvil");
        assertFalse(gm.checkSelfOpted(grouping + ":include", wsSubjectLookup));
    }

    @Test
    public void isOwnerTest(){
        assertTrue(gm.isOwner(grouping, "zknoebel"));
    }

    @Test
    public void inGroupTest(){
        assertTrue(gm.inGroup(grouping + ":include", "aaronvil"));
        assertFalse(gm.inGroup(grouping + ":exclude", "aaronvil"));
    }

    @Test
    public void groupOptInPermissionTest(){
        assertTrue(gm.groupOptInPermission("aaronvil", grouping + ":include"));
        assertTrue(gm.groupOptInPermission("aaronvil", grouping + ":exclude"));
    }

    @Test
    public void groupOptOutPermissionTest(){
        assertTrue(gm.groupOptOutPermission("aaronvil", grouping + ":include"));
        assertTrue(gm.groupOptOutPermission("aaronvil", grouping + ":exclude"));
    }

    @Test
    public void updateLastModifiedTest(){
        //test is accurate to the minute, and if checks to see if the current time gets added to the lastModified attribute of a group
        //if the minute happens to change in between getting the time and setting the time, the test will fail..
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hhmm");
        Date date = new Date();
        Date time = new Date();
        String currentDateTime = dateFormat.format(date) + "T" + timeFormat.format(time);

        gm.updateLastModified(grouping + ":include");
        WsGetAttributeAssignmentsResults assignments = new GcGetAttributeAssignments().assignAttributeAssignType("group").addOwnerGroupName(grouping + ":include").addAttributeDefNameName("uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm").execute();
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertEquals(currentDateTime, assignedValue);
    }
}

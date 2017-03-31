package edu.hawaii.its.holiday.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGroupingsService {

    private final static String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private final static String aaron = "aaronvil";
    private final static String zac = "zknoebel";

    @Autowired
    GroupingsService gs;

    @Autowired
    public Environment env; // Just for the settings check.

    @PostConstruct
    public void init() {
        Assert.hasLength(env.getProperty("grouperClient.webService.url"),
                "property 'grouperClient.webService.url' is required");
        Assert.hasLength(env.getProperty("grouperClient.webService.login"),
                "property 'grouperClient.webService.login' is required");
        Assert.hasLength(env.getProperty("grouperClient.webService.password"),
                "property 'grouperClient.webService.password' is required");
    }

    @Test
    public void test001_addSelfOptedTest() {
        gs.addSelfOpted(grouping + ":include", aaron);
        WsSubjectLookup lookup = gs.makeWsSubjectLookup(aaron);
        String group = grouping + ":include";
        WsGetMembershipsResults wsGetMembershipsResults = gs.membershipsResults(lookup, group);
        String assignType = "imm_mem";
        String uuid = GroupingsService.UUID_USERNAME;
        String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

        WsAttributeAssign[] wsAttributes = gs.attributeAssign(assignType, uuid, membershipID);

        ArrayList<String> attributeList = new ArrayList<String>();
        for (WsAttributeAssign att : Arrays.asList(wsAttributes)) {
            attributeList.add(att.getAttributeDefNameName());
        }
        assertTrue(attributeList.contains(GroupingsService.SELF_OPTED));
    }

    @Test
    public void test002_checkSelfOptedTest() {
        WsSubjectLookup wsSubjectLookup = gs.makeWsSubjectLookup(aaron);
        assertTrue(gs.checkSelfOpted(grouping + ":include", wsSubjectLookup));
    }

    @Test
    public void test003_removeSelfOptedTest() {
        gs.removeSelfOpted(grouping + ":include", aaron);
        WsSubjectLookup lookup = gs.makeWsSubjectLookup(aaron);
        String group = grouping + ":include";
        WsGetMembershipsResults wsGetMembershipsResults = gs.membershipsResults(lookup, group);
        String assignType = "imm_mem";
        String uuid = GroupingsService.UUID_USERNAME;
        String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

        WsAttributeAssign[] wsAttributes = gs.attributeAssign(assignType, uuid, membershipID);

        assertTrue(wsAttributes.length == 0
                || !Arrays.asList(wsAttributes).contains(GroupingsService.SELF_OPTED));
    }

    @Test
    public void test004_checkSelfOptedTest2() {
        WsSubjectLookup wsSubjectLookup = gs.makeWsSubjectLookup(aaron);
        assertFalse(gs.checkSelfOpted(grouping + ":include", wsSubjectLookup));
    }

    @Test
    public void isOwnerTest() {
        assertTrue(gs.isOwner(grouping, zac));
    }

    @Test
    public void inGroupTest() {
        assertTrue(gs.inGroup(grouping + ":include", aaron));
        assertFalse(gs.inGroup(grouping + ":exclude", aaron));
    }

    @Test
    public void groupOptInPermissionTest() {
        assertTrue(gs.groupOptInPermission(aaron, grouping + ":include"));
        assertTrue(gs.groupOptInPermission(aaron, grouping + ":exclude"));
    }

    @Test
    public void groupOptOutPermissionTest() {
        assertTrue(gs.groupOptOutPermission(aaron, grouping + ":include"));
        assertTrue(gs.groupOptOutPermission(aaron, grouping + ":exclude"));
    }

    @Test
    public void updateLastModifiedTest() {
        // Test is accurate to the minute, and if checks to see if the current
        // time gets added to the lastModified attribute of a group if the
        // minute happens to change in between getting the time and setting
        // the time, the test will fail.

        String currentDateTime = gs.wsDateTime();
        String group = grouping + ":include";

        gs.updateLastModified(group);

        String assignType = "group";
        String nameName = "uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm";

        WsGetAttributeAssignmentsResults assignments =
                gs.attributeAssignmentsResults(assignType, group, nameName);
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertEquals(currentDateTime, assignedValue);
    }
}

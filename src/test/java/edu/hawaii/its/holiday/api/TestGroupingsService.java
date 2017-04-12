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
    public void isOwnerTest() {
        assertTrue(gs.isOwner(grouping, zac));
    }

    @Test
    public void inGroupTest() {
        assertTrue(gs.inGroup(grouping + ":include", "iamTst01"));
        assertFalse(gs.inGroup(grouping + ":exclude", "iamTst01"));
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

        String group = grouping + ":include";
        String currentDateTime = gs.wsDateTime();

        gs.updateLastModified(group);

        String assignType = "group";
        String nameName = "uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm";

        WsGetAttributeAssignmentsResults assignments =
                gs.attributeAssignmentsResults(assignType, group, nameName);
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertEquals(currentDateTime, assignedValue);
    }


    //TODO add test for getOwners
    //TODO add test for optOutPermission
    //TODO add test for optInPermission
    //TODO add test for groupingsIn
    //TODO add test for hasListServe
    //TODO add test for groupingsOwned
    //TODO add test for groupingsToOptOutOf
    //TODO add test for groupingsToOptInto
    //TODO add test for addSelfOpted
    //TODO add test for checkSelfOpted
    //TODO add test for inGroup


    //TODO write down the rest of the things to test...


    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
    //TODO add test for
}

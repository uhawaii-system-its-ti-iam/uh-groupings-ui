package edu.hawaii.its.groupings.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.groupings.api.type.Group;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.GroupingsServiceResult;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.util.Dates;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsService {

    private final String GROUPING = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private final String GROUPING_INCLUDE = GROUPING + ":include";
    private final String GROUPING_EXCLUDE = GROUPING + ":exclude";
    private String[] username = new String[6];

    @Autowired
    private GroupingsServiceImpl gs;

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

    @Before
    public void setUp() {
        for (int i = 0; i < 6; i++) {
            username[i] = "iamtst0" + (i + 1);
        }
    }

    @Test
    public void isOwnerTest() {
        assertTrue(gs.isOwner(GROUPING, username[0]));
    }

    @Test
    public void groupOptInPermissionTest() {
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));
    }

    @Test
    public void groupOptOutPermissionTest() {
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
    }

    @Test
    public void updateLastModifiedTest() {
        // Test is accurate to the minute, and if checks to see if the current
        // time gets added to the lastModified attribute of a group if the
        // minute happens to change in between getting the time and setting
        // the time, the test will fail.

        final String group = GROUPING_INCLUDE;

        GroupingsServiceResult gsr = gs.updateLastModified(group);
        String dateStr = gsr.getAction().split(" to time ")[1];

        String assignType = "group";
        String nameName = "uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm";

        WsGetAttributeAssignmentsResults assignments =
                gs.attributeAssignmentsResults(assignType, group, nameName);
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();

        assertEquals(dateStr, assignedValue);
    }

    @Test
    public void findOwners() {
        Group owners = gs.findOwners(GROUPING, username[0]);

        assertTrue(owners.getUsernames().contains(username[0]));
        assertFalse(owners.getUsernames().contains(username[1]));
    }

    @Test
    public void optOutPermissionTest() {
        assertTrue(gs.optOutPermission(GROUPING));
    }

    @Test
    public void optInPermissionTest() {
        assertTrue(gs.optInPermission(GROUPING));
    }

    @Test
    public void hasListservTest() {
        assertTrue(gs.hasListserv(GROUPING));
    }

    @Test
    public void groupingsInTest() {
        MyGroupings myGroupings = gs.getMyGroupings(username[0]);
        boolean inGrouping = false;

        for (Grouping grouping : myGroupings.getGroupingsIn()) {
            if (grouping.getPath().contains(GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertTrue(inGrouping);

        inGrouping = false;
        myGroupings = gs.getMyGroupings(username[4]);
        for (Grouping grouping : myGroupings.getGroupingsIn()) {
            if (grouping.getPath().contains(GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertFalse(inGrouping);
    }

    @Test
    public void groupingsOwnedTest() {
        MyGroupings myGroupings = gs.getMyGroupings(username[0]);
        boolean ownsGrouping = false;

        for (Grouping grouping : myGroupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertTrue(ownsGrouping);

        ownsGrouping = false;
        myGroupings = gs.getMyGroupings(username[4]);
        for (Grouping grouping : myGroupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertFalse(ownsGrouping);
    }

    @Test
    public void groupingsToOptTest() {
        MyGroupings myGroupings = gs.getMyGroupings(username[0]);

        boolean canOptIn = false;
        for (Grouping grouping : myGroupings.getGroupingsToOptInTo()) {
            if (grouping.getPath().contains(GROUPING)) {
                canOptIn = true;
                break;
            }
        }
        assertTrue(canOptIn);

        boolean canOptOut = false;
        for (Grouping grouping : myGroupings.getGroupingsToOptOutOf()) {
            if (grouping.getPath().contains(GROUPING)) {
                canOptOut = true;
                break;
            }
        }
        assertTrue(canOptOut);
    }

    @Test
    public void addRemoveSelfOptedTest() {
        WsSubjectLookup lookup = gs.makeWsSubjectLookup(username[4]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, lookup));

        gs.addSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, lookup));

        gs.removeSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, lookup));
    }

    @Test
    public void inGroupTest() {
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[1]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[3]));

        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[3]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[1]));
    }

    @Test
    public void groupOptPermissionTest() {
        assertTrue(gs.groupOptOutPermission(username[0], GROUPING_INCLUDE));
        assertTrue(gs.groupOptOutPermission(username[0], GROUPING_EXCLUDE));

        assertTrue(gs.groupOptInPermission(username[0], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[0], GROUPING_EXCLUDE));
    }


    @Test
    public void makeWsSubjectTest() {
        WsSubjectLookup subjectLookup = gs.makeWsSubjectLookup(username[1]);
        assertTrue(subjectLookup.getSubjectIdentifier().equals(username[1]));
    }

    @Test
    public void makeWsGroupLookupTest() {
        WsGroupLookup lookup = gs.makeWsGroupLookup(GROUPING_EXCLUDE);
        assertTrue(lookup.getGroupName().equals(GROUPING_EXCLUDE));
    }

    //TODO add test for assignAttributesResults (both)
    //TODO add test for membershipAttributeAssign
    //TODO add test for attributeAssignments
    //TODO add test for grouperPrivilegesLite (both)
    //TODO add test for membershipsResults
    //TODO add test for addMemberAs
    //TODO add test for deleteMemberAs
    //TODO add test for getMember
    //TODO add test for allGroupings
    //TODO add test for extractGroupings
    //TODO add test for getGroupNames
    //TODO add test for makeGroupings
    //TODO add test for extractGroupNames
    //TODO add test for extractGroupingNames
    //TODO add test for removeGroupOwnership
    //TODO add test for addGroupOwnership
    //TODO add test for makeGroup
    //TODO add test for makePerson
    //TODO add test for groupingNamesFromPrivilegeResults
}

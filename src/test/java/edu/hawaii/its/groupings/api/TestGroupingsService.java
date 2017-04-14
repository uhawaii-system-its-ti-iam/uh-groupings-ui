package edu.hawaii.its.groupings.api;

import edu.hawaii.its.groupings.api.type.Group;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupingsService {

    private final static String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private String[] tst = new String[6];
    private String[] tstName = {"tst01fname", "tst02name", "tst03name", "tst04name", "tst05name", "tst06name"};

    @Autowired
    GroupingsServiceImpl gs;

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
            tst[i] = "iamtst0" + (i + 1);
        }
    }

    @Test
    public void isOwnerTest() {
        assertTrue(gs.isOwner(grouping, tst[0]));
    }

    @Test
    public void groupOptInPermissionTest() {
        assertTrue(gs.groupOptInPermission(tst[1], grouping + ":include"));
        assertTrue(gs.groupOptInPermission(tst[1], grouping + ":exclude"));
    }

    @Test
    public void groupOptOutPermissionTest() {
        assertTrue(gs.groupOptOutPermission(tst[1], grouping + ":include"));
        assertTrue(gs.groupOptOutPermission(tst[1], grouping + ":exclude"));
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

    @Test
    public void getOwnersTest(){
        Group owners = gs.getOwners(grouping, tst[0]);

        assertTrue(owners.getUsernames().contains(tst[0]));
        assertFalse(owners.getUsernames().contains(tst[1]));
    }


    @Test
    public void optOutPermissionTest(){
        assertTrue(gs.optOutPermission(grouping));
    }


    @Test
    public void optInPermissionTest(){
        assertTrue(gs.optInPermission(grouping));
    }

    @Test
    public void hasListServeTest(){
        assertTrue(gs.hasListServe(grouping));
    }

    @Test
    public void groupingsInTest(){
        MyGroupings myGroupings = gs.getMyGroupings(tst[0]);
        boolean inGrouping = false;

        for (Grouping grouping : myGroupings.getGroupingsIn()){
            if(grouping.getPath().contains(this.grouping)){
                inGrouping = true;
            }
        }
        assertTrue(inGrouping);
        inGrouping = false;

        myGroupings = gs.getMyGroupings(tst[4]);
        for (Grouping grouping : myGroupings.getGroupingsIn()){
            if(grouping.getPath().contains(this.grouping)){
                inGrouping = true;
            }
        }
        assertFalse(inGrouping);
    }

    @Test
    public void groupingsOwnedTest(){
        MyGroupings myGroupings = gs.getMyGroupings(tst[0]);
        boolean ownsGrouping = false;

        for (Grouping grouping : myGroupings.getGroupingsOwned()){
            if(grouping.getPath().contains(this.grouping)){
                ownsGrouping = true;
            }
        }
        assertTrue(ownsGrouping);
        ownsGrouping = false;

        myGroupings = gs.getMyGroupings(tst[4]);
        for (Grouping grouping : myGroupings.getGroupingsOwned()){
            if(grouping.getPath().contains(this.grouping)){
                ownsGrouping = true;
            }
        }
        assertFalse(ownsGrouping);
    }

    @Test
    public void groupingsToOptTest(){
        MyGroupings myGroupings = gs.getMyGroupings(tst[0]);
        boolean canOptOut = false;
        boolean canOptIn = false;

        for (Grouping grouping : myGroupings.getGroupingsToOptInTo()){
            if(grouping.getPath().contains(this.grouping)){
                canOptIn = true;
            }
        }
        assertTrue(canOptIn);

        for (Grouping grouping : myGroupings.getGroupingsToOptOutOf()){
            if(grouping.getPath().contains(this.grouping)){
                canOptOut = true;
            }
        }
        assertTrue(canOptOut);
    }

    @Test
    public void addRemoveSelfOptedTest(){
        WsSubjectLookup lookup = gs.makeWsSubjectLookup(tst[4]);
        String excludeGroup = grouping + ":exclude";
        assertFalse(gs.checkSelfOpted(excludeGroup, lookup));

        gs.addSelfOpted(excludeGroup, tst[4]);
        assertTrue(gs.checkSelfOpted(excludeGroup, lookup));

        gs.removeSelfOpted(excludeGroup, tst[4]);
        assertFalse(gs.checkSelfOpted(excludeGroup, lookup));
    }

    @Test
    public void inGroupTest(){
        assertTrue(gs.inGroup(grouping + ":include", tst[1]));
        assertFalse(gs.inGroup(grouping + ":include", tst[3]));

        assertTrue(gs.inGroup(grouping + ":exclude", tst[3]));
        assertFalse(gs.inGroup(grouping + ":exclude", tst[1]));
    }

    //TODO add test for groupOptOutPermission
    //TODO add test for groupOptInPermission
    //TODO add test for updateLastModified
    //TODO add test for makeWsSubject
    //TODO add test for makeWsGroupLookup
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

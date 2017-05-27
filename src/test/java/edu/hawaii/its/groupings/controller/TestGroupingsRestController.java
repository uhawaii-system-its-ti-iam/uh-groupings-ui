package edu.hawaii.its.groupings.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.groupings.api.GroupingsService;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsRestController {
//    private String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
//    private String grouping = "hawaii.edu:custom:test:zknoebel:zknoebel-v2-empty-basis";
    private String grouping = "tmp:win-many";
    private String include = grouping + ":include";
    private String exclude = grouping + ":exclude";
    private String[] tst = new String[6];
    private String[] tstName = {"tst01fname", "tst02name", "tst03name", "tst04name", "tst05name", "tst06name"};

    private WsSubjectLookup[] tstLookup = new WsSubjectLookup[6];

    @Autowired
    private GroupingsService gs;

    @Autowired
    private GroupingsRestController gc;

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
            tstLookup[i] = gs.makeWsSubjectLookup(tst[i]);
        }
    }

    @Test
    public void testConstruction() {
        assertNotNull(gs);
        assertNotNull(gc);
    }

    @Test
    public void assignAndRemoveOwnershipTest() {
        Grouping g = gc.grouping(grouping, tst[0]).getBody();
        assertFalse(g.getOwners().getUsernames().contains(tst[1]));

        gc.assignOwnership(grouping, tst[0], tst[1]);
        g = gc.grouping(grouping, tst[0]).getBody();
        assertTrue(g.getOwners().getUsernames().contains(tst[1]));

        gc.removeOwnership(grouping, tst[0], tst[1]);
        g = gc.grouping(grouping, tst[0]).getBody();
        assertFalse(g.getOwners().getUsernames().contains(tst[1]));
    }

    @Test
    public void addMemberTest() {

        assertTrue(gs.inGroup(exclude, tst[4]));

        gc.addMemberToIncludeGroup(grouping, tst[0], tst[4]);
        assertFalse(gs.inGroup(exclude, tst[4]));
        assertTrue(gs.inGroup(include, tst[4]));

        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(gs.inGroup(include, tst[4]));
        assertTrue(gs.inGroup(exclude, tst[4]));
    }

    @Test
    public void deleteMemberTest() {

        assertTrue(gs.inGroup(exclude, tst[4]));
        gc.deleteMemberFromExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(gs.inGroup(exclude, tst[4]));
        assertTrue(gs.inGroup(grouping, tst[4]));

        assertTrue(gs.inGroup(include, tst[1]));
        gc.deleteMemberFromIncludeGroup(grouping, tst[0], tst[1]);
        assertFalse(gs.inGroup(exclude, tst[1]));
        assertFalse(gs.inGroup(include, tst[1]));

        //reset Grouping
        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        gc.addMemberToIncludeGroup(grouping, tst[0], tst[1]);
    }

    @Test
    public void getGroupingTest() {
        Grouping grouping = gc.grouping(this.grouping, tst[0]).getBody();

        assertTrue(grouping.getBasis().getNames().contains(tstName[3]));
        assertTrue(grouping.getBasis().getNames().contains(tstName[4]));
        assertTrue(grouping.getBasis().getNames().contains(tstName[5]));
        assertTrue(grouping.getInclude().getNames().contains(tstName[0]));
        assertTrue(grouping.getInclude().getNames().contains(tstName[1]));
        assertTrue(grouping.getInclude().getNames().contains(tstName[2]));
        assertTrue(grouping.getExclude().getNames().contains(tstName[3]));
        assertTrue(grouping.getExclude().getNames().contains(tstName[4]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains(tstName[0]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains(tstName[1]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains(tstName[2]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains(tstName[5]));

        assertTrue(grouping.getBasis().getUsernames().contains(tst[3]));
        assertTrue(grouping.getBasis().getUsernames().contains(tst[4]));
        assertTrue(grouping.getBasis().getUsernames().contains(tst[5]));
        assertTrue(grouping.getInclude().getUsernames().contains(tst[0]));
        assertTrue(grouping.getInclude().getUsernames().contains(tst[1]));
        assertTrue(grouping.getInclude().getUsernames().contains(tst[2]));
        assertTrue(grouping.getExclude().getUsernames().contains(tst[3]));
        assertTrue(grouping.getExclude().getUsernames().contains(tst[4]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[0]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[1]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[2]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[5]));

        assertTrue(grouping.getBasis().getUuids().contains(tst[3]));
        assertTrue(grouping.getBasis().getUuids().contains(tst[4]));
        assertTrue(grouping.getBasis().getUuids().contains(tst[5]));
        assertTrue(grouping.getInclude().getUuids().contains(tst[0]));
        assertTrue(grouping.getInclude().getUuids().contains(tst[1]));
        assertTrue(grouping.getInclude().getUuids().contains(tst[2]));
        assertTrue(grouping.getExclude().getUuids().contains(tst[3]));
        assertTrue(grouping.getExclude().getUuids().contains(tst[4]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains(tst[0]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains(tst[1]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains(tst[2]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains(tst[5]));

        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
        gc.assignOwnership(grouping.getPath(), tst[0], tst[5]);
        grouping = gc.grouping(this.grouping, tst[0]).getBody();
        assertTrue(grouping.getOwners().getNames().contains(tstName[5]));
        gc.removeOwnership(grouping.getPath(), tst[0], tst[5]);
        grouping = gc.grouping(this.grouping, tst[0]).getBody();
        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
    }

    @Test
    public void myGroupingsTest() {
        MyGroupings groupings = gc.myGroupings(tst[0]).getBody();

        boolean inGrouping = false;
        for (Grouping grouping : groupings.getGroupingsIn()) {
            if (grouping.getPath().contains(this.grouping)) {
                inGrouping = true;
                break;
            }
        }
        assertTrue(inGrouping);

        boolean canOptin = false;
        for (Grouping grouping : groupings.getGroupingsToOptInTo()) {
            if (grouping.getPath().contains(this.grouping)) {
                canOptin = true;
                break;
            }
        }
        assertTrue(canOptin);

        boolean canOptOut = false;
        for (Grouping grouping : groupings.getGroupingsToOptOutOf()) {
            if (grouping.getPath().contains(this.grouping)) {
                canOptOut = true;
                break;
            }
        }
        assertTrue(canOptOut);

        boolean ownsGrouping = false;
        for (Grouping grouping : groupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(this.grouping)) {
                ownsGrouping = true;
                break;
            }
        }
        assertTrue(ownsGrouping);

    }

    @Test
    public void myGroupingsTest2() {
        MyGroupings groupings = gc.myGroupings(tst[4]).getBody();

        boolean inGrouping = false;
        for (Grouping grouping : groupings.getGroupingsIn()) {
            if (grouping.getPath().contains(this.grouping)) {
                inGrouping = true;
                break;
            }
        }
        assertFalse(inGrouping);

        boolean ownsGrouping = false;
        for (Grouping grouping : groupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(this.grouping)) {
                ownsGrouping = true;
                break;
            }
        }
        assertFalse(ownsGrouping);
    }

    @Test
    public void myGroupingsTest3() {
        boolean optedIn = false;
        boolean optedOut = false;

        gc.optIn(grouping, tst[4]);
        MyGroupings tst4Groupings = gc.myGroupings(tst[4]).getBody();
        for (Grouping grouping : tst4Groupings.getGroupingsOptedInTo()) {
            if (grouping.getPath().contains(this.grouping)) {
                optedIn = true;
            }
        }
        assertTrue(optedIn);

        gc.optOut(grouping, tst[5]);
        MyGroupings tst5Groupings = gc.myGroupings(tst[5]).getBody();
        for (Grouping grouping : tst5Groupings.getGroupingsOptedOutOf()) {
            if (grouping.getPath().contains(this.grouping)) {
                optedOut = true;
            }
        }
        assertTrue(optedOut);

        //reset Grouping
        gc.cancelOptIn(grouping, tst[4]);
        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        gc.cancelOptOut(grouping, tst[5]);
    }


    @Test
    public void optInTest() {
        assertFalse(gs.inGroup(grouping, tst[4]));
        assertTrue(gs.inGroup(grouping + ":basis", tst[5]));

        gc.optIn(grouping, tst[4]);
        assertTrue(gs.checkSelfOpted(include, tstLookup[4]));
        assertFalse(gs.checkSelfOpted(exclude, tstLookup[4]));
        assertTrue(gs.inGroup(grouping, tst[4]));

        gc.cancelOptIn(grouping, tst[4]);
        assertFalse(gs.checkSelfOpted(exclude, tstLookup[4]));
        assertFalse(gs.checkSelfOpted(include, tstLookup[4]));

        assertTrue(gs.inGroup(grouping, tst[5]));

        //reset Grouping
        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(gs.inGroup(grouping, tst[4]));
    }

    @Test
    public void optOutTest() {
        assertTrue(gs.inGroup(grouping, tst[5]));

        gc.optOut(grouping, tst[5]);
        assertTrue(gs.checkSelfOpted(exclude, tstLookup[5]));
        assertFalse(gs.checkSelfOpted(include, tstLookup[5]));
        assertFalse(gs.inGroup(grouping, tst[5]));

        gc.cancelOptOut(grouping, tst[5]);
        assertFalse(gs.checkSelfOpted(exclude, tstLookup[5]));
        assertFalse(gs.checkSelfOpted(include, tstLookup[5]));

        assertTrue(gs.inGroup(grouping + ":basis+include", tst[5]));
    }

    @Test
    public void changeListserveStatusTest() {
        assertTrue(gs.hasListserv(grouping));

        gc.setListserve(grouping, tst[0], false);
        assertFalse(gs.hasListserv(grouping));

        gc.setListserve(grouping, tst[0], true);
        assertTrue(gs.hasListserv(grouping));
    }

    @Test
    public void changeOptInTest() {
        assertTrue(gs.optInPermission(grouping));

        gc.setOptIn(grouping, tst[0], false);
        assertFalse(gs.optInPermission(grouping));

        gc.setOptIn(grouping, tst[0], true);
        assertTrue(gs.optInPermission(grouping));
    }

    @Test
    public void changeOptOutTest() {
        assertTrue(gs.optOutPermission(grouping));

        gc.setOptOut(grouping, tst[0], false);
        assertFalse(gs.optOutPermission(grouping));

        gc.setOptOut(grouping, tst[0], true);
        assertTrue(gs.optOutPermission(grouping));
    }
}

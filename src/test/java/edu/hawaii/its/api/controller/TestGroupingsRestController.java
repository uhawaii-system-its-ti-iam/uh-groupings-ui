package edu.hawaii.its.api.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import edu.hawaii.its.api.controller.GroupingsRestController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.api.service.GroupingsService;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.MyGroupings;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsRestController {

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
    }

    @Test
    public void testConstruction() {
        assertNotNull(gs);
        assertNotNull(gc);
    }

    @Test
    public void assignAndRemoveOwnershipTest() {
        Grouping g = gc.grouping(GROUPING, tst[0]).getBody();
        assertFalse(g.getOwners().getUsernames().contains(tst[1]));

        gc.assignOwnership(GROUPING, tst[0], tst[1]);
        g = gc.grouping(GROUPING, tst[0]).getBody();
        assertTrue(g.getOwners().getUsernames().contains(tst[1]));

        gc.removeOwnership(GROUPING, tst[0], tst[1]);
        g = gc.grouping(GROUPING, tst[0]).getBody();
        assertFalse(g.getOwners().getUsernames().contains(tst[1]));
    }

    @Test
    public void addMemberTest() {

        assertTrue(gs.inGroup(GROUPING_EXCLUDE, tst[4]));

        gc.addMemberToIncludeGroup(GROUPING, tst[0], tst[4]);
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING_INCLUDE, tst[4]));

        gc.addMemberToExcludeGroup(GROUPING, tst[0], tst[4]);
        assertFalse(gs.inGroup(GROUPING_INCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
    }

    @Test
    public void deleteMemberTest() {

        assertTrue(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
        gc.deleteMemberFromExcludeGroup(GROUPING, tst[0], tst[4]);
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING, tst[4]));

        assertTrue(gs.inGroup(GROUPING_INCLUDE, tst[1]));
        gc.deleteMemberFromIncludeGroup(GROUPING, tst[0], tst[1]);
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, tst[1]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, tst[1]));

        //reset Grouping
        gc.addMemberToExcludeGroup(GROUPING, tst[0], tst[4]);
        gc.addMemberToIncludeGroup(GROUPING, tst[0], tst[1]);
    }

    @Test
    public void getGroupingTest() {
        Grouping grouping = gc.grouping(this.GROUPING, tst[0]).getBody();

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

        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[0]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[1]));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[2]));
        assertFalse(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[3]));
        assertFalse(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains(tst[4]));

        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
        gc.assignOwnership(grouping.getPath(), tst[0], tst[5]);
        grouping = gc.grouping(this.GROUPING, tst[0]).getBody();
        assertTrue(grouping.getOwners().getNames().contains(tstName[5]));
        gc.removeOwnership(grouping.getPath(), tst[0], tst[5]);
        grouping = gc.grouping(this.GROUPING, tst[0]).getBody();
        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
    }

    @Test
    public void myGroupingsTest() {
        MyGroupings groupings = gc.myGroupings(tst[0]).getBody();

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
    public void myGroupingsTest2() {
        MyGroupings groupings = gc.myGroupings(tst[4]).getBody();

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
    public void myGroupingsTest3() {
        boolean optedIn = false;
        boolean optedOut = false;

        gc.optIn(GROUPING, tst[4]);
        MyGroupings tst4Groupings = gc.myGroupings(tst[4]).getBody();
        for (Grouping grouping : tst4Groupings.getGroupingsOptedInTo()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                optedIn = true;
            }
        }
        assertTrue(optedIn);

        gc.optOut(GROUPING, tst[5]);
        MyGroupings tst5Groupings = gc.myGroupings(tst[5]).getBody();
        for (Grouping grouping : tst5Groupings.getGroupingsOptedOutOf()) {
            if (grouping.getPath().contains(this.GROUPING)) {
                optedOut = true;
            }
        }
        assertTrue(optedOut);

        //reset Grouping
        gc.cancelOptIn(GROUPING, tst[4]);
        gc.addMemberToExcludeGroup(GROUPING, tst[0], tst[4]);
        gc.cancelOptOut(GROUPING, tst[5]);
    }


    @Test
    public void optInTest() {
        assertFalse(gs.inGroup(GROUPING, tst[4]));
        assertTrue(gs.inGroup(GROUPING + BASIS, tst[5]));

        gc.optIn(GROUPING, tst[4]);
        assertTrue(gs.checkSelfOpted(GROUPING_INCLUDE, tst[4]));
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[4]));
        assertTrue(gs.inGroup(GROUPING, tst[4]));

        gc.cancelOptIn(GROUPING, tst[4]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[4]));
        assertFalse(gs.checkSelfOpted(GROUPING_INCLUDE, tst[4]));

        assertTrue(gs.inGroup(GROUPING, tst[5]));

        //reset Grouping
        gc.addMemberToExcludeGroup(GROUPING, tst[0], tst[4]);
        assertFalse(gs.inGroup(GROUPING, tst[4]));
    }

    @Test
    public void optOutTest() {
        assertTrue(gs.inGroup(GROUPING, tst[5]));

        gc.optOut(GROUPING, tst[5]);
        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[5]));
        assertFalse(gs.checkSelfOpted(GROUPING_INCLUDE, tst[5]));
        assertFalse(gs.inGroup(GROUPING, tst[5]));

        gc.cancelOptOut(GROUPING, tst[5]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, tst[5]));
        assertFalse(gs.checkSelfOpted(GROUPING_INCLUDE, tst[5]));

        assertTrue(gs.inGroup(GROUPING + BASIS_PLUS_INCLUDE, tst[5]));
    }

    @Test
    public void changeListservStatusTest() {
        assertTrue(gs.hasListserv(GROUPING));

        gc.setListserv(GROUPING, tst[0], false);
        assertFalse(gs.hasListserv(GROUPING));

        gc.setListserv(GROUPING, tst[0], true);
        assertTrue(gs.hasListserv(GROUPING));
    }

    @Test
    public void changeOptInTest() {
        assertTrue(gs.optInPermission(GROUPING));

        gc.setOptIn(GROUPING, tst[0], false);
        assertFalse(gs.optInPermission(GROUPING));

        gc.setOptIn(GROUPING, tst[0], true);
        assertTrue(gs.optInPermission(GROUPING));
    }

    @Test
    public void changeOptOutTest() {
        assertTrue(gs.optOutPermission(GROUPING));

        gc.setOptOut(GROUPING, tst[0], false);
        assertFalse(gs.optOutPermission(GROUPING));

        gc.setOptOut(GROUPING, tst[0], true);
        assertTrue(gs.optOutPermission(GROUPING));
    }

    @Test
    public void aaronTest() {
        MyGroupings aaronsGroupings = gs.getMyGroupings("aaronvil");
        assertNotNull(aaronsGroupings);
    }

    @Test
    public void getEmptyGroupingTest() {

        Grouping storeEmpty = gs.getGrouping(GROUPING_STORE_EMPTY, tst[0]);
        Grouping trueEmpty = gs.getGrouping(GROUPING_TRUE_EMPTY, tst[0]);

        assertTrue(storeEmpty.getBasis().getMembers().size() == 1);
        assertTrue(storeEmpty.getBasisPlusIncludeMinusExclude().getMembers().size() == 0);
        assertTrue(storeEmpty.getExclude().getMembers().size() == 0);
        assertTrue(storeEmpty.getInclude().getMembers().size() == 0);
        assertTrue(storeEmpty.getOwners().getUsernames().contains(tst[0]));

        assertTrue(trueEmpty.getBasis().getMembers().size() == 0);
        assertTrue(trueEmpty.getBasisPlusIncludeMinusExclude().getMembers().size() == 0);
        assertTrue(trueEmpty.getExclude().getMembers().size() == 0);
        assertTrue(trueEmpty.getInclude().getMembers().size() == 0);
        assertTrue(trueEmpty.getOwners().getUsernames().contains(tst[0]));

    }

    @Test
    public void getAllGroupingsTest() {
        List<Grouping> groupings = gs.allGroupings(tst[0]);

        assertNotNull(groupings);
    }
}

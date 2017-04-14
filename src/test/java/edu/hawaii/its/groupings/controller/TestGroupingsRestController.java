package edu.hawaii.its.groupings.controller;
import edu.hawaii.its.holiday.api.GroupingsService;
import edu.hawaii.its.holiday.api.type.Grouping;
import edu.hawaii.its.holiday.api.type.MyGroupings;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
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
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsRestController {
    private String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
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
    public void assignOwnershipTest() {
        gc.assignOwnership(grouping, tst[0], tst[1]);

        String group = include;
        String privilegeName = "update";

        WsGetGrouperPrivilegesLiteResult updateInclude =
                gs.grouperPrivilegesLite(tst[1], group, privilegeName);

        group = exclude;
        WsGetGrouperPrivilegesLiteResult updateExclude =
                gs.grouperPrivilegesLite(tst[1], group, privilegeName);

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        //reset Grouping
        gc.removeOwnership(grouping, tst[0], tst[1]);
    }

    @Test
    public void removeOwnershipTest() {
        gc.assignOwnership(grouping, tst[0], tst[1]);

        String group = include;
        String privilegeName = "update";

        WsGetGrouperPrivilegesLiteResult updateInclude =
                gs.grouperPrivilegesLite(tst[1], group, privilegeName);

        group = exclude;
        WsGetGrouperPrivilegesLiteResult updateExclude =
                gs.grouperPrivilegesLite(tst[1], group, privilegeName);

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        gc.removeOwnership(grouping, tst[0], tst[1]);

        group = include;

        updateInclude = gs.grouperPrivilegesLite(tst[1], group, privilegeName);

        group = exclude;
        updateExclude = gs.grouperPrivilegesLite(tst[1], group, privilegeName);

        assertFalse(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertFalse(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
    }

    @Test
    public void addMemberTest(){

        assertTrue(gs.inGroup(exclude, tst[4]));

        gc.addMemberToIncludeGroup(grouping, tst[0], tst[4]);
        assertFalse(gs.inGroup(exclude, tst[4]));
        assertTrue(gs.inGroup(include, tst[4]));

        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(gs.inGroup(include, tst[4]));
        assertTrue(gs.inGroup(exclude, tst[4]));
    }

    @Test
    public void deleteMemberTest(){

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
    public void myGroupingsTest(){
        MyGroupings groupings = gc.myGroupings(tst[0]).getBody();
        boolean inGrouping = false;
        boolean canOptin = false;
        boolean canOptOut = false;
        boolean ownsGrouping = false;

        for (Grouping grouping : groupings.getGroupingsIn()){
            if(grouping.getPath().contains(this.grouping)){
                inGrouping = true;
            }
        }
        for (Grouping grouping : groupings.getGroupingsToOptInTo()) {
            if (grouping.getPath().contains(this.grouping)) {
                canOptin = true;
            }
        }
        for (Grouping grouping : groupings.getGroupingsToOptOutOf()) {
            if (grouping.getPath().contains(this.grouping)) {
                canOptOut = true;
            }
        }
        for (Grouping grouping : groupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(this.grouping)) {
                ownsGrouping = true;
            }
        }

        assertTrue(inGrouping);
        assertTrue(canOptin);
        assertTrue(canOptOut);
        assertTrue(ownsGrouping);

    }


    @Test
    public void myGroupingsTest2(){
        MyGroupings groupings = gc.myGroupings(tst[4]).getBody();
        boolean inGrouping = false;
        boolean ownsGrouping = false;

        for (Grouping grouping : groupings.getGroupingsIn()){
            if(grouping.getPath().contains(this.grouping)){
                inGrouping = true;
            }
        }
        for (Grouping grouping : groupings.getGroupingsOwned()) {
            if (grouping.getPath().contains(this.grouping)) {
                ownsGrouping = true;
            }
        }

        assertFalse(inGrouping);
        assertFalse(ownsGrouping);

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
    public void changeListserveStatusTest(){
        assertTrue(gs.hasListServe(grouping));

        gc.setListserve(grouping, tst[0], false);
        assertFalse(gs.hasListServe(grouping));

        gc.setListserve(grouping, tst[0], true);
        assertTrue(gs.hasListServe(grouping));
    }

    @Test
    public void changeOptInTest(){
        assertTrue(gs.optInPermission(grouping));

        gc.setOptIn(grouping, tst[0], false);
        assertFalse(gs.optInPermission(grouping));

        gc.setOptIn(grouping, tst[0], true);
        assertTrue(gs.optInPermission(grouping));
    }

    @Test
    public void changeOptOutTest(){
        assertTrue(gs.optOutPermission(grouping));

        gc.setOptOut(grouping, tst[0], false);
        assertFalse(gs.optOutPermission(grouping));

        gc.setOptOut(grouping, tst[0], true);
        assertTrue(gs.optOutPermission(grouping));
    }
}

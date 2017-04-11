package edu.hawaii.its.holiday.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import edu.hawaii.its.holiday.api.type.Group;
import edu.hawaii.its.holiday.api.type.MyGroupings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.holiday.api.type.Grouping;
import edu.hawaii.its.holiday.api.GroupingsService;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

/**
 * Created by zac on 1/31/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsController {

    private String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private String include = grouping + ":include";
    private String exclude = grouping + ":exclude";
    private String aaron = "aaronvil";
    private String zac = "zknoebel";

    private WsSubjectLookup[] lookupTst = new WsSubjectLookup[6];

    @Autowired
    private GroupingsService gs;

    @Autowired
    private GroupingsController gc;

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
            lookupTst[i] = gs.makeWsSubjectLookup("iamtst0" + (i + 1));
        }
    }

    @Test
    public void testConstruction() {
        assertNotNull(gs);
        assertNotNull(gc);
    }


    @Test
    public void assignOwnershipTest() {
        gc.assignOwnership(grouping, zac, aaron);

        String group = include;
        String privilegeName = "update";

        WsGetGrouperPrivilegesLiteResult updateInclude =
                gs.grouperPrivilegesLite(aaron, group, privilegeName);

        group = exclude;
        WsGetGrouperPrivilegesLiteResult updateExclude =
                gs.grouperPrivilegesLite(aaron, group, privilegeName);

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        //reset Grouping
        gc.removeOwnership(grouping, zac, aaron);
    }

    @Test
    public void removeOwnershipTest() {
        gc.assignOwnership(grouping, zac, aaron);

        String group = include;
        String privilegeName = "update";

        WsGetGrouperPrivilegesLiteResult updateInclude =
                gs.grouperPrivilegesLite(aaron, group, privilegeName);

        group = exclude;
        WsGetGrouperPrivilegesLiteResult updateExclude =
                gs.grouperPrivilegesLite(aaron, group, privilegeName);

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        gc.removeOwnership(grouping, zac, aaron);

        group = include;

        updateInclude = gs.grouperPrivilegesLite(aaron, group, privilegeName);

        group = exclude;
        updateExclude = gs.grouperPrivilegesLite(aaron, group, privilegeName);

        assertFalse(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertFalse(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
    }

    @Test
    public void addMemberTest(){

        assertTrue(gs.inGroup(exclude, "iamtst05"));

        gc.addMemberToIncludeGroup(grouping, zac, "iamtst05");
        assertFalse(gs.inGroup(exclude, "iamtst05"));
        assertTrue(gs.inGroup(include, "iamtst05"));

        gc.addMemberToExcludeGroup(grouping, zac, "iamtst05");
        assertFalse(gs.inGroup(include, "iamtst05"));
        assertTrue(gs.inGroup(exclude, "iamtst05"));
    }

    @Test
    public void deleteMemberTest(){

        assertTrue(gs.inGroup(exclude, "iamtst05"));
        gc.deleteMemberFromExcludeGroup(grouping, zac, "iamtst05");
        assertFalse(gs.inGroup(exclude, "iamtst05"));
        assertTrue(gs.inGroup(grouping, "iamtst05"));


        assertTrue(gs.inGroup(include, "iamtst01"));
        gc.deleteMemberFromIncludeGroup(grouping, zac, "iamtst01");
        assertFalse(gs.inGroup(exclude, "iamtst01"));
        assertFalse(gs.inGroup(include, "iamtst01"));

        //reset Grouping
        gc.addMemberToExcludeGroup(grouping, zac, "iamtst05");
        gc.addMemberToIncludeGroup(grouping, zac, "iamtst01");
    }

    @Test
    public void getGroupingTest() {
        Grouping grouping = gc.getGrouping(this.grouping, zac);

        assertTrue(grouping.getBasis().getNames().contains("tst04name"));
        assertTrue(grouping.getBasis().getNames().contains("tst05name"));
        assertTrue(grouping.getBasis().getNames().contains("tst06name"));
        assertTrue(grouping.getInclude().getNames().contains("tst01fname"));
        assertTrue(grouping.getInclude().getNames().contains("tst02name"));
        assertTrue(grouping.getInclude().getNames().contains("tst03name"));
        assertTrue(grouping.getExclude().getNames().contains("tst04name"));
        assertTrue(grouping.getExclude().getNames().contains("tst05name"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains("tst01fname"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains("tst02name"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains("tst03name"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getNames().contains("tst06name"));


        assertTrue(grouping.getBasis().getUsernames().contains("iamtst04"));
        assertTrue(grouping.getBasis().getUsernames().contains("iamtst05"));
        assertTrue(grouping.getBasis().getUsernames().contains("iamtst06"));
        assertTrue(grouping.getInclude().getUsernames().contains("iamtst01"));
        assertTrue(grouping.getInclude().getUsernames().contains("iamtst02"));
        assertTrue(grouping.getInclude().getUsernames().contains("iamtst03"));
        assertTrue(grouping.getExclude().getUsernames().contains("iamtst04"));
        assertTrue(grouping.getExclude().getUsernames().contains("iamtst05"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains("iamtst01"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains("iamtst02"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains("iamtst03"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUsernames().contains("iamtst06"));

        assertTrue(grouping.getBasis().getUuids().contains("iamtst04"));
        assertTrue(grouping.getBasis().getUuids().contains("iamtst05"));
        assertTrue(grouping.getBasis().getUuids().contains("iamtst06"));
        assertTrue(grouping.getInclude().getUuids().contains("iamtst01"));
        assertTrue(grouping.getInclude().getUuids().contains("iamtst02"));
        assertTrue(grouping.getInclude().getUuids().contains("iamtst03"));
        assertTrue(grouping.getExclude().getUuids().contains("iamtst04"));
        assertTrue(grouping.getExclude().getUuids().contains("iamtst05"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains("iamtst01"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains("iamtst02"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains("iamtst03"));
        assertTrue(grouping.getBasisPlusIncludeMinusExclude().getUuids().contains("iamtst06"));

        assertFalse(grouping.getOwners().getNames().contains("tst06name"));
        gc.assignOwnership(grouping.getPath(), zac, "iamtst06");
        grouping = gc.getGrouping(this.grouping, zac);
        assertTrue(grouping.getOwners().getNames().contains("tst06name"));
        gc.removeOwnership(grouping.getPath(), zac, "iamtst06");
        grouping = gc.getGrouping(this.grouping, zac);
        assertFalse(grouping.getOwners().getNames().contains("tst06name"));
    }

    @Test
    public void myGroupingsTest(){
        MyGroupings groupings = gc.myGroupings(aaron);
        boolean inGrouping = false;
        boolean canOptin = false;
        boolean canOptOut = false;

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

        assertTrue(inGrouping);
        assertTrue(canOptin);
        assertTrue(canOptOut);

        //TODO add test for groupingsOwned

    }


    @Test
    public void optInTest() {
        assertFalse(gs.inGroup(grouping, "iamtst05"));
        assertTrue(gs.inGroup(grouping + ":basis", "iamtst06"));

        gc.optIn("iamtst05", grouping);
        assertTrue(gs.checkSelfOpted(include, lookupTst[4]));
        assertFalse(gs.checkSelfOpted(exclude, lookupTst[4]));
        assertTrue(gs.inGroup(grouping, "iamtst05"));

        gc.cancelOptIn(grouping, "iamtst05");
        assertFalse(gs.checkSelfOpted(exclude, lookupTst[4]));
        assertFalse(gs.checkSelfOpted(include, lookupTst[4]));

        assertTrue(gs.inGroup(grouping, "iamtst06"));

        //reset Grouping
        gc.addMemberToExcludeGroup(grouping, zac, "iamtst05");
        assertFalse(gs.inGroup(grouping, "iamtst05"));
    }
    

    @Test
    public void optOutTest() {
        assertTrue(gs.inGroup(grouping, "iamtst06"));

        gc.optOut("iamtst06", grouping);
        assertTrue(gs.checkSelfOpted(exclude, lookupTst[5]));
        assertFalse(gs.checkSelfOpted(include, lookupTst[5]));
        assertFalse(gs.inGroup(grouping, "iamtst06"));

        gc.cancelOptOut(grouping, "iamtst06");
        assertFalse(gs.checkSelfOpted(exclude, lookupTst[5]));
        assertFalse(gs.checkSelfOpted(include, lookupTst[5]));

        assertTrue(gs.inGroup(grouping + ":basis+include", "iamtst06"));
    }


}

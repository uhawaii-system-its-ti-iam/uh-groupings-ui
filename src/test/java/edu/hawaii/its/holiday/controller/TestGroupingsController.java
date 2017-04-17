package edu.hawaii.its.holiday.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.groupings.api.GroupingsService;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupingsController {

    private String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private String include = grouping + ":include";
    private String exclude = grouping + ":exclude";
    private String[] tst = new String[6];
    private String[] tstName = { "tst01fname", "tst02name", "tst03name", "tst04name", "tst05name", "tst06name" };

    private WsSubjectLookup[] tstLookup = new WsSubjectLookup[6];

    @Autowired
    private GroupingsService service;

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
            tst[i] = "iamtst0" + (i + 1);
            tstLookup[i] = service.makeWsSubjectLookup(tst[i]);
        }
    }

    @Test
    public void testConstruction() {
        assertNotNull(service);
        assertNotNull(gc);
    }

    @Test
    public void assignOwnershipTest() {
        gc.assignOwnership(grouping, tst[0], tst[1]);

        String group = include;
        String privilegeName = "update";

        WsGetGrouperPrivilegesLiteResult updateInclude =
                service.grouperPrivilegesLite(tst[1], privilegeName, group);

        group = exclude;
        WsGetGrouperPrivilegesLiteResult updateExclude =
                service.grouperPrivilegesLite(tst[1], privilegeName, group);

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
                service.grouperPrivilegesLite(tst[1], privilegeName, group);

        group = exclude;
        WsGetGrouperPrivilegesLiteResult updateExclude =
                service.grouperPrivilegesLite(tst[1], privilegeName, group);

        assertTrue(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertTrue(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));

        gc.removeOwnership(grouping, tst[0], tst[1]);

        group = include;

        updateInclude = service.grouperPrivilegesLite(tst[1], privilegeName, group);

        group = exclude;
        updateExclude = service.grouperPrivilegesLite(tst[1], privilegeName, group);

        assertFalse(updateInclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
        assertFalse(updateExclude.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED"));
    }

    @Test
    public void addMemberTest() {

        assertTrue(service.inGroup(exclude, tst[4]));

        gc.addMemberToIncludeGroup(grouping, tst[0], tst[4]);
        assertFalse(service.inGroup(exclude, tst[4]));
        assertTrue(service.inGroup(include, tst[4]));

        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(service.inGroup(include, tst[4]));
        assertTrue(service.inGroup(exclude, tst[4]));
    }

    @Test
    public void deleteMemberTest() {

        assertTrue(service.inGroup(exclude, tst[4]));
        gc.deleteMemberFromExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(service.inGroup(exclude, tst[4]));
        assertTrue(service.inGroup(grouping, tst[4]));

        assertTrue(service.inGroup(include, tst[1]));
        gc.deleteMemberFromIncludeGroup(grouping, tst[0], tst[1]);
        assertFalse(service.inGroup(exclude, tst[1]));
        assertFalse(service.inGroup(include, tst[1]));

        //reset Grouping
        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        gc.addMemberToIncludeGroup(grouping, tst[0], tst[1]);
    }

    @Test
    public void getGroupingTest() {
        Grouping grouping = gc.getGrouping(this.grouping, tst[0]).getBody();

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
        grouping = gc.getGrouping(this.grouping, tst[0]).getBody();
        assertTrue(grouping.getOwners().getNames().contains(tstName[5]));
        gc.removeOwnership(grouping.getPath(), tst[0], tst[5]);
        grouping = gc.getGrouping(this.grouping, tst[0]).getBody();
        assertFalse(grouping.getOwners().getNames().contains(tstName[5]));
    }

    @Test
    public void myGroupingsTest() {
        ResponseEntity<MyGroupings> response = gc.myGroupings(tst[0]);
        MyGroupings groupings = response.getBody();

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
    public void optInTest() {
        assertFalse(service.inGroup(grouping, tst[4]));
        assertTrue(service.inGroup(grouping + ":basis", tst[5]));

        gc.optIn(grouping, tst[4]);
        assertTrue(service.checkSelfOpted(include, tstLookup[4]));
        assertFalse(service.checkSelfOpted(exclude, tstLookup[4]));
        assertTrue(service.inGroup(grouping, tst[4]));

        gc.cancelOptIn(grouping, tst[4]);
        assertFalse(service.checkSelfOpted(exclude, tstLookup[4]));
        assertFalse(service.checkSelfOpted(include, tstLookup[4]));

        assertTrue(service.inGroup(grouping, tst[5]));

        //reset Grouping
        gc.addMemberToExcludeGroup(grouping, tst[0], tst[4]);
        assertFalse(service.inGroup(grouping, tst[4]));
    }

    @Test
    public void optOutTest() {
        assertTrue(service.inGroup(grouping, tst[5]));

        gc.optOut(grouping, tst[5]);
        assertTrue(service.checkSelfOpted(exclude, tstLookup[5]));
        assertFalse(service.checkSelfOpted(include, tstLookup[5]));
        assertFalse(service.inGroup(grouping, tst[5]));

        gc.cancelOptOut(grouping, tst[5]);
        assertFalse(service.checkSelfOpted(exclude, tstLookup[5]));
        assertFalse(service.checkSelfOpted(include, tstLookup[5]));

        assertTrue(service.inGroup(grouping + ":basis+include", tst[5]));
    }

}

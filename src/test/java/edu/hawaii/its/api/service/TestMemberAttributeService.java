package edu.hawaii.its.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestMemberAttributeService {

    @Value("${groupings.api.test.grouping_many}")
    private String GROUPING;
    @Value("${groupings.api.test.grouping_many_basis}")
    private String GROUPING_BASIS;
    @Value("${groupings.api.test.grouping_many_include}")
    private String GROUPING_INCLUDE;
    @Value("${groupings.api.test.grouping_many_exclude}")
    private String GROUPING_EXCLUDE;
    @Value("${groupings.api.test.grouping_many_owners}")
    private String GROUPING_OWNERS;

    @Value("${groupings.api.success}")
    private String SUCCESS;

    @Value("${groupings.api.test.usernames}")
    private String[] username;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Autowired
    GroupAttributeService groupAttributeService;

    @Autowired
    GroupingAssignmentService gas;

    @Autowired
    private MemberAttributeService gs;

    @Autowired
    private MembershipService ms;

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
        groupAttributeService.changeListservStatus(GROUPING, username[0], true);
        groupAttributeService.changeOptInStatus(GROUPING, username[0], true);
        groupAttributeService.changeOptOutStatus(GROUPING, username[0], true);

        //put in include
        ms.addGroupingMemberByUsername(username[0], GROUPING, username[0]);
        ms.addGroupingMemberByUsername(username[0], GROUPING, username[1]);
        ms.addGroupingMemberByUsername(username[0], GROUPING, username[2]);

        //remove from exclude
        ms.addGroupingMemberByUsername(username[0], GROUPING, username[4]);
        ms.addGroupingMemberByUsername(username[0], GROUPING, username[5]);

        //add to exclude
        ms.deleteGroupingMemberByUsername(username[0], GROUPING, username[3]);
    }

    @Test
    public void isOwnerTest() {
        assertTrue(gs.isOwner(GROUPING, username[0]));
    }

    @Test
    public void assignRemoveOwnershipTest() {
        //expect to fail
        GroupingsServiceResult assignOwnershipFail;
        GroupingsServiceResult removeOwnershipFail;

        assertTrue(gs.isOwner(GROUPING, username[0]));
        assertFalse(gs.isOwner(GROUPING, username[1]));
        assertFalse(gs.isOwner(GROUPING, username[2]));

        try {
            assignOwnershipFail = gs.assignOwnership(GROUPING, username[1], username[1]);
        } catch (GroupingsServiceResultException gsre) {
            assignOwnershipFail = gsre.getGsr();
        }
        assertFalse(gs.isOwner(GROUPING, username[1]));
        assertTrue(assignOwnershipFail.getResultCode().startsWith(FAILURE));

        GroupingsServiceResult assignOwnershipSuccess = gs.assignOwnership(GROUPING, username[0], username[1]);
        assertTrue(gs.isOwner(GROUPING, username[1]));
        assertTrue(assignOwnershipSuccess.getResultCode().startsWith(SUCCESS));

        try {
            removeOwnershipFail = gs.removeOwnership(GROUPING, username[2], username[1]);
        } catch (GroupingsServiceResultException gsre) {
            removeOwnershipFail = gsre.getGsr();
        }

        assertTrue(gs.isOwner(GROUPING, username[1]));
        assertTrue(removeOwnershipFail.getResultCode().startsWith(FAILURE));

        GroupingsServiceResult removeOwnershipSuccess = gs.removeOwnership(GROUPING, username[0], username[1]);
        assertFalse(gs.isOwner(GROUPING, username[1]));
        assertTrue(removeOwnershipSuccess.getResultCode().startsWith(SUCCESS));

        //have an owner remove itself
        assignOwnershipSuccess = gs.assignOwnership(GROUPING, username[0], username[1]);
        assertTrue(gs.isOwner(GROUPING, username[1]));
        removeOwnershipSuccess = gs.removeOwnership(GROUPING, username[1], username[1]);
        assertFalse(gs.isOwner(GROUPING, username[1]));

    }

    @Test
    public void inGroupTest() {
        assertTrue(gs.isMember(GROUPING_INCLUDE, username[1]));
        assertFalse(gs.isMember(GROUPING_INCLUDE, username[3]));

        assertTrue(gs.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(gs.isMember(GROUPING_EXCLUDE, username[1]));
    }

}

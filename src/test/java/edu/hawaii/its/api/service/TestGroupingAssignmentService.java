package edu.hawaii.its.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupingAssignmentService {

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

    @Value("${groupings.api.test.grouping_store_empty}")
    private String GROUPING_STORE_EMPTY;
    @Value("${groupings.api.test.grouping_store_empty_include}")
    private String GROUPING_STORE_EMPTY_INCLUDE;
    @Value("${groupings.api.test.grouping_store_empty_exclude}")
    private String GROUPING_STORE_EMPTY_EXCLUDE;
    @Value("${groupings.api.test.grouping_store_empty_owners}")
    private String GROUPING_STORE_EMPTY_OWNERS;

    @Value("${groupings.api.test.grouping_true_empty}")
    private String GROUPING_TRUE_EMPTY;
    @Value("${groupings.api.test.grouping_true_empty_include}")
    private String GROUPING_TRUE_EMPTY_INCLUDE;
    @Value("${groupings.api.test.grouping_true_empty_exclude}")
    private String GROUPING_TRUE_EMPTY_EXCLUDE;
    @Value("${groupings.api.test.grouping_true_empty_owners}")
    private String GROUPING_TRUE_EMPTY_OWNERS;

    @Value("${groupings.api.yyyymmddThhmm}")
    private String YYYYMMDDTHHMM;

    @Value("${groupings.api.trio}")
    private String TRIO;

    @Value("${groupings.api.assign_type_group}")
    private String ASSIGN_TYPE_GROUP;

    @Value("${groupings.api.test.usernames}")
    private String[] username;

    @Value("${groupings.api.grouping_admins}")

    private String GROUPING_ADMINS;
    @Autowired
    GroupAttributeService groupAttributeService;

    @Autowired
    GroupingAssignmentService groupingAssignmentService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private HelperService helperService;

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
        membershipService.addGroupingMemberByUsername(username[0], GROUPING, username[0]);
        membershipService.addGroupingMemberByUsername(username[0], GROUPING, username[1]);
        membershipService.addGroupingMemberByUsername(username[0], GROUPING, username[2]);

        //remove from exclude
        membershipService.addGroupingMemberByUsername(username[0], GROUPING, username[4]);
        membershipService.addGroupingMemberByUsername(username[0], GROUPING, username[5]);

        //add to exclude
        membershipService.deleteGroupingMemberByUsername(username[0], GROUPING, username[3]);
    }

    @Test
    public void adminListsTest() {
        //try with non-admin
        AdminListsHolder info = groupingAssignmentService.adminLists(username[0]);
        assertNotNull(info);
        assertEquals(info.getAllGroupings().size(), 0);
        assertEquals(info.getAdminGroup().getMembers().size(), 0);
        assertEquals(info.getAdminGroup().getUsernames().size(), 0);
        assertEquals(info.getAdminGroup().getNames().size(), 0);
        assertEquals(info.getAdminGroup().getUuids().size(), 0);

        //try with admin
        AdminListsHolder infoAdmin = groupingAssignmentService.adminLists(GROUPING_ADMINS);
        assertNotNull(infoAdmin);



    }

    @Test
    public void updateLastModifiedTest() {
        // Test is accurate to the minute, and if checks to see if the current
        // time gets added to the lastModified attribute of a group if the
        // minute happens to change in between getting the time and setting
        // the time, the test will fail.

        final String group = GROUPING_INCLUDE;

        GroupingsServiceResult gsr = membershipService.updateLastModified(group);
        String dateStr = gsr.getAction().split(" to time ")[1];

        WsGetAttributeAssignmentsResults assignments =
                groupAttributeService.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, group, YYYYMMDDTHHMM);
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();

        assertEquals(dateStr, assignedValue);
    }

    @Test
    public void getGroupingTest() {
        Grouping grouping = groupingAssignmentService.getGrouping(GROUPING, username[4]);
        assertEquals(grouping.getPath(), "");
        assertEquals(grouping.getName(), "");
        assertEquals(grouping.getOwners().getMembers().size(), 0);
        assertEquals(grouping.getInclude().getMembers().size(), 0);
        assertEquals(grouping.getExclude().getMembers().size(), 0);
        assertEquals(grouping.getBasis().getMembers().size(), 0);
        assertEquals(grouping.getComposite().getMembers().size(), 0);

        grouping = groupingAssignmentService.getGrouping(GROUPING, username[0]);

        assertEquals(grouping.getPath(), GROUPING);

        assertTrue(grouping.getBasis().getUsernames().contains(username[3]));
        assertTrue(grouping.getBasis().getUsernames().contains(username[4]));
        assertTrue(grouping.getBasis().getUsernames().contains(username[5]));

        assertTrue(grouping.getComposite().getUsernames().contains(username[0]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[1]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[2]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[4]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[5]));

        assertTrue(grouping.getExclude().getUsernames().contains(username[3]));

        assertTrue(grouping.getInclude().getUsernames().contains(username[0]));
        assertTrue(grouping.getInclude().getUsernames().contains(username[1]));
        assertTrue(grouping.getInclude().getUsernames().contains(username[2]));

        assertTrue(grouping.getOwners().getUsernames().contains(username[0]));
    }

    @Test
    public void groupingsInTest() {
        GroupingAssignment groupingAssignment = groupingAssignmentService.getGroupingAssignment(username[0]);
        boolean inGrouping = false;

        for (Grouping grouping : groupingAssignment.getGroupingsIn()) {
            if (grouping.getPath().contains(GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertTrue(inGrouping);

        inGrouping = false;
        groupingAssignment = groupingAssignmentService.getGroupingAssignment(username[3]);
        for (Grouping grouping : groupingAssignment.getGroupingsIn()) {
            if (grouping.getPath().contains(GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertFalse(inGrouping);
    }

    @Test
    public void groupingsOwnedTest() {
        GroupingAssignment groupingAssignment = groupingAssignmentService.getGroupingAssignment(username[0]);
        boolean ownsGrouping = false;

        for (Grouping grouping : groupingAssignment.getGroupingsOwned()) {
            if (grouping.getPath().contains(GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertTrue(ownsGrouping);

        ownsGrouping = false;
        groupingAssignment = groupingAssignmentService.getGroupingAssignment(username[4]);
        for (Grouping grouping : groupingAssignment.getGroupingsOwned()) {
            if (grouping.getPath().contains(GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertFalse(ownsGrouping);
    }

    @Test
    public void groupingsOptedTest() {
        //todo
    }

    @Test
    public void groupingsToOptTest() {
        GroupingAssignment groupingAssignment = groupingAssignmentService.getGroupingAssignment(username[0]);

        boolean canOptIn = false;
        for (Grouping grouping : groupingAssignment.getGroupingsToOptInTo()) {
            if (grouping.getPath().contains(GROUPING)) {
                canOptIn = true;
                break;
            }
        }
        assertFalse(canOptIn);

        boolean canOptOut = false;
        for (Grouping grouping : groupingAssignment.getGroupingsToOptOutOf()) {
            if (grouping.getPath().contains(GROUPING)) {
                canOptOut = true;
                break;
            }
        }
        assertTrue(canOptOut);
    }

    @Test
    public void getMembersTest() {
        Group group = groupingAssignmentService.getMembers(username[0], GROUPING);
        List<String> usernames = group.getUsernames();

        assertTrue(usernames.contains(username[0]));
        assertTrue(usernames.contains(username[1]));
        assertTrue(usernames.contains(username[2]));
        assertFalse(usernames.contains(username[3]));
        assertTrue(usernames.contains(username[4]));
        assertTrue(usernames.contains(username[5]));
    }

    @Test
    public void getGroupNamesTest() {
        List<String> groupNames1 = groupingAssignmentService.getGroupPaths(username[1]);
        List<String> groupNames3 = groupingAssignmentService.getGroupPaths(username[3]);

        //username[1] should be in the composite and the include, not basis or exclude
        assertTrue(groupNames1.contains(GROUPING));
        assertTrue(groupNames1.contains(GROUPING_INCLUDE));
        assertFalse(groupNames1.contains(GROUPING_BASIS));
        assertFalse(groupNames1.contains(GROUPING_EXCLUDE));

        //username[3] should be in the basis and exclude, not the composite or include
        assertTrue(groupNames3.contains(GROUPING_BASIS));
        assertTrue(groupNames3.contains(GROUPING_EXCLUDE));
        assertFalse(groupNames3.contains(GROUPING));
        assertFalse(groupNames3.contains(GROUPING_INCLUDE));
    }

    @Test
    public void getGroupNames() {
        List<String> groups = groupingAssignmentService.getGroupPaths(username[0]);

        assertTrue(groups.contains(GROUPING_OWNERS));
        assertTrue(groups.contains(GROUPING_STORE_EMPTY_OWNERS));
        assertTrue(groups.contains(GROUPING_TRUE_EMPTY_OWNERS));

        List<String> groups2 = groupingAssignmentService.getGroupPaths(username[1]);

        assertFalse(groups2.contains(GROUPING_OWNERS));
        assertFalse(groups2.contains(GROUPING_STORE_EMPTY_OWNERS));
        assertFalse(groups2.contains(GROUPING_TRUE_EMPTY_OWNERS));
    }

    @Test
    public void grouperTest() {
        List<String> groupPaths = groupingAssignmentService.getGroupPaths(username[0]);

        List<String> groupings = new ArrayList<>();
        List<String> groupings2 = new ArrayList<>();

        if (groupPaths.size() > 0) {

            List<WsAttributeAssign> attributes = new ArrayList<>();

            for (String path : groupPaths) {
                WsGetAttributeAssignmentsResults trioGroups = new GcGetAttributeAssignments()
                        .addAttributeDefNameName(TRIO)
                        .assignAttributeAssignType(ASSIGN_TYPE_GROUP)
                        .addOwnerGroupName(path)
                        .execute();

                if (trioGroups.getWsAttributeAssigns() != null) {
                    Collections.addAll(attributes, trioGroups.getWsAttributeAssigns());
                }
            }

            if (attributes.size() > 0) {
                groupings.addAll(attributes.stream().map(WsAttributeAssign::getOwnerGroupName)
                        .collect(Collectors.toList()));
            }

            assertNotNull(groupings);

            //////////////////////////////////////////////////////////////////////////////////

            GcGetAttributeAssignments trioGroups2 = new GcGetAttributeAssignments()
                    .addAttributeDefNameName(TRIO)
                    .assignAttributeAssignType(ASSIGN_TYPE_GROUP);

            groupPaths.forEach(trioGroups2::addOwnerGroupName);

            WsGetAttributeAssignmentsResults attributeAssignmentsResults2 = trioGroups2.execute();

            assertNotNull(attributeAssignmentsResults2);

            WsAttributeAssign[] wsGroups2 = attributeAssignmentsResults2.getWsAttributeAssigns();

            if (wsGroups2 != null && wsGroups2.length > 0) {
                for (WsAttributeAssign grouping : wsGroups2) {
                    groupings2.add(grouping.getOwnerGroupName());
                }
            }
        }

        assertNotNull(groupings2);

    }

    @Test
    public void makeGroupingsTest() {
        List<String> groupingPaths = new ArrayList<>();
        groupingPaths.add(GROUPING);
        groupingPaths.add(GROUPING_STORE_EMPTY);
        groupingPaths.add(GROUPING_TRUE_EMPTY);

        List<Grouping> groupings = helperService.makeGroupings(groupingPaths);

        assertTrue(groupings.size() == 3);
    }
}

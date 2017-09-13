package edu.hawaii.its.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import edu.hawaii.its.api.type.*;
import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.ws.beans.*;
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

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import static org.junit.Assert.*;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsService {

    @Value("${groupings.api.test.grouping_many}")
    private String GROUPING;
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

    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.attributes}")
    private String ATTRIBUTES;

    @Value("${groupings.api.success}")
    private String SUCCESS;

    @Value("${groupings.api.for_groups}")
    private String FOR_GROUPS;

    @Value("${groupings.api.for_memberships}")
    private String FOR_MEMBERSHIPS;

    @Value("${groupings.api.last_modified}")
    private String LAST_MODIFIED;

    @Value("${groupings.api.yyyymmddThhmm}")
    private String YYYYMMDDTHHMM;

    @Value("${groupings.api.uhgrouping}")
    private String UHGROUPING;

    @Value("${groupings.api.destinations}")
    private String DESTINATIONS;

    @Value("${groupings.api.listserv}")
    private String LISTSERV;

    @Value("${groupings.api.trio}")
    private String TRIO;

    @Value("${groupings.api.self_opted}")
    private String SELF_OPTED;

    @Value("${groupings.api.anyone_can}")
    private String ANYONE_CAN;

    @Value("${groupings.api.opt_in}")
    private String OPT_IN;

    @Value("${groupings.api.opt_out}")
    private String OPT_OUT;

    @Value("${groupings.api.basis}")
    private String BASIS;

    @Value("${groupings.api.basis_plus_include}")
    private String BASIS_PLUS_INCLUDE;

    @Value("${groupings.api.exclude}")
    private String EXCLUDE;

    @Value("${groupings.api.include}")
    private String INCLUDE;

    @Value("${groupings.api.owners}")
    private String OWNERS;

    @Value("${groupings.api.assign_type_group}")
    private String ASSIGN_TYPE_GROUP;

    @Value("${groupings.api.assign_type_immediate_membership}")
    private String ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP;

    @Value("${groupings.api.subject_attribute_name_uuid}")
    private String SUBJECT_ATTRIBUTE_NAME_UID;

    @Value("${groupings.api.operation_assign_attribute}")
    private String OPERATION_ASSIGN_ATTRIBUTE;

    @Value("${groupings.api.operation_remove_attribute}")
    private String OPERATION_REMOVE_ATTRIBUTE;

    @Value("${groupings.api.operation_replace_values}")
    private String OPERATION_REPLACE_VALUES;

    @Value("${groupings.api.privilege_opt_out}")
    private String PRIVILEGE_OPT_OUT;

    @Value("${groupings.api.privilege_opt_in}")
    private String PRIVILEGE_OPT_IN;

    @Value("${groupings.api.test.usernames}")
    private String[] username;

    @Value("${groupings.api.failure}")
    private String FAILURE;

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
        gs.changeListservStatus(GROUPING, username[0], true);
        gs.changeOptInStatus(GROUPING, username[0], true);
        gs.changeOptOutStatus(GROUPING, username[0], true);

        gs.addMemberAs(username[0], GROUPING_INCLUDE, username[0]);
        gs.deleteMemberAs(username[0], GROUPING_EXCLUDE, username[0]);

        gs.addMemberAs(username[0], GROUPING_INCLUDE, username[1]);
        gs.deleteMemberAs(username[0], GROUPING_EXCLUDE, username[1]);

        gs.addMemberAs(username[0], GROUPING_INCLUDE, username[2]);
        gs.deleteMemberAs(username[0], GROUPING_EXCLUDE, username[2]);

        gs.addMemberAs(username[0], GROUPING_EXCLUDE, username[3]);
        gs.deleteMemberAs(username[0], GROUPING_INCLUDE, username[3]);

        gs.addMemberAs(username[0], GROUPING_EXCLUDE, username[4]);
        gs.deleteMemberAs(username[0], GROUPING_INCLUDE, username[4]);
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
    public void adminInfoTest() {
        AdminListsHolder info = gs.adminLists(username[0]);
        assertNotNull(info);
        assertEquals(info.getAllGroupings().size(), 0);
        assertEquals(info.getAdminGroup().getMembers().size(), 0);
        assertEquals(info.getAdminGroup().getUsernames().size(), 0);
        assertEquals(info.getAdminGroup().getNames().size(), 0);
        assertEquals(info.getAdminGroup().getUuids().size(), 0);

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

        WsGetAttributeAssignmentsResults assignments =
                gs.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, group, YYYYMMDDTHHMM);
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();

        assertEquals(dateStr, assignedValue);
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
    public void optTest() {
        assertFalse(gs.inGroup(GROUPING, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));

        gs.optIn(username[4], GROUPING);
        assertTrue(gs.inGroup(GROUPING, username[4]));
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertTrue(gs.checkSelfOpted(GROUPING_INCLUDE, username[4]));

        gs.cancelOptIn(GROUPING, username[4]);
        assertTrue(gs.inGroup(GROUPING, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[4]));

        //not in group
        List<GroupingsServiceResult> cancelOptIn_notInGroup = gs.cancelOptIn(GROUPING, username[4]);
        assertTrue(cancelOptIn_notInGroup.get(0).getResultCode().startsWith(SUCCESS));
        //not selfOpted
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[2]));
        List<GroupingsServiceResult> cancelOptInFail = gs.cancelOptIn(GROUPING, username[2]);
        assertTrue(cancelOptInFail.get(0).getResultCode().startsWith(FAILURE));

        gs.optOut(username[4], GROUPING);
        assertFalse(gs.inGroup(GROUPING, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));

        gs.cancelOptOut(GROUPING, username[4]);
        assertTrue(gs.inGroup(GROUPING, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[4]));

        //not in group
        List<GroupingsServiceResult> cancelOptOut_notInGroup = gs.cancelOptOut(GROUPING, username[4]);
        assertTrue(cancelOptOut_notInGroup.get(0).getResultCode().startsWith(SUCCESS));
        //not selfOpted
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[3]));
        List<GroupingsServiceResult> cancelOptOutFail = gs.cancelOptOut(GROUPING, username[3]);
        assertTrue(cancelOptOutFail.get(0).getResultCode().startsWith(FAILURE));

        gs.addMemberAs(username[0], GROUPING_EXCLUDE, username[4]);
    }

    @Test
    public void getGroupingTest() {
        Grouping grouping = gs.getGrouping(GROUPING, username[4]);
        assertEquals(grouping.getPath(), "");
        assertEquals(grouping.getName(), "");
        assertEquals(grouping.getOwners().getMembers().size(), 0);
        assertEquals(grouping.getInclude().getMembers().size(), 0);
        assertEquals(grouping.getExclude().getMembers().size(), 0);
        assertEquals(grouping.getBasis().getMembers().size(), 0);
        assertEquals(grouping.getComposite().getMembers().size(), 0);

        grouping = gs.getGrouping(GROUPING, username[0]);

        assertEquals(grouping.getPath(), GROUPING);
        assertTrue(grouping.getOwners().getUsernames().contains(username[0]));
        assertTrue(grouping.getInclude().getUsernames().contains(username[0]));
        assertTrue(grouping.getInclude().getUsernames().contains(username[1]));
        assertTrue(grouping.getInclude().getUsernames().contains(username[2]));
        assertTrue(grouping.getExclude().getUsernames().contains(username[3]));
        assertTrue(grouping.getExclude().getUsernames().contains(username[4]));
        assertTrue(grouping.getBasis().getUsernames().contains(username[4]));
        assertTrue(grouping.getBasis().getUsernames().contains(username[5]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[0]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[1]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[2]));
        assertTrue(grouping.getComposite().getUsernames().contains(username[5]));
    }

    @Test
    public void assignRemoveOwnershipTest() {
        assertTrue(gs.isOwner(GROUPING, username[0]));
        assertFalse(gs.isOwner(GROUPING, username[1]));
        assertFalse(gs.isOwner(GROUPING, username[2]));

        GroupingsServiceResult assignOwnershipFail = gs.assignOwnership(GROUPING, username[1], username[1]);
        assertFalse(gs.isOwner(GROUPING, username[1]));
        assertTrue(assignOwnershipFail.getResultCode().startsWith(FAILURE));

        GroupingsServiceResult assignOwnershipSuccess = gs.assignOwnership(GROUPING, username[0], username[1]);
        assertTrue(gs.isOwner(GROUPING, username[1]));
        assertTrue(assignOwnershipSuccess.getResultCode().startsWith(SUCCESS));

        GroupingsServiceResult removeOwnershipFail = gs.removeOwnership(GROUPING, username[2], username[1]);
        assertTrue(gs.isOwner(GROUPING, username[1]));
        assertTrue(removeOwnershipFail.getResultCode().startsWith(FAILURE));

        GroupingsServiceResult removeOwnershipSuccess = gs.removeOwnership(GROUPING, username[0], username[1]);
        assertFalse(gs.isOwner(GROUPING, username[1]));
        assertTrue(removeOwnershipSuccess.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void groupingsInTest() {
        GroupingAssignment groupingAssignment = gs.getGroupingAssignment(username[0]);
        boolean inGrouping = false;

        for (Grouping grouping : groupingAssignment.getGroupingsIn()) {
            if (grouping.getPath().contains(GROUPING)) {
                inGrouping = true;
                break;
            }
        }
        assertTrue(inGrouping);

        inGrouping = false;
        groupingAssignment = gs.getGroupingAssignment(username[4]);
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
        GroupingAssignment groupingAssignment = gs.getGroupingAssignment(username[0]);
        boolean ownsGrouping = false;

        for (Grouping grouping : groupingAssignment.getGroupingsOwned()) {
            if (grouping.getPath().contains(GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertTrue(ownsGrouping);

        ownsGrouping = false;
        groupingAssignment = gs.getGroupingAssignment(username[4]);
        for (Grouping grouping : groupingAssignment.getGroupingsOwned()) {
            if (grouping.getPath().contains(GROUPING)) {
                ownsGrouping = true;
                break;
            }
        }
        assertFalse(ownsGrouping);
    }

    @Test
    public void groupingsToOptTest() {
        GroupingAssignment groupingAssignment = gs.getGroupingAssignment(username[0]);

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
    public void addRemoveSelfOptedTest() {
        List<String> groupsIn = gs.getGroupPaths(username[4]);

        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));
        int numberOptedInBefore = gs.groupingsOptedOutOf(username[4], groupsIn).size();

        gs.addSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));
        int numberOptedInAfter = gs.groupingsOptedOutOf(username[4], groupsIn).size();
        assertEquals(numberOptedInBefore, numberOptedInAfter -1);
        gs.addSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));

        gs.removeSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));
        assertEquals(numberOptedInBefore, gs.groupingsOptedOutOf(username[4], groupsIn).size());
        gs.removeSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));

        gs.addSelfOpted(GROUPING_EXCLUDE, username[2]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[2]));
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
    public void addMemberAsTest() {
        assertFalse(gs.inGroup(GROUPING, username[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        GroupingsServiceResult addMember = gs.addMemberAs(username[0], GROUPING_INCLUDE, username[4]);

        assertTrue(gs.inGroup(GROUPING, username[4]));
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertEquals(addMember.getResultCode(), "SUCCESS");
        assertEquals(addMember.getAction(), "add " + username[4] + " to " + GROUPING_INCLUDE);
        addMember = gs.addMemberAs(username[0], GROUPING_EXCLUDE, username[4]);

        assertFalse(gs.inGroup(GROUPING, username[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertEquals(addMember.getResultCode(), "SUCCESS");
        assertEquals(addMember.getAction(), "add " + username[4] + " to " + GROUPING_EXCLUDE);
        //test when already in group
        addMember = gs.addMemberAs(username[0], GROUPING_EXCLUDE, username[4]);

        assertFalse(gs.inGroup(GROUPING, username[4]));
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertEquals(addMember.getResultCode(), "SUCCESS");
        assertEquals(addMember.getAction(), "add " + username[4] + " to " + GROUPING_EXCLUDE);

        //TODO add use case when user is not in exclude group
    }

    @Test
    public void deleteMemberTest() {
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[2]));
        gs.deleteMember(GROUPING_INCLUDE, username[2]);
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[2]));
        gs.addMemberAs(username[0], GROUPING_INCLUDE, username[2]);
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[2]));
    }

    @Test
    public void getMembersTest() {
        Group group = gs.getMembers(username[0], GROUPING);
        assertTrue(group.getUsernames().contains(username[0]));
        assertTrue(group.getUsernames().contains(username[1]));
        assertTrue(group.getUsernames().contains(username[2]));
        assertFalse(group.getUsernames().contains(username[3]));
        assertFalse(group.getUsernames().contains(username[4]));
        assertTrue(group.getUsernames().contains(username[5]));
    }

    @Test
    public void deleteMemberAsTest() {
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[2]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[2]));

        GroupingsServiceResult deleteMember1 = gs.deleteMemberAs(username[0], GROUPING_EXCLUDE, username[4]);
        GroupingsServiceResult deleteMember2 = gs.deleteMemberAs(username[0], GROUPING_INCLUDE, username[2]);

        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[4]));
        assertFalse(gs.inGroup(GROUPING_INCLUDE, username[2]));
        assertFalse(gs.inGroup(GROUPING_EXCLUDE, username[2]));

        assertEquals(deleteMember1.getResultCode(), "SUCCESS");
        assertEquals(deleteMember2.getResultCode(), "SUCCESS");
        assertEquals(deleteMember1.getAction(), "delete " + username[4] + " from " + GROUPING_EXCLUDE);
        assertEquals(deleteMember2.getAction(), "delete " + username[2] + " from " + GROUPING_INCLUDE);

        //test when not in group
        deleteMember1 = gs.deleteMemberAs(username[0], GROUPING_EXCLUDE, username[4]);
        deleteMember2 = gs.deleteMemberAs(username[0], GROUPING_INCLUDE, username[2]);

        assertEquals(deleteMember1.getResultCode(), "SUCCESS");
        assertEquals(deleteMember2.getResultCode(), "SUCCESS");
        assertEquals(deleteMember1.getAction(), "delete " + username[4] + " from " + GROUPING_EXCLUDE);
        assertEquals(deleteMember2.getAction(), "delete " + username[2] + " from " + GROUPING_INCLUDE);

        //reset Grouping
        gs.addMemberAs(username[0], GROUPING_EXCLUDE, username[4]);
        gs.addMemberAs(username[0], GROUPING_INCLUDE, username[2]);
        assertTrue(gs.inGroup(GROUPING_EXCLUDE, username[4]));
        assertTrue(gs.inGroup(GROUPING_INCLUDE, username[2]));

    }

    @Test
    public void getGroupNamesTest() {
        List<String> groupNames1 = gs.getGroupPaths(username[4]);
        List<String> groupNames2 = gs.getGroupPaths(username[2]);

        assertTrue(groupNames1.contains(GROUPING_EXCLUDE));
        assertFalse(groupNames1.contains(GROUPING));
        assertFalse(groupNames1.contains(GROUPING_INCLUDE));

        assertTrue(groupNames2.contains(GROUPING_INCLUDE));
        assertTrue(groupNames2.contains(GROUPING));
        assertFalse(groupNames2.contains(GROUPING_EXCLUDE));


    }


    @Test
    public void getGroupNames() {
        List<String> groups = gs.getGroupPaths(username[0]);

        assertTrue(groups.contains(GROUPING_OWNERS));
        assertTrue(groups.contains(GROUPING_STORE_EMPTY_OWNERS));
        assertTrue(groups.contains(GROUPING_TRUE_EMPTY_OWNERS));

        List<String> groups2 = gs.getGroupPaths(username[1]);

        assertFalse(groups2.contains(GROUPING_OWNERS));
        assertFalse(groups2.contains(GROUPING_STORE_EMPTY_OWNERS));
        assertFalse(groups2.contains(GROUPING_TRUE_EMPTY_OWNERS));
        //TODO add the rest of the groups
    }

    @Test
    public void grouperTest() {
        List<String> groupPaths = gs.getGroupPaths(username[0]);


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
                for (WsAttributeAssign grouping : attributes) {
                    groupings.add(grouping.getOwnerGroupName());
                }
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
    public void changeListServeStatusTest() {
        assertTrue(gs.isOwner(GROUPING, username[0]));
        assertTrue(gs.hasListserv(GROUPING));
        gs.changeListservStatus(GROUPING, username[0], true);
        assertTrue(gs.hasListserv(GROUPING));
        gs.changeListservStatus(GROUPING, username[0], false);
        assertFalse(gs.hasListserv(GROUPING));
        gs.changeListservStatus(GROUPING, username[0], false);
        assertFalse(gs.hasListserv(GROUPING));

        assertFalse(gs.isOwner(GROUPING, username[1]));
        gs.changeListservStatus(GROUPING, username[1], true);
        assertFalse(gs.hasListserv(GROUPING));
        gs.changeListservStatus(GROUPING, username[0], true);
        assertTrue(gs.hasListserv(GROUPING));
        gs.changeListservStatus(GROUPING, username[1], false);
        assertTrue(gs.hasListserv(GROUPING));
    }

    @Test
    public void changeOptInStatusTest() {
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        assertTrue(gs.isOwner(GROUPING, username[0]));
        assertTrue(gs.optInPermission(GROUPING));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        gs.changeOptInStatus(GROUPING, username[0], true);
        assertTrue(gs.optInPermission(GROUPING));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        gs.changeOptInStatus(GROUPING, username[0], false);
        assertFalse(gs.optInPermission(GROUPING));
        assertFalse(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertFalse(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
        List<GroupingsServiceResult> optInFail = gs.optIn(username[4], GROUPING);
        assertTrue(optInFail.get(0).getResultCode().startsWith(FAILURE));
        assertFalse(gs.inGroup(GROUPING, username[4]));
        gs.changeOptInStatus(GROUPING, username[0], false);
        assertFalse(gs.optInPermission(GROUPING));
        assertFalse(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertFalse(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        assertFalse(gs.isOwner(GROUPING, username[1]));
        gs.changeOptInStatus(GROUPING, username[1], true);
        assertFalse(gs.optInPermission(GROUPING));
        assertFalse(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertFalse(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
        gs.changeOptInStatus(GROUPING, username[0], true);
        assertTrue(gs.optInPermission(GROUPING));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
        gs.changeOptInStatus(GROUPING, username[1], false);
        assertTrue(gs.optInPermission(GROUPING));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
    }

    @Test
    public void changeOptOutStatusTest() {
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        assertTrue(gs.isOwner(GROUPING, username[0]));
        assertTrue(gs.optOutPermission(GROUPING));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        gs.changeOptOutStatus(GROUPING, username[0], true);
        assertTrue(gs.optOutPermission(GROUPING));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        gs.changeOptOutStatus(GROUPING, username[0], false);
        assertFalse(gs.optOutPermission(GROUPING));
        assertFalse(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertFalse(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));
        List<GroupingsServiceResult> optOutFail = gs.optOut(username[1], GROUPING);
        assertTrue(optOutFail.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(gs.inGroup(GROUPING, username[1]));
        gs.changeOptOutStatus(GROUPING, username[0], false);
        assertFalse(gs.optOutPermission(GROUPING));
        assertFalse(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertFalse(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        assertFalse(gs.isOwner(GROUPING, username[1]));
        gs.changeOptOutStatus(GROUPING, username[1], true);
        assertFalse(gs.optOutPermission(GROUPING));
        assertFalse(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertFalse(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));
        gs.changeOptOutStatus(GROUPING, username[0], true);
        assertTrue(gs.optOutPermission(GROUPING));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));
        gs.changeOptOutStatus(GROUPING, username[1], false);
        assertTrue(gs.optOutPermission(GROUPING));
        assertTrue(gs.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(gs.groupOptInPermission(username[1], GROUPING_EXCLUDE));

    }

    @Test
    public void makeGroupingsTest() {
        List<String> groupingPaths = new ArrayList<>();
        groupingPaths.add(GROUPING);
        groupingPaths.add(GROUPING_STORE_EMPTY);
        groupingPaths.add(GROUPING_TRUE_EMPTY);

        List<Grouping> groupings = gs.makeGroupings(groupingPaths, true);

        assertTrue(groupings.size() == 3);
    }
    //TODO add test for assignMembershipAttributes (both)
    //TODO add test for membershipAttributeAssign
    //TODO add test for attributeAssignments
    //TODO add test for getGrouperPrivilege (both)
    //TODO add test for membershipsResults
    //TODO add test for getMember
    //TODO add test for extractGroupings
    //TODO add test for extractGroupingNames
    //TODO add test for removeGroupOwnership
    //TODO add test for addGroupOwnership
    //TODO add test for groupingNamesFromPrivilegeResults
}

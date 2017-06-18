package edu.hawaii.its.groupings.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import edu.hawaii.its.groupings.api.service.GroupingsServiceImpl;
import edu.hawaii.its.groupings.api.type.*;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class TestGroupingsService {

    //    private final String GROUPING = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    @Value("${groupings.api.test.grouping}")
    private String GROUPING;

    @Value("${groupings.api.test.grouping_include}")
    private String GROUPING_INCLUDE;

    @Value("${groupings.api.test.grouping_exclude}")
    private String GROUPING_EXCLUDE;

    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.attributes}")
    private String ATTRIBUTES;

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

        WsGetAttributeAssignmentsResults assignments =
                gs.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, group, YYYYMMDDTHHMM);
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
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));

        gs.addSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertTrue(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));

        gs.removeSelfOpted(GROUPING_EXCLUDE, username[4]);
        assertFalse(gs.checkSelfOpted(GROUPING_EXCLUDE, username[4]));
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

    @Test
    public void makeGroupTest() {
        WsSubject[] list = new WsSubject[3];
        for (int i = 0; i < 3; i++) {
            list[i] = new WsSubject();
            list[i].setName("testSubject_" + i);
            list[i].setId("testSubject_uuid_" + i);
            list[i].setAttributeValues(new String[]{"testSubject_username_" + i});
        }

        Group group = gs.makeGroup(list);

        for (int i = 0; i < group.getMembers().size(); i++) {
            assertTrue(group.getMembers().get(i).getName().equals("testSubject_" + i));
            assertTrue(group.getNames().contains("testSubject_" + i));
            assertTrue(group.getMembers().get(i).getUuid().equals("testSubject_uuid_" + i));
            assertTrue(group.getUuids().contains("testSubject_uuid_" + i));
            assertTrue(group.getMembers().get(i).getUsername().equals("testSubject_username_" + i));
            assertTrue(group.getUsernames().contains("testSubject_username_" + i));
        }
    }

    @Test
    public void makePersonTest() {
        String name = "name";
        String id = "uuid";
        String identifier = "username";

        WsSubject subject = new WsSubject();
        subject.setName(name);
        subject.setId(id);
        subject.setAttributeValues(new String[]{identifier});

        Person person = gs.makePerson(subject);

        assertTrue(person.getName().equals(name));
        assertTrue(person.getUuid().equals(id));
        assertTrue(person.getUsername().equals(identifier));

    }

    @Test
    public void extractGroupNamesTest() {
        List<WsGroup> groups = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            groups.add(new WsGroup());
            groups.get(i).setName("testName_" + i);
        }
        List<String> groupNames = gs.extractGroupNames(groups);

        for (int i = 0; i < 3; i++) {
            assertTrue(groupNames.contains("testName_" + i));
        }
    }


    //TODO add test for assignMembershipAttributes (both)
    //TODO add test for membershipAttributeAssign
    //TODO add test for attributeAssignments
    //TODO add test for grouperPrivilegesLite (both)
    //TODO add test for membershipsResults
    //TODO add test for addMemberAs
    //TODO add test for deleteMemberAs
    //TODO add test for getMember
    //TODO add test for extractGroupings
    //TODO add test for getGroupNames
    //TODO add test for makeGroupings
    //TODO add test for extractGroupingNames
    //TODO add test for removeGroupOwnership
    //TODO add test for addGroupOwnership
    //TODO add test for groupingNamesFromPrivilegeResults
}

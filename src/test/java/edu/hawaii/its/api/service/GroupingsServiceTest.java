package edu.hawaii.its.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("localTest")
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsServiceTest {
    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.grouping_admins}")
    private String GROUPING_ADMINS;

    @Value("${groupings.api.grouping_apps}")
    private String GROUPING_APPS;

    @Value("${groupings.api.grouping_owners}")
    private String GROUPING_OWNERS;

    @Value("${groupings.api.grouping_superusers}")
    private String GROUPING_SUPERUSERS;

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

    @Value("${groupings.api.every_entity}")
    private String EVERY_ENTITY;

    @Value("${groupings.api.is_member}")
    private String IS_MEMBER;

    @Value("${groupings.api.success}")
    private String SUCCESS;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Value("${groupings.api.success_allowed}")
    private String SUCCESS_ALLOWED;

    @Value("$groupings.api.stem}")
    private String STEM;

    String grouping = "grouping";

    @Autowired
    GroupingsServiceImpl gs;

    @Autowired
    private GrouperFactoryService gfs;

    @Test
    public void groupingParentPath() {
        String[] groups = new String[]
                {grouping + EXCLUDE,
                        grouping + INCLUDE,
                        grouping + OWNERS,
                        grouping + BASIS,
                        grouping + BASIS_PLUS_INCLUDE,
                        grouping};

        for (String g : groups) {
            assertEquals(grouping, gs.parentGroupingPath(g));
        }

        assertEquals("", gs.parentGroupingPath(null));
    }

    @Test
    public void extractGroupPaths() {
        List<WsGroup> groups = new ArrayList<>();
        List<String> groupNames;

        for (int i = 0; i < 3; i++) {
            groups.add(new WsGroup());
            groups.get(i).setName("testName_" + i);
        }

        groupNames = gs.extractGroupPaths(groups);

        for (int i = 0; i < 3; i++) {
            assertTrue(groupNames.contains("testName_" + i));
        }
    }

    @Test
    public void extractFirstMembershipID() {
        WsGetMembershipsResults mr = new WsGetMembershipsResults();
        WsMembership[] memberships = new WsMembership[3];
        for (int i = 0; i < 3; i++) {
            memberships[i] = new WsMembership();
            memberships[i].setMembershipId("membershipID_" + i);
        }
        mr.setWsMemberships(memberships);

        assertEquals("membershipID_0", gs.extractFirstMembershipID(mr));
    }

    @Test
    public void makeGroup() {
        WsSubject[] subjects = new WsSubject[0];
        assertNotNull(gs.makeGroup(subjects));

        subjects = new WsSubject[1];
        assertNotNull(gs.makeGroup(subjects));

        subjects[0] = new WsSubject();
        assertNotNull(gs.makeGroup(subjects));

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
    public void makeGroupingsNoAttributes() {
        List<String> groupPaths = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            groupPaths.add("grouping_" + i);
        }
        for (int i = 0; i < 5; i++) {
            groupPaths.add("path:grouping_" + (i + 5));
        }

        List<Grouping> groupings = gs.makeGroupings(groupPaths, false);

        for (int i = 5; i < 10; i++) {
            assertEquals("path:grouping_" + i, groupings.get(i).getPath());
            assertEquals("grouping_" + i, groupings.get(i).getName());
        }
    }

    @Test
    public void makePerson() {
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

        assertNotNull(gs.makePerson(null));
    }

    @Test
    public void makeGroupingsServiceResult() {
        String action = "add a member";
        String resultCode = "successfully added member";
        WsAddMemberResults gr = new WsAddMemberResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(resultCode);
        gr.setResultMetadata(resultMeta);

        GroupingsServiceResult gsr = gs.makeGroupingsServiceResult(gr, action);

        assertEquals(action, gsr.getAction());
        assertEquals(resultCode, gsr.getResultCode());
    }

    @Test
    public void extractFirstMembershipIDTest() {
        WsGetMembershipsResults membershipsResults = null;
        String firstMembershipId = gs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        membershipsResults = new WsGetMembershipsResults();
        firstMembershipId = gs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        WsMembership[] memberships = null;
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = gs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        memberships = new WsMembership[]{null};
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = gs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        WsMembership membership = new WsMembership();
        memberships = new WsMembership[]{membership};
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = gs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        membership.setMembershipId("1234");
        memberships = new WsMembership[]{membership};
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = gs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "1234");
    }
}

package edu.hawaii.its.api.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.beans.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class GroupingsServiceTest {

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

    @Value("${groupings.api.person_attributes.uuid}")
    private String UUID_KEY;

    @Value("${groupings.api.person_attributes.username}")
    private String UID_KEY;

    @Value("${groupings.api.person_attributes.first_name}")
    private String FIRST_NAME_KEY;

    @Value("${groupings.api.person_attributes.last_name}")
    private String LAST_NAME_KEY;

    @Value("${groupings.api.person_attributes.composite_name}")
    private String COMPOSITE_NAME_KEY;

    String grouping = "grouping";

    @Autowired
    GroupingAssignmentService gas;

    @Autowired
    GroupingsService gs;

    @Autowired
    HelperService hs;

    @Test
    public void constructorTest() {
        assertNotNull(gs);
    }

    @Test
    public void groupingParentPath() {
        String[] groups = new String[] { grouping + EXCLUDE,
                grouping + INCLUDE,
                grouping + OWNERS,
                grouping + BASIS,
                grouping + BASIS_PLUS_INCLUDE,
                grouping };

        for (String g : groups) {
            assertEquals(grouping, hs.parentGroupingPath(g));
        }

        assertEquals("", hs.parentGroupingPath(null));
    }

    @Test
    public void extractGroupPaths() {
        List<WsGroup> groups = null;
        //List<String> groupNames = gs.extractGroupPaths(groups);
        List<String> groupNames = gas.extractGroupPaths(groups);
        assertThat(groupNames.size(), equalTo(0));

        groups = new ArrayList<>();
        final int size = 300;

        for (int i = 0; i < size; i++) {
            WsGroup w = new WsGroup();
            w.setName("testName_" + i);
            groups.add(w);
        }
        assertThat(groups.size(), equalTo(size));

        groupNames = gas.extractGroupPaths(groups);
        for (int i = 0; i < size; i++) {
            assertTrue(groupNames.contains("testName_" + i));
        }
        assertThat(groupNames.size(), equalTo(size));

        // Create some duplicates.
        groups = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < size; i++) {
                WsGroup w = new WsGroup();
                w.setName("testName_" + i);
                groups.add(w);
            }
        }
        assertThat(groups.size(), equalTo(3 * size));

        // Duplicates should not be in groupNames list.
        groupNames = gas.extractGroupPaths(groups);
        assertThat(groupNames.size(), equalTo(size));
        for (int i = 0; i < size; i++) {
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

        assertEquals("membershipID_0", hs.extractFirstMembershipID(mr));
    }

    @Test
    public void makeGroup() {
        WsGetMembersResults getMembersResults = new WsGetMembersResults();
        WsGetMembersResult[] getMembersResult = new WsGetMembersResult[1];
        WsGetMembersResult getMembersResult1 = new WsGetMembersResult();
        WsSubject[] subjects = new WsSubject[0];
        getMembersResult1.setWsSubjects(subjects);
        getMembersResult[0] = getMembersResult1;
        getMembersResults.setResults(getMembersResult);
        assertNotNull(gas.makeGroup(getMembersResults));

        subjects = new WsSubject[1];
        getMembersResults.getResults()[0].setWsSubjects(subjects);
        assertNotNull(gas.makeGroup(getMembersResults));

        subjects[0] = new WsSubject();
        getMembersResults.getResults()[0].setWsSubjects(subjects);
        assertNotNull(gas.makeGroup(getMembersResults));

    }

    @Test
    public void makeGroupTest() {
        WsGetMembersResults getMembersResults = new WsGetMembersResults();
        WsGetMembersResult[] getMembersResult = new WsGetMembersResult[1];
        WsGetMembersResult getMembersResult1 = new WsGetMembersResult();

        WsSubject[] list = new WsSubject[3];
        for (int i = 0; i < 3; i++) {
            list[i] = new WsSubject();
            list[i].setName("testSubject_" + i);
            list[i].setId("testSubject_uuid_" + i);
            list[i].setAttributeValues(new String[] { "testSubject_username_" + i });
        }

        getMembersResult1.setWsSubjects(list);
        getMembersResult[0] = getMembersResult1;
        getMembersResults.setResults(getMembersResult);

        Group group = gas.makeGroup(getMembersResults);

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

        List<Grouping> groupings = hs.makeGroupings(groupPaths);

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
        String[] attributeNames = new String[] { UID_KEY, UUID_KEY, LAST_NAME_KEY, COMPOSITE_NAME_KEY, FIRST_NAME_KEY };
        String[] attributeValues = new String[] { identifier, id, null, name, null };

        WsSubject subject = new WsSubject();
        subject.setName(name);
        subject.setId(id);
        subject.setAttributeValues(attributeValues);

        Person person = gas.makePerson(subject, attributeNames);

        assertTrue(person.getName().equals(name));
        assertTrue(person.getUuid().equals(id));
        assertTrue(person.getUsername().equals(identifier));

        assertNotNull(gas.makePerson(new WsSubject(), new String[] {}));
    }

    @Test
    public void makeGroupingsServiceResult() {
        String action = "add a member";
        String resultCode = "successfully added member";
        WsAddMemberResults gr = new WsAddMemberResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(resultCode);
        gr.setResultMetadata(resultMeta);

        GroupingsServiceResult gsr = hs.makeGroupingsServiceResult(gr, action);

        assertEquals(action, gsr.getAction());
        assertEquals(resultCode, gsr.getResultCode());
    }

    @Test
    public void extractFirstMembershipIDTest() {
        WsGetMembershipsResults membershipsResults = null;
        String firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        membershipsResults = new WsGetMembershipsResults();
        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        WsMembership[] memberships = null;
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        memberships = new WsMembership[] { null };
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        WsMembership membership = new WsMembership();
        memberships = new WsMembership[] { membership };
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "");

        membership.setMembershipId("1234");
        memberships = new WsMembership[] { membership };
        membershipsResults.setWsMemberships(memberships);
        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
        assertEquals(firstMembershipId, "1234");
    }

}

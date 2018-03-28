package edu.hawaii.its.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestMembershipService {

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

    @Value("${groupings.api.yyyymmddThhmm}")
    private String YYYYMMDDTHHMM;

    @Value("${groupings.api.assign_type_group}")
    private String ASSIGN_TYPE_GROUP;

    @Value("${groupings.api.test.usernames}")
    private String[] username;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Autowired
    GroupAttributeService groupAttributeService;

    @Autowired
    GroupingAssignmentService groupingAssignmentService;

    @Autowired
    private MemberAttributeService memberAttributeService;

    @Autowired
    private MembershipService membershipService;

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
    public void groupOptInPermissionTest() {
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));
    }

    @Test
    public void groupOptOutPermissionTest() {
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
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
    public void optTest() {

        //tst[3] is not in the composite or include, but is in the basis and exclude
        //tst[3] is not self opted into the exclude
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(memberAttributeService.isSelfOpted(GROUPING_EXCLUDE, username[3]));

        //tst[3] opts in to the Grouping
        membershipService.optIn(username[3], GROUPING);
        //tst[3] should still be in the basis and now also in the Grouping
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING, username[3]));
        //tst[3] is no longer in the exclude, and because tst[3] is in the basis,
        //tst[3] does not get added to the include
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));

        //tst[3] opts out of the Grouping
        membershipService.optOut(username[3], GROUPING);
        //tst[3] is still in basis, now in exclude and not in Grouping or include
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));
        //tst[3] is now self opted into exclude
        assertTrue(memberAttributeService.isSelfOpted(GROUPING_EXCLUDE, username[3]));

        //reset group
        membershipService.removeSelfOpted(GROUPING_EXCLUDE, username[3]);
        assertFalse(memberAttributeService.isSelfOpted(GROUPING_EXCLUDE, username[3]));
    }

    @Test
    public void addRemoveSelfOptedTest() {

        //username[2] is not in the include, but not self opted
        assertTrue(memberAttributeService.isMember(GROUPING_INCLUDE, username[2]));
        assertFalse(memberAttributeService.isSelfOpted(GROUPING_INCLUDE, username[2]));

        //add the self opted attribute for username[2]'s membership for the include group
        membershipService.addSelfOpted(GROUPING_INCLUDE, username[2]);

        //username[2] should now be self opted
        assertTrue(memberAttributeService.isSelfOpted(GROUPING_INCLUDE, username[2]));

        //remove the self opted attribute for username[2]'s membership from the include group
        membershipService.removeSelfOpted(GROUPING_INCLUDE, username[2]);

        //username[2] should no longer be self opted into the include
        assertFalse(memberAttributeService.isSelfOpted(GROUPING_INCLUDE, username[2]));

        //try to add self opted attribute when not in the group
        GroupingsServiceResult groupingsServiceResult;

        try {
            groupingsServiceResult = membershipService.addSelfOpted(GROUPING_EXCLUDE, username[2]);
        } catch (GroupingsServiceResultException gsre) {
            groupingsServiceResult = gsre.getGsr();
        }
        assertTrue(groupingsServiceResult.getResultCode().startsWith(FAILURE));
        assertFalse(memberAttributeService.isSelfOpted(GROUPING_EXCLUDE, username[2]));
    }

    @Test
    public void groupOptPermissionTest() {
        assertTrue(membershipService.groupOptOutPermission(username[0], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptOutPermission(username[0], GROUPING_EXCLUDE));

        assertTrue(membershipService.groupOptInPermission(username[0], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[0], GROUPING_EXCLUDE));
    }

    @Test
    public void addMemberAsTest() {

        //username[3] is in the basis and exclude, not the composite or include
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));

        //an owner adds username[3] to the include group
        List<GroupingsServiceResult> addMember =
                membershipService.addGroupMemberByUsername(username[0], GROUPING_INCLUDE, username[3]);

        //the addition was successful
        assertTrue(addMember.get(0).getResultCode().startsWith(SUCCESS));
        //username[3] is in the basis, include and composite, not the exclude
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));

        //put username[3] back in the exclude group
        addMember = membershipService.addGroupMemberByUsername(username[0], GROUPING_EXCLUDE, username[3]);

        //the addition was successful
        assertEquals(addMember.get(0).getResultCode(), SUCCESS);
        //username[3] is in the basis and exclude, not the composite or include
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));

        //test adding when already in group
        addMember = membershipService.addGroupMemberByUsername(username[0], GROUPING_EXCLUDE, username[3]);
        //the addition was successful
        assertTrue(addMember.get(0).getResultCode().startsWith(SUCCESS));
        //username[3] is in the basis and exclude, not the composite or include
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));
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
    public void deleteMemberAsTest() {
        //username[2] is in composite and include, not basis or exclude
        assertTrue(memberAttributeService.isMember(GROUPING, username[2]));
        assertTrue(memberAttributeService.isMember(GROUPING_INCLUDE, username[2]));
        assertFalse(memberAttributeService.isMember(GROUPING_BASIS, username[2]));
        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, username[2]));

        //username[3] is in basis and exclude, not composite or include
        assertTrue(memberAttributeService.isMember(GROUPING_BASIS, username[3]));
        assertTrue(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[3]));

        //delete username[3] from exclude
        GroupingsServiceResult deleteMember1 =
                membershipService.deleteGroupMemberByUsername(username[0], GROUPING_EXCLUDE, username[3]);
        //deletion was successful
        assertEquals(deleteMember1.getResultCode(), SUCCESS);
        //username[3] is no longer in the exclude
        assertFalse(memberAttributeService.isMember(GROUPING_EXCLUDE, username[3]));

        //delete username[2] from include
        GroupingsServiceResult deleteMember2 =
                membershipService.deleteGroupMemberByUsername(username[0], GROUPING_INCLUDE, username[2]);
        //deletion was successful
        assertEquals(deleteMember2.getResultCode(), SUCCESS);
        //username[2] is no longer in composite or include
        assertFalse(memberAttributeService.isMember(GROUPING, username[2]));
        assertFalse(memberAttributeService.isMember(GROUPING_INCLUDE, username[2]));

        //test when not in group
        deleteMember1 = membershipService.deleteGroupMemberByUsername(username[0], GROUPING_EXCLUDE, username[3]);
        deleteMember2 = membershipService.deleteGroupMemberByUsername(username[0], GROUPING_INCLUDE, username[2]);

        //results are successful because the end result is the same
        assertTrue(deleteMember1.getResultCode().startsWith(SUCCESS));
        assertTrue(deleteMember2.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void addGroupingMemberByUuidTest(){
        //todo
    }

    @Test
    public void deleteGroupingMemberByUuidTest(){
        //todo
    }

    @Test
    public void addGroupMembersByUsernameTest(){
        //todo
    }

    @Test
    public void addGroupMemberByUuidTest(){
        //todo
    }

    @Test
    public void addGroupMembersByUuidTest(){
        //todo
    }

    @Test
    public void deleteGroupMemberByUuidTest(){
        //todo
    }

    @Test
    public void addAdminTest(){
        //todo
    }

    @Test
    public void deleteAdminTest(){
        //todo
    }
}

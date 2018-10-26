package edu.hawaii.its.api.service;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupAttributeService {

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

    @Value("${groupings.api.basis_plus_include}")
    private String BASIS_PLUS_INCLUDE;

    @Value("${groupings.api.test.usernames}")
    private String[] username;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Value("${groupings.api.assign_type_group}")
    private String ASSIGN_TYPE_GROUP;

    @Value("${groupings.api.yyyymmddThhmm}")
    private String YYYYMMDDTHHMM;

    @Autowired
    private GroupAttributeService groupAttributeService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MemberAttributeService memberAttributeService;

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

        //remove from owners
        memberAttributeService.removeOwnership(GROUPING, username[0], username[1]);
    }

    @Test
    public void optOutPermissionTest() {
        assertTrue(groupAttributeService.optOutPermission(GROUPING));
    }

    @Test
    public void optInPermissionTest() {
        assertTrue(groupAttributeService.optInPermission(GROUPING));
    }

    @Test
    public void hasListservTest() {
        assertTrue(groupAttributeService.hasListserv(GROUPING));
    }

    @Test
    public void changeListServeStatusTest() {
        GroupingsServiceResult groupingsServiceResult;

        assertTrue(memberAttributeService.isOwner(GROUPING, username[0]));
        assertTrue(groupAttributeService.hasListserv(GROUPING));

        //get last modified time
        WsGetAttributeAssignmentsResults attributes = groupAttributeService.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, GROUPING, YYYYMMDDTHHMM);
        String lastModTime = attributes.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();

        groupAttributeService.changeListservStatus(GROUPING, username[0], true);
        assertTrue(groupAttributeService.hasListserv(GROUPING));

        //get last modified time and make sure that it hasn't changed
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e){
            fail();
        }
        attributes = groupAttributeService.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, GROUPING, YYYYMMDDTHHMM);
        String lastModTime2 = attributes.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertEquals(lastModTime, lastModTime2);

        groupAttributeService.changeListservStatus(GROUPING, username[0], false);
        assertFalse(groupAttributeService.hasListserv(GROUPING));

        //todo get last modified time and make sure that it has changed
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e){
            fail();
        }
        attributes = groupAttributeService.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, GROUPING, YYYYMMDDTHHMM);
        String lastModTime3 = attributes.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertNotEquals(lastModTime2, lastModTime3);

        groupAttributeService.changeListservStatus(GROUPING, username[0], false);
        assertFalse(groupAttributeService.hasListserv(GROUPING));

        //todo get last modified time and make sure that it hasn't changed
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e){
            fail();
        }
        attributes = groupAttributeService.attributeAssignmentsResults(ASSIGN_TYPE_GROUP, GROUPING, YYYYMMDDTHHMM);
        String lastModTime4 = attributes.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertEquals(lastModTime3, lastModTime4);

        assertFalse(memberAttributeService.isOwner(GROUPING, username[1]));
        try {
            groupingsServiceResult = groupAttributeService.changeListservStatus(GROUPING, username[1], true);
        } catch (GroupingsServiceResultException gsre) {
            groupingsServiceResult = gsre.getGsr();
        }
        assertTrue(groupingsServiceResult.getResultCode().startsWith(FAILURE));
        assertFalse(groupAttributeService.hasListserv(GROUPING));
        groupAttributeService.changeListservStatus(GROUPING, username[0], true);
        assertTrue(groupAttributeService.hasListserv(GROUPING));
        try {
            groupingsServiceResult = groupAttributeService.changeListservStatus(GROUPING, username[1], false);
        } catch (GroupingsServiceResultException gsre) {
            groupingsServiceResult = gsre.getGsr();
        }
        assertTrue(groupingsServiceResult.getResultCode().startsWith(FAILURE));
        assertTrue(groupAttributeService.hasListserv(GROUPING));
    }

    @Test
    public void changeOptInStatusTest() {
        //expect these to fail
        List<GroupingsServiceResult> optInFail;

        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        assertTrue(memberAttributeService.isOwner(GROUPING, username[0]));
        assertTrue(groupAttributeService.optInPermission(GROUPING));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        groupAttributeService.changeOptInStatus(GROUPING, username[0], true);
        assertTrue(groupAttributeService.optInPermission(GROUPING));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        groupAttributeService.changeOptInStatus(GROUPING, username[0], false);
        assertFalse(groupAttributeService.optInPermission(GROUPING));
        assertFalse(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertFalse(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        try {
            optInFail = membershipService.optIn(username[4], GROUPING);
        } catch (GroupingsServiceResultException gsre) {
            optInFail = new ArrayList<>();
            optInFail.add(gsre.getGsr());
        }
        assertTrue(optInFail.get(0).getResultCode().startsWith(FAILURE));
        assertFalse(memberAttributeService.isMember(GROUPING, username[3]));
        groupAttributeService.changeOptInStatus(GROUPING, username[0], false);
        assertFalse(groupAttributeService.optInPermission(GROUPING));
        assertFalse(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertFalse(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));

        assertFalse(memberAttributeService.isOwner(GROUPING, username[1]));
        try {
            optInFail = groupAttributeService.changeOptInStatus(GROUPING, username[1], true);
        } catch (GroupingsServiceResultException gsre) {
            optInFail = new ArrayList<>();
            optInFail.add(gsre.getGsr());
        }
        assertTrue(optInFail.get(0).getResultCode().startsWith(FAILURE));
        assertFalse(groupAttributeService.optInPermission(GROUPING));
        assertFalse(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertFalse(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
        groupAttributeService.changeOptInStatus(GROUPING, username[0], true);
        assertTrue(groupAttributeService.optInPermission(GROUPING));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
        try {
            optInFail = groupAttributeService.changeOptInStatus(GROUPING, username[1], false);
        } catch (GroupingsServiceResultException gsre) {
            optInFail = new ArrayList<>();
            optInFail.add(gsre.getGsr());
        }
        assertTrue(optInFail.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(groupAttributeService.optInPermission(GROUPING));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_EXCLUDE));
    }

    @Test
    public void changeOptOutStatusTest() {
        //expect this to fail
        List<GroupingsServiceResult> optOutFail;

        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        assertTrue(memberAttributeService.isOwner(GROUPING, username[0]));
        assertTrue(groupAttributeService.optOutPermission(GROUPING));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        groupAttributeService.changeOptOutStatus(GROUPING, username[0], true);
        assertTrue(groupAttributeService.optOutPermission(GROUPING));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        groupAttributeService.changeOptOutStatus(GROUPING, username[0], false);
        assertFalse(groupAttributeService.optOutPermission(GROUPING));
        assertFalse(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertFalse(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        try {
            optOutFail = membershipService.optOut(username[1], GROUPING);
        } catch (GroupingsServiceResultException gsre) {
            optOutFail = new ArrayList<>();
            optOutFail.add(gsre.getGsr());
        }

        assertTrue(optOutFail.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(memberAttributeService.isMember(GROUPING, username[1]));
        groupAttributeService.changeOptOutStatus(GROUPING, username[0], false);
        assertFalse(groupAttributeService.optOutPermission(GROUPING));
        assertFalse(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertFalse(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        assertFalse(memberAttributeService.isOwner(GROUPING, username[1]));

        try {
            groupAttributeService.changeOptOutStatus(GROUPING, username[1], true);
        } catch (GroupingsServiceResultException gsre) {
            assertTrue(gsre.getGsr().getResultCode().startsWith(FAILURE));
        }

        assertFalse(groupAttributeService.optOutPermission(GROUPING));
        assertFalse(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertFalse(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));
        groupAttributeService.changeOptOutStatus(GROUPING, username[0], true);
        assertTrue(groupAttributeService.optOutPermission(GROUPING));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

        try {
            groupAttributeService.changeOptOutStatus(GROUPING, username[1], false);
        } catch (GroupingsServiceResultException gsre) {
            assertTrue(gsre.getGsr().getResultCode().startsWith(FAILURE));
        }

        assertTrue(groupAttributeService.optOutPermission(GROUPING));
        assertTrue(membershipService.groupOptOutPermission(username[1], GROUPING_INCLUDE));
        assertTrue(membershipService.groupOptInPermission(username[1], GROUPING_EXCLUDE));

    }
}

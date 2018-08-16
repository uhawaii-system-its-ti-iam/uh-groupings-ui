package edu.hawaii.its.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.hawaii.its.api.repository.GroupRepository;
import edu.hawaii.its.api.repository.GroupingRepository;
import edu.hawaii.its.api.repository.MembershipRepository;
import edu.hawaii.its.api.repository.PersonRepository;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.api.type.Membership;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MembershipServiceTest {

    @Value("${groupings.api.grouping_admins}")
    private String GROUPING_ADMINS;

    @Value("${groupings.api.grouping_apps}")
    private String GROUPING_APPS;

    @Value("${groupings.api.success}")
    private String SUCCESS;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Value("${groupings.api.test.username}")
    private String USERNAME;

    @Value("${groupings.api.test.name}")
    private String NAME;

    @Value("${groupings.api.test.uuid}")
    private String UUID;

    private static final String PATH_ROOT = "path:to:grouping";
    private static final String INCLUDE = ":include";
    private static final String EXCLUDE = ":exclude";

    private static final String GROUPING_0_PATH = PATH_ROOT + 0;
    private static final String GROUPING_1_PATH = PATH_ROOT + 1;
    private static final String GROUPING_2_PATH = PATH_ROOT + 2;
    private static final String GROUPING_3_PATH = PATH_ROOT + 3;
    private static final String GROUPING_4_PATH = PATH_ROOT + 4;

    private static final String GROUPING_1_INCLUDE_PATH = GROUPING_1_PATH + INCLUDE;
    private static final String GROUPING_1_EXCLUDE_PATH = GROUPING_1_PATH + EXCLUDE;

    private static final String GROUPING_2_EXCLUDE_PATH = GROUPING_2_PATH + EXCLUDE;

    private static final String GROUPING_3_INCLUDE_PATH = GROUPING_3_PATH + INCLUDE;
    private static final String GROUPING_3_EXCLUDE_PATH = GROUPING_3_PATH + EXCLUDE;

    private static final String GROUPING_4_EXCLUDE_PATH = GROUPING_4_PATH + EXCLUDE;

    private static final String ADMIN_USER = "admin";
    private static final Person ADMIN_PERSON = new Person(ADMIN_USER, ADMIN_USER, ADMIN_USER);
    private List<Person> admins = new ArrayList<>();
    private Group adminGroup;

    private static final String APP_USER = "app";
    private static final Person APP_PERSON = new Person(APP_USER, APP_USER, APP_USER);
    private List<Person> apps = new ArrayList<>();
    private Group appGroup;

    private List<Person> users = new ArrayList<>();
    private List<WsSubjectLookup> lookups = new ArrayList<>();

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MemberAttributeService memberAttributeService;

    @Autowired
    private GroupingRepository groupingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Before
    public void setup() {

        new DatabaseSetup(personRepository, groupRepository, groupingRepository, membershipRepository);

        admins.add(ADMIN_PERSON);
        adminGroup = new Group(GROUPING_ADMINS, admins);
        personRepository.save(ADMIN_PERSON);
        groupRepository.save(adminGroup);

        admins.add(APP_PERSON);
        appGroup = new Group(GROUPING_APPS, apps);
        personRepository.save(APP_PERSON);
        groupRepository.save(appGroup);

        for (int i = 0; i < 100; i++) {
            String name = NAME + i;
            String uuid = UUID + i;
            String username = USERNAME + i;

            Person person = new Person(name, uuid, username);
            users.add(person);

            WsSubjectLookup lookup = new WsSubjectLookup(null, null, username);
            lookups.add(lookup);
        }
    }

    @Test
    public void construction() {
        //autowired
        assertNotNull(membershipService);
    }

    // Debug statement to look at contents of database
    // Delete user from include group to remove them
    // Use user number not slot in array
    // Use assert to check if it worked
    @Test
    public void deleteGroupingMemberByUuidTest() {
        List<GroupingsServiceResult> listGsr;

        // Base test
        // Remove person from include and composite
        listGsr = membershipService.deleteGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(5).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        // If person is in composite and basis, add to exclude group
        listGsr = membershipService.deleteGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(1).getUuid());
        for (GroupingsServiceResult gsrFor : listGsr) {
            assertTrue(gsrFor.getResultCode().startsWith(SUCCESS));
        }

        // Not in composite, do nothing but return success
        listGsr = membershipService.deleteGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(2).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        // todo Can't test with current database setup
        // Not in basis, but in exclude
        // Can't happen with current database

        // Test if user is not an owner
        try {
            listGsr = membershipService.deleteGroupingMemberByUuid(users.get(5).getUsername(), GROUPING_3_PATH,
                    users.get(6).getUuid());
            assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
        } catch (GroupingsServiceResultException gsre) {
            gsre.getGsr();
        }

        // Test if user is admin
        listGsr = membershipService.deleteGroupingMemberByUuid(ADMIN_USER, GROUPING_3_PATH,
                users.get(6).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void addGroupingMemberbyUuidTest() {
        List<GroupingsServiceResult> listGsr;

        // Base test
        // Remove person who's not in composite from exclude and return SUCCESS
        listGsr = membershipService.addGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(3).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        //todo Case where !inComposite && !inBasis is impossible w/ current db

        // In composite
        listGsr = membershipService.addGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(5).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        //todo Case where inBasis && inInclude is impossible w/ current db

        // Test if user is not an owner
        try {
            listGsr = membershipService.addGroupingMemberByUuid(users.get(5).getUsername(), GROUPING_3_PATH,
                    users.get(3).getUuid());
            assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
        } catch (GroupingsServiceResultException gsre) {
            gsre.getGsr();
        }

        // Test if user is admin
        listGsr = membershipService.addGroupingMemberByUuid(ADMIN_USER, GROUPING_3_PATH,
                users.get(3).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void addGroupingMemberbyUsernameTest() {
        List<GroupingsServiceResult> listGsr;

        // Base test
        // Remove person who's not in composite from exclude and return SUCCESS
        listGsr = membershipService.addGroupingMemberByUsername(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(2).getUsername());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        //todo Case where !inComposite && !inBasis is impossible w/ current db

        // In composite
        listGsr = membershipService.addGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(5).getUsername());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        //todo Case where inBasis && inInclude is impossible w/ current db

        // Test if user is not an owner
        try {
            listGsr = membershipService.addGroupingMemberByUuid(users.get(5).getUsername(), GROUPING_3_PATH,
                    users.get(3).getUsername());
            assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
        } catch (GroupingsServiceResultException gsre) {
            gsre.getGsr();
        }

        // Test if user is admin
        listGsr = membershipService.addGroupingMemberByUuid(ADMIN_USER, GROUPING_3_PATH,
                users.get(3).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void deleteGroupingMemberbyUsernameTest() {
        List<GroupingsServiceResult> listGsr;

        // Base test
        // Remove person from include and composite
        listGsr = membershipService.deleteGroupingMemberByUsername(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(5).getUsername());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        // If person is in composite and basis, add to exclude group
        listGsr = membershipService.deleteGroupingMemberByUsername(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(1).getUsername());
        for (GroupingsServiceResult gsrFor : listGsr) {
            assertTrue(gsrFor.getResultCode().startsWith(SUCCESS));
        }

        // Not in composite, do nothing but return success
        listGsr = membershipService.deleteGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
                users.get(2).getUsername());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));

        // todo Can't test with current database setup
        // Not in basis, but in exclude
        // Can't happen with current database

        // Test if user is not an owner
        try {
            listGsr = membershipService.deleteGroupingMemberByUuid(users.get(5).getUsername(), GROUPING_3_PATH,
                    users.get(6).getUuid());
            assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
        } catch (GroupingsServiceResultException gsre) {
            gsre.getGsr();
        }

        // Test if user is admin
        listGsr = membershipService.deleteGroupingMemberByUuid(ADMIN_USER, GROUPING_3_PATH,
                users.get(6).getUuid());
        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void addAdminTest() {

        GroupingsServiceResult gsr;
        try {
            //user is not super user
            gsr = membershipService.addAdmin(users.get(9).getUsername(), users.get(9).getUsername());
        } catch (GroupingsServiceResultException gsre) {
            gsr = gsre.getGsr();
        }
        assertTrue(gsr.getResultCode().startsWith(FAILURE));

        //user is super user
        gsr = membershipService.addAdmin(ADMIN_USER, users.get(9).getUsername());
        assertEquals(SUCCESS, gsr.getResultCode());

        //users.get(9) is already and admin
        gsr = membershipService.addAdmin(ADMIN_USER, users.get(9).getUsername());
        assertTrue(gsr.getResultCode().startsWith(SUCCESS));

    }

    @Test
    public void deleteAdminTest() {
        GroupingsServiceResult gsr;

        //usernameToDelete is not a superuser
        String usernameToDelete = users.get(9).getUsername();

        try {
            //user is not super user
            gsr = membershipService.deleteAdmin(usernameToDelete, ADMIN_USER);
        } catch (GroupingsServiceResultException gsre) {
            gsr = gsre.getGsr();
        }
        assertTrue(gsr.getResultCode().startsWith(FAILURE));

        //user is super user usernameToDelete is not superuser
        gsr = membershipService.deleteAdmin(ADMIN_USER, usernameToDelete);
        assertEquals(SUCCESS, gsr.getResultCode());

        //make usernameToDelete a superuser
        membershipService.addAdmin(ADMIN_USER, usernameToDelete);
        assertTrue(memberAttributeService.isAdmin(usernameToDelete));

        //user is super user usernameToDelete is not superuser
        gsr = membershipService.deleteAdmin(ADMIN_USER, usernameToDelete);
        assertEquals(SUCCESS, gsr.getResultCode());

    }

    @Test
    public void optInTest() {
        List<GroupingsServiceResult> optInResults;

        try {
            //opt in Permission for include group false
            optInResults = membershipService.optIn(users.get(2).getUsername(), GROUPING_2_PATH);
        } catch (GroupingsServiceResultException gsre) {
            optInResults = new ArrayList<>();
            optInResults.add(gsre.getGsr());
        }
        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));

        //opt in Permission for include group true and not in group, but in basis
        optInResults = membershipService.optIn(users.get(1).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertEquals(1, optInResults.size());

        //opt in Permission for include group true but already in group, not self opted
        optInResults = membershipService.optIn(users.get(9).getUsername(), GROUPING_0_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));

        //opt in Permission for include group true, but already self-opted
        optInResults = membershipService.optIn(users.get(9).getUsername(), GROUPING_0_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void optOutTest() {
        List<GroupingsServiceResult> optInResults;
        try {
            //opt in Permission for exclude group false
            optInResults = membershipService.optOut(users.get(1).getUsername(), GROUPING_0_PATH);
        } catch (GroupingsServiceResultException gsre) {
            optInResults = new ArrayList<>();
            optInResults.add(gsre.getGsr());
        }
        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));

        //opt in Permission for exclude group true
        optInResults = membershipService.optOut(users.get(1).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

        //opt in Permission for exclude group true, but already in the exclude group
        optInResults = membershipService.optOut(users.get(2).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

        //opt in Permission for exclude group true, but already self-opted
        optInResults = membershipService.optOut(users.get(2).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

    }

    @Test
    public void removeSelfOptedTest() {
        Group group = groupRepository.findByPath(GROUPING_4_EXCLUDE_PATH);

        GroupingsServiceResult gsr;

        try {
            //member is not in group
            gsr = membershipService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(5).getUsername());
        } catch (GroupingsServiceResultException gsre) {
            gsr = gsre.getGsr();
        }
        assertTrue(gsr.getResultCode().startsWith(FAILURE));

        //member is not self-opted
        gsr = membershipService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(4).getUsername());
        assertTrue(gsr.getResultCode().startsWith(SUCCESS));

        //make member self-opted
        Membership membership = membershipRepository.findByPersonAndGroup(users.get(4), group);
        membership.setSelfOpted(true);
        membershipRepository.save(membership);

        //member is self-opted
        gsr = membershipService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(4).getUsername());
        assertTrue(gsr.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void groupOptOutPermissionTest() {
        boolean oop = membershipService.groupOptOutPermission(users.get(1).getUsername(), GROUPING_2_EXCLUDE_PATH);
        assertEquals(false, oop);

        oop = membershipService.groupOptOutPermission(users.get(1).getUsername(), GROUPING_1_EXCLUDE_PATH);
        assertEquals(true, oop);
    }

    @Test
    public void addMemberByUsernameTest() {
        Grouping grouping = groupingRepository.findByPath(GROUPING_1_PATH);
        assertFalse(grouping.getComposite().getMembers().contains(users.get(3)));

        membershipService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_1_INCLUDE_PATH,
                users.get(3).getUsername());
        grouping = groupingRepository.findByPath(GROUPING_1_PATH);
        assertTrue(grouping.getComposite().getMembers().contains(users.get(3)));
        //todo Cases (inBasis && inInclude) and (!inComposite && !inBasis) not reachable w/ current DB
    }

    @Test
    public void addMembersByUsername() {
        //add all usernames
        List<String> usernames = new ArrayList<>();
        for (Person user : users) {
            usernames.add(user.getUsername());
        }

        Grouping grouping = groupingRepository.findByPath(GROUPING_3_PATH);

        //check how many members are in the basis
        int numberOfBasisMembers = grouping.getBasis().getMembers().size();

        //try to put all users into exclude group
        membershipService.addGroupMembersByUsername(users.get(0).getUsername(), GROUPING_3_EXCLUDE_PATH, usernames);
        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
        //there should be no real members in composite, but it should still have the 'grouperAll' member
        assertEquals(1, grouping.getComposite().getMembers().size());
        //only the users in the basis should have been added to the exclude group
        assertEquals(numberOfBasisMembers, grouping.getExclude().getMembers().size());

        //try to put all users into the include group
        membershipService.addGroupMembersByUsername(users.get(0).getUsername(), GROUPING_3_INCLUDE_PATH, usernames);
        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
        //all members should be in the group ( - 1 for 'grouperAll' in composite);
        assertEquals(usernames.size(), grouping.getComposite().getMembers().size() - 1);
        //members in basis should not have been added to the include group ( + 2 for 'grouperAll' in both groups)
        assertEquals(usernames.size() - numberOfBasisMembers + 2, grouping.getInclude().getMembers().size());
    }

    @Test
    public void addMemberByUuidTest() {
        Grouping grouping = groupingRepository.findByPath(GROUPING_1_PATH);
        assertFalse(grouping.getComposite().getMembers().contains(users.get(3)));

        membershipService
                .addGroupMemberByUuid(users.get(0).getUsername(), GROUPING_1_INCLUDE_PATH, users.get(3).getUuid());
        grouping = groupingRepository.findByPath(GROUPING_1_PATH);
        assertTrue(grouping.getComposite().getMembers().contains(users.get(3)));
    }

    @Test
    public void addMembersByUuid() {
        //add all uuids
        List<String> uuids = new ArrayList<>();
        for (Person user : users) {
            uuids.add(user.getUuid());
        }

        Grouping grouping = groupingRepository.findByPath(GROUPING_3_PATH);

        //check how many members are in the basis
        int numberOfBasisMembers = grouping.getBasis().getMembers().size();

        //try to put all users into exclude group
        membershipService.addGroupMembersByUuid(users.get(0).getUsername(), GROUPING_3_EXCLUDE_PATH, uuids);
        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
        //there should be no real members in composite, but it should still have the 'grouperAll' member
        assertEquals(1, grouping.getComposite().getMembers().size());
        //only the users in the basis should have been added to the exclude group
        assertEquals(numberOfBasisMembers, grouping.getExclude().getMembers().size());

        //try to put all users into the include group
        membershipService.addGroupMembersByUuid(users.get(0).getUsername(), GROUPING_3_INCLUDE_PATH, uuids);
        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
        //all members should be in the group ( - 1 for 'grouperAll' in composite);
        assertEquals(uuids.size(), grouping.getComposite().getMembers().size() - 1);
        //members in basis should not have been added to the include group ( + 2 for 'grouperAll' in both groups)
        assertEquals(uuids.size() - numberOfBasisMembers + 2, grouping.getInclude().getMembers().size());
    }
}

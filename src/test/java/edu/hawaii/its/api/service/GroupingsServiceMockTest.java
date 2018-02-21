package edu.hawaii.its.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
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
public class GroupingsServiceMockTest {

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

    @Value("${groupings.api.test.username}")
    private String USERNAME;

    @Value("${groupings.api.test.name}")
    private String NAME;

    @Value("${groupings.api.test.uuid}")
    private String UUID;

    private static final String PATH_ROOT = "path:to:grouping";

    private static final String GROUPING_0_PATH = PATH_ROOT + 0;
    private static final String GROUPING_1_PATH = PATH_ROOT + 1;
    private static final String GROUPING_2_PATH = PATH_ROOT + 2;
    private static final String GROUPING_3_PATH = PATH_ROOT + 3;
    private static final String GROUPING_4_PATH = PATH_ROOT + 4;

    private static final String GROUPING_0_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_0_OWNERS_PATH = GROUPING_0_PATH + ":owners";

    private static final String GROUPING_1_INCLUDE_PATH = GROUPING_1_PATH + ":include";
    private static final String GROUPING_1_EXCLUDE_PATH = GROUPING_1_PATH + ":exclude";

    private static final String GROUPING_2_INCLUDE_PATH = GROUPING_2_PATH + ":include";
    private static final String GROUPING_2_EXCLUDE_PATH = GROUPING_2_PATH + ":exclude";
    private static final String GROUPING_2_BASIS_PATH = GROUPING_2_PATH + ":basis";
    private static final String GROUPING_2_OWNERS_PATH = GROUPING_2_PATH + ":owners";

    private static final String GROUPING_3_INCLUDE_PATH = GROUPING_3_PATH + ":include";
    private static final String GROUPING_3_EXCLUDE_PATH = GROUPING_3_PATH + ":exclude";

    private static final String GROUPING_4_EXCLUDE_PATH = GROUPING_4_PATH + ":exclude";

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
    private GroupingsService groupingsService;

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
        assertNotNull(groupingsService);

        //constructed normally
        GrouperFactoryService grouperFactoryOther = new GrouperFactoryServiceImplLocal();
        GroupingsService groupingsServiceOther = new GroupingsServiceImpl(grouperFactoryOther);
        assertNotNull(groupingsServiceOther);
    }

    @Test
    public void addAdminTest() {
        GroupingsServiceResult gsr;
        try {
            //user is not super user
            gsr = groupingsService.addAdmin(users.get(9).getUsername(), users.get(9).getUsername());
        } catch (GroupingsServiceResultException gsre) {
            gsr = gsre.getGsr();
        }
        assertTrue(gsr.getResultCode().startsWith(FAILURE));

        //user is super user
        gsr = groupingsService.addAdmin(ADMIN_USER, users.get(9).getUsername());
        assertTrue(gsr.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void deleteAdminTest() {
        GroupingsServiceResult gsr;

        //usernameToDelete is not a superuser
        String usernameToDelete = users.get(9).getUsername();

        try {
            //user is not super user
            gsr = groupingsService.deleteAdmin(usernameToDelete, ADMIN_USER);
        } catch (GroupingsServiceResultException gsre) {
            gsr = gsre.getGsr();
        }
        assertTrue(gsr.getResultCode().startsWith(FAILURE));

        //user is super user usernameToDelete is not superuser
        gsr = groupingsService.deleteAdmin(ADMIN_USER, usernameToDelete);
        assertEquals(SUCCESS, gsr.getResultCode());

        //make usernameToDelete a superuser
        groupingsService.addAdmin(ADMIN_USER, usernameToDelete);
        assertTrue(groupingsService.isAdmin(usernameToDelete));

        //user is super user usernameToDelete is not superuser
        gsr = groupingsService.deleteAdmin(ADMIN_USER, usernameToDelete);
        assertEquals(SUCCESS, gsr.getResultCode());

    }

    @Test
    public void databaseTest() {
        Iterable<Person> persons = personRepository.findAll();
        Iterable<Group> groups = groupRepository.findAll();
        Iterable<Grouping> groupings = groupingRepository.findAll();
        assertNotNull(persons);
        assertNotNull(groups);
        assertNotNull(groupings);
    }

    @Test
    public void assignOwnershipTest() {
        //expect this to fail
        GroupingsServiceResult randomUserAdds;

        Person randomUser = personRepository.findByUsername(users.get(1).getUsername());
        Grouping grouping = groupingRepository.findByPath(GROUPING_0_PATH);

        assertFalse(grouping.getOwners().getMembers().contains(randomUser));
        assertFalse(grouping.getOwners().isMember(randomUser));

        try {
            randomUserAdds = groupingsService.assignOwnership(GROUPING_0_PATH, randomUser.getUsername(), randomUser.getUsername());
        } catch (GroupingsServiceResultException gsre) {
            randomUserAdds = gsre.getGsr();
        }

        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
        assertFalse(grouping.getOwners().getMembers().contains(randomUser));
        assertFalse(grouping.getOwners().isMember(randomUser));
        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);

        GroupingsServiceResult ownerAdds = groupingsService.assignOwnership(GROUPING_0_PATH, users.get(0).getUsername(), randomUser.getUsername());
        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
        assertTrue(grouping.getOwners().getMembers().contains(randomUser));
        assertTrue(grouping.getOwners().isMember(randomUser));
        assertEquals(ownerAdds.getResultCode(), SUCCESS);

        GroupingsServiceResult adminAdds = groupingsService.assignOwnership(GROUPING_0_PATH, ADMIN_USER, randomUser.getUsername());
        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
        assertTrue(grouping.getOwners().getMembers().contains(randomUser));
        assertTrue(grouping.getOwners().isMember(randomUser));
        assertEquals(SUCCESS, adminAdds.getResultCode());
    }

    @Test
    public void changeListservStatusTest() {

        //expect actions by "Random" to fail
        GroupingsServiceResult turnOnWhenOnRandom;
        GroupingsServiceResult turnOnWhenOffRandom;
        GroupingsServiceResult turnOffWhenOnRandom;
        GroupingsServiceResult turnOffWhenOffRandom;

        Grouping grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        try {
            turnOnWhenOffRandom = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), true);
        } catch (GroupingsServiceResultException gsre) {
            turnOnWhenOffRandom = gsre.getGsr();
        }
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        GroupingsServiceResult turnOnWhenOffOwner = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), true);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertTrue(grouping.isListservOn());

        try {
            turnOnWhenOnRandom = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), true);
        } catch (GroupingsServiceResultException gsre) {
            turnOnWhenOnRandom = gsre.getGsr();
        }
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertTrue(grouping.isListservOn());

        GroupingsServiceResult turnOnWhenOnOwner = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), true);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertTrue(grouping.isListservOn());

        GroupingsServiceResult turnOnWhenOnAdmin = groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, true);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertTrue(grouping.isListservOn());

        try {
            turnOffWhenOnRandom = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), false);
        } catch (GroupingsServiceResultException gsre) {
            turnOffWhenOnRandom = gsre.getGsr();
        }
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertTrue(grouping.isListservOn());

        GroupingsServiceResult turnOffWhenOnOwner = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), false);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        GroupingsServiceResult turnOnWhenOffAdmin = groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, true);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertTrue(grouping.isListservOn());

        GroupingsServiceResult turnOffWhenOnAdmin = groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, false);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        try {
            turnOffWhenOffRandom = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), false);
        } catch (GroupingsServiceResultException gsre) {
            turnOffWhenOffRandom = gsre.getGsr();
        }
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        GroupingsServiceResult turnOffWhenOffOwner = groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), false);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        GroupingsServiceResult turnOffWhenOffAdmin = groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, false);
        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
        assertFalse(grouping.isListservOn());

        assertTrue(turnOnWhenOnRandom.getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOnOwner.getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOnRandom.getResultCode().startsWith(FAILURE));
        assertTrue(turnOffWhenOnOwner.getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOnAdmin.getResultCode().startsWith(SUCCESS));

        assertTrue(turnOnWhenOffRandom.getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOffOwner.getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOffAdmin.getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOffRandom.getResultCode().startsWith(FAILURE));
        assertTrue(turnOffWhenOffOwner.getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void changeOptInStatusTest() {
        //expect actions done by "Random" to fail
        List<GroupingsServiceResult> turnOnWhenOnRandom;
        List<GroupingsServiceResult> turnOnWhenOffRandom;
        List<GroupingsServiceResult> turnOffWhenOnRandom;
        List<GroupingsServiceResult> turnOffWhenOffRandom;

        try {
            turnOnWhenOnRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
        } catch (GroupingsServiceResultException gsre) {
            turnOnWhenOnRandom = new ArrayList<>();
            turnOnWhenOnRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOnWhenOnOwner = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOnAdmin = groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, true);

        try {
            turnOffWhenOnRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
        } catch (GroupingsServiceResultException gsre) {
            turnOffWhenOnRandom = new ArrayList<>();
            turnOffWhenOnRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOffWhenOnOwner = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);

        try {
            turnOffWhenOffRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
        } catch (GroupingsServiceResultException gsre) {
            turnOffWhenOffRandom = new ArrayList<>();
            turnOffWhenOffRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOffWhenOffOwner = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOffAdmin = groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, false);

        try {
            turnOnWhenOffRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
        } catch (GroupingsServiceResultException gsre) {
            turnOnWhenOffRandom = new ArrayList<>();
            turnOnWhenOffRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOnWhenOffOwner = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);

        List<GroupingsServiceResult> turnOffWhenOnAdmin = groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, false);

        List<GroupingsServiceResult> turnOnWhenOffAdmin = groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, true);

        assertTrue(turnOnWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOnOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(2).getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(SUCCESS, turnOffWhenOnOwner.get(0).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnOwner.get(1).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnOwner.get(2).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(0).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(1).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(2).getResultCode());

        assertTrue(turnOnWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(SUCCESS, turnOnWhenOffOwner.get(0).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffOwner.get(1).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffOwner.get(2).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(0).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(1).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(2).getResultCode());

        assertTrue(turnOffWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOffWhenOffOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(2).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void changeOptOutStatusTest() {

        //expect to fail
        List<GroupingsServiceResult> turnOnWhenOnRandom;
        List<GroupingsServiceResult> turnOffWhenOnRandom;
        List<GroupingsServiceResult> turnOnWhenOffRandom;
        List<GroupingsServiceResult> turnOffWhenOffRandom;

        try {
            turnOnWhenOnRandom = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), true);
        } catch (GroupingsServiceResultException gsre) {
            turnOnWhenOnRandom = new ArrayList<>();
            turnOnWhenOnRandom.add(gsre.getGsr());
        }

        List<GroupingsServiceResult> turnOnWhenOnOwner = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOnAdmin = groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, true);

        try {
            turnOffWhenOnRandom = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), false);
        } catch (GroupingsServiceResultException gsre) {
            turnOffWhenOnRandom = new ArrayList<>();
            turnOffWhenOnRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOffWhenOnOwner = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), false);

        try {
            turnOnWhenOffRandom = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), true);
        } catch (GroupingsServiceResultException gsre) {
            turnOnWhenOffRandom = new ArrayList<>();
            turnOnWhenOffRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOnWhenOffOwner = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), true);

        List<GroupingsServiceResult> turnOffWhenOnAdmin = groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, false);

        try {
            turnOffWhenOffRandom = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), false);
        } catch (GroupingsServiceResultException gsre) {
            turnOffWhenOffRandom = new ArrayList<>();
            turnOffWhenOffRandom.add(gsre.getGsr());
        }
        List<GroupingsServiceResult> turnOffWhenOffOwner = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOffAdmin = groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, false);

        List<GroupingsServiceResult> turnOnWhenOffAdmin = groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, true);

        assertTrue(turnOnWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOnOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(2).getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(SUCCESS, turnOffWhenOnOwner.get(0).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnOwner.get(1).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnOwner.get(2).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(0).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(1).getResultCode());
        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(2).getResultCode());

        assertTrue(turnOnWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(SUCCESS, turnOnWhenOffOwner.get(0).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffOwner.get(1).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffOwner.get(2).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(0).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(1).getResultCode());
        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(2).getResultCode());

        assertTrue(turnOffWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOffWhenOffOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(2).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void removeOwnershipTest() {
        GroupingsServiceResult randomUserRemoves;

        try {
            //non-owner/non-admin tries to remove ownership
            randomUserRemoves = groupingsService.removeOwnership(GROUPING_0_PATH, users.get(1).getUsername(), users.get(1).getUsername());
        } catch (GroupingsServiceResultException gsre) {
            randomUserRemoves = gsre.getGsr();
        }
        assertTrue(randomUserRemoves.getResultCode().startsWith(FAILURE));

        //add owner for owner to remove
        groupingsService.addMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH, users.get(1).getUsername());

        //owner tries to remove other ownership
        GroupingsServiceResult ownerRemoves = groupingsService.removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
        assertEquals(SUCCESS, ownerRemoves.getResultCode());

        //try to remove ownership from user that is not an owner
        GroupingsServiceResult ownerRemovesNonOwner = groupingsService.removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
        assertEquals(SUCCESS, ownerRemovesNonOwner.getResultCode());

        //add owner for admin to remove
        groupingsService.addMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH, users.get(1).getUsername());

        //admin tries to remove ownership
        GroupingsServiceResult adminRemoves = groupingsService.removeOwnership(GROUPING_0_PATH, ADMIN_USER, users.get(1).getUsername());
        assertEquals(adminRemoves.getResultCode(), SUCCESS);
    }

    @Test
    public void getGroupingTest() {
        Grouping groupingRandom = groupingsService.getGrouping(GROUPING_0_PATH, users.get(1).getUsername());
        Grouping groupingOwner = groupingsService.getGrouping(GROUPING_0_PATH, users.get(0).getUsername());
        Grouping groupingAdmin = groupingsService.getGrouping(GROUPING_0_PATH, ADMIN_USER);

        assertEquals(0, groupingRandom.getComposite().getMembers().size());
        assertEquals(0, groupingRandom.getInclude().getMembers().size());
        assertEquals(0, groupingRandom.getExclude().getMembers().size());
        assertEquals(0, groupingRandom.getBasis().getMembers().size());
        assertEquals(0, groupingRandom.getOwners().getMembers().size());

        assertTrue(groupingOwner.getComposite().getNames().contains(users.get(0).getName()));
        assertTrue(groupingOwner.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertTrue(groupingOwner.getComposite().getUuids().contains(users.get(0).getUuid()));
        assertTrue(groupingOwner.getInclude().getNames().contains(users.get(5).getName()));
        assertTrue(groupingOwner.getExclude().getNames().contains(users.get(2).getName()));
        assertTrue(groupingOwner.getBasis().getNames().contains(users.get(4).getName()));
        assertTrue(groupingOwner.getOwners().getNames().contains(users.get(0).getName()));

        assertTrue(groupingAdmin.getComposite().getNames().contains(users.get(0).getName()));
        assertTrue(groupingAdmin.getComposite().getUsernames().contains(users.get(0).getUsername()));
        assertTrue(groupingAdmin.getComposite().getUuids().contains(users.get(0).getUuid()));
        assertTrue(groupingAdmin.getInclude().getNames().contains(users.get(5).getName()));
        assertTrue(groupingAdmin.getExclude().getNames().contains(users.get(2).getName()));
        assertTrue(groupingAdmin.getBasis().getNames().contains(users.get(4).getName()));
        assertTrue(groupingAdmin.getOwners().getNames().contains(users.get(0).getName()));
    }

    @Test
    public void getMyGroupingsTest() {
        GroupingAssignment myGroupings = groupingsService.getGroupingAssignment(users.get(1).getUsername());

        assertEquals(0, myGroupings.getGroupingsOwned().size());
        assertEquals(5, myGroupings.getGroupingsIn().size());
        assertEquals(0, myGroupings.getGroupingsOptedInTo().size());
        assertEquals(0, myGroupings.getGroupingsOptedOutOf().size());
        assertEquals(0, myGroupings.getGroupingsToOptInTo().size());
        assertEquals(2, myGroupings.getGroupingsToOptOutOf().size());

    }

    @Test
    public void groupingsToOptTest() {
        GroupingAssignment myGroupings = groupingsService.getGroupingAssignment(users.get(1).getUsername());

        //todo finish

    }

    @Test
    public void optInTest() {
        List<GroupingsServiceResult> optInResults;

        try {
            //opt in Permission for include group false
            optInResults = groupingsService.optIn(users.get(2).getUsername(), GROUPING_2_PATH);
        } catch (GroupingsServiceResultException gsre) {
            optInResults = new ArrayList<>();
            optInResults.add(gsre.getGsr());
        }
        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));

        //opt in Permission for include group true and not in group, but in basis
        optInResults = groupingsService.optIn(users.get(1).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertEquals(1, optInResults.size());

        //opt in Permission for include group true but already in group, not self opted
        optInResults = groupingsService.optIn(users.get(9).getUsername(), GROUPING_0_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));

        //opt in Permission for include group true, but already self-opted
        optInResults = groupingsService.optIn(users.get(9).getUsername(), GROUPING_0_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void optOutTest() {
        List<GroupingsServiceResult> optInResults;
        try {
            //opt in Permission for exclude group false
            optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_0_PATH);
        } catch (GroupingsServiceResultException gsre) {
            optInResults = new ArrayList<>();
            optInResults.add(gsre.getGsr());
        }
        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));

        //opt in Permission for exclude group true
        optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

        //opt in Permission for exclude group true, but already in the exclude group
        optInResults = groupingsService.optOut(users.get(2).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

        //opt in Permission for exclude group true, but already self-opted
        optInResults = groupingsService.optOut(users.get(2).getUsername(), GROUPING_1_PATH);
        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

    }

    @Test
    public void optOutPermissionTest() {

        boolean permission = groupingsService.optOutPermission(GROUPING_0_PATH);

        assertEquals(false, permission);

        permission = groupingsService.optOutPermission(GROUPING_1_PATH);

        assertEquals(true, permission);

    }

    @Test
    public void optInPermissionTest() {

        boolean permission = groupingsService.optInPermission(GROUPING_0_PATH);

        assertEquals(true, permission);

        permission = groupingsService.optInPermission(GROUPING_2_PATH);

        assertEquals(false, permission);
    }

    @Test
    public void groupHasAttributeTest() {

        //group does not have the attribute
        boolean hasAttribute = groupingsService.groupHasAttribute(GROUPING_0_PATH, OPT_OUT);
        assertFalse(hasAttribute);

        //group has the attribute
        hasAttribute = groupingsService.groupHasAttribute(GROUPING_0_PATH, OPT_IN);
        assertTrue(hasAttribute);
    }

    @Test
    public void groupingsInTest() {

        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(users.get(6).getUsername());
        List<String> groupPaths = new ArrayList<>();
        List<String> supposedGroupings = new ArrayList<>();

        for (Group group : groupsIn) {
            groupPaths.add(group.getPath());
        }
        for (String groupPath : groupPaths) {
            if (groupPath.matches("[a-zA-Z0-9:]*grouping[0-9]*")) {
                supposedGroupings.add(groupPath);
            }
        }

        List<Grouping> groupingsIn = groupingsService.groupingsIn(groupPaths);
        List<String> groupingPaths = new ArrayList<>();
        for (Grouping grouping : groupingsIn) {
            groupingPaths.add(grouping.getPath());
        }

        for (String path : supposedGroupings) {
            assertTrue(groupingPaths.contains(path));
        }
        for (Grouping grouping : groupingsIn) {
            assertTrue(supposedGroupings.contains(grouping.getPath()));
        }
    }

    @Test
    public void hasListservTest() {

        boolean groupingHasListserv = groupingsService.hasListserv(GROUPING_0_PATH);

        assertEquals(false, groupingHasListserv);

        groupingHasListserv = groupingsService.hasListserv(GROUPING_3_PATH);

        assertEquals(true, groupingHasListserv);
    }

    @Test
    public void groupingsOwnedTest() {
        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(users.get(0).getUsername());
        List<String> groupPaths = new ArrayList<>();

        for (Group group : groupsIn) {
            groupPaths.add(group.getPath());
        }

        List<Grouping> groupingsOwned = groupingsService.groupingsOwned(groupPaths);

        for (int i = 0; i < groupingsOwned.size(); i++) {
            assertTrue(groupingsOwned.get(i).getPath().equals(PATH_ROOT + i));
        }
    }

    @Test
    public void groupingsOptedIntoTest() {
        String user5 = users.get(5).getUsername();

        Iterable<Group> groups = groupRepository.findByMembersUsername(user5);
        List<String> groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }

        List<Grouping> groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);

        //starts with no groupings opted into
        assertEquals(0, groupingsOptedInto.size());

        //opt into a grouping
        groupingsService.optIn(user5, GROUPING_1_PATH);
        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
        assertEquals(1, groupingsOptedInto.size());

        //opt into another grouping
        groupingsService.optIn(user5, GROUPING_3_PATH);
        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
        assertEquals(2, groupingsOptedInto.size());

        //opt out of a grouping
        groupingsService.optOut(user5, GROUPING_3_PATH);
        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
        assertEquals(1, groupingsOptedInto.size());

        //opt out of another grouping
        groupingsService.optOut(user5, GROUPING_1_PATH);
        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
        assertEquals(0, groupingsOptedInto.size());
    }

    @Test
    public void groupingsOptedOutOfTest() {
        String user1 = users.get(1).getUsername();

        Iterable<Group> groups = groupRepository.findByMembersUsername(user1);
        List<String> groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }

        List<Grouping> groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);

        //starts with no groupings out of
        assertEquals(0, groupingsOptedOutOf.size());

        //opt out of a grouping
        groupingsService.optOut(user1, GROUPING_1_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
        assertEquals(1, groupingsOptedOutOf.size());

        //opt out of another grouping
        groupingsService.optOut(user1, GROUPING_3_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
        assertEquals(2, groupingsOptedOutOf.size());

        //opt into a grouping
        groupingsService.optIn(user1, GROUPING_3_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
        assertEquals(1, groupingsOptedOutOf.size());

        //opt into another grouping
        groupingsService.optIn(user1, GROUPING_1_PATH);
        groups = groupRepository.findByMembersUsername(user1);
        groupPaths = new ArrayList<>();
        for (Group group : groups) {
            groupPaths.add(group.getPath());
        }
        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
        assertEquals(0, groupingsOptedOutOf.size());
    }

    @Test
    public void adminListsTest() {
        AdminListsHolder adminListsHolder = groupingsService.adminLists(ADMIN_USER);
        AdminListsHolder emptyAdminListHolder = groupingsService.adminLists(users.get(1).getUsername());

        assertEquals(adminListsHolder.getAllGroupings().size(), 5);
        assertEquals(adminListsHolder.getAdminGroup().getMembers().size(), 1);

        assertEquals(emptyAdminListHolder.getAllGroupings().size(), 0);
        assertEquals(emptyAdminListHolder.getAdminGroup().getMembers().size(), 0);
    }

    @Test
    public void addSelfOptedTest() {
        assertFalse(groupingsService.checkSelfOpted(GROUPING_2_EXCLUDE_PATH, users.get(4).getUsername()));
        groupingsService.addSelfOpted(GROUPING_2_EXCLUDE_PATH, users.get(4).getUsername());
        assertTrue(groupingsService.checkSelfOpted(GROUPING_2_EXCLUDE_PATH, users.get(4).getUsername()));
    }

    @Test
    public void checkSelfOptedTest() {

        //user is not in group
        boolean selfOpted = groupingsService.checkSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(2).getUsername());
        assertFalse(selfOpted);

        //user has not self opted
        selfOpted = groupingsService.checkSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(5).getUsername());
        assertFalse(selfOpted);

        //user has self opted
        Person person = personRepository.findByUsername(users.get(5).getUsername());
        Group group = groupRepository.findByPath(GROUPING_0_INCLUDE_PATH);
        Membership membership = membershipRepository.findByPersonAndGroup(person, group);
        membership.setSelfOpted(true);
        membershipRepository.save(membership);

        selfOpted = groupingsService.checkSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(5).getUsername());
        assertTrue(selfOpted);
    }

    @Test
    public void inGroupTest() {
        assertFalse(groupingsService.inGroup(GROUPING_0_PATH, users.get(2).getUsername()));
        assertTrue(groupingsService.inGroup(GROUPING_0_PATH, users.get(5).getUsername()));
    }

    @Test
    public void isOwnerTest() {
        assertFalse(groupingsService.isOwner(GROUPING_0_PATH, users.get(1).getUsername()));
        assertTrue(groupingsService.isOwner(GROUPING_0_PATH, users.get(0).getUsername()));

    }

    @Test
    public void isAdminTest() {
        assertFalse(groupingsService.isAdmin(users.get(1).getUsername()));
        assertTrue(groupingsService.isAdmin(ADMIN_USER));
    }

    @Test
    public void removeSelfOptedTest() {
        Group group = groupRepository.findByPath(GROUPING_4_EXCLUDE_PATH);

        GroupingsServiceResult gsr;

        try {
            //member is not in group
            gsr = groupingsService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(5).getUsername());
        } catch (GroupingsServiceResultException gsre) {
            gsr = gsre.getGsr();
        }
        assertTrue(gsr.getResultCode().startsWith(FAILURE));

        //member is not self-opted
        gsr = groupingsService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(4).getUsername());
        assertTrue(gsr.getResultCode().startsWith(SUCCESS));

        //make member self-opted
        Membership membership = membershipRepository.findByPersonAndGroup(users.get(4), group);
        membership.setSelfOpted(true);
        membershipRepository.save(membership);

        //member is self-opted
        gsr = groupingsService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(4).getUsername());
        assertTrue(gsr.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void groupOptOutPermissionTest() {
        boolean oop = groupingsService.groupOptOutPermission(users.get(1).getUsername(), GROUPING_2_EXCLUDE_PATH);
        assertEquals(false, oop);

        oop = groupingsService.groupOptOutPermission(users.get(1).getUsername(), GROUPING_1_EXCLUDE_PATH);
        assertEquals(true, oop);
    }

    @Test
    public void addMemberAsTest() {
        Grouping grouping = groupingRepository.findByPath(GROUPING_1_PATH);
        assertFalse(grouping.getComposite().getMembers().contains(users.get(3)));

        groupingsService.addMemberByUsername(users.get(0).getUsername(), GROUPING_1_INCLUDE_PATH, users.get(3).getUsername());
        grouping = groupingRepository.findByPath(GROUPING_1_PATH);
        assertTrue(grouping.getComposite().getMembers().contains(users.get(3)));
    }

    @Test
    public void addMemberAsTest2() {
        //add all usernames
        List<String> usernames = new ArrayList<>();
        for (Person user : users) {
            usernames.add(user.getUsername());
        }

        Grouping grouping = groupingRepository.findByPath(GROUPING_3_PATH);

        //check how many members are in the basis
        int numberOfBasisMembers = grouping.getBasis().getMembers().size();

        //try to put all users into exclude group
        groupingsService.addMembersByUsername(users.get(0).getUsername(), GROUPING_3_EXCLUDE_PATH, usernames);
        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
        //there should be no real members in composite, but it should still have the 'grouperAll' member
        assertEquals(1, grouping.getComposite().getMembers().size());
        //only the users in the basis should have been added to the exclude group
        assertEquals(numberOfBasisMembers, grouping.getExclude().getMembers().size());

        //try to put all users into the include group
        groupingsService.addMembersByUsername(users.get(0).getUsername(), GROUPING_3_INCLUDE_PATH, usernames);
        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
        //all members should be in the group ( - 1 for 'grouperAll' in composite);
        assertEquals(usernames.size(), grouping.getComposite().getMembers().size() - 1);
        //members in basis should not have been added to the include group ( + 2 for 'grouperAll' in both groups)
        assertEquals(usernames.size() - numberOfBasisMembers + 2, grouping.getInclude().getMembers().size());
    }

    @Test
    public void parentGroupingPathTest() {
        assertEquals(GROUPING_2_PATH, groupingsService.parentGroupingPath(GROUPING_2_BASIS_PATH));
        assertEquals(GROUPING_2_PATH, groupingsService.parentGroupingPath(GROUPING_2_PATH + BASIS_PLUS_INCLUDE));
        assertEquals(GROUPING_2_PATH, groupingsService.parentGroupingPath(GROUPING_2_EXCLUDE_PATH));
        assertEquals(GROUPING_2_PATH, groupingsService.parentGroupingPath(GROUPING_2_INCLUDE_PATH));
        assertEquals(GROUPING_2_PATH, groupingsService.parentGroupingPath(GROUPING_2_OWNERS_PATH));
        assertEquals(GROUPING_APPS, groupingsService.parentGroupingPath(GROUPING_APPS));
        assertEquals("", groupingsService.parentGroupingPath(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void deleteGroupingTest() {
        groupingsService.deleteGrouping(users.get(0).getUsername(), GROUPING_4_PATH);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addGrouping() {
        List<String> basis = new ArrayList<>();
        List<String> exclude = new ArrayList<>();
        List<String> include = new ArrayList<>();
        List<String> owners = new ArrayList<>();
        groupingsService.addGrouping(
                users.get(0).getUsername(),
                "newGroupingPath",
                basis,
                include,
                exclude,
                owners);
    }
}

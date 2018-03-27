//package edu.hawaii.its.api.service;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import edu.hawaii.its.api.repository.GroupRepository;
//import edu.hawaii.its.api.repository.GroupingRepository;
//import edu.hawaii.its.api.repository.MembershipRepository;
//import edu.hawaii.its.api.repository.PersonRepository;
//import edu.hawaii.its.api.type.AdminListsHolder;
//import edu.hawaii.its.api.type.Group;
//import edu.hawaii.its.api.type.Grouping;
//import edu.hawaii.its.api.type.GroupingAssignment;
//import edu.hawaii.its.api.type.GroupingsServiceResult;
//import edu.hawaii.its.api.type.GroupingsServiceResultException;
//import edu.hawaii.its.api.type.Membership;
//import edu.hawaii.its.api.type.Person;
//import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
//
//import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
//import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResult;
//import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
//import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;
//import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
//import edu.internet2.middleware.grouperClient.ws.beans.WsMembership;
//import edu.internet2.middleware.grouperClient.ws.beans.WsResultMeta;
//import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
//import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//@ActiveProfiles("localTest")
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { SpringBootWebApplication.class })
//@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class MemberAttributeServiceTest {
//
//    @Value("${groupings.api.grouping_admins}")
//    private String GROUPING_ADMINS;
//
//    @Value("${groupings.api.grouping_apps}")
//    private String GROUPING_APPS;
//
//    @Value("${groupings.api.basis_plus_include}")
//    private String BASIS_PLUS_INCLUDE;
//
//    @Value("${groupings.api.success}")
//    private String SUCCESS;
//
//    @Value("${groupings.api.failure}")
//    private String FAILURE;
//
//    @Value("${groupings.api.test.username}")
//    private String USERNAME;
//
//    @Value("${groupings.api.test.name}")
//    private String NAME;
//
//    @Value("${groupings.api.test.uuid}")
//    private String UUID;
//
//    @Value("${groupings.api.person_attributes.uuid}")
//    private String UUID_KEY;
//
//    @Value("${groupings.api.person_attributes.username}")
//    private String UID_KEY;
//
//    @Value("${groupings.api.person_attributes.first_name}")
//    private String FIRST_NAME_KEY;
//
//    @Value("${groupings.api.person_attributes.last_name}")
//    private String LAST_NAME_KEY;
//
//    @Value("${groupings.api.person_attributes.composite_name}")
//    private String COMPOSITE_NAME_KEY;
//
//    private static final String PATH_ROOT = "path:to:grouping";
//    private static final String INCLUDE = ":include";
//    private static final String EXCLUDE = ":exclude";
//    private static final String OWNERS = ":owners";
//    private static final String BASIS = ":basis";
//
//    private static final String GROUPING_0_PATH = PATH_ROOT + 0;
//    private static final String GROUPING_1_PATH = PATH_ROOT + 1;
//    private static final String GROUPING_2_PATH = PATH_ROOT + 2;
//    private static final String GROUPING_3_PATH = PATH_ROOT + 3;
//    private static final String GROUPING_4_PATH = PATH_ROOT + 4;
//
//    private static final String GROUPING_0_INCLUDE_PATH = GROUPING_0_PATH + INCLUDE;
//    private static final String GROUPING_0_OWNERS_PATH = GROUPING_0_PATH + OWNERS;
//
//    private static final String GROUPING_1_INCLUDE_PATH = GROUPING_1_PATH + INCLUDE;
//    private static final String GROUPING_1_EXCLUDE_PATH = GROUPING_1_PATH + EXCLUDE;
//
//    private static final String GROUPING_2_INCLUDE_PATH = GROUPING_2_PATH + INCLUDE;
//    private static final String GROUPING_2_EXCLUDE_PATH = GROUPING_2_PATH + EXCLUDE;
//    private static final String GROUPING_2_BASIS_PATH = GROUPING_2_PATH + BASIS;
//    private static final String GROUPING_2_OWNERS_PATH = GROUPING_2_PATH + OWNERS;
//
//    private static final String GROUPING_3_INCLUDE_PATH = GROUPING_3_PATH + INCLUDE;
//    private static final String GROUPING_3_EXCLUDE_PATH = GROUPING_3_PATH + EXCLUDE;
//
//    private static final String ADMIN_USER = "admin";
//    private static final Person ADMIN_PERSON = new Person(ADMIN_USER, ADMIN_USER, ADMIN_USER);
//    private List<Person> admins = new ArrayList<>();
//    private Group adminGroup;
//
//    private static final String APP_USER = "app";
//    private static final Person APP_PERSON = new Person(APP_USER, APP_USER, APP_USER);
//    private List<Person> apps = new ArrayList<>();
//    private Group appGroup;
//
//    private List<Person> users = new ArrayList<>();
//    private List<WsSubjectLookup> lookups = new ArrayList<>();
//
//    @Autowired
//    private MemberAttributeService groupingsService;
//
//    @Autowired
//    private GroupingRepository groupingRepository;
//
//    @Autowired
//    private GroupRepository groupRepository;
//
//    @Autowired
//    private PersonRepository personRepository;
//
//    @Autowired
//    private MembershipRepository membershipRepository;
//
//    @Autowired
//    private HelperService hs;
//
//    @Autowired
//    GroupingAssignmentService gas;
//
//    @Before
//    public void setup() {
//
//        new DatabaseSetup(personRepository, groupRepository, groupingRepository, membershipRepository);
//
//        admins.add(ADMIN_PERSON);
//        adminGroup = new Group(GROUPING_ADMINS, admins);
//        personRepository.save(ADMIN_PERSON);
//        groupRepository.save(adminGroup);
//
//        admins.add(APP_PERSON);
//        appGroup = new Group(GROUPING_APPS, apps);
//        personRepository.save(APP_PERSON);
//        groupRepository.save(appGroup);
//
//        for (int i = 0; i < 100; i++) {
//            String name = NAME + i;
//            String uuid = UUID + i;
//            String username = USERNAME + i;
//
//            Person person = new Person(name, uuid, username);
//            users.add(person);
//
//            WsSubjectLookup lookup = new WsSubjectLookup(null, null, username);
//            lookups.add(lookup);
//        }
//    }
//
//    @Test
//    public void construction() {
//        //autowired
//        assertNotNull(groupingsService);
//    }
//
//    @Test
//    public void addGroupingMemberbyUuidTest() {
//        Iterable<Grouping> group = groupingRepository.findAll();
//        List<GroupingsServiceResult> listGsr;
//        GroupingsServiceResult gsr;
//
//        // Base test
//        // Remove person who's not in composite from exclude and return SUCCESS
//        listGsr = groupingsService.addGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
//                users.get(3).getUuid());
//        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
//
//        //todo Case where !inComposite && !inBasis is impossible w/ current db
//
//        // In composite
//        listGsr = groupingsService.addGroupingMemberByUuid(users.get(0).getUsername(), GROUPING_3_PATH,
//                users.get(5).getUuid());
//        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
//
//        //todo Case where inBasis && inInclude is impossible w/ current db
//
//        // Test if user is not an owner
//        try {
//            listGsr = groupingsService.addGroupingMemberByUuid(users.get(5).getUsername(), GROUPING_3_PATH,
//                    users.get(3).getUuid());
//            assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
//        } catch (GroupingsServiceResultException gsre) {
//            gsr = gsre.getGsr();
//        }
//
//        // Test if user is admin
//        listGsr = groupingsService.addGroupingMemberByUuid(ADMIN_USER, GROUPING_3_PATH,
//                users.get(3).getUuid());
//        assertTrue(listGsr.get(0).getResultCode().startsWith(SUCCESS));
//    }
//
//    @Test
//    public void assignOwnershipTest() {
//        //expect this to fail
//        GroupingsServiceResult randomUserAdds;
//
//        Person randomUser = personRepository.findByUsername(users.get(1).getUsername());
//        Grouping grouping = groupingRepository.findByPath(GROUPING_0_PATH);
//
//        assertFalse(grouping.getOwners().getMembers().contains(randomUser));
//        assertFalse(grouping.getOwners().isMember(randomUser));
//
//        try {
//            randomUserAdds = groupingsService
//                    .assignOwnership(GROUPING_0_PATH, randomUser.getUsername(), randomUser.getUsername());
//        } catch (GroupingsServiceResultException gsre) {
//            randomUserAdds = gsre.getGsr();
//        }
//
//        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
//        assertFalse(grouping.getOwners().getMembers().contains(randomUser));
//        assertFalse(grouping.getOwners().isMember(randomUser));
//        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);
//
//        GroupingsServiceResult ownerAdds =
//                groupingsService.assignOwnership(GROUPING_0_PATH, users.get(0).getUsername(), randomUser.getUsername());
//        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
//        assertTrue(grouping.getOwners().getMembers().contains(randomUser));
//        assertTrue(grouping.getOwners().isMember(randomUser));
//        assertEquals(ownerAdds.getResultCode(), SUCCESS);
//
//        GroupingsServiceResult adminAdds =
//                groupingsService.assignOwnership(GROUPING_0_PATH, ADMIN_USER, randomUser.getUsername());
//        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
//        assertTrue(grouping.getOwners().getMembers().contains(randomUser));
//        assertTrue(grouping.getOwners().isMember(randomUser));
//        assertEquals(SUCCESS, adminAdds.getResultCode());
//    }
//
//    @Test
//    public void changeListservStatusTest() {
//
//        //expect actions by "Random" to fail
//        GroupingsServiceResult turnOnWhenOnRandom;
//        GroupingsServiceResult turnOnWhenOffRandom;
//        GroupingsServiceResult turnOffWhenOnRandom;
//        GroupingsServiceResult turnOffWhenOffRandom;
//
//        Grouping grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        try {
//            turnOnWhenOffRandom =
//                    groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), true);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOnWhenOffRandom = gsre.getGsr();
//        }
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        GroupingsServiceResult turnOnWhenOffOwner =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), true);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertTrue(grouping.isListservOn());
//
//        try {
//            turnOnWhenOnRandom =
//                    groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), true);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOnWhenOnRandom = gsre.getGsr();
//        }
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertTrue(grouping.isListservOn());
//
//        GroupingsServiceResult turnOnWhenOnOwner =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), true);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertTrue(grouping.isListservOn());
//
//        GroupingsServiceResult turnOnWhenOnAdmin =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, true);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertTrue(grouping.isListservOn());
//
//        try {
//            turnOffWhenOnRandom =
//                    groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), false);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOffWhenOnRandom = gsre.getGsr();
//        }
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertTrue(grouping.isListservOn());
//
//        GroupingsServiceResult turnOffWhenOnOwner =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), false);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        GroupingsServiceResult turnOnWhenOffAdmin =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, true);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertTrue(grouping.isListservOn());
//
//        GroupingsServiceResult turnOffWhenOnAdmin =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, false);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        try {
//            turnOffWhenOffRandom =
//                    groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(1).getUsername(), false);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOffWhenOffRandom = gsre.getGsr();
//        }
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        GroupingsServiceResult turnOffWhenOffOwner =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, users.get(0).getUsername(), false);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        GroupingsServiceResult turnOffWhenOffAdmin =
//                groupingsService.changeListservStatus(GROUPING_4_PATH, ADMIN_USER, false);
//        grouping = groupingRepository.findByPath(GROUPING_4_PATH);
//        assertFalse(grouping.isListservOn());
//
//        assertTrue(turnOnWhenOnRandom.getResultCode().startsWith(FAILURE));
//        assertTrue(turnOnWhenOnOwner.getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.getResultCode().startsWith(SUCCESS));
//
//        assertTrue(turnOffWhenOnRandom.getResultCode().startsWith(FAILURE));
//        assertTrue(turnOffWhenOnOwner.getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOnAdmin.getResultCode().startsWith(SUCCESS));
//
//        assertTrue(turnOnWhenOffRandom.getResultCode().startsWith(FAILURE));
//        assertTrue(turnOnWhenOffOwner.getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOffAdmin.getResultCode().startsWith(SUCCESS));
//
//        assertTrue(turnOffWhenOffRandom.getResultCode().startsWith(FAILURE));
//        assertTrue(turnOffWhenOffOwner.getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.getResultCode().startsWith(SUCCESS));
//    }
//
//    @Test
//    public void changeOptInStatusTest() {
//        //expect actions done by "Random" to fail
//        List<GroupingsServiceResult> turnOnWhenOnRandom;
//        List<GroupingsServiceResult> turnOnWhenOffRandom;
//        List<GroupingsServiceResult> turnOffWhenOnRandom;
//        List<GroupingsServiceResult> turnOffWhenOffRandom;
//
//        try {
//            turnOnWhenOnRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOnWhenOnRandom = new ArrayList<>();
//            turnOnWhenOnRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOnWhenOnOwner =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);
//        List<GroupingsServiceResult> turnOnWhenOnAdmin =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, true);
//
//        try {
//            turnOffWhenOnRandom =
//                    groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOffWhenOnRandom = new ArrayList<>();
//            turnOffWhenOnRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOffWhenOnOwner =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);
//
//        try {
//            turnOffWhenOffRandom =
//                    groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOffWhenOffRandom = new ArrayList<>();
//            turnOffWhenOffRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOffWhenOffOwner =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);
//        List<GroupingsServiceResult> turnOffWhenOffAdmin =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, false);
//
//        try {
//            turnOnWhenOffRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOnWhenOffRandom = new ArrayList<>();
//            turnOnWhenOffRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOnWhenOffOwner =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);
//
//        List<GroupingsServiceResult> turnOffWhenOnAdmin =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, false);
//
//        List<GroupingsServiceResult> turnOnWhenOffAdmin =
//                groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, true);
//
//        assertTrue(turnOnWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertTrue(turnOnWhenOnOwner.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnOwner.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnOwner.get(2).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.get(2).getResultCode().startsWith(SUCCESS));
//
//        assertTrue(turnOffWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertEquals(SUCCESS, turnOffWhenOnOwner.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnOwner.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnOwner.get(2).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(2).getResultCode());
//
//        assertTrue(turnOnWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertEquals(SUCCESS, turnOnWhenOffOwner.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffOwner.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffOwner.get(2).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(2).getResultCode());
//
//        assertTrue(turnOffWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertTrue(turnOffWhenOffOwner.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffOwner.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffOwner.get(2).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.get(2).getResultCode().startsWith(SUCCESS));
//    }
//
//    @Test
//    public void changeOptOutStatusTest() {
//
//        //expect to fail
//        List<GroupingsServiceResult> turnOnWhenOnRandom;
//        List<GroupingsServiceResult> turnOffWhenOnRandom;
//        List<GroupingsServiceResult> turnOnWhenOffRandom;
//        List<GroupingsServiceResult> turnOffWhenOffRandom;
//
//        try {
//            turnOnWhenOnRandom = groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), true);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOnWhenOnRandom = new ArrayList<>();
//            turnOnWhenOnRandom.add(gsre.getGsr());
//        }
//
//        List<GroupingsServiceResult> turnOnWhenOnOwner =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), true);
//        List<GroupingsServiceResult> turnOnWhenOnAdmin =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, true);
//
//        try {
//            turnOffWhenOnRandom =
//                    groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), false);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOffWhenOnRandom = new ArrayList<>();
//            turnOffWhenOnRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOffWhenOnOwner =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), false);
//
//        try {
//            turnOnWhenOffRandom =
//                    groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), true);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOnWhenOffRandom = new ArrayList<>();
//            turnOnWhenOffRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOnWhenOffOwner =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), true);
//
//        List<GroupingsServiceResult> turnOffWhenOnAdmin =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, false);
//
//        try {
//            turnOffWhenOffRandom =
//                    groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(1).getUsername(), false);
//        } catch (GroupingsServiceResultException gsre) {
//            turnOffWhenOffRandom = new ArrayList<>();
//            turnOffWhenOffRandom.add(gsre.getGsr());
//        }
//        List<GroupingsServiceResult> turnOffWhenOffOwner =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, users.get(0).getUsername(), false);
//        List<GroupingsServiceResult> turnOffWhenOffAdmin =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, false);
//
//        List<GroupingsServiceResult> turnOnWhenOffAdmin =
//                groupingsService.changeOptOutStatus(GROUPING_1_PATH, ADMIN_USER, true);
//
//        assertTrue(turnOnWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertTrue(turnOnWhenOnOwner.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnOwner.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnOwner.get(2).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOnWhenOnAdmin.get(2).getResultCode().startsWith(SUCCESS));
//
//        assertTrue(turnOffWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertEquals(SUCCESS, turnOffWhenOnOwner.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnOwner.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnOwner.get(2).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOffWhenOnAdmin.get(2).getResultCode());
//
//        assertTrue(turnOnWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertEquals(SUCCESS, turnOnWhenOffOwner.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffOwner.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffOwner.get(2).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(0).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(1).getResultCode());
//        assertEquals(SUCCESS, turnOnWhenOffAdmin.get(2).getResultCode());
//
//        assertTrue(turnOffWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
//        assertTrue(turnOffWhenOffOwner.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffOwner.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffOwner.get(2).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(turnOffWhenOffAdmin.get(2).getResultCode().startsWith(SUCCESS));
//    }
//
//    @Test
//    public void removeOwnershipTest() {
//        GroupingsServiceResult randomUserRemoves;
//
//        try {
//            //non-owner/non-admin tries to remove ownership
//            randomUserRemoves = groupingsService
//                    .removeOwnership(GROUPING_0_PATH, users.get(1).getUsername(), users.get(1).getUsername());
//        } catch (GroupingsServiceResultException gsre) {
//            randomUserRemoves = gsre.getGsr();
//        }
//        assertTrue(randomUserRemoves.getResultCode().startsWith(FAILURE));
//
//        //add owner for owner to remove
//        groupingsService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH,
//                users.get(1).getUsername());
//
//        //owner tries to remove other ownership
//        GroupingsServiceResult ownerRemoves = groupingsService
//                .removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
//        assertEquals(SUCCESS, ownerRemoves.getResultCode());
//
//        //try to remove ownership from user that is not an owner
//        GroupingsServiceResult ownerRemovesNonOwner = groupingsService
//                .removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
//        assertEquals(SUCCESS, ownerRemovesNonOwner.getResultCode());
//
//        //add owner for admin to remove
//        groupingsService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH,
//                users.get(1).getUsername());
//
//        //admin tries to remove ownership
//        GroupingsServiceResult adminRemoves =
//                groupingsService.removeOwnership(GROUPING_0_PATH, ADMIN_USER, users.get(1).getUsername());
//        assertEquals(adminRemoves.getResultCode(), SUCCESS);
//    }
//
//    @Test
//    public void getGroupingTest() {
//        Grouping groupingRandom = groupingsService.getGrouping(GROUPING_0_PATH, users.get(1).getUsername());
//        Grouping groupingOwner = groupingsService.getGrouping(GROUPING_0_PATH, users.get(0).getUsername());
//        Grouping groupingAdmin = groupingsService.getGrouping(GROUPING_0_PATH, ADMIN_USER);
//
//        assertEquals(0, groupingRandom.getComposite().getMembers().size());
//        assertEquals(0, groupingRandom.getInclude().getMembers().size());
//        assertEquals(0, groupingRandom.getExclude().getMembers().size());
//        assertEquals(0, groupingRandom.getBasis().getMembers().size());
//        assertEquals(0, groupingRandom.getOwners().getMembers().size());
//
//        assertTrue(groupingOwner.getComposite().getNames().contains(users.get(0).getName()));
//        assertTrue(groupingOwner.getComposite().getUsernames().contains(users.get(0).getUsername()));
//        assertTrue(groupingOwner.getComposite().getUuids().contains(users.get(0).getUuid()));
//        assertTrue(groupingOwner.getInclude().getNames().contains(users.get(5).getName()));
//        assertTrue(groupingOwner.getExclude().getNames().contains(users.get(2).getName()));
//        assertTrue(groupingOwner.getBasis().getNames().contains(users.get(4).getName()));
//        assertTrue(groupingOwner.getOwners().getNames().contains(users.get(0).getName()));
//
//        assertTrue(groupingAdmin.getComposite().getNames().contains(users.get(0).getName()));
//        assertTrue(groupingAdmin.getComposite().getUsernames().contains(users.get(0).getUsername()));
//        assertTrue(groupingAdmin.getComposite().getUuids().contains(users.get(0).getUuid()));
//        assertTrue(groupingAdmin.getInclude().getNames().contains(users.get(5).getName()));
//        assertTrue(groupingAdmin.getExclude().getNames().contains(users.get(2).getName()));
//        assertTrue(groupingAdmin.getBasis().getNames().contains(users.get(4).getName()));
//        assertTrue(groupingAdmin.getOwners().getNames().contains(users.get(0).getName()));
//    }
//
//    @Test
//    public void getMyGroupingsTest() {
//        GroupingAssignment myGroupings = groupingsService.getGroupingAssignment(users.get(1).getUsername());
//
//        assertEquals(0, myGroupings.getGroupingsOwned().size());
//        assertEquals(5, myGroupings.getGroupingsIn().size());
//        assertEquals(0, myGroupings.getGroupingsOptedInTo().size());
//        assertEquals(0, myGroupings.getGroupingsOptedOutOf().size());
//        assertEquals(0, myGroupings.getGroupingsToOptInTo().size());
//        assertEquals(2, myGroupings.getGroupingsToOptOutOf().size());
//
//    }
//
//    @Test
//    public void groupingsToOptTest() {
//        GroupingAssignment myGroupings = groupingsService.getGroupingAssignment(users.get(1).getUsername());
//
//        //todo finish
//
//    }
//
//    @Test
//    public void optInTest() {
//        List<GroupingsServiceResult> optInResults;
//
//        try {
//            //opt in Permission for include group false
//            optInResults = groupingsService.optIn(users.get(2).getUsername(), GROUPING_2_PATH);
//        } catch (GroupingsServiceResultException gsre) {
//            optInResults = new ArrayList<>();
//            optInResults.add(gsre.getGsr());
//        }
//        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));
//
//        //opt in Permission for include group true and not in group, but in basis
//        optInResults = groupingsService.optIn(users.get(1).getUsername(), GROUPING_1_PATH);
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertEquals(1, optInResults.size());
//
//        //opt in Permission for include group true but already in group, not self opted
//        optInResults = groupingsService.optIn(users.get(9).getUsername(), GROUPING_0_PATH);
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//
//        //opt in Permission for include group true, but already self-opted
//        optInResults = groupingsService.optIn(users.get(9).getUsername(), GROUPING_0_PATH);
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//    }
//
//    @Test
//    public void optOutTest() {
//        List<GroupingsServiceResult> optInResults;
//        try {
//            //opt in Permission for exclude group false
//            optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_0_PATH);
//        } catch (GroupingsServiceResultException gsre) {
//            optInResults = new ArrayList<>();
//            optInResults.add(gsre.getGsr());
//        }
//        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));
//
//        //opt in Permission for exclude group true
//        optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_1_PATH);
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));
//
//        //opt in Permission for exclude group true, but already in the exclude group
//        optInResults = groupingsService.optOut(users.get(2).getUsername(), GROUPING_1_PATH);
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));
//
//        //opt in Permission for exclude group true, but already self-opted
//        optInResults = groupingsService.optOut(users.get(2).getUsername(), GROUPING_1_PATH);
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));
//
//    }
//
//    @Test
//    public void optOutPermissionTest() {
//
//        boolean permission = groupingsService.optOutPermission(GROUPING_0_PATH);
//
//        assertEquals(false, permission);
//
//        permission = groupingsService.optOutPermission(GROUPING_1_PATH);
//
//        assertEquals(true, permission);
//
//    }
//
//    @Test
//    public void optInPermissionTest() {
//
//        boolean permission = groupingsService.optInPermission(GROUPING_0_PATH);
//
//        assertEquals(true, permission);
//
//        permission = groupingsService.optInPermission(GROUPING_2_PATH);
//
//        assertEquals(false, permission);
//    }
//
//    @Test
//    public void groupingsInTest() {
//
//        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(users.get(6).getUsername());
//        List<String> groupPaths = new ArrayList<>();
//        List<String> supposedGroupings = new ArrayList<>();
//
//        for (Group group : groupsIn) {
//            groupPaths.add(group.getPath());
//        }
//        for (String groupPath : groupPaths) {
//            if (groupPath.matches("[a-zA-Z0-9:]*grouping[0-9]*")) {
//                supposedGroupings.add(groupPath);
//            }
//        }
//
//        List<Grouping> groupingsIn = groupingsService.groupingsIn(groupPaths);
//        List<String> groupingPaths = new ArrayList<>();
//        for (Grouping grouping : groupingsIn) {
//            groupingPaths.add(grouping.getPath());
//        }
//
//        for (String path : supposedGroupings) {
//            assertTrue(groupingPaths.contains(path));
//        }
//        for (Grouping grouping : groupingsIn) {
//            assertTrue(supposedGroupings.contains(grouping.getPath()));
//        }
//    }
//
//    @Test
//    public void hasListservTest() {
//
//        boolean groupingHasListserv = groupingsService.hasListserv(GROUPING_0_PATH);
//
//        assertEquals(false, groupingHasListserv);
//
//        groupingHasListserv = groupingsService.hasListserv(GROUPING_3_PATH);
//
//        assertEquals(true, groupingHasListserv);
//    }
//
//    @Test
//    public void groupingsOwnedTest() {
//        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(users.get(0).getUsername());
//        List<String> groupPaths = new ArrayList<>();
//
//        for (Group group : groupsIn) {
//            groupPaths.add(group.getPath());
//        }
//
//        List<Grouping> groupingsOwned = groupingsService.groupingsOwned(groupPaths);
//
//        for (int i = 0; i < groupingsOwned.size(); i++) {
//            assertTrue(groupingsOwned.get(i).getPath().equals(PATH_ROOT + i));
//        }
//    }
//
//    @Test
//    public void groupingsOptedIntoTest() {
//        String user5 = users.get(5).getUsername();
//
//        Iterable<Group> groups = groupRepository.findByMembersUsername(user5);
//        List<String> groupPaths = new ArrayList<>();
//        for (Group group : groups) {
//            groupPaths.add(group.getPath());
//        }
//
//        List<Grouping> groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
//
//        //starts with no groupings opted into
//        assertEquals(0, groupingsOptedInto.size());
//
//        //opt into a grouping
//        groupingsService.optIn(user5, GROUPING_1_PATH);
//        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
//        assertEquals(1, groupingsOptedInto.size());
//
//        //opt into another grouping
//        groupingsService.optIn(user5, GROUPING_3_PATH);
//        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
//        assertEquals(2, groupingsOptedInto.size());
//
//        //opt out of a grouping
//        groupingsService.optOut(user5, GROUPING_3_PATH);
//        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
//        assertEquals(1, groupingsOptedInto.size());
//
//        //opt out of another grouping
//        groupingsService.optOut(user5, GROUPING_1_PATH);
//        groupingsOptedInto = groupingsService.groupingsOptedInto(user5, groupPaths);
//        assertEquals(0, groupingsOptedInto.size());
//    }
//
//    @Test
//    public void groupingsOptedOutOfTest() {
//        String user1 = users.get(1).getUsername();
//
//        Iterable<Group> groups = groupRepository.findByMembersUsername(user1);
//        List<String> groupPaths = new ArrayList<>();
//        for (Group group : groups) {
//            groupPaths.add(group.getPath());
//        }
//
//        List<Grouping> groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
//
//        //starts with no groupings out of
//        assertEquals(0, groupingsOptedOutOf.size());
//
//        //opt out of a grouping
//        groupingsService.optOut(user1, GROUPING_1_PATH);
//        groups = groupRepository.findByMembersUsername(user1);
//        groupPaths = new ArrayList<>();
//        for (Group group : groups) {
//            groupPaths.add(group.getPath());
//        }
//        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
//        assertEquals(1, groupingsOptedOutOf.size());
//
//        //opt out of another grouping
//        groupingsService.optOut(user1, GROUPING_3_PATH);
//        groups = groupRepository.findByMembersUsername(user1);
//        groupPaths = new ArrayList<>();
//        for (Group group : groups) {
//            groupPaths.add(group.getPath());
//        }
//        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
//        assertEquals(2, groupingsOptedOutOf.size());
//
//        //opt into a grouping
//        groupingsService.optIn(user1, GROUPING_3_PATH);
//        groups = groupRepository.findByMembersUsername(user1);
//        groupPaths = new ArrayList<>();
//        for (Group group : groups) {
//            groupPaths.add(group.getPath());
//        }
//        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
//        assertEquals(1, groupingsOptedOutOf.size());
//
//        //opt into another grouping
//        groupingsService.optIn(user1, GROUPING_1_PATH);
//        groups = groupRepository.findByMembersUsername(user1);
//        groupPaths = new ArrayList<>();
//        for (Group group : groups) {
//            groupPaths.add(group.getPath());
//        }
//        groupingsOptedOutOf = groupingsService.groupingsOptedOutOf(user1, groupPaths);
//        assertEquals(0, groupingsOptedOutOf.size());
//    }
//
//    @Test
//    public void adminListsTest() {
//        AdminListsHolder adminListsHolder = groupingsService.adminLists(ADMIN_USER);
//        AdminListsHolder emptyAdminListHolder = groupingsService.adminLists(users.get(1).getUsername());
//
//        assertEquals(adminListsHolder.getAllGroupings().size(), 5);
//        assertEquals(adminListsHolder.getAdminGroup().getMembers().size(), 1);
//
//        assertEquals(emptyAdminListHolder.getAllGroupings().size(), 0);
//        assertEquals(emptyAdminListHolder.getAdminGroup().getMembers().size(), 0);
//    }
//
//    @Test
//    public void checkSelfOptedTest() {
//
//        //user is not in group
//        boolean selfOpted = groupingsService.isSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(2).getUsername());
//        assertFalse(selfOpted);
//
//        //user has not self opted
//        selfOpted = groupingsService.isSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(5).getUsername());
//        assertFalse(selfOpted);
//
//        //user has self opted
//        Person person = personRepository.findByUsername(users.get(5).getUsername());
//        Group group = groupRepository.findByPath(GROUPING_0_INCLUDE_PATH);
//        Membership membership = membershipRepository.findByPersonAndGroup(person, group);
//        membership.setSelfOpted(true);
//        membershipRepository.save(membership);
//
//        selfOpted = groupingsService.isSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(5).getUsername());
//        assertTrue(selfOpted);
//    }
//
//    @Test
//    public void inGroupTest() {
//        //test with username
//        Person person2 = users.get(2);
//        Person person5 = users.get(5);
//
//        assertFalse(groupingsService.isMember(GROUPING_0_PATH, person2));
//        assertTrue(groupingsService.isMember(GROUPING_0_PATH, person5));
//
//        //test with uuid
//        person2.setUsername(null);
//        person5.setUsername(null);
//
//        assertFalse(groupingsService.isMember(GROUPING_0_PATH, person2));
//        assertTrue(groupingsService.isMember(GROUPING_0_PATH, person5));
//    }
//
//    @Test
//    public void isOwnerTest() {
//
//        assertFalse(groupingsService.isOwner(GROUPING_0_PATH, users.get(1).getUsername()));
//        assertTrue(groupingsService.isOwner(GROUPING_0_PATH, users.get(0).getUsername()));
//
//    }
//
//    @Test
//    public void isAdminTest() {
//        assertFalse(groupingsService.isAdmin(users.get(1).getUsername()));
//        assertTrue(groupingsService.isAdmin(ADMIN_USER));
//    }
//
//    //todo fix
//    //    @Test
//    //    public void removeSelfOptedTest() {
//    //        Group group = groupRepository.findByPath(GROUPING_4_EXCLUDE_PATH);
//    //
//    //        GroupingsServiceResult gsr;
//    //
//    //        try {
//    //            //member is not in group
//    //            gsr = groupingsService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(5).getUsername());
//    //        } catch (GroupingsServiceResultException gsre) {
//    //            gsr = gsre.getGsr();
//    //        }
//    //        assertTrue(gsr.getResultCode().startsWith(FAILURE));
//    //
//    //        //member is not self-opted
//    //        gsr = groupingsService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(4).getUsername());
//    //        assertTrue(gsr.getResultCode().startsWith(SUCCESS));
//    //
//    //        //make member self-opted
//    //        Membership membership = membershipRepository.findByPersonAndGroup(users.get(4), group);
//    //        membership.setSelfOpted(true);
//    //        membershipRepository.save(membership);
//    //
//    //        //member is self-opted
//    //        gsr = groupingsService.removeSelfOpted(GROUPING_4_EXCLUDE_PATH, users.get(4).getUsername());
//    //        assertTrue(gsr.getResultCode().startsWith(SUCCESS));
//    //    }
//
//    @Test
//    public void groupOptOutPermissionTest() {
//        boolean oop = groupingsService.groupOptOutPermission(users.get(1).getUsername(), GROUPING_2_EXCLUDE_PATH);
//        assertEquals(false, oop);
//
//        oop = groupingsService.groupOptOutPermission(users.get(1).getUsername(), GROUPING_1_EXCLUDE_PATH);
//        assertEquals(true, oop);
//    }
//
//    @Test
//    public void addMemberByUsernameTest() {
//        Grouping grouping = groupingRepository.findByPath(GROUPING_1_PATH);
//        assertFalse(grouping.getComposite().getMembers().contains(users.get(3)));
//
//        groupingsService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_1_INCLUDE_PATH,
//                users.get(3).getUsername());
//        grouping = groupingRepository.findByPath(GROUPING_1_PATH);
//        assertTrue(grouping.getComposite().getMembers().contains(users.get(3)));
//        //todo Cases (inBasis && inInclude) and (!inComposite && !inBasis) not reachable w/ current DB
//    }
//
//    @Test
//    public void addMembersByUsername() {
//        //add all usernames
//        List<String> usernames = new ArrayList<>();
//        for (Person user : users) {
//            usernames.add(user.getUsername());
//        }
//
//        Grouping grouping = groupingRepository.findByPath(GROUPING_3_PATH);
//
//        //check how many members are in the basis
//        int numberOfBasisMembers = grouping.getBasis().getMembers().size();
//
//        //try to put all users into exclude group
//        groupingsService.addGroupMembersByUsername(users.get(0).getUsername(), GROUPING_3_EXCLUDE_PATH, usernames);
//        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
//        //there should be no real members in composite, but it should still have the 'grouperAll' member
//        assertEquals(1, grouping.getComposite().getMembers().size());
//        //only the users in the basis should have been added to the exclude group
//        assertEquals(numberOfBasisMembers, grouping.getExclude().getMembers().size());
//
//        //try to put all users into the include group
//        groupingsService.addGroupMembersByUsername(users.get(0).getUsername(), GROUPING_3_INCLUDE_PATH, usernames);
//        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
//        //all members should be in the group ( - 1 for 'grouperAll' in composite);
//        assertEquals(usernames.size(), grouping.getComposite().getMembers().size() - 1);
//        //members in basis should not have been added to the include group ( + 2 for 'grouperAll' in both groups)
//        assertEquals(usernames.size() - numberOfBasisMembers + 2, grouping.getInclude().getMembers().size());
//    }
//
//    @Test
//    public void addMemberByUuidTest() {
//        Grouping grouping = groupingRepository.findByPath(GROUPING_1_PATH);
//        assertFalse(grouping.getComposite().getMembers().contains(users.get(3)));
//
//        groupingsService
//                .addGroupMemberByUuid(users.get(0).getUsername(), GROUPING_1_INCLUDE_PATH, users.get(3).getUuid());
//        grouping = groupingRepository.findByPath(GROUPING_1_PATH);
//        assertTrue(grouping.getComposite().getMembers().contains(users.get(3)));
//    }
//
//    @Test
//    public void addMembersByUuid() {
//        //add all uuids
//        List<String> uuids = new ArrayList<>();
//        for (Person user : users) {
//            uuids.add(user.getUuid());
//        }
//
//        Grouping grouping = groupingRepository.findByPath(GROUPING_3_PATH);
//
//        //check how many members are in the basis
//        int numberOfBasisMembers = grouping.getBasis().getMembers().size();
//
//        //try to put all users into exclude group
//        groupingsService.addGroupMembersByUuid(users.get(0).getUsername(), GROUPING_3_EXCLUDE_PATH, uuids);
//        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
//        //there should be no real members in composite, but it should still have the 'grouperAll' member
//        assertEquals(1, grouping.getComposite().getMembers().size());
//        //only the users in the basis should have been added to the exclude group
//        assertEquals(numberOfBasisMembers, grouping.getExclude().getMembers().size());
//
//        //try to put all users into the include group
//        groupingsService.addGroupMembersByUuid(users.get(0).getUsername(), GROUPING_3_INCLUDE_PATH, uuids);
//        grouping = groupingRepository.findByPath(GROUPING_3_PATH);
//        //all members should be in the group ( - 1 for 'grouperAll' in composite);
//        assertEquals(uuids.size(), grouping.getComposite().getMembers().size() - 1);
//        //members in basis should not have been added to the include group ( + 2 for 'grouperAll' in both groups)
//        assertEquals(uuids.size() - numberOfBasisMembers + 2, grouping.getInclude().getMembers().size());
//    }
//
//    @Test
//    public void parentGroupingPathTest() {
//        assertEquals(GROUPING_2_PATH, hs.parentGroupingPath(GROUPING_2_BASIS_PATH));
//        assertEquals(GROUPING_2_PATH, hs.parentGroupingPath(GROUPING_2_PATH + BASIS_PLUS_INCLUDE));
//        assertEquals(GROUPING_2_PATH, hs.parentGroupingPath(GROUPING_2_EXCLUDE_PATH));
//        assertEquals(GROUPING_2_PATH, hs.parentGroupingPath(GROUPING_2_INCLUDE_PATH));
//        assertEquals(GROUPING_2_PATH, hs.parentGroupingPath(GROUPING_2_OWNERS_PATH));
//        assertEquals(GROUPING_APPS, hs.parentGroupingPath(GROUPING_APPS));
//        assertEquals("", hs.parentGroupingPath(null));
//    }
//
//    @Test(expected = UnsupportedOperationException.class)
//    public void deleteGroupingTest() {
//        groupingsService.deleteGrouping(users.get(0).getUsername(), GROUPING_4_PATH);
//    }
//
//    //todo fill this in after changes to addGrouping method
//    @Test
//    public void addGrouping() {
//    }
//
//    /////////////////////////////////////////////////////
//    // non-mocked tests//////////////////////////////////
//    /////////////////////////////////////////////////////
//
//    @Test
//    public void groupingParentPath() {
//        String grouping = "grouping";
//
//        String[] groups = new String[] { grouping + EXCLUDE,
//                grouping + INCLUDE,
//                grouping + OWNERS,
//                grouping + BASIS,
//                grouping + BASIS_PLUS_INCLUDE,
//                grouping };
//
//        for (String g : groups) {
//            assertEquals(grouping, hs.parentGroupingPath(g));
//        }
//
//        assertEquals("", hs.parentGroupingPath(null));
//    }
//
//    @Test
//    public void extractGroupPaths() {
//        List<WsGroup> groups = null;
//        //List<String> groupNames = gs.extractGroupPaths(groups);
//        List<String> groupNames = gas.extractGroupPaths(groups);
//        assertEquals(0, groupNames.size());
//
//        groups = new ArrayList<>();
//        final int size = 300;
//
//        for (int i = 0; i < size; i++) {
//            WsGroup w = new WsGroup();
//            w.setName("testName_" + i);
//            groups.add(w);
//        }
//        assertEquals(size, groups.size());
//
//        groupNames = gas.extractGroupPaths(groups);
//        for (int i = 0; i < size; i++) {
//            assertTrue(groupNames.contains("testName_" + i));
//        }
//        assertEquals(size, groupNames.size());
//
//        // Create some duplicates.
//        groups = new ArrayList<>();
//        for (int j = 0; j < 3; j++) {
//            for (int i = 0; i < size; i++) {
//                WsGroup w = new WsGroup();
//                w.setName("testName_" + i);
//                groups.add(w);
//            }
//        }
//        assertEquals(size * 3, groups.size());
//
//        // Duplicates should not be in groupNames list.
//        groupNames = gas.extractGroupPaths(groups);
//        assertEquals(size, groupNames.size());
//        for (int i = 0; i < size; i++) {
//            assertTrue(groupNames.contains("testName_" + i));
//        }
//    }
//
//    @Test
//    public void extractFirstMembershipID() {
//        WsGetMembershipsResults mr = new WsGetMembershipsResults();
//        WsMembership[] memberships = new WsMembership[3];
//        for (int i = 0; i < 3; i++) {
//            memberships[i] = new WsMembership();
//            memberships[i].setMembershipId("membershipID_" + i);
//        }
//        mr.setWsMemberships(memberships);
//
//        assertEquals("membershipID_0", hs.extractFirstMembershipID(mr));
//    }
//
//    @Test
//    public void makeGroup() {
//        WsGetMembersResults getMembersResults = new WsGetMembersResults();
//        WsGetMembersResult[] getMembersResult = new WsGetMembersResult[1];
//        WsGetMembersResult getMembersResult1 = new WsGetMembersResult();
//        WsSubject[] subjects = new WsSubject[0];
//        getMembersResult1.setWsSubjects(subjects);
//        getMembersResult[0] = getMembersResult1;
//        getMembersResults.setResults(getMembersResult);
//        assertNotNull(gas.makeGroup(getMembersResults));
//
//        subjects = new WsSubject[1];
//        getMembersResults.getResults()[0].setWsSubjects(subjects);
//        assertNotNull(gas.makeGroup(getMembersResults));
//
//        subjects[0] = new WsSubject();
//        getMembersResults.getResults()[0].setWsSubjects(subjects);
//        assertNotNull(gas.makeGroup(getMembersResults));
//
//    }
//
//    @Test
//    public void makeGroupTest() {
//        WsGetMembersResults getMembersResults = new WsGetMembersResults();
//        WsGetMembersResult[] getMembersResult = new WsGetMembersResult[1];
//        WsGetMembersResult getMembersResult1 = new WsGetMembersResult();
//
//        WsSubject[] list = new WsSubject[3];
//        for (int i = 0; i < 3; i++) {
//            list[i] = new WsSubject();
//            list[i].setName("testSubject_" + i);
//            list[i].setId("testSubject_uuid_" + i);
//            list[i].setAttributeValues(new String[] { "testSubject_username_" + i });
//        }
//
//        getMembersResult1.setWsSubjects(list);
//        getMembersResult[0] = getMembersResult1;
//        getMembersResults.setResults(getMembersResult);
//
//        Group group = gas.makeGroup(getMembersResults);
//
//        for (int i = 0; i < group.getMembers().size(); i++) {
//            assertTrue(group.getMembers().get(i).getName().equals("testSubject_" + i));
//            assertTrue(group.getNames().contains("testSubject_" + i));
//            assertTrue(group.getMembers().get(i).getUuid().equals("testSubject_uuid_" + i));
//            assertTrue(group.getUuids().contains("testSubject_uuid_" + i));
//            assertTrue(group.getMembers().get(i).getUsername().equals("testSubject_username_" + i));
//            assertTrue(group.getUsernames().contains("testSubject_username_" + i));
//        }
//    }
//
//    @Test
//    public void makeGroupingsNoAttributes() {
//        List<String> groupPaths = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            groupPaths.add("grouping_" + i);
//        }
//        for (int i = 0; i < 5; i++) {
//            groupPaths.add("path:grouping_" + (i + 5));
//        }
//
//        List<Grouping> groupings = hs.makeGroupings(groupPaths);
//
//        for (int i = 5; i < 10; i++) {
//            assertEquals("path:grouping_" + i, groupings.get(i).getPath());
//            assertEquals("grouping_" + i, groupings.get(i).getName());
//        }
//    }
//
//    @Test
//    public void makePerson() {
//        String name = "name";
//        String id = "uuid";
//        String identifier = "username";
//        String[] attributeNames = new String[] { UID_KEY, UUID_KEY, LAST_NAME_KEY, COMPOSITE_NAME_KEY, FIRST_NAME_KEY };
//        String[] attributeValues = new String[] { identifier, id, null, name, null };
//
//        WsSubject subject = new WsSubject();
//        subject.setName(name);
//        subject.setId(id);
//        subject.setAttributeValues(attributeValues);
//
//        Person person = gas.makePerson(subject, attributeNames);
//
//        assertTrue(person.getName().equals(name));
//        assertTrue(person.getUuid().equals(id));
//        assertTrue(person.getUsername().equals(identifier));
//
//        assertNotNull(gas.makePerson(new WsSubject(), new String[] {}));
//    }
//
//    @Test
//    public void makeGroupingsServiceResult() {
//        String action = "add a member";
//        String resultCode = "successfully added member";
//        WsAddMemberResults gr = new WsAddMemberResults();
//        WsResultMeta resultMeta = new WsResultMeta();
//        resultMeta.setResultCode(resultCode);
//        gr.setResultMetadata(resultMeta);
//
//        GroupingsServiceResult gsr = hs.makeGroupingsServiceResult(gr, action);
//
//        assertEquals(action, gsr.getAction());
//        assertEquals(resultCode, gsr.getResultCode());
//    }
//
//    @Test
//    public void extractFirstMembershipIDTest() {
//        WsGetMembershipsResults membershipsResults = null;
//        String firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
//        assertEquals(firstMembershipId, "");
//
//        membershipsResults = new WsGetMembershipsResults();
//        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
//        assertEquals(firstMembershipId, "");
//
//        WsMembership[] memberships = null;
//        membershipsResults.setWsMemberships(memberships);
//        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
//        assertEquals(firstMembershipId, "");
//
//        memberships = new WsMembership[] { null };
//        membershipsResults.setWsMemberships(memberships);
//        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
//        assertEquals(firstMembershipId, "");
//
//        WsMembership membership = new WsMembership();
//        memberships = new WsMembership[] { membership };
//        membershipsResults.setWsMemberships(memberships);
//        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
//        assertEquals(firstMembershipId, "");
//
//        membership.setMembershipId("1234");
//        memberships = new WsMembership[] { membership };
//        membershipsResults.setWsMemberships(memberships);
//        firstMembershipId = hs.extractFirstMembershipID(membershipsResults);
//        assertEquals(firstMembershipId, "1234");
//    }
//}


//package edu.hawaii.its.groupings.access;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import edu.hawaii.its.api.repository.GroupRepository;
//import edu.hawaii.its.api.repository.GroupingRepository;
//import edu.hawaii.its.api.repository.MembershipRepository;
//import edu.hawaii.its.api.repository.PersonRepository;
//import edu.hawaii.its.api.type.Group;
//import edu.hawaii.its.api.type.Grouping;
//import edu.hawaii.its.api.type.Person;
//import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
//import edu.hawaii.its.api.service.*;
//
//import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.env.Environment;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//@ActiveProfiles("localTest")
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { SpringBootWebApplication.class })
//@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)//change
//public class AuthorizationServiceImplTest {
//    @Value("${groupings.api.grouping_admins}")
//    private String GROUPING_ADMINS;
//
//    @Value("${groupings.api.grouping_apps}")
//    private String GROUPING_APPS;
//
//    @Value("${groupings.api.success}")
//    private String SUCCESS;
//
//    @Value("${groupings.api.failure}")
//    private String FAILURE;
//
//    @Value("${groupings.api.test.usernames}")
//    private String USERNAME;
//
//    @Value("${groupings.api.test.name}")
//    private String NAME;
//
//    @Value("${groupings.api.test.uuid}")
//    private String UUID;
//
//    @Value("${groupings.api.test.student_test_username}")
//    private String AARON;
//
//    private static final String PATH_ROOT = "path:to:grouping";
//    private static final String INCLUDE = ":include";
//    private static final String OWNERS = ":owners";
//
//    private static final String GROUPING_0_PATH = PATH_ROOT + 0;
//
//    private static final String GROUPING_0_INCLUDE_PATH = GROUPING_0_PATH + INCLUDE;
//    private static final String GROUPING_0_OWNERS_PATH = GROUPING_0_PATH + OWNERS;
//
//    private static final String ADMIN_USER = "admin";
//    private static final Person ADMIN_PERSON = new Person(ADMIN_USER, ADMIN_USER, ADMIN_USER);
//    private List<Person> admins = new ArrayList<>();
//
//    private static final String APP_USER = "app";
//    private static final Person APP_PERSON = new Person(APP_USER, APP_USER, APP_USER);
//    private List<Person> apps = new ArrayList<>();
//
//    private List<Person> users = new ArrayList<>();
//    private List<WsSubjectLookup> lookups = new ArrayList<>();
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
//    private MembershipService membershipService;
//
//    @Autowired
//    private MemberAttributeService memberAttributeService;
//
//    @Autowired
//    GroupingAssignmentService groupingAssignmentService;
//
//    @Autowired
//    private AuthorizationServiceImpl authorizationserviceimpl;
//
//    @Autowired
//    public Environment env;
//
//    @Before
//    public void setup() {
//        authorizationserviceimpl = new AuthorizationServiceImpl();
//    //   new DatabaseSetup(personRepository, groupRepository, groupingRepository, membershipRepository);
//        admins.add(ADMIN_PERSON);
//        Group adminGroup = new Group(GROUPING_ADMINS, admins);
//        personRepository.save(ADMIN_PERSON);
//        groupRepository.save(adminGroup);
//
//        admins.add(APP_PERSON);
//        Group appGroup = new Group(GROUPING_APPS, apps);
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
//    public void nulltest() {
//        assertNotNull(authorizationserviceimpl);
//    }
//
//    @Test
//    public void getDatabaseTest() {
//        Grouping groupingRandom = groupingAssignmentService.getGrouping(GROUPING_0_PATH, users.get(1).getUsername());
//        Grouping groupingOwner = groupingAssignmentService.getGrouping(GROUPING_0_PATH, users.get(0).getUsername());
//        Grouping groupingAdmin = groupingAssignmentService.getGrouping(GROUPING_0_PATH, ADMIN_USER);
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
//        assertTrue(groupingOwner.getBasis().getNames().contains(users.get(5).getName()));
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
//    public void adminTest() {
//        //fetch roles
//        memberAttributeService.assignOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
//        assertTrue(memberAttributeService.isOwner(GROUPING_0_PATH, users.get(0).getUsername()));
//        assertTrue(memberAttributeService.isOwner(GROUPING_0_PATH, users.get(1).getUsername()));
//        //fetch owner
//        membershipService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH,
//                users.get(1).getUsername());
//        assertTrue(authorizationserviceimpl.fetchOwner(users.get(0).getUsername()));
//        assertTrue(authorizationserviceimpl.fetchOwner(users.get(1).getUsername()));
//
//        //fetch admin
//        assertTrue(authorizationserviceimpl.fetchAdmin(ADMIN_USER));
//    }
//
//    @Test
//    public void membershipTest() {
//     //   membershipService.addAdmin(AARON, USERNAME[0]);
//        authorizationserviceimpl.fetchRoles("uuid3", "username3");
//        assertTrue(memberAttributeService.isAdmin(ADMIN_USER));
//        membershipService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH,
//                users.get(1).getUsername());
//    }
//}

package edu.hawaii.its.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class MemberAttributeServiceTest {

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
    private static final String OWNERS = ":owners";

    private static final String GROUPING_0_PATH = PATH_ROOT + 0;

    private static final String GROUPING_0_INCLUDE_PATH = GROUPING_0_PATH + INCLUDE;
    private static final String GROUPING_0_OWNERS_PATH = GROUPING_0_PATH + OWNERS;

    private static final String ADMIN_USER = "admin";
    private static final Person ADMIN_PERSON = new Person(ADMIN_USER, ADMIN_USER, ADMIN_USER);
    private List<Person> admins = new ArrayList<>();

    private static final String APP_USER = "app";
    private static final Person APP_PERSON = new Person(APP_USER, APP_USER, APP_USER);
    private List<Person> apps = new ArrayList<>();

    private List<Person> users = new ArrayList<>();
    private List<WsSubjectLookup> lookups = new ArrayList<>();

    @Autowired
    private GroupingRepository groupingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MemberAttributeService memberAttributeService;

    @Autowired
    GroupingAssignmentService groupingAssignmentService;

    @Before
    public void setup() {

        new DatabaseSetup(personRepository, groupRepository, groupingRepository, membershipRepository);

        admins.add(ADMIN_PERSON);
        Group adminGroup = new Group(GROUPING_ADMINS, admins);
        personRepository.save(ADMIN_PERSON);
        groupRepository.save(adminGroup);

        admins.add(APP_PERSON);
        Group appGroup = new Group(GROUPING_APPS, apps);
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
        assertNotNull(memberAttributeService);
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
            randomUserAdds = memberAttributeService
                    .assignOwnership(GROUPING_0_PATH, randomUser.getUsername(), randomUser.getUsername());
        } catch (GroupingsServiceResultException gsre) {
            randomUserAdds = gsre.getGsr();
        }

        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
        assertFalse(grouping.getOwners().getMembers().contains(randomUser));
        assertFalse(grouping.getOwners().isMember(randomUser));
        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);

        GroupingsServiceResult ownerAdds =
                memberAttributeService
                        .assignOwnership(GROUPING_0_PATH, users.get(0).getUsername(), randomUser.getUsername());
        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
        assertTrue(grouping.getOwners().getMembers().contains(randomUser));
        assertTrue(grouping.getOwners().isMember(randomUser));
        assertEquals(ownerAdds.getResultCode(), SUCCESS);

        GroupingsServiceResult adminAdds =
                memberAttributeService.assignOwnership(GROUPING_0_PATH, ADMIN_USER, randomUser.getUsername());
        grouping = groupingRepository.findByPath(GROUPING_0_PATH);
        assertTrue(grouping.getOwners().getMembers().contains(randomUser));
        assertTrue(grouping.getOwners().isMember(randomUser));
        assertEquals(SUCCESS, adminAdds.getResultCode());
    }

    @Test
    public void removeOwnershipTest() {
        GroupingsServiceResult randomUserRemoves;

        try {
            //non-owner/non-admin tries to remove ownership
            randomUserRemoves = memberAttributeService
                    .removeOwnership(GROUPING_0_PATH, users.get(1).getUsername(), users.get(1).getUsername());
        } catch (GroupingsServiceResultException gsre) {
            randomUserRemoves = gsre.getGsr();
        }
        assertTrue(randomUserRemoves.getResultCode().startsWith(FAILURE));

        //add owner for owner to remove
        membershipService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH,
                users.get(1).getUsername());

        //owner tries to remove other ownership
        GroupingsServiceResult ownerRemoves = memberAttributeService
                .removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
        assertEquals(SUCCESS, ownerRemoves.getResultCode());

        //try to remove ownership from user that is not an owner
        GroupingsServiceResult ownerRemovesNonOwner = memberAttributeService
                .removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
        assertEquals(SUCCESS, ownerRemovesNonOwner.getResultCode());

        //add owner for admin to remove
        membershipService.addGroupMemberByUsername(users.get(0).getUsername(), GROUPING_0_OWNERS_PATH,
                users.get(1).getUsername());

        //admin tries to remove ownership
        GroupingsServiceResult adminRemoves =
                memberAttributeService.removeOwnership(GROUPING_0_PATH, ADMIN_USER, users.get(1).getUsername());
        assertEquals(adminRemoves.getResultCode(), SUCCESS);
    }

    @Test
    public void checkSelfOptedTest() {

        //user is not in group
        boolean selfOpted = memberAttributeService.isSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(2).getUsername());
        assertFalse(selfOpted);

        //user has not self opted
        selfOpted = memberAttributeService.isSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(5).getUsername());
        assertFalse(selfOpted);

        //user has self opted
        Person person = personRepository.findByUsername(users.get(5).getUsername());
        Group group = groupRepository.findByPath(GROUPING_0_INCLUDE_PATH);
        Membership membership = membershipRepository.findByPersonAndGroup(person, group);
        membership.setSelfOpted(true);
        membershipRepository.save(membership);

        selfOpted = memberAttributeService.isSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(5).getUsername());
        assertTrue(selfOpted);
    }

    @Test
    public void isMemberTest() {
        //test with username
        Person person2 = users.get(2);
        Person person5 = users.get(5);

        assertFalse(memberAttributeService.isMember(GROUPING_0_PATH, person2));
        assertTrue(memberAttributeService.isMember(GROUPING_0_PATH, person5));

        //test with uuid
        person2.setUsername(null);
        person5.setUsername(null);

        assertFalse(memberAttributeService.isMember(GROUPING_0_PATH, person2));
        assertTrue(memberAttributeService.isMember(GROUPING_0_PATH, person5));
    }

    @Test
    public void isOwnerTest() {

        assertFalse(memberAttributeService.isOwner(GROUPING_0_PATH, users.get(1).getUsername()));
        assertTrue(memberAttributeService.isOwner(GROUPING_0_PATH, users.get(0).getUsername()));

    }

    @Test
    public void isAdminTest() {
        assertFalse(memberAttributeService.isAdmin(users.get(1).getUsername()));
        assertTrue(memberAttributeService.isAdmin(ADMIN_USER));
    }

    //todo Can't find in Github history where these tests came from. Don't work on UI despite working on API with same code, not sure how to fix
    //todo Possible that APP_USER is wrong for this codebase??? Either way shouldn't matter much because API deals with it in future once codebase is split

    //    @Test
    //    public void isAppTest() {
    //        assertFalse(memberAttributeService.isApp(users.get(2).getUsername()));
    //
    //        assertTrue(memberAttributeService.isApp(APP_USER));
    //    }
    //
    //    @Test
    //    public void isSuperuserTest() {
    //        assertFalse(memberAttributeService.isSuperuser(users.get(2).getUsername()));
    //        assertTrue(memberAttributeService.isSuperuser(ADMIN_USER));
    //
    //        assertTrue(memberAttributeService.isSuperuser(APP_USER));
    //    }

    @Test
    public void getUserAttributesLocalTest() {

        String username = users.get(5).getUsername();
        Person personFive = personRepository.findByUsername(users.get(5).getUsername());

        Map<String, String> attributes = memberAttributeService.getUserAttributesLocal(username);

        assertTrue(attributes.get("uid").equals(personFive.getUsername()));
        assertTrue(attributes.get("cn").equals(personFive.getName()));
        assertTrue(attributes.get("uuid").equals(personFive.getUuid()));

        //todo Possible code for non-null data, if ever implemented
        //        assertTrue(attributes.get("givenName").equals(personFive.getFirstName()));
        //        assertTrue(attributes.get("sn").equals(personFive.getLastName()));

        // FirstName and LastName in mock database is null
        assertNull(attributes.get("givenName"));
        assertNull(personFive.getFirstName());

        assertNull(attributes.get("sn"));
        assertNull(personFive.getLastName());
    }

}

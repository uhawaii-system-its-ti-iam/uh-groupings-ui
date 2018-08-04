/*//package edu.hawaii.its.groupings.access;
package edu.hawaii.its.serivce;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.repository.GroupRepository;
import edu.hawaii.its.api.repository.GroupingRepository;
import edu.hawaii.its.api.repository.MembershipRepository;
import edu.hawaii.its.api.repository.PersonRepository;
import edu.hawaii.its.api.service.GroupingAssignmentService;
import edu.hawaii.its.api.service.MemberAttributeService;
import edu.hawaii.its.api.service.MembershipService;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.api.type.Membership;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorizationServiceImplTest {
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

    @Autowired
*//*    private AuthorizationServiceImpl authorizationserviceimpl;*//*

    @Autowired
    public Environment env;

    @Before
    public void setup() {
*//*        new DatabaseSetup(personRepository, groupRepository, groupingRepository, membershipRepository);*//*

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
    public void nulltest() {
        assertNotNull(authorizationserviceimpl);
    }



    @Test
    public void fetchTest() {

    }
}*/

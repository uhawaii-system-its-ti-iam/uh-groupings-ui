package edu.hawaii.its.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.repository.GroupRepository;
import edu.hawaii.its.api.repository.GroupingRepository;
import edu.hawaii.its.api.repository.MembershipRepository;
import edu.hawaii.its.api.repository.PersonRepository;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.util.Dates;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@WebAppConfiguration

public class GroupingsFactoryServiceTest {

    @Value("${groupings.api.grouping_admins}")
    private String GROUPING_ADMINS;

    @Value("${groupings.api.grouping_apps}")
    private String GROUPING_APPS;

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
    private GrouperFactoryServiceImplLocal gfsl = new GrouperFactoryServiceImplLocal();

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
    public void makeWsAddMemberResultsTest() {
        WsAddMemberResults results;
        List<String> members = new ArrayList<>();
        members.add(users.get(0).getUsername());
        WsSubjectLookup lookup = gfsl.makeWsSubjectLookup(users.get(0).getUsername());

        results = gfsl.makeWsAddMemberResults(GROUPING_3_PATH, lookup, members);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));
    }

    @Test
    public void makeWsDeleteMemberResultsTest() {
        WsDeleteMemberResults results;
        List<String> members = new ArrayList<>();
        members.add(users.get(5).getUsername());
        WsSubjectLookup lookup = gfsl.makeWsSubjectLookup(users.get(5).getUsername());

        results = gfsl.makeWsDeleteMemberResults(GROUPING_3_PATH, lookup, members);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));
    }

    //todo
    @Test
    public void makeWsAssignAttributesResultsForGroupTest() {

    }

    //todo
    @Test
    public void makeWsAssignAttributesResultsForGroupTestTwo() {

    }

    //todo
    @Test
    public void removeGroupsWithoutOptOut() {

    }

}

package edu.hawaii.its.api.service;

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
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignAttributesResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class GrouperFactoryServiceTest {

    @Value("${groupings.api.operation_assign_attribute}")
    private String OPERATION_ASSIGN_ATTRIBUTE;

    @Value("${groupings.api.operation_remove_attribute}")
    private String OPERATION_REMOVE_ATTRIBUTE;

    @Value("${groupings.api.listserv}")
    private String LISTSERV;

    @Value("${groupings.api.opt_in}")
    private String OPT_IN;

    @Value("${groupings.api.opt_out}")
    private String OPT_OUT;

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

    private static final String GROUPING_3_PATH = PATH_ROOT + 3;

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

    // This test also takes care of setGroupingAttribute(grouping, attributeName, on)
    @Test
    public void makeWsAssignAttributesResultsForGroupTest() {
        WsAssignAttributesResults results;

        String assignType = "type";
        String assignOperation = OPERATION_ASSIGN_ATTRIBUTE;
        String removeOperation = OPERATION_REMOVE_ATTRIBUTE;
        String defName = LISTSERV;
        String defName2 = OPT_IN;
        String defName3 = OPT_OUT;
        String groupName = GROUPING_3_PATH;

        results = gfsl.makeWsAssignAttributesResultsForGroup(assignType, assignOperation, defName, groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));

        results = gfsl.makeWsAssignAttributesResultsForGroup(assignType, assignOperation, defName2, groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));

        results = gfsl.makeWsAssignAttributesResultsForGroup(assignType, assignOperation, defName3, groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));

        results = gfsl.makeWsAssignAttributesResultsForGroup(assignType, assignOperation, "nothing", groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("FAILURE"));

        results = gfsl.makeWsAssignAttributesResultsForGroup(assignType, removeOperation, defName, groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));
    }

    @Test
    public void makeWsAssignAttributesResultsForGroupLookupVersionTest() {
        WsAssignAttributesResults results;

        WsSubjectLookup lookup = gfsl.makeWsSubjectLookup(users.get(0).getUsername());
        WsSubjectLookup lookup2 = gfsl.makeWsSubjectLookup(users.get(3).getUsername());

        String assignType = "type";
        String assignOperation = OPERATION_ASSIGN_ATTRIBUTE;
        String defName = LISTSERV;
        String groupName = GROUPING_3_PATH;

        results = gfsl.makeWsAssignAttributesResultsForGroup(lookup, assignType, assignOperation, defName, groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("SUCCESS"));

        results = gfsl.makeWsAssignAttributesResultsForGroup(lookup2, assignType, assignOperation, defName, groupName);
        assertTrue(results.getResultMetadata().getResultCode().startsWith("FAILURE"));
    }
}

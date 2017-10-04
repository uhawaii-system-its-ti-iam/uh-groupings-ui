package edu.hawaii.its.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import edu.hawaii.its.api.type.*;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
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
    private static final String GROUPING_1_PATH = PATH_ROOT + 0;
    private static final String GROUPING_2_PATH = PATH_ROOT + 0;
    private static final String GROUPING_3_PATH = PATH_ROOT + 0;
    private static final String GROUPING_4_PATH = PATH_ROOT + 0;

    private static final String GROUPING_0_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_0_EXCLUDE_PATH =  GROUPING_0_PATH +":exclude";
    private static final String GROUPING_0_BASIS_PATH = GROUPING_0_PATH + ":basis";
    private static final String GROUPING_0_OWNERS_PATH = GROUPING_0_PATH + ":owners";
    
    private static final String GROUPING_1_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_1_EXCLUDE_PATH =  GROUPING_0_PATH +":exclude";
    private static final String GROUPING_1_BASIS_PATH = GROUPING_0_PATH + ":basis";
    private static final String GROUPING_1_OWNERS_PATH = GROUPING_0_PATH + ":owners";
    
    private static final String GROUPING_2_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_2_EXCLUDE_PATH =  GROUPING_0_PATH +":exclude";
    private static final String GROUPING_2_BASIS_PATH = GROUPING_0_PATH + ":basis";
    private static final String GROUPING_2_OWNERS_PATH = GROUPING_0_PATH + ":owners";
    
    private static final String GROUPING_3_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_3_EXCLUDE_PATH =  GROUPING_0_PATH +":exclude";
    private static final String GROUPING_3_BASIS_PATH = GROUPING_0_PATH + ":basis";
    private static final String GROUPING_3_OWNERS_PATH = GROUPING_0_PATH + ":owners";
    
    private static final String GROUPING_4_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_4_EXCLUDE_PATH =  GROUPING_0_PATH +":exclude";
    private static final String GROUPING_4_BASIS_PATH = GROUPING_0_PATH + ":basis";
    private static final String GROUPING_4_OWNERS_PATH = GROUPING_0_PATH + ":owners";
    
    
    private static final String NO_ATTRIBUTE_GROUPING_PATH = GROUPING_0_PATH;
    private static final String NO_ATTRIBUTE_GROUPING_OWNERS_PATH = GROUPING_0_OWNERS_PATH;

    private static final String ADMIN_USER = "admin";

    private static final WsSubjectLookup ADMIN_LOOKUP = new WsSubjectLookup(null, null, ADMIN_USER);
    private final WsSubjectLookup EVERY_ENTITY_LOOKUP = new WsSubjectLookup(null, null, EVERY_ENTITY);

    private static final Person ADMIN_PERSON = new Person(ADMIN_USER, ADMIN_USER, ADMIN_USER);

    private List<Person> admins = new ArrayList<>();

    private Group adminGroup;

    private DatabaseFactory databaseFactory;

    private List<Person> users = new ArrayList<>();
    private List<WsSubjectLookup> lookups = new ArrayList<>();
    
    @Mock
    private GrouperFactoryService gf;

    @InjectMocks
    @Autowired
    private GroupingsService groupingsService;

    @Autowired
    private GroupingRepository groupingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        databaseFactory = new DatabaseFactory(personRepository, groupRepository, groupingRepository);

        admins.add(ADMIN_PERSON);
        adminGroup = new Group(GROUPING_ADMINS, admins);

        personRepository.save(ADMIN_PERSON);
        groupRepository.save(adminGroup);

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
        assertNotNull(groupingsService);
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

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsSubjectLookup(users.get(0).getUsername())).willReturn(lookups.get(0));
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);

        given(gf.makeWsAddMemberResults(GROUPING_0_OWNERS_PATH, lookups.get(1), users.get(1).getUsername())).willReturn(addMemberResultsSuccess());
        given(gf.makeWsAddMemberResults(GROUPING_0_OWNERS_PATH, lookups.get(0), users.get(1).getUsername())).willReturn(addMemberResultsSuccess());
        given(gf.makeWsAddMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_LOOKUP, users.get(1).getUsername())).willReturn(addMemberResultsSuccess());

        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(0).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        GroupingsServiceResult randomUserAdds = groupingsService.assignOwnership(GROUPING_0_PATH, users.get(1).getUsername(), users.get(1).getUsername());
        GroupingsServiceResult ownerAdds = groupingsService.assignOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
        GroupingsServiceResult adminAdds = groupingsService.assignOwnership(GROUPING_0_PATH, ADMIN_USER, users.get(1).getUsername());

        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);
        assertEquals(ownerAdds.getResultCode(), SUCCESS);
        assertEquals(adminAdds.getResultCode(), SUCCESS);
    }

    @Test
    public void changeListservStatusTest() {
        List<String> attributes = new ArrayList<>();
        attributes.add(LISTSERV);

        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(0).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, LISTSERV, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, LISTSERV, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(null));

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, LISTSERV, GROUPING_0_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));
        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_REMOVE_ATTRIBUTE, LISTSERV, GROUPING_0_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, LISTSERV, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));
        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_REMOVE_ATTRIBUTE, LISTSERV, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        GroupingsServiceResult turnOnWhenOnRandom = groupingsService.changeListservStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
        GroupingsServiceResult turnOnWhenOnOwner = groupingsService.changeListservStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);
        GroupingsServiceResult turnOnWhenOnAdmin = groupingsService.changeListservStatus(GROUPING_0_PATH, ADMIN_USER, true);

        GroupingsServiceResult turnOffWhenOnRandom = groupingsService.changeListservStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
        GroupingsServiceResult turnOffWhenOnOwner = groupingsService.changeListservStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);
        GroupingsServiceResult turnOffWhenOnAdmin = groupingsService.changeListservStatus(GROUPING_0_PATH, ADMIN_USER, false);

        GroupingsServiceResult turnOnWhenOffRandom = groupingsService.changeListservStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(1).getUsername(), true);
        GroupingsServiceResult turnOnWhenOffOwner = groupingsService.changeListservStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(0).getUsername(), true);
        GroupingsServiceResult turnOnWhenOffAdmin = groupingsService.changeListservStatus(NO_ATTRIBUTE_GROUPING_PATH, ADMIN_USER, true);

        GroupingsServiceResult turnOffWhenOffRandom = groupingsService.changeListservStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(1).getUsername(), false);
        GroupingsServiceResult turnOffWhenOffOwner = groupingsService.changeListservStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(0).getUsername(), false);
        GroupingsServiceResult turnOffWhenOffAdmin = groupingsService.changeListservStatus(NO_ATTRIBUTE_GROUPING_PATH, ADMIN_USER, false);

        assertTrue(turnOnWhenOnRandom.getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOnOwner.getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOnRandom.getResultCode().startsWith(FAILURE));
        assertEquals(turnOffWhenOnOwner.getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.getResultCode(), SUCCESS);

        assertTrue(turnOnWhenOffRandom.getResultCode().startsWith(FAILURE));
        assertEquals(turnOnWhenOffOwner.getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.getResultCode(), SUCCESS);

        assertTrue(turnOffWhenOffRandom.getResultCode().startsWith(FAILURE));
        assertTrue(turnOffWhenOffOwner.getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.getResultCode().startsWith(SUCCESS));
    }

    @Test
    public void changeOptInStatusTest() {
        List<String> attributes = new ArrayList<>();
        attributes.add(OPT_IN);

        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(0).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_IN, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_IN, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(null));

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, OPT_IN, GROUPING_0_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));
        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_REMOVE_ATTRIBUTE, OPT_IN, GROUPING_0_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, OPT_IN, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));
        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_REMOVE_ATTRIBUTE, OPT_IN, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsSubjectLookup(users.get(0).getUsername())).willReturn(lookups.get(0));
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);
        given(gf.makeWsSubjectLookup(EVERY_ENTITY)).willReturn(EVERY_ENTITY_LOOKUP);

        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + INCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + INCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + EXCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + EXCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));

        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + INCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + INCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + EXCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + EXCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));

        List<GroupingsServiceResult> turnOnWhenOnRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOnOwner = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOnAdmin = groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, true);

        List<GroupingsServiceResult> turnOffWhenOnRandom = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOnOwner = groupingsService.changeOptInStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOnAdmin = groupingsService.changeOptInStatus(GROUPING_0_PATH, ADMIN_USER, false);

        List<GroupingsServiceResult> turnOnWhenOffRandom = groupingsService.changeOptInStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(1).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOffOwner = groupingsService.changeOptInStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(0).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOffAdmin = groupingsService.changeOptInStatus(NO_ATTRIBUTE_GROUPING_PATH, ADMIN_USER, true);

        List<GroupingsServiceResult> turnOffWhenOffRandom = groupingsService.changeOptInStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(1).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOffOwner = groupingsService.changeOptInStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(0).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOffAdmin = groupingsService.changeOptInStatus(NO_ATTRIBUTE_GROUPING_PATH, ADMIN_USER, false);

        assertTrue(turnOnWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOnOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(2).getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(turnOffWhenOnOwner.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnOwner.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnOwner.get(2).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.get(2).getResultCode(), SUCCESS);

        assertTrue(turnOnWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(turnOnWhenOffOwner.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffOwner.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffOwner.get(2).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.get(2).getResultCode(), SUCCESS);

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
        List<String> attributes = new ArrayList<>();
        attributes.add(OPT_OUT);

        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(NO_ATTRIBUTE_GROUPING_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(0).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_OUT, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_OUT, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(null));

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, OPT_OUT, GROUPING_0_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));
        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_REMOVE_ATTRIBUTE, OPT_OUT, GROUPING_0_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, OPT_OUT, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));
        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_REMOVE_ATTRIBUTE, OPT_OUT, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsSubjectLookup(users.get(0).getUsername())).willReturn(lookups.get(0));
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);
        given(gf.makeWsSubjectLookup(EVERY_ENTITY)).willReturn(EVERY_ENTITY_LOOKUP);

        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + INCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + INCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + EXCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(GROUPING_0_PATH + EXCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));

        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + INCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + INCLUDE, PRIVILEGE_OPT_OUT, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + EXCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, true))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));
        given(gf.makeWsAssignGrouperPrivilegesLiteResult(NO_ATTRIBUTE_GROUPING_PATH + EXCLUDE, PRIVILEGE_OPT_IN, EVERY_ENTITY_LOOKUP, false))
                .willReturn(makeWsAssignGrouperPrivilegesLiteResult(SUCCESS));

        List<GroupingsServiceResult> turnOnWhenOnRandom = groupingsService.changeOptOutStatus(GROUPING_0_PATH, users.get(1).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOnOwner = groupingsService.changeOptOutStatus(GROUPING_0_PATH, users.get(0).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOnAdmin = groupingsService.changeOptOutStatus(GROUPING_0_PATH, ADMIN_USER, true);

        List<GroupingsServiceResult> turnOffWhenOnRandom = groupingsService.changeOptOutStatus(GROUPING_0_PATH, users.get(1).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOnOwner = groupingsService.changeOptOutStatus(GROUPING_0_PATH, users.get(0).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOnAdmin = groupingsService.changeOptOutStatus(GROUPING_0_PATH, ADMIN_USER, false);

        List<GroupingsServiceResult> turnOnWhenOffRandom = groupingsService.changeOptOutStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(1).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOffOwner = groupingsService.changeOptOutStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(0).getUsername(), true);
        List<GroupingsServiceResult> turnOnWhenOffAdmin = groupingsService.changeOptOutStatus(NO_ATTRIBUTE_GROUPING_PATH, ADMIN_USER, true);

        List<GroupingsServiceResult> turnOffWhenOffRandom = groupingsService.changeOptOutStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(1).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOffOwner = groupingsService.changeOptOutStatus(NO_ATTRIBUTE_GROUPING_PATH, users.get(0).getUsername(), false);
        List<GroupingsServiceResult> turnOffWhenOffAdmin = groupingsService.changeOptOutStatus(NO_ATTRIBUTE_GROUPING_PATH, ADMIN_USER, false);

        assertTrue(turnOnWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOnWhenOnOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOnWhenOnAdmin.get(2).getResultCode().startsWith(SUCCESS));

        assertTrue(turnOffWhenOnRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(turnOffWhenOnOwner.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnOwner.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnOwner.get(2).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOffWhenOnAdmin.get(2).getResultCode(), SUCCESS);

        assertTrue(turnOnWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertEquals(turnOnWhenOffOwner.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffOwner.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffOwner.get(2).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.get(0).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.get(1).getResultCode(), SUCCESS);
        assertEquals(turnOnWhenOffAdmin.get(2).getResultCode(), SUCCESS);

        assertTrue(turnOffWhenOffRandom.get(0).getResultCode().startsWith(FAILURE));
        assertTrue(turnOffWhenOffOwner.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffOwner.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffOwner.get(2).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(turnOffWhenOffAdmin.get(2).getResultCode().startsWith(SUCCESS));
    }

//    @Test
    public void removeOwnershipTest() {
        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsSubjectLookup(users.get(0).getUsername())).willReturn(lookups.get(0));
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);

        given(gf.makeWsDeleteMemberResults(GROUPING_0_OWNERS_PATH, lookups.get(1), users.get(1).getUsername()))
                .willReturn(deleteMemberResultsSuccess(GROUPING_0_OWNERS_PATH, users.get(1).getUsername()));
        given(gf.makeWsDeleteMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername()))
                .willReturn(deleteMemberResultsSuccess(GROUPING_0_OWNERS_PATH, users.get(1).getUsername()));
        given(gf.makeWsDeleteMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_LOOKUP, users.get(1).getUsername()))
                .willReturn(deleteMemberResultsSuccess(GROUPING_0_OWNERS_PATH, users.get(1).getUsername()));

        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(0).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        GroupingsServiceResult randomUserAdds = groupingsService.removeOwnership(GROUPING_0_PATH, users.get(1).getUsername(), users.get(1).getUsername());
        GroupingsServiceResult ownerAdds = groupingsService.removeOwnership(GROUPING_0_PATH, users.get(0).getUsername(), users.get(1).getUsername());
        GroupingsServiceResult adminAdds = groupingsService.removeOwnership(GROUPING_0_PATH, ADMIN_USER, users.get(1).getUsername());

        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);
        assertEquals(ownerAdds.getResultCode(), SUCCESS);
        assertEquals(adminAdds.getResultCode(), SUCCESS);

    }

    @Test
    public void getGroupingTest() {
        List<String> attributes = new ArrayList<>();
        attributes.add(LISTSERV);
        attributes.add(OPT_IN);
        attributes.add(OPT_OUT);

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsSubjectLookup(users.get(0).getUsername())).willReturn(lookups.get(0));
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);

        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(0).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());


        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));


        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, NO_ATTRIBUTE_GROUPING_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(null));

        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(1), GROUPING_0_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(0), GROUPING_0_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, ADMIN_LOOKUP, GROUPING_0_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(1), GROUPING_0_OWNERS_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(0), GROUPING_0_OWNERS_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, ADMIN_LOOKUP, GROUPING_0_OWNERS_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(1), GROUPING_0_INCLUDE_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(0), GROUPING_0_INCLUDE_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, ADMIN_LOOKUP, GROUPING_0_INCLUDE_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(1), GROUPING_0_EXCLUDE_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(0), GROUPING_0_EXCLUDE_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, ADMIN_LOOKUP, GROUPING_0_EXCLUDE_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(1), GROUPING_0_BASIS_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, lookups.get(0), GROUPING_0_BASIS_PATH)).willReturn(makeWsGetMembersResults());
        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, ADMIN_LOOKUP, GROUPING_0_BASIS_PATH)).willReturn(makeWsGetMembersResults());

        Grouping groupingRandom = groupingsService.getGrouping(GROUPING_0_PATH, users.get(1).getUsername());
        Grouping groupingOwner = groupingsService.getGrouping(GROUPING_0_PATH, users.get(0).getUsername());
        Grouping groupingAdmin = groupingsService.getGrouping(GROUPING_0_PATH, ADMIN_USER);

        assertEquals(groupingRandom.getComposite().getMembers().size(), 0);
        assertEquals(groupingRandom.getInclude().getMembers().size(), 0);
        assertEquals(groupingRandom.getExclude().getMembers().size(), 0);
        assertEquals(groupingRandom.getBasis().getMembers().size(), 0);
        assertEquals(groupingRandom.getOwners().getMembers().size(), 0);
        assertTrue(groupingOwner.getComposite().getNames().contains("name0"));
        assertTrue(groupingOwner.getInclude().getNames().contains("name0"));
        assertTrue(groupingOwner.getExclude().getNames().contains("name0"));
        assertTrue(groupingOwner.getBasis().getNames().contains("name0"));
        assertTrue(groupingOwner.getOwners().getNames().contains("name0"));
        assertTrue(groupingAdmin.getComposite().getNames().contains("name0"));
        assertTrue(groupingAdmin.getInclude().getNames().contains("name0"));
        assertTrue(groupingAdmin.getExclude().getNames().contains("name0"));
        assertTrue(groupingAdmin.getBasis().getNames().contains("name0"));
        assertTrue(groupingAdmin.getOwners().getNames().contains("name0"));
    }

    @Test
    public void getMyGroupingsTest() {

        //todo
    }

    @Test
    public void optInTest() {

        //all

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));

        //opt in Permission for include group false
        given(gf.makeWsGetGrouperPrivilegesLiteResult(GROUPING_0_INCLUDE_PATH, PRIVILEGE_OPT_IN, lookups.get(1))).willReturn(makeWsGetGrouperPrivilegesLiteResult(false));

        List<GroupingsServiceResult> optInResults = groupingsService.optIn(users.get(1).getUsername(), GROUPING_0_PATH);

        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));

        //opt in Permission for include group true
        given(gf.makeWsGetGrouperPrivilegesLiteResult(GROUPING_0_INCLUDE_PATH, PRIVILEGE_OPT_IN, lookups.get(1))).willReturn(makeWsGetGrouperPrivilegesLiteResult(true));

        given(gf.makeWsDeleteMemberResults(GROUPING_0_EXCLUDE_PATH, lookups.get(1), users.get(1).getUsername()))
                .willReturn(deleteMemberResultsSuccess(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername()));

        given(gf.makeWsAddMemberResults(GROUPING_0_INCLUDE_PATH, lookups.get(1), users.get(1).getUsername())).willReturn(addMemberResultsSuccess());

        WsAttributeAssignValue dateTime = makeWsAttributeAssignValue();

        List<String> attributes = new ArrayList<>();

        given(gf.makeWsAttributeAssignValue(anyString())).willReturn(dateTime);

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_INCLUDE_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_EXCLUDE_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsHasMemberResults(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());

        //in include group not self opted
        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsGetMembershipsResults(GROUPING_0_INCLUDE_PATH, lookups.get(1))).willReturn(makeWsGetMembershipsResults());
        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, "1234"))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        optInResults = groupingsService.optIn(users.get(1).getUsername(), GROUPING_0_PATH);

        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

        //TODO make a mock database
        //not in include group
//        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
//
//        optInResults = groupingsService.optIn(users.get(1).getUsername(), GROUPING_0_PATH);
//
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));


    }

    @Test
    public void optOutTest() {

        //all

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));

        //opt in Permission for include group false
        given(gf.makeWsGetGrouperPrivilegesLiteResult(GROUPING_0_EXCLUDE_PATH, PRIVILEGE_OPT_IN, lookups.get(1))).willReturn(makeWsGetGrouperPrivilegesLiteResult(false));

        List<GroupingsServiceResult> optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_0_PATH);

        assertTrue(optInResults.get(0).getResultCode().startsWith(FAILURE));

        //opt in Permission for include group true
        given(gf.makeWsGetGrouperPrivilegesLiteResult(GROUPING_0_EXCLUDE_PATH, PRIVILEGE_OPT_IN, lookups.get(1))).willReturn(makeWsGetGrouperPrivilegesLiteResult(true));

        given(gf.makeWsDeleteMemberResults(GROUPING_0_INCLUDE_PATH, lookups.get(1), users.get(1).getUsername()))
                .willReturn(deleteMemberResultsSuccess(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername()));

        given(gf.makeWsAddMemberResults(GROUPING_0_EXCLUDE_PATH, lookups.get(1), users.get(1).getUsername())).willReturn(addMemberResultsSuccess());

        WsAttributeAssignValue dateTime = makeWsAttributeAssignValue();

        List<String> attributes = new ArrayList<>();

        given(gf.makeWsAttributeAssignValue(anyString())).willReturn(dateTime);

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_EXCLUDE_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_INCLUDE_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());

        //in include group not self opted
        given(gf.makeWsHasMemberResults(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsGetMembershipsResults(GROUPING_0_EXCLUDE_PATH, lookups.get(1))).willReturn(makeWsGetMembershipsResults());
        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, "1234"))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));

        optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_0_PATH);

        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

        //TODO make a mock database
        //not in include group
//        given(gf.makeWsHasMemberResults(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
//
//        optInResults = groupingsService.optOut(users.get(1).getUsername(), GROUPING_0_PATH);
//
//        assertTrue(optInResults.get(0).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(1).getResultCode().startsWith(SUCCESS));
//        assertTrue(optInResults.get(2).getResultCode().startsWith(SUCCESS));

    }

    @Test
    public void cancelOptInTest() {
        //not in group
        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        List<GroupingsServiceResult> cancelOptInResults = groupingsService.cancelOptIn(GROUPING_0_PATH, users.get(1).getUsername());
        assertTrue(cancelOptInResults.get(0).getResultCode().startsWith(SUCCESS));

        //in group but not self opted
        List<String> attributes = new ArrayList<>();

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsGetMembershipsResults(GROUPING_0_INCLUDE_PATH, lookups.get(1))).willReturn(makeWsGetMembershipsResults());
        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, "1234"))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));


        cancelOptInResults = groupingsService.cancelOptIn(GROUPING_0_PATH, users.get(1).getUsername());

        assertTrue(cancelOptInResults.get(0).getResultCode().startsWith(FAILURE));

        //in group and self opted
        attributes.add(SELF_OPTED);

        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsDeleteMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername()))
                .willReturn(deleteMemberResultsSuccess(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername()));

        WsAttributeAssignValue dateTime = makeWsAttributeAssignValue();

        given(gf.makeWsAttributeAssignValue(anyString())).willReturn(dateTime);

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_INCLUDE_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        cancelOptInResults = groupingsService.cancelOptIn(GROUPING_0_PATH, users.get(1).getUsername());

        assertTrue(cancelOptInResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(cancelOptInResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(cancelOptInResults.get(2).getResultCode().startsWith(SUCCESS));

    }

    @Test
    public void cancelOptOutTest() {
        //not in group
        given(gf.makeWsHasMemberResults(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        List<GroupingsServiceResult> cancelOptOutResults = groupingsService.cancelOptOut(GROUPING_0_PATH, users.get(1).getUsername());
        assertTrue(cancelOptOutResults.get(0).getResultCode().startsWith(SUCCESS));

        //in group but not self opted
        List<String> attributes = new ArrayList<>();

        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsHasMemberResults(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsGetMembershipsResults(GROUPING_0_EXCLUDE_PATH, lookups.get(1))).willReturn(makeWsGetMembershipsResults());
        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, "1234"))
                .willReturn(makeWsAssignAttributesResults(SUCCESS));


        cancelOptOutResults = groupingsService.cancelOptOut(GROUPING_0_PATH, users.get(1).getUsername());

        assertTrue(cancelOptOutResults.get(0).getResultCode().startsWith(FAILURE));

        //in group and self opted
        attributes.add(SELF_OPTED);

        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        given(gf.makeWsDeleteMemberResults(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername())).willReturn(deleteMemberResultsSuccess(GROUPING_0_EXCLUDE_PATH, users.get(1).getUsername()));

        WsAttributeAssignValue dateTime = makeWsAttributeAssignValue();

        given(gf.makeWsAttributeAssignValue(anyString())).willReturn(dateTime);

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        given(gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                GROUPING_0_EXCLUDE_PATH,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTime
        )).willReturn(makeWsAssignAttributesResults(SUCCESS));

        cancelOptOutResults = groupingsService.cancelOptOut(GROUPING_0_PATH, users.get(1).getUsername());

        assertTrue(cancelOptOutResults.get(0).getResultCode().startsWith(SUCCESS));
        assertTrue(cancelOptOutResults.get(1).getResultCode().startsWith(SUCCESS));
        assertTrue(cancelOptOutResults.get(2).getResultCode().startsWith(SUCCESS));

    }

    @Test
    public void optOutPermissionTest() {

        List<String> attributes = new ArrayList<>();

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_OUT, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        boolean permission = groupingsService.optOutPermission(GROUPING_0_PATH);

        assertEquals(permission, false);

        attributes.add(OPT_OUT);

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_OUT, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        permission = groupingsService.optOutPermission(GROUPING_0_PATH);

        assertEquals(permission, true);

    }

    @Test
    public void optInPermissionTest() {

        List<String> attributes = new ArrayList<>();

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_IN, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        boolean permission = groupingsService.optInPermission(GROUPING_0_PATH);

        assertEquals(permission, false);

        attributes.add(OPT_IN);

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, OPT_IN, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        permission = groupingsService.optInPermission(GROUPING_0_PATH);

        assertEquals(permission, true);
    }

    @Test
    public void groupHasAttributeTest() {
        List<String> attributes = new ArrayList<>();
        String attribute = "an attribute";

        //group does not have the attribute
        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, attribute, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        boolean hasAttribute = groupingsService.groupHasAttribute(GROUPING_0_PATH, attribute);

        assertFalse(hasAttribute);

        //group has the attribute
        attributes.add(attribute);

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, attribute, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        hasAttribute = groupingsService.groupHasAttribute(GROUPING_0_PATH, attribute);

        assertTrue(hasAttribute);

    }

    @Test
    public void groupingsInTest() {
        String username = "username2";
        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(username);
        List<String> groupPaths = new ArrayList<>();

        for (Group group : groupsIn) {
            groupPaths.add(group.getPath());
        }

        given(gf.makeWsGetAttributeAssignmentsResults(ASSIGN_TYPE_GROUP, TRIO, groupPaths))
                .willReturn(makeWsGetAttributeAssignmentsResultsForTrios(groupPaths));

        List<Grouping> groupingsIn = groupingsService.groupingsIn(groupPaths);

        for (int i = 0; i < groupingsIn.size(); i++) {
            assertTrue(groupingsIn.get(i).getPath().equals(PATH_ROOT + i));
        }
    }

    @Test
    public void hasListservTest() {

        List<String> attributes = new ArrayList<>();

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, LISTSERV, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        boolean groupingHasListserv = groupingsService.hasListserv(GROUPING_0_PATH);

        assertEquals(groupingHasListserv, false);

        attributes.add(LISTSERV);

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, LISTSERV, GROUPING_0_PATH))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        groupingHasListserv = groupingsService.hasListserv(GROUPING_0_PATH);

        assertEquals(groupingHasListserv, true);
    }

    @Test
    public void groupingsOwnedTest() {
        String ownerUsername = "username0";
        Iterable<Group> groupsIn = groupRepository.findByMembersUsername(ownerUsername);
        List<String> attributes = new ArrayList<>();
        List<String> groupPaths = new ArrayList<>();

        for (Group group : groupsIn) {
            groupPaths.add(group.getPath());
        }

        List<String> ownerGroups = groupPaths
                .stream()
                .filter(groupPath -> groupPath.endsWith(OWNERS))
                .map(groupPath -> groupPath.substring(0, groupPath.length() - OWNERS.length()))
                .collect(Collectors.toList());

        given(gf.makeWsGetAttributeAssignmentsResults(ASSIGN_TYPE_GROUP, TRIO, ownerGroups))
                .willReturn(makeWsGetAttributeAssignmentsResultsForTrios(groupPaths));

        for (String groups : ownerGroups) {
            given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, groups))
                    .willReturn(makeWsGetAttributeAssignmentsResults(attributes));
        }

        List<Grouping> groupingsOwned = groupingsService.groupingsOwned(groupPaths);

        for (int i = 0; i < groupingsOwned.size(); i++) {
            assertTrue(groupingsOwned.get(i).getPath().equals(PATH_ROOT + i));
        }
    }

    @Test
    public void groupingsOptedIntoTest() {
//todo
    }

    @Test
    public void groupingsOptedOutOfTest() {
//todo
    }

    @Test
    public void groupingsOptedTest() {
//todo
    }

    @Test
    public void adminListsTest() {
        List<String> groupingPaths = new ArrayList<>();
        List<String> attributes = new ArrayList<>();
        groupingPaths.add(PATH_ROOT + 0);
        groupingPaths.add(GROUPING_1_PATH);
        groupingPaths.add(PATH_ROOT + 2);
        groupingPaths.add(PATH_ROOT + 3);
        groupingPaths.add(PATH_ROOT + 4);

        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_APPS, ADMIN_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_APPS, users.get(1).getUsername())).willReturn(notMemberResults());

        given(gf.makeWsGetAttributeAssignmentsResults(ASSIGN_TYPE_GROUP, TRIO))
                .willReturn(makeWsGetAttributeAssignmentsResultsForTrios(groupingPaths));

        given(gf.makeWsGetMembersResults(SUBJECT_ATTRIBUTE_NAME_UID, ADMIN_LOOKUP, GROUPING_ADMINS))
                .willReturn(makeWsGetMembersResultsDB(GROUPING_ADMINS));

        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);

        for (String groupingPath : groupingPaths) {
            given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, groupingPath))
                    .willReturn(makeWsGetAttributeAssignmentsResults(attributes));
        }

        AdminListsHolder adminListsHolder = groupingsService.adminLists(ADMIN_USER);
        AdminListsHolder emptyAdminListHolder = groupingsService.adminLists(users.get(1).getUsername());


        assertEquals(adminListsHolder.getAllGroupings().size(), 5);
        assertEquals(adminListsHolder.getAdminGroup().getMembers().size(), 1);

        assertEquals(emptyAdminListHolder.getAllGroupings().size(), 0);
        assertEquals(emptyAdminListHolder.getAdminGroup().getMembers().size(), 0);
    }

    @Test
    public void groupingsToOptOutOfTest() {

//todo
    }

    @Test
    public void groupingsToOptIntoTest() {
//todo
    }

    @Test
    public void addSelfOptedTest() {
//todo
    }

    @Test
    public void checkSelfOptedTest() {

        //user is not in group
        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(notMemberResults());

        boolean selfOpted = groupingsService.checkSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername());
        assertFalse(selfOpted);

        //user has not self opted
        List<String> attributes = new ArrayList<>();

        given(gf.makeWsHasMemberResults(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername())).willReturn(isMemberResults());
        given(gf.makeWsSubjectLookup(users.get(1).getUsername())).willReturn(lookups.get(1));
        given(gf.makeWsGetMembershipsResults(GROUPING_0_INCLUDE_PATH, lookups.get(1))).willReturn(makeWsGetMembershipsResults());
        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        selfOpted = groupingsService.checkSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername());
        assertFalse(selfOpted);

        //user has self opted
        attributes.add(SELF_OPTED);
        given(gf.makeWsGetAttributeAssignmentsResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, "1234"))
                .willReturn(makeWsGetAttributeAssignmentsResults(attributes));

        selfOpted = groupingsService.checkSelfOpted(GROUPING_0_INCLUDE_PATH, users.get(1).getUsername());
        assertTrue(selfOpted);
    }

    @Test
    public void inGroupTest() {

        //user is not in group
        given(gf.makeWsHasMemberResults(GROUPING_0_PATH, users.get(1).getUsername())).willReturn(notMemberResults());

        assertFalse(groupingsService.inGroup(GROUPING_0_PATH, users.get(1).getUsername()));

        //user is in group
        given(gf.makeWsHasMemberResults(GROUPING_0_PATH, users.get(1).getUsername())).willReturn(isMemberResults());

        assertTrue(groupingsService.inGroup(GROUPING_0_PATH, users.get(1).getUsername()));
    }

    @Test
    public void isOwnerTest() {
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_0_OWNERS_PATH, users.get(0).getUsername())).willReturn(isMemberResults());

        assertFalse(groupingsService.isOwner(GROUPING_0_PATH, users.get(1).getUsername()));
        assertTrue(groupingsService.isOwner(GROUPING_0_PATH, users.get(0).getUsername()));

    }

    @Test
    public void isAdminTest() {
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, users.get(1).getUsername())).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        assertFalse(groupingsService.isAdmin(users.get(1).getUsername()));
        assertTrue(groupingsService.isAdmin(ADMIN_USER));
    }

    @Test
    public void removeSelfOptedTest() {

//todo
    }

    @Test
    public void groupOptOutPermissionTest() {

//todo
    }

    @Test
    public void groupOptInPermissionTest() {

//todo
    }

    @Test
    public void updateLastModifiedTest() {

//todo
    }

    @Test
    public void assignMembershipAttributesTest() {

//todo
    }

    @Test
    public void getMembershipAttributesTest() {

//todo
    }

    @Test
    public void assignGroupAttributesTest() {

//todo
    }

    @Test
    public void attributeAssignmentsResultsTest() {

//todo
    }

    @Test
    public void getGrouperPrivilegeTest() {

//todo
    }

    @Test
    public void assignGrouperPrivilegeTest() {

//todo
    }

    @Test
    public void membershipsResultsTest() {

//todo
    }

    //    @Test
    public void addMemberAsTest() {
        List<String> attributes = new ArrayList<>();

       given(gf.makeWsDeleteMemberResults(GROUPING_1_PATH + EXCLUDE, lookups.get(0), users.get(3).getUsername())).willReturn(deleteMemberResultsSuccess(GROUPING_1_PATH + EXCLUDE, users.get(3).getUsername()));
        given(gf.makeWsAddMemberResults(GROUPING_1_PATH + INCLUDE, lookups.get(0), users.get(3).getUsername())).willReturn(addMemberResultsSuccess());
//        given(gf.makeWsAssignAttributesResults(ASSIGN_TYPE_GROUP,
//                OPERATION_ASSIGN_ATTRIBUTE,
//                GROUPING_1_PATH, YYYYMMDDTHHMM,
//                OPERATION_REPLACE_VALUES,
//                any(WsAttributeAssignValue.class)))
//                .willReturn(makeWsAssignAttributesResults(SUCCESS));
//        given(gf.makeWsAssignAttributesResults(ASSIGN_TYPE_GROUP,
//                OPERATION_ASSIGN_ATTRIBUTE,
//                GROUPING_1_PATH + INCLUDE, YYYYMMDDTHHMM,
//                OPERATION_REPLACE_VALUES,
//                any(WsAttributeAssignValue.class)))
//                .willReturn(makeWsAssignAttributesResults(SUCCESS));


        Grouping grouping = groupingRepository.findByPath(GROUPING_1_PATH).get(0);
        assertFalse(grouping.getComposite().getMembers().contains(users.get(3)));

        groupingsService.addMemberAs(users.get(0).getUsername(), GROUPING_1_PATH + INCLUDE, users.get(3).getUsername());

        grouping = groupingRepository.findByPath(GROUPING_1_PATH).get(0);
        assertTrue(grouping.getComposite().getMembers().contains(users.get(3)));
    }

    @Test
    public void deleteMemberAsTest() {

//todo
    }

    @Test
    public void deleteMemberTest() {

//todo
    }

    @Test
    public void getMembersTest() {

//todo
    }

    @Test
    public void extractGroupingsTest() {

//todo
    }

    @Test
    public void getGroupPathsTest() {

//todo
    }

    @Test
    public void setGroupingAttributesTest() {

//todo
    }

    @Test
    public void parentGroupingPathTest() {

//todo
    }

    @Test
    public void extractGroupPathsTest() {

//todo
    }

    @Test
    public void changeGroupAttributeStatusTest() {

//todo
    }

    /////////////////////////////////////////////////////
    //factory methods
    //////////////////////////////////////////////////////////

    private WsHasMemberResults isMemberResults() {
        WsHasMemberResults isMemberResults = new WsHasMemberResults();
        WsHasMemberResult isMemberResult = new WsHasMemberResult();
        WsResultMeta isMemberMeta = new WsResultMeta();
        isMemberMeta.setResultCode(IS_MEMBER);
        isMemberResult.setResultMetadata(isMemberMeta);
        isMemberResults.setResults(new WsHasMemberResult[]{isMemberResult});

        return isMemberResults;
    }

    private WsHasMemberResults notMemberResults() {
        WsHasMemberResults notMemberResults = new WsHasMemberResults();
        WsHasMemberResult notMemberResult = new WsHasMemberResult();
        WsResultMeta notMemberMeta = new WsResultMeta();
        notMemberMeta.setResultCode("not member");
        notMemberResult.setResultMetadata(notMemberMeta);
        notMemberResults.setResults(new WsHasMemberResult[]{notMemberResult});

        return notMemberResults;
    }

    private WsAddMemberResults addMemberResultsSuccess() {
        WsAddMemberResults addMemberResults = new WsAddMemberResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(SUCCESS);
        addMemberResults.setResultMetadata(resultMeta);

        return addMemberResults;
    }

    private WsDeleteMemberResults deleteMemberResultsSuccess(String groupPath, String username) {
        WsDeleteMemberResults deleteMemberResults = new WsDeleteMemberResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(SUCCESS);
        deleteMemberResults.setResultMetadata(resultMeta);

        //todo actually delete mermber
//        Group group = groupRepository.findByPath(groupPath);

        return deleteMemberResults;
    }

    private WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(List<String> attributes) {
        WsGetAttributeAssignmentsResults getAttributeAssignmentsResults = new WsGetAttributeAssignmentsResults();
        List<WsAttributeDefName> attributeDefNames = new ArrayList<>();
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();
        if (attributes != null) {
            for (String attribute : attributes) {
                WsAttributeDefName attributeDefName = new WsAttributeDefName();
                attributeDefName.setName(attribute);
                attributeDefNames.add(attributeDefName);

                WsAttributeAssign attributeAssign = new WsAttributeAssign();
                attributeAssign.setAttributeDefNameName(attribute);
                attributeAssigns.add(attributeAssign);
            }
            getAttributeAssignmentsResults.setWsAttributeDefNames(attributeDefNames.toArray(new WsAttributeDefName[attributeDefNames.size()]));
            getAttributeAssignmentsResults.setWsAttributeAssigns(attributeAssigns.toArray(new WsAttributeAssign[attributeDefNames.size()]));

        }
        return getAttributeAssignmentsResults;

    }

    private WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForTrios(List<String> groupPaths) {
        WsGetAttributeAssignmentsResults getAttributeAssignmentsResults = new WsGetAttributeAssignmentsResults();
        List<WsAttributeDefName> attributeDefNames = new ArrayList<>();
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();
        List<String> groupingPaths = new ArrayList<>();
        List<String> ownedGroupingPaths = new ArrayList<>();

        groupingPaths.add(PATH_ROOT + 0);
        groupingPaths.add(GROUPING_1_PATH);
        groupingPaths.add(PATH_ROOT + 2);
        groupingPaths.add(PATH_ROOT + 3);
        groupingPaths.add(PATH_ROOT + 4);

        WsGroup[] groups = new WsGroup[groupPaths.size()];

        for (int i = 0; i < groupPaths.size(); i++) {
            if (groupingPaths.contains(groupPaths.get(i))) {
                ownedGroupingPaths.add(groupPaths.get(i));
                groups[i] = new WsGroup();
                groups[i].setName(groupPaths.get(i));
            }
        }

        for (String group : ownedGroupingPaths) {
            WsAttributeDefName attributeDefName = new WsAttributeDefName();
            attributeDefName.setName(TRIO);
            attributeDefNames.add(attributeDefName);

            WsAttributeAssign attributeAssign = new WsAttributeAssign();
            attributeAssign.setAttributeDefNameName(TRIO);
            attributeAssign.setOwnerGroupName(group);
            attributeAssigns.add(attributeAssign);
        }
        getAttributeAssignmentsResults.setWsAttributeDefNames(attributeDefNames.toArray(new WsAttributeDefName[attributeDefNames.size()]));
        getAttributeAssignmentsResults.setWsAttributeAssigns(attributeAssigns.toArray(new WsAttributeAssign[attributeDefNames.size()]));
        getAttributeAssignmentsResults.setWsGroups(groups);

        return getAttributeAssignmentsResults;
    }

    private WsAssignAttributesResults makeWsAssignAttributesResults(String resultCode) {
        WsAssignAttributesResults assignAttributesResults = new WsAssignAttributesResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(resultCode);
        assignAttributesResults.setResultMetadata(resultMeta);

        return assignAttributesResults;
    }

    private WsAssignGrouperPrivilegesLiteResult makeWsAssignGrouperPrivilegesLiteResult(String resultCode) {
        WsAssignGrouperPrivilegesLiteResult agplr = new WsAssignGrouperPrivilegesLiteResult();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(resultCode);
        agplr.setResultMetadata(resultMeta);

        return agplr;
    }

    private WsGetMembersResults makeWsGetMembersResults() {
        List<WsSubject> subjects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            WsSubject subject = new WsSubject();
            subject.setName("name" + i);
            subject.setId("ID" + i);
            subject.setAttributeValues(new String[]{"username" + i});

            subjects.add(subject);
        }

        WsGetMembersResults getMembersResults = new WsGetMembersResults();
        WsGetMembersResult getMembersResult = new WsGetMembersResult();
        getMembersResult.setWsSubjects(subjects.toArray(new WsSubject[subjects.size()]));

        WsGetMembersResult[] membersResults = new WsGetMembersResult[]{getMembersResult};
        getMembersResults.setResults(membersResults);


        return getMembersResults;
    }

    private WsGetMembersResults makeWsGetMembersResultsDB(String groupPath) {
        List<WsSubject> subjects = new ArrayList<>();
        Group group = groupRepository.findByPath(groupPath);

        List<Person> members = group.getMembers();
        for (Person member : members) {
            WsSubject subject = new WsSubject();
            subject.setName(member.getName());
            subject.setId(member.getUuid());
            subject.setAttributeValues(new String[]{member.getUsername()});

            subjects.add(subject);
        }

        WsGetMembersResults getMembersResults = new WsGetMembersResults();
        WsGetMembersResult getMembersResult = new WsGetMembersResult();
        getMembersResult.setWsSubjects(subjects.toArray(new WsSubject[subjects.size()]));

        WsGetMembersResult[] membersResults = new WsGetMembersResult[]{getMembersResult};
        getMembersResults.setResults(membersResults);


        return getMembersResults;
    }

    private WsAttributeAssignValue makeWsAttributeAssignValue() {
        WsAttributeAssignValue attributeAssignValue = new WsAttributeAssignValue();
        attributeAssignValue.setValueSystem("time");
        return attributeAssignValue;
    }

    private WsGetGrouperPrivilegesLiteResult makeWsGetGrouperPrivilegesLiteResult(boolean success) {

        WsGetGrouperPrivilegesLiteResult gplr = new WsGetGrouperPrivilegesLiteResult();
        WsResultMeta resultMetadata = new WsResultMeta();
        if (success) {
            resultMetadata.setResultCode(SUCCESS_ALLOWED);
        } else {
            resultMetadata.setResultCode(FAILURE);
        }
        gplr.setResultMetadata(resultMetadata);

        return gplr;
    }

    private WsGetMembershipsResults makeWsGetMembershipsResults() {
        WsGetMembershipsResults membershipsResults = new WsGetMembershipsResults();
        WsMembership membership = new WsMembership();
        membership.setMembershipId("1234");
        WsMembership[] memberships = new WsMembership[]{membership};
        membershipsResults.setWsMemberships(memberships);

        return membershipsResults;

    }
}

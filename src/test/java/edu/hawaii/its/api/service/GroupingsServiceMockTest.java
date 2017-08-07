package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.*;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsServiceMockTest {
    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.admins}")
    private String ADMINS;

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

    private static final String[] USERNAME = new String[]{"username_0", "username_1", "username_2", "username_3", "username_4"};
    private static final String GROUP = "group";
    private static final String GROUPING = "grouping";
    private static final String GROUPING_PATH = "path_to:" + GROUPING;
    private static final String GROUPING_OWNERS_PATH = GROUPING_PATH + ":owners";
    private static final String RANDOM_USER = "randomUser";
    private static final String OWNER_USER = "owner";
    private static final String ADMIN_USER = "admin";

    private static final WsSubjectLookup RANDOM_USER_LOOKUP = new WsSubjectLookup(null, null, RANDOM_USER);
    private static final WsSubjectLookup OWNER_LOOKUP = new WsSubjectLookup(null, null, OWNER_USER);
    private static final WsSubjectLookup ADMIN_LOOKUP = new WsSubjectLookup(null, null, ADMIN_USER);

    @Mock
    private GrouperFactoryService gf;

    @InjectMocks
    @Autowired
    private GroupingsService groupingsService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void construction() {
        assertNotNull(groupingsService);
    }

    @Test
    public void assignOwnershipTest(){

        given(gf.makeWsSubjectLookup(RANDOM_USER)).willReturn(RANDOM_USER_LOOKUP);
        given(gf.makeWsSubjectLookup(OWNER_USER)).willReturn(OWNER_LOOKUP);
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);

        given(gf.makeWsAddMemberResults(GROUPING_OWNERS_PATH, RANDOM_USER_LOOKUP, RANDOM_USER)).willReturn(addMemberResultsSuccess());
        given(gf.makeWsAddMemberResults(GROUPING_OWNERS_PATH, OWNER_LOOKUP, RANDOM_USER)).willReturn(addMemberResultsSuccess());
        given(gf.makeWsAddMemberResults(GROUPING_OWNERS_PATH, ADMIN_LOOKUP, RANDOM_USER)).willReturn(addMemberResultsSuccess());

        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, RANDOM_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, OWNER_USER)).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(ADMINS, RANDOM_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(ADMINS, OWNER_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        GroupingsServiceResult randomUserAdds = groupingsService.assignOwnership(GROUPING_PATH, RANDOM_USER, RANDOM_USER);
        GroupingsServiceResult ownerAdds = groupingsService.assignOwnership(GROUPING_PATH, OWNER_USER, RANDOM_USER);
        GroupingsServiceResult adminAdds = groupingsService.assignOwnership(GROUPING_PATH, ADMIN_USER, RANDOM_USER);

        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);
        assertEquals(ownerAdds.getResultCode(), SUCCESS);
        assertEquals(adminAdds.getResultCode(), SUCCESS);
    }

    @Test
    public void changeListservStatusTest(){
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, RANDOM_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, OWNER_USER)).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(ADMINS, RANDOM_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(ADMINS, OWNER_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        given(gf.makeWsGetAttributeAssignmentsResultsForGroup(ASSIGN_TYPE_GROUP, LISTSERV, GROUPING))
                .willReturn(getAttributeAssignmentsResultsListserv());

        given(gf.makeWsAssignAttributesResultsForGroup(ASSIGN_TYPE_GROUP, OPERATION_ASSIGN_ATTRIBUTE, LISTSERV, GROUPING))
                .willReturn(assignAttributesResultsListserv());
        //TODO finish test
    }

    @Test
    public void changeOptInStatusTest(){

    }

    @Test
    public void changeOptOutStatusTest(){

    }

    @Test
    public void removeOwnershipTest(){
        given(gf.makeWsSubjectLookup(RANDOM_USER)).willReturn(RANDOM_USER_LOOKUP);
        given(gf.makeWsSubjectLookup(OWNER_USER)).willReturn(OWNER_LOOKUP);
        given(gf.makeWsSubjectLookup(ADMIN_USER)).willReturn(ADMIN_LOOKUP);

        given(gf.makeWsDeleteMemberResults(GROUPING_OWNERS_PATH, RANDOM_USER_LOOKUP, RANDOM_USER)).willReturn(deleteMemberResultsSuccess());
        given(gf.makeWsDeleteMemberResults(GROUPING_OWNERS_PATH, OWNER_LOOKUP, RANDOM_USER)).willReturn(deleteMemberResultsSuccess());
        given(gf.makeWsDeleteMemberResults(GROUPING_OWNERS_PATH, ADMIN_LOOKUP, RANDOM_USER)).willReturn(deleteMemberResultsSuccess());

        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, RANDOM_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, OWNER_USER)).willReturn(isMemberResults());
        given(gf.makeWsHasMemberResults(GROUPING_OWNERS_PATH, ADMIN_USER)).willReturn(notMemberResults());

        given(gf.makeWsHasMemberResults(ADMINS, RANDOM_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(ADMINS, OWNER_USER)).willReturn(notMemberResults());
        given(gf.makeWsHasMemberResults(ADMINS, ADMIN_USER)).willReturn(isMemberResults());

        GroupingsServiceResult randomUserAdds = groupingsService.removeOwnership(GROUPING_PATH, RANDOM_USER, RANDOM_USER);
        GroupingsServiceResult ownerAdds = groupingsService.removeOwnership(GROUPING_PATH, OWNER_USER, RANDOM_USER);
        GroupingsServiceResult adminAdds = groupingsService.removeOwnership(GROUPING_PATH, ADMIN_USER, RANDOM_USER);

        assertNotEquals(randomUserAdds.getResultCode(), SUCCESS);
        assertEquals(ownerAdds.getResultCode(), SUCCESS);
        assertEquals(adminAdds.getResultCode(), SUCCESS);

    }

    @Test
    public void getGroupingTest(){

    }

    @Test
    public void getMyGroupingsTest(){

    }

    @Test
    public void optInTest(){

    }

    @Test
    public void optOutTest(){

    }

    @Test
    public void optTest(){

    }

    @Test
    public void cancelOptInTest(){

    }

    @Test
    public void cancelOptOutTest(){

    }

    @Test
    public void optOutPermissionTest(){

    }

    @Test
    public void optInPermissionTest(){

    }

    @Test
    public void groupHasAttributeTest(){

    }

    @Test
    public void groupingsInTest(){

    }

    @Test
    public void hasListservTest(){

    }

    @Test
    public void groupingsOwnedTest(){

    }

    @Test
    public void groupingsOptedIntoTest(){

    }

    @Test
    public void groupingsOptedOutOfTest(){

    }

    @Test
    public void groupingsOptedTest(){

    }

    @Test
    public void adminInfoTest(){

    }

    @Test
    public void groupingsToOptOutOfTest(){

    }

    @Test
    public void groupingsToOptIntoTest(){

    }

    @Test
    public void addSelfOptedTest(){

    }

    @Test
    public void checkSelfOptedTest(){

    }

    @Test
    public void inGroupTest(){

    }

    @Test
    public void isOwnerTest(){

    }

    @Test
    public void isAdminTest(){

    }

    @Test
    public void removeSelfOptedTest(){

    }

    @Test
    public void extractFirstMembershipIDTest(){

    }

    @Test
    public void groupOptOutPermissionTest(){

    }

    @Test
    public void groupOptInPermissionTest(){

    }

    @Test
    public void updateLastModifiedTest(){

    }

    @Test
    public void assignMembershipAttributesTest(){

    }

    @Test
    public void getMembershipAttributesTest(){

    }

    @Test
    public void assignGroupAttributesTest(){

    }

    @Test
    public void attributeAssignmentsResultsTest(){

    }

    @Test
    public void getGrouperPrivilegeTest(){

    }

    @Test
    public void assignGrouperPrivilegeTest(){

    }

    @Test
    public void membershipsResultsTest(){

    }

    @Test
    public void addMemberAsTest(){

    }

    @Test
    public void deleteMemberAsTest(){

    }

    @Test
    public void deleteMemberTest(){

    }

    @Test
    public void getMembersTest(){

    }

    @Test
    public void extractGroupingsTest(){

    }

    @Test
    public void getGroupPathsTest(){

    }

    @Test
    public void setGroupingAttributesTest(){

    }

    @Test
    public void parentGroupingPathTest(){

    }

    @Test
    public void extractGroupPathsTest(){

    }

    @Test
    public void changeGroupAttributeStatusTest(){

    }

        /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    private WsHasMemberResults isMemberResults() {
        WsHasMemberResults isMemberResults = new WsHasMemberResults();
        WsHasMemberResult isMemberResult = new WsHasMemberResult();
        WsResultMeta isMemberMeta = new WsResultMeta();
        isMemberMeta.setResultCode(IS_MEMBER);
        isMemberResult.setResultMetadata(isMemberMeta);
        isMemberResults.setResults(new WsHasMemberResult[] {isMemberResult});

        return isMemberResults;
    }

    private WsHasMemberResults notMemberResults() {
        WsHasMemberResults notMemberResults = new WsHasMemberResults();
        WsHasMemberResult notMemberResult = new WsHasMemberResult();
        WsResultMeta notMemberMeta = new WsResultMeta();
        notMemberMeta.setResultCode("not member");
        notMemberResult.setResultMetadata(notMemberMeta);
        notMemberResults.setResults(new WsHasMemberResult[] {notMemberResult});

        return notMemberResults;
    }

    private WsAddMemberResults addMemberResultsSuccess() {
        WsAddMemberResults addMemberResults = new WsAddMemberResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(SUCCESS);
        addMemberResults.setResultMetadata(resultMeta);

        return addMemberResults;
    }

    private WsDeleteMemberResults deleteMemberResultsSuccess() {
        WsDeleteMemberResults deleteMemberResults = new WsDeleteMemberResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(SUCCESS);
        deleteMemberResults.setResultMetadata(resultMeta);

        return deleteMemberResults;
    }

    private WsGetAttributeAssignmentsResults getAttributeAssignmentsResultsListserv() {
        WsGetAttributeAssignmentsResults getAttributeAssignmentsResults = new WsGetAttributeAssignmentsResults();
        WsAttributeAssign attributeAssign = new WsAttributeAssign();
        attributeAssign.setAttributeDefNameName(LISTSERV);
        getAttributeAssignmentsResults.setWsAttributeAssigns(new WsAttributeAssign[] {attributeAssign});

        return getAttributeAssignmentsResults;
    }

    private WsAssignAttributesResults assignAttributesResultsListserv() {
        WsAssignAttributesResults assignAttributesResults = new WsAssignAttributesResults();
        WsResultMeta resultMeta = new WsResultMeta();
        resultMeta.setResultCode(SUCCESS);
        assignAttributesResults.setResultMetadata(resultMeta);

        return assignAttributesResults;
    }
}

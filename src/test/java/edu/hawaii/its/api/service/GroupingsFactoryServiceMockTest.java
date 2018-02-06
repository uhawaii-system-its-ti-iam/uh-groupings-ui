package edu.hawaii.its.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.util.Dates;

import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssignValue;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
@WebAppConfiguration
public class GroupingsFactoryServiceMockTest {

    GrouperFactoryServiceImpl gfs = new GrouperFactoryServiceImpl();

    @Test
    public void addEmptyGroupTest() {
//    public WsGroupSaveResults addEmptyGroup(String username, String path) {
//        WsGroupToSave groupToSave = new WsGroupToSave();
//        WsGroup group = new WsGroup();
//        group.setName(path);
//        groupToSave.setWsGroup(group);
//
//        WsSubjectLookup lookup = makeWsSubjectLookup(username);
//
//        return new GcGroupSave().addGroupToSave(groupToSave).assignActAsSubject(lookup).execute();
    }

    @Test
    public void makeWsSubjectLookupTest() {
        String username = "username";
        WsSubjectLookup lookup = gfs.makeWsSubjectLookup(username);

        assertEquals(username, lookup.getSubjectIdentifier());
    }

    @Test
    public void makeWsGroupLookupTest() {
        String groupPath = "path:to:group";
        WsGroupLookup groupLookup = gfs.makeWsGroupLookup(groupPath);
        assertEquals(groupPath, groupLookup.getGroupName());
    }

    @Test
    public void makeWsStemLookupTest() {
        String stemPath = "path:to:stem";
        String stemUuid = "12345";
        WsStemLookup stemLookup = gfs.makeWsStemLookup(stemPath);
        assertEquals(stemPath, stemLookup.getStemName());

        stemLookup = gfs.makeWsStemLookup(stemPath, stemUuid);
        assertEquals(stemPath, stemLookup.getStemName());
        assertEquals(stemUuid, stemLookup.getUuid());
    }

    @Test
    public void makeWsStemSaveResultsTest() {
//    public WsStemSaveResults makeWsStemSaveResults(String username, String stemPath) {
//        WsStemToSave stemToSave = new WsStemToSave();
//        WsStem stem = new WsStem();
//        stem.setName(stemPath);
//        stemToSave.setWsStem(stem);
//        WsSubjectLookup subject = makeWsSubjectLookup(username);
//        return new GcStemSave().addStemToSave(stemToSave).assignActAsSubject(subject).execute();
    }

    @Test
    public void makeWsAttributeAssignValueTest() {
        String time = Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");

        WsAttributeAssignValue attributeAssignValue = gfs.makeWsAttributeAssignValue(time);
        assertEquals(time, attributeAssignValue.getValueSystem());
    }

    @Test
    public void makeWsAddMemberResultsWithLookupTest() {
//    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, String newMember) {
//        return new GcAddMember()
//                .assignActAsSubject(lookup)
//                .addSubjectIdentifier(newMember)
//                .assignGroupName(group)
//                .execute();
    }

    @Test
    public void makeWsAddMemberResultsWithListTest() {
//    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, List<String> newMembers) {
//        GcAddMember addMember = new GcAddMember();
//        addMember.assignActAsSubject(lookup);
//        addMember.assignGroupName(group);
//
//        newMembers.forEach(addMember::addSubjectIdentifier);
//
//        return addMember.execute();
    }

    @Test
    public void makeWsAddMemberResultsTest() {
//    public WsAddMemberResults makeWsAddMemberResults(String group, String newMember) {
//        return new GcAddMember()
//                .addSubjectIdentifier(newMember)
//                .assignGroupName(group)
//                .execute();
    }

    @Test
    public void makeWsDeleteMemberResultsTest() {
//    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, String memberToDelete) {
//        return new GcDeleteMember()
//                .addSubjectIdentifier(memberToDelete)
//                .assignGroupName(group)
//                .execute();
    }

    @Test
    public void makeWsDeleteMemberResultsTestWithSubjectLookup() {
//    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, String memberToDelete) {
//        return new GcDeleteMember()
//                .assignActAsSubject(lookup)
//                .addSubjectIdentifier(memberToDelete)
//                .assignGroupName(group)
//                .execute();
    }

    @Test
    public void makeWsDeleteMemberResultsWithListTest() {
//    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, List<String> membersToDelete) {
//        GcDeleteMember deleteMember = new GcDeleteMember();
//        deleteMember.assignActAsSubject(lookup);
//        deleteMember.assignGroupName(group);
//
//        membersToDelete.forEach(deleteMember::addSubjectIdentifier);
//
//        return deleteMember.execute();
}

    @Test
    public void makeWsGetAttributeAssignmentsResultsTest() {
//    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
//String attributeDefNameName){
//        return new GcGetAttributeAssignments()
//                .addAttributeDefNameName(attributeDefNameName)
//                .assignAttributeAssignType(assignType)
//                .execute();
    }

    @Test
    public void makeWsGetAttributeAssignmentsResultsTrioTest() {
//    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
//String attributeDefNameName0,
//        String attributeDefNameName1){
//        return new GcGetAttributeAssignments()
//                .addAttributeDefNameName(attributeDefNameName0)
//                .addAttributeDefNameName(attributeDefNameName1)
//                .assignAttributeAssignType(assignType)
//                .execute();
    }

@Test
public void makeWsGetAttributeAssignmentsResultsTrioWithListTest() {
//    public List<WsGetAttributeAssignmentsResults> makeWsGetAttributeAssignmentsResultsTrio(String assignType,
//String attributeDefNameName,
//        List<String>ownerGroupNames){
//
//        List<WsGetAttributeAssignmentsResults> attributeAssignmentsResultList = new ArrayList<>();
//        Iterator iterator = ownerGroupNames.iterator();
//
//        for (int i = 0; i < ownerGroupNames.size(); i += ATTRIBUTES_ASSIGN_ID_SIZE) {
//            GcGetAttributeAssignments attributeAssignments = new GcGetAttributeAssignments()
//                    .addAttributeDefNameName(attributeDefNameName)
//                    .assignAttributeAssignType(assignType);
//
//            for (int j = 0; j < ATTRIBUTES_ASSIGN_ID_SIZE; j++) {
//                if (iterator.hasNext()) {
//                    attributeAssignments.addOwnerGroupName(iterator.next().toString());
//                } else {
//                    break;
//                }
//            }
//            attributeAssignmentsResultList.add(attributeAssignments.execute());
//        }
//
//        return attributeAssignmentsResultList;
}

@Test
public void makeWsGetAttributeAssignmentsResultsTrioWithTwoAttributeDefNamesTest() {
//public List<WsGetAttributeAssignmentsResults>makeWsGetAttributeAssignmentsResultsTrio(String assignType,
//        String attributeDefNameName0,
//        String attributeDefNameName1,
//        List<String>ownerGroupNames){
//        List<WsGetAttributeAssignmentsResults> attributeAssignmentsResultList = new ArrayList<>();
//        Iterator iterator = ownerGroupNames.iterator();
//
//        for (int i = 0; i < ownerGroupNames.size(); i += ATTRIBUTES_ASSIGN_ID_SIZE) {
//            GcGetAttributeAssignments attributeAssignments = new GcGetAttributeAssignments()
//                    .addAttributeDefNameName(attributeDefNameName0)
//                    .addAttributeDefNameName(attributeDefNameName1)
//                    .assignAttributeAssignType(assignType);
//
//            for (int j = 0; j < ATTRIBUTES_ASSIGN_ID_SIZE; j++) {
//                if (iterator.hasNext()) {
//                    attributeAssignments.addOwnerGroupName(iterator.next().toString());
//                } else {
//                    break;
//                }
//            }
//            attributeAssignmentsResultList.add(attributeAssignments.execute());
//        }
//
//        return attributeAssignmentsResultList;
        }

@Test
public void makeWsGetAttributeAssignmentsResultsForMembershipTest() {
//public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForMembership(String assignType,
//        String attributeDefNameName,
//        String membershipId){
//        return new GcGetAttributeAssignments()
//                .addAttributeDefNameName(attributeDefNameName)
//                .addOwnerMembershipId(membershipId)
//                .assignAttributeAssignType(assignType)
//                .execute();
        }

@Test
    public void makeWsGetAttributeAssignmentsResultsForGroupTest() {
//public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
//        String group){
//        return new GcGetAttributeAssignments()
//                .addOwnerGroupName(group)
//                .assignAttributeAssignType(assignType)
//                .execute();
        }

@Test
    public void makeWsGetAttributeAssignmentsResultsForGroupWithAttributeDefNameTest() {
//public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
//        String attributeDefNameName,
//        String group){
//        return new GcGetAttributeAssignments()
//                .addAttributeDefNameName(attributeDefNameName)
//                .addOwnerGroupName(group)
//                .assignAttributeAssignType(assignType)
//                .execute();
        }

@Test
    public void makeWsHasMemberResultsTest() {
//public WsHasMemberResults makeWsHasMemberResults(String group,String username){

//        return new GcHasMember()
//                .assignGroupName(group)
//                .addSubjectIdentifier(username)
//                .execute();
        }

@Test
    public void makeWsAssignAttributesReultsTest() {
//public WsAssignAttributesResults makeWsAssignAttributesResults(String attributeAssignType,
//        String attributeAssignOperation,
//        String ownerGroupName,
//        String attributeDefNameName,
//        String attributeAssignValueOperation,
//        WsAttributeAssignValue value){

//        return new GcAssignAttributes()
//                .assignAttributeAssignType(attributeAssignType)
//                .assignAttributeAssignOperation(attributeAssignOperation)
//                .addOwnerGroupName(ownerGroupName)
//                .addAttributeDefNameName(attributeDefNameName)
//                .assignAttributeAssignValueOperation(attributeAssignValueOperation)
//                .addValue(value)
//                .execute();
        }

@Test
public void makeWsAssignAttributesResultsForMembershipTest() {
//public WsAssignAttributesResults makeWsAssignAttributesResultsForMembership(String attributeAssignType,
//        String attributeAssignOperation,
//        String attributeDefNameName,
//        String ownerMembershipId){

//        return new GcAssignAttributes()
//                .assignAttributeAssignType(attributeAssignType)
//                .assignAttributeAssignOperation(attributeAssignOperation)
//                .addAttributeDefNameName(attributeDefNameName)
//                .addOwnerMembershipId(ownerMembershipId)
//                .execute();
        }

@Test
public void makeWsAssignAttributesResultsForGroupTest() {
//public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(String attributeAssingType,
//        String attributeAssignOperation,
//        String attributeDefNameName,
//        String ownerGroupName){
//        return new GcAssignAttributes()
//                .assignAttributeAssignType(attributeAssingType)
//                .assignAttributeAssignOperation(attributeAssignOperation)
//                .addAttributeDefNameName(attributeDefNameName)
//                .addOwnerGroupName(ownerGroupName)
//                .execute();
        }

@Test
public void makeWsAssignAttributesResultsForGroup() {
//public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(WsSubjectLookup lookup,
//        String attributeAssingType,
//        String attributeAssignOperation,
//        String attributeDefNameName,
//        String ownerGroupName){
//        return new GcAssignAttributes()
//                .assignActAsSubject(lookup)
//                .assignAttributeAssignType(attributeAssingType)
//                .assignAttributeAssignOperation(attributeAssignOperation)
//                .addAttributeDefNameName(attributeDefNameName)
//                .addOwnerGroupName(ownerGroupName)
//                .execute();
        }

@Test
public void makeWsAssignGrouperPrivilegesLiteResult() {
//public WsAssignGrouperPrivilegesLiteResult makeWsAssignGrouperPrivilegesLiteResult(String groupName,
//        String privilegeName,
//        WsSubjectLookup lookup,
//        boolean allowed){

//        return new GcAssignGrouperPrivilegesLite()
//                .assignGroupName(groupName)
//                .assignPrivilegeName(privilegeName)
//                .assignSubjectLookup(lookup)
//                .assignAllowed(allowed)
//                .execute();
        }

@Test
public void makeWsGetGrouperPrivilegesLiteResult() {
//public WsGetGrouperPrivilegesLiteResult makeWsGetGrouperPrivilegesLiteResult(String groupName,
//        String privilegeName,
//        WsSubjectLookup lookup){

//        return new GcGetGrouperPrivilegesLite()
//                .assignGroupName(groupName)
//                .assignPrivilegeName(privilegeName)
//                .assignSubjectLookup(lookup)
//                .execute();
        }


@Test
public void makeWsGetMembershipsResults() {
//public WsGetMembershipsResults makeWsGetMembershipsResults(String groupName,
//        WsSubjectLookup lookup){

//        return new GcGetMemberships()
//                .addGroupName(groupName)
//                .addWsSubjectLookup(lookup)
//                .execute();
        }


@Test
public void makeWsGetMembersResults() {
//public WsGetMembersResults makeWsGetMembersResults(String subjectAttributeName,
//        WsSubjectLookup lookup,
//        String groupName){

//        return new GcGetMembers()
//                .addSubjectAttributeName(subjectAttributeName)
//                .assignActAsSubject(lookup)
//                .addGroupName(groupName)
//                .assignIncludeSubjectDetail(true)
//                .execute();
        }


@Test
public void makeWsGetGroupsResults() {
//public WsGetGroupsResults makeWsGetGroupsResults(String username,
//        WsStemLookup stemLookup,
//        StemScope stemScope){

//        return new GcGetGroups()
//                .addSubjectIdentifier(username)
//                .assignWsStemLookup(stemLookup)
//                .assignStemScope(stemScope)
//                .execute();
        }


@Test
public void makeEmptyWsAttributeAssignArrayTest(){
//public WsAttributeAssign[]makeEmptyWsAttributeAssignArray(){
//        return new WsAttributeAssign[0];
        }


@Test
public void toStringTest(){
//    public String toString(){
//        return "GrouperFactoryServiceImpl";
        }
        }

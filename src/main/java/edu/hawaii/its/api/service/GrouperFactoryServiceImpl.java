package edu.hawaii.its.api.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.hawaii.its.api.type.Person;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import edu.internet2.middleware.grouperClient.api.GcAddMember;
import edu.internet2.middleware.grouperClient.api.GcAssignAttributes;
import edu.internet2.middleware.grouperClient.api.GcAssignGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.api.GcDeleteMember;
import edu.internet2.middleware.grouperClient.api.GcFindGroups;
import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.api.GcGetGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.api.GcGetGroups;
import edu.internet2.middleware.grouperClient.api.GcGetMembers;
import edu.internet2.middleware.grouperClient.api.GcGetMemberships;
import edu.internet2.middleware.grouperClient.api.GcGetSubjects;
import edu.internet2.middleware.grouperClient.api.GcGroupSave;
import edu.internet2.middleware.grouperClient.api.GcHasMember;
import edu.internet2.middleware.grouperClient.api.GcStemSave;
import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignAttributesResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssignValue;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsFindGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetSubjectsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupDetail;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupSaveResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupToSave;
import edu.internet2.middleware.grouperClient.ws.beans.WsHasMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsStem;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemSaveResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemToSave;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@Service("grouperFactoryService")
@Profile(value = { "localhost", "test", "integrationTest", "qa", "prod" })
public class GrouperFactoryServiceImpl implements GrouperFactoryService {

    @Value("${groupings.api.attribute_assign_id_size}")
    private Integer ATTRIBUTES_ASSIGN_ID_SIZE;

    @Value("${groupings.api.composite_type.complement}")
    private String COMPLEMENT;

    @Value("${groupings.api.composite_type.intersection}")
    private String INTERSECTION;

    @Value("${groupings.api.composite_type.union}")
    private String UNION;

    // Constructor.
    public GrouperFactoryServiceImpl() {
        // Empty.
    }

    @Override
    public WsGroupSaveResults addEmptyGroup(String username, String path) {
        WsGroupToSave groupToSave = new WsGroupToSave();
        WsGroupLookup groupLookup = makeWsGroupLookup(path);
        WsGroup group = new WsGroup();
        group.setName(path);
        groupToSave.setWsGroup(group);
        groupToSave.setWsGroupLookup(groupLookup);

        WsSubjectLookup subjectLookup = makeWsSubjectLookup(username);

        return new GcGroupSave().addGroupToSave(groupToSave).assignActAsSubject(subjectLookup).execute();
    }

    @Override
    public WsGroupSaveResults addCompositeGroup(String username, String parentGroupPath, String compositeType,
            String leftGroupPath, String rightGroupPath) {
        WsGroupToSave groupToSave = new WsGroupToSave();
        WsGroup group = new WsGroup();
        WsGroupDetail wsGroupDetail = new WsGroupDetail();

        //get the left and right groups from the database/grouper
        WsGroup leftGroup = makeWsFindGroupsResults(leftGroupPath).getGroupResults()[0];
        WsGroup rightGroup = makeWsFindGroupsResults(rightGroupPath).getGroupResults()[0];

        wsGroupDetail.setCompositeType(compositeType);
        wsGroupDetail.setLeftGroup(leftGroup);
        wsGroupDetail.setRightGroup(rightGroup);

        group.setName(parentGroupPath);
        group.setDetail(wsGroupDetail);
        groupToSave.setWsGroup(group);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);

        return new GcGroupSave().addGroupToSave(groupToSave).assignActAsSubject(lookup).execute();
    }

    /**
     * @param username: username of user to be looked up
     * @return a WsSubjectLookup with username as the subject identifier
     */
    @Override
    public WsSubjectLookup makeWsSubjectLookup(String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        return wsSubjectLookup;
    }

    /**
     * @param group: group to be looked up
     * @return a WsGroupLookup with group as the group name
     */
    @Override
    public WsGroupLookup makeWsGroupLookup(String group) {
        WsGroupLookup groupLookup = new WsGroupLookup();
        groupLookup.setGroupName(group);

        return groupLookup;
    }

    @Override
    public WsStemLookup makeWsStemLookup(String stemName) {
        return makeWsStemLookup(stemName, null);
    }

    @Override
    public WsStemLookup makeWsStemLookup(String stemName, String stemUuid) {
        return new WsStemLookup(stemName, stemUuid);
    }

    @Override
    public WsStemSaveResults makeWsStemSaveResults(String username, String stemPath) {
        String[] splitString = stemPath.split(":");
        String splitStringName = splitString[splitString.length - 1];

        WsStemToSave stemToSave = new WsStemToSave();
        WsStemLookup stemLookup = new WsStemLookup();
        stemLookup.setStemName(stemPath);
        WsStem stem = new WsStem();
        stem.setName(stemPath);
        stem.setExtension(splitStringName);
        stem.setDescription(splitStringName);
        stem.setDisplayExtension(splitStringName);

        stemToSave.setWsStem(stem);
        stemToSave.setWsStemLookup(stemLookup);

        WsSubjectLookup subject = makeWsSubjectLookup(username);
        return new GcStemSave().addStemToSave(stemToSave).assignActAsSubject(subject).execute();
    }

    @Override
    public WsAttributeAssignValue makeWsAttributeAssignValue(String time) {

        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(time);

        return dateTimeValue;
    }

    @Override
    public WsFindGroupsResults makeWsFindGroupsResults(String groupPath) {
        return new GcFindGroups()
                .addGroupName(groupPath)
                .execute();
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResultsGroup(String groupPath, WsSubjectLookup lookup, String groupUid) {
        return new GcAddMember()
                .assignActAsSubject(lookup)
                .addSubjectId(groupUid)
                .assignGroupName(groupPath)
                .execute();
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, String newMember) {
        return new GcAddMember()
                .assignActAsSubject(lookup)
                .addSubjectIdentifier(newMember)
                .assignGroupName(group)
                .execute();
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, Person personToAdd) {
        if (personToAdd.getUsername() != null) {
            return makeWsAddMemberResults(group, lookup, personToAdd.getUsername());
        }

        if (personToAdd.getUuid() == null) {
            throw new NullPointerException("The person is required to have either a username or a uuid");
        }

        return new GcAddMember()
                .assignActAsSubject(lookup)
                .addSubjectId(personToAdd.getUuid())
                .assignGroupName(group)
                .execute();
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, List<String> newMembers) {
        GcAddMember addMember = new GcAddMember();
        addMember.assignActAsSubject(lookup);
        addMember.assignGroupName(group);

        newMembers.forEach(addMember::addSubjectIdentifier);

        return addMember.execute();
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, String newMember) {
        return new GcAddMember()
                .addSubjectIdentifier(newMember)
                .assignGroupName(group)
                .execute();
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, String memberToDelete) {
        return new GcDeleteMember()
                .addSubjectIdentifier(memberToDelete)
                .assignGroupName(group)
                .execute();
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup,
            String memberToDelete) {
        return new GcDeleteMember()
                .assignActAsSubject(lookup)
                .addSubjectIdentifier(memberToDelete)
                .assignGroupName(group)
                .execute();
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup,
            Person personToDelete) {
        if (personToDelete.getUsername() != null) {
            return makeWsDeleteMemberResults(group, lookup, personToDelete.getUsername());
        }

        if (personToDelete.getUuid() == null) {
            throw new NullPointerException("The person is required to have either a username or a uuid");
        }

        return new GcDeleteMember()
                .assignActAsSubject(lookup)
                .addSubjectId(personToDelete.getUuid())
                .assignGroupName(group)
                .execute();
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup,
            List<String> membersToDelete) {
        GcDeleteMember deleteMember = new GcDeleteMember();
        deleteMember.assignActAsSubject(lookup);
        deleteMember.assignGroupName(group);

        membersToDelete.forEach(deleteMember::addSubjectIdentifier);

        return deleteMember.execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName) {
        return new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName0,
            String attributeDefNameName1) {
        return new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName0)
                .addAttributeDefNameName(attributeDefNameName1)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public List<WsGetAttributeAssignmentsResults> makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName,
            List<String> ownerGroupNames) {

        List<WsGetAttributeAssignmentsResults> attributeAssignmentsResultList = new ArrayList<>();
        Iterator iterator = ownerGroupNames.iterator();

        for (int i = 0; i < ownerGroupNames.size(); i += ATTRIBUTES_ASSIGN_ID_SIZE) {
            GcGetAttributeAssignments attributeAssignments = new GcGetAttributeAssignments()
                    .addAttributeDefNameName(attributeDefNameName)
                    .assignAttributeAssignType(assignType);

            for (int j = 0; j < ATTRIBUTES_ASSIGN_ID_SIZE; j++) {
                if (iterator.hasNext()) {
                    attributeAssignments.addOwnerGroupName(iterator.next().toString());
                } else {
                    break;
                }
            }
            attributeAssignmentsResultList.add(attributeAssignments.execute());
        }

        return attributeAssignmentsResultList;
    }

    @Override
    public List<WsGetAttributeAssignmentsResults> makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName0,
            String attributeDefNameName1,
            List<String> ownerGroupNames) {
        List<WsGetAttributeAssignmentsResults> attributeAssignmentsResultList = new ArrayList<>();
        Iterator iterator = ownerGroupNames.iterator();

        for (int i = 0; i < ownerGroupNames.size(); i += ATTRIBUTES_ASSIGN_ID_SIZE) {
            GcGetAttributeAssignments attributeAssignments = new GcGetAttributeAssignments()
                    .addAttributeDefNameName(attributeDefNameName0)
                    .addAttributeDefNameName(attributeDefNameName1)
                    .assignAttributeAssignType(assignType);

            for (int j = 0; j < ATTRIBUTES_ASSIGN_ID_SIZE; j++) {
                if (iterator.hasNext()) {
                    attributeAssignments.addOwnerGroupName(iterator.next().toString());
                } else {
                    break;
                }
            }
            attributeAssignmentsResultList.add(attributeAssignments.execute());
        }

        return attributeAssignmentsResultList;
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForMembership(String assignType,
            String attributeDefNameName,
            String membershipId) {
        return new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName)
                .addOwnerMembershipId(membershipId)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
            String group) {
        return new GcGetAttributeAssignments()
                .addOwnerGroupName(group)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
            String attributeDefNameName,
            String group) {
        return new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName)
                .addOwnerGroupName(group)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public WsHasMemberResults makeWsHasMemberResults(String group, String username) {

        return new GcHasMember()
                .assignGroupName(group)
                .addSubjectIdentifier(username)
                .execute();
    }

    @Override
    public WsHasMemberResults makeWsHasMemberResults(String group, Person person) {
        if (person.getUsername() != null) {
            return makeWsHasMemberResults(group, person.getUsername());
        }

        if (person.getUuid() == null) {
            throw new NullPointerException("The person is required to have either a username or a uuid");
        }

        return new GcHasMember()
                .assignGroupName(group)
                .addSubjectId(person.getUuid())
                .execute();
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResults(String attributeAssignType,
            String attributeAssignOperation,
            String ownerGroupName,
            String attributeDefNameName,
            String attributeAssignValueOperation,
            WsAttributeAssignValue value) {

        return new GcAssignAttributes()
                .assignAttributeAssignType(attributeAssignType)
                .assignAttributeAssignOperation(attributeAssignOperation)
                .addOwnerGroupName(ownerGroupName)
                .addAttributeDefNameName(attributeDefNameName)
                .assignAttributeAssignValueOperation(attributeAssignValueOperation)
                .addValue(value)
                .execute();
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResultsForMembership(String attributeAssignType,
            String attributeAssignOperation,
            String attributeDefNameName,
            String ownerMembershipId) {

        return new GcAssignAttributes()
                .assignAttributeAssignType(attributeAssignType)
                .assignAttributeAssignOperation(attributeAssignOperation)
                .addAttributeDefNameName(attributeDefNameName)
                .addOwnerMembershipId(ownerMembershipId)
                .execute();
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(String attributeAssingType,
            String attributeAssignOperation,
            String attributeDefNameName,
            String ownerGroupName) {
        return new GcAssignAttributes()
                .assignAttributeAssignType(attributeAssingType)
                .assignAttributeAssignOperation(attributeAssignOperation)
                .addAttributeDefNameName(attributeDefNameName)
                .addOwnerGroupName(ownerGroupName)
                .execute();
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(WsSubjectLookup lookup,
            String attributeAssingType,
            String attributeAssignOperation,
            String attributeDefNameName,
            String ownerGroupName) {
        return new GcAssignAttributes()
                .assignActAsSubject(lookup)
                .assignAttributeAssignType(attributeAssingType)
                .assignAttributeAssignOperation(attributeAssignOperation)
                .addAttributeDefNameName(attributeDefNameName)
                .addOwnerGroupName(ownerGroupName)
                .execute();
    }

    @Override
    public WsAssignGrouperPrivilegesLiteResult makeWsAssignGrouperPrivilegesLiteResult(String groupName,
            String privilegeName,
            WsSubjectLookup lookup,
            boolean allowed) {

        return new GcAssignGrouperPrivilegesLite()
                .assignGroupName(groupName)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .assignAllowed(allowed)
                .execute();
    }

    @Override
    public WsGetGrouperPrivilegesLiteResult makeWsGetGrouperPrivilegesLiteResult(String groupName,
            String privilegeName,
            WsSubjectLookup lookup) {

        return new GcGetGrouperPrivilegesLite()
                .assignGroupName(groupName)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .execute();
    }

    @Override
    public WsGetMembershipsResults makeWsGetMembershipsResults(String groupName,
            WsSubjectLookup lookup) {

        return new GcGetMemberships()
                .addGroupName(groupName)
                .addWsSubjectLookup(lookup)
                .execute();
    }

    @Override
    public WsGetMembersResults makeWsGetMembersResults(String subjectAttributeName,
            WsSubjectLookup lookup,
            String groupName) {

        return new GcGetMembers()
                .addSubjectAttributeName(subjectAttributeName)
                .assignActAsSubject(lookup)
                .addGroupName(groupName)
                .assignIncludeSubjectDetail(true)
                .execute();
    }

    @Override
    public WsGetGroupsResults makeWsGetGroupsResults(String username,
            WsStemLookup stemLookup,
            StemScope stemScope) {

        return new GcGetGroups()
                .addSubjectIdentifier(username)
                .assignWsStemLookup(stemLookup)
                .assignStemScope(stemScope)
                .execute();
    }

    public WsGetSubjectsResults makeWsGetSubjectsResults(WsSubjectLookup lookup) {

        return new GcGetSubjects()
                .addSubjectAttributeName("uid")
                .addSubjectAttributeName("cn")
                .addSubjectAttributeName("sn")
                .addSubjectAttributeName("givenName")
                .addSubjectAttributeName("uhuuid")
                .addWsSubjectLookup(lookup)
                .execute();
    }

    @Override
    public WsAttributeAssign[] makeEmptyWsAttributeAssignArray() {
        return new WsAttributeAssign[0];
    }

    @Override
    public String toString() {
        return "GrouperFactoryServiceImpl";
    }

}

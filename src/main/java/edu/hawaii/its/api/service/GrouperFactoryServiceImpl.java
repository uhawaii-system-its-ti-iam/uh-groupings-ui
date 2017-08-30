package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Person;

import edu.internet2.middleware.grouperClient.api.*;
import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("GrouperFactoryService")
public class GrouperFactoryServiceImpl implements GrouperFactoryService{
    public GrouperFactoryServiceImpl() {
        //empty
    }

    @Override
    public WsGroupSaveResults addGroup (String path, List<Person> members) {
        //todo
        return null;
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
    public WsStemLookup makeWsStemLookup(String stemName, String stemUuid) {
        return new WsStemLookup(stemName, stemUuid);
    }

    @Override
    public WsAttributeAssignValue makeWsAttributeAssignValue(String time) {

        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(time);

        return  dateTimeValue;
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
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, String memberToDelete) {
        return new GcDeleteMember()
                .assignActAsSubject(lookup)
                .addSubjectIdentifier(memberToDelete)
                .assignGroupName(group)
                .execute();
    }


    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                  String attributeDefNameName) {
        return new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                  String attributeDefNameName0,
                                                                                  String attributeDefNameName1) {
        return new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName0)
                .addAttributeDefNameName(attributeDefNameName1)
                .assignAttributeAssignType(assignType)
                .execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                  String attributeDefNameName,
                                                                                  List<String> ownerGroupNames) {

        GcGetAttributeAssignments getAttributeAssignments = new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName)
                .assignAttributeAssignType(assignType);

        ownerGroupNames.forEach(getAttributeAssignments::addOwnerGroupName);

        return getAttributeAssignments.execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                  String attributeDefNameName0,
                                                                                  String attributeDefNameName1,
                                                                                  List<String> ownerGroupNames) {

        GcGetAttributeAssignments getAttributeAssignments = new GcGetAttributeAssignments()
                .addAttributeDefNameName(attributeDefNameName0)
                .addAttributeDefNameName(attributeDefNameName1)
                .assignAttributeAssignType(assignType);

        ownerGroupNames.forEach(getAttributeAssignments::addOwnerGroupName);

        return getAttributeAssignments.execute();
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

    @Override
    public WsAttributeAssign[] makeEmptyWsAttributeAssignArray() {
        return new WsAttributeAssign[0];
    }
}

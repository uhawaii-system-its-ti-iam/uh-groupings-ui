package edu.hawaii.its.api.service;

import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.*;

import java.util.List;

public interface GrouperFactoryService {
    public WsGroupSaveResults addEmptyGroup(String username, String path);

    public WsSubjectLookup makeWsSubjectLookup(String username);

    public WsGroupLookup makeWsGroupLookup(String group);

    public WsStemLookup makeWsStemLookup(String stemName, String stemUuid);

    public WsStemSaveResults makeWsStemSaveResults(String username, String stemPath);

    public WsAttributeAssignValue makeWsAttributeAssignValue(String time);

    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, String newMember);

    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, List<String> newMembers);

    public WsAddMemberResults makeWsAddMemberResults(String group, String newMember);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, String memberToDelete);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, String memberToDelete);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, List<String> membersToDelete);


    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                 String attributeDefNameName);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                 String attributeDefNameName0,
                                                                                 String attributeDefNameName1);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                 String attributeDefNameName,
                                                                                 List<String> ownerGroupNames);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResults(String assignType,
                                                                                 String attributeDefNameName0,
                                                                                 String attributeDefNameName1,
                                                                                 List<String> ownerGroupNames);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForMembership(String assignType,
                                                                                              String attributeDefNameName,
                                                                                              String membershipId);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
                                                                                         String group);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
                                                                                         String attributeDefNameName,
                                                                                         String group);

    public WsHasMemberResults makeWsHasMemberResults(String group, String username);

    public WsAssignAttributesResults makeWsAssignAttributesResults(String attributeAssignType,
                                                                   String attributeAssignOperation,
                                                                   String ownerGroupName,
                                                                   String attributeDefNameName,
                                                                   String attributeAssignValueOperation,
                                                                   WsAttributeAssignValue value);

    public WsAssignAttributesResults makeWsAssignAttributesResultsForMembership(String attributeAssignType,
                                                                                String attributeAssignOperation,
                                                                                String attributeDefNameName,
                                                                                String ownerMembershipId);


    public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(String attributeAssingType,
                                                                           String attributeAssignOperation,
                                                                           String attributeDefNameName,
                                                                           String ownerGroupName);

    public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(WsSubjectLookup lookup,
                                                                           String attributeAssingType,
                                                                           String attributeAssignOperation,
                                                                           String attributeDefNameName,
                                                                           String ownerGroupName);

    public WsAssignGrouperPrivilegesLiteResult makeWsAssignGrouperPrivilegesLiteResult(String groupName,
                                                                                       String privilegeName,
                                                                                       WsSubjectLookup lookup,
                                                                                       boolean allowed);

    public WsGetGrouperPrivilegesLiteResult makeWsGetGrouperPrivilegesLiteResult(String groupName,
                                                                                 String privilegeName,
                                                                                 WsSubjectLookup lookup);

    public WsGetMembershipsResults makeWsGetMembershipsResults(String groupName, WsSubjectLookup lookup);

    public WsGetMembersResults makeWsGetMembersResults(String subjectAttributeName, WsSubjectLookup lookup, String groupName);

    public WsGetGroupsResults makeWsGetGroupsResults(String username, WsStemLookup stemLookup, StemScope stemScope);

    public WsAttributeAssign[] makeEmptyWsAttributeAssignArray();
}

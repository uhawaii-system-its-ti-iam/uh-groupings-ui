package edu.hawaii.its.api.service;

import java.util.List;

import edu.hawaii.its.api.type.Person;

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
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupSaveResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsHasMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemSaveResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

public interface GrouperFactoryService {
    public WsGroupSaveResults addEmptyGroup(String username, String path);

    public WsSubjectLookup makeWsSubjectLookup(String username);

    public WsGroupLookup makeWsGroupLookup(String group);

    public WsStemLookup makeWsStemLookup(String stemName);

    public WsStemLookup makeWsStemLookup(String stemName, String stemUuid);

    public WsStemSaveResults makeWsStemSaveResults(String username, String stemPath);

    public WsAttributeAssignValue makeWsAttributeAssignValue(String time);

    public WsAddMemberResults makeWsAddMemberResultsGroup(String groupPath, WsSubjectLookup lookup, String groupUid);

    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, String newMember);

    public WsFindGroupsResults makeWsFindGroupsResults(String groupPath);

    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, Person personToAdd);

    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, List<String> newMembers);

    public WsAddMemberResults makeWsAddMemberResults(String group, String newMember);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, String memberToDelete);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, String memberToDelete);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, Person personToDelete);

    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup,
            List<String> membersToDelete);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName);

    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName0,
            String attributeDefNameName1);

    public List<WsGetAttributeAssignmentsResults> makeWsGetAttributeAssignmentsResultsTrio(String assignType,
            String attributeDefNameName,
            List<String> ownerGroupNames);

    public List<WsGetAttributeAssignmentsResults> makeWsGetAttributeAssignmentsResultsTrio(String assignType,
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

    public WsHasMemberResults makeWsHasMemberResults(String group, Person person);

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

    public WsGetMembersResults makeWsGetMembersResults(String subjectAttributeName, WsSubjectLookup lookup,
            String groupName);

    public WsGetGroupsResults makeWsGetGroupsResults(String username, WsStemLookup stemLookup, StemScope stemScope);

    public WsAttributeAssign[] makeEmptyWsAttributeAssignArray();

    public WsGroupSaveResults addCompositeGroup(String username, String parentGroupPath, String compositeType, String leftGroupPath, String rightGroupPath);
}
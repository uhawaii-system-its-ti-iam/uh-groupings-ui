package edu.hawaii.its.holiday.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.hawaii.its.holiday.api.type.Group;
import edu.hawaii.its.holiday.api.type.Grouping;
import edu.hawaii.its.holiday.api.type.Person;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.type.Owner;
import edu.hawaii.its.holiday.util.Dates;
import edu.internet2.middleware.grouperClient.api.GcAddMember;
import edu.internet2.middleware.grouperClient.api.GcAssignAttributes;
import edu.internet2.middleware.grouperClient.api.GcAssignGrouperPrivileges;
import edu.internet2.middleware.grouperClient.api.GcDeleteMember;
import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.api.GcGetGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.api.GcGetGroups;
import edu.internet2.middleware.grouperClient.api.GcGetMembers;
import edu.internet2.middleware.grouperClient.api.GcGetMemberships;
import edu.internet2.middleware.grouperClient.api.GcHasMember;
import edu.internet2.middleware.grouperClient.ws.StemScope;

@Service
public class GroupingsService {

    public static final Log logger = LogFactory.getLog(GroupingsService.class);

    public static final String UUID_USERNAME = "ef62bf0473614b379695ecec6cb8b3b5";
    public static final String SELF_OPTED = "uh-settings:attributes:for-memberships:uh-grouping:self-opted";
    public static final String UUID_TRIO = "1d7365a23c994f5f83f7b541d4a5fa5e";
    public static final WsStemLookup STEM = new WsStemLookup("hawaii.edu:custom", null);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // public methods to be called from groupingsController
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Object[] addMember(String grouping, String username, String userToAdd) {
        Object[] results = new Object[5];

        WsSubjectLookup user = makeWsSubjectLookup(username);

        results[2] = removeSelfOpted(grouping + ":exclude", userToAdd);
        results[0] = addMemberAs(user, grouping + ":include", userToAdd);
        results[1] = deleteMemberAs(user, grouping + ":exclude", userToAdd);
        results[3] = updateLastModified(grouping + ":exclude");
        results[4] = updateLastModified(grouping + ":include");

        return results;
    }


    public Object[] deleteMember(String grouping, String username, String userToDelete) {
        Object[] results = new Object[5];

        WsSubjectLookup user = makeWsSubjectLookup(username);

        results[2] = removeSelfOpted(grouping + ":include", userToDelete);
        results[0] = deleteMemberAs(user, grouping + ":include", userToDelete);
        results[1] = addMemberAs(user, grouping + ":exclude", userToDelete);
        results[3] = updateLastModified(grouping + ":exclude");
        results[4] = updateLastModified(grouping + ":include");

        return results;
    }


    public Object[] assignOwnership(String grouping, String username, String newOwner) {
        WsAssignGrouperPrivilegesResults[] privilegeResults = new WsAssignGrouperPrivilegesResults[4];

        if (isOwner(grouping, username)) {
            WsSubjectLookup ownerToAdd = makeWsSubjectLookup(newOwner);

            WsGroupLookup includeGroupLookup = makeWsGroupLookup(grouping + ":include");
            WsGroupLookup basisGroupLookup = makeWsGroupLookup(grouping + ":basis");
            WsGroupLookup basisPlusIncludeGroupLookup = makeWsGroupLookup(grouping + ":basis+include");
            WsGroupLookup excludeGroupLookup = makeWsGroupLookup(grouping + ":exclude");

            privilegeResults[0] = addGroupOwnership(basisGroupLookup, ownerToAdd);
            privilegeResults[1] = addGroupOwnership(basisPlusIncludeGroupLookup, ownerToAdd);
            privilegeResults[2] = addGroupOwnership(excludeGroupLookup, ownerToAdd);
            privilegeResults[3] = addGroupOwnership(includeGroupLookup, ownerToAdd);

            return privilegeResults;
        } else {
            throw new AccessDeniedException("user does not have permission to update Grouping");
        }
        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }


    public Object[] removeOwnership(String grouping, String username, String ownerToRemove) {
        WsAssignGrouperPrivilegesResults[] privilegeResults = new WsAssignGrouperPrivilegesResults[4];

        if (isOwner(grouping, username)) {
            WsSubjectLookup ownerToRemoveLookup = makeWsSubjectLookup(ownerToRemove);

            WsGroupLookup includeGroupLookup = makeWsGroupLookup(grouping + ":include");
            WsGroupLookup basisGroupLookup = makeWsGroupLookup(grouping + ":basis");
            WsGroupLookup basisPlusIncludeGroupLookup = makeWsGroupLookup(grouping + ":basis+include");
            WsGroupLookup excludeGroupLookup = makeWsGroupLookup(grouping + ":exclude");

            privilegeResults[0] = removeGroupOwnership(basisGroupLookup, ownerToRemoveLookup);
            privilegeResults[1] = removeGroupOwnership(basisPlusIncludeGroupLookup, ownerToRemoveLookup);
            privilegeResults[2] = removeGroupOwnership(excludeGroupLookup, ownerToRemoveLookup);
            privilegeResults[3] = removeGroupOwnership(includeGroupLookup, ownerToRemoveLookup);

            return privilegeResults;
        } else {
            throw new AccessDeniedException("user does not have permission to update Grouping");
        }
        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }


    public Grouping getMembers(String grouping, String username) {
        WsSubjectLookup user = makeWsSubjectLookup(username);


        WsGetMembersResults basisResults = getMembersAs(user, grouping + ":basis");
        WsGetMembersResults basisPlusIncludeResults = getMembersAs(user, grouping + ":basis+include");
        WsGetMembersResults excludeResults = getMembersAs(user, grouping + ":exclude");
        WsGetMembersResults includeResults = getMembersAs(user, grouping + ":include");
        WsGetMembersResults basisPlusIncludeMinusExcludeResults = getMembersAs(user, grouping);

        Group includeGroup = makeGroup(includeResults.getResults()[0].getWsSubjects());
        Group excludeGroup = makeGroup(excludeResults.getResults()[0].getWsSubjects());
        Group basisGroup = makeGroup(basisResults.getResults()[0].getWsSubjects());
        Group basisPlusIncludeGroup = makeGroup(basisPlusIncludeResults.getResults()[0].getWsSubjects());
        Group basisPlusIncludeMinusExcludeGroup = makeGroup(basisPlusIncludeMinusExcludeResults.getResults()[0].getWsSubjects());
        Group owners = getOwners(grouping, username);

        Grouping members = new Grouping(basisGroup, basisPlusIncludeGroup, excludeGroup, includeGroup, basisPlusIncludeMinusExcludeGroup, owners);
        members.setPath(grouping);

        return members;
    }


    public Group getOwners(String grouping, String username) {
        logger.info("getOwners; grouping: " + grouping + "; username: " + username);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        String group = grouping + ":include";
        String privilegeName = "update";
        WsGetGrouperPrivilegesLiteResult privileges =
                new GcGetGrouperPrivilegesLite()
                        .assignActAsSubject(lookup)
                        .assignGroupName(group)
                        .assignPrivilegeName(privilegeName)
                        .addSubjectAttributeName("uid")
                        .execute();
        ArrayList<WsSubject> subjects = new ArrayList<>();

        for (WsGrouperPrivilegeResult result : privileges.getPrivilegeResults()) {
            subjects.add(result.getOwnerSubject());
        }

        return makeGroup(subjects.toArray(new WsSubject[subjects.size()]));
    }


    public List<Grouping> groupingsIn(String username) {
        String[] groupsIn = getGroupNames(username);
        List<String> groupingPaths = extractGroupings(groupsIn);

        return getGroupings(groupingPaths);
    }


    public List<Grouping> groupingsOwned(String username) {
        WsSubjectLookup user = makeWsSubjectLookup(username);
        WsGetGrouperPrivilegesLiteResult getPrivilegeResults = new GcGetGrouperPrivilegesLite().assignPrivilegeName("update").assignSubjectLookup(user).execute();
        WsGrouperPrivilegeResult[] privilegeResults = getPrivilegeResults.getPrivilegeResults();
        WsGroup[] groups = new WsGroup[privilegeResults.length];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = privilegeResults[i].getWsGroup();
        }
        ArrayList<String> names = extractGroupNames(groups);
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).endsWith(":include")) {
                names.set(i, names.get(i).split(":include")[0]);
            } else if (names.get(i).endsWith(":exclude")) {
                names.set(i, names.get(i).split(":exclude")[0]);
            }
        }
        List<String> paths = extractGroupings(names.toArray(new String[names.size()]));
        return getGroupings(paths);
    }


    public List<Grouping> groupingsToOptOutOf(String username) {
        ArrayList<String> groupingPaths = new ArrayList<>();
        List<Grouping> groupingsIn = groupingsIn(username);
        for (Grouping grouping : groupingsIn) {
            if (optOutPermission(username, grouping.getPath())) {
                groupingPaths.add(grouping.getPath());
            }
        }
        return getGroupings(groupingPaths);
    }


    public List<Grouping> groupingsToOptInto(String username) {
        ArrayList<String> groupingsPaths = new ArrayList<>();
        List<Grouping> groupingsIn = groupingsIn(username);
        for (Grouping grouping : groupingsIn) {
            if (optInPermission(username, grouping.getPath())) {
                groupingsPaths.add(grouping.getPath());
            }
        }
        return getGroupings(groupingsPaths);
    }


    public Object[] optIn(String username, String grouping) {
        Object[] results = new Object[6];

        if (groupOptInPermission(username, grouping + ":include")
                && (!inGroup(grouping + ":exclude", username)
                || groupOptOutPermission(username, grouping + ":exclude"))) {

            results[3] = removeSelfOpted(grouping + ":exclude", username);
            results[0] = deleteMemberFromGroup(grouping + ":exclude", username);
            results[1] = addMemberToGroup(grouping + ":include", username);
            results[4] = updateLastModified(grouping + ":exclude");
            results[5] = updateLastModified(grouping + ":include");
            results[2] = addSelfOpted(grouping + ":include", username);

            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt into this Grouping");
        }
    }


    public Object[] optOut(String username, String grouping) {
        Object[] results = new Object[6];

        if (groupOptInPermission(username, grouping + ":exclude") && (!inGroup(grouping + ":include", username)
                || groupOptOutPermission(username, grouping + ":include"))) {

            results[3] = removeSelfOpted(grouping + ":include", username);
            results[0] = deleteMemberFromGroup(grouping + ":include", username);
            results[1] = addMemberToGroup(grouping + ":exclude", username);
            results[4] = updateLastModified(grouping + ":exclude");
            results[5] = updateLastModified(grouping + ":include");
            results[2] = addSelfOpted(grouping + ":exclude", username);

            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt out of this Grouping");
        }
    }


    public Object[] cancelOptIn(String grouping, String username) {
        Object[] results = new Object[3];
        String group = grouping + ":include";

        if (inGroup(group, username)) {

            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(lookup, group);
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            if (checkSelfOpted(group, lookup)) {

                String privilegeName = "optout";
                WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                        grouperPrivilegesLite(username, group, privilegeName);

                if (wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED")) {
                    String operation = "remove_attr";
                    String uuid = GroupingsService.UUID_USERNAME;
                    results[1] = assignAttributesResults(operation, uuid, membershipID);
                    results[0] = deleteMemberFromGroup(group, username);
                    results[2] = updateLastModified(group);

                    return results;
                } else {
                    throw new AccessDeniedException("user is not allowed to opt out of 'include' group");
                }

            } else {
                throw new IllegalStateException("user is in include group, but cannot cancel opt in, because user did not opt in");
            }
        } else {
            results[0] = "Success, user is not opted in, because user was not in 'include' group";
        }
        return results;
    }


    public Object[] cancelOptOut(String grouping, String username) {
        Object[] results = new Object[3];
        String group = grouping + ":exclude";

        if (inGroup(group, username)) {

            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(lookup, group);
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            if (checkSelfOpted(group, lookup)) {

                String privilegeName = "optout";
                WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                        grouperPrivilegesLite(lookup.getSubjectIdentifier(), group, privilegeName);

                if (wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED")) {
                    String operation = "remove_attr";
                    String uuid = GroupingsService.UUID_USERNAME;
                    results[1] = assignAttributesResults(operation, uuid, membershipID);
                    results[0] = deleteMemberFromGroup(group, username);

                    results[2] = updateLastModified(group);

                    return results;
                } else {
                    throw new AccessDeniedException("user is not allowed to opt out of 'exclude' group");
                }
            } else {
                throw new IllegalStateException("user is in exclude group, but cannot cancel opt out, because user did not opt out");
            }
        } else {
            results[0] = "Success, user is not opted out, because user was not in 'exclude' group";
        }
        return results;
    }


    public boolean optOutPermission(String username, String grouping) {
        //a user can opt out of a Grouping if:
        //      they have permission to opt into the exclude group,
        //      and
        //          they are not in the include group
        //          or
        //          they have permission to opt out of the include group

        return (groupOptInPermission(username, grouping + ":exclude") && (!inGroup(grouping + ":include", username)
                || groupOptOutPermission(username, grouping + ":include")));
    }


    public boolean optInPermission(String username, String grouping) {
        //a user can opt into a Grouping if:
        //      they have permission to opt into the include group,
        //      and
        //          they are not in the exclude group
        //          or
        //          they have permission to opt out of the exclude group

        return (groupOptInPermission(username, grouping + ":include") && (!inGroup(grouping + ":exclude", username)
                || groupOptOutPermission(username, grouping + ":exclude")));
    }


    public boolean hasListServe(String grouping) throws NullPointerException {

        String assignType = "group";
        String nameName = "uh-settings:attributes:for-groups:uh-grouping:destinations:listserv";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                attributeAssignmentsResults(assignType, grouping, nameName);

        WsAttributeAssign listServeAttriubte = wsGetAttributeAssignmentsResults.getWsAttributeAssigns()[0];
        return listServeAttriubte.getAttributeDefNameName().equals(nameName);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //todo make all helper methods public
    //todo change tests for public methods

    /**
     * adds the self-opted attribute to a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    public WsAssignAttributesResults addSelfOpted(String group, String username) {
        logger.info("addSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            if (!checkSelfOpted(group, lookup)) {
                WsGetMembershipsResults includeMembershipsResults = membershipsResults(lookup, group);
                String membershipID = includeMembershipsResults.getWsMemberships()[0].getMembershipId();
                String operation = "assign_attr";
                return assignAttributesResults(operation, UUID_USERNAME, membershipID);
            }
        }
        return new WsAssignAttributesResults();
    }

    /**
     * @param group:  group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param lookup: WsSubjectLookup of user
     * @return true if the membership between the user and the group has the "self-opted" attribute
     */
    public boolean checkSelfOpted(String group, WsSubjectLookup lookup) {
        logger.info("checkSelfOpted; group: " + group + "; wsSubjectLookup: " + lookup);

        if (inGroup(group, lookup.getSubjectIdentifier())) {
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(lookup, group);
            String assignType = "imm_mem";
            String uuid = UUID_USERNAME;
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            WsAttributeAssign[] wsAttributes = attributeAssign(assignType, uuid, membershipID);
            for (WsAttributeAssign att : wsAttributes) {
                if (att.getAttributeDefNameName().equals(SELF_OPTED)) {
                    return true; // We are done, get out.
                }
            }
        }
        return false;
    }

    /**
     * @param group:    group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param username: subjectIdentifier of user to be searched for
     * @return true if username is a member of group
     */
    public boolean inGroup(String group, String username) {
        logger.info("inGroup; group: " + group + "; username: " + username);

        WsHasMemberResults wsHasMemberResults =
                new GcHasMember()
                        .assignGroupName(group)
                        .addSubjectIdentifier(username)
                        .execute();
        WsHasMemberResult[] memberResultArray = wsHasMemberResults.getResults();

        boolean userIsInGroup = false;
        for (WsHasMemberResult hasMember : memberResultArray) {
            if (hasMember.getResultMetadata().getResultCode().equals("IS_MEMBER")) {
                userIsInGroup = true;
                break; // Found it, break out.
            }
        }
        return userIsInGroup;
    }

    public boolean isOwner(String grouping, String username) {
        logger.info("isOwner; grouping: " + grouping + "; username: " + username);

        if (username == null) {
            return false;
        }

        WsSubjectLookup lookup = makeWsSubjectLookup(username);

        WsGetGrouperPrivilegesLiteResult privilegeResults =
                new GcGetGrouperPrivilegesLite()
                        .assignSubjectLookup(lookup)
                        .assignPrivilegeName("update")
                        .assignPrivilegeName("read")
                        .assignGroupName(grouping + ":include")
                        .assignGroupName(grouping + ":include")
                        .addSubjectAttributeName("uid")
                        .execute();

        for (WsGrouperPrivilegeResult owner : privilegeResults.getPrivilegeResults()) {
            String values[] = owner.getOwnerSubject().getAttributeValues();
            if (values != null && values.length > 0) {
                if (username.equals(owner.getOwnerSubject().getAttributeValue(0))) {
                    return true; // Found it, get out.
                }
            }
        }

        return false;
    }

    /**
     * removes the self-opted attribute from a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    public WsAssignAttributesResults removeSelfOpted(String group, String username) {
        logger.info("removeSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            if (checkSelfOpted(group, lookup)) {
                WsGetMembershipsResults getIncludeMembershipsResults = membershipsResults(lookup, group);
                String membershipID = getIncludeMembershipsResults.getWsMemberships()[0].getMembershipId();
                String operation = "remove_attr";
                return assignAttributesResults(operation, UUID_USERNAME, membershipID);
            }
        }
        return new WsAssignAttributesResults();
    }

    /**
     * @return date and time in yyyymmddThhmm format
     * ex. 20170314T0923
     */
    public String wsDateTime() {
        return Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");
    }

    /**
     * checks for permission to opt out of a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt out, false if not
     */
    public boolean groupOptOutPermission(String username, String group) {
        logger.info("groupOptOutPermission; group: " + group + "; username: " + username);
        String privilegeName = "optout";
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, group, privilegeName);

        return result
                .getResultMetadata()
                .getResultCode()
                .equals("SUCCESS_ALLOWED");
    }

    /**
     * checks for permission to opt into a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt in, false if not
     */
    public boolean groupOptInPermission(String username, String group) {
        logger.info("groupOptInPermission; group: " + group + "; username: " + username);
        String privilegeName = "optin";
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, group, privilegeName);

        return result
                .getResultMetadata()
                .getResultCode()
                .equals("SUCCESS_ALLOWED");
    }

    /**
     * updates the last modified time of a group
     * this should be done whenever a group is modified
     * <p>
     * ie. a member was added or deleted
     *
     * @param group: group whos last modified attribute will be updated
     * @return results from Grouper Web Service
     */
    public WsAssignAttributesResults updateLastModified(String group) {
        logger.info("updateLastModified; group: " + group);

        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(wsDateTime());

        return new GcAssignAttributes()
                .assignAttributeAssignType("group")
                .assignAttributeAssignOperation("assign_attr")
                .addOwnerGroupName(group)
                .addAttributeDefNameName("uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm")
                .assignAttributeAssignValueOperation("replace_values")
                .addValue(dateTimeValue)
                .execute();

    }

    // Helper method.
    public WsSubjectLookup makeWsSubjectLookup(String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        return wsSubjectLookup;
    }

    // Helper method.
    public WsGroupLookup makeWsGroupLookup(String group) {
        WsGroupLookup groupLookup = new WsGroupLookup();
        groupLookup.setGroupName(group);

        return groupLookup;
    }

    // Helper method.
    public WsAssignAttributesResults assignAttributesResults(String operation, String uuid, String membershipID) {
        logger.info("assignAttributesResults; operation: " + operation + "; uuid: " + uuid + "; membershipID: " + membershipID);

        String assignType = "imm_mem";
        return new GcAssignAttributes()
                .assignAttributeAssignType(assignType)
                .assignAttributeAssignOperation(operation)
                .addAttributeDefNameUuid(uuid)
                .addOwnerMembershipId(membershipID)
                .execute();
    }

    // Helper method.
    public WsAttributeAssign[] attributeAssign(String assignType, String uuid, String membershipID) {
        logger.info("attribureAssign; assignType: " + assignType + "; uuid: " + uuid + "; membershipID: " + membershipID);

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                new GcGetAttributeAssignments()
                        .assignAttributeAssignType(assignType)
                        .addAttributeDefNameUuid(uuid)
                        .addOwnerMembershipId(membershipID)
                        .execute();
        WsAttributeAssign[] wsAttributes = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();

        return wsAttributes != null ? wsAttributes : new WsAttributeAssign[0];
    }

    // Helper method.
    public WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String group, String nameName) {
        logger.info("attributeAssignmentsResults; assignType: " + assignType + "; group: " + group + "; nameName: " + nameName);

        return new GcGetAttributeAssignments().assignAttributeAssignType(assignType)
                .addOwnerGroupName(group)
                .addAttributeDefNameName(nameName)
                .execute();
    }

    // Helper method.
    public WsGetAttributeAssignmentsResults attributeAssignments(String assignType, String subjectAttributeName, String uuid) {
        logger.info("attributeAssignments; assignType: " + assignType + "; subjectAttribureName: " + subjectAttributeName + "; uuid: " + uuid);

        return new GcGetAttributeAssignments()
                .assignAttributeAssignType(assignType)
                .addSubjectAttributeName(subjectAttributeName)
                .addAttributeDefUuid(uuid)
                .execute();
    }

    // Helper method.
    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String group, String privilegeName) {
        logger.info("grouperPrivlegesLite; username: " + username + "; group: " + group + "; privilegeName: " + privilegeName);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        return new GcGetGrouperPrivilegesLite()
                .assignGroupName(group)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .execute();
    }

    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String privilegeName) {
        logger.info("grouperPrivlegesLite; username: " + username + "; privilegeName: " + privilegeName);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        return new GcGetGrouperPrivilegesLite()
                .assignSubjectLookup(lookup)
                .assignPrivilegeName(privilegeName)
                .execute();
    }

    // Helper method.
    public WsGetMembershipsResults membershipsResults(WsSubjectLookup lookup, String group) {
        logger.info("membershipResults; lookup: " + lookup + "; group: " + group);

        return new GcGetMemberships()
                .addWsSubjectLookup(lookup)
                .addGroupName(group)
                .execute();
    }

    // Helper method.
    public WsAddMemberResults addMemberAs(WsSubjectLookup user, String group, String userToAdd) {
        logger.info("addMemberAs; user: " + user + "; group: " + group + "; userToAdd: " + userToAdd);

        return new GcAddMember()
                .assignActAsSubject(user)
                .assignGroupName(group)
                .addSubjectIdentifier(userToAdd)
                .execute();
    }

    // Helper method.
    public WsDeleteMemberResults deleteMemberAs(WsSubjectLookup user, String group, String userToDelete) {
        logger.info("delteMemberAs; user: " + user + "; group: " + group + "; userToDelete: " + userToDelete);

        return new GcDeleteMember()
                .assignActAsSubject(user)
                .assignGroupName(group)
                .addSubjectIdentifier(userToDelete)
                .execute();
    }

    // Helper method.
    public WsDeleteMemberResults deleteMemberFromGroup(String group, String user) {
        logger.info("deleteMemberFromGroup; group: " + group + "; user: " + user);

        return new GcDeleteMember()
                .assignGroupName(group)
                .addSubjectIdentifier(user)
                .execute();
    }

    // Helper method.
    public WsAddMemberResults addMemberToGroup(String group, String user) {
        logger.info("addMemberToGroup; group: " + group + "; user: " + user);

        return new GcAddMember()
                .assignGroupName(group)
                .addSubjectIdentifier(user)
                .execute();
    }

    // Helper method.
    public WsGetMembersResults getMembersAs(WsSubjectLookup user, String group) {
        logger.info("getMembersAs; user: " + user + "; group: " + group);

        return new GcGetMembers()
                .assignActAsSubject(user)
                .addSubjectAttributeName("uid")
                .addGroupName(group)
                .assignIncludeSubjectDetail(true)
                .execute();
    }

    // Helper method.
    public ArrayList<String> allGroupings() {
        String uuid = UUID_TRIO;
        String assignType = "group";
        String subjectAttributeName = "uh-settings:attributes:for-groups:uh-grouping:is-trio";
        ArrayList<String> trios = new ArrayList<>();

        WsGetAttributeAssignmentsResults groupings = attributeAssignments(assignType, subjectAttributeName, uuid);

        for (WsGroup aTrio : groupings.getWsGroups()) {
            trios.add(aTrio.getName());
        }

        return trios;
    }


    // Helper method.
    public ArrayList<String> extractGroupings(String[] groups) {
        ArrayList<String> allGroupings = allGroupings();
        ArrayList<String> groupings = new ArrayList<>();

        for (String name : groups) {
            if (allGroupings.contains(name) && !groupings.contains(name)) {
                groupings.add(name);
            }
        }

        return groupings;
    }


    // Helper method.
    public String[] getGroupNames(String username) {
        ArrayList<String> names;

        WsGetGroupsResults wsGetGroupsResults = new GcGetGroups()
                .addSubjectIdentifier(username)
                .assignWsStemLookup(STEM)
                .assignStemScope(StemScope.ALL_IN_SUBTREE)
                .execute();

        WsGetGroupsResult groupResults = wsGetGroupsResults.getResults()[0];
        WsGroup[] groups = groupResults.getWsGroups();

        names = extractGroupNames(groups);
        return names.toArray(new String[names.size()]);
    }

    //Helper method
    public List<Grouping> getGroupings(List<String> groupingPaths){
        return groupingPaths.stream().map(Grouping::new).collect(Collectors.toList());
    }

    // Helper method.
    public ArrayList<String> extractGroupNames(WsGroup[] groups) {
        ArrayList<String> names = new ArrayList<>();

        for (WsGroup group : groups) {
            if (!names.contains(group.getName())) {
                names.add(group.getName());
            }
        }

        return names;
    }

    // Helper method.
    public WsAssignGrouperPrivilegesResults removeGroupOwnership(WsGroupLookup group, WsSubjectLookup ownerToRemove) {
        logger.info("removeGroupOwnership; group: " + group + "; ownerToRemove: " + ownerToRemove);

        return new GcAssignGrouperPrivileges()
                .assignGroupLookup(group)
                .addSubjectLookup(ownerToRemove)
                .addPrivilegeName("admin")
                .addPrivilegeName("update")
                .addPrivilegeName("read")
                .assignAllowed(false)
                .execute();
    }

    // Helper method.
    public WsAssignGrouperPrivilegesResults addGroupOwnership(WsGroupLookup group, WsSubjectLookup ownerToAdd) {
        logger.info("addGroupOwnership; group: " + group + "; ownerToAdd: " + ownerToAdd);

        return new GcAssignGrouperPrivileges()
                .assignGroupLookup(group)
                .addSubjectLookup(ownerToAdd)
                .addPrivilegeName("update")
                .addPrivilegeName("read")
                .assignAllowed(true)
                .execute();
    }

    //Helper method
    public Group makeGroup(WsSubject[] subjects){
        Group group = new Group();
        ArrayList<Person> persons = new ArrayList<>();
        try {
            for (WsSubject subject : subjects) {
                persons.add(makePerson(subject));
            }
            group.setMembers(persons);
        }
        catch(NullPointerException npe){
        }
        return group;
    }

    //Helper method
    public Person makePerson(WsSubject person){
        String name = person.getName();
        String uuid = person.getId();
        String username = person.getAttributeValue(0);
        return new Person(name, uuid, username);
    }

    // ////////////////////////////////////////////////////////////////////////
    // Start of reworking.

    public List<Owner> findOwners(String username, String groupingName) {
        logger.info("findOwners; username: " + username + "; groupingName" + groupingName);

        List<Owner> owners = new ArrayList<>();

        try {
            WsGetGrouperPrivilegesLiteResult results =
                    new GcGetGrouperPrivilegesLite()
                            .assignActAsSubject(makeWsSubjectLookup(username))
                            .assignGroupName(groupingName + ":include")
                            .assignPrivilegeName("update")
                            .addSubjectAttributeName("uid")
                            .execute();

            WsGrouperPrivilegeResult[] privilegeResults = results.getPrivilegeResults();
            if (privilegeResults != null && privilegeResults.length > 0) {
                for (WsGrouperPrivilegeResult o : privilegeResults) {
                    Owner owner = new Owner(o.getPrivilegeName());
                    owner.setName(o.getOwnerSubject().getName());
                    owner.setUhuuid(o.getOwnerSubject().getId());
                    owner.setUid(o.getWsSubject().getAttributeValues()[0]);
                    owners.add(owner);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return owners;
    }

}

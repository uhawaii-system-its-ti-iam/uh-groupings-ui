package edu.hawaii.its.groupings.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.hawaii.its.groupings.api.type.*;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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

@Service("groupingsService")
public class GroupingsServiceImpl implements GroupingsService {

    public static final Log logger = LogFactory.getLog(GroupingsServiceImpl.class);

    public static final String UUID_USERNAME = "ef62bf0473614b379695ecec6cb8b3b5";
    private static final String SETTINGS = "uh-settings";
    private static final String ATTRIBUTES = SETTINGS + ":attributes";
    private static final String UHGROUPING = "uh-settings:attributes:for-groups:uh-grouping";
    public static final String SELF_OPTED = ATTRIBUTES + ":for-memberships:uh-grouping:self-opted";
    public static final String UUID_TRIO = "1d7365a23c994f5f83f7b541d4a5fa5e";
    public static final String EXCLUDE = ":exclude";
    public static final String INCLUDE = ":include";
    public static final WsStemLookup STEM = new WsStemLookup("hawaii.edu:custom", null);

    /**
     * gives a user ownersip permissions for a Grouping
     *
     * @param grouping: the Grouping that the user will get ownership permissions for
     * @param username: the owner of the Grouping who will give ownership permissions to the new owner
     * @param newOwner: the user that will become an owner of the Grouping
     * @return information about the success of the operation
     */
    @Override
    public GrouperActionResult[] assignOwnership(String grouping, String username, String newOwner) {

        if (isOwner(grouping, username)) {
            WsSubjectLookup ownerToAdd = makeWsSubjectLookup(newOwner);
            GrouperActionResult[] privilegeResults = new GrouperActionResult[4];

            WsGroupLookup basisGroupLookup = makeWsGroupLookup(grouping + ":basis");
            privilegeResults[0] = makeGrouperActionResult(addGroupOwnership(basisGroupLookup, ownerToAdd), "add ownership to basis");

            WsGroupLookup basisPlusIncludeGroupLookup = makeWsGroupLookup(grouping + ":basis+include");
            privilegeResults[1] = makeGrouperActionResult(addGroupOwnership(basisPlusIncludeGroupLookup, ownerToAdd), "add ownership to basis+include");

            WsGroupLookup excludeGroupLookup = makeWsGroupLookup(grouping + EXCLUDE);
            privilegeResults[2] = makeGrouperActionResult(addGroupOwnership(excludeGroupLookup, ownerToAdd), "add ownership to exclude");

            WsGroupLookup includeGroupLookup = makeWsGroupLookup(grouping + INCLUDE);
            privilegeResults[3] = makeGrouperActionResult(addGroupOwnership(includeGroupLookup, ownerToAdd), "add ownership to include");

            return privilegeResults;
        }

        throw new AccessDeniedException("user does not have permission to update Grouping");

        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }

    @Override
    public String changeListServeStatus(String grouping, String username, boolean listServeOn) {
        String attributeName = UHGROUPING + ":destinations:listserv";

        return changeGroupAttributeStatus(grouping, username, attributeName, listServeOn);
    }

    @Override
    public String changeOptInStatus(String grouping, String username, boolean optInOn) {
        String attributeName = UHGROUPING + ":anyone-can:opt-in";

        return changeGroupAttributeStatus(grouping, username, attributeName, optInOn);
    }

    @Override
    public String changeOptOutStatus(String grouping, String username, boolean optOutOn) {
        String attributeName = UHGROUPING + ":anyone-can:opt-out";

        return changeGroupAttributeStatus(grouping, username, attributeName, optOutOn);
    }

    /**
     * removes ownership permissions from a user
     *
     * @param grouping:      the Grouping for which the user's ownership permissions will be removed
     * @param username:      the owner of the Grouping who will be removing ownership permissions from the owner to be removed
     * @param ownerToRemove: the owner who will have ownership privilages removed
     * @return information about the success of the operation
     */
    @Override
    public GrouperActionResult[] removeOwnership(String grouping, String username, String ownerToRemove) {

        if (isOwner(grouping, username)) {
            GrouperActionResult[] privileges = new GrouperActionResult[4];

            WsSubjectLookup ownerToRemoveLookup = makeWsSubjectLookup(ownerToRemove);
            WsGroupLookup basisGroupLookup = makeWsGroupLookup(grouping + ":basis");
            privileges[0] = makeGrouperActionResult(removeGroupOwnership(basisGroupLookup, ownerToRemoveLookup), "remove ownership from basis");

            WsGroupLookup basisPlusIncludeGroupLookup = makeWsGroupLookup(grouping + ":basis+include");
            privileges[1] = makeGrouperActionResult(removeGroupOwnership(basisPlusIncludeGroupLookup, ownerToRemoveLookup), "remove ownership from basis+include");

            WsGroupLookup excludeGroupLookup = makeWsGroupLookup(grouping + EXCLUDE);
            privileges[2] = makeGrouperActionResult(removeGroupOwnership(excludeGroupLookup, ownerToRemoveLookup), "remove ownership from exclude");

            WsGroupLookup includeGroupLookup = makeWsGroupLookup(grouping + INCLUDE);
            privileges[3] = makeGrouperActionResult(removeGroupOwnership(includeGroupLookup, ownerToRemoveLookup), "remove ownership from include");

            return privileges;
        }

        throw new AccessDeniedException("user does not have permission to update Grouping");

        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }

    /**
     * @param grouping: the path of the Grouping to be searched for
     * @param username: the user who is doing the search
     * @return a Grouping Object containing information about the Grouping
     */
    @Override
    public Grouping getGrouping(String grouping, String username) {
        WsSubjectLookup user = makeWsSubjectLookup(username);

        Group includeGroup = new Group();
        WsGetMembersResults includeResults = getMembers(user, grouping + INCLUDE);
        if (includeResults.getResults() != null) {
            includeGroup = makeGroup(includeResults.getResults()[0].getWsSubjects());
        }

        Group excludeGroup = new Group();
        WsGetMembersResults excludeResults = getMembers(user, grouping + EXCLUDE);
        if (excludeResults.getResults() != null) {
            excludeGroup = makeGroup(excludeResults.getResults()[0].getWsSubjects());
        }

        Group basisGroup = new Group();
        WsGetMembersResults basisResults = getMembers(user, grouping + ":basis");
        if (basisResults.getResults() != null) {
            basisGroup = makeGroup(basisResults.getResults()[0].getWsSubjects());
        }

        Group basisPlusIncludeMinusExcludeGroup = new Group();
        WsGetMembersResults basisPlusIncludeMinusExcludeResults = getMembers(user, grouping);
        if (basisPlusIncludeMinusExcludeResults.getResults() != null) {
            basisPlusIncludeMinusExcludeGroup = makeGroup(basisPlusIncludeMinusExcludeResults.getResults()[0].getWsSubjects());
        }

        Grouping members = new Grouping(grouping);
        members.setBasis(basisGroup);
        members.setExclude(excludeGroup);
        members.setInclude(includeGroup);
        members.setBasisPlusIncludeMinusExclude(basisPlusIncludeMinusExcludeGroup);
        members.setOwners(findOwners(grouping, username));
        members.setHasListserv(hasListserv(grouping));

        return members;
    }

    /**
     * @param username: username of the user to display Groupings for
     * @return the Groupigns that the user
     * is in
     * owns
     * can opt into
     * can opt out of
     */
    @Override
    public MyGroupings getMyGroupings(String username) {
        MyGroupings myGroupings = new MyGroupings();

        myGroupings.setGroupingsIn(groupingsIn(username));
        myGroupings.setGroupingsOwned(groupingsOwned(username));
        myGroupings.setGroupingsToOptInTo(groupingsToOptInto(username));
        myGroupings.setGroupingsToOptOutOf(groupingsToOptOutOf(username));

        return myGroupings;
    }

    /**
     * if a user has permission to opt into a Grouping
     * this will put them in the include group
     * if they are in the exclude group, they will be removed from it
     *
     * @param username: user to be opting in
     * @param grouping: Grouping the user will opt into
     * @return information about the success of the operation
     */
    @Override
    public GrouperActionResult[] optIn(String username, String grouping) {

        if (groupOptInPermission(username, grouping + INCLUDE)) {
            GrouperActionResult[] results = new GrouperActionResult[6];
            results[3] = removeSelfOpted(grouping + EXCLUDE, username);
            results[0] = makeGrouperActionResult(deleteMemberAs(username, grouping + EXCLUDE, username), "delete member from exclude group");
            results[1] = makeGrouperActionResult(addMemberAs(username, grouping + INCLUDE, username), "add member to include group");
            results[4] = makeGrouperActionResult(updateLastModified(grouping + EXCLUDE), "update last-modified attribute for exclude group");
            results[5] = makeGrouperActionResult(updateLastModified(grouping + INCLUDE), "update last-modified attribute for include group");
            results[2] = addSelfOpted(grouping + INCLUDE, username);

            return results;
        }

        throw new AccessDeniedException("user is not allowed to opt into this Grouping");
    }

    /**
     * if a user has permission to opt out of a Grouping
     * this will put them in the exclude group
     * if they are in the include group, they will be removed from it
     *
     * @param username: user to be opting out
     * @param grouping: Grouping the user will opt out of
     * @return information about the success of the operation
     */
    @Override
    public GrouperActionResult[] optOut(String username, String grouping) {

        if (groupOptInPermission(username, grouping + EXCLUDE)) {
            GrouperActionResult[] results = new GrouperActionResult[6];
            results[3] = removeSelfOpted(grouping + INCLUDE, username);
            results[0] = makeGrouperActionResult(deleteMemberAs(username, grouping + INCLUDE, username), "delete member from include group");
            results[1] = makeGrouperActionResult(addMemberAs(username, grouping + EXCLUDE, username), "add member to exclude group");
            results[4] = makeGrouperActionResult(updateLastModified(grouping + EXCLUDE), "update last-modified attribute for exclude group");
            results[5] = makeGrouperActionResult(updateLastModified(grouping + INCLUDE), "update last-modified attribute for include group");
            results[2] = addSelfOpted(grouping + EXCLUDE, username);

            return results;
        }

        throw new AccessDeniedException("user is not allowed to opt out of this Grouping");
    }

    /**
     * if the user has opted into a Grouping, this will remove them from the include group
     *
     * @param grouping: the path to the Grouping that the user is opted into
     * @param username: username of the user canceling optIn
     * @return information about the success of the operation
     */
    @Override
    public GrouperActionResult[] cancelOptIn(String grouping, String username) {
        GrouperActionResult[] results = new GrouperActionResult[3];
        String group = grouping + INCLUDE;

        if (inGroup(group, username)) {
            if (groupOptInPermission(username, group)) {
                results[0] = makeGrouperActionResult(deleteMemberAs(username, group, username), "delete member from include group");
                results[2] = makeGrouperActionResult(updateLastModified(group), "update last-modified attribute for include group");

                return results;
            } else {
                throw new AccessDeniedException("user is not allowed to opt out of 'include' group");
            }
        } else {
            GrouperActionResult noOptIn = new GrouperActionResult();
            noOptIn.setAction("cancel opt in");
            noOptIn.setResultCode("SUCCESS, user is not opted in, because user was not in 'include' group");
            results[0] = noOptIn;
        }

        return results;
    }

    /**
     * if the user has opted out of a Grouping, this will remove them from the exclude group
     *
     * @param grouping: the path to the Grouping that the user is opted out of
     * @param username: username of the user canceling optOut
     * @return information about the success of the operation
     */
    @Override
    public GrouperActionResult[] cancelOptOut(String grouping, String username) {
        GrouperActionResult[] results = new GrouperActionResult[3];
        String group = grouping + EXCLUDE;

        if (inGroup(group, username)) {
            if (groupOptOutPermission(username, group)) {
                results[0] = makeGrouperActionResult(deleteMemberAs(username, group, username), "delete memeber from exclude group");
                results[2] = makeGrouperActionResult(updateLastModified(group), "update last-modified attribute for exclude group");

                return results;
            } else {
                throw new AccessDeniedException("user is not allowed to opt out of 'exclude' group");
            }
        } else {
            GrouperActionResult noOptOut = new GrouperActionResult();
            noOptOut.setAction("cancel opt out");
            noOptOut.setResultCode("SUCCESS, user is not opted in, because user was not in 'exclude' group");
            results[0] = noOptOut;
        }
        return results;
    }

    /**
     * @param grouping: the path of the Grouping to be searched for
     * @param username: the user doing the search
     * @return a Group of owners for the Grouping
     */
    @Override
    public Group findOwners(String grouping, String username) {
        logger.info("getOwners; grouping: " + grouping + "; username: " + username);

        Group owners = new Group();

        if (isOwner(grouping, username)) {
            String group = grouping + INCLUDE;
            String privilegeName = "update";
            WsGetGrouperPrivilegesLiteResult privileges =
                    new GcGetGrouperPrivilegesLite()
                            .assignGroupName(group)
                            .assignPrivilegeName(privilegeName)
                            .addSubjectAttributeName("uid")
                            .execute();
            List<WsSubject> subjects = new ArrayList<>();

            if (privileges.getPrivilegeResults() != null) {
                for (WsGrouperPrivilegeResult result : privileges.getPrivilegeResults()) {
                    subjects.add(result.getOwnerSubject());
                }

                owners = makeGroup(subjects.toArray(new WsSubject[subjects.size()]));
            }
        }

        return owners;
    }

    @Override
    public boolean optOutPermission(String grouping) {
        String nameName = UHGROUPING + ":anyone-can:opt-out";

        return groupHasAttribute(grouping, nameName);
    }

    @Override
    public boolean optInPermission(String grouping) {
        String nameName = UHGROUPING + ":anyone-can:opt-in";

        return groupHasAttribute(grouping, nameName);
    }

    public boolean groupHasAttribute(String grouping, String nameName) {

        String assignType = "group";
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                attributeAssignmentsResults(assignType, grouping, nameName);
        if (wsGetAttributeAssignmentsResults.getWsAttributeAssigns() != null) {
            for (WsAttributeAssign attribute : wsGetAttributeAssignmentsResults.getWsAttributeAssigns()) {
                if (attribute.getAttributeDefNameName().equals(nameName)) {
                    return true; // Found it, get out now.
                }
            }
        }

        return false;
    }

    @Override
    public List<Grouping> groupingsIn(String username) {
        List<String> groupsIn = getGroupNames(username);
        List<String> groupingPaths = extractGroupings(groupsIn);

        return makeGroupings(groupingPaths);
    }

    @Override
    public boolean hasListserv(String grouping) {

        String nameName = UHGROUPING + ":destinations:listserv";

        return groupHasAttribute(grouping, nameName);
    }

    public List<Grouping> groupingsOwned(String username) {
        WsGetGrouperPrivilegesLiteResult getPrivilegesResult =
                new GcGetGrouperPrivilegesLite()
                        .assignPrivilegeName("update")
                        .assignSubjectLookup(makeWsSubjectLookup(username))
                        .execute();
        WsGrouperPrivilegeResult[] privilegeResults = getPrivilegesResult.getPrivilegeResults();
        if (privilegeResults != null) {
            if (privilegeResults.length > 0) {
                List<WsGroup> groups = new ArrayList<>();
                for (WsGrouperPrivilegeResult privilegeResult : privilegeResults) {
                    groups.add(privilegeResult.getWsGroup());

                }
                List<String> names = extractGroupNames(groups);
                for (int i = 0; i < names.size(); i++) {
                    if (names.get(i).endsWith(INCLUDE)) {
                        names.set(i, names.get(i).split(INCLUDE)[0]);
                    } else if (names.get(i).endsWith(EXCLUDE)) {
                        names.set(i, names.get(i).split(EXCLUDE)[0]);
                    }
                }

                return makeGroupings(extractGroupings(names));
            }
        }

        return new ArrayList<>();
    }

    public List<Grouping> groupingsToOptOutOf(String username) {
        WsGetGrouperPrivilegesLiteResult optinResults = grouperPrivilegesLite(username, "optout");

        List<String> groupingNames = groupingNamesFromPrivilegeResults(optinResults);
        List<String> groupings = groupingNames
                .stream()
                .filter(this::optOutPermission)
                .collect(Collectors.toList());

        return makeGroupings(groupings);
    }

    public List<Grouping> groupingsToOptInto(String username) {
        WsGetGrouperPrivilegesLiteResult optinResults = grouperPrivilegesLite(username, "optin");

        List<String> groupingNames = groupingNamesFromPrivilegeResults(optinResults);
        List<String> groupings = groupingNames
                .stream()
                .filter(this::optInPermission)
                .collect(Collectors.toList());

        return makeGroupings(groupings);
    }

    /**
     * adds the self-opted attribute to a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    @Override
    public GrouperActionResult addSelfOpted(String group, String username) {
        logger.info("addSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            if (!checkSelfOpted(group, lookup)) {
                WsGetMembershipsResults includeMembershipsResults = membershipsResults(lookup, group);
                String membershipID = includeMembershipsResults.getWsMemberships()[0].getMembershipId();
                String operation = "assign_attr";
                return makeGrouperActionResult(assignAttributesResults(operation, UUID_USERNAME, membershipID), "add self-opted attribute");
            }
        }
        return new GrouperActionResult("FAILURE", "add self-opted attribute");
    }

    /**
     * @param group:  group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param lookup: WsSubjectLookup of user
     * @return true if the membership between the user and the group has the "self-opted" attribute
     */
    @Override
    public boolean checkSelfOpted(String group, WsSubjectLookup lookup) {
        logger.info("checkSelfOpted; group: " + group + "; wsSubjectLookup: " + lookup);

        if (inGroup(group, lookup.getSubjectIdentifier())) {
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(lookup, group);
            String assignType = "imm_mem";
            String uuid = UUID_USERNAME;
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            WsAttributeAssign[] wsAttributes = membershipAttributeAssign(assignType, uuid, membershipID);
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
    @Override
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

    @Override
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
                        .assignGroupName(grouping + INCLUDE)
                        .assignGroupName(grouping + EXCLUDE)
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
    @Override
    public GrouperActionResult removeSelfOpted(String group, String username) {
        logger.info("removeSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            if (checkSelfOpted(group, lookup)) {
                WsGetMembershipsResults membershipsResults = membershipsResults(lookup, group);
                String membershipID = membershipsResults.getWsMemberships()[0].getMembershipId();
                String operation = "remove_attr";
                return makeGrouperActionResult(assignAttributesResults(operation, UUID_USERNAME, membershipID), "remove self-opted attribute");
            }
        }
        return new GrouperActionResult("FAILURE", "remove self-opted attribute");
    }

    /*
     * @return date and time in yyyymmddThhmm format
     * ex. 20170314T0923
     */
    private String wsDateTime() {
        return Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");
    }

    /**
     * checks for permission to opt out of a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt out, false if not
     */
    @Override
    public boolean groupOptOutPermission(String username, String group) {
        logger.info("groupOptOutPermission; group: " + group + "; username: " + username);
        String privilegeName = "optout";
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, privilegeName, group);

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
    @Override
    public boolean groupOptInPermission(String username, String group) {
        logger.info("groupOptInPermission; group: " + group + "; username: " + username);
        String privilegeName = "optin";
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, privilegeName, group);

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
    @Override
    public WsAssignAttributesResults updateLastModified(String group) {
        logger.info("updateLastModified; group: " + group);

        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(wsDateTime());

        return new GcAssignAttributes()
                .assignAttributeAssignType("group")
                .assignAttributeAssignOperation("assign_attr")
                .addOwnerGroupName(group)
                .addAttributeDefNameName(ATTRIBUTES + ":for-groups:last-modified:yyyymmddThhmm")
                .assignAttributeAssignValueOperation("replace_values")
                .addValue(dateTimeValue)
                .execute();

    }

    // Helper method.
    @Override
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
    private WsAttributeAssign[] membershipAttributeAssign(String assignType, String uuid, String membershipID) {
        logger.info("membershipAttributeAssign; assignType: " + assignType + "; uuid: " + uuid + "; membershipID: " + membershipID);

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                new GcGetAttributeAssignments()
                        .assignAttributeAssignType(assignType)
                        .addAttributeDefNameUuid(uuid)
                        .addOwnerMembershipId(membershipID)
                        .execute();
        WsAttributeAssign[] wsAttributes = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();

        return wsAttributes != null ? wsAttributes : new WsAttributeAssign[0];
    }

    // Helper method
    public WsAssignAttributesResults groupAttributeAssign(String attributeName, String attributeOperation, String group) {
        logger.info("groupAttributeAssign; " + "; attributeName: " + attributeName + "; attributeOperation: " + attributeOperation + "; group: " + group);

        return new GcAssignAttributes()
                .assignAttributeAssignType("group")
                .assignAttributeAssignOperation(attributeOperation)
                .addOwnerGroupName(group)
                .addAttributeDefNameName(attributeName)
                .execute();
    }

    @Override
    public WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String group, String nameName) {
        logger.info("attributeAssignmentsResults; assignType: " + assignType + "; group: " + group + "; nameName: " + nameName);

        return new GcGetAttributeAssignments()
                .assignAttributeAssignType(assignType)
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
    @Override
    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String privilegeName, String group) {
        logger.info("grouperPrivlegesLite; username: " + username + "; group: " + group + "; privilegeName: " + privilegeName);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        return new GcGetGrouperPrivilegesLite()
                .assignGroupName(group)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .execute();
    }

    @Override
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
    @Override
    public WsAddMemberResults addMemberAs(String username, String group, String userToAdd) {
        logger.info("addMemberAs; user: " + username + "; group: " + group + "; userToAdd: " + userToAdd);

        WsSubjectLookup user = makeWsSubjectLookup(username);

        if (group.endsWith(":include")) {
            new GcDeleteMember()
                    .assignActAsSubject(user)
                    .assignGroupName(group.split(":include")[0] + ":exclude")
                    .addSubjectIdentifier(userToAdd)
                    .execute();
        }
        if (group.endsWith(":exclude")) {
            new GcDeleteMember()
                    .assignActAsSubject(user)
                    .assignGroupName(group.split(":exclude")[0] + ":include")
                    .addSubjectIdentifier(userToAdd)
                    .execute();
        }
        return new GcAddMember()
                .assignActAsSubject(user)
                .assignGroupName(group)
                .addSubjectIdentifier(userToAdd)
                .execute();
    }

    // Helper method.
    @Override
    public WsDeleteMemberResults deleteMemberAs(String username, String group, String userToDelete) {
        logger.info("delteMemberAs; user: " + username + "; group: " + group + "; userToDelete: " + userToDelete);

        WsSubjectLookup user = makeWsSubjectLookup(username);
        return new GcDeleteMember()
                .assignActAsSubject(user)
                .assignGroupName(group)
                .addSubjectIdentifier(userToDelete)
                .execute();
    }

    // Helper method.
    public WsGetMembersResults getMembers(WsSubjectLookup user, String group) {
        logger.info("getMembers; user: " + user + "; group: " + group);

        List<String> groupNames = Collections.singletonList(group);
        String grouping = extractGroupingNames(groupNames).get(0);
        if (isOwner(grouping, user.getSubjectIdentifier())) {
            return new GcGetMembers()
                    .addSubjectAttributeName("uid")
                    .addGroupName(group)
                    .assignIncludeSubjectDetail(true)
                    .execute();
        }
        return new WsGetMembersResults();
    }

    // Helper method.
    public List<String> allGroupings() {
        String uuid = UUID_TRIO;
        String assignType = "group";
        String subjectAttributeName = UHGROUPING + ":is-trio";
        List<String> trios = new ArrayList<>();

        WsGetAttributeAssignmentsResults groupings = attributeAssignments(assignType, subjectAttributeName, uuid);

        for (WsGroup aTrio : groupings.getWsGroups()) {
            trios.add(aTrio.getName());
        }

        return trios;
    }

    // Helper method.
    public List<String> extractGroupings(List<String> groups) {
        List<String> allGroupings = allGroupings();
        List<String> groupings = new ArrayList<>();

        for (String name : groups) {
            if (allGroupings.contains(name) && !groupings.contains(name)) {
                groupings.add(name);
            }
        }

        return groupings;
    }

    // Helper method.
    public List<String> getGroupNames(String username) {

        WsGetGroupsResults wsGetGroupsResults = new GcGetGroups()
                .addSubjectIdentifier(username)
                .assignWsStemLookup(STEM)
                .assignStemScope(StemScope.ALL_IN_SUBTREE)
                .execute();

        WsGetGroupsResult groupResults = wsGetGroupsResults.getResults()[0];
        List<WsGroup> groups = Arrays.asList(groupResults.getWsGroups());

        return extractGroupNames(groups);
    }

    // Helper method.
    public List<Grouping> makeGroupings(List<String> groupingPaths) {
        return groupingPaths.stream().map(Grouping::new).collect(Collectors.toList());
    }

    public List<String> extractGroupNames(List<WsGroup> groups) {
        List<String> names = new ArrayList<>();

        for (WsGroup group : groups) {
            if (!names.contains(group.getName())) {
                names.add(group.getName());
            }
        }

        return names;
    }

    // Helper method.
    private List<String> extractGroupingNames(List<String> groupNames) {
        List<String> groupingNames = new ArrayList<>();
        for (String name : groupNames) {
            if (name.endsWith(INCLUDE)) {
                groupingNames.add(name.split(INCLUDE)[0]);
            } else if (name.endsWith(EXCLUDE)) {
                groupingNames.add(name.split(EXCLUDE)[0]);
            } else if (name.endsWith(":basis")) {
                groupingNames.add(name.split(":basis")[0]);
            } else {
                groupingNames.add(name);
            }
        }

        return extractGroupings(groupingNames);
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

    // Helper method.
    public Group makeGroup(WsSubject[] subjects) {
        Group group = new Group();
        if (subjects != null && subjects.length > 0) {
            for (WsSubject subject : subjects) {
                if (subject != null) {
                    group.addMember(makePerson(subject));
                }
            }
        }

        return group;
    }

    // Helper method.
    private Person makePerson(WsSubject person) {
        String name = person.getName();
        String uuid = person.getId();
        String username = person.getAttributeValue(0);
        return new Person(name, uuid, username);
    }

    public List<String> groupingNamesFromPrivilegeResults(WsGetGrouperPrivilegesLiteResult privilegesLiteResult) {
        List<WsGroup> groups = new ArrayList<>();
        for (WsGrouperPrivilegeResult result : privilegesLiteResult.getPrivilegeResults()) {
            groups.add(result.getWsGroup());
        }

        List<String> groupNames = extractGroupNames(groups);
        return extractGroupingNames(groupNames);
    }

    public String changeGroupAttributeStatus(String group, String username, String attributeName, boolean attributeOn) {
        final String message;

        if (isOwner(group, username)) {
            boolean hasAttribute = groupHasAttribute(group, attributeName);
            if (attributeOn) {
                if (!hasAttribute) {
                    groupAttributeAssign(attributeName, "assign_attr", group);

                    message = attributeName + " has been turned on";
                } else {
                    message = attributeName + " is already on";
                }
            } else {
                if (hasAttribute) {
                    groupAttributeAssign(attributeName, "remove_attr", group);

                    message = attributeName + " has been turned off";
                } else {
                    message = attributeName + " is already off";
                }
            }
        } else {
            message = "User does not own Grouping";
        }

        return message;
    }

    public GrouperActionResult makeGrouperActionResult(ResultMetadataHolder resultMetadataHolder, String action) {
        GrouperActionResult grouperActionResult = new GrouperActionResult();
        grouperActionResult.setAction(action);
        grouperActionResult.setResultCode(resultMetadataHolder.getResultMetadata().getResultCode());

        return grouperActionResult;
    }

}


package edu.hawaii.its.groupings.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.hawaii.its.groupings.api.type.Group;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import edu.hawaii.its.groupings.api.type.Person;
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
public class GroupingsServiceImpl implements GroupingsService{

    public static final Log logger = LogFactory.getLog(GroupingsServiceImpl.class);

    public static final String UUID_USERNAME = "ef62bf0473614b379695ecec6cb8b3b5";
    public static final String SELF_OPTED = "uh-settings:attributes:for-memberships:uh-grouping:self-opted";
    public static final String UUID_TRIO = "1d7365a23c994f5f83f7b541d4a5fa5e";
    public static final String EXCLUDE = ":exclude";
    public static final String INCLUDE = ":include";
    public static final WsStemLookup STEM = new WsStemLookup("hawaii.edu:custom", null);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             public methods to be called from groupingsController                                                ///
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * gives a user ownersip permissions for a Grouping
     *
     * @param grouping: the Grouping that the user will get ownership permissions for
     * @param username: the owner of the Grouping who will give ownership permissions to the new owner
     * @param newOwner: the user that will become an owner of the Grouping
     * @return information about the success of the operation
     */
    public Object[] assignOwnership(String grouping, String username, String newOwner) {
        WsAssignGrouperPrivilegesResults[] privilegeResults = new WsAssignGrouperPrivilegesResults[4];

        if (isOwner(grouping, username)) {
            WsSubjectLookup ownerToAdd = makeWsSubjectLookup(newOwner);

            WsGroupLookup includeGroupLookup = makeWsGroupLookup(grouping + INCLUDE);
            WsGroupLookup basisGroupLookup = makeWsGroupLookup(grouping + ":basis");
            WsGroupLookup basisPlusIncludeGroupLookup = makeWsGroupLookup(grouping + ":basis+include");
            WsGroupLookup excludeGroupLookup = makeWsGroupLookup(grouping + EXCLUDE);

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

    public String changeListServeStatus(String grouping, String username, boolean listServeOn) {
        String attributeName = "uh-settings:attributes:for-groups:uh-grouping:destinations:listserv";

        return changeGroupAttributeStatus(grouping, username, attributeName, listServeOn);
    }


    public String changeOptInStatus(String grouping, String username, boolean optInOn) {
        String attributeName = "uh-settings:attributes:for-groups:uh-grouping:anyone-can:opt-in";

        return changeGroupAttributeStatus(grouping, username, attributeName, optInOn);
    }


    public String changeOptOutStatus(String grouping, String username, boolean optOutOn) {
        String attributeName = "uh-settings:attributes:for-groups:uh-grouping:anyone-can:opt-out";

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
    public Object[] removeOwnership(String grouping, String username, String ownerToRemove) {
        WsAssignGrouperPrivilegesResults[] privilegeResults = new WsAssignGrouperPrivilegesResults[4];

        if (isOwner(grouping, username)) {
            WsSubjectLookup ownerToRemoveLookup = makeWsSubjectLookup(ownerToRemove);

            WsGroupLookup includeGroupLookup = makeWsGroupLookup(grouping + INCLUDE);
            WsGroupLookup basisGroupLookup = makeWsGroupLookup(grouping + ":basis");
            WsGroupLookup basisPlusIncludeGroupLookup = makeWsGroupLookup(grouping + ":basis+include");
            WsGroupLookup excludeGroupLookup = makeWsGroupLookup(grouping + EXCLUDE);

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


    /**
     * @param grouping: the path of the Grouping to be searched for
     * @param username: the user who is doing the search
     * @return a Grouping Object containing information about the Grouping
     */
    public Grouping getGrouping(String grouping, String username) {
        WsSubjectLookup user = makeWsSubjectLookup(username);

        Group includeGroup = new Group();
        Group excludeGroup = new Group();
        Group basisGroup = new Group();
        Group basisPlusIncludeMinusExcludeGroup = new Group();

        WsGetMembersResults basisResults = getMembers(user, grouping + ":basis");
        WsGetMembersResults excludeResults = getMembers(user, grouping + EXCLUDE);
        WsGetMembersResults includeResults = getMembers(user, grouping + INCLUDE);
        WsGetMembersResults basisPlusIncludeMinusExcludeResults = getMembers(user, grouping);

        if (includeResults.getResults() != null) {
            includeGroup = makeGroup(includeResults.getResults()[0].getWsSubjects());
        }
        if (excludeResults.getResults() != null) {
            excludeGroup = makeGroup(excludeResults.getResults()[0].getWsSubjects());
        }
        if (basisResults.getResults() != null) {
            basisGroup = makeGroup(basisResults.getResults()[0].getWsSubjects());
        }
        if (basisPlusIncludeMinusExcludeResults.getResults() != null) {
            basisPlusIncludeMinusExcludeGroup = makeGroup(basisPlusIncludeMinusExcludeResults.getResults()[0].getWsSubjects());
        }
        Group owners = getOwners(grouping, username);

        Grouping members = new Grouping(basisGroup, excludeGroup, includeGroup, basisPlusIncludeMinusExcludeGroup, owners);
        members.setPath(grouping);
        members.setListServe(hasListServe(grouping));

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
    public Object[] optIn(String username, String grouping) {
        Object[] results = new Object[6];

        if (groupOptInPermission(username, grouping + INCLUDE)) {

            results[3] = removeSelfOpted(grouping + EXCLUDE, username);
            results[0] = deleteMemberAs(username, grouping + EXCLUDE, username);
            results[1] = addMemberAs(username, grouping + INCLUDE, username);
            results[4] = updateLastModified(grouping + EXCLUDE);
            results[5] = updateLastModified(grouping + INCLUDE);
            results[2] = addSelfOpted(grouping + INCLUDE, username);

            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt into this Grouping");
        }
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
    public Object[] optOut(String username, String grouping) {
        Object[] results = new Object[6];

        if (groupOptInPermission(username, grouping + EXCLUDE)) {

            results[3] = removeSelfOpted(grouping + INCLUDE, username);
            results[0] = deleteMemberAs(username, grouping + INCLUDE, username);
            results[1] = addMemberAs(username, grouping + EXCLUDE, username);
            results[4] = updateLastModified(grouping + EXCLUDE);
            results[5] = updateLastModified(grouping + INCLUDE);
            results[2] = addSelfOpted(grouping + EXCLUDE, username);

            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt out of this Grouping");
        }
    }


    /**
     * if the user has opted into a Grouping, this will remove them from the include group
     *
     * @param grouping: the path to the Grouping that the user is opted into
     * @param username: username of the user canceling optIn
     * @return information about the success of the operation
     */
    public Object[] cancelOptIn(String grouping, String username) {
        Object[] results = new Object[3];
        String group = grouping + INCLUDE;

        if (inGroup(group, username)) {

            if (groupOptInPermission(username, group)) {

                results[0] = deleteMemberAs(username, group, username);
                results[2] = updateLastModified(group);

                return results;
            } else {
                throw new AccessDeniedException("user is not allowed to opt out of 'include' group");
            }
        } else {
            results[0] = "Success, user is not opted in, because user was not in 'include' group";
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
    public Object[] cancelOptOut(String grouping, String username) {
        Object[] results = new Object[3];
        String group = grouping + EXCLUDE;

        if (inGroup(group, username)) {

            if (groupOptOutPermission(username, group)) {
                results[0] = deleteMemberAs(username, group, username);

                results[2] = updateLastModified(group);

                return results;
            } else {
                throw new AccessDeniedException("user is not allowed to opt out of 'exclude' group");
            }
        } else {
            results[0] = "Success, user is not opted out, because user was not in 'exclude' group";
        }
        return results;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                                              helper methods                                                                     ///
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @param grouping: the path of the Grouping to be searched for
     * @param username: the user doing the search
     * @return a Group of owners for the Grouping
     */
    public Group getOwners(String grouping, String username) {
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
            ArrayList<WsSubject> subjects = new ArrayList<>();

            if (privileges.getPrivilegeResults() != null) {
                for (WsGrouperPrivilegeResult result : privileges.getPrivilegeResults()) {
                    subjects.add(result.getOwnerSubject());
                }

                owners = makeGroup(subjects.toArray(new WsSubject[subjects.size()]));
            }
        }
        return owners;
    }

    public boolean optOutPermission(String grouping) {
        String nameName = "uh-settings:attributes:for-groups:uh-grouping:anyone-can:opt-out";

        return groupHasAttribute(grouping, nameName);
    }


    public boolean optInPermission(String grouping) {
        String nameName = "uh-settings:attributes:for-groups:uh-grouping:anyone-can:opt-in";

        return groupHasAttribute(grouping, nameName);
    }

    public boolean groupHasAttribute(String grouping, String nameName) {

        boolean hasAtt = false;
        String assignType = "group";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                attributeAssignmentsResults(assignType, grouping, nameName);

        if (wsGetAttributeAssignmentsResults.getWsAttributeAssigns() != null) {
            for (WsAttributeAssign attribute : wsGetAttributeAssignmentsResults.getWsAttributeAssigns()) {
                if (attribute.getAttributeDefNameName().equals(nameName)) {
                    hasAtt = true;
                }
            }
        }
        return hasAtt;
    }


    public List<Grouping> groupingsIn(String username) {
        String[] groupsIn = getGroupNames(username);
        List<String> groupingPaths = extractGroupings(groupsIn);

        return makeGroupings(groupingPaths);
    }


    public boolean hasListServe(String grouping) {

        String nameName = "uh-settings:attributes:for-groups:uh-grouping:destinations:listserv";

        return groupHasAttribute(grouping, nameName);
    }

    public List<Grouping> groupingsOwned(String username) {
        WsSubjectLookup user = makeWsSubjectLookup(username);
        WsGetGrouperPrivilegesLiteResult getPrivilegesResult = new GcGetGrouperPrivilegesLite().assignPrivilegeName("update").assignSubjectLookup(user).execute();
        WsGrouperPrivilegeResult[] privilegeResults = getPrivilegesResult.getPrivilegeResults();
        if (privilegeResults != null) {
            WsGroup[] groups = new WsGroup[privilegeResults.length];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = privilegeResults[i].getWsGroup();
            }
            List<String> names = extractGroupNames(groups);
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).endsWith(INCLUDE)) {
                    names.set(i, names.get(i).split(INCLUDE)[0]);
                } else if (names.get(i).endsWith(EXCLUDE)) {
                    names.set(i, names.get(i).split(EXCLUDE)[0]);
                }
            }
            List<String> paths = extractGroupings(names.toArray(new String[names.size()]));
            return makeGroupings(paths);
        } else {
            return new ArrayList<Grouping>();
        }
    }


    public List<Grouping> groupingsToOptOutOf(String username) {
        WsGetGrouperPrivilegesLiteResult optinResults = grouperPrivilegesLite(username, "optout");
        ArrayList<String> groupings = new ArrayList<>();

        List<String> groupingNames = groupingNamesFromPrivilegeResults(optinResults);

        groupings.addAll(groupingNames.stream().filter(name -> optOutPermission(name)).collect(Collectors.toList()));

        return makeGroupings(groupings);
    }


    public List<Grouping> groupingsToOptInto(String username) {
        WsGetGrouperPrivilegesLiteResult optinResults = grouperPrivilegesLite(username, "optin");
        ArrayList<String> groupings = new ArrayList<>();

        List<String> groupingNames = groupingNamesFromPrivilegeResults(optinResults);

        groupings.addAll(groupingNames.stream().filter(name -> optInPermission(name)).collect(Collectors.toList()));

        return makeGroupings(groupings);
    }

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
    public WsAttributeAssign[] membershipAttributeAssign(String assignType, String uuid, String membershipID) {
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

        String grouping = extractGroupingNames(new String[]{group}).get(0);
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
        List<String> names;

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
    public List<Grouping> makeGroupings(List<String> groupingPaths) {
        return groupingPaths.stream().map(Grouping::new).collect(Collectors.toList());
    }

    // Helper method.
    public List<String> extractGroupNames(WsGroup[] groups) {
        ArrayList<String> names = new ArrayList<>();

        for (WsGroup group : groups) {
            if (!names.contains(group.getName())) {
                names.add(group.getName());
            }
        }

        return names;
    }

    //Helper method
    public List<String> extractGroupingNames(String[] groupNames) {
        ArrayList<String> groupingNames = new ArrayList<>();
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

        return extractGroupings(groupingNames.toArray(new String[groupingNames.size()]));
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
    public Group makeGroup(WsSubject[] subjects) {
        Group group = new Group();
        ArrayList<Person> persons = new ArrayList<>();
        try {
            for (WsSubject subject : subjects) {
                persons.add(makePerson(subject));
            }
            group.setMembers(persons);
        } catch (NullPointerException npe) {
        }
        return group;
    }

    //Helper method
    public Person makePerson(WsSubject person) {
        String name = person.getName();
        String uuid = person.getId();
        String username = person.getAttributeValue(0);
        return new Person(name, uuid, username);
    }

    public List<String> groupingNamesFromPrivilegeResults(WsGetGrouperPrivilegesLiteResult privilegesLiteResult) {

        ArrayList<WsGroup> groups = new ArrayList<>();

        for (WsGrouperPrivilegeResult result : privilegesLiteResult.getPrivilegeResults()) {
            groups.add(result.getWsGroup());
        }

        List<String> groupNames = extractGroupNames(groups.toArray(new WsGroup[groups.size()]));

        return extractGroupingNames(groupNames.toArray(new String[groupNames.size()]));
    }


    public String changeGroupAttributeStatus(String group, String username, String attributeName, boolean attributeOn) {
        String returnMessage;

        if (isOwner(group, username)) {
            if (attributeOn) {
                if (!groupHasAttribute(group, attributeName)) {
                    groupAttributeAssign(attributeName, "assign_attr", group);

                    returnMessage = attributeName + " has been turned on";
                } else {
                    returnMessage = attributeName + " is already on";
                }
            } else {
                if (groupHasAttribute(group, attributeName)) {
                    groupAttributeAssign(attributeName, "remove_attr", group);

                    returnMessage = attributeName + " has been turned off";
                } else {
                    returnMessage = attributeName + " is already off";
                }
            }
        }
        else {
            returnMessage = "User does not own Grouping";
        }

        return returnMessage;
    }

}

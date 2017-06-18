package edu.hawaii.its.groupings.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.hawaii.its.groupings.api.type.*;
import edu.hawaii.its.holiday.util.Dates;

import edu.internet2.middleware.grouperClient.api.*;
import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.internet2.middleware.grouperClient.ws.StemScope;

@Service("groupingsService")
public class GroupingsServiceImpl implements GroupingsService {
    public static final Log logger = LogFactory.getLog(GroupingsServiceImpl.class);
    private static final WsStemLookup STEM = new WsStemLookup("tmp", null);

    @Value("${groupings.api.settings}")
    private String SETTINGS;

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

    /**
     * gives a user ownership permissions for a Grouping
     *
     * @param grouping: the Grouping that the user will get ownership permissions for
     * @param username: the owner of the Grouping who will give ownership permissions to the new owner
     * @param newOwner: the user that will become an owner of the Grouping
     * @return information about the success of the operation
     */
    @Override
    public GroupingsServiceResult assignOwnership(String grouping, String username, String newOwner) {
        String action = "give " + newOwner + " ownership of " + grouping;
        GroupingsServiceResult ownershipResult;

        if (isOwner(grouping, username)) {
            WsSubjectLookup user = makeWsSubjectLookup(username);
            WsAddMemberResults amr = new GcAddMember()
                    .assignActAsSubject(user)
                    .addSubjectIdentifier(newOwner)
                    .assignGroupName(grouping + OWNERS)
                    .execute();
            ownershipResult = makeGroupingsServiceResult(amr, action);

            return ownershipResult;
        }

        ownershipResult = new GroupingsServiceResult(
                "FAILURE, " + username + " does not own " + grouping,
                action);
        return ownershipResult;
    }

    /**
     * @param grouping:    the path of the Grouping that will have its listserv status changed
     * @param username:    username of the Grouping Owner preforming the action
     * @param listservOn: true if the listserv should be turned on, false if it should be turned off
     * @return "SUCCESS" if the action succeeds or "FAILURE" if it does not.
     */
    @Override
    public GroupingsServiceResult changeListservStatus(String grouping, String username, boolean listservOn) {
        return changeGroupAttributeStatus(grouping, username, LISTSERV, listservOn);
    }

    /**
     * @param grouping: the path of the Grouping that will have its optIn permission changed
     * @param username: username of the Grouping Owner preforming the action
     * @param optInOn:  true if the optIn permission should be turned on, false if it should be turned off
     * @return "SUCCESS" if the action succeeds or "FAILURE" if it does not.
     */
    @Override
    public GroupingsServiceResult changeOptInStatus(String grouping, String username, boolean optInOn) {
        assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_IN, grouping + INCLUDE, optInOn);
        if(optInOn) {
            assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, grouping + INCLUDE, optInOn);
            assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, grouping + EXCLUDE, optInOn);
        }
        return changeGroupAttributeStatus(grouping, username, OPT_IN, optInOn);
    }

    /**
     * @param grouping: the path of the Grouping that will have its optOut permission changed
     * @param username: username of the Grouping Owner preforming the action
     * @param optOutOn: true if the optOut permission should be turned on, false if it should be turned off
     * @return "SUCCESS" if the action succeeds or "FAILURE" if it does not.
     */
    @Override
    public GroupingsServiceResult changeOptOutStatus(String grouping, String username, boolean optOutOn) {
        assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_IN, grouping + EXCLUDE, optOutOn);
        if(optOutOn) {
            assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, grouping + EXCLUDE, optOutOn);
            assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, grouping + INCLUDE, optOutOn);
        }
        return changeGroupAttributeStatus(grouping, username, OPT_OUT, optOutOn);
    }

    /**
     * removes ownership permissions from a user
     *
     * @param grouping:      the Grouping for which the user's ownership will be removed
     * @param username:      the owner of the Grouping who will be removing ownership from the owner to be removed
     * @param ownerToRemove: the owner who will have ownership privileges removed
     * @return information about the success of the operation
     */
    @Override
    public GroupingsServiceResult removeOwnership(String grouping, String username, String ownerToRemove) {
        GroupingsServiceResult ownershipResults;
        String action = "remove ownership of " + grouping + " from " + ownerToRemove;

        if (isOwner(grouping, username)) {

            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            WsDeleteMemberResults memberResults = new GcDeleteMember()
                    .assignActAsSubject(lookup)
                    .addSubjectIdentifier(ownerToRemove)
                    .assignGroupName(grouping + OWNERS)
                    .execute();

            ownershipResults = makeGroupingsServiceResult(memberResults, action);

            return ownershipResults;
        }

        ownershipResults = new GroupingsServiceResult("FAILURE, " + username + " does not own " + grouping, action);
        return ownershipResults;
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
        WsGetMembersResults basisResults = getMembers(user, grouping + BASIS);
        if (basisResults.getResults() != null) {
            basisGroup = makeGroup(basisResults.getResults()[0].getWsSubjects());
        }

        Group basisPlusIncludeMinusExcludeGroup = new Group();
        WsGetMembersResults basisPlusIncludeMinusExcludeResults = getMembers(user, grouping);
        if (basisPlusIncludeMinusExcludeResults.getResults() != null) {
            basisPlusIncludeMinusExcludeGroup = makeGroup(basisPlusIncludeMinusExcludeResults
                    .getResults()[0]
                    .getWsSubjects());
        }

        Grouping members = new Grouping(grouping);
        members.setBasis(basisGroup);
        members.setExclude(excludeGroup);
        members.setInclude(includeGroup);
        members.setBasisPlusIncludeMinusExclude(basisPlusIncludeMinusExcludeGroup);
        members.setOwners(findOwners(grouping, username));
        members.setListservOn(hasListserv(grouping));
        members.setOptInOn(optInPermission(grouping));
        members.setOptOutOn(optOutPermission(grouping));

        return members;
    }

    /**
     * @param username: username of the user to display Groupings for
     * @return the Groupings that the user
     * is in
     * owns
     * can opt into
     * can opt out of
     * has opted into
     * has opted out of
     */
    @Override
    public MyGroupings getMyGroupings(String username) {
        MyGroupings myGroupings = new MyGroupings();

        myGroupings.setGroupingsIn(groupingsIn(username));
        myGroupings.setGroupingsOwned(groupingsOwned(username));
        myGroupings.setGroupingsToOptInTo(groupingsToOptInto());
        myGroupings.setGroupingsToOptOutOf(groupingsToOptOutOf());
        myGroupings.setGroupingsOptedOutOf(groupingsOptedOutOf(username));
        myGroupings.setGroupingsOptedInTo(groupingsOptedInto(username));

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
    public List<GroupingsServiceResult> optIn(String username, String grouping) {
        List<GroupingsServiceResult> results = new ArrayList<>();

        if (groupOptInPermission(username, grouping + INCLUDE)) {
            results.add(removeSelfOpted(grouping + EXCLUDE, username));
            results.add(0, deleteMemberAs(username, grouping + EXCLUDE, username));
            results.add(1, addMemberAs(username, grouping + INCLUDE, username));
            results.add(updateLastModified(grouping + EXCLUDE));
            results.add(updateLastModified(grouping + INCLUDE));
            results.add(2, addSelfOpted(grouping + INCLUDE, username));

            return results;
        }
        results.add(new GroupingsServiceResult(
                "FAILURE, " + username + " does not have permission to opt in to " + grouping,
                "opt in " + username + " to " + grouping));
        return results;
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
    public List<GroupingsServiceResult> optOut(String username, String grouping) {
        List<GroupingsServiceResult> results = new ArrayList<>();

        if (groupOptInPermission(username, grouping + EXCLUDE)) {
            results.add(removeSelfOpted(grouping + INCLUDE, username));
            results.add(0, deleteMemberAs(username, grouping + INCLUDE, username));
            results.add(1, addMemberAs(username, grouping + EXCLUDE, username));
            results.add(updateLastModified(grouping + EXCLUDE));
            results.add(updateLastModified(grouping + INCLUDE));
            results.add(2, addSelfOpted(grouping + EXCLUDE, username));

            return results;
        }

        results.add(new GroupingsServiceResult(
                "FAILURE, " + username + " does not have permission to opt out of " + grouping,
                "opt out " + username + " from " + grouping));
        return results;
    }

    /**
     * if the user has opted into a Grouping, this will remove them from the include group
     *
     * @param grouping: the path to the Grouping that the user is opted into
     * @param username: username of the user canceling optIn
     * @return information about the success of the operation
     */
    @Override
    public List<GroupingsServiceResult> cancelOptIn(String grouping, String username) {
        List<GroupingsServiceResult> results = new ArrayList<>();
        String group = grouping + INCLUDE;

        if (inGroup(group, username)) {
            if (groupOptInPermission(username, group)) {
                results.add(deleteMemberAs(username, group, username));
                results.add(updateLastModified(group));

                return results;
            } else {
                results.add(new GroupingsServiceResult(
                        username + " is not allowed to opt out of " + group,
                        "opt " + username + " out of " + group));
            }
        } else {
            results.add(new GroupingsServiceResult(
                    "SUCCESS, " + username + " is not opted in, because " + username + " was not in " + group,
                    "cancel opt in for " + username + " to " + grouping));
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
    public List<GroupingsServiceResult> cancelOptOut(String grouping, String username) {
        String group = grouping + EXCLUDE;
        List<GroupingsServiceResult> results = new ArrayList<>();

        if (inGroup(group, username)) {
            if (groupOptOutPermission(username, group)) {
                results.add(deleteMemberAs(username, group, username));
                results.add(updateLastModified(group));

                return results;
            } else {
                results.add(new GroupingsServiceResult(
                        username + " is not allowed to opt out of " + group,
                        "opt " + username + " out of " + group));
            }
        } else {
            results.add(new GroupingsServiceResult(
                    "SUCCESS, " + username + " is not opted out, because " + username + " was not in " + group,
                    "cancel opt out for " + username + " to " + grouping));
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
        logger.info("findOwners; grouping: " + grouping + "; username: " + username);

        Group owners = new Group();

        if (isOwner(grouping, username)) {
            WsGetMembersResults membersResults = new GcGetMembers()
                    .addGroupName(grouping + OWNERS)
                    .addSubjectAttributeName(SUBJECT_ATTRIBUTE_NAME_UID)
                    .execute();
            List<WsSubject> subjects = new ArrayList<>();
            if (membersResults.getResults() != null) {
                for (WsGetMembersResult membersResult : membersResults.getResults()) {
                    if (membersResult != null) {
                        Collections.addAll(subjects, membersResult.getWsSubjects());
                    }
                }
            }
            owners = makeGroup(subjects.toArray(new WsSubject[subjects.size()]));
        }

        return owners;
    }

    /**
     * @param grouping: path to the Grouping that will have its permissions checked
     * @return true if the Grouping is allowed to be opted out of and false if not
     */
    @Override
    public boolean optOutPermission(String grouping) {
        return groupHasAttribute(grouping, OPT_OUT);
    }

    /**
     * @param grouping: path to the Grouping that will have its permissions checked
     * @return true if the Grouping is allowed to be opted into and false if not
     */
    @Override
    public boolean optInPermission(String grouping) {
        return groupHasAttribute(grouping, OPT_IN);
    }

    /**
     * @param grouping: path to Grouping that will have its attributes checked
     * @param nameName: name of attribute to be checked for
     * @return true if that attribute exists in that Grouping
     */
    public boolean groupHasAttribute(String grouping, String nameName) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                attributeAssignmentsResults(ASSIGN_TYPE_GROUP, grouping, nameName);
        if (wsGetAttributeAssignmentsResults.getWsAttributeAssigns() != null) {
            for (WsAttributeAssign attribute : wsGetAttributeAssignmentsResults.getWsAttributeAssigns()) {
                if (attribute.getAttributeDefNameName().equals(nameName)) {
                    return true; // Found it, get out now.
                }
            }
        }
        return false;
    }

    /**
     * @param username: username of the user who's groupings will be looked for
     * @return a list of all of the Groupings that the user is in
     */
    @Override
    public List<Grouping> groupingsIn(String username) {
        List<String> groupsIn = getGroupNames(username);
        List<String> groupingsIn = groupsIn
                .stream()
                .filter(this::isGrouping)
                .collect(Collectors.toList());

        return makeGroupings(groupingsIn);
    }

    public boolean isGrouping(String group) {
        boolean returnValue = false;
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments()
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP)
                .addOwnerGroupName(group)
                .execute();

        WsAttributeDefName[] names = wsGetAttributeAssignmentsResults.getWsAttributeDefNames();
        if (names != null) {
            for (WsAttributeDefName name : names) {
                if (name.getName().equals(TRIO)) {
                    returnValue = true;
                }
            }
        }

        return returnValue;

    }

    /**
     * @param grouping: path to the Grouping that will have its listserv attribute checked
     * @return true if the Grouping has a listserv attribute false if not
     */
    @Override
    public boolean hasListserv(String grouping) {
        return groupHasAttribute(grouping, LISTSERV);
    }

    /**
     * @param username: username of the user who's groupings will be looked for
     * @return a list of all of the Groupings that the user owns
     */
    public List<Grouping> groupingsOwned(String username) {
        List<String> groupingsOwned = new ArrayList<>();
        List<String> groupsIn = getGroupNames(username);

        groupsIn.stream().filter(group -> group.endsWith(OWNERS)).forEach(group -> {
            String grouping = group.split(OWNERS)[0];
            if (isGrouping(grouping)) {
                groupingsOwned.add(grouping);
            }
        });
        return makeGroupings(groupingsOwned);
    }

    /**
     * @param username: username of the user who's groupings will be looked for
     * @return a list of all of the Groupings that the user is opted into
     */
    public List<Grouping> groupingsOptedInto(String username) {
        return groupingsOpted(INCLUDE, username);
    }

    /**
     * @param username: username of the user who's groupings will be looked for
     * @return a list of all of the Groupings that the user is opted out of
     */
    public List<Grouping> groupingsOptedOutOf(String username) {
        return groupingsOpted(EXCLUDE, username);
    }

    /**
     * @param includeOrrExclude: ":include" for the include group ":exclude" for
     *                           the exclude group
     * @param username:          username of the user who's groupings will be looked for
     * @return a list of all of the groups that the user is opted into that end
     * with the suffix defined in includeOrrExclude
     */
    public List<Grouping> groupingsOpted(String includeOrrExclude, String username) {

        List<String> groupsIn = getGroupNames(username);
        List<String> groupsOpted = new ArrayList<>();
        List<String> groupingsOpted = new ArrayList<>();

        for (String group : groupsIn) {
            String parentGrouping = parentGroupingPath(group);
            if (group.endsWith(includeOrrExclude)
                    && checkSelfOpted(group, username)) {
                groupsOpted.add(parentGrouping);
            }
        }

        GcGetAttributeAssignments getAttributeAssignments = new GcGetAttributeAssignments()
                .addAttributeDefNameName(TRIO)
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP);

        groupsOpted.forEach(getAttributeAssignments::addOwnerGroupName);

        WsGetAttributeAssignmentsResults attributeAssignmentsResults = getAttributeAssignments.execute();
        WsGroup[] trios = attributeAssignmentsResults.getWsGroups();

        for (WsGroup group : trios) {
            if(groupsOpted.contains(group.getName())) {
                groupingsOpted.add(group.getName());
            }
        }
        return makeGroupings(groupingsOpted);
    }


    /**
     * @return a list of all groupings that the user is able to opt out of
     */
    public List<Grouping> groupingsToOptOutOf() {

        WsGetAttributeAssignmentsResults attributeAssignmentsResults = new GcGetAttributeAssignments()
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP)
                .addAttributeDefNameName(TRIO)
                .addAttributeDefNameName(OPT_OUT)
                .execute();


        WsGroup[] wsGroups = attributeAssignmentsResults.getWsGroups();

        List<String> groups = new ArrayList<>();
        for (WsGroup group : wsGroups) {
            groups.add(group.getName());
        }


        List<String> groupings = extractGroupings(groups);

        return makeGroupings(groupings);
    }

    /**
     * @return a list of all groupings that the user is able to opt into
     */
    public List<Grouping> groupingsToOptInto() {

        WsGetAttributeAssignmentsResults attributeAssignmentsResults = new GcGetAttributeAssignments()
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP)
                .addAttributeDefNameName(TRIO)
                .addAttributeDefNameName(OPT_IN)
                .execute();


        WsGroup[] wsGroups = attributeAssignmentsResults.getWsGroups();

        List<String> groups = new ArrayList<>();
        for (WsGroup group : wsGroups) {
            groups.add(group.getName());
        }


        List<String> groupings = extractGroupings(groups);

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
    public GroupingsServiceResult addSelfOpted(String group, String username) {
        logger.info("addSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            if (!checkSelfOpted(group, username)) {
                WsGetMembershipsResults includeMembershipsResults = membershipsResults(username, group);

                String membershipID = includeMembershipsResults
                        .getWsMemberships()[0]
                        .getMembershipId();

                return makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, membershipID),
                        "add self-opted attribute to the membership of " + username + " to " + group);
            }
        }
        return new GroupingsServiceResult(
                "FAILURE, " + username + " is not a member of " + group,
                "add self-opted attribute to the membership of " + username + " to " + group);
    }

    /**
     * @param group:  group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param username: username
     * @return true if the membership between the user and the group has the "self-opted" attribute
     */
    public boolean checkSelfOpted(String group, String username) {
        logger.info("checkSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(username, group);
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            WsAttributeAssign[] wsAttributes = getMembershipAttributes(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, membershipID);
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

    /**
     * @param grouping: path to Grouping that will be checked
     * @param username: username of user who's permissions will be checked
     * @return true if user owns grouping false if not
     */
    @Override
    public boolean isOwner(String grouping, String username) {
        logger.info("isOwner; grouping: " + grouping + "; username: " + username);

        return inGroup(grouping + OWNERS, username);
    }

    /**
     * removes the self-opted attribute from a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    @Override
    public GroupingsServiceResult removeSelfOpted(String group, String username) {
        logger.info("removeSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            if (checkSelfOpted(group, username)) {
                WsGetMembershipsResults membershipsResults = membershipsResults(username, group);
                String membershipID = membershipsResults
                        .getWsMemberships()[0]
                        .getMembershipId();
                return makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_REMOVE_ATTRIBUTE, SELF_OPTED, membershipID),
                        "remove self-opted attribute from the membership of " + username + " to " + group);
            }
        }
        return new GroupingsServiceResult(
                "FAILURE, " + username + " is not a member of " + group,
                "remove self-opted attribute from the membership of " + username + " to " + group);
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
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, PRIVILEGE_OPT_OUT, group);

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
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, PRIVILEGE_OPT_IN, group);

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
     * @param group: group who's last modified attribute will be updated
     * @return results from Grouper Web Service
     */
    @Override
    public GroupingsServiceResult updateLastModified(String group) {
        logger.info("updateLastModified; group: " + group);
        String time = wsDateTime();

        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(time);

        WsAssignAttributesResults assignAttributesResults = new GcAssignAttributes()
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP)
                .assignAttributeAssignOperation(OPERATION_ASSIGN_ATTRIBUTE)
                .addOwnerGroupName(group)
                .addAttributeDefNameName(YYYYMMDDTHHMM)
                .assignAttributeAssignValueOperation(OPERATION_REPLACE_VALUES)
                .addValue(dateTimeValue)
                .execute();

        return makeGroupingsServiceResult(assignAttributesResults, "update last-modified attribute for " + group + " to time " + time);

    }

    /**
     * @param username: username of user to be looked up
     * @return a WsSubjectLookup with username as the subject identifier
     */
    public WsSubjectLookup makeWsSubjectLookup(String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        return wsSubjectLookup;
    }

    /**
     * @param group: group to be looked up
     * @return a WsGroupLookup with group as the group name
     */
    public WsGroupLookup makeWsGroupLookup(String group) {
        WsGroupLookup groupLookup = new WsGroupLookup();
        groupLookup.setGroupName(group);

        return groupLookup;
    }

    /**
     * @param operation:    name of operation
     * @param uuid:         uuid of the attribute
     * @param membershipID: membership id for the membership between the user and Grouping
     * @return information about the success of the action
     */
    public WsAssignAttributesResults assignMembershipAttributes(String operation, String uuid, String membershipID) {
        logger.info("assignMembershipAttributes; operation: " + operation + "; uuid: " + uuid + "; membershipID: " + membershipID);

        return new GcAssignAttributes()
                .assignAttributeAssignType(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP)
                .assignAttributeAssignOperation(operation)
                .addAttributeDefNameName(uuid)
                .addOwnerMembershipId(membershipID)
                .execute();
    }

    /**
     * @param assignType:   assign type of the attribute
     * @param name:         uuid of the attribute
     * @param membershipID: membership id for the membership between the user and Grouping
     * @return information about the success of the action
     */
    public WsAttributeAssign[] getMembershipAttributes(String assignType, String name, String membershipID) {
        logger.info("getMembershipAttributes; assignType: " + assignType + "; name: " + name + "; membershipID: " + membershipID);

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                new GcGetAttributeAssignments()
                        .assignAttributeAssignType(assignType)
                        .addAttributeDefNameName(name)
                        .addOwnerMembershipId(membershipID)
                        .execute();
        WsAttributeAssign[] wsAttributes = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();

        return wsAttributes != null ? wsAttributes : new WsAttributeAssign[0];
    }

    /**
     * @param attributeName:      name of attribute to be assigned
     * @param attributeOperation: operation to be done with the attribute to the group
     * @param group:              path to the group to have the attribute acted upon
     * @return information about the success of the operation
     */
    public WsAssignAttributesResults assignGroupAttributes(String attributeName, String attributeOperation, String group) {
        logger.info("assignGroupAttributes; " + "; attributeName: " + attributeName + "; attributeOperation: " + attributeOperation + "; group: " + group);

        return new GcAssignAttributes()
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP)
                .assignAttributeAssignOperation(attributeOperation)
                .addOwnerGroupName(group)
                .addAttributeDefNameName(attributeName)
                .execute();
    }

    /**
     * @param assignType: assign type of the attribute
     * @param group:      path to the group to have attributes searched
     * @param nameName:   name of the attribute to be looked up
     * @return information about the attributes that the group has
     */
    public WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String group, String nameName) {
        logger.info("attributeAssignmentsResults; assignType: " + assignType + "; group: " + group + "; nameName: " + nameName);

        return new GcGetAttributeAssignments()
                .assignAttributeAssignType(assignType)
                .addOwnerGroupName(group)
                .addAttributeDefNameName(nameName)
                .execute();
    }

    /**
     * @param username:      username of user who's privileges will be checked
     * @param privilegeName: name of the privilege to be checked
     * @param group:         name of group the privilege is for
     * @return return information about user's privileges in the group
     */
    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String privilegeName, String group) {
        logger.info("grouperPrivilegesLite; username: " + username + "; group: " + group + "; privilegeName: " + privilegeName);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        return new GcGetGrouperPrivilegesLite()
                .assignGroupName(group)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .execute();
    }

    public GroupingsServiceResult assignGrouperPrivilege(String username, String privilegeName, String group, boolean set) {
        logger.info("assignGrouperPrivilege; username: " + username + "; group: " + group + "; privilegeName: " + privilegeName + " set: " + set);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        String action = "set " + privilegeName + " " + set + " for " + username + " in " + group;
        WsAssignGrouperPrivilegesLiteResult grouperPrivilegesLiteResult = new GcAssignGrouperPrivilegesLite()
                .assignGroupName(group)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .assignAllowed(set)
                .execute();

        return makeGroupingsServiceResult(grouperPrivilegesLiteResult, action);
    }

    /**
     * @param username: WsSubjectLookup of user who's membership will be checked
     * @param group:  group that membership status will be checked for
     * @return membership results for user
     */
    public WsGetMembershipsResults membershipsResults(String username, String group) {
        logger.info("membershipResults; username: " + username + "; group: " + group);

        WsSubjectLookup lookup = makeWsSubjectLookup(username);

        return new GcGetMemberships()
                .addWsSubjectLookup(lookup)
                .addGroupName(group)
                .execute();
    }

    /**
     * @param username:  username of owner adding member
     * @param group:     path to group the user to be added will be added to
     * @param userToAdd: username of user to be added to grup
     * @return information about success of action
     */
    @Override
    public GroupingsServiceResult addMemberAs(String username, String group, String userToAdd) {
        logger.info("addMemberAs; user: " + username + "; group: " + group + "; userToAdd: " + userToAdd);

        WsSubjectLookup user = makeWsSubjectLookup(username);

        if (group.endsWith(INCLUDE)) {
            new GcDeleteMember()
                    .assignActAsSubject(user)
                    .assignGroupName(group.split(INCLUDE)[0] + EXCLUDE)
                    .addSubjectIdentifier(userToAdd)
                    .execute();
        } else if (group.endsWith(EXCLUDE)) {
            new GcDeleteMember()
                    .assignActAsSubject(user)
                    .assignGroupName(group.split(EXCLUDE)[0] + INCLUDE)
                    .addSubjectIdentifier(userToAdd)
                    .execute();
        }
        WsAddMemberResults addMemberResults = new GcAddMember()
                .assignActAsSubject(user)
                .assignGroupName(group)
                .addSubjectIdentifier(userToAdd)
                .execute();

        updateLastModified(parentGroupingPath(group));

        return makeGroupingsServiceResult(addMemberResults, "add " + userToAdd + " to " + group);
    }

    /**
     * TODO for Grouper update
     * create a method that allows Grouping owners to see what attributes are assigned to the groups that they own
     */

    /**
     * @param username:     username of owner preforming action
     * @param group:        path to group that the member will be removed from
     * @param userToDelete: username of user to be removed from group
     * @return information about success of action
     */
    @Override
    public GroupingsServiceResult deleteMemberAs(String username, String group, String userToDelete) {
        logger.info("delteMemberAs; user: " + username + "; group: " + group + "; userToDelete: " + userToDelete);

        WsSubjectLookup user = makeWsSubjectLookup(username);
        WsDeleteMemberResults deleteMemberResults = new GcDeleteMember()
                .assignActAsSubject(user)
                .assignGroupName(group)
                .addSubjectIdentifier(userToDelete)
                .execute();

        updateLastModified(parentGroupingPath(group));

        return makeGroupingsServiceResult(deleteMemberResults, "delete " + userToDelete + " from " + group);
    }

    /**
     * @param lookup: lookup for owner
     * @param group:  path to group to be searched
     * @return results for members of the group
     */
    public WsGetMembersResults getMembers(WsSubjectLookup lookup, String group) {
        logger.info("getMembers; lookup: " + lookup + "; group: " + group);
        String username = lookup.getSubjectIdentifier();

        List<String> groupNames = Collections.singletonList(group);
        String grouping = extractGroupingNames(groupNames).get(0);
        if (isOwner(grouping, username)) {
            return new GcGetMembers()
                    .addSubjectAttributeName(SUBJECT_ATTRIBUTE_NAME_UID)
                    .assignActAsSubject(lookup)
                    .addGroupName(group)
                    .assignIncludeSubjectDetail(true)
                    .execute();
        }
        return new WsGetMembersResults();
    }

    /**
     * @param groups: list of groups paths
     * @return a list of Groupings that were is the list groups
     */
    public List<String> extractGroupings(List<String> groups) {
        List<String> groupings = new ArrayList<>();

        GcGetAttributeAssignments g = new GcGetAttributeAssignments()
                .addAttributeDefNameName(TRIO)
                .assignAttributeAssignType(ASSIGN_TYPE_GROUP);
        groups.forEach(g::addOwnerGroupName);

        WsGetAttributeAssignmentsResults attributeAssignmentsResults = g.execute();

        WsGroup[] wsGroups = attributeAssignmentsResults.getWsGroups();

        for (WsGroup grouping : wsGroups) {
            groupings.add(grouping.getName());
        }

        return groupings;
    }

    /**
     * @param username: username of user who's groups will be searched for
     * @return a list of all groups that the user is a member of
     */
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

    /**
     * @param groupingPaths: list of paths to groups that are Groupings
     * @return a list of Grouping Objects made from the list of Grouping paths
     */
    public List<Grouping> makeGroupings(List<String> groupingPaths) {
        List<Grouping> groupings = groupingPaths
                .stream()
                .map(Grouping::new)
                .collect(Collectors.toList());
        for (Grouping grouping : groupings) {
            grouping.setListservOn(hasListserv(grouping.getPath()));
            grouping.setOptInOn(optInPermission(grouping.getPath()));
            grouping.setOptOutOn(optOutPermission(grouping.getPath()));
        }
        return groupings;
    }

    /**
     * @param group: path of group to be checked
     * @return the parent Grouping of the group
     */
    public String parentGroupingPath(String group) {
        if (group.endsWith(EXCLUDE)) {
            return group.split(EXCLUDE)[0];
        } else if (group.endsWith(INCLUDE)) {
            return group.split(INCLUDE)[0];
        } else if (group.endsWith(OWNERS)) {
            return group.split(OWNERS)[0];
        } else if (group.endsWith(BASIS)) {
            return group.split(BASIS)[0];
        } else if (group.endsWith(BASIS_PLUS_INCLUDE)) {
            return group.split(BASIS_PLUS_INCLUDE)[0];
        }
        return group;
    }

    /**
     * @param groups: list of WsGroups
     * @return a list of the names of the groups in the WsGroups
     */
    public List<String> extractGroupNames(List<WsGroup> groups) {
        List<String> names = new ArrayList<>();

        groups
                .stream()
                .filter(group -> !names.contains(group.getName()))
                .forEach(group -> names.add(group.getName()));

        return names;
    }

    /**
     * @param groupNames: list of paths to groups
     * @return list of paths to the parent Groupings of the groups
     */
    private List<String> extractGroupingNames(List<String> groupNames) {
        List<String> groupingNames = new ArrayList<>();
        for (String name : groupNames) {
            if (name.endsWith(INCLUDE)) {
                groupingNames.add(name.split(INCLUDE)[0]);
            } else if (name.endsWith(EXCLUDE)) {
                groupingNames.add(name.split(EXCLUDE)[0]);
            } else if (name.endsWith(BASIS)) {
                groupingNames.add(name.split(BASIS)[0]);
            } else if (name.endsWith(BASIS_PLUS_INCLUDE)) {
                groupingNames.add(name.split(BASIS_PLUS_INCLUDE)[0]);
            } else if (name.endsWith(OWNERS)) {
                groupingNames.add(name.split(OWNERS)[0]);
            } else {
                groupingNames.add(name);
            }
        }

        return extractGroupings(groupingNames);
    }

    /**
     * @param subjects: array of WsSubjects to be made into a Group
     * @return the Group that is made
     */
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

    /**
     * @param person:
     * @return a person made from the WsSubject
     */
    public Person makePerson(WsSubject person) {
        String name = person.getName();
        String uuid = person.getId();
        String username = person.getAttributeValue(0);
        return new Person(name, uuid, username);
    }

    /**
     * @param group:         path to group who's attributes will be changed
     * @param username:      username of user preforming action
     * @param attributeName; name of attribute to be changed
     * @param attributeOn:   on if the attribute should exist false otherwise
     * @return information about success of the action
     */
    public GroupingsServiceResult changeGroupAttributeStatus(String group, String username, String attributeName, boolean attributeOn) {
        GroupingsServiceResult gsr = new GroupingsServiceResult();

        String verb = "removed from ";
        if (attributeOn) {
            verb = "added to ";
        }
        gsr.setAction(attributeName + " has been " + verb + group + " by " + username);

        if (isOwner(group, username)) {
            boolean hasAttribute = groupHasAttribute(group, attributeName);
            if (attributeOn) {
                if (!hasAttribute) {
                    assignGroupAttributes(attributeName, OPERATION_ASSIGN_ATTRIBUTE, group);

                    gsr.setResultCode("SUCCESS");
                } else {
                    gsr.setResultCode("SUCCESS, " + attributeName + " already existed");
                }
            } else {
                if (hasAttribute) {
                    assignGroupAttributes(attributeName, OPERATION_REMOVE_ATTRIBUTE, group);

                    gsr.setResultCode("SUCCESS");
                } else {
                    gsr.setResultCode("SUCCESS, " + attributeName + " did not exist");
                }
            }
        } else {
            gsr.setResultCode("FAILURE, " + username + " does not own " + group);
        }

        return gsr;
    }

    /**
     * @param resultMetadataHolder: ResultMetadataHolder that will be turned into GroupingsServiceResult
     * @param action:               the action being preformed in the resultMetadataHolder
     * @return a GroupingsServiceResult made from the ResultMetadataHolder and the action
     */
    public GroupingsServiceResult makeGroupingsServiceResult(ResultMetadataHolder resultMetadataHolder, String action) {
        GroupingsServiceResult groupingsServiceResult = new GroupingsServiceResult();
        groupingsServiceResult.setAction(action);
        groupingsServiceResult.setResultCode(resultMetadataHolder.getResultMetadata().getResultCode());

        return groupingsServiceResult;
    }

}

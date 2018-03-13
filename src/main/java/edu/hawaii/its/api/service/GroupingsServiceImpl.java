package edu.hawaii.its.api.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.GroupingsServiceResultException;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.util.Dates;

import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.*;

@Service("groupingsService")
public class GroupingsServiceImpl implements GroupingsService {
    public static final Log logger = LogFactory.getLog(GroupingsServiceImpl.class);

    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.grouping_admins}")
    private String GROUPING_ADMINS;

    @Value("${groupings.api.grouping_apps}")
    private String GROUPING_APPS;

    @Value("${groupings.api.grouping_owners}")
    private String GROUPING_OWNERS;

    @Value("${groupings.api.grouping_superusers}")
    private String GROUPING_SUPERUSERS;

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

    @Value("${groupings.api.purge_grouping}")
    private String PURGE_GROUPING;

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

    @Value("${groupings.api.stem}")
    private String STEM;

    @Value("${groupings.api.person_attributes.uuid}")
    private String UUID;

    @Value("${groupings.api.person_attributes.username}")
    private String UID;

    @Value("${groupings.api.person_attributes.first_name}")
    private String FIRST_NAME;

    @Value("${groupings.api.person_attributes.last_name}")
    private String LAST_NAME;

    @Value("${groupings.api.person_attributes.composite_name}")
    private String COMPOSITE_NAME;


    @Autowired
    private GrouperFactoryService grouperFS;

    @Autowired
    private GroupingFactoryService groupingFS;

    // Constructor.
    public GroupingsServiceImpl() {
        // Empty.
    }

    // Constructor.
    public GroupingsServiceImpl(GrouperFactoryService grouperFactory) {
        grouperFS = grouperFactory;
    }

    public GrouperFactoryService getGrouperFactoryService() {
        return grouperFS;
    }

    public void setGrouperFactoryService(GrouperFactoryService gf) {
        this.grouperFS = gf;
    }

    //todo start creating real basis group
    @Override
    public List<GroupingsServiceResult> addGrouping(
            String username,
            String groupingPath,
            List<String> basis,
            List<String> include,
            List<String> exclude,
            List<String> owners) {

                List<GroupingsServiceResult> addGroupingResults = new ArrayList<>();
                String action = username + "is adding a Grouping: " + groupingPath;



                //todo consider changing this to isAdmin. Will an app account ever need to make a Grouping?
                if (isSuperuser(username)) {

                    return groupingFS.makeGrouping(username, groupingPath, basis, include, exclude, owners);

//                    List<Group> groups = new ArrayList<>();
//
//                    List<String> basisPlusInclude = unionMemberLists(basis, include);
//
//                    Map<String, List<String>> memberLists = new HashMap<>();
//                    memberLists.put("", new ArrayList<>());
//                    memberLists.put(BASIS, basis);
//                    memberLists.put(INCLUDE, include);
//                    memberLists.put(BASIS_PLUS_INCLUDE, basisPlusInclude);
//                    memberLists.put(EXCLUDE, exclude);
//                    memberLists.put(OWNERS, owners);
//
//
//                    //todo check about making folders
//                    //todo is a folder the same as a stem?
//                    grouperFS.makeWsStemSaveResults(username, groupingPath);
//
//                    //todo always create a basis folder?
//                    grouperFS.makeWsStemSaveResults(username, groupingPath + BASIS);
//
//                    for (Map.Entry<String, List<String>> entry : memberLists.entrySet()) {
//                        Group group = makeGroup(groupingPath + entry.getKey(), entry.getValue());
//                        groups.add(group);
//                    }
//
//                    for (Group group : groups) {
//                        GroupingsServiceResult result = makeGroupingsServiceResult(
//                                grouperFS.addEmptyGroup(username, group.getPath()),
//                                action);
//                        addGroupingResults.add(result);
//                    }
//                    addGroupingResults.add(updateLastModified(groupingPath));
//
//                    for (Map.Entry<String, List<String>> entry : memberLists.entrySet()) {
//                        addGroupingResults.add(addGroupMembersByUsername(username, groupingPath + entry.getKey(), entry.getValue()));
//                        addGroupingResults.add(updateLastModified(groupingPath + entry.getKey()));
//                    }
//
//                    addGroupingResults.add(addGroupMembersByUsername(username, GROUPING_OWNERS, memberLists.get(OWNERS)));
//                    addGroupingResults.add(updateLastModified(GROUPING_OWNERS));
//
                } else {
                    GroupingsServiceResult gsr = makeGroupingsServiceResult(FAILURE + ": " + username + " does not have permission to add this grouping", action);
                    addGroupingResults.add(gsr);
                }

                return addGroupingResults;
    }

    @Override
    public List<GroupingsServiceResult> deleteGrouping(String username, String groupingPath) {

        //this method will not work until Grouper is updated

        //        List<GroupingsServiceResult> deleteGroupingResults = new ArrayList<>();
        //        if (isAdmin(username)) {
        //            deleteGroupingResults.add(assignGroupAttributes(username, PURGE_GROUPING, OPERATION_ASSIGN_ATTRIBUTE, groupingPath));
        //            deleteGroupingResults.add(assignGroupAttributes(username, TRIO, OPERATION_REMOVE_ATTRIBUTE, groupingPath));
        //        } else if (isApp(username)) {
        //            deleteGroupingResults.add(assignGroupAttributes(PURGE_GROUPING, OPERATION_ASSIGN_ATTRIBUTE, groupingPath));
        //            deleteGroupingResults.add(assignGroupAttributes(TRIO, OPERATION_REMOVE_ATTRIBUTE, groupingPath));
        //        } else {
        //            GroupingsServiceResult failureResult = makeGroupingsServiceResult(FAILURE, "delete grouping" + groupingPath);
        //
        //            deleteGroupingResults.add(failureResult);
        //        }
        //        return deleteGroupingResults;
        throw new UnsupportedOperationException();
    }

    //give ownership to a new user
    @Override
    public GroupingsServiceResult assignOwnership(String groupingPath, String ownerUsername, String newOwnerUsername) {
        logger.info("assignOwnership; groupingPath: "
                + groupingPath
                + "; ownerUsername: "
                + ownerUsername
                + "; newOwnerUsername: "
                + newOwnerUsername
                + ";");

        String action = "give " + newOwnerUsername + " ownership of " + groupingPath;
        GroupingsServiceResult ownershipResult;

        if (isOwner(groupingPath, ownerUsername) || isAdmin(ownerUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(ownerUsername);
            WsAddMemberResults amr = grouperFS.makeWsAddMemberResults(groupingPath + OWNERS, user, newOwnerUsername);
            ownershipResult = makeGroupingsServiceResult(amr, action);

            return ownershipResult;
        }

        ownershipResult = makeGroupingsServiceResult(FAILURE + ", " + ownerUsername + " does not own " + groupingPath, action);
        return ownershipResult;
    }

    //turn the listserv for a grouping on or off
    @Override
    public GroupingsServiceResult changeListservStatus(String groupingPath, String owenerUsername, boolean listservOn) {
        return changeGroupAttributeStatus(groupingPath, owenerUsername, LISTSERV, listservOn);
    }

    //turn the ability for users to opt-in to a grouping on or off
    @Override
    public List<GroupingsServiceResult> changeOptInStatus(String groupingPath, String ownerUsername, boolean optInOn) {
        List<GroupingsServiceResult> results = new ArrayList<>();
        if (isOwner(groupingPath, ownerUsername) || isAdmin(ownerUsername)) {
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_IN, groupingPath + INCLUDE, optInOn));
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, groupingPath + EXCLUDE, optInOn));
            results.add(changeGroupAttributeStatus(groupingPath, ownerUsername, OPT_IN, optInOn));
        } else {

            GroupingsServiceResult failure = makeGroupingsServiceResult(
                    FAILURE + ", " + ownerUsername + " does not own " + groupingPath,
                    "change opt in status for " + groupingPath + " to " + optInOn);
            results.add(failure);
        }
        return results;
    }

    //turn the ability for users to opt-out of a grouping on or off
    @Override
    public List<GroupingsServiceResult> changeOptOutStatus(String groupingPath, String ownerUsername, boolean optOutOn) {
        List<GroupingsServiceResult> results = new ArrayList<>();
        if (isOwner(groupingPath, ownerUsername) || isAdmin(ownerUsername)) {
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_IN, groupingPath + EXCLUDE, optOutOn));
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, groupingPath + INCLUDE, optOutOn));
            results.add(changeGroupAttributeStatus(groupingPath, ownerUsername, OPT_OUT, optOutOn));
        } else {

            GroupingsServiceResult failure = makeGroupingsServiceResult(
                    FAILURE + ", " + ownerUsername + " does not own " + groupingPath,
                    "change opt out status for " + groupingPath + " to " + optOutOn);

            results.add(failure);
        }
        return results;
    }

    //remove ownership of a grouping from a current owner
    //todo change ownerUsername to "actor"?
    @Override
    public GroupingsServiceResult removeOwnership(String groupingPath, String ownerUsername, String ownerToRemove) {
        logger.info("removeOwnership; grouping: "
                + groupingPath
                + "; username: "
                + ownerUsername
                + "; ownerToRemove: "
                + ownerToRemove
                + ";");

        GroupingsServiceResult ownershipResults;
        String action = "remove ownership of " + groupingPath + " from " + ownerToRemove;

        if (isOwner(groupingPath, ownerUsername) || isAdmin(ownerUsername)) {
            WsSubjectLookup lookup = grouperFS.makeWsSubjectLookup(ownerUsername);
            WsDeleteMemberResults memberResults = grouperFS.makeWsDeleteMemberResults(
                    groupingPath + OWNERS,
                    lookup,
                    ownerToRemove);
            ownershipResults = makeGroupingsServiceResult(memberResults, action);
            return ownershipResults;
        }

        ownershipResults = makeGroupingsServiceResult(
                FAILURE + ", " + ownerUsername + " does not own " + groupingPath,
                action);
        return ownershipResults;
    }

    //fetch a grouping from Grouper or the database
    @Override
    public Grouping getGrouping(String groupingPath, String ownerUsername) {
        logger.info("getGrouping; grouping: " + groupingPath + "; username: " + ownerUsername + ";");

        Grouping compositeGrouping = new Grouping();

        if (isOwner(groupingPath, ownerUsername) || isAdmin(ownerUsername)) {
            compositeGrouping = new Grouping(groupingPath);

            Group include = getMembers(ownerUsername, groupingPath + INCLUDE);
            Group exclude = getMembers(ownerUsername, groupingPath + EXCLUDE);
            Group basis = getMembers(ownerUsername, groupingPath + BASIS);
            Group composite = getMembers(ownerUsername, groupingPath);
            Group owners = getMembers(ownerUsername, groupingPath + OWNERS);

            compositeGrouping = setGroupingAttributes(compositeGrouping);

            compositeGrouping.setBasis(basis);
            compositeGrouping.setExclude(exclude);
            compositeGrouping.setInclude(include);
            compositeGrouping.setComposite(composite);
            compositeGrouping.setOwners(owners);

        }
        return compositeGrouping;
    }

    //get a GroupingAssignment object containing the groups that a user is in and can opt into
    @Override
    public GroupingAssignment getGroupingAssignment(String username) {
        GroupingAssignment groupingAssignment = new GroupingAssignment();
        List<String> groupPaths = getGroupPaths(username);

        groupingAssignment.setGroupingsIn(groupingsIn(groupPaths));
        groupingAssignment.setGroupingsOwned(groupingsOwned(groupPaths));
        groupingAssignment.setGroupingsToOptInTo(groupingsToOptInto(username, groupPaths));
        groupingAssignment.setGroupingsToOptOutOf(groupingsToOptOutOf(username, groupPaths));
        groupingAssignment.setGroupingsOptedOutOf(groupingsOptedOutOf(username, groupPaths));
        groupingAssignment.setGroupingsOptedInTo(groupingsOptedInto(username, groupPaths));

        return groupingAssignment;
    }

    //user adds them self to the group if they have permission
    @Override
    public List<GroupingsServiceResult> optIn(String optInUsername, String groupingPath) {
        String outOrrIn = "in ";
        String preposition = "to ";
        String addGroup = groupingPath + INCLUDE;

        return opt(optInUsername, groupingPath, addGroup, outOrrIn, preposition);
    }

    //user removes them self from the group if they have permission
    @Override
    public List<GroupingsServiceResult> optOut(String optOutUsername, String groupingPath) {
        String outOrrIn = "out ";
        String preposition = "from ";
        String addGroup = groupingPath + EXCLUDE;

        return opt(optOutUsername, groupingPath, addGroup, outOrrIn, preposition);
    }

    private List<GroupingsServiceResult> opt(String username, String grouping, String addGroup, String outOrrIn, String preposition) {

        List<GroupingsServiceResult> results = new ArrayList<>();

        if (groupOptInPermission(username, addGroup)) {

            switch (outOrrIn) {
                case "out ":
                    results.addAll(deleteGroupingMemberByUsername(username, grouping, username));

                    break;

                case "in ":
                    results.addAll(addGroupingMemberByUsername(username, grouping, username));
                    break;
            }

            if (inGroup(addGroup, username)) {
                results.add(addSelfOpted(addGroup, username));
            }
        } else {

            String action = "opt " + outOrrIn + username + " " + preposition + grouping;
            String failureResult = FAILURE
                    + ": "
                    + username
                    + " does not have permission to opt "
                    + outOrrIn
                    + preposition
                    + grouping;
            results.add(makeGroupingsServiceResult(failureResult, action));
        }
        return results;
    }

    //returns true if the grouping allows the user to opt out, false otherwise
    @Override
    public boolean optOutPermission(String groupingPath) {
        return groupHasAttribute(groupingPath, OPT_OUT);
    }

    //returns true if the grouping allows the user to opt in, false otherwise
    @Override
    public boolean optInPermission(String groupingPath) {
        return groupHasAttribute(groupingPath, OPT_IN);
    }

    //returns true if the group has the attribute with that name
    public boolean groupHasAttribute(String groupPath, String attributeName) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = attributeAssignmentsResults(
                ASSIGN_TYPE_GROUP,
                groupPath,
                attributeName);

        if (wsGetAttributeAssignmentsResults.getWsAttributeAssigns() != null) {
            for (WsAttributeAssign attribute : wsGetAttributeAssignmentsResults.getWsAttributeAssigns()) {
                if (attribute.getAttributeDefNameName() != null && attribute.getAttributeDefNameName().equals(attributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    // returns a list of all of the groups in groupPaths that are also groupings
    @Override
    public List<Grouping> groupingsIn(List<String> groupPaths) {
        List<String> groupingsIn = extractGroupings(groupPaths);

        return makeGroupings(groupingsIn);
    }

    //returns true if the grouping has a listserv, false otherwise
    @Override
    public boolean hasListserv(String groupingPath) {
        return groupHasAttribute(groupingPath, LISTSERV);
    }

    //returns a list of groupings that corresponds to all of the owner groups in groupPaths
    @Override
    public List<Grouping> groupingsOwned(List<String> groupPaths) {
        List<String> ownerGroups = groupPaths
                .stream()
                .filter(groupPath -> groupPath.endsWith(OWNERS))
                .map(groupPath -> groupPath.substring(0, groupPath.length() - OWNERS.length()))
                .collect(Collectors.toList());

        List<String> ownedGroupings = extractGroupings(ownerGroups);

        return makeGroupings(ownedGroupings);
    }

    //returns a list of all of the groupings corresponding to the include groups in groupPaths that have the self-opted attribute
    //set in the membership
    @Override
    public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths) {
        return groupingsOpted(INCLUDE, username, groupPaths);
    }

    //returns a list of all of the groupings corresponding to the exclude groups in groupPaths that have the self-opted attribute
    //set in the membership
    @Override
    public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths) {
        return groupingsOpted(EXCLUDE, username, groupPaths);
    }

    //returns a list of groupings corresponding to the include group orr exclude group (includeOrrExclude) in groupPaths that
    //have the self-opted attribute set in the membership
    private List<Grouping> groupingsOpted(String includeOrrExclude, String username, List<String> groupPaths) {
        logger.info("groupingsOpted; includeOrrExclude: " + includeOrrExclude + "; username: " + username + ";");

        List<String> groupingsOpted = new ArrayList<>();

        List<String> groupsOpted = groupPaths.stream().filter(group -> group.endsWith(includeOrrExclude)
                && checkSelfOpted(group, username)).map(this::parentGroupingPath).collect(Collectors.toList());

        if (groupsOpted.size() > 0) {

            List<WsGetAttributeAssignmentsResults> attributeAssignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsTrio(
                    ASSIGN_TYPE_GROUP,
                    TRIO,
                    groupsOpted);

            List<WsGroup> triosList = new ArrayList<>();
            for (WsGetAttributeAssignmentsResults results : attributeAssignmentsResults) {
                triosList.addAll(Arrays.asList(results.getWsGroups()));
            }

            groupingsOpted.addAll(triosList.stream().map(WsGroup::getName).collect(Collectors.toList()));
        }
        return makeGroupings(groupingsOpted);
    }

    //returns an adminLists object containing the list of all admins and all groupings
    @Override
    public AdminListsHolder adminLists(String adminUsername) {
        AdminListsHolder info = new AdminListsHolder();
        List<Grouping> groupings;

        if (isSuperuser(adminUsername)) {

            WsGetAttributeAssignmentsResults attributeAssignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsTrio(
                    ASSIGN_TYPE_GROUP,
                    TRIO);

            List<WsGroup> groups = new ArrayList<>(Arrays.asList(attributeAssignmentsResults.getWsGroups()));

            List<String> groupPaths = groups.stream().map(WsGroup::getName).collect(Collectors.toList());

            Group admin = getMembers(adminUsername, GROUPING_ADMINS);
            groupings = makeGroupings(groupPaths);
            info.setAdminGroup(admin);
            info.setAllGroupings(groupings);
        }
        return info;
    }

    //returns a list of groupings that the user is allowed to opt-out of
    private List<Grouping> groupingsToOptOutOf(String optOutUsername, List<String> groupPaths) {
        logger.info("groupingsToOptOutOf; username: " + optOutUsername + "; groupPaths: " + groupPaths + ";");

        List<String> trios = new ArrayList<>();
        List<String> opts = new ArrayList<>();
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();

        List<WsGetAttributeAssignmentsResults> assignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsTrio(
                ASSIGN_TYPE_GROUP,
                TRIO,
                OPT_OUT,
                groupPaths);

        assignmentsResults
                .stream()
                .filter(results -> results.getWsAttributeAssigns() != null)
                .forEach(results -> attributeAssigns.addAll(Arrays.asList(results.getWsAttributeAssigns())));

        if (attributeAssigns.size() > 0) {
            attributeAssigns.stream().filter(assign -> assign.getAttributeDefNameName() != null).forEach(assign -> {
                if (assign.getAttributeDefNameName().equals(TRIO)) {
                    trios.add(assign.getOwnerGroupName());
                } else if (assign.getAttributeDefNameName().equals(OPT_OUT)) {
                    opts.add(assign.getOwnerGroupName());
                }
            });

            opts.retainAll(trios);
        }

        return makeGroupings(opts);
    }

    //returns the list of groupings that the user is allowed to opt-in to
    private List<Grouping> groupingsToOptInto(String optInUsername, List<String> groupPaths) {
        logger.info("groupingsToOptInto; username: " + optInUsername + "; groupPaths : " + groupPaths + ";");

        List<String> trios = new ArrayList<>();
        List<String> opts = new ArrayList<>();
        List<String> excludes = groupPaths.stream().map(group -> group + EXCLUDE).collect(Collectors.toList());

        WsGetAttributeAssignmentsResults assignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsTrio(
                ASSIGN_TYPE_GROUP,
                TRIO,
                OPT_IN);

        if (assignmentsResults.getWsAttributeAssigns() != null) {
            for (WsAttributeAssign assign : assignmentsResults.getWsAttributeAssigns()) {
                if (assign.getAttributeDefNameName() != null) {
                    if (assign.getAttributeDefNameName().equals(TRIO)) {
                        trios.add(assign.getOwnerGroupName());
                    } else if (assign.getAttributeDefNameName().equals(OPT_IN)) {
                        opts.add(assign.getOwnerGroupName());
                    }
                }
            }

            //opts intersection trios
            opts.retainAll(trios);
            //excludes intersection opts
            excludes.retainAll(opts);
            //opts - (opts intersection groupPaths)
            opts.removeAll(groupPaths);
            //opts union excludes
            opts.addAll(excludes);

        }

        //get rid of duplicates
        List<String> groups = new ArrayList<>(new HashSet<>(opts));
        return makeGroupings(groups);
    }

    //adds the self-opted attribute to the membership between the group and user
    public GroupingsServiceResult addSelfOpted(String groupPath, String username) {
        logger.info("addSelfOpted; group: " + groupPath + "; username: " + username + ";");

        String action = "add self-opted attribute to the membership of " + username + " to " + groupPath;

        if (inGroup(groupPath, username)) {
            if (!checkSelfOpted(groupPath, username)) {
                WsGetMembershipsResults includeMembershipsResults = membershipsResults(username, groupPath);

                String membershipID = extractFirstMembershipID(includeMembershipsResults);

                return makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, membershipID),
                        action);
            }
            return makeGroupingsServiceResult(
                    SUCCESS + ", " + username + " was already self opted into " + groupPath,
                    action);
        }
        return makeGroupingsServiceResult(
                FAILURE + ", " + username + " is not a member of " + groupPath,
                action);
    }

    //return true if the membership between the group and user has the self-opted attribute, false otherwise
    @Override
    public boolean checkSelfOpted(String groupPath, String username) {
        logger.info("checkSelfOpted; group: " + groupPath + "; username: " + username + ";");

        if (inGroup(groupPath, username)) {
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(username, groupPath);
            String membershipID = extractFirstMembershipID(wsGetMembershipsResults);

            WsAttributeAssign[] wsAttributes = getMembershipAttributes(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, SELF_OPTED, membershipID);

            for (WsAttributeAssign att : wsAttributes) {
                if (att.getAttributeDefNameName() != null) {
                    if (att.getAttributeDefNameName().equals(SELF_OPTED)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //returns true if the user is a member of the group
    @Override
    public boolean inGroup(String groupPath, String username) {
        logger.info("inGroup; groupPath: " + groupPath + "; username: " + username + ";");

        WsHasMemberResults memberResults = grouperFS.makeWsHasMemberResults(groupPath, username);

        WsHasMemberResult[] memberResultArray = memberResults.getResults();

        for (WsHasMemberResult hasMember : memberResultArray) {
            if (hasMember.getResultMetadata().getResultCode().equals(IS_MEMBER)) {
                return true;
            }
        }
        return false;
    }

    //returns true if the person is a member of the group
    @Override
    public boolean inGroup(String groupPath, Person person) {
        if(person.getUsername() != null) {
            return inGroup(groupPath, person.getUsername());
        }

        WsHasMemberResults memberResults = grouperFS.makeWsHasMemberResults(groupPath, person);

        WsHasMemberResult[] memberResultArray = memberResults.getResults();

        for (WsHasMemberResult hasMember : memberResultArray) {
            if (hasMember.getResultMetadata().getResultCode().equals(IS_MEMBER)) {
                return true;
            }
        }
        return false;

    }

    //returns true if the user is in the owner group of the grouping
    @Override
    public boolean isOwner(String groupingPath, String username) {
        return inGroup(groupingPath + OWNERS, username);
    }

    //returns true if the user is in the admins group
    @Override
    public boolean isAdmin(String username) {
        return inGroup(GROUPING_ADMINS, username);
    }

    //returns true if the user is in the apps group
    @Override
    public boolean isApp(String username) {
        return inGroup(GROUPING_APPS, username);
    }

    //returns true if the user is in the superusers group
    @Override
    public boolean isSuperuser(String username) {
        return isAdmin(username) || isApp(username);
    }

    //removes the self-opted attribute from the membership that corresponds to the user and group
    public GroupingsServiceResult removeSelfOpted(String groupPath, String username) {
        logger.info("removeSelfOpted; group: " + groupPath + "; username: " + username + ";");

        String action = "remove self-opted attribute from the membership of " + username + " to " + groupPath;

        if (inGroup(groupPath, username)) {
            if (checkSelfOpted(groupPath, username)) {
                WsGetMembershipsResults membershipsResults = membershipsResults(username, groupPath);
                String membershipID = extractFirstMembershipID(membershipsResults);

                return makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_REMOVE_ATTRIBUTE, SELF_OPTED, membershipID),
                        action);
            }
            return makeGroupingsServiceResult(
                    SUCCESS + ", " + username + " was not self-opted into " + groupPath,
                    action);
        }
        return makeGroupingsServiceResult(
                FAILURE + ", " + username + " is not a member of " + groupPath,
                action);
    }

    //returns the first membership id in the list of membership ids inside of the WsGerMembershipsResults object
    String extractFirstMembershipID(WsGetMembershipsResults wsGetMembershipsResults) {
        if (wsGetMembershipsResults != null
                && wsGetMembershipsResults.getWsMemberships() != null
                && wsGetMembershipsResults.getWsMemberships()[0] != null
                && wsGetMembershipsResults.getWsMemberships()[0].getMembershipId() != null) {

            return wsGetMembershipsResults
                    .getWsMemberships()[0]
                    .getMembershipId();
        }
        return "";
    }

    /*
     * @return date and time in yyyymmddThhmm format
     * ex. 20170314T0923
     */
    private String wsDateTime() {
        return Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");
    }

    //returns true if the group allows that user to opt out
    @Override
    public boolean groupOptOutPermission(String optOutUsername, String groupPath) {
        logger.info("groupOptOutPermission; group: " + groupPath + "; username: " + optOutUsername + ";");
        WsGetGrouperPrivilegesLiteResult result = getGrouperPrivilege(optOutUsername, PRIVILEGE_OPT_OUT, groupPath);

        return result
                .getResultMetadata()
                .getResultCode()
                .equals(SUCCESS_ALLOWED);
    }

    //returns true if the group allows that user to opt in
    @Override
    public boolean groupOptInPermission(String optInUsername, String groupPath) {
        logger.info("groupOptInPermission; group: " + groupPath + "; username: " + optInUsername + ";");

        WsGetGrouperPrivilegesLiteResult result = getGrouperPrivilege(optInUsername, PRIVILEGE_OPT_IN, groupPath);

        return result
                .getResultMetadata()
                .getResultCode()
                .equals(SUCCESS_ALLOWED);
    }


    //updates the last modified attribute of the group to the current date and time
    public GroupingsServiceResult updateLastModified(String groupPath) {
        logger.info("updateLastModified; group: " + groupPath + ";");
        String time = wsDateTime();
        WsAttributeAssignValue dateTimeValue = grouperFS.makeWsAttributeAssignValue(time);

        WsAssignAttributesResults assignAttributesResults = grouperFS.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                groupPath,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTimeValue);

        return makeGroupingsServiceResult(assignAttributesResults,
                "update last-modified attribute for " + groupPath + " to time " + time);

    }

    //adds, removes, updates (operationName) the attribute for the membership
    private WsAssignAttributesResults assignMembershipAttributes(String operationName, String attributeUuid, String membershipID) {
        logger.info("assignMembershipAttributes; operation: "
                + operationName
                + "; uuid: "
                + attributeUuid
                + "; membershipID: "
                + membershipID
                + ";");

        return grouperFS.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, operationName, attributeUuid, membershipID);
    }

    //checks to see if a membership has an attribute of a specific type and returns the list if it does
    private WsAttributeAssign[] getMembershipAttributes(String assignType, String attributeUuid, String membershipID) {
        logger.info("getMembershipAttributes; assignType: "
                + assignType
                + "; name: "
                + attributeUuid
                + "; membershipID: "
                + membershipID
                + ";");

        WsGetAttributeAssignmentsResults attributeAssignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsForMembership(
                assignType,
                attributeUuid,
                membershipID);

        WsAttributeAssign[] wsAttributes = attributeAssignmentsResults.getWsAttributeAssigns();

        return wsAttributes != null ? wsAttributes : grouperFS.makeEmptyWsAttributeAssignArray();
    }

    //adds, removes, updates (operationName) the attribute for the group
    private GroupingsServiceResult assignGroupAttributes(String attributeName, String attributeOperation, String groupPath) {
        logger.info("assignGroupAttributes; "
                + "; attributeName: "
                + attributeName
                + "; attributeOperation: "
                + attributeOperation
                + "; group: "
                + groupPath
                + ";");

        WsAssignAttributesResults attributesResults = grouperFS.makeWsAssignAttributesResultsForGroup(
                ASSIGN_TYPE_GROUP,
                attributeOperation,
                attributeName,
                groupPath);

        return makeGroupingsServiceResult(attributesResults, "assign " + attributeName + " attribute to " + groupPath);
    }

    //checks to see if a group has an attribute of a specific type and returns the list if it does
    WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String groupPath, String attributeName) {
        logger.info("attributeAssignmentsResults; assignType: "
                + assignType
                + "; group: "
                + groupPath
                + "; nameName: "
                + attributeName
                + ";");

        return grouperFS.makeWsGetAttributeAssignmentsResultsForGroup(assignType, attributeName, groupPath);
    }

    //checks to see if the user has the privilege in that group
    private WsGetGrouperPrivilegesLiteResult getGrouperPrivilege(String username, String privilegeName, String groupPath) {
        logger.info("getGrouperPrivilege; username: "
                + username
                + "; group: "
                + groupPath
                + "; privilegeName: "
                + privilegeName
                + ";");

        WsSubjectLookup lookup = grouperFS.makeWsSubjectLookup(username);

        return grouperFS.makeWsGetGrouperPrivilegesLiteResult(groupPath, privilegeName, lookup);
    }

    //gives the user the privilege for that group
    private GroupingsServiceResult assignGrouperPrivilege(
            String username,
            String privilegeName,
            String groupPath,
            boolean set) {

        logger.info("assignGrouperPrivilege; username: "
                + username
                + "; group: "
                + groupPath
                + "; privilegeName: "
                + privilegeName
                + " set: "
                + set
                + ";");

        WsSubjectLookup lookup = grouperFS.makeWsSubjectLookup(username);
        String action = "set " + privilegeName + " " + set + " for " + username + " in " + groupPath;

        WsAssignGrouperPrivilegesLiteResult grouperPrivilegesLiteResult = grouperFS.makeWsAssignGrouperPrivilegesLiteResult(
                groupPath,
                privilegeName,
                lookup,
                set);

        return makeGroupingsServiceResult(grouperPrivilegesLiteResult, action);
    }

    //returns a list of groups that the user belongs to inside of a WsGetMembershipsResults object
    private WsGetMembershipsResults membershipsResults(String username, String group) {
        logger.info("membershipResults; username: " + username + "; group: " + group + ";");

        WsSubjectLookup lookup = grouperFS.makeWsSubjectLookup(username);

        return grouperFS.makeWsGetMembershipsResults(group, lookup);
    }

    //adds a user to the admins group
    @Override
    public GroupingsServiceResult addAdmin(String currentAdminUsername, String newAdminUsername) {
        logger.info("addAdmin; username: " + currentAdminUsername + "; newAdmin: " + newAdminUsername + ";");

        String action = "add " + newAdminUsername + " to " + GROUPING_ADMINS;

        if (isSuperuser(currentAdminUsername)) {
            if (isAdmin(newAdminUsername)) {
                return makeGroupingsServiceResult("SUCCESS: " + newAdminUsername + " was already in" + GROUPING_ADMINS, action);
            }
            WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(
                    GROUPING_ADMINS,
                    newAdminUsername);

            return makeGroupingsServiceResult(addMemberResults, action);
        }

        return makeGroupingsServiceResult("FAILURE: " + currentAdminUsername + " is not an admin", action);
    }

    //removes a user from the admins group
    @Override
    public GroupingsServiceResult deleteAdmin(String adminUsername, String adminToDeleteUsername) {
        logger.info("deleteAdmin; username: " + adminUsername + "; adminToDelete: " + adminToDeleteUsername + ";");

        String action = "delete " + adminToDeleteUsername + " from " + GROUPING_ADMINS;

        if (isSuperuser(adminUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(adminUsername);

            WsDeleteMemberResults deleteMemberResults = grouperFS.makeWsDeleteMemberResults(
                    GROUPING_ADMINS,
                    user,
                    adminToDeleteUsername);

            return makeGroupingsServiceResult(deleteMemberResults, action);
        }
        return makeGroupingsServiceResult("FAILURE: " + adminUsername + " is not an admin", action);
    }

    //logic for adding a member
    private List<GroupingsServiceResult> addMemberHelper(String username, String groupPath, Person personToAdd){
        logger.info("addMemberHelper; user: " + username + "; group: " + groupPath + "; personToAdd: " + personToAdd + ";");

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        String action = "add users to " + groupPath;

        if (isOwner(parentGroupingPath(groupPath), username) || isSuperuser(username) || personToAdd.getUsername().equals(username)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(username);
            String composite = parentGroupingPath(groupPath);
            String exclude = composite + EXCLUDE;
            String include = composite + INCLUDE;
            String owners = composite + OWNERS;

            boolean updateComposite = false;
            boolean updateExclude = false;
            boolean updateInclude = false;
            boolean updateOwners = false;

            //check to see if it is the include, exclude or owners
            if (groupPath.endsWith(INCLUDE)) {
                //if personToAdd is in exclude, get them out
                if (inGroup(exclude, personToAdd)) {
                    WsDeleteMemberResults wsDeleteMemberResults = grouperFS.makeWsDeleteMemberResults(
                            exclude,
                            user,
                            personToAdd);

                    updateExclude = true;

                    gsrList.add(makeGroupingsServiceResult(wsDeleteMemberResults, "delete " + personToAdd.toString() + " from " + exclude));
                }
                //check to see if personToAdd is already in include
                if (!inGroup(include, personToAdd)) {
                    //add to include
                    WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(include, user, personToAdd);

                    updateInclude = true;

                    gsrList.add(makeGroupingsServiceResult(addMemberResults, action));
                } else {
                    //They are already in the group, so just return SUCCESS
                    gsrList.add(makeGroupingsServiceResult(SUCCESS + ": " + personToAdd.toString() + " was already in " + groupPath, action));
                }
            }

            //if exclude check if personToAdd is in the include
            else if (groupPath.endsWith(EXCLUDE)) {
                //if personToAdd is in include, get them out
                if (inGroup(include, personToAdd)) {
                    WsDeleteMemberResults wsDeleteMemberResults = grouperFS.makeWsDeleteMemberResults(
                            include,
                            user,
                            personToAdd);

                    updateInclude = true;

                    gsrList.add(makeGroupingsServiceResult(wsDeleteMemberResults, "delete " + personToAdd.toString() + " from " + include));
                }
                //check to see if userToAdd is already in exclude
                if (!inGroup(exclude, personToAdd)) {
                    //add to exclude
                    WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(exclude, user, personToAdd);

                    updateExclude = true;

                    gsrList.add(makeGroupingsServiceResult(addMemberResults, action));
                }
                //They are already in the group, so just return SUCCESS
                gsrList.add(makeGroupingsServiceResult(SUCCESS + ": " + personToAdd.toString() + " was already in " + groupPath, action));

            }
            //if owners check to see if the user is already in owners
            else if (groupPath.endsWith(OWNERS)) {
                //check to see if userToAdd is already in owners
                if (!inGroup(owners, personToAdd)) {
                    //add userToAdd to owners
                    WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(owners, user, personToAdd);

                    updateOwners = true;

                    gsrList.add(makeGroupingsServiceResult(addMemberResults, action));
                }
                //They are already in the group, so just return SUCCESS
                gsrList.add(makeGroupingsServiceResult(SUCCESS + ": " + personToAdd.toString() + " was already in " + groupPath, action));
            }
            //Owners can only change include, exclude and owners groups
            else {
                gsrList.add(makeGroupingsServiceResult(FAILURE + ": " + username + " may only add to exclude, include or owner group", action));
            }

            //update groups that were changed
            if (updateExclude) {
                updateLastModified(exclude);
                updateComposite = true;
            }
            if (updateInclude) {
                updateLastModified(include);
                updateComposite = true;
            }
            if (updateComposite) {
                updateLastModified(composite);
            }
            if (updateOwners) {
                updateLastModified(owners);
            }
        } else {
            gsrList.add(makeGroupingsServiceResult(FAILURE + ": " + username + "does not have permission to edit " + groupPath, action));
        }

        return gsrList;
    }

    //finds a user by a username and adds that user to the group
    @Override
    public List<GroupingsServiceResult> addGroupMemberByUsername(String ownerUsername, String groupPath, String userToAddUsername) {
        logger.info("addGroupMemberByUsername; user: " + ownerUsername + "; groupPath: " + groupPath + "; userToAdd: " + userToAddUsername + ";");

        Person personToAdd = new Person(null, null, userToAddUsername);
        return addMemberHelper(ownerUsername, groupPath, personToAdd);
    }

    //finds all the user from a list of usernames and adds them to the group
    @Override
    public List<GroupingsServiceResult> addGroupMembersByUsername(String ownerUsername, String groupPath, List<String> usernamesToAdd) {
        logger.info("addGroupMembersByUsername; user: " + ownerUsername + "; group: " + groupPath + "; usersToAddUsername: " + usernamesToAdd + ";");
        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        for (String userToAdd : usernamesToAdd) {
            gsrList.addAll(addGroupMemberByUsername(ownerUsername, groupPath, userToAdd));
        }
        return gsrList;
    }

    //finds a user by a uuid and adds them to the group
    @Override
    public List<GroupingsServiceResult> addGroupMemberByUuid(String ownerUsername, String groupPath, String userToAddUuid) {
        logger.info("addGroupMemberByUuid; user: " + ownerUsername + "; groupPath: " + groupPath + "; userToAdd: " + userToAddUuid + ";");

        Person personToAdd = new Person(null, userToAddUuid, null);
        return addMemberHelper(ownerUsername, groupPath, personToAdd);
    }

    //finds all the user from a list of uuids and adds them to the group
    @Override
    public List<GroupingsServiceResult> addGroupMembersByUuid(String ownerUsername, String groupPath, List<String> usersToAddUuid) {
        logger.info("addGroupMembersByUuid; user: " + ownerUsername + "; groupPath: " + groupPath + "; usersToAddUuid: " + usersToAddUuid + ";");
        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        for (String userToAdd : usersToAddUuid) {
            gsrList.addAll(addGroupMemberByUuid(ownerUsername, groupPath, userToAdd));
        }
        return gsrList;
    }

    //finds a user by a username and adds them to a grouping
    @Override
    public List<GroupingsServiceResult> addGroupingMemberByUsername(String ownerUsername, String groupingPath, String userToAddUsername) {
        logger.info("addGroupingMemberByUsername; user: " + ownerUsername + "; group: " + groupingPath + "; usersToAdd: " + userToAddUsername + ";");

        List<GroupingsServiceResult> gsrs = new ArrayList<>();

        String action = "add user to " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        boolean inBasis = inGroup(basis, userToAddUsername);
        boolean inComposite = inGroup(groupingPath, userToAddUsername);
        boolean inInclude = inGroup(include, userToAddUsername);

        //check to see if they are already in the grouping
        if (!inComposite) {
            //get them out of the exclude
            gsrs.add(deleteGroupMemberByUsername(ownerUsername, exclude, userToAddUsername));
            //only add them to the include if they are not in the basis
            if (!inBasis) {
                gsrs.addAll(addGroupMemberByUsername(ownerUsername, include, userToAddUsername));
            } else {
                gsrs.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAddUsername + " was in " + basis, action));
            }
        } else {
            gsrs.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAddUsername + " was already in " + groupingPath, action));
        }
        //should only be in one or the other
        if (inBasis && inInclude) {
            gsrs.add(deleteGroupMemberByUsername(ownerUsername, include, userToAddUsername));
        }

        return gsrs;
    }

    //todo
    //find a user by a uuid and add them to a grouping
    @Override
    public List<GroupingsServiceResult> addGroupingMemberByUuid(String username, String groupingPath, String userToAddUuid) {
        logger.info("addGroupingMemberByUuid; user: " + username + "; grouping: " + groupingPath + "; userToAdd: " + userToAddUuid + ";");

        List<GroupingsServiceResult> gsrs = new ArrayList<>();

        String action = "add user to " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        Person personToAdd = new Person(null, userToAddUuid, null);

        boolean inBasis = inGroup(basis, personToAdd);
        boolean inComposite = inGroup(groupingPath, personToAdd);
        boolean inInclude = inGroup(include, personToAdd);

        //check to see if they are already in the grouping
        if (!inComposite) {
            //get them out of the exclude
            gsrs.add(deleteGroupMemberByUuid(username, exclude, userToAddUuid));
            //only add them to the include if they are not in the basis
            if (!inBasis) {
                gsrs.addAll(addGroupMemberByUuid(username, include, userToAddUuid));
            } else {
                gsrs.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAddUuid + " was in " + basis, action));
            }
        } else {
            gsrs.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAddUuid + " was already in " + groupingPath, action));
        }
        //should only be in one or the other
        if (inBasis && inInclude) {
            gsrs.add(deleteGroupMemberByUuid(username, include, userToAddUuid));
        }

        return gsrs;
    }

    //find a user by a username and remove them from the grouping
    @Override
    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String ownerUsername, String groupingPath, String userToDeleteUsername) {
        logger.info("deleteGroupingMemberByUsername; username: "
                + ownerUsername
                + "; groupingPath: "
                + groupingPath + "; userToDelete: "
                + userToDeleteUsername
                + ";");

        List<GroupingsServiceResult> gsrList = new ArrayList<>();

        String action = ownerUsername + " deletes " + userToDeleteUsername + " from " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        boolean inBasis = inGroup(basis, userToDeleteUsername);
        boolean inComposite = inGroup(groupingPath, userToDeleteUsername);
        boolean inExclude = inGroup(exclude, userToDeleteUsername);

        //if they are in the include group, get them out
        gsrList.add(deleteGroupMemberByUsername(ownerUsername, include, userToDeleteUsername));

        //make sure userToDelete is actually in the Grouping
        if (inComposite) {
            //if they are not in the include group, then they are in the basis, so add them to the exclude group
            if (inBasis) {
                gsrList.addAll(addGroupMemberByUsername(ownerUsername, exclude, userToDeleteUsername));
            }
        }
        //since they are not in the Grouping, do nothing, but return SUCCESS
        else {
            gsrList.add(makeGroupingsServiceResult(SUCCESS + userToDeleteUsername + " was not in " + groupingPath, action));
        }

        //should not be in exclude if not in basis
        if (!inBasis && inExclude) {
            gsrList.add(deleteGroupMemberByUsername(ownerUsername, exclude, userToDeleteUsername));
        }

        return gsrList;
    }

    //todo
    //finds a user by a uuid and remove them from a grouping
    @Override
    public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String ownerUsername, String groupingPath, String userToDeleteUuid) {
        logger.info("deleteGroupingMemberByUuid; ownerUsername: "
                + ownerUsername
                + "; groupingPath: "
                + groupingPath + "; userToDelete: "
                + userToDeleteUuid
                + ";");

        List<GroupingsServiceResult> gsrList = new ArrayList<>();

        String action = ownerUsername + " deletes " + userToDeleteUuid + " from " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        Person personToDelete = new Person(null, userToDeleteUuid, null);

        boolean inBasis = inGroup(basis, personToDelete);
        boolean inComposite = inGroup(groupingPath, personToDelete);
        boolean inExclude = inGroup(exclude, personToDelete);

        //if they are in the include group, get them out
        gsrList.add(deleteGroupMemberByUuid(ownerUsername, include, userToDeleteUuid));

        //make sure userToDelete is actually in the Grouping
        if (inComposite) {
            //if they are not in the include group, then they are in the basis, so add them to the exclude group
            if (inBasis) {
                gsrList.addAll(addGroupMemberByUuid(ownerUsername, exclude, userToDeleteUuid));
            }
        }
        //since they are not in the Grouping, do nothing, but return SUCCESS
        else {
            gsrList.add(makeGroupingsServiceResult(SUCCESS + userToDeleteUuid + " was not in " + groupingPath, action));
        }

        //should not be in exclude if not in basis
        if (!inBasis && inExclude) {
            gsrList.add(deleteGroupMemberByUuid(ownerUsername, exclude, userToDeleteUuid));
        }

        return gsrList;
    }

    //find a user by a username and remove them from a group
    @Override
    public GroupingsServiceResult deleteGroupMemberByUsername(String ownerUsername, String groupPath, String userToDeleteUsername) {
        logger.info("deleteGroupMemberByUsername; user: " + ownerUsername
                + "; group: " + groupPath
                + "; userToDelete: " + userToDeleteUsername
                + ";");

        String action = "delete " + userToDeleteUsername + " from " + groupPath;

        String composite = parentGroupingPath(groupPath);

        if (isOwner(composite, ownerUsername) || isSuperuser(ownerUsername) || userToDeleteUsername.equals(ownerUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(ownerUsername);
            if (groupPath.endsWith(EXCLUDE) || groupPath.endsWith(INCLUDE) || groupPath.endsWith(OWNERS)) {
                if (inGroup(groupPath, userToDeleteUsername)) {
                    WsDeleteMemberResults deleteMemberResults = grouperFS.makeWsDeleteMemberResults(groupPath, user, userToDeleteUsername);

                    updateLastModified(composite);
                    updateLastModified(groupPath);
                    return makeGroupingsServiceResult(deleteMemberResults, action);
                }
                return makeGroupingsServiceResult(SUCCESS + ": " + ownerUsername + " was not in " + groupPath, action);
            }
            return makeGroupingsServiceResult(FAILURE + ": " + ownerUsername + " may only delete from exclude, include or owner group", action);
        }
        return makeGroupingsServiceResult(FAILURE + ": " + ownerUsername + " does not have permission to edit " + groupPath, action);
    }

    @Override
    public GroupingsServiceResult deleteGroupMemberByUuid(String ownerUsername, String groupPath, String userToDeleteUuid) {
        logger.info("deleteGroupMemberByUuid; user: " + ownerUsername
                + "; group: " + groupPath
                + "; userToDelete: " + userToDeleteUuid
                + ";");

        String action = "delete " + userToDeleteUuid + " from " + groupPath;
        Person personToDelete = new Person(null, userToDeleteUuid, null);

        String composite = parentGroupingPath(groupPath);

        if (isOwner(composite, ownerUsername) || isSuperuser(ownerUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(ownerUsername);
            if (groupPath.endsWith(EXCLUDE) || groupPath.endsWith(INCLUDE) || groupPath.endsWith(OWNERS)) {
                if (inGroup(groupPath, personToDelete)) {
                    WsDeleteMemberResults deleteMemberResults = grouperFS.makeWsDeleteMemberResults(groupPath, user, personToDelete);

                    updateLastModified(composite);
                    updateLastModified(groupPath);
                    return makeGroupingsServiceResult(deleteMemberResults, action);
                }
                return makeGroupingsServiceResult(SUCCESS + ": " + ownerUsername + " was not in " + groupPath, action);
            }
            return makeGroupingsServiceResult(FAILURE + ": " + ownerUsername + " may only delete from exclude, include or owner group", action);
        }
        return makeGroupingsServiceResult(FAILURE + ": " + ownerUsername + " does not have permission to edit " + groupPath, action);
    }

    //returns a group from grouper or the database
    Group getMembers(String owenrUsername, String groupPath) {
        logger.info("getMembers; user: " + owenrUsername + "; group: " + groupPath + ";");

        WsSubjectLookup lookup = grouperFS.makeWsSubjectLookup(owenrUsername);
        WsGetMembersResults members = grouperFS.makeWsGetMembersResults(
                SUBJECT_ATTRIBUTE_NAME_UID,
                lookup,
                groupPath);

        //todo should we use EmptyGroup?
        Group groupMembers = new Group();
        if (members.getResults() != null) {
            groupMembers = makeGroup(members);
        }
        return groupMembers;
    }

    //returns the list of all of the groups in groupPaths that are also groupings
    private List<String> extractGroupings(List<String> groupPaths) {
        logger.info("extractGroupings; groupPaths: " + groupPaths + ";");

        List<String> groupings = new ArrayList<>();
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();

        if (groupPaths.size() > 0) {

            List<WsGetAttributeAssignmentsResults> attributeAssignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsTrio(
                    ASSIGN_TYPE_GROUP,
                    TRIO,
                    groupPaths);

            attributeAssignmentsResults
                    .stream()
                    .filter(results -> results.getWsAttributeAssigns() != null)
                    .forEach(results -> attributeAssigns.addAll(Arrays.asList(results.getWsAttributeAssigns())));

            if (attributeAssigns.size() > 0) {
                groupings.addAll(attributeAssigns.stream().map(WsAttributeAssign::getOwnerGroupName).collect(Collectors.toList()));
            }
        }
        return groupings;
    }

    //returns the list of groups that a user is in
    List<String> getGroupPaths(String username) {
        logger.info("getGroupPaths; username: " + username + ";");
        WsStemLookup stemLookup = grouperFS.makeWsStemLookup(STEM);

        WsGetGroupsResults wsGetGroupsResults = grouperFS.makeWsGetGroupsResults(
                username,
                stemLookup,
                StemScope.ALL_IN_SUBTREE);

        WsGetGroupsResult groupResults = wsGetGroupsResults.getResults()[0];

        List<WsGroup> groups = new ArrayList<>();

        if(groupResults.getWsGroups() != null) {
            groups = new ArrayList<>(Arrays.asList(groupResults.getWsGroups()));
        }

        return extractGroupPaths(groups);
    }

    //sets the attributes of a grouping in grouper or the database to match the attributes of the supplied grouping
    private Grouping setGroupingAttributes(Grouping grouping) {
        logger.info("setGroupingAttributes; grouping: " + grouping + ";");
        boolean listservOn = false;
        boolean optInOn = false;
        boolean optOutOn = false;

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = grouperFS.makeWsGetAttributeAssignmentsResultsForGroup(
                ASSIGN_TYPE_GROUP,
                grouping.getPath());

        WsAttributeDefName[] attributeDefNames = wsGetAttributeAssignmentsResults.getWsAttributeDefNames();
        if (attributeDefNames != null && attributeDefNames.length > 0) {
            for (WsAttributeDefName defName : attributeDefNames) {
                String name = defName.getName();
                if (name.equals(LISTSERV)) {
                    listservOn = true;
                } else if (name.equals(OPT_IN)) {
                    optInOn = true;
                } else if (name.equals(OPT_OUT)) {
                    optOutOn = true;
                }
            }
        }

        grouping.setListservOn(listservOn);
        grouping.setOptInOn(optInOn);
        grouping.setOptOutOn(optOutOn);

        return grouping;
    }

    //removes one of the words (:exclude, :include, :owners ...) from the end of the string
    public String parentGroupingPath(String group) {
        if (group != null) {
            if (group.endsWith(EXCLUDE)) {
                return group.substring(0, group.length() - EXCLUDE.length());
            } else if (group.endsWith(INCLUDE)) {
                return group.substring(0, group.length() - INCLUDE.length());
            } else if (group.endsWith(OWNERS)) {
                return group.substring(0, group.length() - OWNERS.length());
            } else if (group.endsWith(BASIS)) {
                return group.substring(0, group.length() - BASIS.length());
            } else if (group.endsWith(BASIS_PLUS_INCLUDE)) {
                return group.substring(0, group.length() - BASIS_PLUS_INCLUDE.length());
            }
            return group;
        }
        return "";
    }

    //take a list of WsGroups ans return a list of the paths for all of those groups
    List<String> extractGroupPaths(List<WsGroup> groups) {
        List<String> names = new ArrayList<>();
        if (groups != null) {
            groups.stream()
                    .filter(group -> !names.contains(group.getName()))
                    .forEach(group -> names.add(group.getName()));
        }
        return names;
    }

    //turns the attribute on or off in a group
    private GroupingsServiceResult changeGroupAttributeStatus(String groupPath, String ownerUsername, String attributeName, boolean attributeOn) {
        GroupingsServiceResult gsr;

        String verb = "removed from ";
        if (attributeOn) {
            verb = "added to ";
        }
        String action = attributeName + " has been " + verb + groupPath + " by " + ownerUsername;

        if (isOwner(groupPath, ownerUsername) || isAdmin(ownerUsername)) {
            boolean hasAttribute = groupHasAttribute(groupPath, attributeName);
            if (attributeOn) {
                if (!hasAttribute) {
                    assignGroupAttributes(attributeName, OPERATION_ASSIGN_ATTRIBUTE, groupPath);

                    gsr = makeGroupingsServiceResult(SUCCESS, action);
                } else {
                    gsr = makeGroupingsServiceResult(SUCCESS + ", " + attributeName + " already existed", action);
                }
            } else {
                if (hasAttribute) {
                    assignGroupAttributes(attributeName, OPERATION_REMOVE_ATTRIBUTE, groupPath);

                    gsr = makeGroupingsServiceResult(SUCCESS, action);
                } else {
                    gsr = makeGroupingsServiceResult(SUCCESS + ", " + attributeName + " did not exist", action);
                }
            }
        } else {
            gsr = makeGroupingsServiceResult(FAILURE + ", " + ownerUsername + "does not have permission to set " + attributeName
                    + " because " + ownerUsername + " does not own " + groupPath, action);
        }

        return gsr;
    }

    @Override
    public String toString() {
        return "GroupingsServiceImpl [SETTINGS=" + SETTINGS + "]";
    }

    /////////////////////////////////////////////////////
    ////Factory Methods
    /////////////////////////////////////////////////////

    //makes a groupingsServiceResult with the result code from the metadataHolder and the action string
    GroupingsServiceResult makeGroupingsServiceResult(ResultMetadataHolder resultMetadataHolder, String action) {
        GroupingsServiceResult groupingsServiceResult = new GroupingsServiceResult();
        groupingsServiceResult.setAction(action);
        groupingsServiceResult.setResultCode(resultMetadataHolder.getResultMetadata().getResultCode());

        if (groupingsServiceResult.getResultCode().startsWith(FAILURE)) {
            throw new GroupingsServiceResultException(groupingsServiceResult);
        }

        return groupingsServiceResult;
    }

    //makes a groupingsServiceResult with the resultCode and the action string
    private GroupingsServiceResult makeGroupingsServiceResult(String resultCode, String action) {
        GroupingsServiceResult groupingsServiceResult = new GroupingsServiceResult();
        groupingsServiceResult.setAction(action);
        groupingsServiceResult.setResultCode(resultCode);

        if (groupingsServiceResult.getResultCode().startsWith(FAILURE)) {
            throw new GroupingsServiceResultException(groupingsServiceResult);
        }

        return groupingsServiceResult;
    }

    //makes a list of groupings each with a path fro the list
    List<Grouping> makeGroupings(List<String> groupingPaths) {
        logger.info("makeGroupings; groupingPaths: " + groupingPaths + ";");

        List<Grouping> groupings = new ArrayList<>();
        if (groupingPaths.size() > 0) {
            groupings = groupingPaths
                    .stream()
                    .map(Grouping::new)
                    .collect(Collectors.toList());
        }

        return groupings;
    }

    //makes a group filled with members from membersResults
    Group makeGroup(WsGetMembersResults membersResults) {
        Group group = new Group();
        try {
            WsSubject[] subjects = membersResults.getResults()[0].getWsSubjects();
            String[] attributeNames = membersResults.getSubjectAttributeNames();

            if (subjects.length > 0) {
                for (WsSubject subject : subjects) {
                    if (subject != null) {
                        group.addMember(makePerson(subject, attributeNames));
                    }
                }
            }
        } catch (NullPointerException npe) {
            return new Group();
        }

        return group;
    }

    //makes a person from a WsSubject
    //todo do we still need this method?
    Person makePerson(WsSubject person) {
        if (person != null) {
            String username = null;
            String name = person.getName();
            String uuid = person.getId();
            if (person.getAttributeValues() != null) {
                username = person.getAttributeValue(0);
            }
            return new Person(name, uuid, username);
        }
        return new Person();
    }

    //makes a person with all attributes in attributeNames
    private Person makePerson(WsSubject subject, String[] attributeNames) {
        if (subject == null || subject.getAttributeValues() == null) {
            return new Person();
        } else {

            Map<String, String> attributes = new HashMap<>();
            for (int i = 0; i < subject.getAttributeValues().length; i++) {
                attributes.put(attributeNames[i], subject.getAttributeValue(i));
            }
            //uuid is the only attribute not actually in the WsSubject attribute array
            attributes.put(UUID, subject.getId());

            return new Person(attributes);
        }
    }
}
package edu.hawaii.its.api.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.jar.Attributes;
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

    @Value("$groupings.api.stem}")
    private String STEM;

    @Autowired
    private GrouperFactoryService gf;

    // Constructor.
    public GroupingsServiceImpl() {
        // Empty.
    }

    // Constructor.
    public GroupingsServiceImpl(GrouperFactoryService grouperFactory) {
        gf = grouperFactory;
    }

    public GrouperFactoryService getGrouperFactoryService() {
        return gf;
    }

    public void setGrouperFactoryService(GrouperFactoryService gf) {
        this.gf = gf;
    }

    @Override
    public List<GroupingsServiceResult> addGrouping(
            String username,
            String path,
            List<String> basis,
            List<String> include,
            List<String> exclude,
            List<String> owners) {

        //This method will not work until Grouper is updated

        //        List<GroupingsServiceResult> addGroupingResults = new ArrayList<>();
        //        String action = username + "is adding a Grouping located at path " + path;
        //
        //        //todo consider changing this to isAdmin. Will an app account ever need to make a Grouping?
        //        if (isSuperuser(username)) {
        //
        //            List<Group> groups = new ArrayList<>();
        //
        //            List<String> basisPlusInclude = unionMemberLists(basis, include);
        //
        //            Map<String, List<String>> memberLists = new HashMap<>();
        //            memberLists.put("", new ArrayList<>());
        //            memberLists.put(BASIS, basis);
        //            memberLists.put(INCLUDE, include);
        //            memberLists.put(BASIS_PLUS_INCLUDE, basisPlusInclude);
        //            memberLists.put(EXCLUDE, exclude);
        //            memberLists.put(OWNERS, owners);
        //
        //
        //            //todo check about making folders
        //            //todo is a folder the same as a stem?
        //            gf.makeWsStemSaveResults(username, path);
        //
        //            //todo always create a basis folder?
        //            gf.makeWsStemSaveResults(username, path + BASIS);
        //
        //            for (Map.Entry<String, List<String>> entry : memberLists.entrySet()) {
        //                Group group = makeGroup(path + entry.getKey(), entry.getValue());
        //                groups.add(group);
        //            }
        //
        //            for (Group group : groups) {
        //                GroupingsServiceResult result = makeGroupingsServiceResult(
        //                        gf.addEmptyGroup(username, group.getPath()),
        //                        action);
        //                addGroupingResults.add(result);
        //            }
        //            addGroupingResults.add(updateLastModified(path));
        //
        //            for (Map.Entry<String, List<String>> entry : memberLists.entrySet()) {
        //                addGroupingResults.add(addMembersAs(username, path + entry.getKey(), entry.getValue()));
        //                addGroupingResults.add(updateLastModified(path + entry.getKey()));
        //            }
        //
        //            addGroupingResults.add(addMembersAs(username, GROUPING_OWNERS, memberLists.get(OWNERS)));
        //            addGroupingResults.add(updateLastModified(GROUPING_OWNERS));
        //
        //        } else {
        //            GroupingsServiceResult gsr = makeGroupingsServiceResult(FAILURE, action);
        //            addGroupingResults.add(gsr);
        //        }
        //
        //        return addGroupingResults;\
        throw new UnsupportedOperationException();
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
        logger.info("assignOwnership; grouping: "
                + grouping
                + "; username: "
                + username
                + "; newOwner: "
                + newOwner
                + ";");

        String action = "give " + newOwner + " ownership of " + grouping;
        GroupingsServiceResult ownershipResult;

        if (isOwner(grouping, username) || isAdmin(username)) {
            WsSubjectLookup user = gf.makeWsSubjectLookup(username);
            WsAddMemberResults amr = gf.makeWsAddMemberResults(grouping + OWNERS, user, newOwner);
            ownershipResult = makeGroupingsServiceResult(amr, action);

            return ownershipResult;
        }

        ownershipResult = makeGroupingsServiceResult(FAILURE + ", " + username + " does not own " + grouping, action);
        return ownershipResult;
    }

    /**
     * @param grouping:   the path of the Grouping that will have its listserv status changed
     * @param username:   username of the Grouping Owner preforming the action
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
     * @param set:      true if the optIn permission should be turned on, false if it should be turned off
     * @return "SUCCESS" if the action succeeds or "FAILURE" if it does not.
     */
    @Override
    public List<GroupingsServiceResult> changeOptInStatus(String grouping, String username, boolean set) {
        List<GroupingsServiceResult> results = new ArrayList<>();
        if (isOwner(grouping, username) || isAdmin(username)) {
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_IN, grouping + INCLUDE, set));
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, grouping + EXCLUDE, set));
            results.add(changeGroupAttributeStatus(grouping, username, OPT_IN, set));
        } else {

            GroupingsServiceResult failure = makeGroupingsServiceResult(
                    FAILURE + ", " + username + " does not own " + grouping,
                    "change opt in status for " + grouping + " to " + set);
            results.add(failure);
        }
        return results;
    }

    /**
     * @param grouping: the path of the Grouping that will have its optOut permission changed
     * @param username: username of the Grouping Owner preforming the action
     * @param set:      true if the optOut permission should be turned on, false if it should be turned off
     * @return "SUCCESS" if the action succeeds or "FAILURE" if it does not.
     */
    @Override
    public List<GroupingsServiceResult> changeOptOutStatus(String grouping, String username, boolean set) {
        List<GroupingsServiceResult> results = new ArrayList<>();
        if (isOwner(grouping, username) || isAdmin(username)) {
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_IN, grouping + EXCLUDE, set));
            results.add(assignGrouperPrivilege(EVERY_ENTITY, PRIVILEGE_OPT_OUT, grouping + INCLUDE, set));
            results.add(changeGroupAttributeStatus(grouping, username, OPT_OUT, set));
        } else {

            GroupingsServiceResult failure = makeGroupingsServiceResult(
                    FAILURE + ", " + username + " does not own " + grouping,
                    "change opt out status for " + grouping + " to " + set);

            results.add(failure);
        }
        return results;
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
        logger.info("removeOwnership; grouping: "
                + grouping
                + "; username: "
                + username
                + "; ownerToRemove: "
                + ownerToRemove
                + ";");

        GroupingsServiceResult ownershipResults;
        String action = "remove ownership of " + grouping + " from " + ownerToRemove;

        if (isOwner(grouping, username) || isAdmin(username)) {
            WsSubjectLookup lookup = gf.makeWsSubjectLookup(username);
            WsDeleteMemberResults memberResults = gf.makeWsDeleteMemberResults(
                    grouping + OWNERS,
                    lookup,
                    ownerToRemove);
            ownershipResults = makeGroupingsServiceResult(memberResults, action);
            return ownershipResults;
        }

        ownershipResults = makeGroupingsServiceResult(
                FAILURE + ", " + username + " does not own " + grouping,
                action);
        return ownershipResults;
    }

    /**
     * @param grouping: the path of the Grouping to be searched for
     * @param username: the user who is doing the search
     * @return a Grouping Object containing information about the Grouping
     */
    @Override
    public Grouping getGrouping(String grouping, String username) {
        logger.info("getGrouping; grouping: " + grouping + "; username: " + username + ";");

        Grouping compositeGrouping = new Grouping();

        if (isOwner(grouping, username) || isAdmin(username)) {
            compositeGrouping = new Grouping(grouping);

            Group include = getMembers(username, grouping + INCLUDE);
            Group exclude = getMembers(username, grouping + EXCLUDE);
            Group basis = getMembers(username, grouping + BASIS);
            Group composite = getMembers(username, grouping);
            Group owners = getMembers(username, grouping + OWNERS);

            compositeGrouping = setGroupingAttributes(compositeGrouping);

            compositeGrouping.setBasis(basis);
            compositeGrouping.setExclude(exclude);
            compositeGrouping.setInclude(include);
            compositeGrouping.setComposite(composite);
            compositeGrouping.setOwners(owners);

        }
        return compositeGrouping;
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
        String outOrrIn = "in ";
        String preposition = "to ";
        String addGroup = grouping + INCLUDE;

        return opt(username, grouping, addGroup, outOrrIn, preposition);
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
        String outOrrIn = "out ";
        String preposition = "from ";
        String addGroup = grouping + EXCLUDE;

        return opt(username, grouping, addGroup, outOrrIn, preposition);
    }

    private List<GroupingsServiceResult> opt(String username, String grouping, String addGroup, String outOrrIn, String preposition) {

        List<GroupingsServiceResult> results = new ArrayList<>();

        if (groupOptInPermission(username, addGroup)) {

            switch (outOrrIn) {
                case "out ":
                    results.addAll(deleteMemberFromGrouping(username, grouping, username));

                    break;

                case "in ":
                    results.addAll(addMemberToGrouping(username, grouping, username));
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
    @Override
    public boolean groupHasAttribute(String grouping, String nameName) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = attributeAssignmentsResults(
                ASSIGN_TYPE_GROUP,
                grouping,
                nameName);

        if (wsGetAttributeAssignmentsResults.getWsAttributeAssigns() != null) {
            for (WsAttributeAssign attribute : wsGetAttributeAssignmentsResults.getWsAttributeAssigns()) {
                if (attribute.getAttributeDefNameName() != null && attribute.getAttributeDefNameName().equals(nameName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return a list of all of the Groupings that the user is in
     */
    @Override
    public List<Grouping> groupingsIn(List<String> groupPaths) {
        List<String> groupingsIn = extractGroupings(groupPaths);

        return makeGroupings(groupingsIn);
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
     * @return a list of all of the Groupings that the user owns
     */
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

    /**
     * @param username: username of the user who's groupings will be looked for
     * @return a list of all of the Groupings that the user is opted into
     */
    @Override
    public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths) {
        return groupingsOpted(INCLUDE, username, groupPaths);
    }

    /**
     * @param username: username of the user who's groupings will be looked for
     * @return a list of all of the Groupings that the user is opted out of
     */
    @Override
    public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths) {
        return groupingsOpted(EXCLUDE, username, groupPaths);
    }

    /**
     * @param includeOrrExclude: ":include" for the include group ":exclude" for the exclude group
     * @param username:          username of the user who's groupings will be looked for
     * @return a list of all of the groups that the user is opted into that end
     * with the suffix defined in includeOrrExclude
     */
    private List<Grouping> groupingsOpted(String includeOrrExclude, String username, List<String> groupPaths) {
        logger.info("groupingsOpted; includeOrrExclude: " + includeOrrExclude + "; username: " + username + ";");

        List<String> groupsOpted = new ArrayList<>();
        List<String> groupingsOpted = new ArrayList<>();

        groupsOpted.addAll(groupPaths.stream().filter(group -> group.endsWith(includeOrrExclude)
                && checkSelfOpted(group, username)).map(this::parentGroupingPath).collect(Collectors.toList()));

        if (groupsOpted.size() > 0) {

            List<WsGetAttributeAssignmentsResults> attributeAssignmentsResults = gf.makeWsGetAttributeAssignmentsResultsTrio(
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

    /**
     * @param username: username of user making request
     * @return a list of all of the groupings in the database
     */
    @Override
    public AdminListsHolder adminLists(String username) {
        AdminListsHolder info = new AdminListsHolder();
        List<Grouping> groupings;

        if (isSuperuser(username)) {
            List<String> groupPaths = new ArrayList<>();

            WsGetAttributeAssignmentsResults attributeAssignmentsResults = gf.makeWsGetAttributeAssignmentsResultsTrio(
                    ASSIGN_TYPE_GROUP,
                    TRIO);

            List<WsGroup> groups = new ArrayList<>(Arrays.asList(attributeAssignmentsResults.getWsGroups()));

            groupPaths.addAll(groups.stream().map(WsGroup::getName).collect(Collectors.toList()));

            Group admin = getMembers(username, GROUPING_ADMINS);
            groupings = makeGroupings(groupPaths);
            info.setAdminGroup(admin);
            info.setAllGroupings(groupings);
        }
        return info;
    }

    /**
     * @return a list of all groupings that the user is able to opt out of
     */
    private List<Grouping> groupingsToOptOutOf(String username, List<String> groupPaths) {
        logger.info("groupingsToOptOutOf; username: " + username + "; groupPaths: " + groupPaths + ";");

        List<String> trios = new ArrayList<>();
        List<String> opts = new ArrayList<>();
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();

        List<WsGetAttributeAssignmentsResults> assignmentsResults = gf.makeWsGetAttributeAssignmentsResultsTrio(
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

    /**
     * @return a list of all groupings that the user is able to opt into
     */
    private List<Grouping> groupingsToOptInto(String username, List<String> groupPaths) {
        logger.info("groupingsToOptInto; username: " + username + "; groupPaths : " + groupPaths + ";");

        List<String> trios = new ArrayList<>();
        List<String> opts = new ArrayList<>();
        List<String> excludes = groupPaths.stream().map(group -> group + EXCLUDE).collect(Collectors.toList());

        WsGetAttributeAssignmentsResults assignmentsResults = gf.makeWsGetAttributeAssignmentsResultsTrio(
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

    /**
     * adds the self-opted attribute to a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    @Override
    public GroupingsServiceResult addSelfOpted(String group, String username) {
        logger.info("addSelfOpted; group: " + group + "; username: " + username + ";");

        String action = "add self-opted attribute to the membership of " + username + " to " + group;

        if (inGroup(group, username)) {
            if (!checkSelfOpted(group, username)) {
                WsGetMembershipsResults includeMembershipsResults = membershipsResults(username, group);

                String membershipID = extractFirstMembershipID(includeMembershipsResults);

                return makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, membershipID),
                        action);
            }
            return makeGroupingsServiceResult(
                    SUCCESS + ", " + username + " was already self opted into " + group,
                    action);
        }
        return makeGroupingsServiceResult(
                FAILURE + ", " + username + " is not a member of " + group,
                action);
    }

    /**
     * @param group:    group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param username: username
     * @return true if the membership between the user and the group has the "self-opted" attribute
     */
    @Override
    public boolean checkSelfOpted(String group, String username) {
        logger.info("checkSelfOpted; group: " + group + "; username: " + username + ";");

        if (inGroup(group, username)) {
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(username, group);
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

    /**
     * @param group:    group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param username: subjectIdentifier of user to be searched for
     * @return true if username is a member of group
     */
    @Override
    public boolean inGroup(String group, String username) {
        logger.info("inGroup; group: " + group + "; username: " + username + ";");

        WsHasMemberResults memberResults = gf.makeWsHasMemberResults(group, username);

        WsHasMemberResult[] memberResultArray = memberResults.getResults();

        for (WsHasMemberResult hasMember : memberResultArray) {
            if (hasMember.getResultMetadata().getResultCode().equals(IS_MEMBER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param grouping: path to Grouping that will be checked
     * @param username: username of user who's permissions will be checked
     * @return true if user owns grouping false if not
     */
    @Override
    public boolean isOwner(String grouping, String username) {
        return inGroup(grouping + OWNERS, username);
    }

    @Override
    public boolean isAdmin(String username) {
        return inGroup(GROUPING_ADMINS, username);
    }

    @Override
    public boolean isApp(String username) {
        return inGroup(GROUPING_APPS, username);
    }

    @Override
    public boolean isSuperuser(String username) {
        return isAdmin(username) || isApp(username);
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
        logger.info("removeSelfOpted; group: " + group + "; username: " + username + ";");

        String action = "remove self-opted attribute from the membership of " + username + " to " + group;

        if (inGroup(group, username)) {
            if (checkSelfOpted(group, username)) {
                WsGetMembershipsResults membershipsResults = membershipsResults(username, group);
                String membershipID = extractFirstMembershipID(membershipsResults);

                return makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_REMOVE_ATTRIBUTE, SELF_OPTED, membershipID),
                        action);
            }
            return makeGroupingsServiceResult(
                    SUCCESS + ", " + username + " was not self-opted into " + group,
                    action);
        }
        return makeGroupingsServiceResult(
                FAILURE + ", " + username + " is not a member of " + group,
                action);
    }

    /**
     * @param wsGetMembershipsResults: has an array of memberships, but we are just interested
     *                                 in the first one. (there will probably only be one anyway)
     * @return the membership id of the first membership
     */
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

    /**
     * checks for permission to opt out of a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt out, false if not
     */
    @Override
    public boolean groupOptOutPermission(String username, String group) {
        logger.info("groupOptOutPermission; group: " + group + "; username: " + username + ";");
        WsGetGrouperPrivilegesLiteResult result = getGrouperPrivilege(username, PRIVILEGE_OPT_OUT, group);

        return result
                .getResultMetadata()
                .getResultCode()
                .equals(SUCCESS_ALLOWED);
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
        logger.info("groupOptInPermission; group: " + group + "; username: " + username + ";");

        WsGetGrouperPrivilegesLiteResult result = getGrouperPrivilege(username, PRIVILEGE_OPT_IN, group);

        return result
                .getResultMetadata()
                .getResultCode()
                .equals(SUCCESS_ALLOWED);
    }

    /**
     * updates the last modified time of a group
     * this should be done whenever a group is modified
     * ie. a member was added or deleted
     *
     * @param group: group who's last modified attribute will be updated
     * @return results from Grouper Web Service
     */
    @Override
    public GroupingsServiceResult updateLastModified(String group) {
        logger.info("updateLastModified; group: " + group + ";");
        String time = wsDateTime();
        WsAttributeAssignValue dateTimeValue = gf.makeWsAttributeAssignValue(time);

        WsAssignAttributesResults assignAttributesResults = gf.makeWsAssignAttributesResults(
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                group,
                YYYYMMDDTHHMM,
                OPERATION_REPLACE_VALUES,
                dateTimeValue);

        return makeGroupingsServiceResult(assignAttributesResults,
                "update last-modified attribute for " + group + " to time " + time);

    }

    /**
     * @param operation:    name of operation
     * @param uuid:         uuid of the attribute
     * @param membershipID: membership id for the membership between the user and Grouping
     * @return information about the success of the action
     */
    private WsAssignAttributesResults assignMembershipAttributes(String operation, String uuid, String membershipID) {
        logger.info("assignMembershipAttributes; operation: "
                + operation
                + "; uuid: "
                + uuid
                + "; membershipID: "
                + membershipID
                + ";");

        return gf.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, operation, uuid, membershipID);
    }

    /**
     * @param assignType:   assign type of the attribute
     * @param name:         uuid of the attribute
     * @param membershipID: membership id for the membership between the user and Grouping
     * @return information about the success of the action
     */
    private WsAttributeAssign[] getMembershipAttributes(String assignType, String name, String membershipID) {
        logger.info("getMembershipAttributes; assignType: "
                + assignType
                + "; name: "
                + name
                + "; membershipID: "
                + membershipID
                + ";");

        WsGetAttributeAssignmentsResults attributeAssignmentsResults = gf.makeWsGetAttributeAssignmentsResultsForMembership(
                assignType,
                name,
                membershipID);

        WsAttributeAssign[] wsAttributes = attributeAssignmentsResults.getWsAttributeAssigns();

        return wsAttributes != null ? wsAttributes : gf.makeEmptyWsAttributeAssignArray();
    }

    /**
     * @param attributeName:      name of attribute to be assigned
     * @param attributeOperation: operation to be done with the attribute to the group
     * @param group:              path to the group to have the attribute acted upon
     */
    private GroupingsServiceResult assignGroupAttributes(String attributeName, String attributeOperation, String group) {
        logger.info("assignGroupAttributes; "
                + "; attributeName: "
                + attributeName
                + "; attributeOperation: "
                + attributeOperation
                + "; group: "
                + group
                + ";");

        WsAssignAttributesResults attributesResults = gf.makeWsAssignAttributesResultsForGroup(
                ASSIGN_TYPE_GROUP,
                attributeOperation,
                attributeName,
                group);

        return makeGroupingsServiceResult(attributesResults, "assign " + attributeName + " attribute to " + group);
    }

    /**
     * @param assignType: assign type of the attribute
     * @param group:      path to the group to have attributes searched
     * @param nameName:   name of the attribute to be looked up
     * @return information about the attributes that the group has
     */
    WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String group, String nameName) {
        logger.info("attributeAssignmentsResults; assignType: "
                + assignType
                + "; group: "
                + group
                + "; nameName: "
                + nameName
                + ";");

        return gf.makeWsGetAttributeAssignmentsResultsForGroup(assignType, nameName, group);
    }

    /**
     * @param username:      username of user who's privileges will be checked
     * @param privilegeName: name of the privilege to be checked
     * @param group:         name of group the privilege is for
     * @return return information about user's privileges in the group
     */
    private WsGetGrouperPrivilegesLiteResult getGrouperPrivilege(String username, String privilegeName, String group) {
        logger.info("getGrouperPrivilege; username: "
                + username
                + "; group: "
                + group
                + "; privilegeName: "
                + privilegeName
                + ";");

        WsSubjectLookup lookup = gf.makeWsSubjectLookup(username);

        return gf.makeWsGetGrouperPrivilegesLiteResult(group, privilegeName, lookup);
    }

    private GroupingsServiceResult assignGrouperPrivilege(
            String username,
            String privilegeName,
            String group,
            boolean set) {

        logger.info("assignGrouperPrivilege; username: "
                + username
                + "; group: "
                + group
                + "; privilegeName: "
                + privilegeName
                + " set: "
                + set
                + ";");

        WsSubjectLookup lookup = gf.makeWsSubjectLookup(username);
        String action = "set " + privilegeName + " " + set + " for " + username + " in " + group;

        WsAssignGrouperPrivilegesLiteResult grouperPrivilegesLiteResult = gf.makeWsAssignGrouperPrivilegesLiteResult(
                group,
                privilegeName,
                lookup,
                set);

        return makeGroupingsServiceResult(grouperPrivilegesLiteResult, action);
    }

    /**
     * @param username: WsSubjectLookup of user who's membership will be checked
     * @param group:    group that membership status will be checked for
     * @return membership results for user
     */
    private WsGetMembershipsResults membershipsResults(String username, String group) {
        logger.info("membershipResults; username: " + username + "; group: " + group + ";");

        WsSubjectLookup lookup = gf.makeWsSubjectLookup(username);

        return gf.makeWsGetMembershipsResults(group, lookup);
    }

    /**
     * @param username: username of owner adding member
     * @param newAdmin: username of user to be added to grup
     * @return information about success of action
     */
    @Override
    public GroupingsServiceResult addAdmin(String username, String newAdmin) {
        logger.info("addAdmin; username: " + username + "; newAdmin: " + newAdmin + ";");

        String action = "add " + newAdmin + " to " + GROUPING_ADMINS;

        if (isSuperuser(username)) {
            if (isAdmin(newAdmin)) {
                return makeGroupingsServiceResult("SUCCESS: " + newAdmin + " was already in" + GROUPING_ADMINS, action);
            }
            WsAddMemberResults addMemberResults = gf.makeWsAddMemberResults(
                    GROUPING_ADMINS,
                    newAdmin);

            return makeGroupingsServiceResult(addMemberResults, action);
        }

        return makeGroupingsServiceResult("FAILURE: " + username + " is not an admin", action);
    }

    /**
     * @param username:      username of owner adding member
     * @param adminToDelete: username of user to be added to grup
     * @return information about success of action
     */
    @Override
    public GroupingsServiceResult deleteAdmin(String username, String adminToDelete) {
        logger.info("deleteAdmin; username: " + username + "; adminToDelete: " + adminToDelete + ";");

        String action = "delete " + adminToDelete + " from " + GROUPING_ADMINS;

        if (isSuperuser(username)) {
            WsSubjectLookup user = gf.makeWsSubjectLookup(username);

            WsDeleteMemberResults deleteMemberResults = gf.makeWsDeleteMemberResults(
                    GROUPING_ADMINS,
                    user,
                    adminToDelete);

            return makeGroupingsServiceResult(deleteMemberResults, action);
        }
        return makeGroupingsServiceResult("FAILURE: " + username + " is not an admin", action);
    }

    @Override
    public List<GroupingsServiceResult> addMemberAs(String username, String group, String userToAdd) {
        logger.info("addMemberToGroup; user: " + username + "; group: " + group + "; userToAdd: " + userToAdd + ";");

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        String action = "add users to " + group;

        if (isOwner(parentGroupingPath(group), username) || isSuperuser(username) || userToAdd.equals(username)) {
            WsSubjectLookup user = gf.makeWsSubjectLookup(username);
            String composite = parentGroupingPath(group);
            String exclude = composite + EXCLUDE;
            String include = composite + INCLUDE;
            String owners = composite + OWNERS;

            boolean updateComposite = false;
            boolean updateExclude = false;
            boolean updateInclude = false;
            boolean updateOwners = false;

            //check to see if it is the include, exclude or owners
            if (group.endsWith(INCLUDE)) {
                //if userToAdd is in exclude, get them out
                if (inGroup(exclude, userToAdd)) {
                    WsDeleteMemberResults wsDeleteMemberResults = gf.makeWsDeleteMemberResults(
                            exclude,
                            user,
                            userToAdd);

                    updateExclude = true;

                    gsrList.add(makeGroupingsServiceResult(wsDeleteMemberResults, "delete " + userToAdd + " from " + exclude));
                }
                //check to see if userToAdd is already in include
                if (!inGroup(include, userToAdd)) {
                    //add to include
                    WsAddMemberResults addMemberResults = gf.makeWsAddMemberResults(include, user, userToAdd);

                    updateInclude = true;

                    gsrList.add(makeGroupingsServiceResult(addMemberResults, action));
                } else {
                    //They are already in the group, so just return SUCCESS
                    gsrList.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAdd + " was already in " + group, action));
                }
            }

            //if exclude check if userToAdd is in the include
            else if (group.endsWith(EXCLUDE)) {
                //if userToAdd is in include, get them out
                if (inGroup(include, userToAdd)) {
                    WsDeleteMemberResults wsDeleteMemberResults = gf.makeWsDeleteMemberResults(
                            include,
                            user,
                            userToAdd);

                    updateInclude = true;

                    gsrList.add(makeGroupingsServiceResult(wsDeleteMemberResults, "delete " + userToAdd + " from " + include));
                }
                //check to see if userToAdd is already in exclude
                if (!inGroup(exclude, userToAdd)) {
                    //add to exclude
                    WsAddMemberResults addMemberResults = gf.makeWsAddMemberResults(exclude, user, userToAdd);

                    updateExclude = true;

                    gsrList.add(makeGroupingsServiceResult(addMemberResults, action));
                }
                //They are already in the group, so just return SUCCESS
                gsrList.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAdd + " was already in " + group, action));

            }
            //if owners check to see if the user is already in owners
            else if (group.endsWith(OWNERS)) {
                //check to see if userToAdd is already in owners
                if (!inGroup(owners, userToAdd)) {
                    //add userToAdd to owners
                    WsAddMemberResults addMemberResults = gf.makeWsAddMemberResults(owners, user, userToAdd);

                    updateOwners = true;

                    gsrList.add(makeGroupingsServiceResult(addMemberResults, action));
                }
                //They are already in the group, so just return SUCCESS
                gsrList.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAdd + " was already in " + group, action));
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
            gsrList.add(makeGroupingsServiceResult(FAILURE + ": " + username + "does not have permission to edit " + group, action));
        }

        return gsrList;
    }

    /**
     * @param username:   username of owner adding member
     * @param group:      path to group the user to be added will be added to
     * @param usersToAdd: list of usernames to be added to group
     * @return information about success of action
     */
    @Override
    public List<GroupingsServiceResult> addMembersAs(String username, String group, List<String> usersToAdd) {
        logger.info("addMembersAs; user: " + username + "; group: " + group + "; usersToAdd: " + usersToAdd + ";");
        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        for (String userToAdd : usersToAdd) {
            gsrList.addAll(addMemberAs(username, group, userToAdd));
        }
        return gsrList;
    }

    @Override
    public List<GroupingsServiceResult> addMemberToGrouping(String username, String groupingPath, String userToAdd) {
        logger.info("addMemberToGrouping; user: " + username + "; group: " + groupingPath + "; usersToAdd: " + userToAdd + ";");

        List<GroupingsServiceResult> gsrs = new ArrayList<>();

        String action = "add user to " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        boolean inBasis = inGroup(basis, userToAdd);
        boolean inComposite = inGroup(groupingPath, userToAdd);
        boolean inInclude = inGroup(include, userToAdd);

        //check to see if they are already in the grouping
        if (!inComposite) {
            //get them out of the exclude
            gsrs.add(deleteMemberAs(username, exclude, userToAdd));
            //only add them to the include if they are not in the basis
            if (!inBasis) {
                gsrs.addAll(addMemberAs(username, include, userToAdd));
            } else {
                gsrs.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAdd + " was in " + basis, action));
            }
        } else {
            gsrs.add(makeGroupingsServiceResult(SUCCESS + ": " + userToAdd + " was already in " + groupingPath, action));
        }
        //should only be in one or the other
        if (inBasis && inInclude) {
            gsrs.add(deleteMemberAs(username, include, userToAdd));
        }

        return gsrs;
    }

    @Override
    public List<GroupingsServiceResult> deleteMemberFromGrouping(String username, String groupingPath, String userToDelete) {
        logger.info("deleteMemberFromGrouping; username: "
                + username
                + "; groupingPath: "
                + groupingPath + "; userToDelete: "
                + userToDelete
                + ";");

        List<GroupingsServiceResult> gsrList = new ArrayList<>();

        String action = username + " deletes " + userToDelete + " from " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        boolean inBasis = inGroup(basis, userToDelete);
        boolean inComposite = inGroup(groupingPath, userToDelete);
        boolean inExclude = inGroup(exclude, userToDelete);

        //if they are in the include group, get them out
        gsrList.add(deleteMemberAs(username, include, userToDelete));

        //make sure userToDelete is actually in the Grouping
        if (inComposite) {
            //if they are not in the include group, then they are in the basis, so add them to the exclude group
            if (inBasis) {
                gsrList.addAll(addMemberAs(username, exclude, userToDelete));
            }
        }
        //since they are not in the Grouping, do nothing, but return SUCCESS
        else {
            gsrList.add(makeGroupingsServiceResult(SUCCESS + userToDelete + " was not in " + groupingPath, action));
        }

        //should not be in exclude if not in basis
        if (!inBasis && inExclude) {
            gsrList.add(deleteMemberAs(username, exclude, userToDelete));
        }

        return gsrList;
    }

    /**
     * @param username:     username of owner preforming action
     * @param group:        path to group that the member will be removed from
     * @param userToDelete: username of user to be removed from group
     * @return information about success of action
     */
    @Override
    public GroupingsServiceResult deleteMemberAs(String username, String group, String userToDelete) {
        logger.info("deleteMemberAs; user: "
                + username
                + "; group: "
                + group + "; userToDelete: "
                + userToDelete
                + ";");

        String action = "delete " + userToDelete + " from " + group;

        String composite = parentGroupingPath(group);

        if (isOwner(composite, username) || isSuperuser(username) || userToDelete.equals(username)) {
            WsSubjectLookup user = gf.makeWsSubjectLookup(username);
            if (group.endsWith(EXCLUDE) || group.endsWith(INCLUDE) || group.endsWith(OWNERS)) {
                if (inGroup(group, userToDelete)) {
                    WsDeleteMemberResults deleteMemberResults = gf.makeWsDeleteMemberResults(group, user, userToDelete);

                    updateLastModified(composite);
                    updateLastModified(group);
                    return makeGroupingsServiceResult(deleteMemberResults, action);
                }
                return makeGroupingsServiceResult(SUCCESS + ": " + username + " was not in " + group, action);
            }
            return makeGroupingsServiceResult(FAILURE + ": " + username + " may only delete from exclude, include or owner group", action);
        }
        return makeGroupingsServiceResult(FAILURE + ": " + username + " does not have permission to edit " + group, action);
    }

    /**
     * @param username: lookup for owner
     * @param group:    path to group to be searched
     * @return results for members of the group
     */
    Group getMembers(String username, String group) {
        logger.info("getMembers; user: " + username + "; group: " + group + ";");

        WsSubjectLookup lookup = gf.makeWsSubjectLookup(username);
        WsGetMembersResults members = gf.makeWsGetMembersResults(
                SUBJECT_ATTRIBUTE_NAME_UID,
                lookup,
                group);

        //todo should we use EmptyGroup?
        Group groupMembers = new Group();
        if (members.getResults() != null) {
            groupMembers = makeGroup(members);
        }
        return groupMembers;
    }

    /**
     * @param groupPaths: list of group paths
     * @return a list of Grouping paths that were is the list group paths
     */
    private List<String> extractGroupings(List<String> groupPaths) {
        logger.info("extractGroupings; groupPaths: " + groupPaths + ";");

        List<String> groupings = new ArrayList<>();
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();

        if (groupPaths.size() > 0) {

            List<WsGetAttributeAssignmentsResults> attributeAssignmentsResults = gf.makeWsGetAttributeAssignmentsResultsTrio(
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

    /**
     * @param username: username of user who's groups will be searched for
     * @return a list of all groups that the user is a member of
     */
    List<String> getGroupPaths(String username) {
        logger.info("getGroupPaths; username: " + username + ";");
        WsStemLookup stemLookup = gf.makeWsStemLookup(STEM);

        WsGetGroupsResults wsGetGroupsResults = gf.makeWsGetGroupsResults(
                username,
                stemLookup,
                StemScope.ALL_IN_SUBTREE);

        WsGetGroupsResult groupResults = wsGetGroupsResults.getResults()[0];
        List<WsGroup> groups = Arrays.asList(groupResults.getWsGroups());

        return extractGroupPaths(groups);
    }

    private Grouping setGroupingAttributes(Grouping grouping) {
        logger.info("setGroupingAttributes; grouping: " + grouping + ";");
        boolean listservOn = false;
        boolean optInOn = false;
        boolean optOutOn = false;

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = gf.makeWsGetAttributeAssignmentsResultsForGroup(
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

    /**
     * @param group: path of group to be checked
     * @return the parent Grouping of the group
     */
    @Override
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

    /**
     * @param groups: list of WsGroups
     * @return a list of the names of the groups in the WsGroups
     */
    List<String> extractGroupPaths(List<WsGroup> groups) {
        List<String> names = new ArrayList<>();
        if (groups != null) {
            groups.stream()
                    .filter(group -> !names.contains(group.getName()))
                    .forEach(group -> names.add(group.getName()));
        }
        return names;
    }

    /**
     * @param group:         path to group who's attributes will be changed
     * @param username:      username of user preforming action
     * @param attributeName; name of attribute to be changed
     * @param attributeOn:   on if the attribute should exist false otherwise
     * @return information about success of the action
     */
    private GroupingsServiceResult changeGroupAttributeStatus(String group, String username, String attributeName, boolean attributeOn) {
        GroupingsServiceResult gsr;

        String verb = "removed from ";
        if (attributeOn) {
            verb = "added to ";
        }
        String action = attributeName + " has been " + verb + group + " by " + username;

        if (isOwner(group, username) || isAdmin(username)) {
            boolean hasAttribute = groupHasAttribute(group, attributeName);
            if (attributeOn) {
                if (!hasAttribute) {
                    assignGroupAttributes(attributeName, OPERATION_ASSIGN_ATTRIBUTE, group);

                    gsr = makeGroupingsServiceResult(SUCCESS, action);
                } else {
                    gsr = makeGroupingsServiceResult(SUCCESS + ", " + attributeName + " already existed", action);
                }
            } else {
                if (hasAttribute) {
                    assignGroupAttributes(attributeName, OPERATION_REMOVE_ATTRIBUTE, group);

                    gsr = makeGroupingsServiceResult(SUCCESS, action);
                } else {
                    gsr = makeGroupingsServiceResult(SUCCESS + ", " + attributeName + " did not exist", action);
                }
            }
        } else {
            gsr = makeGroupingsServiceResult(FAILURE + ", " + username + "does not have permission to set " + attributeName
                    + " because " + username + " does not own " + group, action);
        }

        return gsr;
    }

    /////////////////////////////////////////////////////
    ////Factory Methods
    /////////////////////////////////////////////////////

    /**
     * @param resultMetadataHolder: ResultMetadataHolder that will be turned into GroupingsServiceResult
     * @param action:               the action being preformed in the resultMetadataHolder
     * @return a GroupingsServiceResult made from the ResultMetadataHolder and the action
     */
    GroupingsServiceResult makeGroupingsServiceResult(ResultMetadataHolder resultMetadataHolder, String action) {
        GroupingsServiceResult groupingsServiceResult = new GroupingsServiceResult();
        groupingsServiceResult.setAction(action);
        groupingsServiceResult.setResultCode(resultMetadataHolder.getResultMetadata().getResultCode());

        if (groupingsServiceResult.getResultCode().startsWith(FAILURE)) {
            throw new GroupingsServiceResultException(groupingsServiceResult);
        }

        return groupingsServiceResult;
    }

    GroupingsServiceResult makeGroupingsServiceResult(String resultCode, String action) {
        GroupingsServiceResult groupingsServiceResult = new GroupingsServiceResult();
        groupingsServiceResult.setAction(action);
        groupingsServiceResult.setResultCode(resultCode);

        if (groupingsServiceResult.getResultCode().startsWith(FAILURE)) {
            throw new GroupingsServiceResultException(groupingsServiceResult);
        }

        return groupingsServiceResult;
    }

    /**
     * @param groupingPaths: list of paths to groups that are Groupings
     * @return a list of Grouping Objects made from the list of Grouping paths
     */
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

    /**
     * @param person:
     * @return a person made from the WsSubject
     */
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

    /**
     * @param subject:
     * @return a subject made from the WsSubject
     * @Param attributeNames:
     */
    Person makePerson(WsSubject subject, String[] attributeNames) {
        if (subject == null || subject.getAttributeValues() == null) {
            return new Person();
        } else {

            Attributes attributes = new Attributes();
            for (int i = 0; i < subject.getAttributeValues().length; i++) {
                attributes.put(new Attributes.Name(attributeNames[i]), subject.getAttributeValue(i));
            }

            //todo add strings to config file or change person to have an attribute map
            String uuid = subject.getId();
//            String name = attributes.getValue("cn");
            String name = subject.getName();
            String username = attributes.getValue("uid");
            String firstName = attributes.getValue("givenName");
            String lastName = attributes.getValue("sn");
            return new Person(name, uuid, username, firstName, lastName);
        }
    }

    @Override
    public String toString() {
        return "GroupingsServiceImpl [SETTINGS=" + SETTINGS + "]";
    }
}
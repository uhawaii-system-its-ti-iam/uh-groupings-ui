package edu.hawaii.its.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.util.Dates;

import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignAttributesResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssignValue;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("membershipService")
public class MembershipServiceImpl implements MembershipService {

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

    @Value("${groupings.api.ldap}")
    private String LDAP;

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
    private HelperService hs;

    @Autowired
    private MemberAttributeService mas;

    public static final Log logger = LogFactory.getLog(MembershipServiceImpl.class);

    //finds a user by a username and adds them to a grouping
    @Override
    public List<GroupingsServiceResult> addGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToAddUsername) {
        logger.info(
                "addGroupingMemberByUsername; user: " + ownerUsername + "; group: " + groupingPath + "; usersToAdd: "
                        + userToAddUsername + ";");

        List<GroupingsServiceResult> gsrs = new ArrayList<>();

        String action = "add user to " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        boolean inBasis = mas.isMember(basis, userToAddUsername);
        boolean inComposite = mas.isMember(groupingPath, userToAddUsername);
        boolean inInclude = mas.isMember(include, userToAddUsername);

        //check to see if they are already in the grouping
        if (!inComposite) {
            //get them out of the exclude
            gsrs.add(deleteGroupMemberByUsername(ownerUsername, exclude, userToAddUsername));
            //only add them to the include if they are not in the basis
            if (!inBasis) {
                gsrs.addAll(addGroupMemberByUsername(ownerUsername, include, userToAddUsername));
            } else {
                gsrs.add(
                        hs.makeGroupingsServiceResult(SUCCESS + ": " + userToAddUsername + " was in " + basis, action));
            }
        } else {
            gsrs.add(hs.makeGroupingsServiceResult(
                    SUCCESS + ": " + userToAddUsername + " was already in " + groupingPath, action));
        }
        //should only be in one or the other
        if (inBasis && inInclude) {
            gsrs.add(deleteGroupMemberByUsername(ownerUsername, include, userToAddUsername));
        }

        return gsrs;
    }

    //find a user by a uuid and add them to a grouping
    @Override
    public List<GroupingsServiceResult> addGroupingMemberByUuid(String username, String groupingPath,
            String userToAddUuid) {
        logger.info("addGroupingMemberByUuid; user: " + username + "; grouping: " + groupingPath + "; userToAdd: "
                + userToAddUuid + ";");

        List<GroupingsServiceResult> gsrs = new ArrayList<>();

        String action = "add user to " + groupingPath;
        String basis = groupingPath + BASIS;
        String exclude = groupingPath + EXCLUDE;
        String include = groupingPath + INCLUDE;

        Person personToAdd = new Person(null, userToAddUuid, null);

        boolean inBasis = mas.isMember(basis, personToAdd);
        boolean inComposite = mas.isMember(groupingPath, personToAdd);
        boolean inInclude = mas.isMember(include, personToAdd);

        //check to see if they are already in the grouping
        if (!inComposite) {
            //get them out of the exclude
            gsrs.add(deleteGroupMemberByUuid(username, exclude, userToAddUuid));
            //only add them to the include if they are not in the basis
            if (!inBasis) {
                gsrs.addAll(addGroupMemberByUuid(username, include, userToAddUuid));
            } else {
                gsrs.add(hs.makeGroupingsServiceResult(SUCCESS + ": " + userToAddUuid + " was in " + basis, action));
            }
        } else {
            gsrs.add(hs.makeGroupingsServiceResult(SUCCESS + ": " + userToAddUuid + " was already in " + groupingPath,
                    action));
        }
        //should only be in one or the other
        if (inBasis && inInclude) {
            gsrs.add(deleteGroupMemberByUuid(username, include, userToAddUuid));
        }

        return gsrs;
    }

    //find a user by a username and remove them from the grouping
    @Override
    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToDeleteUsername) {
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

        boolean inBasis = mas.isMember(basis, userToDeleteUsername);
        boolean inComposite = mas.isMember(groupingPath, userToDeleteUsername);
        boolean inExclude = mas.isMember(exclude, userToDeleteUsername);

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
            gsrList.add(hs.makeGroupingsServiceResult(SUCCESS + userToDeleteUsername + " was not in " + groupingPath,
                    action));
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
    public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToDeleteUuid) {
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

        boolean inBasis = mas.isMember(basis, personToDelete);
        boolean inComposite = mas.isMember(groupingPath, personToDelete);
        boolean inExclude = mas.isMember(exclude, personToDelete);

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
            gsrList.add(hs.makeGroupingsServiceResult(SUCCESS + userToDeleteUuid + " was not in " + groupingPath,
                    action));
        }

        //should not be in exclude if not in basis
        if (!inBasis && inExclude) {
            gsrList.add(deleteGroupMemberByUuid(ownerUsername, exclude, userToDeleteUuid));
        }

        return gsrList;
    }

    //finds a user by a username and adds that user to the group
    @Override
    public List<GroupingsServiceResult> addGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToAddUsername) {
        logger.info("addGroupMemberByUsername; user: " + ownerUsername + "; groupPath: " + groupPath + "; userToAdd: "
                + userToAddUsername + ";");

        Person personToAdd = new Person(null, null, userToAddUsername);
        return addMemberHelper(ownerUsername, groupPath, personToAdd);
    }

    //finds all the user from a list of usernames and adds them to the group
    @Override
    public List<GroupingsServiceResult> addGroupMembersByUsername(String ownerUsername, String groupPath,
            List<String> usernamesToAdd) {
        logger.info(
                "addGroupMembersByUsername; user: " + ownerUsername + "; group: " + groupPath + "; usersToAddUsername: "
                        + usernamesToAdd + ";");
        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        for (String userToAdd : usernamesToAdd) {
            gsrList.addAll(addGroupMemberByUsername(ownerUsername, groupPath, userToAdd));
        }
        return gsrList;
    }

    //finds a user by a uuid and adds them to the group
    @Override
    public List<GroupingsServiceResult> addGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToAddUuid) {
        logger.info("addGroupMemberByUuid; user: " + ownerUsername + "; groupPath: " + groupPath + "; userToAdd: "
                + userToAddUuid + ";");

        Person personToAdd = new Person(null, userToAddUuid, null);
        return addMemberHelper(ownerUsername, groupPath, personToAdd);
    }

    //finds all the user from a list of uuids and adds them to the group
    @Override
    public List<GroupingsServiceResult> addGroupMembersByUuid(String ownerUsername, String groupPath,
            List<String> usersToAddUuid) {
        logger.info("addGroupMembersByUuid; user: " + ownerUsername + "; groupPath: " + groupPath + "; usersToAddUuid: "
                + usersToAddUuid + ";");
        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        for (String userToAdd : usersToAddUuid) {
            gsrList.addAll(addGroupMemberByUuid(ownerUsername, groupPath, userToAdd));
        }
        return gsrList;
    }

    //find a user by a username and remove them from a group
    @Override
    public GroupingsServiceResult deleteGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToDeleteUsername) {
        logger.info("deleteGroupMemberByUsername; user: " + ownerUsername
                + "; group: " + groupPath
                + "; userToDelete: " + userToDeleteUsername
                + ";");

        String action = "delete " + userToDeleteUsername + " from " + groupPath;

        String composite = hs.parentGroupingPath(groupPath);

        if (mas.isOwner(composite, ownerUsername) || mas.isSuperuser(ownerUsername) || userToDeleteUsername
                .equals(ownerUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(ownerUsername);
            if (groupPath.endsWith(EXCLUDE) || groupPath.endsWith(INCLUDE) || groupPath.endsWith(OWNERS)) {
                if (mas.isMember(groupPath, userToDeleteUsername)) {
                    WsDeleteMemberResults deleteMemberResults =
                            grouperFS.makeWsDeleteMemberResults(groupPath, user, userToDeleteUsername);

                    updateLastModified(composite);
                    updateLastModified(groupPath);
                    return hs.makeGroupingsServiceResult(deleteMemberResults, action);
                }
                return hs.makeGroupingsServiceResult(SUCCESS + ": " + ownerUsername + " was not in " + groupPath,
                        action);
            }
            return hs.makeGroupingsServiceResult(
                    FAILURE + ": " + ownerUsername + " may only delete from exclude, include or owner group", action);
        }
        return hs.makeGroupingsServiceResult(
                FAILURE + ": " + ownerUsername + " does not have permission to edit " + groupPath, action);
    }

    @Override
    public GroupingsServiceResult deleteGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToDeleteUuid) {
        logger.info("deleteGroupMemberByUuid; user: " + ownerUsername
                + "; group: " + groupPath
                + "; userToDelete: " + userToDeleteUuid
                + ";");

        String action = "delete " + userToDeleteUuid + " from " + groupPath;
        Person personToDelete = new Person(null, userToDeleteUuid, null);

        String composite = hs.parentGroupingPath(groupPath);

        if (mas.isOwner(composite, ownerUsername) || mas.isSuperuser(ownerUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(ownerUsername);
            if (groupPath.endsWith(EXCLUDE) || groupPath.endsWith(INCLUDE) || groupPath.endsWith(OWNERS)) {
                if (mas.isMember(groupPath, personToDelete)) {
                    WsDeleteMemberResults deleteMemberResults =
                            grouperFS.makeWsDeleteMemberResults(groupPath, user, personToDelete);

                    updateLastModified(composite);
                    updateLastModified(groupPath);
                    return hs.makeGroupingsServiceResult(deleteMemberResults, action);
                }
                return hs.makeGroupingsServiceResult(SUCCESS + ": " + ownerUsername + " was not in " + groupPath,
                        action);
            }
            return hs.makeGroupingsServiceResult(
                    FAILURE + ": " + ownerUsername + " may only delete from exclude, include or owner group", action);
        }
        return hs.makeGroupingsServiceResult(
                FAILURE + ": " + ownerUsername + " does not have permission to edit " + groupPath, action);
    }

    //adds a user to the admins group
    @Override
    public GroupingsServiceResult addAdmin(String currentAdminUsername, String newAdminUsername) {
        logger.info("addAdmin; username: " + currentAdminUsername + "; newAdmin: " + newAdminUsername + ";");

        String action = "add " + newAdminUsername + " to " + GROUPING_ADMINS;

        if (mas.isSuperuser(currentAdminUsername)) {
            if (mas.isAdmin(newAdminUsername)) {
                //todo replace hard coded string with value from top
                return hs.makeGroupingsServiceResult(
                        "SUCCESS: " + newAdminUsername + " was already in" + GROUPING_ADMINS, action);
            }
            WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(
                    GROUPING_ADMINS,
                    newAdminUsername);

            return hs.makeGroupingsServiceResult(addMemberResults, action);
        }

        //todo replace hard coded string with value from top
        return hs.makeGroupingsServiceResult("FAILURE: " + currentAdminUsername + " is not an admin", action);
    }

    //removes a user from the admins group
    @Override
    public GroupingsServiceResult deleteAdmin(String adminUsername, String adminToDeleteUsername) {
        logger.info("deleteAdmin; username: " + adminUsername + "; adminToDelete: " + adminToDeleteUsername + ";");

        String action = "delete " + adminToDeleteUsername + " from " + GROUPING_ADMINS;

        if (mas.isSuperuser(adminUsername)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(adminUsername);

            WsDeleteMemberResults deleteMemberResults = grouperFS.makeWsDeleteMemberResults(
                    GROUPING_ADMINS,
                    user,
                    adminToDeleteUsername);

            return hs.makeGroupingsServiceResult(deleteMemberResults, action);
        }
        //todo replace hard coded string with value from top
        return hs.makeGroupingsServiceResult("FAILURE: " + adminUsername + " is not an admin", action);
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

    public List<GroupingsServiceResult> opt(String username, String grouping, String addGroup, String outOrrIn,
            String preposition) {

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

            if (mas.isMember(addGroup, username)) {
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
            results.add(hs.makeGroupingsServiceResult(failureResult, action));
        }
        return results;
    }

    //adds the self-opted attribute to the membership between the group and user
    @Override
    public GroupingsServiceResult addSelfOpted(String groupPath, String username) {
        logger.info("addSelfOpted; group: " + groupPath + "; username: " + username + ";");

        String action = "add self-opted attribute to the membership of " + username + " to " + groupPath;

        if (mas.isMember(groupPath, username)) {
            if (!mas.isSelfOpted(groupPath, username)) {
                WsGetMembershipsResults includeMembershipsResults = hs.membershipsResults(username, groupPath);

                String membershipID = hs.extractFirstMembershipID(includeMembershipsResults);

                return hs.makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_ASSIGN_ATTRIBUTE, SELF_OPTED, membershipID),
                        action);
            }
            return hs.makeGroupingsServiceResult(
                    SUCCESS + ", " + username + " was already self opted into " + groupPath,
                    action);
        }
        return hs.makeGroupingsServiceResult(
                FAILURE + ", " + username + " is not a member of " + groupPath,
                action);
    }

    //removes the self-opted attribute from the membership that corresponds to the user and group
    @Override
    public GroupingsServiceResult removeSelfOpted(String groupPath, String username) {
        logger.info("removeSelfOpted; group: " + groupPath + "; username: " + username + ";");

        String action = "remove self-opted attribute from the membership of " + username + " to " + groupPath;

        if (mas.isMember(groupPath, username)) {
            if (mas.isSelfOpted(groupPath, username)) {
                WsGetMembershipsResults membershipsResults = hs.membershipsResults(username, groupPath);
                String membershipID = hs.extractFirstMembershipID(membershipsResults);

                return hs.makeGroupingsServiceResult(
                        assignMembershipAttributes(OPERATION_REMOVE_ATTRIBUTE, SELF_OPTED, membershipID),
                        action);
            }
            return hs.makeGroupingsServiceResult(
                    SUCCESS + ", " + username + " was not self-opted into " + groupPath,
                    action);
        }
        return hs.makeGroupingsServiceResult(
                FAILURE + ", " + username + " is not a member of " + groupPath,
                action);
    }

    //logic for adding a member
    public List<GroupingsServiceResult> addMemberHelper(String username, String groupPath, Person personToAdd) {
        logger.info(
                "addMemberHelper; user: " + username + "; group: " + groupPath + "; personToAdd: " + personToAdd + ";");

        List<GroupingsServiceResult> gsrList = new ArrayList<>();
        String action = "add users to " + groupPath;

        if (mas.isOwner(hs.parentGroupingPath(groupPath), username) || mas.isSuperuser(username) || personToAdd
                .getUsername().equals(username)) {
            WsSubjectLookup user = grouperFS.makeWsSubjectLookup(username);
            String composite = hs.parentGroupingPath(groupPath);
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
                if (mas.isMember(exclude, personToAdd)) {
                    WsDeleteMemberResults wsDeleteMemberResults = grouperFS.makeWsDeleteMemberResults(
                            exclude,
                            user,
                            personToAdd);

                    updateExclude = true;

                    gsrList.add(hs.makeGroupingsServiceResult(wsDeleteMemberResults,
                            "delete " + personToAdd.toString() + " from " + exclude));
                }
                //check to see if personToAdd is already in include
                if (!mas.isMember(include, personToAdd)) {
                    //add to include
                    WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(include, user, personToAdd);

                    updateInclude = true;

                    gsrList.add(hs.makeGroupingsServiceResult(addMemberResults, action));
                } else {
                    //They are already in the group, so just return SUCCESS
                    gsrList.add(hs.makeGroupingsServiceResult(
                            SUCCESS + ": " + personToAdd.toString() + " was already in " + groupPath, action));
                }
            }

            //if exclude check if personToAdd is in the include
            else if (groupPath.endsWith(EXCLUDE)) {
                //if personToAdd is in include, get them out
                if (mas.isMember(include, personToAdd)) {
                    WsDeleteMemberResults wsDeleteMemberResults = grouperFS.makeWsDeleteMemberResults(
                            include,
                            user,
                            personToAdd);

                    updateInclude = true;

                    gsrList.add(hs.makeGroupingsServiceResult(wsDeleteMemberResults,
                            "delete " + personToAdd.toString() + " from " + include));
                }
                //check to see if userToAdd is already in exclude
                if (!mas.isMember(exclude, personToAdd)) {
                    //add to exclude
                    WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(exclude, user, personToAdd);

                    updateExclude = true;

                    gsrList.add(hs.makeGroupingsServiceResult(addMemberResults, action));
                }
                //They are already in the group, so just return SUCCESS
                gsrList.add(hs.makeGroupingsServiceResult(
                        SUCCESS + ": " + personToAdd.toString() + " was already in " + groupPath, action));

            }
            //if owners check to see if the user is already in owners
            else if (groupPath.endsWith(OWNERS)) {
                //check to see if userToAdd is already in owners
                if (!mas.isMember(owners, personToAdd)) {
                    //add userToAdd to owners
                    WsAddMemberResults addMemberResults = grouperFS.makeWsAddMemberResults(owners, user, personToAdd);

                    updateOwners = true;

                    gsrList.add(hs.makeGroupingsServiceResult(addMemberResults, action));
                }
                //They are already in the group, so just return SUCCESS
                gsrList.add(hs.makeGroupingsServiceResult(
                        SUCCESS + ": " + personToAdd.toString() + " was already in " + groupPath, action));
            }
            //Owners can only change include, exclude and owners groups
            else {
                gsrList.add(hs.makeGroupingsServiceResult(
                        FAILURE + ": " + username + " may only add to exclude, include or owner group", action));
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
            gsrList.add(hs.makeGroupingsServiceResult(
                    FAILURE + ": " + username + "does not have permission to edit " + groupPath, action));
        }

        return gsrList;
    }

    //updates the last modified attribute of the group to the current date and time
    @Override
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

        return hs.makeGroupingsServiceResult(assignAttributesResults,
                "update last-modified attribute for " + groupPath + " to time " + time);

    }

    //checks to see if the user has the privilege in that group
    public WsGetGrouperPrivilegesLiteResult getGrouperPrivilege(String username, String privilegeName,
            String groupPath) {
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

    /*
     * @return date and time in yyyymmddThhmm format
     * ex. 20170314T0923
     */
    public String wsDateTime() {
        return Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");
    }

    //adds, removes, updates (operationName) the attribute for the membership
    public WsAssignAttributesResults assignMembershipAttributes(String operationName, String attributeUuid,
            String membershipID) {
        logger.info("assignMembershipAttributes; operation: "
                + operationName
                + "; uuid: "
                + attributeUuid
                + "; membershipID: "
                + membershipID
                + ";");

        return grouperFS.makeWsAssignAttributesResultsForMembership(ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP, operationName,
                attributeUuid, membershipID);
    }

}

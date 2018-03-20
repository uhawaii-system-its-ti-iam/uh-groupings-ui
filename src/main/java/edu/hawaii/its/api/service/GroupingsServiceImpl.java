package edu.hawaii.its.api.service;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;

@Service("groupingsService")
public class GroupingsServiceImpl implements GroupingsService {
    public static final Log logger = LogFactory.getLog(GroupingsServiceImpl.class);

    @Autowired
    private GroupAttributeService groupAttributeService;

    @Autowired
    GroupingAssignmentService groupingAssignmentService;

    @Autowired
    private GroupingFactoryService groupingFactoryService;

    @Autowired
    MemberAttributeService memberAttributeService;

    @Autowired
    MembershipService membershipService;

    // Constructor.
    public GroupingsServiceImpl() {
        // Empty.
    }

//    // Constructor.
//    public GroupingsServiceImpl(GrouperFactoryService grouperFactory) {
//        grouperFactoryService = grouperFactory;
//    }
//
//    //todo this is only for testing. can we find a better way to do this?
//    public GrouperFactoryService getGrouperFactoryService() {
//        return grouperFactoryService;
//    }
//
//    //todo this is only for testing. can we find a better way to do this?
//    public void setGrouperFactoryService(GrouperFactoryService gf) {
//        this.grouperFactoryService = gf;
//    }

    @Override
    public List<GroupingsServiceResult> addGrouping(String adminUsername, String groupingPath, List<String> basis,
            List<String> include, List<String> exclude, List<String> owners) {
        return groupingFactoryService.addGrouping(adminUsername, groupingPath, basis, include, exclude, owners);
    }

    @Override public List<GroupingsServiceResult> deleteGrouping(String adminUsername, String groupingPath) {
        return groupingFactoryService.deleteGrouping(adminUsername, groupingPath);
    }

    @Override public List<GroupingsServiceResult> addGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToAddUsername) {
        return membershipService.addGroupingMemberByUsername(ownerUsername, groupingPath, userToAddUsername);
    }

    @Override public List<GroupingsServiceResult> addGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToAddUuid) {
        return membershipService.addGroupingMemberByUuid(ownerUsername, groupingPath, userToAddUuid);
    }

    @Override public List<GroupingsServiceResult> addGroupMemberByUsername(String ownersername, String groupPath,
            String userToAddUsername) {
        return membershipService.addGroupMemberByUsername(ownersername, groupPath, userToAddUsername);
    }

    @Override public List<GroupingsServiceResult> addGroupMembersByUsername(String ownerUsername, String group,
            List<String> usersToAddUsername) {
        return membershipService.addGroupMembersByUsername(ownerUsername, group, usersToAddUsername);
    }

    @Override
    public List<GroupingsServiceResult> addGroupMemberByUuid(String ownerUsername, String group, String userToAddUuid) {
        return membershipService.addGroupMemberByUuid(ownerUsername, group, userToAddUuid);
    }

    @Override public List<GroupingsServiceResult> addGroupMembersByUuid(String ownerUsername, String group,
            List<String> usersToAddUuid) {
        return membershipService.addGroupMembersByUuid(ownerUsername, group, usersToAddUuid);
    }

    @Override
    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToDeleteUsername) {
        return membershipService.deleteGroupingMemberByUsername(ownerUsername, groupingPath, userToDeleteUsername);
    }

    @Override public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToDeleteUuid) {
        return membershipService.deleteGroupingMemberByUuid(ownerUsername, groupingPath, userToDeleteUuid);
    }

    @Override public GroupingsServiceResult deleteGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToDeleteUsername) {
        return membershipService.deleteGroupMemberByUsername(ownerUsername, groupPath, userToDeleteUsername);
    }

    @Override public GroupingsServiceResult deleteGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToDeleteUuid) {
        return membershipService.deleteGroupMemberByUuid(ownerUsername, groupPath, userToDeleteUuid);
    }

    @Override public GroupingsServiceResult addAdmin(String adminUsername, String adminToAddUsername) {
        return membershipService.addAdmin(adminUsername, adminToAddUsername);
    }

    @Override public GroupingsServiceResult deleteAdmin(String adminUsername, String adminToDeleteUsername) {
        return membershipService.deleteAdmin(adminUsername, adminToDeleteUsername);
    }

    @Override public List<GroupingsServiceResult> optIn(String username, String groupingPath) {
        return membershipService.optIn(username, groupingPath);
    }

    @Override public List<GroupingsServiceResult> optOut(String username, String groupingPath) {
        return membershipService.optOut(username, groupingPath);
    }

    @Override
    public GroupingsServiceResult changeListservStatus(String groupingPath, String ownerUsername, boolean listservOn) {
        return groupAttributeService.changeListservStatus(groupingPath, ownerUsername, listservOn);
    }

    @Override
    public List<GroupingsServiceResult> changeOptInStatus(String groupingPath, String ownerUsername, boolean optInOn) {
        return groupAttributeService.changeOptInStatus(groupingPath, ownerUsername, optInOn);
    }

    @Override public List<GroupingsServiceResult> changeOptOutStatus(String groupingPath, String ownerUsername,
            boolean optOutOn) {
        return groupAttributeService.changeOptOutStatus(groupingPath, ownerUsername, optOutOn);
    }

    @Override public boolean hasListserv(String groupingPath) {
        return groupAttributeService.hasListserv(groupingPath);
    }

    @Override public boolean optOutPermission(String groupingPath) {
        return groupAttributeService.optOutPermission(groupingPath);
    }

    @Override public boolean optInPermission(String groupingPath) {
        return groupAttributeService.optInPermission(groupingPath);
    }

    @Override
    public GroupingsServiceResult assignOwnership(String groupingPath, String ownerUsername, String newOwnerUsername) {
        return memberAttributeService.assignOwnership(groupingPath, ownerUsername, newOwnerUsername);
    }

    @Override
    public GroupingsServiceResult removeOwnership(String groupingPath, String username, String ownerToRemoveUsername) {
        return memberAttributeService.removeOwnership(groupingPath, username, ownerToRemoveUsername);
    }

    @Override public boolean isMember(String groupPath, String username) {
        return memberAttributeService.isMember(groupPath, username);
    }

    @Override public boolean isMember(String groupPath, Person person) {
        return memberAttributeService.isMember(groupPath, person);
    }

    @Override public boolean isOwner(String groupingPath, String username) {
        return memberAttributeService.isOwner(groupingPath, username);
    }

    @Override public boolean isAdmin(String username) {
        return memberAttributeService.isAdmin(username);
    }

    @Override public boolean isApp(String username) {
        return memberAttributeService.isApp(username);
    }

    @Override public boolean isSuperuser(String username) {
        return memberAttributeService.isSuperuser(username);
    }

    @Override public boolean isSelfOpted(String groupPath, String username) {
        return memberAttributeService.isSelfOpted(groupPath, username);
    }

    @Override public boolean groupOptInPermission(String username, String groupPath) {
        return membershipService.groupOptInPermission(username, groupPath);
    }

    @Override public boolean groupOptOutPermission(String username, String groupPath) {
        return membershipService.groupOptOutPermission(username, groupPath);
    }

    @Override public List<Grouping> groupingsIn(List<String> groupPaths) {
        return groupingAssignmentService.groupingsIn(groupPaths);
    }

    @Override public List<Grouping> groupingsOwned(List<String> groupPaths) {
        return groupingAssignmentService.groupingsOwned(groupPaths);
    }

    @Override public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths) {
        return groupingAssignmentService.groupingsOptedInto(username, groupPaths);
    }

    @Override public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths) {
        return groupingAssignmentService.groupingsOptedOutOf(username, groupPaths);
    }

    @Override public Grouping getGrouping(String groupingPath, String ownerUsername) {
        return groupingAssignmentService.getGrouping(groupingPath, ownerUsername);
    }

    @Override public GroupingAssignment getGroupingAssignment(String username) {
        return groupingAssignmentService.getGroupingAssignment(username);
    }

    @Override public AdminListsHolder adminLists(String adminUsername) {
        return groupingAssignmentService.adminLists(adminUsername);
    }
}
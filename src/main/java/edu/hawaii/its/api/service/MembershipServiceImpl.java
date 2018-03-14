package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsServiceResult;

import org.springframework.stereotype.Service;

import java.util.List;

@Service("membershipService")
public class MembershipServiceImpl implements MembershipService{

    //todo implement
    @Override public List<GroupingsServiceResult> addGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToAddUsername) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> addGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToAddUuid) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> addGroupMemberByUsername(String ownersername, String groupPath,
            String userToAddUsername) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> addGroupMembersByUsername(String ownerUsername, String group,
            List<String> usersToAddUsername) {
        return null;
    }

    //todo implement
    @Override
    public List<GroupingsServiceResult> addGroupMemberByUuid(String ownerUsername, String group, String userToAddUuid) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> addGroupMembersByUuid(String ownerUsername, String group,
            List<String> usersToAddUuid) {
        return null;
    }

    //todo implement
    @Override
    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToDeleteUsername) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToDeleteUuid) {
        return null;
    }

    //todo implement
    @Override public GroupingsServiceResult deleteGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToDeleteUsername) {
        return null;
    }

    //todo implement
    @Override public GroupingsServiceResult deleteGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToDeleteUuid) {
        return null;
    }

    //todo implement
    @Override public GroupingsServiceResult addAdmin(String adminUsername, String adminToAddUsername) {
        return null;
    }

    //todo implement
    @Override public GroupingsServiceResult deleteAdmin(String adminUsername, String adminToDeleteUsername) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> optIn(String username, String groupingPath) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> optOut(String username, String groupingPath) {
        return null;
    }
}

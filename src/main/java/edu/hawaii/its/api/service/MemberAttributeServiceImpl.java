package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;

import org.springframework.stereotype.Service;

@Service("memberAttributeService")
public class MemberAttributeServiceImpl implements MemberAttributeService{

    //todo implement
    @Override
    public GroupingsServiceResult assignOwnership(String groupingPath, String ownerUsername, String newOwnerUsername) {
        return null;
    }

    //todo implement
    @Override
    public GroupingsServiceResult removeOwnership(String groupingPath, String username, String ownerToRemoveUsername) {
        return null;
    }

    //todo implement
    @Override public boolean isMember(String groupPath, String username) {
        return false;
    }

    //todo implement
    @Override public boolean isMember(String groupPath, Person person) {
        return false;
    }

    //todo implement
    @Override public boolean isOwner(String groupingPath, String username) {
        return false;
    }

    //todo implement
    @Override public boolean isAdmin(String username) {
        return false;
    }

    //todo implement
    @Override public boolean isApp(String username) {
        return false;
    }

    //todo implement
    @Override public boolean isSuperuser(String username) {
        return false;
    }

    //todo implement
    @Override public boolean isSelfOpted(String groupPath, String username) {
        return false;
    }

    //todo implement
    @Override public boolean groupOptInPermission(String username, String groupPath) {
        return false;
    }

    //todo implement
    @Override public boolean groupOptOutPermission(String username, String groupPath) {
        return false;
    }
}

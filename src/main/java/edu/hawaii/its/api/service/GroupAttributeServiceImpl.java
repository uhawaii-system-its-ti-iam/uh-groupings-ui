package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.GroupingsServiceResult;

import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupAttributeService")
public class GroupAttributeServiceImpl implements GroupAttributeService{
    //todo implement
    @Override
    public GroupingsServiceResult changeListservStatus(String groupingPath, String ownerUsername, boolean listservOn) {
        return null;
    }

    //todo implement
    @Override
    public List<GroupingsServiceResult> changeOptInStatus(String groupingPath, String ownerUsername, boolean optInOn) {
        return null;
    }

    //todo implement
    @Override public List<GroupingsServiceResult> changeOptOutStatus(String groupingPath, String ownerUsername,
            boolean optOutOn) {
        return null;
    }

    //todo implement
    @Override public boolean hasListserv(String groupingPath) {
        return false;
    }

    //todo implement
    @Override public boolean optOutPermission(String groupingPath) {
        return false;
    }

    //todo implement
    @Override public boolean optInPermission(String groupingPath) {
        return false;
    }
}

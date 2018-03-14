package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;

import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupingAssignmentService")
public class GroupingAssignmentServiceImpl implements GroupingAssignmentService{

    //todo implement
    @Override public List<Grouping> groupingsIn(List<String> groupPaths) {
        return null;
    }

    //todo implement
    @Override public List<Grouping> groupingsOwned(List<String> groupPaths) {
        return null;
    }

    //todo implement
    @Override public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths) {
        return null;
    }

    //todo implement
    @Override public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths) {
        return null;
    }

    //todo implement
    @Override public Grouping getGrouping(String groupingPath, String ownerUsername) {
        return null;
    }

    //todo implement
    @Override public GroupingAssignment getGroupingAssignment(String username) {
        return null;
    }

    //todo implement
    @Override public AdminListsHolder adminLists(String adminUsername) {
        return null;
    }
}

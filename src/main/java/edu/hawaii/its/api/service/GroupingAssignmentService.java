package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;

import java.util.List;

public interface GroupingAssignmentService {

    public List<Grouping> groupingsIn(List<String> groupPaths);

    public List<Grouping> groupingsOwned(List<String> groupPaths);

    public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths);

    public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths);

    public Grouping getGrouping(String groupingPath, String ownerUsername);

    public GroupingAssignment getGroupingAssignment(String username);

    public AdminListsHolder adminLists(String adminUsername);
}

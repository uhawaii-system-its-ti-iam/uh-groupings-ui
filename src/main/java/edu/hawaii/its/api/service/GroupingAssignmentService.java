package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.Person;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;

import java.util.List;

public interface GroupingAssignmentService {

    public List<Grouping> groupingsIn(List<String> groupPaths);

    public List<Grouping> groupingsOwned(List<String> groupPaths);

    public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths);

    public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths);

    public Grouping getGrouping(String groupingPath, String ownerUsername);

    public GroupingAssignment getGroupingAssignment(String username);

    public AdminListsHolder adminLists(String adminUsername);

    //not to be included in the REST controller
    public Person makePerson(WsSubject subject, String[] attributeNames);

    public List<String> extractGroupPaths(List<WsGroup> groups);

    public Group makeGroup(WsGetMembersResults membersResults);

    public List<String> getGroupPaths(String username);

    Group getMembers(String owenrUsername, String groupPath);
}

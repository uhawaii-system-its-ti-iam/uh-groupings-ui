package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.*;

import java.util.List;

public interface GroupingsService {

    public List<GroupingsServiceResult> addGrouping(String username, String path, List<String> basis, List<String> include, List<String> exclude, List<String> owners);

    public List<GroupingsServiceResult> deleteGrouping(String username, String groupingPath);

    public boolean hasListserv(String grouping);

    public List<Grouping> groupingsIn(List<String> groupPaths);

    public List<Grouping> groupingsOwned(List<String> groupPaths);

    public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths);

    public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths);

    public boolean inGroup(String group, String username);

    public boolean inGroup(String group, Person person);

    public boolean checkSelfOpted(String group, String username);

    public GroupingsServiceResult addAdmin(String username, String adminToAdd);

    public GroupingsServiceResult deleteAdmin(String username, String adminToDelete);

    public List<GroupingsServiceResult> addGroupingMemberByUsername(String username, String groupingPath, String userToAddUsername);

    public List<GroupingsServiceResult> addGroupingMemberByUuid(String username, String groupingPath, String userToAddUuid);

    public List<GroupingsServiceResult> addGroupMemberByUsername(String username, String group, String userToAddUsername);

    public List<GroupingsServiceResult> addGroupMembersByUsername(String username, String group, List<String> usersToAddUsername);

    public List<GroupingsServiceResult> addGroupMemberByUuid(String username, String group, String userToAddUuid);

    public List<GroupingsServiceResult> addGroupMembersByUuid(String username, String group, List<String> usersToAddUuid);

    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String username, String groupingPath, String userToDeleteUsername);

    public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String username, String groupingPath, String userToDeleteUuid);

    public GroupingsServiceResult deleteGroupMemberByUsername(String username, String group, String userToDeleteUsername);

    public GroupingsServiceResult assignOwnership(String grouping, String username, String newOwner);

    public GroupingsServiceResult removeOwnership(String grouping, String username, String ownerToRemove);

    public Grouping getGrouping(String grouping, String username);

    public GroupingAssignment getGroupingAssignment(String username);

    public List<GroupingsServiceResult> optIn(String username, String grouping);

    public List<GroupingsServiceResult> optOut(String username, String grouping);

    public GroupingsServiceResult changeListservStatus(String grouping, String username, boolean listservOn);

    public List<GroupingsServiceResult> changeOptInStatus(String grouping, String username, boolean optInOn);

    public List<GroupingsServiceResult> changeOptOutStatus(String grouping, String username, boolean optOutOn);

    public boolean isOwner(String grouping, String username);

    public boolean isAdmin(String username);

    public boolean isApp(String username);

    public boolean isSuperuser(String username);

    public boolean groupOptInPermission(String username, String group);

    public GroupingsServiceResult addSelfOpted(String group, String username);

    public GroupingsServiceResult removeSelfOpted(String group, String username);

    public boolean groupOptOutPermission(String username, String group);

    public GroupingsServiceResult updateLastModified(String group);

    public boolean groupHasAttribute(String grouping, String nameName);

    public boolean optOutPermission(String grouping);

    public boolean optInPermission(String grouping);

    public AdminListsHolder adminLists(String username);

    public String parentGroupingPath(String group);

}

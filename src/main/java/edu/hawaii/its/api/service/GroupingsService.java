package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.*;

import java.util.List;

public interface GroupingsService {

    /////////////////////////////////////////////
    //  add or remove groupings /////////////////
    /////////////////////////////////////////////

    public List<GroupingsServiceResult> addGrouping(String adminUsername,
            String groupingPath,
            List<String> basis,
            List<String> include,
            List<String> exclude,
            List<String> owners);

    public List<GroupingsServiceResult> deleteGrouping(String adminUsername, String groupingPath);



    /////////////////////////////////////////////
    //  add or remove members   /////////////////
    /////////////////////////////////////////////

    public List<GroupingsServiceResult> addGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToAddUsername);

    public List<GroupingsServiceResult> addGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToAddUuid);

    public List<GroupingsServiceResult> addGroupMemberByUsername(String ownersername, String groupPath,
            String userToAddUsername);

    public List<GroupingsServiceResult> addGroupMembersByUsername(String ownerUsername, String group,
            List<String> usersToAddUsername);

    public List<GroupingsServiceResult> addGroupMemberByUuid(String ownerUsername, String group, String userToAddUuid);

    public List<GroupingsServiceResult> addGroupMembersByUuid(String ownerUsername, String group,
            List<String> usersToAddUuid);

    public List<GroupingsServiceResult> deleteGroupingMemberByUsername(String ownerUsername, String groupingPath,
            String userToDeleteUsername);

    public List<GroupingsServiceResult> deleteGroupingMemberByUuid(String ownerUsername, String groupingPath,
            String userToDeleteUuid);

    public GroupingsServiceResult deleteGroupMemberByUsername(String ownerUsername, String groupPath,
            String userToDeleteUsername);

    public GroupingsServiceResult deleteGroupMemberByUuid(String ownerUsername, String groupPath,
            String userToDeleteUuid);

    //todo deleteGroupMembersByUuid
    //todo deleteGroupMembersByUsername

    public GroupingsServiceResult addAdmin(String adminUsername, String adminToAddUsername);

    public GroupingsServiceResult deleteAdmin(String adminUsername, String adminToDeleteUsername);

    public List<GroupingsServiceResult> optIn(String username, String groupingPath);

    public List<GroupingsServiceResult> optOut(String username, String groupingPath);



    //////////////////////////////////////////
    //  group attributes //////////////////
    //////////////////////////////////////////

    public GroupingsServiceResult changeListservStatus(String groupingPath, String ownerUsername, boolean listservOn);

    public List<GroupingsServiceResult> changeOptInStatus(String groupingPath, String ownerUsername, boolean optInOn);

    public List<GroupingsServiceResult> changeOptOutStatus(String groupingPath, String ownerUsername, boolean optOutOn);

    public boolean hasListserv(String groupingPath);

    public boolean optOutPermission(String groupingPath);

    public boolean optInPermission(String groupingPath);



    //////////////////////////////////////////
    //  member attributes   //////////////////
    //////////////////////////////////////////

    public GroupingsServiceResult assignOwnership(String groupingPath, String ownerUsername, String newOwnerUsername);

    public GroupingsServiceResult removeOwnership(String groupingPath, String username, String ownerToRemoveUsername);

    public boolean isMember(String groupPath, String username);

    public boolean isMember(String groupPath, Person person);

    public boolean isOwner(String groupingPath, String username);

    public boolean isAdmin(String username);

    public boolean isApp(String username);

    public boolean isSuperuser(String username);

    public boolean isSelfOpted(String groupPath, String username);

    public boolean groupOptInPermission(String username, String groupPath);

    public boolean groupOptOutPermission(String username, String groupPath);




    //////////////////////////////////////////
    //  fetch groups    //////////////////////
    //////////////////////////////////////////

    public List<Grouping> groupingsIn(List<String> groupPaths);

    public List<Grouping> groupingsOwned(List<String> groupPaths);

    public List<Grouping> groupingsOptedInto(String username, List<String> groupPaths);

    public List<Grouping> groupingsOptedOutOf(String username, List<String> groupPaths);

    public Grouping getGrouping(String groupingPath, String ownerUsername);

    public GroupingAssignment getGroupingAssignment(String username);

    public AdminListsHolder adminLists(String adminUsername);
}

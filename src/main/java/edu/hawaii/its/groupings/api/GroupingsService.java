package edu.hawaii.its.groupings.api;

import edu.hawaii.its.groupings.api.type.Group;
import edu.hawaii.its.groupings.api.type.GroupingsServiceResult;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

import java.util.List;

public interface GroupingsService {

    public boolean hasListserv(String grouping);

    public List<Grouping> groupingsIn(String username);

    public List<Grouping> groupingsOptedInto(String username);

    public List<Grouping> groupingsOptedOutOf(String username);

    public boolean inGroup(String group, String username);

    public boolean checkSelfOpted(String group, String username);

    public GroupingsServiceResult addMemberAs(String username, String group, String userToAdd);

    public GroupingsServiceResult deleteMemberAs(String username, String group, String userToDelete);

    public GroupingsServiceResult assignOwnership(String grouping, String username, String newOwner);

    public GroupingsServiceResult removeOwnership(String grouping, String username, String ownerToRemove);

    public Grouping getGrouping(String grouping, String username);

    public MyGroupings getMyGroupings(String username);

    public List<GroupingsServiceResult> optIn(String username, String grouping);

    public List<GroupingsServiceResult> optOut(String username, String grouping);

    public List<GroupingsServiceResult> cancelOptIn(String grouping, String username);

    public List<GroupingsServiceResult> cancelOptOut(String grouping, String username);

    public GroupingsServiceResult changeListServeStatus(String grouping, String username, boolean listServeOn);

    public GroupingsServiceResult changeOptInStatus(String grouping, String username, boolean optInOn);

    public GroupingsServiceResult changeOptOutStatus(String grouping, String username, boolean optOutOn);

    public Group findOwners(String grouping, String username);

    public boolean isOwner(String grouping, String username);

    public boolean groupOptInPermission(String username, String group);

    public GroupingsServiceResult addSelfOpted(String group, String username);

    public GroupingsServiceResult removeSelfOpted(String group, String username);

    public boolean groupOptOutPermission(String username, String group);

    public GroupingsServiceResult updateLastModified(String group);

    public boolean groupHasAttribute(String grouping, String nameName);

    public boolean optOutPermission(String grouping);

    public boolean optInPermission(String grouping);

}

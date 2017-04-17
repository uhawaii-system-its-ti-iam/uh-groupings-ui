package edu.hawaii.its.groupings.api;

import java.util.List;

import edu.hawaii.its.groupings.api.type.Group;
import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;
import edu.internet2.middleware.grouperClient.ws.beans.WsAddMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignAttributesResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

public interface GroupingsService {

    public boolean hasListserv(String grouping);

    public List<Grouping> groupingsIn(String username);

    public boolean inGroup(String group, String username);

    public WsSubjectLookup makeWsSubjectLookup(String username);

    public boolean checkSelfOpted(String group, WsSubjectLookup lookup);

    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String privilegeName);

    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String privilegeName, String group);

    public WsAddMemberResults addMemberAs(String username, String group, String userToAdd);

    public WsDeleteMemberResults deleteMemberAs(String username, String group, String userToDelete);

    public Object[] assignOwnership(String grouping, String username, String newOwner);

    public Object[] removeOwnership(String grouping, String username, String ownerToRemove);

    public Grouping getGrouping(String grouping, String username);

    public MyGroupings getMyGroupings(String username);

    public Object[] optIn(String username, String grouping);

    public Object[] optOut(String username, String grouping);

    public Object[] cancelOptIn(String grouping, String username);

    public Object[] cancelOptOut(String grouping, String username);

    public String changeListServeStatus(String grouping, String username, boolean listServeOn);

    public String changeOptInStatus(String grouping, String username, boolean optInOn);

    public String changeOptOutStatus(String grouping, String username, boolean optOutOn);

    public Group findOwners(String grouping, String username);

    public boolean isOwner(String grouping, String username);

    public boolean groupOptInPermission(String username, String group);

    public WsAssignAttributesResults addSelfOpted(String group, String username);

    public WsAssignAttributesResults removeSelfOpted(String group, String username);

    public boolean groupOptOutPermission(String username, String group);

    public WsAssignAttributesResults updateLastModified(String group);

    public WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String group, String nameName);

    public boolean optOutPermission(String grouping);

    public boolean optInPermission(String grouping);
}

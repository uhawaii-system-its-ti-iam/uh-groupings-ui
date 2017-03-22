package edu.hawaii.its.holiday.api;

import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import edu.hawaii.its.holiday.util.Dates;
import edu.internet2.middleware.grouperClient.api.GcAssignAttributes;
import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.api.GcGetGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.api.GcGetMemberships;
import edu.internet2.middleware.grouperClient.api.GcHasMember;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignAttributesResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssignValue;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGrouperPrivilegeResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsHasMemberResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsHasMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@Service
public class GroupingsService {

    private static final Log logger = LogFactory.getLog(GroupingsService.class);

    public static final String UUID = "ef62bf0473614b379695ecec6cb8b3b5";
    public static final String SELF_OPTED = "uh-settings:attributes:for-memberships:uh-grouping:self-opted";

    /**
     * adds the self-opted attribute to a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    public WsAssignAttributesResults addSelfOpted(String group, String username) {
        logger.info("addSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            if (!checkSelfOpted(group, lookup)) {
                WsGetMembershipsResults includeMembershipsResults = membershipsResults(lookup, group);
                String membershipID = includeMembershipsResults.getWsMemberships()[0].getMembershipId();
                String operation = "assign_attr";
                return assignAttributesResults(operation, UUID, membershipID);
            }
        }
        return new WsAssignAttributesResults();
    }

    /**
     * @param group:           group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param wsSubjectLookup: WsSubjectLookup of user
     * @return true if the membership between the user and the group has the "self-opted" attribute
     */
    public boolean checkSelfOpted(String group, WsSubjectLookup lookup) {
        logger.info("checkSelfOpted; group: " + group + "; wsSubjectLookup: " + lookup);

        if (inGroup(group, lookup.getSubjectIdentifier())) {
            WsGetMembershipsResults wsGetMembershipsResults = membershipsResults(lookup, group);
            String assignType = "imm_mem";
            String uuid = UUID;
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            WsAttributeAssign[] wsAttributes = attributeAssign(assignType, uuid, membershipID);
            for (WsAttributeAssign att : wsAttributes) {
                if (att.getAttributeDefNameName().equals(SELF_OPTED)) {
                    return true; // We are done, get out.
                }
            }
        }
        return false;
    }

    /**
     * @param group:    group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param username: subjectIdentifier of user to be searched for
     * @return true if username is a member of group
     */
    public boolean inGroup(String group, String username) {
        logger.info("inGroup; group: " + group + "; username: " + username);

        WsHasMemberResults wsHasMemberResults =
                new GcHasMember()
                        .assignGroupName(group)
                        .addSubjectIdentifier(username)
                        .execute();
        WsHasMemberResult[] memberResultArray = wsHasMemberResults.getResults();

        boolean userIsInGroup = false;
        for (WsHasMemberResult hasMember : memberResultArray) {
            if (hasMember.getResultMetadata().getResultCode().equals("IS_MEMBER")) {
                userIsInGroup = true;
                break; // Found it, break out.
            }
        }
        return userIsInGroup;
    }

    public boolean isOwner(String grouping, String username) {
        logger.info("isOwner; grouping: " + grouping + "; username: " + username);

        if (username == null) {
            return false;
        }

        WsSubjectLookup lookup = makeWsSubjectLookup(username);

        WsGetGrouperPrivilegesLiteResult privilegeResults =
                new GcGetGrouperPrivilegesLite()
                        .assignSubjectLookup(lookup)
                        .assignPrivilegeName("update")
                        .assignPrivilegeName("read")
                        .assignGroupName(grouping + ":include")
                        .assignGroupName(grouping + ":include")
                        .addSubjectAttributeName("uid")
                        .execute();

        for (WsGrouperPrivilegeResult owner : privilegeResults.getPrivilegeResults()) {
            String values[] = owner.getOwnerSubject().getAttributeValues();
            if (values != null && values.length > 0) {
                if (username.equals(owner.getOwnerSubject().getAttributeValue(0))) {
                    return true; // Found it, get out.
                }
            }
        }

        return false;
    }

    /**
     * removes the self-opted attribute from a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    public WsAssignAttributesResults removeSelfOpted(String group, String username) {
        logger.info("removeSelfOpted; group: " + group + "; username: " + username);

        if (inGroup(group, username)) {
            WsSubjectLookup lookup = makeWsSubjectLookup(username);
            if (checkSelfOpted(group, lookup)) {
                WsGetMembershipsResults getIncludeMembershipsResults = membershipsResults(lookup, group);
                String membershipID = getIncludeMembershipsResults.getWsMemberships()[0].getMembershipId();
                String operation = "remove_attr";
                return assignAttributesResults(operation, UUID, membershipID);
            }
        }
        return new WsAssignAttributesResults();
    }

    /**
     * @return date and time in yyyymmddThhmm format
     * ex. 20170314T0923
     */
    public String wsDateTime() {
        return Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");
    }

    /**
     * checks for permission to opt out of a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt out, false if not
     */
    public boolean groupOptOutPermission(String username, String group) {
        logger.info("groupOptOutPermission; group: " + group + "; username: " + username);
        String privilegeName = "optout";
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, group, privilegeName);
        return result.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED");
    }

    /**
     * checks for permission to opt into a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt in, false if not
     */
    public boolean groupOptInPermission(String username, String group) {
        logger.info("groupOptInPermission; group: " + group + "; username: " + username);
        String privilegeName = "optin";
        WsGetGrouperPrivilegesLiteResult result = grouperPrivilegesLite(username, group, privilegeName);
        return result.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED");
    }

    /**
     * updates the last modified time of a group
     * this should be done whenever a group is modified
     * <p>
     * ie. a member was added or deleted
     *
     * @param group: group whos last modified attribute will be updated
     * @return results from Grouper Web Service
     */
    public WsAssignAttributesResults updateLastModified(String group) {

        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(wsDateTime());

        return new GcAssignAttributes()
                .assignAttributeAssignType("group")
                .assignAttributeAssignOperation("assign_attr")
                .addOwnerGroupName(group)
                .addAttributeDefNameName("uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm")
                .assignAttributeAssignValueOperation("replace_values")
                .addValue(dateTimeValue)
                .execute();

    }

    // Helper method.
    public WsSubjectLookup makeWsSubjectLookup(String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        return wsSubjectLookup;
    }

    // Helper method.
    public WsAssignAttributesResults assignAttributesResults(String operation, String uuid, String membershipID) {
        String assignType = "imm_mem";
        return new GcAssignAttributes()
                .assignAttributeAssignType(assignType)
                .assignAttributeAssignOperation(operation)
                .addAttributeDefNameUuid(uuid)
                .addOwnerMembershipId(membershipID)
                .execute();
    }

    // Helper method.
    public WsAttributeAssign[] attributeAssign(String assignType, String uuid, String membershipID) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                new GcGetAttributeAssignments()
                        .assignAttributeAssignType(assignType)
                        .addAttributeDefNameUuid(uuid)
                        .addOwnerMembershipId(membershipID)
                        .execute();
        WsAttributeAssign[] wsAttributes = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();

        return wsAttributes != null ? wsAttributes : new WsAttributeAssign[0];
    }

    // Helper method.
    public WsGetAttributeAssignmentsResults attributeAssignmentsResults(String assignType, String group, String nameName) {
        return new GcGetAttributeAssignments().assignAttributeAssignType(assignType)
                .addOwnerGroupName(group)
                .addAttributeDefNameName(nameName)
                .execute();
    }

    // Helper method.
    public WsGetAttributeAssignmentsResults attributeAssignments(String assignType, String subjectAttributeName, String uuid) {
        return new GcGetAttributeAssignments()
                .assignAttributeAssignType(assignType)
                .addSubjectAttributeName(subjectAttributeName)
                .addAttributeDefUuid(uuid)
                .execute();
    }

    // Helper method.
    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String group, String privilegeName) {
        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        return new GcGetGrouperPrivilegesLite()
                .assignGroupName(group)
                .assignPrivilegeName(privilegeName)
                .assignSubjectLookup(lookup)
                .execute();
    }

    public WsGetGrouperPrivilegesLiteResult grouperPrivilegesLite(String username, String privilegeName) {
        WsSubjectLookup lookup = makeWsSubjectLookup(username);
        return new GcGetGrouperPrivilegesLite()
                .assignSubjectLookup(lookup)
                .assignPrivilegeName(privilegeName)
                .execute();
    }

    // Helper method.
    public WsGetMembershipsResults membershipsResults(WsSubjectLookup lookup, String group) {
        return new GcGetMemberships()
                .addWsSubjectLookup(lookup)
                .addGroupName(group)
                .execute();
    }

}

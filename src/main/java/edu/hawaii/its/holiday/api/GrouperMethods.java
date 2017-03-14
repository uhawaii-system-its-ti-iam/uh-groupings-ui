package edu.hawaii.its.holiday.api;

import edu.internet2.middleware.grouperClient.api.*;
import edu.internet2.middleware.grouperClient.ws.beans.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zknoebel on 3/14/2017.
 */
public class GrouperMethods {

   public GrouperMethods(){

    }

    /**
     * adds the self-opted attribute to a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    public WsAssignAttributesResults addSelfOpted(String group, String username) {
        WsSubjectLookup user = new WsSubjectLookup();
        user.setSubjectIdentifier(username);

        if (inGroup(group, username)) {
            if (!checkSelfOpted(group, user)) {
                WsGetMembershipsResults getIncludeMembershipsResults = new GcGetMemberships().addWsSubjectLookup(user).addGroupName(group).execute();
                String membershipID = getIncludeMembershipsResults.getWsMemberships()[0].getMembershipId();
                return new GcAssignAttributes().assignAttributeAssignType("imm_mem").assignAttributeAssignOperation("assign_attr").addAttributeDefNameUuid("ef62bf0473614b379695ecec6cb8b3b5").addOwnerMembershipId(membershipID).execute();
            } else {
                return new WsAssignAttributesResults();
            }
        } else {
            return new WsAssignAttributesResults();
        }
    }

    /**
     * @param group:           group to search through (include extension of Grouping ie. ":include" or ":exclude")
     * @param wsSubjectLookup: WsSubjectLookup of user
     * @return true if the membership between the user and the group has the "self-opted" attribute
     */
    public boolean checkSelfOpted(String group, WsSubjectLookup wsSubjectLookup) {
        WsGetMembershipsResults wsGetMembershipsResults = new GcGetMemberships().addWsSubjectLookup(wsSubjectLookup).addGroupName(group).execute();
        String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().assignAttributeAssignType("imm_mem").addAttributeDefNameUuid("ef62bf0473614b379695ecec6cb8b3b5").addOwnerMembershipId(membershipID).execute();
        WsAttributeAssign[] wsAttributes = wsGetAttributeAssignmentsResults.getWsAttributeAssigns();

        if (wsAttributes != null) {
            for (WsAttributeAssign att : wsAttributes) {
                if (att.getAttributeDefNameName().equals("uh-settings:attributes:for-memberships:uh-grouping:self-opted")) {
                    return true;
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

        WsHasMemberResults wsHasMemberResults = new GcHasMember().assignGroupName(group).addSubjectIdentifier(username).execute();
        WsHasMemberResult[] memberResultArray = wsHasMemberResults.getResults();

        boolean userIsInGroup = false;
        for (WsHasMemberResult hasMember : memberResultArray) {
            if (hasMember.getResultMetadata().getResultCode().equals("IS_MEMBER")) {
                userIsInGroup = true;
            }
        }
        return userIsInGroup;
    }

    /**
     * removes the self-opted attribute from a membership (combination of a group and a subject)
     *
     * @param group:    the group in the membership
     * @param username: the subject in the membership
     * @return the response from grouper web service or empty WsAssignAttributesResults object
     */
    public WsAssignAttributesResults removeSelfOpted(String group, String username) {
        WsSubjectLookup user = new WsSubjectLookup();
        user.setSubjectIdentifier(username);

        if (inGroup(group, username)) {
            if (checkSelfOpted(group, user)) {
                WsGetMembershipsResults getIncludeMembershipsResults = new GcGetMemberships().addWsSubjectLookup(user).addGroupName(group).execute();
                String membershipID = getIncludeMembershipsResults.getWsMemberships()[0].getMembershipId();
                return new GcAssignAttributes().assignAttributeAssignType("imm_mem").assignAttributeAssignOperation("remove_attr").addAttributeDefNameUuid("ef62bf0473614b379695ecec6cb8b3b5").addOwnerMembershipId(membershipID).execute();
            } else {
                return new WsAssignAttributesResults();
            }
        } else {
            return new WsAssignAttributesResults();
        }
    }

    /**
     * @return date and time in yyyymmddThhmm format
     * ex. 20170314T0923
     */
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hhmm");
        Date date = new Date();
        Date time = new Date();
        return dateFormat.format(date) + "T" + timeFormat.format(time);
    }

    /**
     * checks for permission to opt out of a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt out, false if not
     */
    public boolean groupOptOutPermission(String username, String group) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignGroupName(group).assignPrivilegeName("optout").assignSubjectLookup(wsSubjectLookup).execute();
        return wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED");
    }

    /**
     * checks for permission to opt into a group
     *
     * @param username: user who's permission is being checked
     * @param group:    group the user permission is being checked for
     * @return true if the user has the permission to opt in, false if not
     */
    public boolean groupOptInPermission(String username, String group) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignGroupName(group).assignPrivilegeName("optin").assignSubjectLookup(wsSubjectLookup).execute();
        return wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED");
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
        dateTimeValue.setValueSystem(getDateTime());

        return new GcAssignAttributes().assignAttributeAssignType("group").assignAttributeAssignOperation("assign_attr").addOwnerGroupName(group).addAttributeDefNameName("uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm").assignAttributeAssignValueOperation("replace_values").addValue(dateTimeValue).execute();

    }
}

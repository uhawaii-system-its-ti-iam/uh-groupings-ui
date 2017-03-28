package edu.hawaii.its.holiday.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.hawaii.its.holiday.api.Grouping;
import edu.hawaii.its.holiday.api.GroupingsService;
import edu.internet2.middleware.grouperClient.api.GcAddMember;
import edu.internet2.middleware.grouperClient.api.GcAssignGrouperPrivileges;
import edu.internet2.middleware.grouperClient.api.GcDeleteMember;
import edu.internet2.middleware.grouperClient.api.GcGetGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.api.GcGetGroups;
import edu.internet2.middleware.grouperClient.api.GcGetMembers;
import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.WsAssignGrouperPrivilegesResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembersResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetMembershipsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGrouperPrivilegeResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubject;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

/**
 * Created by zknoebel on 12/12/16.
 * <p>
 * file containing the mappings for all groupings methods
 */

@RestController
public class GroupingsController {

    private final static String UUID = "1d7365a23c994f5f83f7b541d4a5fa5e";

    @Autowired
    private GroupingsService gs;

    /**
     * eventually this is intended to give the user the ability to add a Grouping in one of the Groupings that they own,
     * for now it will bring the user to the web page where they can submit a request to the UHGrouper staff
     * //@param grouping:    String containing the path of the parent Grouping
     * //@param newGrouping: String containing the name of the Grouping to be created
     *
     * @return information about the new Grouping and its success
     */
    @RequestMapping("/addGrouping")
    //    public WsGroupSaveResults addGrouping(@RequestParam String grouping, @RequestParam String newGrouping) {
    public RedirectView addGrouping() {
        //return new GcGroupSave().addGroupToSave(grouping + ":" + newGrouping).execute();
        //TODO currently this method is not to be implemented because responsibility to create a new
        //TODO grouping is still going to go through the UH Grouper staff, so the individual should be sent to this address
        //TODO https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form");
        return redirectView;
    }

    /**
     * adds a member to a Grouping that the user owns
     *
     * @param grouping  :  String containing the path of the Grouping
     * @param username  :  username of the subject preforming the action
     * @param userToAdd : username of the member to be added
     * @return information about the new member and its success
     */
    @RequestMapping("/addMember")
    public Object[] addMember(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToAdd) {
        Object[] results = new Object[5];

        WsSubjectLookup user = gs.makeWsSubjectLookup(username);

        results[2] = gs.removeSelfOpted(grouping + ":exclude", userToAdd);
        results[0] = gs.addMemberAs(user, grouping + ":include", userToAdd);
        results[1] = gs.deleteMemberAs(user, grouping + ":exclude", userToAdd);
        results[3] = gs.updateLastModified(grouping + ":exclude");
        results[4] = gs.updateLastModified(grouping + ":include");

        return results;
    }

    /**
     * gives the user read, update and view privileges for the Grouping
     *
     * @param username: username of subject preforming action
     * @param grouping: path to the grouping that the newOwner will own
     * @param newOwner: String containing the username of the new owner
     * @return information about the new owner and its success
     */
    @RequestMapping("/assignOwnership")
    public WsAssignGrouperPrivilegesResults[] assignOwnership(@RequestParam String grouping, @RequestParam String username,
            @RequestParam String newOwner) {
        WsAssignGrouperPrivilegesResults[] wsAssignGrouperPrivilegesResultsArray = new WsAssignGrouperPrivilegesResults[4];

        WsSubjectLookup newOwnerLookup = gs.makeWsSubjectLookup(newOwner);
        WsSubjectLookup currentUserLookup = gs.makeWsSubjectLookup(username);

        WsGroupLookup includeGroupLookup = new WsGroupLookup();
        includeGroupLookup.setGroupName(grouping + ":include");
        WsGroupLookup basisGroupLookup = new WsGroupLookup();
        basisGroupLookup.setGroupName(grouping + ":basis");
        WsGroupLookup basisPlusIncludeGroupLookup = new WsGroupLookup();
        basisPlusIncludeGroupLookup.setGroupName(grouping + ":basis+include");
        WsGroupLookup excludeGroupLookup = new WsGroupLookup();
        excludeGroupLookup.setGroupName(grouping + ":exclude");

        wsAssignGrouperPrivilegesResultsArray[0] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(basisPlusIncludeGroupLookup).addSubjectLookup(newOwnerLookup)
                .addPrivilegeName("view").addPrivilegeName("read").assignAllowed(true).execute();
        wsAssignGrouperPrivilegesResultsArray[1] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(basisGroupLookup).addSubjectLookup(newOwnerLookup)
                .addPrivilegeName("view").addPrivilegeName("read").assignAllowed(true).execute();
        wsAssignGrouperPrivilegesResultsArray[2] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(excludeGroupLookup).addSubjectLookup(newOwnerLookup)
                .addPrivilegeName("view").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(true).execute();
        wsAssignGrouperPrivilegesResultsArray[3] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(includeGroupLookup).addSubjectLookup(newOwnerLookup)
                .addPrivilegeName("view").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(true).execute();
        return wsAssignGrouperPrivilegesResultsArray;
        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }

    /**
     * removes a Grouping
     *
     * @return information about the deleted Grouping and its success
     */
    @RequestMapping("/deleteGrouping")
    //    public WsGroupDeleteResults deleteGrouping(@RequestParam String grouping) {
    public RedirectView deleteGrouping() {
        //        WsGroupLookup wsGroupLookup = new WsGroupLookup();
        //        wsGroupLookup.setGroupName(grouping);
        //        new GcGroupDelete().addGroupLookup(wsGroupLookup).execute();
        //TODO currently this method is not to be implemented because responsibility to create a new
        //TODO grouping is still going to go through the UH Grouper staff, so the individual should be sent to this address
        //TODO https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form
        // email its-iam-help@hawaii.edu for help in deleting a Grouping
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form");
        return redirectView;
    }

    /**
     * removes a member from a Grouping that the user is an owner of
     *
     * @param grouping     :     String containing the path of the Grouping
     * @param username     :     username of the subject preforming the action
     * @param userToDelete : String containing the username of the user to be removed from the Grouping
     * @return information about the deleted member and its success
     */
    @RequestMapping("/deleteMember")
    public Object[] deleteMember(@RequestParam String grouping, @RequestParam String username,
            @RequestParam String userToDelete) {
        Object[] results = new Object[5];

        WsSubjectLookup user = gs.makeWsSubjectLookup(username);

        results[2] = gs.removeSelfOpted(grouping + ":include", userToDelete);
        results[0] = gs.deleteMemberAs(user, grouping + ":include", userToDelete);
        results[1] = gs.addMemberAs(user, grouping + ":exclude", userToDelete);
        results[3] = gs.updateLastModified(grouping + ":exclude");
        results[4] = gs.updateLastModified(grouping + ":include");

        return results;
    }

    /**
     * removes ownership privileges from the user specified
     *
     * @param username:      username of the subject preforming the action
     * @param grouping:      String containing the path of the Grouping
     * @param ownerToRemove: String containing the name of the user who's privileges will be removed
     * @return information about the member who's ownership privileges have been removed and its success
     */
    @RequestMapping("/removeOwnership")
    public WsAssignGrouperPrivilegesResults[] removeOwnership(@RequestParam String grouping, @RequestParam String username,
            @RequestParam String ownerToRemove) {
        WsAssignGrouperPrivilegesResults[] wsAssignGrouperPrivilegesResultsArray = new WsAssignGrouperPrivilegesResults[4];

        WsSubjectLookup ownerToRemoveLookup = gs.makeWsSubjectLookup(ownerToRemove);
        WsSubjectLookup currentUserLookup = gs.makeWsSubjectLookup(username);

        WsGroupLookup includeGroupLookup = new WsGroupLookup();
        includeGroupLookup.setGroupName(grouping + ":include");
        WsGroupLookup basisGroupLookup = new WsGroupLookup();
        basisGroupLookup.setGroupName(grouping + ":basis");
        WsGroupLookup basisPlusIncludeGroupLookup = new WsGroupLookup();
        basisPlusIncludeGroupLookup.setGroupName(grouping + ":basis+include");
        WsGroupLookup excludeGroupLookup = new WsGroupLookup();
        excludeGroupLookup.setGroupName(grouping + ":exclude");

        wsAssignGrouperPrivilegesResultsArray[0] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(basisGroupLookup).addSubjectLookup(ownerToRemoveLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false)
                .execute();
        wsAssignGrouperPrivilegesResultsArray[1] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(basisPlusIncludeGroupLookup).addSubjectLookup(ownerToRemoveLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false)
                .execute();
        wsAssignGrouperPrivilegesResultsArray[2] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(excludeGroupLookup).addSubjectLookup(ownerToRemoveLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false)
                .execute();
        wsAssignGrouperPrivilegesResultsArray[3] = new GcAssignGrouperPrivileges().assignActAsSubject(currentUserLookup)
                .assignGroupLookup(includeGroupLookup).addSubjectLookup(ownerToRemoveLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false)
                .execute();

        return wsAssignGrouperPrivilegesResultsArray;
        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }

    /**
     * finds all the members of a group
     *
     * @param grouping : String containing the path of the Grouping to be searched
     * @param username : username of the subject preforming the action
     * @return information for all of the members
     */
    @RequestMapping("/getMembers")
    public Grouping getMembers(@RequestParam String grouping, @RequestParam String username) {
        Grouping groups = new Grouping();

        WsSubjectLookup user = gs.makeWsSubjectLookup(username);

        WsGetMembersResults basisResults = gs.getMembersAs(user, grouping + ":basis");
        WsGetMembersResults basisPlusIncludeResults = gs.getMembersAs(user, grouping + ":basis+include");
        WsGetMembersResults excludeResults = gs.getMembersAs(user, grouping + ":exclude");
        WsGetMembersResults includeResults = gs.getMembersAs(user, grouping + ":include");
        WsGetMembersResults basisPlusIncludeMinusExcludeResults = gs.getMembersAs(user, grouping);

        groups.setBasis(basisResults.getResults()[0].getWsSubjects());
        groups.setBasisPlusInclude(basisPlusIncludeResults.getResults()[0].getWsSubjects());
        groups.setExclude(excludeResults.getResults()[0].getWsSubjects());
        groups.setInclude(includeResults.getResults()[0].getWsSubjects());
        groups.setBasisPlusIncludeMinusExclude(basisPlusIncludeMinusExcludeResults.getResults()[0].getWsSubjects());

        return groups;
    }

    /**
     * finds all of the owners of a group
     *
     * @param username: username of the subject preforming the action
     * @return information for all of the owners
     */
    @RequestMapping("/getOwners")
    public ArrayList<WsSubject> getOwners(@RequestParam String grouping, @RequestParam String username) {
        WsSubjectLookup lookup = gs.makeWsSubjectLookup(username);
        String group = grouping + ":include";
        String privilegeName = "update";
        WsGetGrouperPrivilegesLiteResult privileges =
                new GcGetGrouperPrivilegesLite()
                        .assignActAsSubject(lookup)
                        .assignGroupName(group)
                        .assignPrivilegeName(privilegeName)
                        .addSubjectAttributeName("uid")
                        .execute();
        ArrayList<WsSubject> subjects = new ArrayList<>();

        for (WsGrouperPrivilegeResult result : privileges.getPrivilegeResults()) {
            subjects.add(result.getOwnerSubject());
        }

        return subjects;
    }

    /**
     * finds the different Groupings that the user is in and allowed to view
     *
     * @param username : String containing the username to be searched for
     * @return information about all of the Groupings the user is in
     */
    @RequestMapping("/groupingsIn")
    public ArrayList<String> groupingsIn(@RequestParam String username) {
        //the time it takes to look up a student is about 3 minutes
        //the time it takes to look up a staff member is less than 3 seconds
        //so until this gets resolved, it would be easier to query for a staff member while testing
        WsStemLookup stem = new WsStemLookup();
        stem.setStemName("hawaii.edu:custom");

        WsGetGroupsResults wsGetGroupsResults = new GcGetGroups()
                .addSubjectIdentifier(username)
                .assignWsStemLookup(stem)
                .assignStemScope(StemScope.ALL_IN_SUBTREE)
                .execute();

        ArrayList<String> groups = new ArrayList<>();
        WsGroup[] groupResults = wsGetGroupsResults.getResults()[0].getWsGroups();
        String uuid = UUID;
        String assignType = "group";
        String subjectAttributeName = "uh-settings:attributes:for-groups:uh-grouping:is-trio";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                gs.attributeAssignments(assignType, subjectAttributeName, uuid);

        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (WsGroup aTrio : trioArray) {
            trios.add(aTrio.getName());
        }

        for (int i = 0; i < wsGetGroupsResults.getResults()[0].getWsGroups().length; i++) {

            if (trios.contains(groupResults[i].getName())) {
                groups.add(groupResults[i].getName());
            }
        }
        return groups;
    }

    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsOwned")
    public ArrayList<String> groupingsOwned(@RequestParam String username) {
        String privilegeName = "update";
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                gs.grouperPrivilegesLite(username, privilegeName);
        ArrayList<String> groups = new ArrayList<>();
        String uuid = UUID;
        String assignType = "group";
        String subjectAttributeName = "uh-settings:attributes:for-groups:uh-grouping:is-trio";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                gs.attributeAssignments(assignType, subjectAttributeName, uuid);
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (WsGroup aTrioArray : trioArray) {
            trios.add(aTrioArray.getName());
        }

        try {
            for (int i = 0; i < wsGetGrouperPrivilegesLiteResult.getPrivilegeResults().length; i++) {
                String temp = wsGetGrouperPrivilegesLiteResult.getPrivilegeResults()[i].getWsGroup().getName();

                if (temp.endsWith(":include")) {
                    temp = temp.split(":include")[0];
                } else if (temp.endsWith(":exclude")) {
                    temp = temp.split(":exclude")[0];
                } else if (temp.endsWith(":basis")) {
                    temp = temp.split(":basis")[0];
                } else if (temp.endsWith(":basis+include")) {
                    temp = temp.split(":basis\\+include")[0];
                }

                if ((!groups.contains(temp)) && (trios.contains(temp))) {
                    groups.add(temp);
                }
            }
        } catch (NullPointerException npe) {
            return null;
        }
        return groups;
    }

    /**
     * if the user is allowed to opt into the grouping, this will add them to the include group of that grouping
     *
     * @param username : the username of user opting in
     * @param grouping : the path to the grouping where the user will be opting in
     * @return information about the success of opting in
     */
    @RequestMapping("/optIn")
    public Object[] optIn(@RequestParam String username, @RequestParam String grouping) {
        Object[] results = new Object[6];

        if (gs.groupOptInPermission(username, grouping + ":include")
                && (!gs.inGroup(grouping + ":exclude", username)
                        || gs.groupOptOutPermission(username, grouping + ":exclude"))) {

            results[3] = gs.removeSelfOpted(grouping + ":exclude", username);
            results[0] = gs.deleteMember(grouping + ":exclude", username);
            results[1] = gs.addMember(grouping + ":include", username);
            results[4] = gs.updateLastModified(grouping + ":exclude");
            results[5] = gs.updateLastModified(grouping + ":include");
            results[2] = gs.addSelfOpted(grouping + ":include", username);

            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt into this Grouping");
        }
    }

    /**
     * if the user is allowed to opt out of the grouping, this will add them to the exclude group of that grouping
     *
     * @param username : the username of user opting out
     * @param grouping : the path to the grouping where the user will be opting out
     * @return information about the success of opting out
     */
    @RequestMapping("/optOut")
    public Object[] optOut(@RequestParam String username, @RequestParam String grouping) {
        Object[] results = new Object[6];

        if (gs.groupOptInPermission(username, grouping + ":exclude") && (!gs.inGroup(grouping + ":include", username)
                || gs.groupOptOutPermission(username, grouping + ":include"))) {

            results[3] = gs.removeSelfOpted(grouping + ":include", username);
            results[0] = gs.deleteMember(grouping + ":include", username);
            results[1] = gs.addMember(grouping + ":exclude", username);
            results[4] = gs.updateLastModified(grouping + ":exclude");
            results[5] = gs.updateLastModified(grouping + ":include");
            results[2] = gs.addSelfOpted(grouping + ":exclude", username);

            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt out of this Grouping");
        }
    }

    @RequestMapping("/cancelOptIn")
    public Object[] cancelOptIn(@RequestParam String grouping, @RequestParam String username) {
        Object[] results = new Object[3];
        String group = grouping + ":include";

        if (gs.inGroup(group, username)) {

            WsSubjectLookup lookup = gs.makeWsSubjectLookup(username);
            WsGetMembershipsResults wsGetMembershipsResults = gs.membershipsResults(lookup, group);
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            if (gs.checkSelfOpted(group, lookup)) {

                String privilegeName = "optout";
                WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                        gs.grouperPrivilegesLite(username, group, privilegeName);

                if (wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED")) {
                    String operation = "remove_attr";
                    String uuid = GroupingsService.UUID;
                    results[1] = gs.assignAttributesResults(operation, uuid, membershipID);
                    results[0] = gs.deleteMember(group, username);

                    results[2] = gs.updateLastModified(group);

                    return results;
                } else {
                    throw new AccessDeniedException("user is not allowed to opt out of 'include' group");
                }

            }
        } else {
            results[0] = "user is not opted in, because user is not in 'include' group";
        }
        return results;
    }

    @RequestMapping("/cancelOptOut")
    public Object[] cancelOptOut(@RequestParam String grouping, @RequestParam String username) {
        Object[] results = new Object[3];
        String group = grouping + ":exclude";

        if (gs.inGroup(group, username)) {

            WsSubjectLookup lookup = gs.makeWsSubjectLookup(username);
            WsGetMembershipsResults wsGetMembershipsResults = gs.membershipsResults(lookup, group);
            String membershipID = wsGetMembershipsResults.getWsMemberships()[0].getMembershipId();

            if (gs.checkSelfOpted(group, lookup)) {

                String privilegeName = "optout";
                WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                        gs.grouperPrivilegesLite(lookup.getSubjectIdentifier(), group, privilegeName);

                if (wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED")) {
                    String operation = "remove_attr";
                    String uuid = GroupingsService.UUID;
                    results[1] = gs.assignAttributesResults(operation, uuid, membershipID);
                    results[0] = gs.deleteMember(group, username);

                    results[2] = gs.updateLastModified(group);

                    return results;
                } else {
                    throw new AccessDeniedException("user is not allowed to opt out of 'exclude' group");
                }

            }
        } else {
            results[0] = "user is not opted in, because user is not in 'exclude' group";
        }
        return results;
    }

    /**
     * checks if the user is allowed to opt out of the grouping
     *
     * @param username: username of the user whos permission is being assessed
     * @param grouping: grouping that is being checked
     * @return True if the user is allowed to opt out, False if the user is not allowed to opt out
     */
    @RequestMapping("/optOutPermission")
    public boolean optOutPermission(@RequestParam String username, @RequestParam String grouping) {
        //a user can opt out of a Grouping if:
        //      they have permission to opt into the exclude group,
        //      and
        //          they are not in the include group
        //          or
        //          they have permission to opt out of the include group

        return (gs.groupOptInPermission(username, grouping + ":exclude") && (!gs.inGroup(grouping + ":include", username)
                || gs.groupOptOutPermission(username, grouping + ":include")));
    }

    /**
     * checks if the user is allowed to opt in of the grouping
     *
     * @param username : username of the user whos permission is being assessed
     * @param grouping : grouping that is being checked
     * @return True if the user is allowed to opt out, False if the user is not allowed to in out
     */
    @RequestMapping("/optInPermission")
    public boolean optInPermission(@RequestParam String username, @RequestParam String grouping) {
        //a user can opt into a Grouping if:
        //      they have permission to opt into the include group,
        //      and
        //          they are not in the exclude group
        //          or
        //          they have permission to opt out of the exclude group

        return (gs.groupOptInPermission(username, grouping + ":include") && (!gs.inGroup(grouping + ":exclude", username)
                || gs.groupOptOutPermission(username, grouping + ":exclude")));
    }

    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsToOptOutOf")
    public ArrayList<String> groupingsToOptOutOf(@RequestParam String username) {
        String privilegeName = "optin";
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                gs.grouperPrivilegesLite(username, privilegeName);
        ArrayList<String> groups = new ArrayList<>();
        String uuid = UUID;
        String assignType = "group";
        String subjectAttributeName = "uh-settings:attributes:for-groups:uh-grouping:is-trio";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                gs.attributeAssignments(assignType, subjectAttributeName, uuid);
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (WsGroup aTrioArray : trioArray) {
            trios.add(aTrioArray.getName());
        }

        for (int i = 0; i < wsGetGrouperPrivilegesLiteResult.getPrivilegeResults().length; i++) {
            String temp = wsGetGrouperPrivilegesLiteResult.getPrivilegeResults()[i].getWsGroup().getName();

            if (temp.endsWith(":exclude")) {
                temp = temp.split(":exclude")[0];
                if (trios.contains(temp)) {
                    groups.add(temp);
                }
            }
        }

        return groups;

    }

    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsToOptInto")
    public ArrayList<String> groupingsToOptInto(@RequestParam String username) {
        String privilegeName = "optin";
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult =
                gs.grouperPrivilegesLite(username, privilegeName);

        ArrayList<String> groups = new ArrayList<>();
        String uuid = UUID;
        String assignType = "group";
        String subjectAttributeName = "uh-settings:attributes:for-groups:uh-grouping:is-trio";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                gs.attributeAssignments(assignType, subjectAttributeName, uuid);
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (WsGroup aTrioArray : trioArray) {
            trios.add(aTrioArray.getName());
        }

        for (int i = 0; i < wsGetGrouperPrivilegesLiteResult.getPrivilegeResults().length; i++) {
            String temp = wsGetGrouperPrivilegesLiteResult.getPrivilegeResults()[i].getWsGroup().getName();

            if (temp.endsWith(":include")) {
                temp = temp.split(":include")[0];
                if (trios.contains(temp)) {
                    groups.add(temp);
                }
            }
        }

        return groups;
    }

    @RequestMapping("/hasListServe")
    public boolean hasListServe(@RequestParam String grouping) throws NullPointerException {

        String assignType = "group";
        String nameName = "uh-settings:attributes:for-groups:uh-grouping:destinations:listserv";

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults =
                gs.attributeAssignmentsResults(assignType, grouping, nameName);

        WsAttributeAssign listServeAttriubte = wsGetAttributeAssignmentsResults.getWsAttributeAssigns()[0];
        return listServeAttriubte.getAttributeDefNameName().equals(nameName);
    }

    //TODO
    // give the Grouping owner the ability to change the optin/optout attribute for their Grouping
    // Edit the text provided to the Grouping's members when they are electing to opt in/out of the Inclusion/exclusion group
    // decide on exception handling policy

}

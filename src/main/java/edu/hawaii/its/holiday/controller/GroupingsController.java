package edu.hawaii.its.holiday.controller;

import edu.internet2.middleware.grouperClient.api.*;
import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

/**
 * Created by zknoebel on 12/12/16.
 */

@RestController
public class GroupingsController {

    public GroupingsController() {
    }

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
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        WsDeleteMemberResults wsDeleteMemberResults = new GcDeleteMember().assignActAsSubject(wsSubjectLookup).assignGroupName(grouping + ":exclude").addSubjectIdentifier(userToAdd).execute();
        WsAddMemberResults wsAddMemberResults = new GcAddMember().assignActAsSubject(wsSubjectLookup).assignGroupName(grouping + ":include").addSubjectIdentifier(userToAdd).execute();

        Object[] results = new Object[2];
        results[0] = wsAddMemberResults;
        results[1] = wsDeleteMemberResults;

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
    public WsAssignGrouperPrivilegesResults[] assignOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String newOwner) {

        WsAssignGrouperPrivilegesResults[] wsAssignGrouperPrivilegesResultsArray = new WsAssignGrouperPrivilegesResults[3];

        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(newOwner);

        WsSubjectLookup wsSubjectLookup1 = new WsSubjectLookup();
        wsSubjectLookup1.setSubjectIdentifier(username);

        WsGroupLookup wsGroupLookup0 = new WsGroupLookup();
        wsGroupLookup0.setGroupName(grouping + ":include");

        WsGroupLookup wsGroupLookup1 = new WsGroupLookup();
        wsGroupLookup1.setGroupName(grouping + ":basis+include");

        WsGroupLookup wsGroupLookup2 = new WsGroupLookup();
        wsGroupLookup2.setGroupName(grouping + ":exclude");

        wsAssignGrouperPrivilegesResultsArray[0] = new GcAssignGrouperPrivileges().assignActAsSubject(wsSubjectLookup1).assignGroupLookup(wsGroupLookup1).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("view").addPrivilegeName("read").assignAllowed(true).execute();

        wsAssignGrouperPrivilegesResultsArray[1] = new GcAssignGrouperPrivileges().assignActAsSubject(wsSubjectLookup1).assignGroupLookup(wsGroupLookup2).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("view").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(true).execute();

        wsAssignGrouperPrivilegesResultsArray[2] = new GcAssignGrouperPrivileges().assignActAsSubject(wsSubjectLookup1).assignGroupLookup(wsGroupLookup0).addSubjectLookup(wsSubjectLookup)
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
    public Object[] deleteMember(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToDelete) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        WsAddMemberResults wsAddMemberResults = new GcAddMember().assignActAsSubject(wsSubjectLookup).assignGroupName(grouping + ":exclude").addSubjectIdentifier(userToDelete).execute();
        WsDeleteMemberResults wsDeleteMemberResults = new GcDeleteMember().assignActAsSubject(wsSubjectLookup).assignGroupName(grouping + ":include").addSubjectIdentifier(userToDelete).execute();

        Object[] results = new Object[2];
        results[0] = wsDeleteMemberResults;
        results[1] = wsAddMemberResults;

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
        WsGroupLookup wsGroupLookup = new WsGroupLookup();
        wsGroupLookup.setGroupName(grouping + ":include");
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(ownerToRemove);
        WsSubjectLookup wsSubjectLookup1 = new WsSubjectLookup();
        wsSubjectLookup1.setSubjectIdentifier(username);

        WsGroupLookup wsGroupLookup1 = new WsGroupLookup();

        wsGroupLookup1.setGroupName(grouping + ":basis+include");

        WsGroupLookup wsGroupLookup2 = new WsGroupLookup();
        wsGroupLookup2.setGroupName(grouping + ":exclude");

        WsAssignGrouperPrivilegesResults[] wsAssignGrouperPrivilegesResultsArray = new WsAssignGrouperPrivilegesResults[3];
        wsAssignGrouperPrivilegesResultsArray[0] = new GcAssignGrouperPrivileges().assignActAsSubject(wsSubjectLookup1).assignGroupLookup(wsGroupLookup1).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false).execute();

        wsAssignGrouperPrivilegesResultsArray[1] = new GcAssignGrouperPrivileges().assignActAsSubject(wsSubjectLookup1).assignGroupLookup(wsGroupLookup2).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false).execute();

        wsAssignGrouperPrivilegesResultsArray[2] = new GcAssignGrouperPrivileges().assignActAsSubject(wsSubjectLookup1).assignGroupLookup(wsGroupLookup).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false).execute();

        return wsAssignGrouperPrivilegesResultsArray;
        //change to api-account for now
        //switch to actAsSubject after we figure out attribute update privlages
    }

    /**
     * finds all the members of a group
     *
     * @param username: username of the subject preforming the action
     * @param grouping  : String containing the path of the Grouping to be searched
     * @return information for all of the members
     */
    @RequestMapping("/getMembers")
    public WsSubject[] getMembers(@RequestParam String grouping, @RequestParam String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetMembersResults wsGetMembersResults = new GcGetMembers().assignActAsSubject(wsSubjectLookup).addSubjectAttributeName("uid").addGroupName(grouping).assignIncludeSubjectDetail(true).execute();

        return wsGetMembersResults.getResults()[0].getWsSubjects();
    }

    /**
     * finds all of the owners of a group
     *
     * @param username: username of the subject preforming the action
     * @return information for all of the owners
     */
    @RequestMapping("/getOwners")
    public ArrayList<WsSubject> getOwners(@RequestParam String grouping, @RequestParam String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignActAsSubject(wsSubjectLookup).assignGroupName(grouping + ":include").assignPrivilegeName("update").execute();
        ArrayList<WsSubject> subjects = new ArrayList<>();
        for (int i = 0; i < wsGetGrouperPrivilegesLiteResult.getPrivilegeResults().length; i++) {
            subjects.add(wsGetGrouperPrivilegesLiteResult.getPrivilegeResults()[i].getOwnerSubject());
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
        WsStemLookup wsStemLookup = new WsStemLookup();
        wsStemLookup.setStemName("hawaii.edu:custom");

        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGroupsResults wsGetGroupsResults = new GcGetGroups().assignActAsSubject(wsSubjectLookup).addSubjectIdentifier(username).assignWsStemLookup(wsStemLookup).assignStemScope(StemScope.ALL_IN_SUBTREE).execute();

        ArrayList<String> groups = new ArrayList<>();
        WsGroup[] groupResults = wsGetGroupsResults.getResults()[0].getWsGroups();

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().addAttributeDefUuid("1d7365a23c994f5f83f7b541d4a5fa5e").addSubjectAttributeName("uh-settings:attributes:for-groups:uh-grouping:is-trio").assignAttributeAssignType("group").execute();
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (int i = 0; i < trioArray.length; i++) {
            trios.add(trioArray[i].getName());
        }

        for (int i = 0; i < wsGetGroupsResults.getResults()[0].getWsGroups().length; i++) {
            String temp = groupResults[i].getName();

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
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignSubjectLookup(wsSubjectLookup).assignPrivilegeName("update").execute();
        ArrayList<String> groups = new ArrayList<>();

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().addAttributeDefUuid("1d7365a23c994f5f83f7b541d4a5fa5e").addSubjectAttributeName("uh-settings:attributes:for-groups:uh-grouping:is-trio").assignAttributeAssignType("group").execute();
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (int i = 0; i < trioArray.length; i++) {
            trios.add(trioArray[i].getName());
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
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult;
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":include").assignPrivilegeName("optin").assignSubjectLookup(wsSubjectLookup).execute();

        if (wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED")) {
            Object[] results = new Object[2];
            results[0] = new GcDeleteMember().assignGroupName(grouping + ":exclude").addSubjectIdentifier(username).execute();
            results[1] = new GcAddMember().assignGroupName(grouping + ":include").addSubjectIdentifier(username).execute();
            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt into this group");
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
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult;
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":exclude").assignPrivilegeName("optin").assignSubjectLookup(wsSubjectLookup).execute();

        if (wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED")) {
            Object[] results = new Object[2];
            results[0] = new GcAddMember().assignGroupName(grouping + ":exclude").addSubjectIdentifier(username).execute();
            results[1] = new GcDeleteMember().assignGroupName(grouping + ":include").addSubjectIdentifier(username).execute();
            return results;
        } else {
            throw new AccessDeniedException("user is not allowed to opt out of this group");
        }
    }

    /**
     * checks if the user is allowed to opt out of the grouping
     *
     * @param username: username of the user whos permission is being assessed
     * @param grouping: grouping that is being checked
     * @return True if the user is allowed to opt out, False if the user is not allowed to opt out
     */
    @RequestMapping("/optOutPermission")
    public boolean optOutPermission(@RequestParam String username, String grouping) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":exclude").assignPrivilegeName("optin").assignSubjectLookup(wsSubjectLookup).execute();
        return wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED");
    }

    /**
     * checks if the user is allowed to opt in of the grouping
     *
     * @param username : username of the user whos permission is being assessed
     * @param grouping : grouping that is being checked
     * @return True if the user is allowed to opt out, False if the user is not allowed to in out
     */
    @RequestMapping("/optInPermission")
    public boolean optInPermission(@RequestParam String username, String grouping) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":include").assignPrivilegeName("optin").assignSubjectLookup(wsSubjectLookup).execute();
        return wsGetGrouperPrivilegesLiteResult.getResultMetadata().getResultCode().equals("SUCCESS_ALLOWED");
    }

    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsToOptOutOf")
    public ArrayList<String> groupingsToOptOutOf(@RequestParam String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignSubjectLookup(wsSubjectLookup).assignPrivilegeName("optin").execute();
        ArrayList<String> groups = new ArrayList<>();

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().addAttributeDefUuid("1d7365a23c994f5f83f7b541d4a5fa5e").addSubjectAttributeName("uh-settings:attributes:for-groups:uh-grouping:is-trio").assignAttributeAssignType("group").execute();
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (int i = 0; i < trioArray.length; i++) {
            trios.add(trioArray[i].getName());
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
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);
        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new GcGetGrouperPrivilegesLite().assignSubjectLookup(wsSubjectLookup).assignPrivilegeName("optin").execute();
        ArrayList<String> groups = new ArrayList<>();

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().addAttributeDefUuid("1d7365a23c994f5f83f7b541d4a5fa5e").addSubjectAttributeName("uh-settings:attributes:for-groups:uh-grouping:is-trio").assignAttributeAssignType("group").execute();
        WsGroup[] trioArray = wsGetAttributeAssignmentsResults.getWsGroups();
        ArrayList<String> trios = new ArrayList<>();

        for (int i = 0; i < trioArray.length; i++) {
            trios.add(trioArray[i].getName());
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
    public boolean hasListServe(@RequestParam String username, @RequestParam String grouping) throws NullPointerException {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new GcGetAttributeAssignments().assignAttributeAssignType("group").addOwnerGroupName(grouping).addAttributeDefNameName("uh-settings:attributes:for-groups:uh-grouping:destinations:listserv").execute();
        WsAttributeAssign listServeAttriubte = wsGetAttributeAssignmentsResults.getWsAttributeAssigns()[0];
        if (listServeAttriubte.getAttributeDefNameName().equals("uh-settings:attributes:for-groups:uh-grouping:destinations:listserv")) {
            return true;
        }
        return false;
    }


    //TODO more methods to add
    //Edit the text provided to the Grouping's members when they are electing to opt in/out of the Inclusion/exclusion group

}

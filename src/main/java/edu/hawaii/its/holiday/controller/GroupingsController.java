package edu.hawaii.its.holiday.controller;

import edu.internet2.middleware.grouperClient.api.*;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import javafx.scene.control.Hyperlink;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URL;

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
     *
     * @param grouping:    String containing the path of the parent Grouping
     * @param newGrouping: String containing the name of the Grouping to be created
     * @return information about the new Grouping and its success
     */
    @RequestMapping("/addGrouping")
//    public WsGroupSaveResults addGrouping(@RequestParam String grouping, @RequestParam String newGrouping) {
    public RedirectView addGrouping(){
        //return new GcGroupSave().addGroupToSave(grouping + ":" + newGrouping).execute();
        //TODO
        //currently this method is not to be implemented because responsibility to create a new
        //grouping is still going to go through the UH Grouper staff, so the individual should be sent to this address
        //https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form");
        return redirectView;
    }

    /**
     * adds a member to a Grouping that the user owns
     *
     * @param grouping:  String containing the path of the Grouping
     * @param userToAdd: username of the member to be added
     * @return information about the new member and its success
     */
    @RequestMapping("/addMember")
    public WsAddMemberResults addMember(@RequestParam String grouping, @RequestParam String userToAdd) {
        new GcDeleteMember().assignGroupName(grouping + ":exclude").addSubjectIdentifier(userToAdd).execute();
        return new GcAddMember().assignGroupName(grouping + ":include").addSubjectIdentifier(userToAdd).execute();
        //TODO currently the api account is being used for authentication, eventually this will change so that the
        //user creating the request will be verified first and only be allowed to change groupings that they have
        //ownership over
    }

    /**
     * gives the user read, update and view privileges for the Grouping
     *
     * @param newOwner: String containing the username of the new owner
     * @return information about the new owner and its success
     */
    @RequestMapping("/assignOwnership")
    public WsAssignGrouperPrivilegesResults assignOwnership(@RequestParam String grouping,
                                                            @RequestParam String newOwner) {
        WsGroupLookup wsGroupLookup = new WsGroupLookup();
        wsGroupLookup.setGroupName(grouping + ":include");
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(newOwner);

        WsGroupLookup wsGroupLookup1 = new WsGroupLookup();
        wsGroupLookup1.setGroupName(grouping + ":basis+include");

        WsGroupLookup wsGroupLookup2 = new WsGroupLookup();
        wsGroupLookup2.setGroupName(grouping + ":exclude");

        new GcAssignGrouperPrivileges().assignGroupLookup(wsGroupLookup1).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("view").addPrivilegeName("read").assignAllowed(true).execute();

        new GcAssignGrouperPrivileges().assignGroupLookup(wsGroupLookup2).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("view").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(true).execute();

        return new GcAssignGrouperPrivileges().assignGroupLookup(wsGroupLookup).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("view").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(true).execute();

        //TODO figure out how to return all three as a batch
    }

    /**
     * removes a Grouping
     *
     * @return information about the deleted Grouping and its success
     */
    @RequestMapping("/deleteGrouping")
//    public WsGroupDeleteResults deleteGrouping(@RequestParam String grouping) {
    public RedirectView deleteGrouping(){
//        WsGroupLookup wsGroupLookup = new WsGroupLookup();
//        wsGroupLookup.setGroupName(grouping);
//        new GcGroupDelete().addGroupLookup(wsGroupLookup).execute();
        //not currently implemented
        //instead email its-iam-help@hawaii.edu for help
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form");
        return redirectView;
    }

    /**
     * removes a member from a Grouping that the user is an owner of
     *
     * @param grouping:     String containing the path of the Grouping
     * @param userToDelete: String containing the username of the user to be removed from the Grouping
     * @return information about the deleted member and its success
     */
    @RequestMapping("/deleteMember")
    public WsDeleteMemberResults deleteMember(@RequestParam String grouping, @RequestParam String userToDelete) {
        new GcAddMember().assignGroupName(grouping + ":exclude").addSubjectIdentifier(userToDelete).execute();
        return new GcDeleteMember().assignGroupName(grouping + ":include").addSubjectIdentifier(userToDelete).execute();
        //TODO currently the api account is being used for authentication, eventually this will change so that the
        //user creating the request will be verified first and only be allowed to change groupings that they have
        //ownership over
    }

    /**
     * removes ownership privileges from the user specified
     *
     * @param ownerToRemove: String containing the name of the user who's privileges will be removed
     * @param grouping: String containing the path of the Grouping
     * @return information about the member who's ownership privileges have been removed and its success
     */
    @RequestMapping("/removeOwnership")
    public WsAssignGrouperPrivilegesResults removeOwnership(@RequestParam String grouping,
                                                            @RequestParam String ownerToRemove) {
        WsGroupLookup wsGroupLookup = new WsGroupLookup();
        wsGroupLookup.setGroupName(grouping + ":include");
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(ownerToRemove);

        WsGroupLookup wsGroupLookup1 = new WsGroupLookup();
        wsGroupLookup1.setGroupName(grouping + ":basis+include");

        WsGroupLookup wsGroupLookup2 = new WsGroupLookup();
        wsGroupLookup2.setGroupName(grouping + ":exclude");


        new GcAssignGrouperPrivileges().assignGroupLookup(wsGroupLookup1).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false).execute();

        new GcAssignGrouperPrivileges().assignGroupLookup(wsGroupLookup2).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false).execute();

        return new GcAssignGrouperPrivileges().assignGroupLookup(wsGroupLookup).addSubjectLookup(wsSubjectLookup)
                .addPrivilegeName("admin").addPrivilegeName("update").addPrivilegeName("read").assignAllowed(false).execute();

        //TODO figure out how to return all three as a batch
    }

    /**
     * finds all the members of a group
     *
     * @param grouping: String containing the path of the Grouping to be searched
     * @return information for all of the members
     */
    @RequestMapping("/getMembers")
    public WsGetMembersResults getMembers(@RequestParam String grouping) {
        return new GcGetMembers().addGroupName(grouping + ":basis+include").assignIncludeSubjectDetail(true).execute();
    }

    /**
     * finds all of the owners of a group
     *
     * @return information for all of the owners
     */
    @RequestMapping("/getOwners")
    public WsGetGrouperPrivilegesLiteResult getOwners(@RequestParam String grouping) {
        return new GcGetGrouperPrivilegesLite().assignGroupName(grouping + ":include").assignPrivilegeName("update").execute();
        //TODO
    }

    /**
     * finds the different Groupings that the user is in and allowed to view
     *
     * @param username: String containing the username to be searched for
     * @return information about all of the Groupings the user is in
     */
    @RequestMapping("/groupingsIn")
    public WsGetGroupsResults groupingsIn(@RequestParam String username) {
        return new GcGetGroups().addSubjectIdentifier(username).execute();
    }

    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @return information about all of the Groupings that the user owns
     */
//    @RequestMapping("/groupingsOwned")
//    public WsFindGroupsResults groupingsOwned(@RequestParam String username) {
//
//       new GcGetGrouperPrivilegesLite().assignPrivilegeName("update").assignStemName("hawaii.edu").assignSubjectLookup()
//        WsQueryFilter wsQueryFilter = new WsQueryFilter();
//        wsQueryFilter.setStemName("hawaii.edu");
//        return new GcFindGroups().assignQueryFilter(wsQueryFilter).execute();
//
////        Todo
//    }

}

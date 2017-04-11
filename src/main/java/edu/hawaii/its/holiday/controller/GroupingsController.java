package edu.hawaii.its.holiday.controller;

import edu.hawaii.its.holiday.api.GroupingsService;
import edu.hawaii.its.holiday.api.type.Grouping;
import edu.hawaii.its.holiday.api.type.MyGroupings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Created by zknoebel on 12/12/16.
 * <p>
 * file containing the mappings for all Groupings methods
 */

@RestController
public class GroupingsController {

    private static final Logger logger = LoggerFactory.getLogger(GroupingsController.class);

    @Autowired
    private GroupingsService gs;


    @RequestMapping("/addMemberToIncludeGroup")
    public Object addMemberToIncludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToAdd){
        return gs.addMemberAs(username, grouping + ":include", userToAdd);
    }


    @RequestMapping("/addMemberToExcludeGroup")
    public Object addMemberToExcludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToAdd){
        return gs.addMemberAs(username, grouping + ":exclude", userToAdd);
    }


    @RequestMapping("/deleteMemberFromIncludeGroup")
    public Object deleteMemberFromIncludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToDelete){
        return gs.deleteMemberAs(username, grouping + ":include", userToDelete);
    }


    @RequestMapping("/deleteMemberFromExcludeGroup")
    public Object deleteMemberFromExcludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToDelete){
        return gs.deleteMemberAs(username, grouping + ":exclude", userToDelete);
    }



    /**
     * gives the user read, update and view privileges for the Grouping
     *      the user should already have view privilege, but the view privilege is added just in case
     *      read privilege allows the user to see the members and owners of a Grouping
     *      update privilege allows the user to add/delete the members and owners of a Grouping
     *
     * @param username: username of user preforming action
     * @param grouping: path to the grouping that the newOwner will get privileges for
     * @param newOwner: String containing the username of the Person to become the new owner
     * @return information about the privileges being added to new owner and the success of these privilege assignments
     */
    @RequestMapping("/assignOwnership")
    public Object[] assignOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String newOwner) {
        return gs.assignOwnership(grouping, username, newOwner);
    }


    /**
     * removes read, and update privileges from the user for the designated Grouping
     *      read privilege allows the user to see the members and owners of a Grouping
     *      update privilege allows the user to add/delete the members and owners of a Grouping
     *      the user should keep the view privilege
     *
     * @param username: username of user preforming action
     * @param grouping: path to the grouping that the owner to be removed will get privileges revoked from
     * @param ownerToRemove: String containing the username of the Person whos owner privileges are to be revoked
     * @return information about the privileges being removed from the owner and the success of these privilege assignments
     */
    @RequestMapping("/removeOwnership")
    public Object[] removeOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String ownerToRemove) {
        return gs.removeOwnership(grouping, username, ownerToRemove);
    }


    /**
     * finds and returns the specified Grouping
     *
     * @param grouping : String containing the path of the Grouping to be searched for
     * @param username : username of the user preforming the action
     * @return the Grouping that was searched for
     *      the Grouping will contain information about
     *          members of each Group in the grouping
     *          owners of the Grouping
     *          name of the Grouping
     *          path of the Grouping
     *          whether or not the Grouping has a list serve associated with it
     */
    @RequestMapping("/Grouping")
    public Grouping getGrouping(@RequestParam String grouping, @RequestParam String username) {
        return gs.getGrouping(grouping, username);
    }


    /**
     *
     * @param username: username of user to get lists of Groupings for
     * @return a MyGrouping Object that contains
     *      Groupings that the user is in
     *      Groupings that the user owns
     *      Groupings that the user can opt into
     *      Groupings that the user can opt out of
     */
    @RequestMapping("/myGroupings")
    public MyGroupings myGroupings(@RequestParam String username){
        return gs.getMyGroupings(username);
    }

    /**
     * if the user is allowed to opt into the grouping
     *      this will add them to the include group of that grouping
     *      if the user is in the exclude group, they will be removed from it
     *
     * @param username : the username of user opting in
     * @param grouping : the path to the grouping where the user will be opting in
     * @return information about the success of opting in
     */
    @RequestMapping("/optIn")
    public Object[] optIn(@RequestParam String username, @RequestParam String grouping) {
        return gs.optIn(username, grouping);
    }


    /**
     * if the user is allowed to opt out of the grouping
     *      this will add them to the exclude group of that grouping
     *      if the user is in the include group of that Grouping, they will be removed from it
     *
     * @param username : the username of user opting out
     * @param grouping : the path to the grouping where the user will be opting out
     * @return information about the success of opting out
     */
    @RequestMapping("/optOut")
    public Object[] optOut(@RequestParam String username, @RequestParam String grouping) {
        return gs.optOut(username, grouping);
    }


    /**
     * if the user has previously opted in
     *      this will cancel the effects of opting in
     *          the user will be removed from the include Group
     *          the user will not be added to the exclude Group
     *          if the user is also in the basis Group, this will not effectively change the user's membership to that Grouping
     *
     * @param username : the username of user canceling opting in
     * @param grouping : the path to the grouping where the user will be canceling opting in
     * @return information about the success of canceling the opt in
     */
    @RequestMapping("/cancelOptIn")
    public Object[] cancelOptIn(@RequestParam String grouping, @RequestParam String username) {
        return gs.cancelOptIn(grouping, username);
    }


    /**
     *
     * if the user has previously opted out
     *      this will cancel the effects of opting out
     *          the user will be removed from the exclude Group
     *          the user will not be added to the include Group
     *          if the user is not in the basis Group, this will not effectively change the user's membership to that Grouping
     *
     * @param username : the username of user canceling opting out
     * @param grouping : the path to the grouping where the user will be canceling opting out
     * @return information about the success of canceling the opt out
     */
    @RequestMapping("/cancelOptOut")
    public Object[] cancelOptOut(@RequestParam String grouping, @RequestParam String username) {
        return gs.cancelOptOut(grouping, username);
    }







    /**
     * eventually this is intended to give the user the ability to add a Grouping in one of the Groupings that they own,
     * for now it will bring the user to the web page where they can submit a request to the UHGrouper staff
     * //@param grouping:    String containing the path of the parent Grouping
     * //@param newGrouping: String containing the name of the Grouping to be created
     *
     * @return information about the new Grouping and its success
     */
    //TODO currently this method is not to be implemented because responsibility to create a new
    //TODO grouping is still going to go through the UH Grouper staff, so the individual should be sent to this address
    //TODO https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form
    @RequestMapping("/addGrouping")
    public RedirectView addGrouping() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form");
        return redirectView;
    }


    /**
     * removes a Grouping
     *
     * @return information about the deleted Grouping and its success
     */
    //TODO currently this method is not to be implemented because responsibility to create a new
    //TODO grouping is still going to go through the UH Grouper staff, so the individual should be sent to this address
    //TODO https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form
    // email its-iam-help@hawaii.edu for help in deleting a Grouping
    @RequestMapping("/deleteGrouping")
    public RedirectView deleteGrouping() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://www.hawaii.edu/bwiki/display/UHIAM/UH+Groupings+Request+Form");
        return redirectView;
    }

    //TODO give the Grouping owner the ability to change the optin/optout attribute for their Grouping
    //      TODO possibly give the owner the ability to change the optin/optout attribute for individual members of the Grouping
    //TODO Edit the text provided to the Grouping's Persons when they are electing to opt in/out of the Inclusion/exclusion group
}

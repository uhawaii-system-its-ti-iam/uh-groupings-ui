package edu.hawaii.its.holiday.controller;

import edu.hawaii.its.holiday.api.GroupingsService;
import edu.hawaii.its.holiday.api.type.Group;
import edu.hawaii.its.holiday.api.type.Grouping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * Created by zknoebel on 12/12/16.
 * <p>
 * file containing the mappings for all groupings methods
 */

@RestController
public class GroupingsController {

    private static final Logger logger = LoggerFactory.getLogger(GroupingsController.class);

    @Autowired
    private GroupingsService gs;


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
        return gs.addMember(grouping, username, userToAdd);
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
        return gs.deleteMember(grouping, username, userToDelete);
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
    public Object[] assignOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String newOwner) {
        return gs.assignOwnership(grouping, username, newOwner);
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
    public Object[] removeOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String ownerToRemove) {
        return gs.removeOwnership(grouping, username, ownerToRemove);
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
        return gs.getMembers(grouping, username);
    }


    /**
     * finds all of the owners of a group
     *
     * @param username: username of the subject preforming the action
     * @return information for all of the owners
     */
    @RequestMapping("/getOwners")
    public Group getOwners(@RequestParam String grouping, @RequestParam String username) {
        return gs.getOwners(grouping, username);
    }


    /**
     * finds the different Groupings that the user is in and allowed to view
     *
     * @param username : String containing the username to be searched for
     * @return ArrayList of the names of the Groupings the user is in
     */
    @RequestMapping("/groupingsIn")
    public List<Grouping> groupingsIn(@RequestParam String username) {
        return gs.groupingsIn(username);
    }


    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsOwned")
    public List<Grouping> groupingsOwned(@RequestParam String username) {
        return gs.groupingsOwned(username);
    }


    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsToOptOutOf")
    public List<Grouping> groupingsToOptOutOf(@RequestParam String username) {
        return gs.groupingsToOptOutOf(username);
    }


    /**
     * finds the different Groupings that the user has owner privileges for
     *
     * @param username : the owner of the groups returned
     * @return information about all of the Groupings that the user owns
     */
    @RequestMapping("/groupingsToOptInto")
    public List<Grouping> groupingsToOptInto(@RequestParam String username) {
        return gs.groupingsToOptInto(username);
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
        return gs.optIn(username, grouping);
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
        return gs.optOut(username, grouping);
    }

    /**
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
     * @param username : the username of user canceling opting out
     * @param grouping : the path to the grouping where the user will be canceling opting out
     * @return information about the success of canceling the opt out
     */
    @RequestMapping("/cancelOptOut")
    public Object[] cancelOptOut(@RequestParam String grouping, @RequestParam String username) {
        return gs.cancelOptOut(grouping, username);
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
        return gs.optOutPermission(username, grouping);
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
        return gs.optInPermission(username, grouping);
    }


    @RequestMapping("/hasListServe")
    public boolean hasListServe(@RequestParam String grouping) throws NullPointerException {
        return gs.hasListServe(grouping);
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
    //TODO Edit the text provided to the Grouping's members when they are electing to opt in/out of the Inclusion/exclusion group
    //TODO decide on exception handling policy
}

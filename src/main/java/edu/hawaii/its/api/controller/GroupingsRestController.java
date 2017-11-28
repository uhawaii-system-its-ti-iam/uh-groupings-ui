package edu.hawaii.its.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.hawaii.its.api.service.GroupingsService;
import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
public class GroupingsRestController {

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    @Value("${app.groupings.controller.uuid}")
    private String uuid;

    @Value("${app.iam.request.form}")
    private String requestForm;

    @Autowired
    private GroupingsService gs;

    @PostConstruct
    public void init() {
        Assert.hasLength(uuid, "Property 'app.groupings.controller.uuid' is required.");
        logger.info("GroupingsRestController started.");
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity
                .ok()
                .body("hello");
    }

    /**
     * adds a member to the admin group
     *
     * @param username:   username of the admin preforming the operation
     * @param adminToAdd: username of the new admin to add
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{username}/{adminToAdd}/addAdmin",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> addAdmin(@PathVariable String username, @PathVariable String adminToAdd) {
        logger.info("Entered REST addAdmin...");
        return ResponseEntity
                .ok()
                .body(gs.addAdmin(username, adminToAdd));
    }

    /**
     * deletes a member from the admin group
     *
     * @param username:      username of the admin preforming the operation
     * @param adminToDelete: username of the admin to be deleted
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{username}/{adminToDelete}/deleteAdmin",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> deleteAdmin(@PathVariable String username, @PathVariable String adminToDelete) {
        logger.info("Entered REST deleteAdmin...");
        return ResponseEntity
                .ok()
                .body(gs.deleteAdmin(username, adminToDelete));
    }


    /**
     * adds a member to the include group of the Grouping who's path is in 'grouping'
     * if that member is in the exclude group, they will be removed from it
     *
     * @param grouping:  path to the Grouping who's include group the new member will be added to
     * @param username:  username of the Grouping owner preforming the operation
     * @param userToAdd: username of the new member to be added to the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{userToAdd}/addMemberToIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> addMemberToIncludeGroup(@PathVariable String grouping, @PathVariable String username, @PathVariable String userToAdd) {
        logger.info("Entered REST addMemberToIncludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.addMemberAs(username, grouping + ":include", userToAdd));
    }

    /**
     * adds a member to the exclude group of the Grouping who's path is in 'grouping'
     * if that member is in the include group, they will be removed from it
     *
     * @param grouping:  path to the Grouping who's exclude group the new member will be added to
     * @param username:  username of the Grouping owner preforming the operation
     * @param userToAdd: username of the new member to be added to the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{userToAdd}/addMemberToExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> addMemberToExcludeGroup(@PathVariable String grouping, @PathVariable String username, @PathVariable String userToAdd) {
        logger.info("Entered REST addMemberToExcludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.addMemberAs(username, grouping + ":exclude", userToAdd));
    }

    /**
     * deletes a member in the include group of the Grouping who's path is in 'grouping'
     *
     * @param grouping:     path to the Grouping who's include group contains the member to be deleted
     * @param username:     username of the Grouping owner preforming the operation
     * @param userToDelete: username of the user to be deleted from the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{userToDelete}/deleteMemberFromIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> deleteMemberFromIncludeGroup(@PathVariable String grouping, @PathVariable String username, @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromIncludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.deleteMemberAs(username, grouping + ":include", userToDelete));
    }

    /**
     * deletes a member in the exclude group of the Grouping who's path is in 'grouping'
     *
     * @param grouping:     path to the Grouping who's exclude group contains the member to be deleted
     * @param username:     username of the Grouping owner preforming the operation
     * @param userToDelete: username of the user to be deleted from the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{userToDelete}/deleteMemberFromExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> deleteMemberFromExcludeGroup(@PathVariable String grouping, @PathVariable String username, @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromExcludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.deleteMemberAs(username, grouping + ":exclude", userToDelete));
    }

    /**
     * gives the user read, update and view privileges for the Grouping
     * the user should already have view privilege, but the view privilege is added just in case
     * read privilege allows the user to see the members and owners of a Grouping
     * update privilege allows the user to add/delete the members and owners of a Grouping
     *
     * @param username: username of user preforming action
     * @param grouping: path to the grouping that the newOwner will get privileges for
     * @param newOwner: String containing the username of the Person to become the new owner
     * @return information about the privileges being added to new owner and the success of these privilege assignments
     */
    @RequestMapping(value = "/{grouping}/{username}/{newOwner}/assignOwnership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> assignOwnership(@PathVariable String grouping, @PathVariable String username, @PathVariable String newOwner) {
        logger.info("Entered REST assignOwnership...");
        return ResponseEntity
                .ok()
                .body(gs.assignOwnership(grouping, username, newOwner));
    }

    /**
     * removes read, and update privileges from the user for the designated Grouping
     * read privilege allows the user to see the members and owners of a Grouping
     * update privilege allows the user to add/delete the members and owners of a Grouping
     * the user should keep the view privilege
     *
     * @param username:      username of user preforming action
     * @param grouping:      path to the grouping that the owner to be removed will get privileges revoked from
     * @param ownerToRemove: String containing the username of the Person whos owner privileges are to be revoked
     * @return information about the privileges being removed from the owner and the success of these privilege assignments
     */
    @RequestMapping(value = "/{grouping}/{username}/{ownerToRemove}/removeOwnership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> removeOwnership(@PathVariable String grouping, @PathVariable String username, @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeOwnership...");
        return ResponseEntity
                .ok()
                .body(gs.removeOwnership(grouping, username, ownerToRemove));
    }

    /**
     * finds and returns the specified Grouping
     *
     * @param grouping : String containing the path of the Grouping to be searched for
     * @param username : username of the user preforming the action
     * @return the Grouping that was searched for
     * the Grouping will contain information about
     * members of each Group in the grouping
     * owners of the Grouping
     * name of the Grouping
     * path of the Grouping
     * whether or not the Grouping has a list serve associated with it
     */
    @RequestMapping(value = "/{grouping}/{username}/grouping",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Grouping> grouping(@PathVariable String grouping, @PathVariable String username) {
        logger.info("Entered REST grouping...");
        return ResponseEntity
                .ok()
                .body(gs.getGrouping(grouping, username));
    }

    /**
     * @param username: username of user to get lists of Groupings for
     * @return a MyGrouping Object that contains
     * Groupings that the user is in
     * Groupings that the user owns
     * Groupings that the user can opt into
     * Groupings that the user can opt out of
     */
    @RequestMapping(value = "/{username}/groupingAssignment",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingAssignment> groupingAssignment(@PathVariable String username) {
        logger.info("Entered REST GroupingAssingment...");
        return ResponseEntity
                .ok()
                .body(gs.getGroupingAssignment(username));
    }

    /**
     * if the user is allowed to opt into the grouping
     * this will add them to the include group of that grouping
     * if the user is in the exclude group, they will be removed from it
     *
     * @param username : the username of user opting in
     * @param grouping : the path to the grouping where the user will be opting in
     * @return information about the success of opting in
     */
    @RequestMapping(value = "/{grouping}/{username}/optIn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> optIn(@PathVariable String grouping, @PathVariable String username) {
        logger.info("Entered REST optIn...");
        return ResponseEntity
                .ok()
                .body(gs.optIn(username, grouping));
    }

    /**
     * if the user is allowed to opt out of the grouping
     * this will add them to the exclude group of that grouping
     * if the user is in the include group of that Grouping, they will be removed from it
     *
     * @param username : the username of user opting out
     * @param grouping : the path to the grouping where the user will be opting out
     * @return information about the success of opting out
     */
    @RequestMapping(value = "/{grouping}/{username}/optOut",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> optOut(@PathVariable String grouping, @PathVariable String username) {
        logger.info("Entered REST optOut...");
        return ResponseEntity
                .ok()
                .body(gs.optOut(username, grouping));
    }

    /**
     * if the user has previously opted in
     * this will cancel the effects of opting in
     * the user will be removed from the include Group
     * the user will not be added to the exclude Group
     * if the user is also in the basis Group, this will not effectively change the user's membership to that Grouping
     *
     * @param username : the username of user canceling opting in
     * @param grouping : the path to the grouping where the user will be canceling opting in
     * @return information about the success of canceling the opt in
     */
    @RequestMapping(value = "/{grouping}/{username}/cancelOptIn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> cancelOptIn(@PathVariable String grouping, @PathVariable String username) {
        logger.info("Entered REST cancelOptIn...");
        return ResponseEntity
                .ok()
                .body(gs.cancelOptIn(grouping, username));
    }

    /**
     * if the user has previously opted out
     * this will cancel the effects of opting out
     * the user will be removed from the exclude Group
     * the user will not be added to the include Group
     * if the user is not in the basis Group, this will not effectively change the user's membership to that Grouping
     *
     * @param username : the username of user canceling opting out
     * @param grouping : the path to the grouping where the user will be canceling opting out
     * @return information about the success of canceling the opt out
     */
    @RequestMapping(value = "/{grouping}/{username}/cancelOptOut",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> cancelOptOut(@PathVariable String grouping, @PathVariable String username) {
        logger.info("Entered REST cancelOptOut...");
        return ResponseEntity
                .ok()
                .body(gs.cancelOptOut(grouping, username));
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping is connected to a Listserv
     *
     * @param grouping:   the path to the Grouping
     * @param username:   username of the Grouping's owner
     * @param listservOn: true if the listserv should be on, false if it should be off
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{listservOn}/setListserv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> setListserv(@PathVariable String grouping, @PathVariable String username, @PathVariable boolean listservOn) {
        logger.info("Entered REST setListserv...");
        return ResponseEntity
                .ok()
                .body(gs.changeListservStatus(grouping, username, listservOn));
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping's members can opt in
     *
     * @param grouping: the path to the Grouping
     * @param username: username of the Grouping's owner
     * @param optInOn:  true if the members should be able to opt in, false if not
     * @return iformation about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{optInOn}/setOptIn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> setOptIn(@PathVariable String grouping, @PathVariable String username, @PathVariable boolean optInOn) {
        logger.info("Entered REST setOptIn...");
        return ResponseEntity
                .ok()
                .body(gs.changeOptInStatus(grouping, username, optInOn));
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping's members can opt out
     *
     * @param grouping: the path to the Grouping
     * @param username: username of the Grouping's owner
     * @param optOutOn: true if the members should be able to opt out, false if not
     * @return iformation about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{username}/{optOutOn}/setOptOut",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> setOptOut(@PathVariable String grouping, @PathVariable String username, @PathVariable boolean optOutOn) {
        logger.info("Entered REST setOptIn...");
        return ResponseEntity
                .ok()
                .body(gs.changeOptOutStatus(grouping, username, optOutOn));
    }

    @RequestMapping(value = "/{username}/adminLists",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminListsHolder> adminLists(@PathVariable String username) {
        logger.info("Entered REST adminListHolder...");
        return ResponseEntity
                .ok()
                .body(gs.adminLists(username));
    }

    /**
     * //@param grouping:    String containing the path of the parent Grouping
     * //@param newGrouping: String containing the name of the Grouping to be created
     *
     * @return information about the new Grouping and its success
     */
    @RequestMapping(value = "/addGrouping",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectView addGrouping() {
        return new RedirectView(requestForm);
        //todo change to real method when it is ready for testing
    }

    /**
     * removes a Grouping
     *
     * @return information about the deleted Grouping and its success
     */
    @RequestMapping(value = "/deleteGrouping",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectView deleteGrouping() {
        return new RedirectView(requestForm);
    }
    //todo change to real method when it is ready for testing
}

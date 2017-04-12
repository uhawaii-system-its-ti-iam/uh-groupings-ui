package edu.hawaii.its.groupings.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import edu.hawaii.its.holiday.api.type.Grouping;
import edu.hawaii.its.holiday.api.type.MyGroupings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import edu.hawaii.its.holiday.api.GroupingsService;

@RestController
public class GroupingsRestController {

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    @Value("${app.groupings.controller.uuid}")
    private String uuid;

    @Autowired
    private GroupingsService gs;

    @PostConstruct
    public void init() {
        Assert.hasLength(uuid, "Property 'app.groupings.controller.uuid' is required.");
        logger.info("GroupingsRestController started.");
    }


    /**
     * adds a member to the include group of the Grouping who's path is in 'grouping'
     * if that member is in the exclude group, they will be removed from it
     * @param grouping: path to the Grouping who's include group the new member will be added to
     * @param username: username of the Grouping owner preforming the operation
     * @param userToAdd: username of the new member to be added to the include group
     * @return information about the success of the operation
     */
    @RequestMapping("/api/groupings/addMemberToIncludeGroup")
    public ResponseEntity<Object> addMemberToIncludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToAdd){
        logger.info("Entered REST addMemberToIncludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.addMemberAs(username, grouping + ":include", userToAdd));
    }


    /**
     * adds a member to the exclude group of the Grouping who's path is in 'grouping'
     * if that member is in the include group, they will be removed from it
     * @param grouping: path to the Grouping who's exclude group the new member will be added to
     * @param username: username of the Grouping owner preforming the operation
     * @param userToAdd: username of the new member to be added to the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping("/api/groupings/addMemberToExcludeGroup")
    public ResponseEntity<Object> addMemberToExcludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToAdd){
        logger.info("Entered REST addMemberToExcludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.addMemberAs(username, grouping + ":exclude", userToAdd));
    }


    /**
     * deletes a member in the include group of the Grouping who's path is in 'grouping'
     * @param grouping: path to the Grouping who's include group contains the member to be deleted
     * @param username: username of the Grouping owner preforming the operation
     * @param userToDelete: username of the user to be deleted from the include group
     * @return information about the success of the operation
     */
    @RequestMapping("/api/groupings/deleteMemberFromIncludeGroup")
    public ResponseEntity<Object> deleteMemberFromIncludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToDelete){
        logger.info("Entered REST deleteMemberFromIncludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.deleteMemberAs(username, grouping + ":include", userToDelete));
    }


    /**
     * deletes a member in the exclude group of the Grouping who's path is in 'grouping'
     * @param grouping: path to the Grouping who's exclude group contains the member to be deleted
     * @param username: username of the Grouping owner preforming the operation
     * @param userToDelete: username of the user to be deleted from the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping("/api/groupings/deleteMemberFromExcludeGroup")
    public ResponseEntity<Object> deleteMemberFromExcludeGroup(@RequestParam String grouping, @RequestParam String username, @RequestParam String userToDelete){
        logger.info("Entered REST deleteMemberFromExcludeGroup...");
        return ResponseEntity
                .ok()
                .body(gs.deleteMemberAs(username, grouping + ":exclude", userToDelete));
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
    @RequestMapping("/api/groupings/assignOwnership")
    public ResponseEntity<Object[]> assignOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String newOwner) {
        logger.info("Entered REST assignOwnership...");
        return ResponseEntity
                .ok()
                .body(gs.assignOwnership(grouping, username, newOwner));
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
    @RequestMapping("/api/groupings/removeOwnership")
    public ResponseEntity<Object[]> removeOwnership(@RequestParam String grouping, @RequestParam String username, @RequestParam String ownerToRemove) {
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
     *      the Grouping will contain information about
     *          members of each Group in the grouping
     *          owners of the Grouping
     *          name of the Grouping
     *          path of the Grouping
     *          whether or not the Grouping has a list serve associated with it
     */
    @RequestMapping("/api/groupings/grouping")
    public ResponseEntity<Grouping> getGrouping(@RequestParam String grouping, @RequestParam String username) {
        logger.info("Entered REST grouping...");
        return ResponseEntity
                .ok()
                .body(gs.getGrouping(grouping, username));
    }
//    @RequestMapping(value = "/api/groupings/{grouping}/{username}/grouping",
//                    method = RequestMethod.GET,
//                    produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Grouping> grouping(@PathVariable String grouping, @PathVariable String username) {
//    logger.info("Entered REST grouping...");
//    return ResponseEntity
//            .ok()
//            .body(gs.getGrouping(grouping, username));
//    }


    /**
     *
     * @param username: username of user to get lists of Groupings for
     * @return a MyGrouping Object that contains
     *      Groupings that the user is in
     *      Groupings that the user owns
     *      Groupings that the user can opt into
     *      Groupings that the user can opt out of
     */
    @RequestMapping("/api/groupings/myGroupings")
    public ResponseEntity<MyGroupings> myGroupings(@RequestParam String username){
        logger.info("Entered REST myGroupings...");
        return ResponseEntity
                .ok()
                .body(gs.getMyGroupings(username));
    }
//    @RequestMapping(value = "/api/grouping/{username}/myGroupings",
//                    method = RequestMethod.GET,
//                    produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<MyGroupings> myGroupings(@PathVariable String username){
//    logger.info("Entered REST myGroupings...");
//    return ResponseEntity
//            .ok()
//            .body(gs.getMyGroupings(username));
//    }


    /**
     * if the user is allowed to opt into the grouping
     *      this will add them to the include group of that grouping
     *      if the user is in the exclude group, they will be removed from it
     *
     * @param username : the username of user opting in
     * @param grouping : the path to the grouping where the user will be opting in
     * @return information about the success of opting in
     */
    @RequestMapping("/api/groupings/optIn")
    public ResponseEntity<Object[]> optIn(@RequestParam String username, @RequestParam String grouping) {
        logger.info("Entered REST optIn...");
        return ResponseEntity
                .ok()
                .body(gs.optIn(username, grouping));
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
    @RequestMapping("/api/groupings/optOut")
    public ResponseEntity<Object[]> optOut(@RequestParam String username, @RequestParam String grouping) {
        logger.info("Entered REST optOut...");
        return ResponseEntity
                .ok()
                .body(gs.optOut(username, grouping));
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
    @RequestMapping("/api/groupings/cancelOptIn")
    public ResponseEntity<Object[]> cancelOptIn(@RequestParam String grouping, @RequestParam String username) {
        logger.info("Entered REST cancelOptIn...");
        return ResponseEntity
                .ok()
                .body(gs.cancelOptIn(grouping, username));
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
    @RequestMapping("/api/groupings/cancelOptOut")
    public ResponseEntity<Object[]> cancelOptOut(@RequestParam String grouping, @RequestParam String username) {
        logger.info("Entered REST cancelOptOut...");
        return ResponseEntity
                .ok()
                .body(gs.cancelOptOut(grouping, username));
    }




}

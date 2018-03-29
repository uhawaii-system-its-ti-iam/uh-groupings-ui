package edu.hawaii.its.api.controller;

import java.security.Principal;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import edu.hawaii.its.api.service.GroupAttributeService;
import edu.hawaii.its.api.service.GroupingAssignmentService;
import edu.hawaii.its.api.service.GroupingFactoryService;
import edu.hawaii.its.api.service.HelperService;
import edu.hawaii.its.api.service.MemberAttributeService;
import edu.hawaii.its.api.service.MembershipService;
import edu.hawaii.its.api.type.AdminListsHolder;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingAssignment;
import edu.hawaii.its.api.type.GroupingsServiceResult;

@RestController
@RequestMapping("/api/groupings")
public class GroupingsRestController {

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    @Value("${app.groupings.controller.uuid}")
    private String uuid;

    @Value("${app.iam.request.form}")
    private String requestForm;

    @Value("${groupings.api.exclude}")
    private String EXCLUDE;

    @Value("${groupings.api.include}")
    private String INCLUDE;

    @Autowired
    private GroupAttributeService groupAttributeService;

    @Autowired
    private GroupingAssignmentService groupingAssignmentService;

    @Autowired
    private GroupingFactoryService groupingFactoryService;

    @Autowired
    private HelperService helperService;

    @Autowired
    private MemberAttributeService memberAttributeService;

    @Autowired
    private MembershipService membershipService;

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
                .body("University of Hawaii Groupings API");
    }

    /**
     * adds a member to the admin group
     *
     * @param adminToAdd: username of the new admin to add
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{adminToAdd}/addAdmin",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> addAdmin(Principal principal, @PathVariable String adminToAdd) {
        logger.info("Entered REST addAdmin...");
        return ResponseEntity
                .ok()
                .body(membershipService.addAdmin(principal.getName(), adminToAdd));
    }

    /**
     * deletes a member from the admin group
     *
     * @param adminToDelete: username of the admin to be deleted
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{adminToDelete}/deleteAdmin",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> deleteAdmin(Principal principal, @PathVariable String adminToDelete) {
        logger.info("Entered REST deleteAdmin...");
        return ResponseEntity
                .ok()
                .body(membershipService.deleteAdmin(principal.getName(), adminToDelete));
    }

    /**
     * adds a member to a Grouping
     *
     * a member will not be in a Grouping for one of two reasons
     * - The member is not in the basis or include group
     * - The member is in the basis group, but also in the exclude group
     *
     * for the first case, the member will be added to the include group
     * for the second case, the member will be removed from the exclude group
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addGroupingMemberByUsername",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> addGroupingMemberByUsername(Principal principal, @PathVariable String grouping,
            @PathVariable String userToAdd) {
        logger.info("Entered REST addGroupingMemberByUsername...");
        return ResponseEntity
                .ok()
                .body(membershipService.addGroupingMemberByUsername(principal.getName(), grouping, userToAdd));
    }


    /**
     * adds a member to a Grouping
     *
     * a member will not be in a Grouping for one of two reasons
     * - The member is not in the basis or include group
     * - The member is in the basis group, but also in the exclude group
     *
     * for the first case, the member will be added to the include group
     * for the second case, the member will be removed from the exclude group
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addGroupingMemberByUuid",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> addGroupingMemberByUuid(Principal principal, @PathVariable String grouping,
                                                                            @PathVariable String userToAdd) {
        logger.info("Entered REST addGroupingMemberByUuid...");
        return ResponseEntity
                .ok()
                .body(membershipService.addGroupingMemberByUuid(principal.getName(), grouping, userToAdd));
    }

    /**
     * adds a member to the include group of the Grouping who's path is in 'grouping'
     * if that member is in the exclude group, they will be removed from it
     *
     * @param grouping: path to the Grouping who's include group the new member will be added to
     * @param userToAdd: username of the new member to be added to the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addMemberToIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> addMemberToIncludeGroup(Principal principal, @PathVariable String grouping,
            @PathVariable String userToAdd) {
        logger.info("Entered REST addMemberToIncludeGroup...");
        return ResponseEntity
                .ok()
                .body(membershipService.addGroupMemberByUsername(principal.getName(), grouping + INCLUDE, userToAdd));
    }

    /**
     * adds a member to the exclude group of the Grouping who's path is in 'grouping'
     * if that member is in the include group, they will be removed from it
     *
     * @param grouping: path to the Grouping who's exclude group the new member will be added to
     * @param userToAdd: username of the new member to be added to the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addMemberToExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> addMemberToExcludeGroup(Principal principal, @PathVariable String grouping,
            @PathVariable String userToAdd) {
        logger.info("Entered REST addMemberToExcludeGroup...");
        return ResponseEntity
                .ok()
                .body(membershipService.addGroupMemberByUsername(principal.getName(), grouping + EXCLUDE, userToAdd));
    }

    /**
     * Deletes a member from a Grouping
     *
     * if the user is in the basis, then it will add them to the exclude
     * if a user is in the include instead of the basis, then it will remove them from the include
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteGroupingMemberByUsername",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> deleteGroupingMemberByUsername(Principal principal, @PathVariable String grouping,
            @PathVariable String userToDelete) {
        logger.info("Entered REST deleteGroupingMemberByUsername...");
        return ResponseEntity
                .ok()
                .body(membershipService.deleteGroupingMemberByUsername(principal.getName(), grouping, userToDelete));
    }

    /**
     * Deletes a member from a Grouping
     *
     * if the user is in the basis, then it will add them to the exclude
     * if a user is in the include instead of the basis, then it will remove them from the include
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteGroupingMemberByUuid",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> deleteGroupingMemberByUuid(Principal principal, @PathVariable String grouping,
                                                                                       @PathVariable String userToDelete) {
        logger.info("Entered REST deleteGroupingMemberByUsername...");
        return ResponseEntity
                .ok()
                .body(membershipService.deleteGroupingMemberByUuid(principal.getName(), grouping, userToDelete));
    }

    /**
     * deletes a member in the include group of the Grouping who's path is in 'grouping'
     *
     * @param grouping: path to the Grouping who's include group contains the member to be deleted
     * @param userToDelete: username of the user to be deleted from the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteMemberFromIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> deleteMemberFromIncludeGroup(Principal principal, @PathVariable String grouping,
            @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromIncludeGroup...");
        return ResponseEntity
                .ok()
                .body(membershipService.deleteGroupMemberByUsername(principal.getName(), grouping + INCLUDE, userToDelete));
    }

    /**
     * deletes a member in the exclude group of the Grouping who's path is in 'grouping'
     *
     * @param grouping: path to the Grouping who's exclude group contains the member to be deleted
     * @param userToDelete: username of the user to be deleted from the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteMemberFromExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> deleteMemberFromExcludeGroup(Principal principal, @PathVariable String grouping,
            @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromExcludeGroup...");
        return ResponseEntity
                .ok()
                .body(membershipService.deleteGroupMemberByUsername(principal.getName(), grouping + EXCLUDE, userToDelete));
    }

    /**
     * gives the user read, update and view privileges for the Grouping
     * the user should already have view privilege, but the view privilege is added just in case
     * read privilege allows the user to see the members and owners of a Grouping
     * update privilege allows the user to add/delete the members and owners of a Grouping
     *
     * @param grouping: path to the grouping that the newOwner will get privileges for
     * @param newOwner: String containing the username of the Person to become the new owner
     * @return information about the privileges being added to new owner and the success of these privilege assignments
     */
    @RequestMapping(value = "/{grouping}/{newOwner}/assignOwnership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> assignOwnership(Principal principal, @PathVariable String grouping, @PathVariable String newOwner) {
        logger.info("Entered REST assignOwnership...");
        return ResponseEntity
                .ok()
                .body(memberAttributeService.assignOwnership(grouping, principal.getName(), newOwner));
    }

    /**
     * removes read, and update privileges from the user for the designated Grouping
     * read privilege allows the user to see the members and owners of a Grouping
     * update privilege allows the user to add/delete the members and owners of a Grouping
     * the user should keep the view privilege
     *
     * @param grouping: path to the grouping that the owner to be removed will get privileges revoked from
     * @param ownerToRemove: String containing the username of the Person whos owner privileges are to be revoked
     * @return information about the privileges being removed from the owner and the success of these privilege
     *         assignments
     */
    @RequestMapping(value = "/{grouping}/{ownerToRemove}/removeOwnership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> removeOwnership(Principal principal, @PathVariable String grouping, @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeOwnership...");
        return ResponseEntity
                .ok()
                .body(memberAttributeService.removeOwnership(grouping, principal.getName(), ownerToRemove));
    }

    /**
     * finds and returns the specified Grouping
     *
     * @param grouping : String containing the path of the Grouping to be searched for
     * @return the Grouping that was searched for
     *         the Grouping will contain information about
     *         members of each Group in the grouping
     *         owners of the Grouping
     *         name of the Grouping
     *         path of the Grouping
     *         whether or not the Grouping has a list serve associated with it
     */
    @RequestMapping(value = "/{grouping}/grouping",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Grouping> grouping(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST grouping...");
        return ResponseEntity
                .ok()
                .body(groupingAssignmentService.getGrouping(grouping, principal.getName()));
    }

    /**
     * @return a MyGrouping Object that contains
     *         Groupings that the user is in
     *         Groupings that the user owns
     *         Groupings that the user can opt into
     *         Groupings that the user can opt out of
     */
    @RequestMapping(value = "/groupingAssignment",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingAssignment> groupingAssignment(Principal principal) {
        logger.info("Entered REST GroupingAssingment...");
        return ResponseEntity
                .ok()
                .body(groupingAssignmentService.getGroupingAssignment(principal.getName()));
    }

    /**
     * if the user is allowed to opt into the grouping
     * this will add them to the include group of that grouping
     * if the user is in the exclude group, they will be removed from it
     *
     * @param grouping : the path to the grouping where the user will be opting in
     * @return information about the success of opting in
     */
    @RequestMapping(value = "/{grouping}/optIn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> optIn(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optIn...");
        return ResponseEntity
                .ok()
                .body(membershipService.optIn(principal.getName(), grouping));
    }

    /**
     * if the user is allowed to opt out of the grouping
     * this will add them to the exclude group of that grouping
     * if the user is in the include group of that Grouping, they will be removed from it
     *
     * @param grouping : the path to the grouping where the user will be opting out
     * @return information about the success of opting out
     */
    @RequestMapping(value = "/{grouping}/optOut",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> optOut(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optOut...");
        return ResponseEntity
                .ok()
                .body(membershipService.optOut(principal.getName(), grouping));
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping is connected to a Listserv
     *
     * @param grouping: the path to the Grouping
     * @param listservOn: true if the listserv should be on, false if it should be off
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{listservOn}/setListserv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupingsServiceResult> setListserv(Principal principal, @PathVariable String grouping, @PathVariable boolean listservOn) {
        logger.info("Entered REST setListserv...");
        return ResponseEntity
                .ok()
                .body(groupAttributeService.changeListservStatus(grouping, principal.getName(), listservOn));
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping's members can opt in
     *
     * @param grouping: the path to the Grouping
     * @param optInOn: true if the members should be able to opt in, false if not
     * @return iformation about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{optInOn}/setOptIn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> setOptIn(Principal principal, @PathVariable String grouping, @PathVariable boolean optInOn) {
        logger.info("Entered REST setOptIn...");
        return ResponseEntity
                .ok()
                .body(groupAttributeService.changeOptInStatus(grouping, principal.getName(), optInOn));
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping's members can opt out
     *
     * @param grouping: the path to the Grouping
     * @param optOutOn: true if the members should be able to opt out, false if not
     * @return iformation about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{optOutOn}/setOptOut",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> setOptOut(Principal principal, @PathVariable String grouping, @PathVariable boolean optOutOn) {
        logger.info("Entered REST setOptOut...");
        return ResponseEntity
                .ok()
                .body(groupAttributeService.changeOptOutStatus(grouping, principal.getName(), optOutOn));
    }

    @RequestMapping(value = "/adminLists",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminListsHolder> adminLists(Principal principal) {
        logger.info("Entered REST adminListHolder...");
        return ResponseEntity
                .ok()
                .body(groupingAssignmentService.adminLists(principal.getName()));
    }

    /**
     * //@param grouping: String containing the path of the parent Grouping
     * //@param newGrouping: String containing the name of the Grouping to be created
     *
     * @return information about the new Grouping and its success
     */
    @RequestMapping(value = "/{grouping}/{basis}/{include}/{exclude}/{owners}/addGrouping",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupingsServiceResult>> addGrouping(Principal principal,
            @PathVariable String grouping,
            //todo when fully implemented, basis will be changed to a string that contains the set theory logic for the
            // groups that it will be comprised of
            @PathVariable List<String> basis,
            @PathVariable List<String> include,
            @PathVariable List<String> exclude,
            @PathVariable List<String> owners) {
        logger.info("Entered REST addGrouping...");

        throw new UnsupportedOperationException();

        //todo implement method
        //        return ResponseEntity
        //                .ok()
        //                .body(groupingFactoryService.addGrouping(username, grouping, basis, include, exclude, owners));
    }

    /**
     * removes a Grouping
     *
     * @return information about the deleted Grouping and its success
     */
    @RequestMapping(value = "/{grouping}/deleteGrouping",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectView deleteGrouping(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST deleteGrouping...");

        throw new UnsupportedOperationException();

        //todo implement method
        //        return ResponseEntity
        //                .ok()
        //                .body(groupingFactoryService.deleteGrouping(username, grouping));
    }
}

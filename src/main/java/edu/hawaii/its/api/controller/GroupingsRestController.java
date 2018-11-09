package edu.hawaii.its.api.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import edu.hawaii.its.api.type.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

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

    @Value("${url.api.2.0.base}")
    private String API_2_0_BASE;

    @Value("${url.api.2.1.base}")
    private String API_2_1_BASE;

    @Value("${groupings.api.current_user}")
    private String CURRENT_USER;

    @Value("${groupings.api.listserv}")
    private String LISTSERV;

    @Value("${groupings.api.ldap}")
    private String UH_RELEASED_GROUPING;

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
     * Get a member's attributes based off username
     *
     * @param uid: Username of user to obtain attributes about
     * @return Map of user attributes
     */
    @RequestMapping(value = "/members/{uid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity memberAttributes(Principal principal, @PathVariable String uid) {
        logger.info("Entered REST memberAttributes...");
//        return ResponseEntity
//                .ok()
//                .body(memberAttributeService.getUserAttributes(uid));
        String uri = String.format(API_2_1_BASE + "/members/%s", uid);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.GET, Map.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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
    public ResponseEntity addAdmin(Principal principal, @PathVariable String adminToAdd) {
        logger.info("Entered REST addAdmin...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.addAdmin(principal.getName(), adminToAdd));
        String uri = String.format(API_2_0_BASE + "/admins/%s", adminToAdd);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, GroupingsServiceResult.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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
    public ResponseEntity deleteAdmin(Principal principal, @PathVariable String adminToDelete) {
        logger.info("Entered REST deleteAdmin...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.deleteAdmin(principal.getName(), adminToDelete));
        String uri = String.format(API_2_0_BASE + "/%s/deleteAdmin", adminToDelete);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, GroupingsServiceResult.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * adds a member to a Grouping
     * <p>
     * a member will not be in a Grouping for one of two reasons
     * - The member is not in the basis or include group
     * - The member is in the basis group, but also in the exclude group
     * <p>
     * for the first case, the member will be added to the include group
     * for the second case, the member will be removed from the exclude group
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addGroupingMemberByUsername",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addGroupingMemberByUsername(Principal principal,
                                                      @PathVariable String grouping,
                                                      @PathVariable String userToAdd) {
        logger.info("Entered REST addGroupingMemberByUsername...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.addGroupingMemberByUsername(principal.getName(), grouping, userToAdd));
        String uri = String.format(API_2_0_BASE + "/%s/%s/addGroupingMemberByUsername", grouping, userToAdd);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * adds a member to a Grouping
     * <p>
     * a member will not be in a Grouping for one of two reasons
     * - The member is not in the basis or include group
     * - The member is in the basis group, but also in the exclude group
     * <p>
     * for the first case, the member will be added to the include group
     * for the second case, the member will be removed from the exclude group
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addGroupingMemberByUuid",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addGroupingMemberByUuid(Principal principal,
                                                  @PathVariable String grouping,
                                                  @PathVariable String userToAdd) {
        logger.info("Entered REST addGroupingMemberByUuid...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.addGroupingMemberByUuid(principal.getName(), grouping, userToAdd));
        String uri = String.format(API_2_0_BASE + "/%s/%s/addGroupingMemberByUuid", grouping, userToAdd);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * adds a member to the include group of the Grouping who's path is in 'grouping'
     * if that member is in the exclude group, they will be removed from it
     *
     * @param grouping:  path to the Grouping who's include group the new member will be added to
     * @param userToAdd: username of the new member to be added to the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addMemberToIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addMemberToIncludeGroup(Principal principal,
                                                  @PathVariable String grouping,
                                                  @PathVariable String userToAdd) {
        logger.info("Entered REST addMemberToIncludeGroup...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.addGroupMemberByUsername(principal.getName(), grouping + INCLUDE, userToAdd));
        String uri = String.format(API_2_0_BASE + "/%s/%s/addMemberToIncludeGroup", grouping, userToAdd);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * adds a member to the exclude group of the Grouping who's path is in 'grouping'
     * if that member is in the include group, they will be removed from it
     *
     * @param grouping:  path to the Grouping who's exclude group the new member will be added to
     * @param userToAdd: username of the new member to be added to the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToAdd}/addMemberToExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addMemberToExcludeGroup(Principal principal,
                                                  @PathVariable String grouping,
                                                  @PathVariable String userToAdd) {
        logger.info("Entered REST addMemberToExcludeGroup...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.addGroupMemberByUsername(principal.getName(), grouping + EXCLUDE, userToAdd));
        String uri = String.format(API_2_0_BASE + "/%s/%s/addMemberToExcludeGroup", grouping, userToAdd);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * Deletes a member from a Grouping
     * <p>
     * if the user is in the basis, then it will add them to the exclude
     * if a user is in the include instead of the basis, then it will remove them from the include
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteGroupingMemberByUsername",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteGroupingMemberByUsername(Principal principal,
                                                         @PathVariable String grouping,
                                                         @PathVariable String userToDelete) {
        logger.info("Entered REST deleteGroupingMemberByUsername...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.deleteGroupingMemberByUsername(principal.getName(), grouping, userToDelete));
        String uri = String.format(API_2_0_BASE + "/%s/%s/deleteGroupingMemberByUsername", grouping, userToDelete);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * Deletes a member from a Grouping
     * <p>
     * if the user is in the basis, then it will add them to the exclude
     * if a user is in the include instead of the basis, then it will remove them from the include
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteGroupingMemberByUuid",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteGroupingMemberByUuid(Principal principal,
                                                     @PathVariable String grouping,
                                                     @PathVariable String userToDelete) {
        logger.info("Entered REST deleteGroupingMemberByUsername...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.deleteGroupingMemberByUuid(principal.getName(), grouping, userToDelete));
        String uri = String.format(API_2_0_BASE + "/%s/%s/deleteGroupingMemberByUuid", grouping, userToDelete);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * deletes a member in the include group of the Grouping who's path is in 'grouping'
     *
     * @param grouping:     path to the Grouping who's include group contains the member to be deleted
     * @param userToDelete: username of the user to be deleted from the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteMemberFromIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteMemberFromIncludeGroup(Principal principal,
                                                       @PathVariable String grouping,
                                                       @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromIncludeGroup...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService
//                        .deleteGroupMemberByUsername(principal.getName(), grouping + INCLUDE, userToDelete));
        String uri = String.format(API_2_0_BASE + "/%s/%s/deleteMemberFromIncludeGroup", grouping, userToDelete);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, GroupingsServiceResult.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * deletes a member in the exclude group of the Grouping who's path is in 'grouping'
     *
     * @param grouping:     path to the Grouping who's exclude group contains the member to be deleted
     * @param userToDelete: username of the user to be deleted from the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{userToDelete}/deleteMemberFromExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteMemberFromExcludeGroup(Principal principal,
                                                       @PathVariable String grouping,
                                                       @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromExcludeGroup...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService
//                        .deleteGroupMemberByUsername(principal.getName(), grouping + EXCLUDE, userToDelete));
        String uri = String.format(API_2_0_BASE + "/%s/%s/deleteMemberFromExcludeGroup", grouping, userToDelete);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, GroupingsServiceResult.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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
    public ResponseEntity assignOwnership(Principal principal, @PathVariable String grouping,
                                          @PathVariable String newOwner) {
        logger.info("Entered REST assignOwnership...");
//        return ResponseEntity
//                .ok()
//                .body(memberAttributeService.assignOwnership(grouping, principal.getName(), newOwner));
        String uri = String.format(API_2_0_BASE + "/%s/%s/assignOwnership", grouping, newOwner);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, GroupingsServiceResult.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * removes read, and update privileges from the user for the designated Grouping
     * read privilege allows the user to see the members and owners of a Grouping
     * update privilege allows the user to add/delete the members and owners of a Grouping
     * the user should keep the view privilege
     *
     * @param grouping:      path to the grouping that the owner to be removed will get privileges revoked from
     * @param ownerToRemove: String containing the username of the Person whos owner privileges are to be revoked
     * @return information about the privileges being removed from the owner and the success of these privilege
     * assignments
     */
    @RequestMapping(value = "/{grouping}/{ownerToRemove}/removeOwnership",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeOwnership(Principal principal, @PathVariable String grouping,
                                          @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeOwnership...");
//        return ResponseEntity
//                .ok()
//                .body(memberAttributeService.removeOwnership(grouping, principal.getName(), ownerToRemove));
        String uri = String.format(API_2_0_BASE + "/%s/%s/removeOwnership", grouping, ownerToRemove);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, GroupingsServiceResult.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * finds and returns the specified Grouping
     *
     * @param grouping : String containing the path of the Grouping to be searched for
     * @return the Grouping that was searched for
     * the Grouping will contain information about
     * members of each Group in the grouping
     * owners of the Grouping
     * name of the Grouping
     * path of the Grouping
     * whether or not the Grouping has a list serve associated with it
     */
    @RequestMapping(value = "/{grouping}/grouping",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity grouping(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST grouping...");
//        return ResponseEntity
//                .ok()
//                .body(groupingAssignmentService.getGrouping(grouping, principal.getName()));
        String uri = String.format(API_2_0_BASE + "/%s/grouping", grouping);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.GET, Grouping.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     *  finds and returns the specified paginated grouping
     *
     * @param paginated grouping :
     * @param grouping
     * @return The Grouping that was searched for
     */
    @RequestMapping(value = "/grouping/{path}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity paginatedGrouping(Principal principal, @PathVariable String path,
                @RequestParam(value = "page") Integer page,
                @RequestParam(value = "size") Integer size) {
        logger.info("Entered REST paginatedgrouping...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/?page=%d&size=%d", path, page, size);
        return makeApiRequest(principal.getName(), uri, HttpMethod.GET, Grouping.class);

    }

    /**
     * @return a MyGrouping Object that contains
     * Groupings that the user is in
     * Groupings that the user owns
     * Groupings that the user can opt into
     * Groupings that the user can opt out of
     */
    @RequestMapping(value = "/groupingAssignment",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity groupingAssignment(Principal principal) {
        logger.info("Entered REST GroupingAssignment...");
//        return ResponseEntity
//                .ok()
//                .body(groupingAssignmentService.getGroupingAssignment(principal.getName()));
        String uri = API_2_0_BASE + "/groupingAssignment";
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.GET, GroupingAssignment.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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
    public ResponseEntity optIn(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optIn...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.optIn(principal.getName(), grouping));
        String uri = String.format(API_2_0_BASE + "/%s/optIn", grouping);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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
    public ResponseEntity optOut(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optOut...");
//        return ResponseEntity
//                .ok()
//                .body(membershipService.optOut(principal.getName(), grouping));
        String uri = String.format(API_2_0_BASE + "/%s/optOut", grouping);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping is connected to a Listserv
     *
     * @param grouping:   the path to the Grouping
     * @param listservOn: true if the listserv should be on, false if it should be off
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{listservOn}/setListserv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setListserv(Principal principal,
                                      @PathVariable String grouping,
                                      @PathVariable boolean listservOn) {
        logger.info("Entered REST setListserv...");
//        return ResponseEntity
//                .ok()
//                .body(groupAttributeService.changeListservStatus(grouping, principal.getName(), listservOn));
        String ending = "disable";
        if (listservOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_0_BASE + "/groupings/%s/preferences/%s/%s", grouping, LISTSERV, ending);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.PUT, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping is connected to LDAP.
     *
     * @param grouping: the path to the Grouping
     * @param ldapOn:   true if the ldap should be on, false if it should be off
     * @return information about the success of the opertaion
     */
    @RequestMapping(value = "/{grouping}/{ldapOn}/setLdap",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setLdap(Principal principal,
                                  @PathVariable String grouping,
                                  @PathVariable boolean ldapOn) {
        logger.info("Entered REST setLdap...");
//        return ResponseEntity
//                .ok()
//                .body(groupAttributeService.changeLdapStatus(grouping, principal.getName(), ldapOn));
        String ending = "disable";
        if (ldapOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/preferences/%s/%s", grouping, UH_RELEASED_GROUPING, ending);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.PUT, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping's members can opt in
     *
     * @param grouping: the path to the Grouping
     * @param optInOn:  true if the members should be able to opt in, false if not
     * @return iformation about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{optInOn}/setOptIn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setOptIn(Principal principal,
                                   @PathVariable String grouping,
                                   @PathVariable boolean optInOn) {
        logger.info("Entered REST setOptIn...");
//        return ResponseEntity
//                .ok()
//                .body(groupAttributeService.changeOptInStatus(grouping, principal.getName(), optInOn));
        String uri = String.format(API_2_0_BASE + "/%s/%s/setOptIn", grouping, optInOn);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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
    public ResponseEntity setOptOut(Principal principal, @PathVariable String grouping,
                                    @PathVariable boolean optOutOn) {
        logger.info("Entered REST setOptOut...");
//        return ResponseEntity
//                .ok()
//                .body(groupAttributeService.changeOptOutStatus(grouping, principal.getName(), optOutOn));
        String uri = String.format(API_2_0_BASE + "/%s/%s/setOptOut", grouping, optOutOn);
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.POST, List.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
    }

    @RequestMapping(value = "/adminLists",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity adminLists(Principal principal) {
        logger.info("Entered REST adminListHolder...");
//        return ResponseEntity
//                .ok()
//                .body(groupingAssignmentService.adminLists(principal.getName()));
        String uri = API_2_0_BASE + "/adminLists";
        try {
            return makeApiRequest(principal.getName(), uri, HttpMethod.GET, AdminListsHolder.class);
        } catch (Exception e) {
            GroupingsHTTPException ge = new GroupingsHTTPException("API Error", e);
            return ResponseEntity
                    .badRequest()
                    .body(ge);
        }
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

    private ResponseEntity makeApiRequest(String currentUser, String uri, HttpMethod method, Class responseClass) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CURRENT_USER, currentUser);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        return restTemplate.exchange(uri, method, httpEntity, responseClass);
    }
}

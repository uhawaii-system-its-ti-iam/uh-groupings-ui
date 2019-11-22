package edu.hawaii.its.api.controller;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.html.Sanitizers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/groupings")
public class GroupingsRestController {

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    private org.owasp.html.PolicyFactory policy;

    @Value("${app.groupings.controller.uuid}")
    private String uuid;

    @Value("${app.iam.request.form}")
    private String requestForm;

    @Value("${groupings.api.exclude}")
    private String EXCLUDE;

    @Value("${groupings.api.include}")
    private String INCLUDE;

    @Value("${url.api.2.1.base}")
    private String API_2_1_BASE;

    @Value("${groupings.api.listserv}")
    private String LISTSERV;

    @Value("${groupings.api.ldap}")
    private String UH_RELEASED_GROUPING;

    @Value("${groupings.api.opt_in}")
    private String OPT_IN;

    @Value("${groupings.api.opt_out}")
    private String OPT_OUT;


    /*
     * This is a dummy name and should never become a real user. In the event that it does, it would cause the app to
     * crash. A solution to this would be either remove the dummy user from the database or use a different fake name
     * that would then become the dummy name.
    */
    @Value("${groupings.api.check}")
    private String CREDENTIAL_CHECK_USER;

    @Autowired
    private Environment env;

    @Autowired
    HttpRequestService httpRequestService;

    @PostConstruct
    public void init() {
        Assert.hasLength(uuid, "Property 'app.groupings.controller.uuid' is required.");
        logger.info("GroupingsRestController started.");

        // For sanitation
        policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        /*
        * Checks to make sure that the API is running and that there are no issues with the overrides file.
        *
        * Gets the active profiles and only runs the tests the active profile relies on the API.
        */
        if(!Arrays.asList(env.getActiveProfiles()).contains("localTest")) {

            // Stops the application from running if the API is not up and displays error message to console.
            Assert.isTrue(isBackendUp().getStatusCode().is2xxSuccessful(),
                    "Please start the UH Groupings API first.");

            // Stops the application from running if there is issue with overrides file.
            Assert.isTrue(credentialCheck().getStatusCode().toString().startsWith("403"),
                    "Possible credential error. Please check the overrides file.");
        }
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("University of Hawaii UHGroupings");
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

        String safeInput = policy.sanitize(uid);

        String uri = String.format(API_2_1_BASE + "/members/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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
        String safeInput = policy.sanitize(adminToAdd);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.POST);
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

        String safeInput = policy.sanitize(adminToDelete);

        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
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

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToAdd = policy.sanitize(userToAdd);

        logger.info("Entered REST addMemberToIncludeGroup...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s", safeGrouping, safeUserToAdd);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Uses  the api's includeMultipleMembers utility.
     * adds multiple members to the include group of the Grouping who's path is in 'grouping'
     * if the members are in the exclude group, they will be removed from it
     * SEE:   addMembers():        general.controller.js
     * updateAddMembers():  general.controller.js
     *
     * @param grouping:   path to the Grouping who's include group the new member will be added to
     * @param usersToAdd: usernames of the new members to be added to the include group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{usersToAdd}/addMembersToIncludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addMembersToIncludeGroup(Principal principal,
            @PathVariable String grouping,
            @PathVariable String usersToAdd) {
        logger.info("Entered REST addMembersToIncludeGroup...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/includeMultipleMembers/%s", grouping, usersToAdd);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
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

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToAdd = policy.sanitize(userToAdd);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s", safeGrouping, safeUserToAdd);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Uses  the api's excludeMultipleMembers utility.
     * adds multiple members to the exclude group of the Grouping who's path is in 'grouping'
     * if the members are in the exclude group, they will be removed from it
     * SEE:   addMembers():        general.controller.js
     * updateAddMembers():  general.controller.js
     *
     * @param grouping:   path to the Grouping who's exclude group the new member will be added to
     * @param usersToAdd: usernames of the new members to be added to the exclude group
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{usersToAdd}/addMembersToExcludeGroup",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addMembersToExcludeGroup(Principal principal,
            @PathVariable String grouping,
            @PathVariable String usersToAdd) {
        logger.info("Entered REST addMembersToExcludeGroup...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/excludeMultipleMembers/%s", grouping, usersToAdd);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
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

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToDelete = policy.sanitize(userToDelete);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s", safeGrouping, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
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

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToDelete = policy.sanitize(userToDelete);

        logger.info("Entered REST deleteMemberFromExcludeGroup...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s", safeGrouping, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
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

        String safeGrouping = policy.sanitize(grouping);
        String safeNewOwner = policy.sanitize(newOwner);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGrouping, newOwner);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
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

        String safeGrouping = policy.sanitize(grouping);
        String safeOwnerToRemove = policy.sanitize(ownerToRemove);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGrouping, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Updates the description of a grouping to the new one
     *
     * @param path:        path to the grouping that the description will be updated
     * @param description: String containing the description of the group to be updated
     * @return information about the descripiton and group being updated
     */

    @RequestMapping(value = "/groupings/{path}/description",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDescription(Principal principal, @PathVariable String path,
            @RequestBody(required = false) String description) {
        logger.info("Entered REST updateDescription...");

        String safePath = policy.sanitize(path);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", safePath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, description, HttpMethod.PUT);
    }

    /**
     * finds and returns the specified Grouping
     *
     * @param path : String containing the path of the Grouping to be searched for
     * @return the Grouping that was searched for
     * the Grouping will contain information about
     * members of each Group in the grouping
     * owners of the Grouping
     * name of the Grouping
     * path of the Grouping
     * whether or not the Grouping has a list serve associated with it
     */

    //    @RequestMapping(value = "/{grouping}/grouping",
    //            method = RequestMethod.GET,
    //            produces = MediaType.APPLICATION_JSON_VALUE)
    //    public ResponseEntity grouping(Principal principal, @PathVariable String grouping) {
    //        logger.info("Entered REST grouping...");
    //        String uri = String.format(API_2_1_BASE + "/groupings/%s", grouping);
    //        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    //    }
    //
    //    //todo This is a test mapping
    //    /**
    //     * Some comments for later (this is getPaginatedGrouping)
    //     */
    //    @RequestMapping(value = "/groupings/{path}",
    //        method = RequestMethod.GET,
    //        produces = MediaType.APPLICATION_JSON_VALUE)
    //    public ResponseEntity paginatedGrouping(Principal principal, @PathVariable String path,
    //            @RequestParam(value = "page") Integer page,
    //            @RequestParam(value = "size") Integer size) {
    //        logger.info("Entered REST paginatedGrouping...");
    //        String uri = String.format(API_2_1_BASE + "/groupings/%s?page=%d&size=%d", path, page, size);
    //        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    //    }

    //todo Consolidate getGrouping and getPaginatedGrouping into one call
    @RequestMapping(value = "/groupings/{path:.+}",

            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity grouping(Principal principal, @PathVariable String path,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortString,
            @RequestParam(required = false) Boolean isAscending) {
        logger.info("Entered REST getGrouping...");
        String baseUri = String.format(API_2_1_BASE + "/groupings/%s?", path);

        // todo There might be a better way to do this, this approach is kinda gross
        String params = "";
        if (page != null)
            params = params + "page=" + page;
        if (size != null) {
            if (!params.equals(""))
                params = params + "&";
            params = params + "size=" + size;
        }
        if (sortString != null) {
            if (!params.equals(""))
                params = params + "&";
            params = params + "sortString=" + sortString;
        }
        if (isAscending != null) {
            if (!params.equals(""))
                params = params + "&";

            params = params + "isAscending=" + isAscending;
        }

        logger.info(baseUri + params);

        return httpRequestService.makeApiRequest(principal.getName(), baseUri + params, HttpMethod.GET);
    }

    /**
     * @return a MembershipAssignment Object that contains
     * Groupings that the user is in
     * Groupings that the user can opt into
     */
    @RequestMapping(value = "/members/groupings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity membershipAssignment(Principal principal) {
        logger.info("Entered REST MembershipAssignment...");
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * @return a list of groupings that a user owns
     */
    @RequestMapping(value = "/owners/groupings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity groupingsOwned(Principal principal) {
        logger.info("Entered REST GroupingAssignment...");
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * @return a list of groupings that a user owns
     */
    @RequestMapping(value = "/owners/{uid}/groupings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity groupingsOwnedUid(Principal principal, @PathVariable String uid) {
        logger.info("Entered REST GroupingAssignment...");

        String safeUid = policy.sanitize(uid);

        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", safeUid);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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

        String safeGrouping = policy.sanitize(grouping);

        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s/self", safeGrouping, principal.getName());

        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
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

        String safeGrouping = policy.sanitize(grouping);

        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s/self", safeGrouping, principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    //todo Remove
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

        String safeGrouping = policy.sanitize(grouping);

        logger.info("Entered REST setListserv...");
        return changePreference(safeGrouping, principal.getName(), LISTSERV, listservOn);
    }

    //todo Remove
    /**
     * This allows an owner of a Grouping to change whether or not a Grouping is connected to LDAP.
     *
     * @param grouping: the path to the Grouping
     * @param ldapOn:   true if the ldap should be on, false if it should be off
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{ldapOn}/setLdap",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setLdap(Principal principal,
            @PathVariable String grouping,
            @PathVariable boolean ldapOn) {

        String safeGrouping = policy.sanitize(grouping);

        logger.info("Entered REST setLdap...");
        return changePreference(safeGrouping, principal.getName(), UH_RELEASED_GROUPING, ldapOn);
    }

    /**
     * This allows an owner of a Grouping to enable that a Grouping connected to a given sync destination
     *
     * @param path: the path to the Grouping
     * @param syncDestId: id of the syncDest to be enabled
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/groupings/{path}/syncDests/{syncDestId}/enable",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity enableSyncDest(Principal principal,
            @PathVariable String path,
            @PathVariable String syncDestId) {

        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);

        logger.info("Entered REST enableSyncDest...");
        return changeSyncDest(safeGrouping, principal.getName(), safeSyncDestId, true);
    }

    /**
     * This allows an owner of a Grouping to disable that a Grouping connected to a given sync destination
     *
     * @param path: the path to the Grouping
     * @param syncDestId: id of the syncDest to be disabled
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/groupings/{path}/syncDests/{syncDestId}/disable",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity disableSyncDest(Principal principal,
            @PathVariable String path,
            @PathVariable String syncDestId) {

        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);

        logger.info("Entered REST disableSyncDest...");
        return changeSyncDest(safeGrouping, principal.getName(), safeSyncDestId, false);
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

        String safeGrouping = policy.sanitize(grouping);

        logger.info("Entered REST setOptIn...");
        return changePreference(safeGrouping, principal.getName(), OPT_IN, optInOn);
    }

    /**
     * This allows an owner of a Grouping to change whether or not a Grouping's members can opt out
     *
     * @param grouping: the path to the Grouping
     * @param optOutOn: true if the members should be able to opt out, false if not
     * @return information about the success of the operation
     */
    @RequestMapping(value = "/{grouping}/{optOutOn}/setOptOut",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setOptOut(Principal principal, @PathVariable String grouping,
            @PathVariable boolean optOutOn) {

        String safeGrouping = policy.sanitize(grouping);

        logger.info("Entered REST setOptOut...");
        return changePreference(safeGrouping, principal.getName(), OPT_OUT, optOutOn);
    }

    @RequestMapping(value = "/adminLists",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity adminLists(Principal principal) {
        logger.info("Entered REST adminListHolder...");
        String uri = API_2_1_BASE + "/adminsGroupings";
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Returns a list of supported sync destinations
     *
     * @return List of Sync Destinations
     */
    @RequestMapping(value = "/groupings/{path}/syncDestinations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllSyncDestinations(Principal principal,
            @PathVariable String path) {
        logger.info("Entered REST getAllSyncDestinations...");

        String safePath = policy.sanitize(path);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/syncDestinations", safePath);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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

    ///////////////////////////////////////////////////////////////////////
    // Helper Functions
    //////////////////////////////////////////////////////////////////////

    // Helper method to change preferenes
    private ResponseEntity changePreference(String grouping, String username, String preference, Boolean isOn) {

        String ending = "disable";
        if (isOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/preferences/%s/%s", grouping, preference, ending);
        return httpRequestService.makeApiRequest(username, uri, HttpMethod.PUT);
    }

    private ResponseEntity changeSyncDest(String grouping, String username, String syncDest, Boolean isOn) {

        String ending = "disable";
        if (isOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/syncDests/%s/%s", grouping, syncDest, ending);
        return httpRequestService.makeApiRequest(username, uri, HttpMethod.PUT);
    }

    /*
    *
    * Helper function for checking overrides file.
    *
    * Makes an HTTP request to the API, specifically getting the list of admins and all groupings.
    * Should return a 403 Forbidden since CREDENTIAL_CHECK_USER should never exist.
    */
    private ResponseEntity credentialCheck() {

        String uri = API_2_1_BASE + "/adminsGroupings";

        return httpRequestService.makeApiRequest(CREDENTIAL_CHECK_USER, uri, HttpMethod.GET);
    }

    /*
     *
     * Helper function for checking if API is running.
     *
     * Makes an HTTP request to the API, specifically getting the landing page.
     * Should return a 200 OK.
     */
    private  ResponseEntity isBackendUp() {
        String uri = API_2_1_BASE + "/";

        return httpRequestService.makeApiRequest(CREDENTIAL_CHECK_USER, uri, HttpMethod.GET);
    }
}

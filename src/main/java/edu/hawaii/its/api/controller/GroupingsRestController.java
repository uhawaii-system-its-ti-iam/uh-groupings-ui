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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        if (!Arrays.asList(env.getActiveProfiles()).contains("localTest")) {

            // Stops the application from running if the API is not up and displays error message to console.
            Assert.isTrue(isBackendUp().getStatusCode().is2xxSuccessful(),
                    "Please start the UH Groupings API first.");

            // Stops the application from running if there is issue with overrides file.
            Assert.isTrue(credentialCheck().getStatusCode().toString().startsWith("403"),
                    "Possible credential error. Please check the overrides file.");
        }
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("University of Hawaii UHGroupings");
    }

    @GetMapping(value = "/generic")
    public ResponseEntity generic(Principal principal) {
        return httpRequestService.makeApiRequest(principal.getName(), API_2_1_BASE + "/generic", HttpMethod.GET);
    }

    @GetMapping(value = "/adminLists")
    public ResponseEntity adminLists(Principal principal) {
        logger.info("Entered REST adminListHolder...");
        String uri = API_2_1_BASE + "/adminsGroupings";
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * True if principal is an admin.
     *
     * @param principal - uid in question.
     * @return - GenericServiceResult {groupingsServiceResult: GroupingsServiceResult, isAdmin: bool }.
     */
    @GetMapping(value = "/admins")
    public ResponseEntity isAdmin(Principal principal) {
        logger.info("Entered REST isAdmin...");
        String uri = String.format(API_2_1_BASE + "/admins/%s/", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * adds a member to the admin group
     *
     * @param adminToAdd: username of the new admin to add
     * @return information about the success of the operation
     */
    @PostMapping(value = "/{adminToAdd}/addAdmin")
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
    @PostMapping(value = "/{adminToDelete}/deleteAdmin")
    public ResponseEntity deleteAdmin(Principal principal,
            @PathVariable String adminToDelete) {
        logger.info("Entered REST deleteAdmin...");

        String safeInput = policy.sanitize(adminToDelete);

        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Get a member's attributes based off username
     *
     * @param uid: Username of user to obtain attributes about
     * @return Map of user attributes
     */
    @GetMapping(value = "/members/{uid}")
    @ResponseBody
    public ResponseEntity memberAttributes(Principal principal, @PathVariable String uid) {
        logger.info("Entered REST memberAttributes...");

        String safeInput = policy.sanitize(uid);

        String uri = String.format(API_2_1_BASE + "/members/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * @return a MembershipAssignment Object that contains
     * Groupings that the user is in
     * Groupings that the user can opt into
     */
    @GetMapping(value = "/members/groupings")
    public ResponseEntity membershipAssignment(Principal principal) {
        logger.info("Entered REST MembershipAssignment...");
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", principal.getName());
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
    @PostMapping(value = "/{grouping}/optIn")
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
    @PostMapping(value = "/{grouping}/optOut")
    public ResponseEntity optOut(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optOut...");

        String safeGrouping = policy.sanitize(grouping);

        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s/self", safeGrouping, principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * adds a member to the include group of the Grouping who's path is in 'grouping'
     * if that member is in the exclude group, they will be removed from it
     *
     * @param grouping:  path to the Grouping who's include group the new member will be added to
     * @param userToAdd: username of the new member to be added to the include group
     * @return information about the success of the operation
     */
    @PostMapping(value = "/{grouping}/{userToAdd}/addMemberToIncludeGroup")
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
    @PostMapping(value = "/{grouping}/{usersToAdd}/addMembersToIncludeGroup")
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
    @PostMapping(value = "/{grouping}/{userToAdd}/addMemberToExcludeGroup")
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
    @PostMapping(value = "/{grouping}/{usersToAdd}/addMembersToExcludeGroup")
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
    @PostMapping(value = "/{grouping}/{userToDelete}/deleteMemberFromIncludeGroup")
    public ResponseEntity deleteMemberFromIncludeGroup(Principal principal,
            @PathVariable String grouping,
            @PathVariable String userToDelete) {
        logger.info("Entered REST deleteMemberFromIncludeGroup...");

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToDelete = policy.sanitize(userToDelete);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s", safeGrouping, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    @PostMapping(value = "/{grouping}/{usersToDelete}/deleteMembersFromIncludeGroup")
    public ResponseEntity deleteMembersFromIncludeGroup(Principal principal,
            @PathVariable String grouping,
            @PathVariable String usersToDelete) {

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToDelete = policy.sanitize(usersToDelete);

        logger.info("Entered REST deleteMembersFromIncludeGroup...");
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/includeMultipleMembers/%s", safeGrouping, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * deletes a member in the exclude group of the Grouping who's path is in 'grouping'
     *
     * @param grouping:     path to the Grouping who's exclude group contains the member to be deleted
     * @param userToDelete: username of the user to be deleted from the exclude group
     * @return information about the success of the operation
     */
    @PostMapping(value = "/{grouping}/{userToDelete}/deleteMemberFromExcludeGroup")
    public ResponseEntity deleteMemberFromExcludeGroup(Principal principal,
            @PathVariable String grouping,
            @PathVariable String userToDelete) {

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToDelete = policy.sanitize(userToDelete);

        logger.info("Entered REST deleteMemberFromExcludeGroup...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s", safeGrouping, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    @PostMapping(value = "/{grouping}/{usersToDelete}/deleteMembersFromExcludeGroup")
    public ResponseEntity deleteMembersFromExcludeGroup(Principal principal,
            @PathVariable String grouping,
            @PathVariable String usersToDelete) {

        String safeGrouping = policy.sanitize(grouping);
        String safeUserToDelete = policy.sanitize(usersToDelete);

        logger.info("Entered REST deleteMembersFromExcludeGroup...");
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/excludeMultipleMembers/%s", safeGrouping, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * @return a list of groupings that a user owns
     */
    @GetMapping(value = "/owners/groupings")
    public ResponseEntity groupingsOwned(Principal principal) {
        logger.info("Entered REST GroupingAssignment...");
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * True if principal is an owner of any grouping.
     *
     * @param principal - uid in question.
     * @return - GenericServiceResult {groupingsServiceResult: GroupingsServiceResult, isOwner: bool }.
     */
    @GetMapping(value = "/owners")
    public ResponseEntity isOwner(Principal principal) {
        logger.info("Entered REST isOwner...");
        String uri = String.format(API_2_1_BASE + "/owners/%s/", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * @return a list of groupings that a user owns
     */
    @GetMapping(value = "/owners/{uid}/groupings")
    public ResponseEntity groupingsOwnedUid(Principal principal, @PathVariable String uid) {
        logger.info("Entered REST GroupingAssignment...");

        String safeUid = policy.sanitize(uid);
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", safeUid);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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
    @PostMapping(value = "/{grouping}/{newOwner}/assignOwnership")
    public ResponseEntity assignOwnership(Principal principal, @PathVariable String grouping,
            @PathVariable String newOwner) {
        logger.info("Entered REST assignOwnership...");

        String safeGrouping = policy.sanitize(grouping);
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
    @PostMapping(value = "/{grouping}/{ownerToRemove}/removeOwnership")
    public ResponseEntity removeOwnership(Principal principal, @PathVariable String grouping,
            @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeOwnership...");

        String safeGrouping = policy.sanitize(grouping);
        String safeOwnerToRemove = policy.sanitize(ownerToRemove);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGrouping, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    @GetMapping(value = "/groupings/{path:.+}/metaData")
    public ResponseEntity getGroupingMetaData(Principal principal, @PathVariable String path) {
        logger.info("Entered REST getGroupingMetaData...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/metaData", path);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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
    //todo Consolidate getGrouping and getPaginatedGrouping into one call
    @GetMapping(value = "/groupings/{path:.+}")
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
            if (!"".equals(params))
                params = params + "&";
            params = params + "size=" + size;
        }
        if (sortString != null) {
            if (!"".equals(params))
                params = params + "&";
            params = params + "sortString=" + sortString;
        }
        if (isAscending != null) {
            if (!"".equals(params))
                params = params + "&";

            params = params + "isAscending=" + isAscending;
        }
        logger.info(baseUri + params);

        return httpRequestService.makeApiRequest(principal.getName(), baseUri + params, HttpMethod.GET);
    }

    /**
     * Updates the description of a grouping to the new one
     *
     * @param path:        path to the grouping that the description will be updated
     * @param description: String containing the description of the group to be updated
     * @return information about the descripiton and group being updated
     */
    @PutMapping(value = "/groupings/{path}/description")
    public ResponseEntity updateDescription(Principal principal, @PathVariable String path,
            @RequestBody(required = false) String description) {
        logger.info("Entered REST updateDescription...");

        String safePath = policy.sanitize(path);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", safePath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, description, HttpMethod.PUT);
    }

    /**
     * This allows an owner of a Grouping to enable that a Grouping connected to a given sync destination
     *
     * @param path:       the path to the Grouping
     * @param syncDestId: id of the syncDest to be enabled
     * @return information about the success of the operation
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/enable")
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
     * @param path:       the path to the Grouping
     * @param syncDestId: id of the syncDest to be disabled
     * @return information about the success of the operation
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/disable")
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
    @PostMapping(value = "/{grouping}/{optInOn}/setOptIn")
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
    @PostMapping(value = "/{grouping}/{optOutOn}/setOptOut")
    public ResponseEntity setOptOut(Principal principal, @PathVariable String grouping,
            @PathVariable boolean optOutOn) {

        String safeGrouping = policy.sanitize(grouping);

        logger.info("Entered REST setOptOut...");
        return changePreference(safeGrouping, principal.getName(), OPT_OUT, optOutOn);
    }

    /**
     * Returns a list of supported sync destinations
     *
     * @return List of Sync Destinations
     */
    @GetMapping(value = "/groupings/{path}/syncDestinations")
    public ResponseEntity getAllSyncDestinations(Principal principal,
            @PathVariable String path) {
        logger.info("Entered REST getAllSyncDestinations...");

        String safePath = policy.sanitize(path);

        String uri = String.format(API_2_1_BASE + "/groupings/%s/syncDestinations", safePath);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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
    private ResponseEntity isBackendUp() {
        String uri = API_2_1_BASE + "/";

        return httpRequestService.makeApiRequest(CREDENTIAL_CHECK_USER, uri, HttpMethod.GET);
    }
}

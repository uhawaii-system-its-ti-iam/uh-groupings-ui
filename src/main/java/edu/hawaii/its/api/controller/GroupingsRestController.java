package edu.hawaii.its.api.controller;

import java.security.Principal;
import java.util.Arrays;

import javax.annotation.PostConstruct;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;

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

    @Autowired
    private UserContextService userContextService;

    /*
     * Checks to make sure that the API is running and that there are no issues with the overrides file.
     * Gets the active profiles and only runs the tests the active profile relies on the API.
     */
    @PostConstruct
    public void init() {
        Assert.hasLength(uuid, "Property 'app.groupings.controller.uuid' is required.");
        logger.info("GroupingsRestController started.");

        policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        if (!Arrays.asList(env.getActiveProfiles()).contains("localTest")) {
            Assert.isTrue(httpRequestService.makeApiRequest(CREDENTIAL_CHECK_USER, API_2_1_BASE + "/", HttpMethod.GET)
                            .getStatusCode()
                            .is2xxSuccessful(),
                    "Please start the UH Groupings API first.");
        }
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("University of Hawaii UHGroupings");
    }

    @GetMapping(value = "/currentUser")
    public ResponseEntity<User> currentUser() {
        User currentUser = userContextService.getCurrentUser();
        return ResponseEntity.ok().body(currentUser);
    }

    @GetMapping(value = "/adminLists")
    public ResponseEntity<String> adminLists(Principal principal) {
        logger.info("Entered REST adminLists...");
        String uri = API_2_1_BASE + "/adminsGroupings";
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Check if principle is an administrator.
     */
    @GetMapping(value = "/admins")
    public ResponseEntity<String> hasAdminPrivs(Principal principal) {
        logger.info("Entered REST hasAdminPrivs...");
        String uri = String.format(API_2_1_BASE + "/admins", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Add a member to the admin group.
     */
    @PostMapping(value = "/{adminToAdd}/addAdmin")
    public ResponseEntity<String> addAdmin(Principal principal, @PathVariable String adminToAdd) {
        logger.info("Entered REST addAdmin...");
        String safeInput = policy.sanitize(adminToAdd);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.POST);
    }

    /**
     * Delete a member from the admin group.
     */
    @PostMapping(value = "/{adminToRemove}/removeAdmin")
    public ResponseEntity<String> removeAdmin(Principal principal,
            @PathVariable String adminToRemove) {
        logger.info("Entered REST deleteAdmin...");
        String safeInput = policy.sanitize(adminToRemove);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Remove a member from multiple groups.
     */
    @PostMapping(value = "/{groupings}/{userToDelete}/removeFromGroups")
    public ResponseEntity<String> removeFromGroups(Principal principal,
            @PathVariable String groupings,
            @PathVariable String userToDelete) {
        logger.info("Entered REST removeFromGroups...");
        String safeGroupings = policy.sanitize(groupings);
        String safeUserToDelete = policy.sanitize(userToDelete);
        String uri = String.format(API_2_1_BASE + "/admins/%s/%s", safeGroupings, safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Wipe the grouping clean of all members and reset the groupings preferences.
     */
    @PostMapping(value = "/{path}/{include}/{exclude}/resetGroup")
    public ResponseEntity<String> resetGroup(Principal principal,
            @PathVariable String path,
            @PathVariable String include,
            @PathVariable String exclude) {
        logger.info("Entered REST resetGroup...");
        String safePath = policy.sanitize(path);
        String safeInclude = policy.sanitize(include);
        String safeExclude = policy.sanitize(exclude);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/%s/%s/resetGroup", safePath, safeInclude, safeExclude);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Get a member's attributes based off username.
     *
     * @return Map of user attributes
     */
    @GetMapping(value = "/members/{uid}")
    @ResponseBody
    public ResponseEntity<String> memberAttributes(Principal principal, @PathVariable String uid) {
        logger.info("Entered REST memberAttributes...");
        String safeInput = policy.sanitize(uid);
        String uri = String.format(API_2_1_BASE + "/members/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get a list of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/groupings")
    public ResponseEntity<String> membershipResults(Principal principal) {
        logger.info("Entered REST membershipResults...");
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get the number of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships")
    public ResponseEntity<String> numberOfMemberships(Principal principal) {
        logger.info("Entered REST numberOfMemberships...");
        String uri = String.format(API_2_1_BASE + "/groupings/%s/memberships", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    @GetMapping(value = "/members/{uid}/groupings",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> membershipAssignment(Principal principal,
            @PathVariable String uid) {
        logger.info("Entered REST MembershipAssignment...");
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", uid);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get a list of grouping paths that the current user can opt into.
     */
    @GetMapping(value = "/groupings/optInGroups")
    public ResponseEntity<String> optInGroups(Principal principal) {
        logger.info("Entered REST optInGroups...");
        String uri = String.format(API_2_1_BASE + "/groupings/optInGroups/%s", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Add yourself to the include group of grouping at path.
     */
    @PostMapping(value = "/{grouping}/optIn")
    public ResponseEntity<String> optIn(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optIn...");
        String safeGrouping = policy.sanitize(grouping);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s/self", safeGrouping, principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Add yourself to the exclude group of grouping at path.
     */
    @PostMapping(value = "/{grouping}/optOut")
    public ResponseEntity<String> optOut(Principal principal, @PathVariable String grouping) {
        logger.info("Entered REST optOut...");
        String safeGrouping = policy.sanitize(grouping);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s/self", safeGrouping, principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Add a list of usersToAdd to include group of grouping at path.
     */
    @PostMapping(value = "/{groupingPath}/{usersToAdd}/addMembersToIncludeGroup")
    public ResponseEntity<String> addMembersToIncludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String usersToAdd) {
        logger.info("Entered REST addMembersToIncludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeUsersToAdd = policy.sanitize(usersToAdd);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s", safeGroupingPath,
                safeUsersToAdd);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Add a list of usersToAdd to exclude group of grouping at path.
     */
    @PostMapping(value = "/{groupingPath}/{usersToAdd}/addMembersToExcludeGroup")
    public ResponseEntity<String> addMembersToExcludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String usersToAdd) {
        logger.info("Entered REST addMembersToExcludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeUsersToAdd = policy.sanitize(usersToAdd);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s", safeGroupingPath,
                safeUsersToAdd);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Remove a list of users from include group of grouping at path.
     */
    @PostMapping(value = "/{groupingPath}/{usersToDelete}/removeMembersFromIncludeGroup")
    public ResponseEntity<String> removeMembersFromIncludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String usersToDelete) {
        logger.info("Entered REST deleteMembersFromIncludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeUserToDelete = policy.sanitize(usersToDelete);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/includeMembers/%s", safeGroupingPath,
                        safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Remove a list of users from exclude group of grouping at path.
     */
    @PostMapping(value = "/{groupingPath}/{usersToDelete}/removeMembersFromExcludeGroup")
    public ResponseEntity<String> removeMembersFromExcludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String usersToDelete) {
        logger.info("Entered REST deleteMembersFromExcludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeUserToDelete = policy.sanitize(usersToDelete);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/excludeMembers/%s", safeGroupingPath,
                        safeUserToDelete);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Request a list of grouping paths owned by principal.
     */
    @GetMapping(value = "/owners/groupings")
    public ResponseEntity<String> groupingsOwned(Principal principal) {
        logger.info("Entered REST GroupingAssignment...");
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Request the number of grouping paths owned by principal.
     */
    @GetMapping(value = "/owners/grouping")
    public ResponseEntity<String> numberOfGroupings(Principal principal) {
        logger.info("Entered REST numberOfGroupings...");
        String uri = String.format(API_2_1_BASE + "/owners/%s/grouping", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Check if principle is an owner, of any grouping.
     */
    @GetMapping(value = "/owners")
    public ResponseEntity<String> hasOwnerPrivs(Principal principal) {
        logger.info("Entered REST isOwner...");
        String uri = String.format(API_2_1_BASE + "/owners", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get a list of groupings owned by uid.
     */
    @GetMapping(value = "/owners/{uid}/groupings")
    public ResponseEntity<String> groupingsOwnedUid(Principal principal, @PathVariable String uid) {
        logger.info("Entered REST GroupingAssignment...");

        String safeUid = policy.sanitize(uid);
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", safeUid);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Give ownership of grouping at grouping path to newOwner. A user with owner privileges has
     * read and write privileges
     * of a grouping.
     */
    @PostMapping(value = "/{groupingPath}/{newOwner}/assignOwnership")
    public ResponseEntity<String> assignOwnership(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String newOwner) {
        logger.info("Entered REST assignOwnership...");
        String safeGrouping = policy.sanitize(groupingPath);
        String safeNewOwner = policy.sanitize(newOwner);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGrouping, safeNewOwner);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Cancel owner privileges of ownerToRemove for the grouping at groupingPath.
     */
    @PostMapping(value = "/{groupingPath}/{ownerToRemove}/removeOwnerships")
    public ResponseEntity<String> removeOwnerships(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeOwnerships...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeOwnerToRemove = policy.sanitize(ownerToRemove);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/removeOwners/%s", safeGroupingPath, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Fetch a page of the specified Grouping.
     */
    @GetMapping(value = "/groupings/{path:.+}")
    // todo getGrouping
    public ResponseEntity<String> grouping(Principal principal,
            @PathVariable String path,
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
     * Update the description of a grouping at path.
     */
    @PutMapping(value = "/groupings/{path}/description")
    public ResponseEntity<String> updateDescription(Principal principal,
            @PathVariable String path,
            @RequestBody(required = false) String description) {
        logger.info("Entered REST updateDescription...");
        String safePath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", safePath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, description, HttpMethod.PUT);
    }

    /**
     * Allow an owner of a Grouping to enable that a Grouping connected to a given sync destination.
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/enable")
    public ResponseEntity<String> enableSyncDest(Principal principal,
            @PathVariable String path,
            @PathVariable String syncDestId) {
        logger.info("Entered REST enableSyncDest...");
        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);
        return changeSyncDest(safeGrouping, principal.getName(), safeSyncDestId, true);
    }

    /**
     * This allows an owner of a Grouping to disable that a Grouping connected to a given sync
     * destination.
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/disable")
    public ResponseEntity<String> disableSyncDest(Principal principal,
            @PathVariable String path,
            @PathVariable String syncDestId) {
        logger.info("Entered REST disableSyncDest...");
        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);
        return changeSyncDest(safeGrouping, principal.getName(), safeSyncDestId, false);
    }

    /**
     * Enable or disable a users ability to optIn to a grouping at groupingPath.
     */
    @PostMapping(value = "/{groupingPath}/{optInOn}/setOptIn")
    public ResponseEntity<String> setOptIn(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable boolean optInOn) {
        logger.info("Entered REST setOptIn...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        return changePreference(safeGroupingPath, principal.getName(), OPT_IN, optInOn);
    }

    /**
     * Enable or disable a users ability to opt out of grouping at groupingPath.
     */
    @PostMapping(value = "/{grouping}/{optOutOn}/setOptOut")
    public ResponseEntity<String> setOptOut(Principal principal,
            @PathVariable String grouping,
            @PathVariable boolean optOutOn) {

        String safeGrouping = policy.sanitize(grouping);

        logger.info("Entered REST setOptOut...");
        return changePreference(safeGrouping, principal.getName(), OPT_OUT, optOutOn);
    }

    /**
     * Fetch a list of supported sync destinations for grouping at path.
     */
    @GetMapping(value = "/groupings/{path}/syncDestinations")
    public ResponseEntity<String> allSyncDestinations(Principal principal, @PathVariable String path) {
        logger.info("Entered REST getAllSyncDestinations...");
        String safePath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/syncDestinations", safePath);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    ///////////////////////////////////////////////////////////////////////
    // Helper Functions
    //////////////////////////////////////////////////////////////////////

    private ResponseEntity<String> changePreference(String grouping, String username, String preference, Boolean isOn) {
        String ending = "disable";
        if (isOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/preferences/%s/%s", grouping, preference, ending);
        return httpRequestService.makeApiRequest(username, uri, HttpMethod.PUT);
    }

    private ResponseEntity<String> changeSyncDest(String grouping, String username, String syncDest, Boolean isOn) {
        String ending = "disable";
        if (isOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/syncDests/%s/%s", grouping, syncDest, ending);
        return httpRequestService.makeApiRequest(username, uri, HttpMethod.PUT);
    }
}
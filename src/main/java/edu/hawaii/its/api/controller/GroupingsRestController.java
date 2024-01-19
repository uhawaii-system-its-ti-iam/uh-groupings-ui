package edu.hawaii.its.api.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.util.UriComponentsBuilder;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.configuration.Realm;
import edu.hawaii.its.groupings.exceptions.ApiServerHandshakeException;

@RestController
@RequestMapping("/api/groupings")
public class GroupingsRestController {

    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);
    private final PolicyFactory policy;

    @Value("${app.groupings.controller.uuid}")
    private String uuid;

    @Value("${url.api.2.1.base}")
    private String API_2_1_BASE;

    @Value("${groupings.api.opt_in}")
    private String OPT_IN;

    @Value("${groupings.api.opt_out}")
    private String OPT_OUT;

    @Value("${app.api.handshake.enabled:true}")
    private Boolean API_HANDSHAKE_ENABLED = true;

    @Autowired
    private UserContextService userContextService;

    /*
     * Checks to make sure that the API is running and that there are no issues with the overrides file.
     * Gets the active profiles and only runs the tests the active profile relies on the API.
     */
    @Value("${groupings.api.check}")
    private String CREDENTIAL_CHECK_USER;

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private Realm realm;

    // Constructor.
    public GroupingsRestController() {
        policy = Sanitizers.FORMATTING;
    }

    /*
     * Checks to make sure that the API is running and that there are no issues with
     * the overrides file. Gets the active profiles and only runs the tests the
     * active profile relies on the API.
     */
    @PostConstruct
    public void init() {
        Assert.hasLength(uuid, "Property 'app.groupings.controller.uuid' is required.");
        logger.info("API_HANDSHAKE_ENABLED: " + API_HANDSHAKE_ENABLED);
        Assert.notNull(API_HANDSHAKE_ENABLED, "Property 'app.api.handshake.enabled' is required.");

        if (shouldDoApiHandshake()) {
            doApiHandshake();
        }

        logger.info(getClass().getSimpleName() + " started.");
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("University of Hawaii UHGroupings");
    }

    @GetMapping(value = "/groupingAdmins")
    public ResponseEntity<String> groupingAdmins(Principal principal) {
        logger.info("Entered REST groupingAdmins...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = API_2_1_BASE + "/grouping-admins";
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/allGroupings")
    public ResponseEntity<String> allGroupings(Principal principal) {
        logger.info("Entered REST allGroupings...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = API_2_1_BASE + "/all-groupings";
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }
    
    @PostMapping(value = "/groupings/group")
    @ResponseBody
    public ResponseEntity<String> getGrouping(@RequestBody(required = true) List<String> groupPaths,
                                              @RequestParam(required = true) Integer page,
                                              @RequestParam(required = true) Integer size,
                                              @RequestParam(required = true) String sortString,
                                              @RequestParam(required = true) Boolean isAscending) {
        logger.info("Entered REST getGrouping...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        Map<String, String> params = mapGroupingParameters(page, size, sortString, isAscending);
        String baseUri = API_2_1_BASE + "/groupings/group";
        String uri = buildUriWithParams(baseUri, params);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, groupPaths, HttpMethod.POST);
    }

    @GetMapping(value = "/groupings/{groupPath}/description")
    public ResponseEntity<String> getGroupingDescription(@PathVariable String groupPath) {
        logger.info("Entered REST getGroupingDescription...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", policy.sanitize(groupPath));
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/groupings/{groupPath}/groupings-sync-destinations")
    public ResponseEntity<String> getGroupingSyncDest(Principal principal, @PathVariable String groupPath) {
        logger.info("Entered REST getGroupingSyncDest...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/groupings/%s/groupings-sync-destinations", policy.sanitize(groupPath));
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/groupings/{groupPath}/opt-attributes")
    public ResponseEntity<String> getGroupingOptAttributes(@PathVariable String groupPath) {
        logger.info("Entered REST getGroupingOptAttributes...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/groupings/%s/opt-attributes", policy.sanitize(groupPath));
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Check if principle is an administrator.
     */
    @GetMapping(value = "/admins")
    public ResponseEntity<String> hasAdminPrivs(Principal principal) {
        logger.info("Entered REST hasAdminPrivs...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/admins", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Add a member to the admin group.
     */
    @PostMapping(value = "/{adminToAdd}/addAdmin")
    public ResponseEntity<String> addAdmin(@PathVariable String adminToAdd) {
        logger.info("Entered REST addAdmin...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeInput = policy.sanitize(adminToAdd);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.POST);
    }

    /**
     * Delete a member from the admin group.
     */
    @PostMapping(value = "/{adminToRemove}/removeAdmin")
    public ResponseEntity<String> removeAdmin(@PathVariable String adminToRemove) {
        logger.info("Entered REST removeAdmin...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeInput = policy.sanitize(adminToRemove);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Remove a member from multiple groups.
     */
    @PostMapping(value = "/{groupings}/{userToDelete}/removeFromGroups")
    public ResponseEntity<String> removeFromGroups(
            @PathVariable String groupings,
            @PathVariable String userToDelete) {
        logger.info("Entered REST removeFromGroups...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupings = policy.sanitize(groupings);
        String safeUserToDelete = policy.sanitize(userToDelete);
        String uri = String.format(API_2_1_BASE + "/admins/%s/%s", safeGroupings, safeUserToDelete);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Wipe the grouping clean of all members and reset the groupings preferences.
     */
    @PostMapping(value = "/{path}/{include}/{exclude}/resetGroup")
    public ResponseEntity<String> resetGroup(
            @PathVariable String path,
            @PathVariable String include,
            @PathVariable String exclude) {
        logger.info("Entered REST resetGroup...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safePath = policy.sanitize(path);
        String safeInclude = policy.sanitize(include);
        String safeExclude = policy.sanitize(exclude);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/%s/%s/reset-group", safePath, safeInclude, safeExclude);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Get a list of invalid uhIdentifiers given a list of uhIdentifiers.
     */
    @PostMapping(value = "/members/invalid")
    @ResponseBody
    public ResponseEntity<String> invalidUhIdentifiers(@RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST invalidUhIdentifiers...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members/invalid");
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeInput, HttpMethod.POST);
    }

    /**
     * Get a list of invalid uhIdentifiers given a list of uhIdentifiers asynchronously.
     */
    @PostMapping(value = "/members/invalidAsync")
    @ResponseBody
    public ResponseEntity<String> invalidUhIdentifiersAsync(@RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST invalidUhIdentifiersAsync...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members/invalid/async");
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeInput, HttpMethod.POST);
    }

    /**
     * Remove all members from the include group.
     */
    @PostMapping(value = "{groupingPath}/resetIncludeGroup")
    public ResponseEntity<String> resetIncludeGroup(@PathVariable String groupingPath) {
        logger.info("Entered REST resetIncludeGroup...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the include group asynchronously.
     */
    @PostMapping(value = "{groupingPath}/resetIncludeGroupAsync")
    public ResponseEntity<String> resetIncludeGroupAsync(@PathVariable String groupingPath) {
        logger.info("Entered REST resetIncludeGroupAsync...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include/async", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the exclude group.
     */
    @PostMapping(value = "{groupingPath}/resetExcludeGroup")
    public ResponseEntity<String> resetExcludeGroup(@PathVariable String groupingPath) {
        logger.info("Entered REST resetExcludeGroup...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the exclude group asynchronously.
     */
    @PostMapping(value = "{groupingPath}/resetExcludeGroupAsync")
    public ResponseEntity<String> resetExcludeGroupAsync(@PathVariable String groupingPath) {
        logger.info("Entered REST resetExcludeGroupAsync...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude/async", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Get a member's attributes based off username.
     */
    @GetMapping(value = "/members/{uhIdentifier}")
    @ResponseBody
    public ResponseEntity<String> memberAttributes(@PathVariable String uhIdentifier) {
        logger.info("Entered REST memberAttributes...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeInput = policy.sanitize(uhIdentifier);
        String uri = String.format(API_2_1_BASE + "/members/%s", safeInput);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Get a member's attributes based off username.
     */
    @PostMapping(value = "/members")
    @ResponseBody
    public ResponseEntity<String> membersAttributes(@RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST membersAttributes...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members");
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeInput, HttpMethod.POST);
    }

    /**
     * Get a list of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships")
    public ResponseEntity<String> membershipResults() {
        logger.info("Entered REST membershipResults...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/members/%s/memberships", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Get the number of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships/count")
    public ResponseEntity<String> getNumberOfMemberships() {
        logger.info("Entered REST getNumberOfMemberships...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/members/%s/memberships/count", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/members/{uhIdentifier}/groupings",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> managePersonResults(@PathVariable String uhIdentifier) {
        logger.info("Entered REST managePersonResults...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", uhIdentifier);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Get a list of grouping paths that the current user can opt into.
     */
    @GetMapping(value = "/groupings/optInGroups")
    public ResponseEntity<String> optInGroups() {
        logger.info("Entered REST optInGroups...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/groupings/members/%s/opt-in-groups", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Add yourself to the include group of grouping at path.
     */
    @PostMapping(value = "/{grouping}/optIn")
    public ResponseEntity<String> optIn(@PathVariable String grouping) {
        logger.info("Entered REST optIn...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGrouping = policy.sanitize(grouping);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/include-members/%s/self", safeGrouping,
                        currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.PUT);
    }

    /**
     * Add yourself to the exclude group of grouping at path.
     */
    @PostMapping(value = "/{grouping}/optOut")
    public ResponseEntity<String> optOut(@PathVariable String grouping) {
        logger.info("Entered REST optOut...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGrouping = policy.sanitize(grouping);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/exclude-members/%s/self", safeGrouping,
                        currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to include group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/addIncludeMembers")
    public ResponseEntity<String> addIncludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST addIncludeMembers...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to include group of grouping at path asynchronously.
     */
    @PutMapping(value = "/{groupingPath}/addIncludeMembersAsync")
    public ResponseEntity<String> addIncludeMembersAsync(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST addIncludeMembersAsync...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include-members/async", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to exclude group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/addExcludeMembers")
    public ResponseEntity<String> addExcludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST addExcludeMembers...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to exclude group of grouping at path asynchronously.
     */
    @PutMapping(value = "/{groupingPath}/addExcludeMembersAsync")
    public ResponseEntity<String> addExcludeMembersAsync(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST addExcludeMembersAsync...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude-members/async", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Remove a list of uhIdentifiers from include group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/removeIncludeMembers")
    public ResponseEntity<String> removeIncludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST removeIncludeMembers...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/include-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeUhIdentifiers,
                HttpMethod.DELETE);
    }

    /**
     * Remove a list of uhIdentifiers from exclude group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/removeExcludeMembers")
    public ResponseEntity<String> removeExcludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST removeExcludeMembers...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/exclude-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, safeUhIdentifiers,
                HttpMethod.DELETE);
    }

    /**
     * Request a list of grouping paths owned by principal.
     */
    @GetMapping(value = "/owners/groupings")
    public ResponseEntity<String> ownerGroupings() {
        logger.info("Entered REST ownerGroupings...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Request the number of grouping paths owned by principal.
     */
    @GetMapping(value = "/owners/groupings/count")
    public ResponseEntity<String> getNumberOfGroupings() {
        logger.info("Entered REST getNumberOfGroupings...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings/count", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Check if principle is an owner, of any grouping.
     */
    @GetMapping(value = "/owners")
    public ResponseEntity<String> hasOwnerPrivs(Principal principal) {
        logger.info("Entered REST hasOwnerPrivs...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/owners", currentUsername);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Get grouping owners in groupingPath.
     */
    @GetMapping(value = "/grouping/{groupingPath}/owners")
    public ResponseEntity<String> groupingOwners(@PathVariable String groupingPath) {
        logger.info("Entered REST groupingOwners...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String uri = String.format(API_2_1_BASE + "/grouping/%s/owners", groupingPath);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Give ownership of grouping at grouping path to newOwner. A user with owner privileges has
     * read and write privileges
     * of a grouping.
     */
    @PostMapping(value = "/{groupingPath}/{newOwner}/addOwnerships")
    public ResponseEntity<String> addOwnerships(
            @PathVariable String groupingPath,
            @PathVariable String newOwner) {
        logger.info("Entered REST addOwnerships...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGrouping = policy.sanitize(groupingPath);
        String safeNewOwner = policy.sanitize(newOwner);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGrouping, safeNewOwner);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.PUT);
    }

    /**
     * Cancel owner privileges of ownerToRemove for the grouping at groupingPath.
     */
    @PostMapping(value = "/{groupingPath}/{ownerToRemove}/removeOwnerships")
    public ResponseEntity<String> removeOwnerships(
            @PathVariable String groupingPath,
            @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeOwnerships...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeOwnerToRemove = policy.sanitize(ownerToRemove);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGroupingPath, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.DELETE);
    }

    /**
     * Fetch a page of the specified Grouping.
     */
    @GetMapping(value = "/groupings/{path:.+}")
    public ResponseEntity<String> getGrouping(@PathVariable String path,
                                              @RequestParam(required = true) Integer page,
                                              @RequestParam(required = true) Integer size,
                                              @RequestParam(required = true) String sortString,
                                              @RequestParam(required = true) Boolean isAscending) {
        logger.info("Entered REST getGrouping...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        Map<String, String> params = mapGroupingParameters(page, size, sortString, isAscending);
        String baseUri = String.format(API_2_1_BASE + "/groupings/%s", path);
        String uri = buildUriWithParams(baseUri, params);

        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Update the description of a grouping at path.
     */
    @PutMapping(value = "/groupings/{path}/description")
    public ResponseEntity<String> updateDescription(
            @PathVariable String path,
            @RequestBody(required = false) String description) {
        logger.info("Entered REST updateDescription...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safePath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", safePath);
        return httpRequestService.makeApiRequestWithBody(currentUsername, uri, description, HttpMethod.PUT);
    }

    /**
     * Allow an owner of a Grouping to enable that a Grouping connected to a given sync destination.
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/enable")
    public ResponseEntity<String> enableSyncDest(
            @PathVariable String path,
            @PathVariable String syncDestId) {
        logger.info("Entered REST enableSyncDest...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);
        return changeSyncDest(safeGrouping, currentUsername, safeSyncDestId, true);
    }

    /**
     * This allows an owner of a Grouping to disable that a Grouping connected to a given sync
     * destination.
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/disable")
    public ResponseEntity<String> disableSyncDest(
            @PathVariable String path,
            @PathVariable String syncDestId) {
        logger.info("Entered REST disableSyncDest...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);
        return changeSyncDest(safeGrouping, currentUsername, safeSyncDestId, false);
    }

    /**
     * Enable or disable a users ability to optIn to a grouping at groupingPath.
     */
    @PostMapping(value = "/{groupingPath}/{optInOn}/setOptIn")
    public ResponseEntity<String> setOptIn(
            @PathVariable String groupingPath,
            @PathVariable boolean optInOn) {
        logger.info("Entered REST setOptIn...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGroupingPath = policy.sanitize(groupingPath);
        return changePreference(safeGroupingPath, currentUsername, OPT_IN, optInOn);
    }

    /**
     * Enable or disable a users ability to opt out of grouping at groupingPath.
     */
    @PostMapping(value = "/{grouping}/{optOutOn}/setOptOut")
    public ResponseEntity<String> setOptOut(
            @PathVariable String grouping,
            @PathVariable boolean optOutOn) {
        logger.info("Entered REST setOptOut...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeGrouping = policy.sanitize(grouping);
        return changePreference(safeGrouping, currentUsername, OPT_OUT, optOutOn);
    }

    /**
     * Checks if the owner of a grouping is the sole owner
     */
    @GetMapping(value = "/{path:.+}/owners/{uidToCheck}")
    public ResponseEntity<String> isSoleOwner(@PathVariable String path,
                                              @PathVariable String uidToCheck) {
        logger.info("Entered REST isSoleOwner...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String baseUri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", path, uidToCheck);

        return httpRequestService.makeApiRequest(currentUsername, baseUri, HttpMethod.GET);
    }

    /**
     * Fetch a list of supported sync destinations for grouping at path.
     */
    @GetMapping(value = "/groupings/{path}/sync-destinations")
    public ResponseEntity<String> allSyncDestinations(@PathVariable String path) {
        logger.info("Entered REST getAllSyncDestinations...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safePath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/sync-destinations", safePath);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    /**
     * Get async job result.
     */
    @GetMapping(value = "/jobs/{jobId}")
    public ResponseEntity<String> getAsyncJobResult(@PathVariable String jobId) {
        logger.debug("Entered REST getAsyncJobResult...");
        String currentUsername = policy.sanitize(userContextService.getCurrentUsername());
        String safeJobId = policy.sanitize(jobId);
        String uri = String.format(API_2_1_BASE + "/jobs/%s", safeJobId);
        return httpRequestService.makeApiRequest(currentUsername, uri, HttpMethod.GET);
    }

    ///////////////////////////////////////////////////////////////////////
    // Helper Methods
    //////////////////////////////////////////////////////////////////////

    public List<String> sanitizeList(List<String> data) {
        List<String> sanitizedList = new ArrayList<>();
        String sanitizedString;

        for (int i = 0; i < data.size(); i++) {
            sanitizedString = policy.sanitize(data.get(i));

            if (!(sanitizedString.isEmpty())) {
                sanitizedList.add(sanitizedString);
            }
        }

        return sanitizedList;
    }

    public Map<String, String> mapGroupingParameters(Integer page, Integer size, String sortString,
                                                     Boolean isAscending) {
        Map<String, String> params = new HashMap<>();
        params.put("page", Integer.toString(page));
        params.put("size", Integer.toString(size));
        params.put("sortString", sortString);
        params.put("isAscending", Boolean.toString(isAscending));
        return params;
    }

    public String buildUriWithParams(String baseUri, Map<String, String> params) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUri);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriComponentsBuilder.encode().toUriString();
    }

    private ResponseEntity<String> changePreference(String grouping, String uhIdentifier, String preference,
                                                    Boolean isOn) {
        String ending = "disable";
        if (isOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/preference/%s/%s", grouping, preference, ending);
        return httpRequestService.makeApiRequest(uhIdentifier, uri, HttpMethod.PUT);
    }

    private ResponseEntity<String> changeSyncDest(String grouping, String uhIdentifier, String syncDest, Boolean isOn) {
        String ending = "disable";
        if (isOn) {
            ending = "enable";
        }
        String uri = String.format(API_2_1_BASE + "/groupings/%s/sync-destination/%s/%s", grouping, syncDest, ending);
        return httpRequestService.makeApiRequest(uhIdentifier, uri, HttpMethod.PUT);
    }

    protected Boolean shouldDoApiHandshake() {
        if (!API_HANDSHAKE_ENABLED) {
            logger.info("API handshake disabled.");
            return false;
        }

        return !realm.isAnyProfileActive("default", "localTest");
    }

    protected void doApiHandshake() {
        if (shouldDoApiHandshake()) {
            boolean success = false;
            try {
                final String uhIdentifier = CREDENTIAL_CHECK_USER;
                final String url = API_2_1_BASE + "/";
                success = httpRequestService.makeApiRequest(uhIdentifier, url, HttpMethod.GET)
                        .getStatusCode()
                        .is2xxSuccessful();
            } catch (Exception e) {
                logger.debug("API Handshack error: ", e);
            }

            if (!success) {
                String text = "Please start the UH Groupings API first.";
                throw new ApiServerHandshakeException(text);
            }
        }
    }

    public HttpRequestService getHttpRequestService() {
        return httpRequestService;
    }

    public void setHttpRequestService(HttpRequestService httpRequestService) {
        this.httpRequestService = httpRequestService;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }
}

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
    private final PolicyFactory policy = Sanitizers.FORMATTING;;

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

    private final UserContextService userContextService;

    /*
     * Checks to make sure that the API is running and that there are no issues with the overrides file.
     * Gets the active profiles and only runs the tests the active profile relies on the API.
     */
    @Value("${groupings.api.check}")
    private String CREDENTIAL_CHECK_USER;

    private HttpRequestService httpRequestService;

    private Realm realm;

    // Constructor.
    public GroupingsRestController(UserContextService userContextService, HttpRequestService httpRequestService, Realm realm) {
        this.userContextService = userContextService;
        this.httpRequestService = httpRequestService;
        this.realm = realm;
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

    @GetMapping(value = "/groupings/admins")
    public ResponseEntity<String> groupingAdmins() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST groupingAdmins - currentUid: %s", currentUid));
        String uri = API_2_1_BASE + "/groupings/admins";
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/groupings")
    public ResponseEntity<String> allGroupings() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST allGroupings - currentUid: %s", currentUid));
        String uri = API_2_1_BASE + "/groupings";
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    @PostMapping(value = "/groupings/group")
    @ResponseBody
    public ResponseEntity<String> getGrouping(@RequestBody(required = true) List<String> groupPaths,
            @RequestParam(required = true) Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = true) String sortBy,
            @RequestParam(required = true) Boolean isAscending) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getGrouping - currentUid: %s, groupPaths: %s, page: %d, size: %d, sortBy: %s, isAscending: %b",
                currentUid, groupPaths, page, size, sortBy, isAscending));
        Map<String, String> params = mapGroupingParameters(page, size, sortBy, isAscending);
        String baseUri = API_2_1_BASE + "/groupings/group";
        String uri = buildUriWithParams(baseUri, params);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, groupPaths, HttpMethod.POST);
    }

    @GetMapping(value = "/groupings/{groupPath}/description")
    public ResponseEntity<String> getGroupingDescription(@PathVariable String groupPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getGroupingDescription - currentUid: %s, groupPath: %s",
                currentUid, groupPath));
        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", policy.sanitize(groupPath));
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/groupings/{groupPath}/groupings-sync-destinations")
    public ResponseEntity<String> getGroupingSyncDest(Principal principal, @PathVariable String groupPath) {
        String principalName = policy.sanitize(principal.getName());
        logger.info(String.format("Entered REST getGroupingSyncDest - principalName: %s, groupPath: %s", principalName, groupPath));
        String uri = String.format(API_2_1_BASE + "/groupings/%s/groupings-sync-destinations", policy.sanitize(groupPath));
        return httpRequestService.makeApiRequest(principalName, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/groupings/{groupPath}/opt-attributes")
    public ResponseEntity<String> getGroupingOptAttributes(@PathVariable String groupPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getGroupingOptAttributes - currentUid: %s, groupPath: %s",
                currentUid, groupPath));
        String uri = String.format(API_2_1_BASE + "/groupings/%s/opt-attributes", policy.sanitize(groupPath));
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Check if principle is an administrator.
     */
    @GetMapping(value = "/members/{uhIdentifier}/is-admin")
    public ResponseEntity<String> hasAdminPrivs(@PathVariable String uhIdentifier) {
        String safeUhIdentifier = policy.sanitize(uhIdentifier);
        logger.info(String.format("Entered REST hasAdminPrivs - uhIdentifier: %s", safeUhIdentifier));
        String uri = String.format(API_2_1_BASE + "/members/%s/is-admin", safeUhIdentifier);
        return httpRequestService.makeApiRequest(uri, HttpMethod.GET);
    }

    /**
     * Add a member to the admin group.
     */
    @PostMapping(value = "/{adminToAdd}/addAdmin")
    public ResponseEntity<String> addAdmin(@PathVariable String adminToAdd) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST addAdmin - currentUid: %s, adminToAdd: %s",
                currentUid, adminToAdd));
        String safeInput = policy.sanitize(adminToAdd);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.POST);
    }

    /**
     * Delete a member from the admin group.
     */
    @PostMapping(value = "/{adminToRemove}/removeAdmin")
    public ResponseEntity<String> removeAdmin(@PathVariable String adminToRemove) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST removeAdmin - currentUid: %s, adminToRemove: %s",
                currentUid, adminToRemove));
        String safeInput = policy.sanitize(adminToRemove);
        String uri = String.format(API_2_1_BASE + "/admins/%s", safeInput);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Remove a member from multiple groups.
     */
    @PostMapping(value = "/{groupings}/{userToDelete}/removeFromGroups")
    public ResponseEntity<String> removeFromGroups(
            @PathVariable String groupings,
            @PathVariable String userToDelete) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST removeFromGroups - currentUid: %s, groupings: %s, userToDelete: %s",
                currentUid, groupings, userToDelete));
        String safeGroupings = policy.sanitize(groupings);
        String safeUserToDelete = policy.sanitize(userToDelete);
        String uri = String.format(API_2_1_BASE + "/admins/%s/%s", safeGroupings, safeUserToDelete);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Wipe the grouping clean of all members and reset the groupings preferences.
     */
    @PostMapping(value = "/{path}/{include}/{exclude}/resetGroup")
    public ResponseEntity<String> resetGroup(
            @PathVariable String path,
            @PathVariable String include,
            @PathVariable String exclude) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST resetGroup - currentUid: %s, path: %s, include: %s, exclude: %s",
                currentUid, path, include, exclude));
        String safePath = policy.sanitize(path);
        String safeInclude = policy.sanitize(include);
        String safeExclude = policy.sanitize(exclude);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/%s/%s/reset-group", safePath, safeInclude, safeExclude);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the include group.
     */
    @PostMapping(value = "{groupingPath}/resetIncludeGroup")
    public ResponseEntity<String> resetIncludeGroup(@PathVariable String groupingPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST resetIncludeGroup - currentUid: %s, groupingPath: %s",
                currentUid, groupingPath));
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the include group asynchronously.
     */
    @PostMapping(value = "{groupingPath}/resetIncludeGroupAsync")
    public ResponseEntity<String> resetIncludeGroupAsync(@PathVariable String groupingPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST resetIncludeGroupAsync - currentUid: %s, groupingPath: %s",
                currentUid, groupingPath));
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include/async", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the exclude group.
     */
    @PostMapping(value = "{groupingPath}/resetExcludeGroup")
    public ResponseEntity<String> resetExcludeGroup(@PathVariable String groupingPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST resetExcludeGroup - currentUid: %s, groupingPath: %s",
                currentUid, groupingPath));
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the exclude group asynchronously.
     */
    @PostMapping(value = "{groupingPath}/resetExcludeGroupAsync")
    public ResponseEntity<String> resetExcludeGroupAsync(@PathVariable String groupingPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST resetExcludeGroupAsync - currentUid: %s, groupingPath: %s",
                currentUid, groupingPath));
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude/async", safeGroupingPath);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Get a member's attributes based off uid.
     * Get a list of invalid uhIdentifiers given a list of uhIdentifiers.
     */
    @PostMapping(value = "/members")
    @ResponseBody
    public ResponseEntity<String> memberAttributeResults(@RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST memberAttributeResults - currentUid: %s, uhIdentifiers: %s",
                currentUid, uhIdentifiers));
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members");
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeInput, HttpMethod.POST);
    }

    /**
     * Get a member's attributes based off uid asynchronously.
     * Get a list of invalid uhIdentifiers given a list of uhIdentifiers asynchronously.
     */
    @PostMapping(value = "/members/membersAsync")
    @ResponseBody
    public ResponseEntity<String> memberAttributeResultsAsync(@RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST memberAttributeResultsAsync - currentUid: %s, uhIdentifiers: %s",
                currentUid, uhIdentifiers));
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members/async");
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeInput, HttpMethod.POST);
    }

    /**
     * Get a list of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships")
    public ResponseEntity<String> membershipResults() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST membershipResults - currentUid: %s", currentUid));
        String uri = String.format(API_2_1_BASE + "/members/%s/memberships", currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Get the number of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships/count")
    public ResponseEntity<String> getNumberOfMemberships() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getNumberOfMemberships - currentUid: %s", currentUid));
        String uri = String.format(API_2_1_BASE + "/members/%s/memberships/count", currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    @GetMapping(value = "/members/{uhIdentifier}/groupings",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> manageSubjectResults(@PathVariable String uhIdentifier) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST manageSubjectResults - currentUid: %s, uhIdentifier: %s",
                currentUid, uhIdentifier));
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", uhIdentifier);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Get a list of grouping paths that the current user can opt into.
     */
    @GetMapping(value = "/groupings/optInGroups")
    public ResponseEntity<String> optInGroups() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST optInGroups - currentUid: %s", currentUid));
        String uri = String.format(API_2_1_BASE + "/groupings/members/%s/opt-in-groups", currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Add yourself to the include group of grouping at path.
     */
    @PostMapping(value = "/{grouping}/optIn")
    public ResponseEntity<String> optIn(@PathVariable String grouping) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST optIn - currentUid: %s, grouping: %s", currentUid, grouping));
        String safeGrouping = policy.sanitize(grouping);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/include-members/%s/self", safeGrouping,
                        currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Add yourself to the exclude group of grouping at path.
     */
    @PostMapping(value = "/{grouping}/optOut")
    public ResponseEntity<String> optOut(@PathVariable String grouping) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST optOut - currentUid: %s, grouping: %s", currentUid, grouping));
        String safeGrouping = policy.sanitize(grouping);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/exclude-members/%s/self", safeGrouping,
                        currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to include group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/addIncludeMembers")
    public ResponseEntity<String> addIncludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST addIncludeMembers - currentUid: %s, groupingPath: %s, uhIdentifiers: %s",
                currentUid, groupingPath, uhIdentifiers));
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to include group of grouping at path asynchronously.
     */
    @PutMapping(value = "/{groupingPath}/addIncludeMembersAsync")
    public ResponseEntity<String> addIncludeMembersAsync(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST addIncludeMembersAsync - currentUid: %s, groupingPath: %s, uhIdentifiers: %s",
                currentUid, groupingPath, uhIdentifiers));
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include-members/async", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to exclude group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/addExcludeMembers")
    public ResponseEntity<String> addExcludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST addExcludeMembers - currentUid: %s, groupingPath: %s, uhIdentifiers: %s",
                currentUid, groupingPath, uhIdentifiers));
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Add a list of uhIdentifiers to exclude group of grouping at path asynchronously.
     */
    @PutMapping(value = "/{groupingPath}/addExcludeMembersAsync")
    public ResponseEntity<String> addExcludeMembersAsync(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST addExcludeMembersAsync - currentUid: %s, groupingPath: %s, uhIdentifiers: %s",
                currentUid, groupingPath, uhIdentifiers));
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude-members/async", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeUhIdentifiers, HttpMethod.PUT);
    }

    /**
     * Remove a list of uhIdentifiers from include group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/removeIncludeMembers")
    public ResponseEntity<String> removeIncludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST removeIncludeMembers - currentUid: %s, groupingPath: %s, uhIdentifiers: %s",
                currentUid, groupingPath, uhIdentifiers));
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/include-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeUhIdentifiers,
                HttpMethod.DELETE);
    }

    /**
     * Remove a list of uhIdentifiers from exclude group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/removeExcludeMembers")
    public ResponseEntity<String> removeExcludeMembers(
            @PathVariable String groupingPath,
            @RequestBody List<String> uhIdentifiers) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST removeExcludeMembers - currentUid: %s, groupingPath: %s, uhIdentifiers: %s",
                currentUid, groupingPath, uhIdentifiers));
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUhIdentifiers = sanitizeList(uhIdentifiers);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/exclude-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeUhIdentifiers,
                HttpMethod.DELETE);
    }

    /**
     * Request a list of grouping paths owned by principal.
     */
    @GetMapping(value = "/owners/groupings")
    public ResponseEntity<String> ownerGroupings() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST ownerGroupings - currentUid: %s", currentUid));
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Request the number of grouping paths owned by principal.
     */
    @GetMapping(value = "/owners/groupings/count")
    public ResponseEntity<String> getNumberOfGroupings() {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getNumberOfGroupings - currentUid: %s", currentUid));
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings/count", currentUid);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Check if principle is an owner, of any grouping.
     */
    @GetMapping(value = "/members/{uhIdentifier}/is-owner")
    public ResponseEntity<String> hasOwnerPrivs(@PathVariable String uhIdentifier) {
        String safeUhIdentifier = policy.sanitize(uhIdentifier);
        logger.info(String.format("Entered REST hasOwnerPrivs - uhIdentifier: %s", safeUhIdentifier));
        String uri = String.format(API_2_1_BASE + "/members/%s/is-owner", safeUhIdentifier);
        return httpRequestService.makeApiRequest(uri, HttpMethod.GET);
    }

    /**
     * Get grouping owners in groupingPath.
     */
    @GetMapping(value = "/grouping/{groupingPath}/owners")
    public ResponseEntity<String> groupingOwners(@PathVariable String groupingPath) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST groupingOwners - currentUid: %s, groupingPath: %s",
                currentUid, groupingPath));
        String uri = String.format(API_2_1_BASE + "/grouping/%s/owners", groupingPath);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Give ownership of grouping at grouping path to newOwner. A user with owner privileges has
     * read and write privileges of a grouping.
     */
    @PostMapping(value = "/{groupingPath}/{newOwner}/addOwnerships")
    public ResponseEntity<String> addOwnerships(
            @PathVariable String groupingPath,
            @PathVariable String newOwner) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST addOwnerships - currentUid: %s, groupingPath: %s, newOwner: %s", currentUid, groupingPath, newOwner));
        String safeGrouping = policy.sanitize(groupingPath);
        String safeNewOwner = policy.sanitize(newOwner);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGrouping, safeNewOwner);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Give owner privileges of the grouping at groupingPath to the owner-grouping (newOwner). A user with owner privileges has
     * read and write privileges of a grouping.
     */
    @PostMapping(value = "/{groupingPath}/{newOwner}/addGroupPathOwnerships")
    public ResponseEntity<String> addGroupPathOwnerships(
            @PathVariable String groupingPath,
            @PathVariable String newOwner) {
        logger.info("Entered REST addGroupPathOwnerships...");
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        String safeGrouping = policy.sanitize(groupingPath);
        String safeNewOwner = policy.sanitize(newOwner);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/path-owner/%s", safeGrouping, safeNewOwner);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Cancel owner privileges of ownerToRemove for the grouping at groupingPath.
     */
    @PostMapping(value = "/{groupingPath}/{ownerToRemove}/removeOwnerships")
    public ResponseEntity<String> removeOwnerships(
            @PathVariable String groupingPath,
            @PathVariable String ownerToRemove) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST removeOwnerships - currentUid: %s, groupingPath: %s, ownerToRemove: %s",
                currentUid, groupingPath, ownerToRemove));
        String safeGroupingPath = policy.sanitize(groupingPath);
        String safeOwnerToRemove = policy.sanitize(ownerToRemove);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGroupingPath, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Cancel owner privileges of the owner-grouping (ownerToRemove) for the grouping at groupingPath.
     */
    @PostMapping(value = "/{groupingPath}/{ownerToRemove}/removeGroupPathOwnerships")
    public ResponseEntity<String> removeGroupPathOwnerships(
            @PathVariable String groupingPath,
            @PathVariable String ownerToRemove) {
        logger.info("Entered REST removeGroupPathOwnerships...");
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        String safeGrouping = policy.sanitize(groupingPath);
        String safeOwnerToRemove = policy.sanitize(ownerToRemove);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/path-owner/%s", safeGrouping, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.DELETE);
    }

    /**
     * Update the description of a grouping at path.
     */
    @PutMapping(value = "/groupings/{path}/description")
    public ResponseEntity<String> updateDescription(
            @PathVariable String path,
            @RequestBody(required = false) String description) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST updateDescription - currentUid: %s, path: %s, description: %s",
                currentUid, path, description));
        String safePath = policy.sanitize(path);
        String safeDescription = policy.sanitize(description);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/description", safePath);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, safeDescription, HttpMethod.PUT);
    }


    /**
     * This allows an owner of a Grouping to enable/disable that a Grouping connected to a given sync
     * destination.
     */
    @PostMapping(value = "/groupings/{path}/syncDests/{syncDestId}/{status}")
    public ResponseEntity<String> updateSyncDest(
            @PathVariable String path,
            @PathVariable String syncDestId,
            @PathVariable boolean status) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST updateSyncDest - currentUid: %s, path: %s, syncDestId: %s, status: %b",
                currentUid, path, syncDestId, status));
        String safeGrouping = policy.sanitize(path);
        String safeSyncDestId = policy.sanitize(syncDestId);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/sync-destination/%s/%s", safeGrouping, safeSyncDestId, status);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Enable or disable a users ability to optIn to a grouping at groupingPath.
     */
    @PostMapping(value = "/groupings/{path}/opt-attribute/IN/{status}")
    public ResponseEntity<String> updateOptIn(
            @PathVariable String path,
            @PathVariable boolean status) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST updateOptIn - currentUid: %s, path: %s, status: %b", currentUid, path, status));
        String safeGroupingPath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/opt-attribute/%s/%s", safeGroupingPath, OPT_IN, status);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Enable or disable a users ability to opt out of grouping at groupingPath.
     */
    @PostMapping(value = "/groupings/{path}/opt-attribute/OUT/{status}")
    public ResponseEntity<String> updateOptOut(
            @PathVariable String path,
            @PathVariable boolean status) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST updateOptOut - currentUid: %s, path: %s, status: %b", currentUid, path, status));
        String safeGroupingPath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/opt-attribute/%s/%s", safeGroupingPath, OPT_OUT, status);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.PUT);
    }

    /**
     * Get the number of owners of the group path that contains the owner with uhIdentifier
     */
    @GetMapping(value = "/{path:.+}/owners/{uhIdentifier}/count")
    public ResponseEntity<String> getNumberOfOwners(@PathVariable String path,
            @PathVariable String uhIdentifier) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getNumberOfOwners - currentUid: %s, path: %s, uhIdentifier: %s",
                currentUid, path, uhIdentifier));
        String baseUri = String.format(API_2_1_BASE + "/members/%s/owners/%s/count", path, uhIdentifier);
        return httpRequestService.makeApiRequest(currentUid, baseUri, HttpMethod.GET);
    }

    /**
     * Fetch a list of supported sync destinations for grouping at path.
     */
    @GetMapping(value = "/groupings/{path}/sync-destinations")
    public ResponseEntity<String> allSyncDestinations(@PathVariable String path) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST allSyncDestinations - currentUid: %s, path: %s", currentUid, path));
        String safePath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/sync-destinations", safePath);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
    }

    /**
     * Get async job result.
     */
    @GetMapping(value = "/jobs/{jobId}")
    public ResponseEntity<String> getAsyncJobResult(@PathVariable String jobId) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        logger.info(String.format("Entered REST getAsyncJobResult - currentUid: %s, jobId: %s", currentUid, jobId));
        String safeJobId = policy.sanitize(jobId);
        String uri = String.format(API_2_1_BASE + "/jobs/%s", safeJobId);
        return httpRequestService.makeApiRequest(currentUid, uri, HttpMethod.GET);
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

    public Map<String, String> mapGroupingParameters(Integer page, Integer size, String sortBy,
            Boolean isAscending) {
        Map<String, String> params = new HashMap<>();
        params.put("page", Integer.toString(page));
        params.put("size", Integer.toString(size));
        params.put("sortBy", sortBy);
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
                logger.debug("API handshake error: ", e);
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
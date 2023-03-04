package edu.hawaii.its.api.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.access.UserContextService;
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

import edu.hawaii.its.groupings.access.User;
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

    @Autowired
    private UserContextService userContextService;

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

    @GetMapping(value = "/currentUser")
    public ResponseEntity<User> currentUser() {
        User currentUser = userContextService.getCurrentUser();
        return ResponseEntity.ok().body(currentUser);
    }

    @GetMapping(value = "/adminLists")
    public ResponseEntity<String> adminLists(Principal principal) {
        logger.info("Entered REST adminLists...");
        String principalName = policy.sanitize(principal.getName());
        String uri = API_2_1_BASE + "/admins-and-groupings";
        return httpRequestService.makeApiRequest(principalName, uri, HttpMethod.GET);
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
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/%s/%s/reset-group", safePath, safeInclude, safeExclude);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Get a list of invalid uhIdentifiers given a list of uhIdentifiers.
     */
    @PostMapping(value = "/members/invalid")
    @ResponseBody
    public ResponseEntity<String> invalidUhIdentifiers(Principal principal, @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST memberAttributes...");
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members/invalid");
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, safeInput, HttpMethod.POST);
    }

    /**
     * Remove all members from the include group.
     */
    @PostMapping(value = "{groupingPath}/resetIncludeGroup")
    public ResponseEntity<String> resetIncludeGroup(Principal principal, @PathVariable String groupingPath) {
        logger.info("Entered REST resetIncludeGroup");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include", safeGroupingPath);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Remove all members from the exclude group.
     */
    @PostMapping(value = "{groupingPath}/resetExcludeGroup")
    public ResponseEntity<String> resetExcludeGroup(Principal principal, @PathVariable String groupingPath) {
        logger.info("Entered REST resetExcludeGroup");
        String safeGroupingPath = policy.sanitize(groupingPath);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude", safeGroupingPath);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Get a member's attributes based off username.
     */
    @GetMapping(value = "/members/{uhIdentifier}")
    @ResponseBody
    public ResponseEntity<String> memberAttributes(Principal principal, @PathVariable String uhIdentifier) {
        logger.info("Entered REST memberAttributes...");
        String safeInput = policy.sanitize(uhIdentifier);
        String uri = String.format(API_2_1_BASE + "/members/%s", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get a member's attributes based off username.
     */
    @PostMapping(value = "/members")
    @ResponseBody
    public ResponseEntity<String> membersAttributes(Principal principal, @RequestBody List<String> uhIdentifiers) {
        logger.info("Entered REST memberAttributes...");
        List<String> safeInput = sanitizeList(uhIdentifiers);
        String uri = String.format(API_2_1_BASE + "/members");
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, safeInput, HttpMethod.POST);
    }

    /**
     * Get a list of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships")
    public ResponseEntity<String> membershipResults(Principal principal) {
        logger.info("Entered REST membershipResults...");
        String principalName = policy.sanitize(principal.getName());
        String uri = String.format(API_2_1_BASE + "/members/%s/memberships", principalName);
        return httpRequestService.makeApiRequest(principalName, uri, HttpMethod.GET);
    }

    /**
     * Get a list of all groupings that the uhIdentifier is associated with.
     */
    @GetMapping(value = "/members/{uhIdentifier}/groupings")
    public ResponseEntity<String> managePersonResults(Principal principal, @PathVariable String uhIdentifier) {
        logger.info("Entered REST managePersonResults...");
        String safeInput = policy.sanitize(uhIdentifier);
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", safeInput);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get the number of memberships that the current user is associated with.
     */
    @GetMapping(value = "/members/memberships/count")
    public ResponseEntity<String> numberOfMemberships(Principal principal) {
        logger.info("Entered REST numberOfMemberships...");
        String uri = String.format(API_2_1_BASE + "/members/%s/memberships/count", principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    @GetMapping(value = "/members/{uhIdentifier}/groupings",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> membershipAssignment(Principal principal,
            @PathVariable String uhIdentifier) {
        logger.info("Entered REST MembershipAssignment...");
        String uri = String.format(API_2_1_BASE + "/members/%s/groupings", uhIdentifier);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Get a list of grouping paths that the current user can opt into.
     */
    @GetMapping(value = "/groupings/optInGroups")
    public ResponseEntity<String> optInGroups(Principal principal) {
        logger.info("Entered REST optInGroups...");
        String uri = String.format(API_2_1_BASE + "/groupings/members/%s/opt-in-groups", principal.getName());
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
                String.format(API_2_1_BASE + "/groupings/%s/include-members/%s/self", safeGrouping,
                        principal.getName());
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
                String.format(API_2_1_BASE + "/groupings/%s/exclude-members/%s/self", safeGrouping,
                        principal.getName());
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.PUT);
    }

    /**
     * Add a list of usersToAdd to include group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/addMembersToIncludeGroup")
    public ResponseEntity<String> addMembersToIncludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @RequestBody List<String> usersToAdd) {
        logger.info("Entered REST addMembersToIncludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUsersToAdd = sanitizeList(usersToAdd);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/include-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, safeUsersToAdd, HttpMethod.PUT);
    }

    /**
     * Add a list of usersToAdd to exclude group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/addMembersToExcludeGroup")
    public ResponseEntity<String> addMembersToExcludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @RequestBody List<String> usersToAdd) {
        logger.info("Entered REST addMembersToExcludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUsersToAdd = sanitizeList(usersToAdd);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/exclude-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, safeUsersToAdd, HttpMethod.PUT);
    }

    /**
     * Remove a list of users from include group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/removeMembersFromIncludeGroup")
    public ResponseEntity<String> removeMembersFromIncludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @RequestBody List<String> usersToDelete) {
        logger.info("Entered REST deleteMembersFromIncludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUsersToDelete = sanitizeList(usersToDelete);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/include-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, safeUsersToDelete,
                HttpMethod.DELETE);
    }

    /**
     * Remove a list of users from exclude group of grouping at path.
     */
    @PutMapping(value = "/{groupingPath}/removeMembersFromExcludeGroup")
    public ResponseEntity<String> removeMembersFromExcludeGroup(Principal principal,
            @PathVariable String groupingPath,
            @RequestBody List<String> usersToDelete) {
        logger.info("Entered REST deleteMembersFromExcludeGroup...");
        String safeGroupingPath = policy.sanitize(groupingPath);
        List<String> safeUsersToDelete = sanitizeList(usersToDelete);
        String uri =
                String.format(API_2_1_BASE + "/groupings/%s/exclude-members", safeGroupingPath);
        return httpRequestService.makeApiRequestWithBody(principal.getName(), uri, safeUsersToDelete,
                HttpMethod.DELETE);
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
    @GetMapping(value = "/owners/groupings/count")
    public ResponseEntity<String> numberOfGroupings(Principal principal) {
        logger.info("Entered REST numberOfGroupings...");
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings/count", principal.getName());
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
     * Get a list of groupings owned by uhIdentifier.
     */
    @GetMapping(value = "/owners/{uhIdentifier}/groupings")
    public ResponseEntity<String> groupingsOwnedUid(Principal principal, @PathVariable String uhIdentifier) {
        logger.info("Entered REST GroupingAssignment...");
        String safeUid = policy.sanitize(uhIdentifier);
        String uri = String.format(API_2_1_BASE + "/owners/%s/groupings", safeUid);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
    }

    /**
     * Give ownership of grouping at grouping path to newOwner. A user with owner privileges has
     * read and write privileges
     * of a grouping.
     */
    @PostMapping(value = "/{groupingPath}/{newOwner}/addOwnerships")
    public ResponseEntity<String> addOwnerships(Principal principal,
            @PathVariable String groupingPath,
            @PathVariable String newOwner) {
        logger.info("Entered REST addOwnerships...");
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
        String uri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", safeGroupingPath, safeOwnerToRemove);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.DELETE);
    }

    /**
     * Fetch a page of the specified Grouping.
     */
    @GetMapping(value = "/groupings/{path:.+}")
    public ResponseEntity<String> grouping(Principal principal, @PathVariable String path,
            @RequestParam(required = true) Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = true) String sortString,
            @RequestParam(required = true) Boolean isAscending) {
        logger.info("Entered REST grouping...");
        Map<String, String> params = mapGroupingParameters(page, size, sortString, isAscending);
        String baseUri = String.format(API_2_1_BASE + "/groupings/%s", path);
        String uri = buildUriWithParams(baseUri, params);

        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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
        logger.info("Entered REST setOptOut...");
        String safeGrouping = policy.sanitize(grouping);
        return changePreference(safeGrouping, principal.getName(), OPT_OUT, optOutOn);
    }

    /**
     * Checks if the owner of a grouping is the sole owner
     */
    @GetMapping(value = "/{path:.+}/owners/{uidToCheck}")
    public ResponseEntity<String> isSoleOwner(Principal principal, @PathVariable String path,
            @PathVariable String uidToCheck) {
        logger.info("Entered REST isSoleOwner...");
        String baseUri = String.format(API_2_1_BASE + "/groupings/%s/owners/%s", path, uidToCheck);

        return httpRequestService.makeApiRequest(principal.getName(), baseUri, HttpMethod.GET);
    }

    /**
     * Fetch a list of supported sync destinations for grouping at path.
     */
    @GetMapping(value = "/groupings/{path}/sync-destinations")
    public ResponseEntity<String> allSyncDestinations(Principal principal, @PathVariable String path) {
        logger.info("Entered REST getAllSyncDestinations...");
        String safePath = policy.sanitize(path);
        String uri = String.format(API_2_1_BASE + "/groupings/%s/sync-destinations", safePath);
        return httpRequestService.makeApiRequest(principal.getName(), uri, HttpMethod.GET);
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
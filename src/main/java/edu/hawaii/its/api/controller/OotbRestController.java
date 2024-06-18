package edu.hawaii.its.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.configuration.OotbStaticUserAuthenticationFilter;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;

@RestController
@RequestMapping("/api/groupings/ootb")
public class OotbRestController {
    private static final Log logger = LogFactory.getLog(OotbRestController.class);
    private final PolicyFactory policy;

    @Value("${url.api.2.1.base}")
    private String API_2_1_BASE;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private OotbStaticUserAuthenticationFilter ootbAuthenticationFilter;

    @Autowired
    private OotbActiveUserProfileService ootbActiveUserProfileService;

    @Autowired
    private HttpRequestService httpRequestService;

    // Constructor.
    public OotbRestController() {
        policy = Sanitizers.FORMATTING;
    }

    /*
     * Checks to make sure that the API is running and that there are no issues with
     * the overrides file. Gets the active profiles and only runs the tests the
     * active profile relies on the API.
     */
    @GetMapping("/availableProfiles")
    public ResponseEntity<List<String>> getAvailableProfiles() {
        List<String> profiles = ootbActiveUserProfileService.getAvailableProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PostMapping(value = "/{activeProfile}")
    public ResponseEntity<String> updateActiveDefaultUser(@PathVariable String activeProfile) {
        logger.info("Entered REST updateActiveUserProfile...");
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        ootbAuthenticationFilter.setUserProfile(activeProfile);
        User user = ootbActiveUserProfileService.getUsers().get(activeProfile);

        // Request Body (List<String> - authorities)
        java.util.Collection<GrantedAuthority> authoritiesCollection = user.getAuthorities();
        List<String> authorities = authoritiesCollection.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Request Params ( String - uid, uhUuid, name, givenName )
        Map<String, String> params =
                mapOotbParameters(user.getUid(), user.getUhUuid(), user.getName(), user.getGivenName());
        String baseUri = API_2_1_BASE + "/activeProfile/ootb";
        String uri = buildUriWithParams(baseUri, params);
        return httpRequestService.makeApiRequestWithBody(currentUid, uri, authorities, HttpMethod.POST);
    }

    private Map<String, String> mapOotbParameters(String uid, String uhUuid, String name, String givenName) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("uhUuid", uhUuid);
        params.put("name", name);
        params.put("givenName", givenName);
        return params;
    }

    private String buildUriWithParams(String baseUri, Map<String, String> params) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUri);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriComponentsBuilder.encode().toUriString();
    }
}
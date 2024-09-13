package edu.hawaii.its.api.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.api.service.OotbHttpRequestService;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.configuration.OotbStaticUserAuthenticationFilter;
import edu.hawaii.its.groupings.service.OotbActiveUserProfileService;
import edu.hawaii.its.groupings.type.OotbActiveProfile;

@RestController
@Profile("ootb")
@RequestMapping("/api/groupings/ootb")
public class OotbRestController {
    private static final Log logger = LogFactory.getLog(OotbRestController.class);
    private final PolicyFactory policy = Sanitizers.FORMATTING;;

    @Value("${url.api.2.1.base}")
    private String API_2_1_BASE;

    private final UserContextService userContextService;

    private final OotbStaticUserAuthenticationFilter ootbAuthenticationFilter;

    private final OotbActiveUserProfileService ootbActiveUserProfileService;

    private final OotbHttpRequestService ootbHttpRequestService;


    // Constructor.
    public OotbRestController(UserContextService userContextService,
            OotbStaticUserAuthenticationFilter ootbAuthenticationFilter,
            OotbActiveUserProfileService ootbActiveUserProfileService,
            OotbHttpRequestService ootbHttpRequestService) {
        this.userContextService = userContextService;
        this.ootbAuthenticationFilter = ootbAuthenticationFilter;
        this.ootbActiveUserProfileService = ootbActiveUserProfileService;
        this.ootbHttpRequestService = ootbHttpRequestService;
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
    public ResponseEntity<OotbActiveProfile> updateActiveDefaultUser(@PathVariable String activeProfile) {
        String currentUid = policy.sanitize(userContextService.getCurrentUid());
        ootbAuthenticationFilter.setUserProfile(activeProfile);
        OotbActiveProfile ootbActiveProfile = ootbActiveUserProfileService.getActiveProfiles().get(activeProfile);
        String baseUri = API_2_1_BASE + "/activeProfile/ootb";
        return ootbHttpRequestService.makeApiRequestWithActiveProfileBody(currentUid, baseUri, ootbActiveProfile,
                HttpMethod.POST);
    }
}

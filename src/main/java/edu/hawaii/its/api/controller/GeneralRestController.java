package edu.hawaii.its.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;

@RestController
public class GeneralRestController {
    private static final Log logger = LogFactory.getLog(GroupingsRestController.class);

    @Value("${url.api.2.1.base}")
    private String API_2_1_BASE;

    private final HttpRequestService httpRequestService;

    private final UserContextService userContextService;

    public GeneralRestController(HttpRequestService httpRequestService, UserContextService userContextService) {
        this.httpRequestService = httpRequestService;
        this.userContextService = userContextService;
    }

    /**
     * Get the list of active announcements to display.
     */
    @GetMapping(value = "/announcements")
    public ResponseEntity<String> activeAnnouncements() {
        logger.info("Entered REST activeAnnouncements...");
        String uri = String.format(API_2_1_BASE + "/announcements");
        return httpRequestService.makeApiRequest(uri, HttpMethod.GET);
    }

    /**
     * Get the current user.
     */
    @GetMapping(value = "/currentUser")
    public ResponseEntity<User> currentUser() {
        User currentUser = userContextService.getCurrentUser();
        return ResponseEntity.ok().body(currentUser);
    }
}

package edu.hawaii.its.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.JwtService;

@Profile("dev | localhost")
@RestController
public class DebugController {

    private static final Log logger = LogFactory.getLog(DebugController.class);

    private final UserContextService userContextService;
    private final JwtService jwtService;

    public DebugController(UserContextService userContextService, JwtService jwtService) {
        this.userContextService = userContextService;
        this.jwtService = jwtService;
    }

    /**
     * Get JWT token DEVELOPMENT ONLY!
     */
    @GetMapping(value="/development/jwt")
    public ResponseEntity<String> getJWT() {
        logger.info("Entered REST getJWT...");
        User currentUser = userContextService.getCurrentUser();
        String jwt = jwtService.generateToken(currentUser);
        return ResponseEntity.ok().body(jwt);
    }
}
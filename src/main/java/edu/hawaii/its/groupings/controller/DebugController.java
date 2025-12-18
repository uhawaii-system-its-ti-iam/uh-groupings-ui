package edu.hawaii.its.groupings.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.service.JwtService;

@Profile({"test", "localhost", "localTest"})
@RestController
public class DebugController {

    private static final Log logger = LogFactory.getLog(DebugController.class);
    private final JwtService jwtService;
    private final UserContextService userContextService;

    public DebugController(JwtService jwtService, UserContextService userContextService) {
        this.jwtService = jwtService;
        this.userContextService = userContextService;
    }

    /**
     * Get JWT token. Not for production environment!
     */
    @GetMapping(value="/development/jwt")
    public ResponseEntity<String> getJWT() {
        String currentUid = userContextService.getCurrentUid();
        logger.info(String.format("Entered REST getJWT - currentUid: %s", currentUid));
        String jwt = jwtService.generateToken();
        return ResponseEntity.ok().body(jwt);
    }
}
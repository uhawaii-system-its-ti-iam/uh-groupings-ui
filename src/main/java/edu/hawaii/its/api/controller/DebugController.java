package edu.hawaii.its.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hawaii.its.groupings.service.JwtService;

@Profile("dev | localhost")
@RestController
public class DebugController {

    private static final Log logger = LogFactory.getLog(DebugController.class);

    private final JwtService jwtService;

    public DebugController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Get JWT token DEVELOPMENT ONLY!
     */
    @GetMapping(value="/development/jwt")
    public ResponseEntity<String> getJWT() {
        logger.info("Entered REST getJWT...");
        String jwt = jwtService.generateToken();
        return ResponseEntity.ok().body(jwt);
    }
}
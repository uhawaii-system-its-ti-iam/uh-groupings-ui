package edu.hawaii.its.groupings.service;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.access.UserContextService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final UserContextService userContextService;

    private static final Log logger = LogFactory.getLog(JwtService.class);

    @Value("${jwt.expiration-time}")
    private Integer EXPIRATION_TIME;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public JwtService(UserContextService userContextService) {
        this.userContextService = userContextService;
    }

    public String generateToken() {

        UserDetails userDetails = userContextService.getCurrentUser();

        logger.info(String.format("JwtService: generating token for %s", userDetails.getUsername()));

        long expirationTime = 1000L * EXPIRATION_TIME; // convert seconds to milliseconds.

        // Get user's role.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

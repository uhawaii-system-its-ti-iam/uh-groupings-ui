package edu.hawaii.its.groupings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import edu.hawaii.its.groupings.access.User;
import edu.hawaii.its.groupings.access.UserContextService;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class JwtServiceTest {

    @MockitoBean
    private UserContextService userContextService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.expiration-time}")
    private long EXPIRATION_TIME;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    private final String TEST_USER = "testiwta";

    @Test
    public void generateTokenTest() {

        User mockUser = mock(User.class);
        when(userContextService.getCurrentUser()).thenReturn(mockUser);

        when(mockUser.getAuthorities()).thenReturn(List.of(
                new SimpleGrantedAuthority("ROLE_UH"),
                new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(mockUser.getUsername()).thenReturn(TEST_USER);

        String token = jwtService.generateToken();
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(claims.getSubject(), TEST_USER);
        assertEquals(claims.get("roles", List.class), List.of("ROLE_UH", "ROLE_ADMIN"));

        long expirationTime = (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000L;
        assertEquals(expirationTime, EXPIRATION_TIME);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
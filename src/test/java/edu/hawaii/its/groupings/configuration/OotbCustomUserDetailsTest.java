package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import edu.hawaii.its.groupings.access.UhAttributes;

@SpringBootTest(classes = { SpringBootWebApplication.class })
public class OotbCustomUserDetailsTest {

    @Test
    public void testOotbCustomUserDetailsConstructor() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        UhAttributes attributes = new UhAttributes(Map.of(
                "cn", Arrays.asList("testiwa"),
                "mail", Arrays.asList("testiwa@hawaii.edu"),
                "givenName", Arrays.asList("John")
        ));
        OotbCustomUserDetails userDetails = new OotbCustomUserDetails(
                "username", "password", authorities, "John", "testiwa", "testiwa@hawaii.edu");
        userDetails.setAttributes(attributes);

        assertNotNull(userDetails);
        assertEquals("username", userDetails.getUsername());
        assertEquals("password", userDetails.getUhUuid());
        assertTrue(userDetails.getAuthorities().containsAll(authorities));
        assertEquals("testiwa", userDetails.getAttributes().getValue("cn"));
        assertEquals("testiwa@hawaii.edu", userDetails.getAttributes().getValue("mail"));
        assertEquals("John", userDetails.getAttributes().getValue("givenName"));
    }

    @Test
    public void testAttributesNotNull() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        OotbCustomUserDetails userDetails = new OotbCustomUserDetails(
                "username", "password", authorities, "John", "testiwa", "testiwa@hawaii.edu");

        assertNotNull(userDetails.getAttributes());
    }
}

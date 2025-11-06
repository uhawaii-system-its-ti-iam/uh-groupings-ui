package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserTest {

    @Test
    public void construction() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        User user = new User("a", authorities);
        assertNotNull(user);

        assertThat(user.getUid(), is("a"));
        assertThat(user.getUid(), is("a"));
        assertNull(user.getUhUuid());
        assertNull(user.getAttributes());

        authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ANONYMOUS.longName()));
        user = new User("b", "12345", authorities);

        assertThat(user.getUid(), is("b"));
        assertThat(user.getUid(), is("b"));
        assertThat(user.getUhUuid(), is("12345"));
        assertNull(user.getAttributes());

        user.setAttributes(new UhAttributes());
        assertThat(user.getName(), is(""));
    }

    @Test
    public void accessors() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "testiwtc");
        map.put("uhUuid", "99997033");
        map.put("cn", "IamtstC1");
        map.put("givenName", "IamtstC");
        map.put("mail", "iamtstc@example.com");
        map.put("eduPersonAffiliation", "aff");

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        User user = new User("a", authorities);
        user.setAttributes(new UhAttributes(map));

        assertThat(user.getAttribute("uid"), is("testiwtc"));
        assertThat(user.getName(), is("IamtstC1"));
        assertThat(user.getGivenName(), is("IamtstC"));
        assertThat(user.toString(), containsString("uid=a"));
        assertThat(user.toString(), containsString("uhUuid=null"));
    }

    @Test
    public void testBuilderConstruction() {
        // Create user with builder
        User user = new User.Builder("testiwtc")
                .uhUuid("99997033")
                .addAttribute("givenName", "IamtstC")
                .addAttribute("cn", "IamtstC1")
                .addAuthorities("ROLE_UH")
                .addAuthorities(Arrays.asList("ROLE_ADMIN", "ROLE_OWNER"))
                .build();

        assertThat(user.getUid(), is("testiwtc"));
        assertThat(user.getUhUuid(), is("99997033"));
        assertThat(user.getGivenName(), is("IamtstC"));
        assertThat(user.getName(), is("IamtstC1"));

        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.OWNER));
        assertTrue(user.hasRole(Role.ADMIN));
    }

    @Test
    public void testBuilderAttributes() {

        User user = new User.Builder("testiwtd")
                .addAttribute("cn", "IamtstD1")
                .addAttribute("givenName", "IamtstD")
                .build();

        assertThat(user.getAttribute("cn"), is("IamtstD1"));
        assertThat(user.getAttribute("givenName"), is("IamtstD"));
    }

    @Test
    public void testBuilderAuthorities() {
        User user = new User.Builder("authUser")
                .addAuthorities(Arrays.asList("ROLE_UH", "ROLE_OWNER"))
                .build();

        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.OWNER));
    }
}

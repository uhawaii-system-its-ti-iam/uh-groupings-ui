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

        User user = new User("testiwta", authorities);
        assertNotNull(user);

        assertThat(user.getUid(), is("testiwta"));
        assertThat(user.getUid(), is("testiwta"));
        assertNull(user.getUhUuid());
        assertNull(user.getAttributes());

        authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ANONYMOUS.longName()));
        user = new User("testiwtb", "99997027", authorities);

        assertThat(user.getUid(), is("testiwtb"));
        assertThat(user.getUid(), is("testiwtb"));
        assertThat(user.getUhUuid(), is("99997027"));
        assertNull(user.getAttributes());

        user.setAttributes(new UhAttributes());
        assertThat(user.getName(), is(""));
    }

    @Test
    public void accessors() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "testiwtc");
        map.put("uhUuid", "99997033");
        map.put("cn", "Testf-iwt-c TestIAM-staff");
        map.put("givenName", "Testf-iwt-c");
        map.put("mail", "iamtstc@example.com");
        map.put("eduPersonAffiliation", "aff");

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        User user = new User("a", authorities);
        user.setAttributes(new UhAttributes(map));

        assertThat(user.getAttribute("uid"), is("testiwtc"));
        assertThat(user.getName(), is("Testf-iwt-c TestIAM-staff"));
        assertThat(user.getGivenName(), is("Testf-iwt-c"));
        assertThat(user.toString(), containsString("uid=a"));
        assertThat(user.toString(), containsString("uhUuid=null"));
    }

    @Test
    public void testBuilderConstruction() {
        // Create user with builder
        User user = new User.Builder("testiwtc")
                .uhUuid("99997033")
                .addAttribute("givenName", "Testf-iwt-c")
                .addAttribute("cn", "Testf-iwt-c TestIAM-staff")
                .addAuthorities("ROLE_UH")
                .addAuthorities(Arrays.asList("ROLE_ADMIN", "ROLE_OWNER"))
                .build();

        assertThat(user.getUid(), is("testiwtc"));
        assertThat(user.getUhUuid(), is("99997033"));
        assertThat(user.getGivenName(), is("Testf-iwt-c"));
        assertThat(user.getName(), is("Testf-iwt-c TestIAM-staff"));

        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.OWNER));
        assertTrue(user.hasRole(Role.ADMIN));
    }

    @Test
    public void testBuilderAttributes() {

        User user = new User.Builder("testiwtd")
                .addAttribute("cn", "Testf-iwt-d TestIAM-faculty")
                .addAttribute("givenName", "Testf-iwt-d")
                .build();

        assertThat(user.getAttribute("cn"), is("Testf-iwt-d TestIAM-faculty"));
        assertThat(user.getAttribute("givenName"), is("Testf-iwt-d"));
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

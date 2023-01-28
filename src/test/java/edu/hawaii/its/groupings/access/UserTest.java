package edu.hawaii.its.groupings.access;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    @Test
    public void construction() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        User user = new User("a", authorities);
        assertNotNull(user);

        assertThat(user.getUsername(), is("a"));
        assertThat(user.getUid(), is("a"));
        assertNull(user.getUhUuid());
        assertNull(user.getAttributes());

        authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ANONYMOUS.longName()));
        user = new User("b", "12345", authorities);

        assertThat(user.getUsername(), is("b"));
        assertThat(user.getUid(), is("b"));
        assertThat(user.getUhUuid(), is("12345"));
        assertNull(user.getAttributes());

        user.setAttributes(new UhCasAttributes());
        assertThat(user.getName(), is(""));
    }

    @Test
    public void accessors() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "duckart");
        map.put("uhUuid", "666666");
        map.put("cn", "Frank6");
        map.put("givenName", "Frank");
        map.put("mail", "frank@example.com");
        map.put("eduPersonAffiliation", "aff");

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        User user = new User("a", authorities);
        user.setAttributes(new UhCasAttributes(map));

        assertThat(user.getAttribute("uid"), is("duckart"));
        assertThat(user.getName(), is("Frank6"));
        assertThat(user.getGivenName(), is("Frank"));
        assertThat(user.toString(), containsString("uid=a"));
        assertThat(user.toString(), containsString("uhUuid=null"));
    }
}

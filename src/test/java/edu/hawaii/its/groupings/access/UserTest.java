package edu.hawaii.its.groupings.access;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class UserTest {

    @Test
    public void construction() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        User user = new User("a", authorities);
        assertNotNull(user);

        assertEquals("a", user.getUsername());
        assertEquals("a", user.getUid());
        assertNull(user.getUhUuid());
        assertNull(user.getAttributes());

        authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ANONYMOUS.longName()));
        user = new User("b", "12345", authorities);

        assertEquals("b", user.getUsername());
        assertEquals("b", user.getUid());
        assertEquals("12345", user.getUhUuid());
        assertNull(user.getAttributes());

        user.setAttributes(new UhCasAttributes());
        assertEquals("", user.getName());
    }

    @Test
    public void accessors() {
        Map<Object, Object> map = new HashMap<>();
        map.put("uid", "duckart");
        map.put("uhUuid", "666666");
        map.put("cn", "Frank");
        map.put("mail", "frank@example.com");
        map.put("eduPersonAffiliation", "aff");

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        User user = new User("a", authorities);
        user.setAttributes(new UhCasAttributes(map));

        assertThat(user.getAttribute("uid"), equalTo("duckart"));
        assertThat(user.getName(), equalTo("Frank"));
        assertThat(user.toString(), containsString("uid=a"));
        assertThat(user.toString(), containsString("uhUuid=null"));
    }
}

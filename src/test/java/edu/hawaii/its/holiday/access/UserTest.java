package edu.hawaii.its.holiday.access;

import org.junit.Test;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void construction() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();

        User user = new User("a", authorities);
        assertNotNull(user);

        assertEquals("a", user.getUsername());
        assertEquals("a", user.getUid());
        assertNull(user.getUhuuid());
        assertNull(user.getAttributes());

        authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(Role.ANONYMOUS.longName()));
        user = new User("b", 12345L, authorities);

        assertEquals("b", user.getUsername());
        assertEquals("b", user.getUid());
        assertEquals(Long.valueOf(12345), user.getUhuuid());
        assertNull(user.getAttributes());

        user.setAttributes(new UhCasAttributes());
        assertEquals("", user.getName());
    }

    @Test
    public void accessors() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("uid", "duckart");
        map.put("uhuuid", "666666");
        map.put("cn", "Frank");
        map.put("mail", "frank@example.com");
        map.put("eduPersonAffiliation", "aff");

        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        User user = new User("a", authorities);
        user.setAttributes(new UhCasAttributes(map));

        assertThat(user.getAttribute("uid"), equalTo("duckart"));
        assertThat(user.getName(), equalTo("Frank"));
        assertThat(user.toString(), containsString("uid=a"));
        assertThat(user.toString(), containsString("uhuuid=null"));
    }
}

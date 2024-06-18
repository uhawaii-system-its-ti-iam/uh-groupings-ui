package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OotbActiveProfileTest {

    private OotbActiveProfile profile;

    @BeforeEach
    public void setUp() {
        profile = new OotbActiveProfile();
    }

    @Test
    public void construction() {
        assertNotNull(profile);
    }

    @Test
    public void uid() {
        assertNull(profile.getUid());
        profile.setUid("123456");
        assertThat(profile.getUid(), is("123456"));
    }

    @Test
    public void uhUuid() {
        assertNull(profile.getUhUuid());
        profile.setUhUuid("ABCDEF");
        assertThat(profile.getUhUuid(), is("ABCDEF"));
    }

    @Test
    public void authorities() {
        assertNull(profile.getAuthorities());
        List<String> authorities = new ArrayList<>();
        authorities.add("ADMIN");
        authorities.add("USER");
        profile.setAuthorities(authorities);
        assertNotNull(profile.getAuthorities());
        assertThat(profile.getAuthorities().size(), is(2));
        assertThat(profile.getAuthorities().get(0), is("ADMIN"));
        assertThat(profile.getAuthorities().get(1), is("USER"));
    }

    @Test
    public void attributes() {
        assertNull(profile.getAttributes());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        profile.setAttributes(attributes);
        assertNotNull(profile.getAttributes());
        assertThat(profile.getAttributes().size(), is(2));
        assertThat(profile.getAttributes().get("key1"), is("value1"));
        assertThat(profile.getAttributes().get("key2"), is("value2"));
    }
}

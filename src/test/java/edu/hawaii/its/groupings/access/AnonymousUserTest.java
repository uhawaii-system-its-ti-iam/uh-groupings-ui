package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnonymousUserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new AnonymousUser();
    }

    @Test
    public void testConstructions() {
        assertNotNull(user);
        assertThat(user.getUid(), is("anonymous"));
        assertThat(user.getUid(), is("anonymous"));
        assertNull(user.getUhUuid());
        assertThat(user.getPassword(), is(""));
        assertThat(user.getAuthorities().size(), is(1));
        assertTrue(user.hasRole(Role.ANONYMOUS));
    }
}

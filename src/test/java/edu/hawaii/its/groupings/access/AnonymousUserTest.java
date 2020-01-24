package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AnonymousUserTest {

    private User user;

    @Before
    public void setUp() {
        user = new AnonymousUser();
    }

    @Test
    public void testConstructions() {
        assertNotNull(user);
        assertThat(user.getUsername(), is("anonymous"));
        assertThat(user.getUid(), is("anonymous"));
        assertNull(user.getUhUuid());
        assertThat(user.getPassword(), is(""));
        assertThat(user.getAuthorities().size(), is(1));
        assertTrue(user.hasRole(Role.ANONYMOUS));
    }
}

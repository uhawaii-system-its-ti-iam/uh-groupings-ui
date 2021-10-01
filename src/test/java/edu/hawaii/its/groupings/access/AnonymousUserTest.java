package edu.hawaii.its.groupings.access;

import static org.junit.Assert.assertEquals;
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
        assertEquals("anonymous", user.getUsername());
        assertEquals("anonymous", user.getUid());
        assertNull(user.getUhUuid());
        assertEquals("", user.getPassword());
        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.hasRole(Role.ANONYMOUS));
    }
}

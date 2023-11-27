package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserRoleTest {

    private UserRole userRole;

    @BeforeEach
    public void setUp() {
        userRole = new UserRole();
    }

    @Test
    public void construction() {
        assertNotNull(userRole);
    }

    @Test
    public void setters() {
        assertNotNull(userRole);

        assertNull(userRole.getId());
        assertNull(userRole.getAuthority());
        assertNull(userRole.getVersion());

        userRole.setId(666);
        userRole.setAuthority("The Beast");
        userRole.setVersion(9);
        assertThat(userRole.getId(), is(666));
        assertThat(userRole.getAuthority(), is("The Beast"));
        assertThat(userRole.getVersion(), is(9));
    }

    @Test
    public void testToString() {
        String expected = "UserRole [id=null, version=null, authority=null]";
        assertThat(userRole.toString(), containsString(expected));

        userRole.setId(12345);
        assertThat(userRole.toString(), containsString("UserRole [id=12345,"));
    }
}

package edu.hawaii.its.holiday.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class UserRoleTest {

    private UserRole userRole;

    @Before
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
        assertThat(userRole.getId(), equalTo(666));
        assertThat(userRole.getAuthority(), equalTo("The Beast"));
        assertThat(userRole.getVersion(), equalTo(9));
    }

    @Test
    public void testToString() {
        String expected = "UserRole [id=null, version=null, authority=null]";
        assertThat(userRole.toString(), containsString(expected));

        userRole.setId(12345);
        assertThat(userRole.toString(), containsString("UserRole [id=12345,"));
    }
}

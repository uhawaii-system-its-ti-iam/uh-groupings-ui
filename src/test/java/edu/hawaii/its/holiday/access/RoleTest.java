package edu.hawaii.its.holiday.access;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RoleTest {

    @Test
    public void longName() {
        for (Role role : Role.values()) {
            assertEquals("ROLE_" + role.name(), role.longName());
        }
    }

    @Test
    public void find() {
        Role role = Role.find(Role.ADMIN.name());
        assertNotNull(role);
        assertThat(role.name(), equalTo(Role.ADMIN.name()));
        assertThat(role.longName(), equalTo(Role.ADMIN.longName()));
        assertThat(role.toString(), equalTo("ROLE_ADMIN"));
        role = Role.find("non-existent-role");
        assertNull(role);
    }
}

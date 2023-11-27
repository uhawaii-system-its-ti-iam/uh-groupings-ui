package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class RoleTest {

    @Test
    public void longName() {
        for (Role role : Role.values()) {
            assertThat(role.longName(), is("ROLE_" + role.name()));
        }
    }

    @Test
    public void find() {
        Role role = Role.find(Role.ADMIN.name());
        assertNotNull(role);
        assertThat(role.name(), is(Role.ADMIN.name()));
        assertThat(role.longName(), is(Role.ADMIN.longName()));
        assertThat(role.toString(), is("ROLE_ADMIN"));
        role = Role.find("non-existent-role");
        assertNull(role);
    }
}

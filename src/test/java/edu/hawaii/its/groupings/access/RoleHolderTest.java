package edu.hawaii.its.groupings.access;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class RoleHolderTest {

    @Test
    public void basics() {
        RoleHolder roleHolder = new RoleHolder();
        assertThat(roleHolder.size(), is(0));
        roleHolder.add(Role.ANONYMOUS);
        assertThat(roleHolder.size(), is(1));
        roleHolder.add(Role.UH);
        assertThat(roleHolder.size(), is(2));
        roleHolder.add(Role.EMPLOYEE);
        assertThat(roleHolder.size(), is(3));
        roleHolder.add(Role.OWNER);
        assertThat(roleHolder.size(), is(4));
        roleHolder.add(Role.ADMIN);
        assertThat(roleHolder.size(), is(5));

        assertThat(roleHolder.toString(), containsString("ROLE_ANONYMOUS"));
        assertThat(roleHolder.toString(), containsString("ROLE_UH"));
        assertThat(roleHolder.toString(), containsString("ROLE_EMPLOYEE"));
        assertThat(roleHolder.toString(), containsString("ROLE_OWNER"));
        assertThat(roleHolder.toString(), containsString("ROLE_ADMIN"));
    }
}

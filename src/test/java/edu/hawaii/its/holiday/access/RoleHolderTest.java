package edu.hawaii.its.holiday.access;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RoleHolderTest {

    @Test
    public void basics() {
        RoleHolder roleHolder = new RoleHolder();
        assertThat(roleHolder.size(), equalTo(0));
        roleHolder.add(Role.ANONYMOUS);
        assertThat(roleHolder.size(), equalTo(1));
        roleHolder.add(Role.UH);
        assertThat(roleHolder.size(), equalTo(2));
        roleHolder.add(Role.EMPLOYEE);
        assertThat(roleHolder.size(), equalTo(3));
        roleHolder.add(Role.OWNER);
        assertThat(roleHolder.size(), equalTo(4));
        roleHolder.add(Role.ADMIN);
        assertThat(roleHolder.size(), equalTo(5));

        assertThat(roleHolder.toString(), containsString("ROLE_ANONYMOUS"));
        assertThat(roleHolder.toString(), containsString("ROLE_UH"));
        assertThat(roleHolder.toString(), containsString("ROLE_EMPLOYEE"));
        assertThat(roleHolder.toString(), containsString("ROLE_OWNER"));
        assertThat(roleHolder.toString(), containsString("ROLE_ADMIN"));
    }
}

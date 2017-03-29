package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class OwnerTest {

    private Owner owner;

    @Before
    public void setUp() {
        owner = new Owner();
    }

    @Test
    public void construction() {
        owner = new Owner("James T Kirk");
        assertThat(owner.getPrivilegeName(), equalTo("James T Kirk"));
    }

    @Test
    public void name() {
        assertThat(owner.getName(), equalTo(null));
        owner.setName("frank");
        assertThat(owner.getName(), equalTo("frank"));
    }

    @Test
    public void privilegeName() {
        assertThat(owner.getPrivilegeName(), equalTo(null));
        owner.setPrivilegeName("frd");
        assertThat(owner.getPrivilegeName(), equalTo("frd"));
    }

    @Test
    public void uhuuid() {
        assertThat(owner.getUhuuid(), equalTo(null));
        owner.setUhuuid("uhuuid");
        assertThat(owner.getUhuuid(), equalTo("uhuuid"));
    }

    @Test
    public void uid() {
        assertThat(owner.getUid(), equalTo(null));
        owner.setUid("uid");
        assertThat(owner.getUid(), equalTo("uid"));
    }

    @Test
    public void testToString() {
        owner.setPrivilegeName("duke");
        assertThat(owner.toString(), containsString("Owner [privilegeName=duke,"));
    }
}

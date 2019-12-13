package edu.hawaii.its.groupings.type;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
    public void uhUuid() {
        assertThat(owner.getUhUuid(), equalTo(null));
        owner.setUhUuid("uhUuid");
        assertThat(owner.getUhUuid(), equalTo("uhUuid"));
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

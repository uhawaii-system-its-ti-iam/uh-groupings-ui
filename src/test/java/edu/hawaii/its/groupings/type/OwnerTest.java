package edu.hawaii.its.groupings.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;


public class OwnerTest {

    private Owner owner;

    @BeforeEach
    public void setUp() {
        owner = new Owner();
    }

    @Test
    public void construction() {
        owner = new Owner("James T Kirk");
        assertThat(owner.getPrivilegeName(), is("James T Kirk"));
    }

    @Test
    public void name() {
        assertNull(owner.getName());
        owner.setName("frank");
        assertThat(owner.getName(), is("frank"));
    }

    @Test
    public void privilegeName() {
        assertNull(owner.getPrivilegeName());
        owner.setPrivilegeName("frd");
        assertThat(owner.getPrivilegeName(), is("frd"));
    }

    @Test
    public void uhUuid() {
        assertNull(owner.getUhUuid());
        owner.setUhUuid("uhUuid");
        assertThat(owner.getUhUuid(), is("uhUuid"));
    }

    @Test
    public void uid() {
        assertNull(owner.getUid());
        owner.setUid("uid");
        assertThat(owner.getUid(), is("uid"));
    }

    @Test
    public void testToString() {
        owner.setPrivilegeName("duke");
        assertThat(owner.toString(), containsString("Owner [privilegeName=duke,"));
    }
}

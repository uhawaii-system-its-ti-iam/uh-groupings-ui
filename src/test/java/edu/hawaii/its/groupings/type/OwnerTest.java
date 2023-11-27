package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OwnerTest {

    private Owner owner;

    @BeforeEach
    public void setUp() {
        owner = new Owner();
    }

    @Test
    public void construction() {
        owner = new Owner("Test I Wa");
        assertThat(owner.getPrivilegeName(), is("Test I Wa"));
    }

    @Test
    public void name() {
        assertNull(owner.getName());
        owner.setName("imtsta");
        assertThat(owner.getName(), is("imtsta"));
    }

    @Test
    public void privilegeName() {
        assertNull(owner.getPrivilegeName());
        owner.setPrivilegeName("iwa");
        assertThat(owner.getPrivilegeName(), is("iwa"));
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
        owner.setPrivilegeName("tsta");
        assertThat(owner.toString(), containsString("Owner [privilegeName=tsta,"));
    }
}

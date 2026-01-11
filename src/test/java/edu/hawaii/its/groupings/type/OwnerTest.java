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
        owner = new Owner("Testf-iwt-a TestIAM-staff");
        assertThat(owner.getPrivilegeName(), is("Testf-iwt-a TestIAM-staff"));
    }

    @Test
    public void name() {
        assertNull(owner.getName());
        owner.setName("Testf-iwt-b TestIAM-staff");
        assertThat(owner.getName(), is("Testf-iwt-b TestIAM-staff"));
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
        owner.setUhUuid("99997010");
        assertThat(owner.getUhUuid(), is("99997010"));
    }

    @Test
    public void uid() {
        assertNull(owner.getUid());
        owner.setUid("testiwta");
        assertThat(owner.getUid(), is("testiwta"));
    }

    @Test
    public void testToString() {
        owner.setPrivilegeName("tsta");
        assertThat(owner.toString(), containsString("Owner [privilegeName=tsta,"));
    }
}

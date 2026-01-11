package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OotbMemberTest {

    private OotbMember member;

    @BeforeEach
    public void setUp() {
        member = new OotbMember();
    }

    @Test
    public void construction() {
        assertNotNull(member);
    }

    @Test
    public void name() {
        assertNull(member.getName());
        member.setName("Testf-iwt-a TestIAM-staff");
        assertThat(member.getName(), is("Testf-iwt-a TestIAM-staff"));
    }

    @Test
    public void uhUuid() {
        assertNull(member.getUhUuid());
        member.setUhUuid("99997010");
        assertThat(member.getUhUuid(), is("99997010"));
    }

    @Test
    public void uid() {
        assertNull(member.getUid());
        member.setUid("testiwta");
        assertThat(member.getUid(), is("testiwta"));
    }

    @Test
    public void allArgsConstructor() {
        OotbMember member = new OotbMember("Testf-iwt-a TestIAM-staff", "99997010", "testiwta");
        assertThat(member.getName(), is("Testf-iwt-a TestIAM-staff"));
        assertThat(member.getUhUuid(), is("99997010"));
        assertThat(member.getUid(), is("testiwta"));
    }

    @Test
    public void noArgsConstructor() {
        OotbMember member = new OotbMember();
        assertNull(member.getName());
        assertNull(member.getUhUuid());
        assertNull(member.getUid());
    }
}

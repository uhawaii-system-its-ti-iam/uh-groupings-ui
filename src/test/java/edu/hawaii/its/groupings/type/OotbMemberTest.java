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
        member.setName("testiwa");
        assertThat(member.getName(), is("testiwa"));
    }

    @Test
    public void uhUuid() {
        assertNull(member.getUhUuid());
        member.setUhUuid("12345678");
        assertThat(member.getUhUuid(), is("12345678"));
    }

    @Test
    public void uid() {
        assertNull(member.getUid());
        member.setUid("tw1234");
        assertThat(member.getUid(), is("tw1234"));
    }

    @Test
    public void allArgsConstructor() {
        OotbMember member = new OotbMember("testiwa", "87654321", "tw1234");
        assertThat(member.getName(), is("testiwa"));
        assertThat(member.getUhUuid(), is("87654321"));
        assertThat(member.getUid(), is("tw1234"));
    }

    @Test
    public void noArgsConstructor() {
        OotbMember member = new OotbMember();
        assertNull(member.getName());
        assertNull(member.getUhUuid());
        assertNull(member.getUid());
    }
}

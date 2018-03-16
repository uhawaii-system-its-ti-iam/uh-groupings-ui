package edu.hawaii.its.api.type;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MembershipTest {
    Person membershipHolder = new Person("Membership Holder", "12345", "mholder");
    Group group;
    Membership membership;

    String id = "membership id";
    String newId = "new membership id";

    @Before
    public void setup() {
        List<Person> members = new ArrayList<>();
        members.add(membershipHolder);

        for(int i = 0; i < 10; i ++) {
            Person member = new Person("name" + i, "uuid" + i, "username" + i);
            members.add(member);
        }

        group = new Group("path:to:group", members);

        membership = new Membership(members.get(0), group);
        membership.setId(id);
    }

    @Test
    public void idTest(){
        membership.setId(newId);
        assertEquals(newId, membership.getId());
    }

    @Test
    public void PersonTest(){
        membership.setPerson(membershipHolder);
        assertEquals(membershipHolder, membership.getPerson());
    }

    @Test
    public void GroupTest(){
        membership.setGroup(group);
        assertEquals(group, membership.getGroup());
    }

    @Test
    public void SelfOptedTest(){
        membership.setSelfOpted(true);
        assertTrue(membership.isSelfOpted());
        membership.setSelfOpted(false);
        assertFalse(membership.isSelfOpted());
    }

    @Test
    public void isOptInEnabledTest(){
        membership.setOptInEnabled(true);
        assertTrue(membership.isOptInEnabled());
        membership.setOptInEnabled(false);
        assertFalse(membership.isOptInEnabled());
    }

    @Test
    public void isOptOutEnabledTest(){
        membership.setOptOutEnabled(true);
        assertTrue(membership.isOptOutEnabled());
        membership.setOptOutEnabled(false);
        assertFalse(membership.isOptOutEnabled());
    }

    @Test
    public void equalsTest() {
        Membership membershipCopy = membership;
        assertTrue(membership.equals(membershipCopy));

        Membership differentMembership = new Membership();
        assertFalse(membership.equals(differentMembership));
    }
}

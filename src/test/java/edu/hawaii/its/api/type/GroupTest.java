package edu.hawaii.its.api.type;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GroupTest {

    private Group group;
    private Person person;

    @Before
    public void setUp() {
        group = new Group();
    }

    @Test
    public void construction() {
        assertNotNull(group);
        assertThat(group.getMembers(), not(equalTo(null)));
        assertThat(group.getNames(), not(equalTo(null)));
        assertThat(group.getPath(), equalTo(""));
        assertThat(group.getMembers().size(), equalTo(0));
    }

    @Test
    public void accessors() {
        assertThat(group.getPath(), equalTo(""));
        group.setPath(null);
        assertThat(group.getPath(), equalTo(""));
        group.setPath("path");
        assertThat(group.getPath(), equalTo("path"));
        assertThat(group.getMembers().size(), equalTo(0));
        group.addMember(new Person("a"));
        assertThat(group.getMembers().size(), equalTo(1));
        group.addMember(new Person("b"));
        assertThat(group.getMembers().size(), equalTo(2));
        group.addMember(new Person("c"));
        assertThat(group.getMembers().size(), equalTo(3));
        group.setMembers(null);
        assertThat(group.getMembers().size(), equalTo(0));
        assertFalse(group.isMember(person));
    }

    @Test
    public void equals() {
        Group g0 = new Group();
        assertThat(g0, equalTo(g0));
        assertThat(g0, is(g0));
        assertFalse(g0.equals(null));
        assertNotEquals(g0, new String());

        Group g1 = new Group();
        assertThat(g0, equalTo(g1));
        assertThat(g1, equalTo(g0));

        g0.addMember(new Person());
        assertThat(g0, not(equalTo(g1)));
        g1.addMember(new Person());
        assertThat(g0, equalTo(g1));

        g0.addMember(new Person("Madonna"));
        assertThat(g0, not(equalTo(g1)));
        g1.addMember(new Person("Madonna"));
        assertThat(g0, equalTo(g1));

        g0.addMember(new Person("Prince"));
        assertThat(g0, not(equalTo(g1)));
        g0.addMember(new Person("Prince"));
        assertThat(g0, not(equalTo(g1)));

        g1.addMember(new Person("Prince"));
        assertThat(g0, not(equalTo(g1)));
        g1.addMember(new Person("Prince"));
        assertThat(g0, equalTo(g1));

        g0.setPath("path");
        assertThat(g0, not(equalTo(g1)));
        g1.setPath("path");
        assertThat(g0, equalTo(g1));

        g1.setPath("memo");
        assertThat(g1, not(equalTo(g0)));
        g0.setPath("memo");
        assertThat(g1, equalTo(g0));

        g1.setPath("memo");
        assertThat(g1, equalTo(g0));
        g0.setPath(null);
        assertThat(g1, not(equalTo(g0)));

        g0.setPath(null);
        assertThat(g0, not(equalTo(g1)));
        g1.setPath(null);
        assertThat(g0, equalTo(g1));

        assertThat(g0.getMembers().size(), equalTo(4));
        assertThat(g1.getMembers().size(), equalTo(4));

        g0.addMember(new Person("Archibald Cox"));
        assertThat(g0, not(equalTo(g1)));
        g0.addMember(new Person("Leon Jaworski"));
        assertThat(g0, not(equalTo(g1)));
        g1.addMember(new Person("Archibald Cox"));
        assertThat(g0, not(equalTo(g1)));
        g1.addMember(new Person("Leon Jaworski"));
        assertThat(g0, equalTo(g1));

        g0.addMember(new Person("Tricky Dick"));
        g1.addMember(new Person("Richard Nixon"));
        assertThat(g0, not(equalTo(g1)));

        g0 = new Group();
        assertFalse(g0.equals(null));
        assertNotEquals(g0, new String());
        assertThat(g0, is(g0));

        g1 = new Group();
        assertThat(g1, is(g0));
        assertThat(g0, is(g1));
        ReflectionTestUtils.setField(g0, "members", null);
        assertNotEquals(g0, g1);
        assertNotEquals(g1, g0);
        ReflectionTestUtils.setField(g1, "members", null);
        assertThat(g1, is(g0));
        assertThat(g0, is(g0));
        ReflectionTestUtils.setField(g0, "path", null);
        assertNotEquals(g0, g1);
        assertNotEquals(g1, g0);
        ReflectionTestUtils.setField(g1, "path", null);
        assertThat(g1, is(g0));
        assertThat(g0, is(g1));
    }

    @Test
    public void testHashCode() {
        Group g0 = new Group("a");

        int result = 1;
        final int prime = 31;
        int hashPath = g0.getPath().hashCode();
        assertThat(g0.getPath().hashCode(), equalTo("a".hashCode()));

        int hashMembers = g0.getMembers().hashCode();
        assertThat(hashMembers, equalTo(result));

        int hashCode = 1089;

        assertNotNull(g0.getPath());
        assertNotNull(g0.getMembers());

        assertThat(g0.hashCode(), equalTo(1089));

        ReflectionTestUtils.setField(g0, "path", null);
        assertThat(g0.hashCode(), equalTo(hashCode - hashPath));

        ReflectionTestUtils.setField(g0, "members", null);

        result = 1;
        result = prime * result + 0;
        result = prime * result + 0;
        assertThat(g0.hashCode(), equalTo(prime * prime));
    }

    @Test
    public void compareTo() {
        Group g0 = new Group();
        Group g1 = new Group();
        assertThat(g0.compareTo(g1), equalTo(0));

        g0 = new Group("a");
        assertThat(g0.compareTo(g1), equalTo(1));
        assertThat(g1.compareTo(g0), equalTo(-1));

        g1 = new Group("b");
        assertThat(g0.compareTo(g1), equalTo(-1));
        assertThat(g1.compareTo(g0), equalTo(1));

        List<Group> groups = new ArrayList<>();
        g0 = new Group("d");
        groups.add(g0);
        g1 = new Group("c");
        groups.add(g1);
        Group g2 = new Group("b");
        groups.add(g2);
        Group g3 = new Group("a");
        groups.add(g3);

        assertThat(groups.get(0).getPath(), equalTo("d"));
        assertThat(groups.get(1).getPath(), equalTo("c"));
        assertThat(groups.get(2).getPath(), equalTo("b"));
        assertThat(groups.get(3).getPath(), equalTo("a"));

        Collections.sort(groups);

        assertThat(groups.get(0).getPath(), equalTo("a"));
        assertThat(groups.get(1).getPath(), equalTo("b"));
        assertThat(groups.get(2).getPath(), equalTo("c"));
        assertThat(groups.get(3).getPath(), equalTo("d"));

        groups = new ArrayList<>();
        List<Person> list0 = new ArrayList<>();
        list0.add(new Person("a"));
        list0.add(new Person("b"));
        list0.add(new Person("c"));
        list0.add(new Person("d"));
        g0 = new Group("A", list0);
        groups.add(g0);
        g1 = new Group("A", list0.subList(1, 4));
        groups.add(g1);
        g2 = new Group("A", list0.subList(2, 4));
        groups.add(g2);
        g3 = new Group("A", list0.subList(3, 4));
        groups.add(g3);

        assertThat(groups.get(0).getMembers().size(), equalTo(4));
        assertThat(groups.get(1).getMembers().size(), equalTo(3));
        assertThat(groups.get(2).getMembers().size(), equalTo(2));
        assertThat(groups.get(3).getMembers().size(), equalTo(1));

        Collections.sort(groups);

        assertThat(groups.get(0).getMembers().size(), equalTo(1));
        assertThat(groups.get(1).getMembers().size(), equalTo(2));
        assertThat(groups.get(2).getMembers().size(), equalTo(3));
        assertThat(groups.get(3).getMembers().size(), equalTo(4));

        List<Person> list1 = new ArrayList<>();
        list1.add(new Person("a"));
        list1.add(new Person("b"));
        list1.add(new Person("c"));
        list1.add(new Person("d"));

        g0 = new Group("a", list0);
        g1 = new Group("a", list1);
        assertThat(g0.compareTo(g1), equalTo(0));
        assertThat(g1, is(g0));
        assertThat(g0, is(g1));

        g1.getMembers().get(3).setName("e");
        assertThat(g0.compareTo(g1), equalTo(-1));
        assertThat(g1.compareTo(g0), equalTo(01));
    }

    @Test
    public void testToString() {
        assertThat(group.toString(), equalTo("Group [path=, members=[]]"));

        group = new Group("eno");
        assertThat(group.toString(), equalTo("Group [path=eno, members=[]]"));

        group = new Group("fripp", new ArrayList<Person>());
        assertThat(group.toString(), equalTo("Group [path=fripp, members=[]]"));

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("A", "B", "C"));
        group = new Group("manzanera", persons);
        String expected = "Group [path=manzanera, "
                + "members=[Person [name=A, uhUuid=B, username=C]]]";
        assertThat(group.toString(), equalTo(expected));
    }

}

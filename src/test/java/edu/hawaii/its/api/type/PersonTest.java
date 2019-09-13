package edu.hawaii.its.api.type;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PersonTest {

    private Person person;

    @Before
    public void setUp() {
        person = new Person();
    }

    @Test
    public void construction() {
        assertNotNull(person);
        assertNull(person.getName());
        assertNull(person.getUuid());
        assertNull(person.getUsername());

        person = new Person("a","b","c","d","e");
        assertThat(person.getName(), equalTo("a"));
        assertThat(person.getUuid(), equalTo("b"));
        assertThat(person.getUsername(), equalTo("c"));
        assertThat(person.getFirstName(),equalTo("d"));
        assertThat(person.getLastName(),equalTo("e"));
    }

    @Test
    public void accessors() {
        assertNull(person.getName());
        assertNull(person.getUuid());
        assertNull(person.getUsername());

        person.setName("name");
        assertThat(person.getName(), equalTo("name"));
        assertNull(person.getUuid());
        assertNull(person.getUsername());

        person.setUuid("uuid");
        assertThat(person.getName(), equalTo("name"));
        assertThat(person.getUuid(), equalTo("uuid"));
        assertNull(person.getUsername());

        person.setUsername("username");
        assertThat(person.getName(), equalTo("name"));
        assertThat(person.getUuid(), equalTo("uuid"));
        assertThat(person.getUsername(), equalTo("username"));


        person.setLastName("last");
        person.setFirstName("first");
        assertThat(person.getFirstName(),equalTo("first"));
        assertThat(person.getLastName(),equalTo("last"));

        Map<String,String> attributes = new HashMap<>();
        attributes.put("name","user");
        person = new Person(attributes);
        assertThat(person.getAttributes().toString(),equalTo("{name=user}"));
        assertThat(person.getAttribute("name"),equalTo("user"));
        person.setAttribute("name","user");
        assertThat(person.getAttribute("name"),equalTo("user"));
        person.setAttributes(attributes);
        assertThat(person.getAttributes().toString(),equalTo("{name=user}"));


    }

    @Test
    public void equals() {
        Person p0 = new Person();
        assertNotEquals(p0, null);
        assertNotEquals(p0, new String());
        assertEquals(p0, p0);

        Person p1 = new Person();
        assertEquals(p0, p1);
        assertEquals(p1, p0);

        p0.setName("name");
        assertNotEquals(p0, p1);
        assertNotEquals(p1, p0);
        p1.setName("name");
        assertEquals(p0, p1);
        assertEquals(p1, p0);

        p0.setUuid("uuid");
        assertNotEquals(p0, p1);
        assertNotEquals(p1, p0);
        p1.setUuid("uuid");
        assertEquals(p0, p1);
        assertEquals(p1, p0);

        p0.setUsername("username");
        assertNotEquals(p0, p1);
        assertNotEquals(p1, p0);
        p1.setUsername("username");
        assertEquals(p0, p1);
        assertEquals(p1, p0);
    }

    @Test
    public void testHashCode() {
        assertThat(person.getName(), equalTo(null));
        assertThat(person.getUuid(), equalTo(null));
        assertThat(person.getUsername(), equalTo(null));

        final int prime = 31;
        int hashCode = person.hashCode();
        assertTrue(hashCode > 31);

        int result = 1;
        result = prime * result + "name".hashCode();
        result = prime * result + 0;
        result = prime * result + 0;
        person.setName("name");
        assertThat(person.hashCode(), equalTo(result));

        result = 1;
        result = prime * result + 0;
        result = prime * result + "username".hashCode();
        result = prime * result + 0;
        person.setName(null);
        person.setUsername("username");
        person.setUuid(null);
        assertThat(person.hashCode(), equalTo(result));

        result = 1;
        result = prime * result + 0;
        result = prime * result + 0;
        result = prime * result + "uuid".hashCode();
        person.setName(null);
        person.setUsername(null);
        person.setUuid("uuid");
        assertThat(person.hashCode(), equalTo(result));

    }

    @Test
    public void compareTo() {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("d"));
        persons.add(new Person("c"));
        persons.add(new Person("b"));
        persons.add(new Person("a"));

        assertThat(persons.get(0).getName(), equalTo("d"));
        assertThat(persons.get(1).getName(), equalTo("c"));
        assertThat(persons.get(2).getName(), equalTo("b"));
        assertThat(persons.get(3).getName(), equalTo("a"));

        Collections.sort(persons);

        assertThat(persons.get(0).getName(), equalTo("a"));
        assertThat(persons.get(1).getName(), equalTo("b"));
        assertThat(persons.get(2).getName(), equalTo("c"));
        assertThat(persons.get(3).getName(), equalTo("d"));

        // Again.
        persons = new ArrayList<>();
        persons.add(new Person("", "p", ""));
        persons.add(new Person("", "o", ""));
        persons.add(new Person("", "n", ""));
        persons.add(new Person("", "m", ""));

        assertThat(persons.get(0).getName(), equalTo(""));
        assertThat(persons.get(1).getName(), equalTo(""));
        assertThat(persons.get(2).getName(), equalTo(""));
        assertThat(persons.get(3).getName(), equalTo(""));
        assertThat(persons.get(0).getUuid(), equalTo("p"));
        assertThat(persons.get(1).getUuid(), equalTo("o"));
        assertThat(persons.get(2).getUuid(), equalTo("n"));
        assertThat(persons.get(3).getUuid(), equalTo("m"));

        Collections.sort(persons);

        assertThat(persons.get(0).getName(), equalTo(""));
        assertThat(persons.get(1).getName(), equalTo(""));
        assertThat(persons.get(2).getName(), equalTo(""));
        assertThat(persons.get(3).getName(), equalTo(""));
        assertThat(persons.get(0).getUuid(), equalTo("m"));
        assertThat(persons.get(1).getUuid(), equalTo("n"));
        assertThat(persons.get(2).getUuid(), equalTo("o"));
        assertThat(persons.get(3).getUuid(), equalTo("p"));

        // Again.
        persons = new ArrayList<>();
        persons.add(new Person("", "", "z"));
        persons.add(new Person("", "", "y"));
        persons.add(new Person("", "", "x"));
        persons.add(new Person("", "", "w"));

        assertThat(persons.get(0).getUsername(), equalTo("z"));
        assertThat(persons.get(1).getUsername(), equalTo("y"));
        assertThat(persons.get(2).getUsername(), equalTo("x"));
        assertThat(persons.get(3).getUsername(), equalTo("w"));

        Collections.sort(persons);

        assertThat(persons.get(0).getUsername(), equalTo("w"));
        assertThat(persons.get(1).getUsername(), equalTo("x"));
        assertThat(persons.get(2).getUsername(), equalTo("y"));
        assertThat(persons.get(3).getUsername(), equalTo("z"));

        // Again.
        persons = new ArrayList<>();
        persons.add(new Person(null, null, "4"));
        persons.add(new Person(null, null, "3"));
        persons.add(new Person(null, null, "2"));
        persons.add(new Person(null, null, "1"));

        assertThat(persons.get(0).getUsername(), equalTo("4"));
        assertThat(persons.get(1).getUsername(), equalTo("3"));
        assertThat(persons.get(2).getUsername(), equalTo("2"));
        assertThat(persons.get(3).getUsername(), equalTo("1"));

        Collections.sort(persons);

        assertThat(persons.get(0).getUsername(), equalTo("1"));
        assertThat(persons.get(1).getUsername(), equalTo("2"));
        assertThat(persons.get(2).getUsername(), equalTo("3"));
        assertThat(persons.get(3).getUsername(), equalTo("4"));
    }
}

package edu.hawaii.its.api.type;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;
import javax.print.DocFlavor;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
public class Person implements Comparable<Person> {

    //todo get these strings to work from a config file, or just wait until we remove the values in a week or two?
    @Transient
    private static String COMPOSITE_NAME = "cn";
    @Transient
    private static String FIRST_NAME = "givenName";
    @Transient
    private static String LAST_NAME = "sn";
    @Transient
    private static String UUID = "uuid";
    @Transient
    private static String USERNAME = "uid";

    private Map<String, String> attributes = new HashMap<>();

    // Constructor.
    public Person() {
        // Empty.
    }

    // Constructor.
    public Person(String name) {
        this();
        attributes.put(COMPOSITE_NAME, name);
    }

    // Constructor.
    public Person(String name, String uuid, String username) {
        this(name);

        attributes.put(UUID, uuid);
        attributes.put(USERNAME, username);
    }

    // Constructor.
    public Person(String name, String uuid, String username, String firstName, String lastName) {
        this(name, uuid, username);

        attributes.put(FIRST_NAME, firstName);
        attributes.put(LAST_NAME, lastName);
    }

    // Constructor.
    public Person(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Id
    @Column
    public String getUsername() {
        return attributes.get(USERNAME);
    }

    public void setUsername(String username) {
        attributes.put(USERNAME, username);
    }

    @Column(name = "FullName")
    public String getName() {
        return attributes.get(COMPOSITE_NAME);
    }

    public void setName(String name) {
        attributes.put(COMPOSITE_NAME, name);
    }

    @Column
    public String getUuid() {
        return attributes.get(UUID);
    }

    public void setUuid(String uuid) {
        attributes.put(UUID, uuid);
    }

    @Column(name = "FirstName")
    public String getFirstName() {
        return attributes.get(FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        attributes.put(FIRST_NAME, firstName);
    }

    @Column(name = "LastName")
    public String getLastName() {
        return attributes.get(LAST_NAME);
    }

    public void setLastName(String lastName) {
        attributes.put(LAST_NAME, lastName);
    }

    @ElementCollection
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Transient
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        String name = getName();
        String username = getUsername();
        String uuid = getUuid();

        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        String name = getName();
        String username = getUsername();
        String uuid = getUuid();

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        if (username == null) {
            if (other.getUsername() != null)
                return false;
        } else if (!username.equals(other.getUsername()))
            return false;
        if (uuid == null) {
            return other.getUuid() == null;
        } else return uuid.equals(other.getUuid());
    }

    @Override
    public int compareTo(Person person) {
        Comparator<String> nullSafeComparator = Comparator.nullsFirst(String::compareTo);

        int usernameComp = nullSafeComparator.compare(getUsername(), person.getUsername());

        if (usernameComp != 0) {
            return usernameComp;
        }

        int nameComp = nullSafeComparator.compare(getName(), person.getName());
        if (nameComp != 0) {
            return nameComp;
        }

        int uuidComp = nullSafeComparator.compare(getUuid(), person.getUuid());
        if (uuidComp != 0) {
            return uuidComp;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Person [name=" + getName() + ", uuid=" + getUuid() + ", username=" + getUsername() + "]";
    }

}

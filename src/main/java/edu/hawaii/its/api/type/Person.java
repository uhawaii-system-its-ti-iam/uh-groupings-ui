package edu.hawaii.its.api.type;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;
import org.springframework.beans.factory.annotation.Value;

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

    @Id
    @Column
    private String username;

    @Column(name = "FullName")
    private String name;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column
    private String uuid;

    //todo add this to the database
    @Transient
    private Map<String, String> attributes = new HashMap<>();

    // Constructor.
    public Person() {
        // Empty.
    }

    // Constructor.
    public Person(String name) {
        this();
        this.name = name;

        attributes.put(COMPOSITE_NAME, name);
    }

    // Constructor.
    public Person(String name, String uuid, String username) {
        this(name);
        this.uuid = uuid;
        this.username = username;

        attributes.put(UUID, uuid);
        attributes.put(USERNAME, username);
    }

    // Constructor.
    public Person(String name, String uuid, String username, String firstName, String lastName) {
        this(name, uuid, username);
        this.firstName = firstName;
        this.lastName = lastName;

        attributes.put(FIRST_NAME, firstName);
        attributes.put(LAST_NAME, lastName);
    }

    // Constructor.
    public Person(Map<String, String> attributes) {
        this.attributes = attributes;
        this.name = attributes.get(COMPOSITE_NAME);
        this.uuid = attributes.get(UUID);
        this.username = attributes.get(USERNAME);
        this.firstName = attributes.get(FIRST_NAME);
        this.lastName = attributes.get(LAST_NAME);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
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
        return "Person [name=" + name + ", uuid=" + uuid + ", username=" + username + "]";
    }

}

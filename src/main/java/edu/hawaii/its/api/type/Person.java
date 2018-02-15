package edu.hawaii.its.api.type;

import java.util.Comparator;
import java.util.Map;
import java.util.jar.Attributes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
public class Person implements Comparable<Person> {
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
    private Attributes attributes;

    // Constructor.
    public Person() {
        // Empty.
    }

    // Constructor.
    public Person(String name) {
        this();
        this.name = name;
    }

    // Constructor.
    public Person(String name, String uuid, String username) {
        this(name);
        this.uuid = uuid;
        this.username = username;
    }

    // Constructor.
    public Person(String name, String uuid, String username, String firstName, String lastName) {
        this(name);
        this.uuid = uuid;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
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

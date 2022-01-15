package edu.hawaii.its.api.type;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Person implements Comparable<Person> {

    private static String COMPOSITE_NAME = "cn";

    private static String FIRST_NAME = "givenName";

    private static String LAST_NAME = "sn";

    private static String UHUUID = "uhUuid";

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
    public Person(String name, String uhUuid, String username) {
        this(name);

        attributes.put(UHUUID, uhUuid);
        attributes.put(USERNAME, username);
    }

    // Constructor.
    public Person(String name, String uhUuid, String username, String firstName, String lastName) {
        this(name, uhUuid, username);

        attributes.put(FIRST_NAME, firstName);
        attributes.put(LAST_NAME, lastName);
    }

    // Constructor.
    public Person(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getUsername() {
        return attributes.get(USERNAME);
    }

    public void setUsername(String username) {
        attributes.put(USERNAME, username);
    }

    public String getName() {
        return attributes.get(COMPOSITE_NAME);
    }

    public void setName(String name) {
        attributes.put(COMPOSITE_NAME, name);
    }

    public String getUhUuid() {
        return attributes.get(UHUUID);
    }

    public void setUhUuid(String uhUuid) {
        attributes.put(UHUUID, uhUuid);
    }

    public String getFirstName() {
        return attributes.get(FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        attributes.put(FIRST_NAME, firstName);
    }

    public String getLastName() {
        return attributes.get(LAST_NAME);
    }

    public void setLastName(String lastName) {
        attributes.put(LAST_NAME, lastName);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

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
        String uhUuid = getUhUuid();

        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((uhUuid == null) ? 0 : uhUuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        String name = getName();
        String username = getUsername();
        String uhUuid = getUhUuid();

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
        if (uhUuid == null) {
            return other.getUhUuid() == null;
        } else
            return uhUuid.equals(other.getUhUuid());
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

        int uhUuidComp = nullSafeComparator.compare(getUhUuid(), person.getUhUuid());
        if (uhUuidComp != 0) {
            return uhUuidComp;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Person [name=" + getName() + ", uhUuid=" + getUhUuid() + ", username=" + getUsername() + "]";
    }

}

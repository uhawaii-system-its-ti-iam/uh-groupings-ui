package edu.hawaii.its.api.type;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "groups")
public class Group implements Comparable<Group> {
    private List<Person> members = new ArrayList<>();
    private String path = "";

    // Constructor.
    public Group() {
        // Empty.
    }

    // Constructor.
    public Group(List<Person> members) {
        setMembers(members);
    }

    // Constructor.
    public Group(String path) {
        this.path = path;
    }

    // Constructor.
    public Group(String path, List<Person> members) {
        this(members);
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Id
    @Column(name = "path")
    public String getPath() {
        return path;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members != null ? members : new ArrayList<>();
    }

    public void addMember(Person person) {
        members.add(person);
    }

    @Transient
    public boolean isMember(Person person) {
        return members.contains(person);
    }

    @Transient
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (Person person : members) {
            names.add(person.getName());
        }
        return names;
    }

    @Transient
    public List<String> getUuids() {
        List<String> uuids = new ArrayList<>();
        for (Person person : members) {
            uuids.add(person.getUuid());
        }
        return uuids;
    }

    @Transient
    public List<String> getUsernames() {
        List<String> usernames = new ArrayList<>();
        for (Person person : members) {
            usernames.add(person.getUsername());
        }
        return usernames;
    }

    @Transient
    @Override
    public boolean equals(Object o) {
        return (o instanceof Group) && (compareTo((Group) o) == 0);
    }

    @Transient
    @Override
    public int compareTo(Group group) {
        int pathComp = getPath().compareTo(group.getPath());
        if (pathComp != 0) {
            return pathComp;
        }

        for (int i = 0; i < getMembers().size(); i++) {
            int personComp = getMembers().get(i).compareTo(group.getMembers().get(i));
            if (personComp != 0) {
                return personComp;
            }
        }

        return 0;
    }

    @Transient
    @Override
    public String toString() {
        return "Group [members=" + members + "]";
    }
}

package edu.hawaii.its.api.type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {
    private List<Person> members = new ArrayList<>();

    private String path = "";

    public Group() {
        //empty
    }

    public Group(List<Person> members) {
        this.members = members;
    }

    public Group(String path) {
        this.path = path;
    }

    public Group(String path, List<Person> members) {
        this.members = members;
        this.path = path;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<Person> getMembers() {
        return members;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Id
    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public void addMember(Person person) {
        members.add(person);
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
    public String toString() {
        return "Group [members=" + members + "]";
    }
}

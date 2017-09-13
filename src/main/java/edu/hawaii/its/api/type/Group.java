package edu.hawaii.its.api.type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Group {
    @Column
    @ElementCollection(targetClass = Person.class)
    private List<Person> members = new ArrayList<>();
    @Id
    @Column
    private String path = "";

    public Group(){
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

    public List<Person> getMembers() {
        return members;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public void addMember(Person person) {
        members.add(person);
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (Person person : members) {
            names.add(person.getName());
        }
        return names;
    }

    public List<String> getUuids() {
        List<String> uuids = new ArrayList<>();
        for (Person person : members) {
            uuids.add(person.getUuid());
        }
        return uuids;
    }

    public List<String> getUsernames() {
        List<String> usernames = new ArrayList<>();
        for (Person person : members) {
            usernames.add(person.getUsername());
        }
        return usernames;
    }

    @Override
    public String toString() {
        return "Group [members=" + members + "]";
    }
}

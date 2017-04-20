package edu.hawaii.its.groupings.api.type;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private List<Person> members = new ArrayList<>();

    public List<Person> getMembers() {
        return members;
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

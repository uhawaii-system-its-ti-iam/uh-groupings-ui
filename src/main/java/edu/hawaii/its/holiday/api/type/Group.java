package edu.hawaii.its.holiday.api.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zknoebel on 3/30/2017.
 */
public class Group {
    private List<Person> members;

    public Group(List<Person> members){
        this.members = members;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public List<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for(Person person: members){
            names.add(person.getName());
        }
        return names;
    }

    public List<String> getUuids(){
        ArrayList<String> uuids = new ArrayList<>();
        for(Person person: members){
            uuids.add(person.getUuid());
        }
        return uuids;
    }

    public List<String> getUsernames(){

        ArrayList<String> usernames = new ArrayList<>();
        for(Person person: members){
            usernames.add(person.getUsername());
        }
        return usernames;
    }
}

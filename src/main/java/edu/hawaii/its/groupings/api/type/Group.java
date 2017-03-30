package edu.hawaii.its.groupings.api.type;

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
}

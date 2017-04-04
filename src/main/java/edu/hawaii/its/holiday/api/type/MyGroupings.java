package edu.hawaii.its.holiday.api.type;

import java.util.List;

/**
 * Created by zknoebel on 4/4/2017.
 */
public class MyGroupings {
    List<Grouping> groupingsIn;
    List<Grouping> groupingsOwned;
    List<Grouping> groupingsToOptOutOf;
    List<Grouping> groupingsToOptInTo;

    public MyGroupings(List<Grouping> groupingsIn, List<Grouping> groupingsOwned, List<Grouping> groupingsToOptInTo, List<Grouping> groupingsToOptOutOf){
        this.groupingsIn = groupingsIn;
        this.groupingsOwned = groupingsOwned;
        this.groupingsToOptInTo = groupingsToOptInTo;
        this.groupingsToOptOutOf = groupingsToOptOutOf;
    }

    public MyGroupings(){

    }

    public List<Grouping> getGroupingsIn() {
        return groupingsIn;
    }

    public void setGroupingsIn(List<Grouping> groupingsIn) {
        this.groupingsIn = groupingsIn;
    }

    public List<Grouping> getGroupingsOwned() {
        return groupingsOwned;
    }

    public void setGroupingsOwned(List<Grouping> groupingsOwned) {
        this.groupingsOwned = groupingsOwned;
    }

    public List<Grouping> getGroupingsToOptOutOf() {
        return groupingsToOptOutOf;
    }

    public void setGroupingsToOptOutOf(List<Grouping> groupingsToOptOutOf) {
        this.groupingsToOptOutOf = groupingsToOptOutOf;
    }

    public List<Grouping> getGroupingsToOptInTo() {
        return groupingsToOptInTo;
    }

    public void setGroupingsToOptInTo(List<Grouping> groupingsToOptInTo) {
        this.groupingsToOptInTo = groupingsToOptInTo;
    }
}

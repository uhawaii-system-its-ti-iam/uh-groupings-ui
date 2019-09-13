package edu.hawaii.its.api.type;

import java.util.List;


public class GroupingAssignment {

   private List<Grouping> groupingsIn;
   private List<Grouping> groupingsOwned;
   private List<Grouping> groupingsOptedOutOf;
   private List<Grouping> groupingsOptedInTo;
   private List<Grouping> groupingsToOptOutOf;
   private List<Grouping> groupingsToOptInTo;

    public GroupingAssignment(){

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

    public List<Grouping> getGroupingsOptedOutOf() {
        return groupingsOptedOutOf;
    }

    public void setGroupingsOptedOutOf(List<Grouping> groupingsOptedOutOf) {
        this.groupingsOptedOutOf = groupingsOptedOutOf;
    }

    public List<Grouping> getGroupingsOptedInTo() {
        return groupingsOptedInTo;
    }

    public void setGroupingsOptedInTo(List<Grouping> groupingsOptedInTo) {
        this.groupingsOptedInTo = groupingsOptedInTo;
    }


}

package edu.hawaii.its.groupings.api.type;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class MyGroupings{
    List<Grouping> groupingsIn;
    List<Grouping> groupingsOwned;
    List<Grouping> groupingsOptedOutOf;
    List<Grouping> groupingsOptedInTo;
    List<Grouping> groupingsToOptOutOf;
    List<Grouping> groupingsToOptInTo;

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

package edu.hawaii.its.api.type;

import java.util.ArrayList;
import java.util.List;

public class AdminListsHolder {
    List<Grouping> allGroupings;
    Group adminGroup;

    public AdminListsHolder() {
        this.allGroupings = new ArrayList<>();
        this.adminGroup = new EmptyGroup();
    }

    public AdminListsHolder(List<Grouping> allGroupings, Group adminGroup) {
        this.allGroupings = allGroupings;
        this.adminGroup = adminGroup;
    }

    public List<Grouping> getAllGroupings() {
        return allGroupings;
    }

    public void setAllGroupings(List<Grouping> allGroupings) {
        this.allGroupings = allGroupings;
    }

    public Group getAdminGroup() {
        return adminGroup;
    }

    public void setAdminGroup(Group adminGroup) {
        this.adminGroup = adminGroup;
    }
}

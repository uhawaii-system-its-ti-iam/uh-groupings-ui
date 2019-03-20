package edu.hawaii.its.api.type;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GroupingAssignmentTest {

    private GroupingAssignment groupingAssignment;

    @Before
    public void setUp() {
        groupingAssignment = new GroupingAssignment();
    }

    @Test
    public void nullTest(){
        assertNull(groupingAssignment.getGroupingsIn());
        assertNull(groupingAssignment.getGroupingsOptedInTo());
        assertNull(groupingAssignment.getGroupingsOptedOutOf());
        assertNull(groupingAssignment.getGroupingsToOptInTo());
        assertNull(groupingAssignment.getGroupingsToOptOutOf());
        assertNull(groupingAssignment.getGroupingsOwned());
    }

    @Test
    public void groupingAssignmentTest() {
        List<Grouping> newList = new ArrayList<Grouping>();
        groupingAssignment.setGroupingsIn(newList);
        assertThat(groupingAssignment.getGroupingsIn().toString(),equalTo("[]"));
        groupingAssignment.setGroupingsOptedInTo(newList);
        assertThat(groupingAssignment.getGroupingsOptedInTo().toString(),equalTo("[]"));
        groupingAssignment.setGroupingsOptedOutOf(newList);
        assertThat(groupingAssignment.getGroupingsOptedOutOf().toString(),equalTo("[]"));
        groupingAssignment.setGroupingsToOptInTo(newList);
        assertThat(groupingAssignment.getGroupingsToOptInTo().toString(),equalTo("[]"));
        groupingAssignment.setGroupingsToOptOutOf(newList);
        assertThat(groupingAssignment.getGroupingsToOptOutOf().toString(),equalTo("[]"));
        groupingAssignment.setGroupingsOwned(newList);
        assertThat(groupingAssignment.getGroupingsOwned().toString(),equalTo("[]"));
    }
}
package edu.hawaii.its.api.type;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GroupingTest {

    private Grouping grouping;
    private Group group;
    private String emptyGroup;
    private boolean Test = true;
    private List<SyncDestination> destinations = new ArrayList<>();

    @Before
    public void setUp(){
        grouping = new Grouping();
        group = new Group();
        emptyGroup = "Group [path=, members=[]]";
        destinations.add(new SyncDestination("test:released", "released"));
        destinations.add(new SyncDestination("test:listserv", "listserv"));
        grouping.setSyncDestinations(destinations);
    }

    @Test
    public void groupingTest(){

    grouping.setOwners(group);
    assertThat(grouping.getOwners().toString(),equalTo(emptyGroup));
    grouping.setOwners(null);

    grouping.setPath("path");
    assertThat(grouping.getPath(),equalTo("path"));
    grouping.setPath(null);

    assertThat(grouping.getName(), equalTo(""));
    grouping.setInclude(group);
    assertThat(grouping.getInclude().toString(),equalTo(emptyGroup));
    grouping.setInclude(null);

    grouping.setExclude(group);
    assertThat(grouping.getExclude().toString(),equalTo(emptyGroup));
    grouping.setExclude(null);

    grouping.setBasis(group);
    assertThat(grouping.getBasis().toString(),equalTo(emptyGroup));
    grouping.setBasis(null);

    grouping.setComposite(group);
    assertThat(grouping.getComposite().toString(),equalTo(emptyGroup));
    grouping.setComposite(null);

    grouping.setOptInOn(Test);
    assertTrue(grouping.isOptInOn());
    grouping.setOptOutOn(Test);
    assertTrue(grouping.isOptOutOn());
    grouping.setSyncDestination("test:listserv", Test);
    assertTrue(grouping.isSyncDestinationOn("test:listserv"));
    grouping.setSyncDestination("test:released", Test);
    assertTrue(grouping.isSyncDestinationOn("test:released"));
    assertNotNull(grouping.toString());
    }
}

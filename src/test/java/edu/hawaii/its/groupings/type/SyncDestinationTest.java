package edu.hawaii.its.groupings.type;

import org.junit.Before;
import org.junit.Test;
import edu.hawaii.its.api.type.SyncDestination;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SyncDestinationTest {

    private SyncDestination destination;

    @Before
    public void setUp() { destination = new SyncDestination(); }

    @Test
    public void construction() {
        assertNotNull(destination);
        assertNull(destination.getName());
        assertNull(destination.getDescription());
        assertNull(destination.getTooltip());
        assertNull(destination.getIsSynced());

        destination = new SyncDestination("name", "description");
        assertThat(destination.getName(), equalTo("name"));
        assertThat(destination.getDescription(), equalTo("description"));
        assertNull(destination.getIsSynced());
        assertNull(destination.getTooltip());
    }

    @Test
    public void accessors() {
        assertNull(destination.getName());
        assertNull(destination.getDescription());
        assertNull(destination.getTooltip());
        assertNull(destination.getIsSynced());

        destination.setIsSynced(true);
        assertTrue(destination.getIsSynced());
        assertNull(destination.getTooltip());
        assertNull(destination.getDescription());
        assertNull(destination.getName());

        destination.setDescription("description");
        assertTrue(destination.getIsSynced());
        assertThat(destination.getDescription(), equalTo("description"));
        assertNull(destination.getName());
        assertNull(destination.getTooltip());

        destination.setName("name");
        assertThat(destination.getName(), equalTo("name"));
        assertThat(destination.getDescription(), equalTo("description"));
        assertTrue(destination.getIsSynced());
        assertNull(destination.getTooltip());

        destination.setTooltip("tooltip");
        assertThat(destination.getTooltip(), equalTo("tooltip"));
        assertThat(destination.getName(), equalTo("name"));
        assertThat(destination.getDescription(), equalTo("description"));
        assertTrue(destination.getIsSynced());
    }

    @Test
    public void parseKeyVal() {
        String desc = "this is a description";
        String descReg = "this is ${} description with regex characters";
        String replacer = "replaced";


        assertThat(desc = destination.parseKeyVal(replacer, desc), equalTo("this is a description"));

        assertThat(descReg = destination.parseKeyVal(replacer, descReg), equalTo("this is replaced description with regex characters"));


    }

}

package edu.hawaii.its.holiday.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TypeTest {

    private Type type;

    @Before
    public void setUp() {
        type = new Type();
    }

    @Test
    public void construction() {
        assertNotNull(type);
    }

    @Test
    public void setters() {
        assertNotNull(type);
        assertNull(type.getId());
        assertNull(type.getDescription());
        assertNull(type.getVersion());

        type.setId(666);
        type.setDescription("The Beast");
        type.setVersion(9);
        assertThat(type.getId(), equalTo(666));
        assertThat(type.getDescription(), equalTo("The Beast"));
        assertThat(type.getVersion(), equalTo(9));
    }

    @Test
    public void testToString() {
        assertThat(type.toString(), containsString("id=null, description=null"));

        type.setId(12345);
        assertThat(type.toString(), containsString("Type [id=12345,"));
    }
}

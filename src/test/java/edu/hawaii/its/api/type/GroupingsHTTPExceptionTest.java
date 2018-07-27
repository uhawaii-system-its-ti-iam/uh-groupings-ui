package edu.hawaii.its.api.type;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class GroupingsHTTPExceptionTest extends RuntimeException {

    private GroupingsHTTPException groupingsHTTPexception;

    @Before
    public void setUp() {
        groupingsHTTPexception = new GroupingsHTTPException();
    }


    @Test
    public void ConstructionGroupingsHTTPException() {
        assertNotNull(groupingsHTTPexception);
        assertNull(groupingsHTTPexception.getExceptionMessage());
        assertNull(groupingsHTTPexception.getMessage());
        assertNull(groupingsHTTPexception.getCause());
    }

    @Test
    public void testGroupingsHTTPException() {

        Exception e = new RuntimeException("Test Case");

        groupingsHTTPexception = new GroupingsHTTPException("Error", e, 1);
        assertThat(groupingsHTTPexception.getMessage(), equalTo("Error"));
        assertThat(groupingsHTTPexception.getCause(), equalTo(e));
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(1));

        Exception e1 = new RuntimeException("File not found");

        groupingsHTTPexception = new GroupingsHTTPException("Error 1", e1);
        assertThat(groupingsHTTPexception.getMessage(), equalTo("Error 1"));
        assertThat(groupingsHTTPexception.getCause(), equalTo(e1));
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(0));

        groupingsHTTPexception = new GroupingsHTTPException("Error 2");
        assertThat(groupingsHTTPexception.getMessage(), equalTo("Error 2"));
        assertNull(groupingsHTTPexception.getCause());
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(0));
    }
}
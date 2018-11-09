package edu.hawaii.its.api.type;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class GroupingsHTTPExceptionTest extends RuntimeException {

    private GroupingsHTTPException groupingsHTTPexception;

    @Before
    public void setUp() {
        groupingsHTTPexception = new GroupingsHTTPException();
    }

    @Test
    public void GroupingsHTTPExceptionConstructionTest() {
        assertNotNull(groupingsHTTPexception);
        assertNull(groupingsHTTPexception.getLocalizedMessage());
        assertNull(groupingsHTTPexception.getMessage());
        assertNull(groupingsHTTPexception.getCause());
        // Status code defaults to 0 if not set
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(0));
    }

    @Test
    public void GroupingsHTTPExceptionExpandedTest() {

        Exception e = new RuntimeException("Test Case");

        groupingsHTTPexception = new GroupingsHTTPException("Error", e, 1);
        assertThat(groupingsHTTPexception.getMessage(), equalTo("Error"));
        assertThat(groupingsHTTPexception.getCause(), equalTo(e));
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(1));
        assertThat(groupingsHTTPexception.getExceptionMessage(), notNullValue());
        assertThat(groupingsHTTPexception.getStackTrace(), notNullValue());

        Exception e1 = new RuntimeException("File not found");

        groupingsHTTPexception = new GroupingsHTTPException("Error 1", e1);
        assertThat(groupingsHTTPexception.getMessage(), equalTo("Error 1"));
        assertThat(groupingsHTTPexception.getCause(), equalTo(e1));
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(0));
        assertThat(groupingsHTTPexception.getExceptionMessage(), notNullValue());
        assertThat(groupingsHTTPexception.getStackTrace(), notNullValue());

        groupingsHTTPexception = new GroupingsHTTPException("Error 2");
        assertThat(groupingsHTTPexception.getMessage(), equalTo("Error 2"));
        assertNull(groupingsHTTPexception.getCause());
        assertThat(groupingsHTTPexception.getStatusCode(), equalTo(0));
        assertThat(groupingsHTTPexception.getExceptionMessage(), nullValue());
        assertThat(groupingsHTTPexception.getStackTrace(), notNullValue());
    }
}

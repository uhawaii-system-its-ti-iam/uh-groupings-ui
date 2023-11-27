package edu.hawaii.its.api.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupingsHTTPExceptionTest extends RuntimeException {

    private GroupingsHTTPException groupingsHTTPexception;

    @BeforeEach
    public void setUp() {
        groupingsHTTPexception = new GroupingsHTTPException();
    }

    @Test
    public void groupingsHTTPExceptionConstructionTest() {
        assertNotNull(groupingsHTTPexception);
        assertNull(groupingsHTTPexception.getLocalizedMessage());
        assertNull(groupingsHTTPexception.getMessage());
        assertNull(groupingsHTTPexception.getCause());
        // Status code defaults to 0 if not set
        assertThat(groupingsHTTPexception.getStatusCode(), is(0));
    }

    @Test
    public void groupingsHTTPExceptionExpandedTest() {

        Exception e = new RuntimeException("Test Case");

        groupingsHTTPexception = new GroupingsHTTPException("Error", e, 1);
        assertThat(groupingsHTTPexception.getMessage(), is("Error"));
        assertThat(groupingsHTTPexception.getCause(), is(e));
        assertThat(groupingsHTTPexception.getStatusCode(), is(1));
        assertThat(groupingsHTTPexception.getExceptionMessage(), notNullValue());
        assertThat(groupingsHTTPexception.getStackTrace(), notNullValue());

        Exception e1 = new RuntimeException("File not found");

        groupingsHTTPexception = new GroupingsHTTPException("Error 1", e1);
        assertThat(groupingsHTTPexception.getMessage(), is("Error 1"));
        assertThat(groupingsHTTPexception.getCause(), is(e1));
        assertThat(groupingsHTTPexception.getStatusCode(), is(0));
        assertThat(groupingsHTTPexception.getExceptionMessage(), notNullValue());
        assertThat(groupingsHTTPexception.getStackTrace(), notNullValue());

        groupingsHTTPexception = new GroupingsHTTPException("Error 2");
        assertThat(groupingsHTTPexception.getMessage(), is("Error 2"));
        assertNull(groupingsHTTPexception.getCause());
        assertThat(groupingsHTTPexception.getStatusCode(), is(0));
        assertThat(groupingsHTTPexception.getExceptionMessage(), nullValue());
        assertThat(groupingsHTTPexception.getStackTrace(), notNullValue());
    }
}

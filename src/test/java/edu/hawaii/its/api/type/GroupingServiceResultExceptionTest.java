package edu.hawaii.its.api.type;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.Before;

public class GroupingServiceResultExceptionTest extends GroupingsServiceResult {

    private GroupingsServiceResultException groupingsServiceResultException;

    @Before
    public void setup(){
        groupingsServiceResultException = new GroupingsServiceResultException();
    }

    @Test
    public void construction(){
        assertNotNull(groupingsServiceResultException);
        assertNull(groupingsServiceResultException.getGsr());
        groupingsServiceResultException.setGsr(new GroupingsServiceResult("resultCode0", "404"));
        String test = "GroupingsServiceResult "+
                "[action=404, resultCode=resultCode0]";
        String expected = test.replaceAll("\\\\","");
        assertThat(groupingsServiceResultException.getGsr().toString(), equalTo(expected));
    }
}
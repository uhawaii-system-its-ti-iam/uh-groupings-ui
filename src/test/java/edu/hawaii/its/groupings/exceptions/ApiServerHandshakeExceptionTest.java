package edu.hawaii.its.groupings.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ApiServerHandshakeExceptionTest {

    @Test
    public void construction() {
        InitializationException ex = new ApiServerHandshakeException("fail");
        assertNotNull(ex);
        assertThat(ex.getMessage(), equalTo("fail"));

        ex = new ApiServerHandshakeException("stop", new Throwable("me"));
        assertNotNull(ex);
        assertThat(ex.getCause(), instanceOf(Throwable.class));
        String expected = "stop; nested exception is java.lang.Throwable: me";
        assertThat(ex.getMessage(), equalTo(expected));
        assertThat(ex.getLocalizedMessage(), equalTo(expected));
    }
}
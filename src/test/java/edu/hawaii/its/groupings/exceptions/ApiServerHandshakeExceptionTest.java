package edu.hawaii.its.groupings.exceptions;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ApiServerHandshakeExceptionTest {

    @Test
    public void construction() {
        InitializationException ex = new ApiServerHandshakeException("fail");
        assertNotNull(ex);
        assertThat(ex.getMessage(), is("fail"));

        ex = new ApiServerHandshakeException("stop", new Throwable("me"));
        assertNotNull(ex);
        assertThat(ex.getCause(), instanceOf(Throwable.class));
        assertThat(ex.getMessage(), is("stop"));
        assertThat(ex.getLocalizedMessage(), is("stop"));
    }
}
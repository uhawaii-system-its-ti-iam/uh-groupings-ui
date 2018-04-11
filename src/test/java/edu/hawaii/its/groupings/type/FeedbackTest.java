package edu.hawaii.its.groupings.type;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FeedbackTest {

    private Feedback feedback;

    @Before
    public void setUp() { feedback = new Feedback();}

    @Test
    public void construction() {
        feedback = new Feedback();
    }

    @Test
    public void setters(){
        assertNotNull(feedback);
        assertNull(feedback.getName());
        assertNull(feedback.getEmail());
        assertNull(feedback.getType());
        assertNull(feedback.getMessage());
        assertNull(feedback.getExceptionError());

        feedback.setName("Test User");
        feedback.setEmail("test@hawaii.edu");
        feedback.setType("Problem");
        feedback.setMessage("Test Message");

        assertThat(feedback.getName(), equalTo("Test User"));
        assertThat(feedback.getEmail(), equalTo("test@hawaii.edu"));
        assertThat(feedback.getType(), equalTo("Problem"));
        assertThat(feedback.getMessage(),equalTo("Test Message"));
        assertNull(feedback.getExceptionError());
    }

    @Test
    public void exceptionSconstruction(){
        feedback = new Feedback("Test Expection Stack Trace");

        assertNotNull(feedback);
        assertNull(feedback.getName());
        assertNull(feedback.getEmail());
        assertNull(feedback.getType());
        assertNull(feedback.getMessage());
        assertThat(feedback.getExceptionError(), equalTo("Test Expection Stack Trace"));
    }
}
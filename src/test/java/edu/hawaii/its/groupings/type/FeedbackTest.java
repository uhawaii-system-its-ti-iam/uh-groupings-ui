package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FeedbackTest {

    private Feedback feedback;

    @Before
    public void setUp() {
        feedback = new Feedback();
    }

    @Test
    public void construction() {
        feedback = new Feedback();
    }

    @Test
    public void setters() {
        assertNotNull(feedback);
        assertNull(feedback.getName());
        assertNull(feedback.getEmail());
        assertNull(feedback.getType());
        assertNull(feedback.getMessage());
        assertNull(feedback.getExceptionMessage());

        feedback.setName("Test User");
        feedback.setEmail("test@hawaii.edu");
        feedback.setType("Problem");
        feedback.setMessage("Test Message");
        feedback.setExceptionMessage("Exception Message");

        assertThat(feedback.getName(), equalTo("Test User"));
        assertThat(feedback.getEmail(), equalTo("test@hawaii.edu"));
        assertThat(feedback.getType(), equalTo("Problem"));
        assertThat(feedback.getMessage(), equalTo("Test Message"));
        assertThat(feedback.getExceptionMessage(), equalTo("Exception Message"));

        assertThat(feedback.toString(), containsString("email=test@hawaii.edu"));
        assertThat(feedback.toString(), containsString("exceptionMessage=Exception Message"));
    }

    @Test
    public void exceptionConstruction() {
        feedback = new Feedback("Test Exception Stack Trace");

        assertNotNull(feedback);
        assertNull(feedback.getName());
        assertNull(feedback.getEmail());
        assertNull(feedback.getType());
        assertNull(feedback.getMessage());
        assertThat(feedback.getExceptionMessage(), equalTo("Test Exception Stack Trace"));

        assertThat(feedback.toString(), containsString("exceptionMessage=Test Exception Stack Trace"));
    }

}
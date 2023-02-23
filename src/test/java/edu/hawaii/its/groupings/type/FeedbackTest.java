package edu.hawaii.its.groupings.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FeedbackTest {

    private Feedback feedback;

    @BeforeEach
    public void setUp() {
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

        assertThat(feedback.getName(), is("Test User"));
        assertThat(feedback.getEmail(), is("test@hawaii.edu"));
        assertThat(feedback.getType(), is("Problem"));
        assertThat(feedback.getMessage(), is("Test Message"));
        assertThat(feedback.getExceptionMessage(), is("Exception Message"));

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
        assertThat(feedback.getExceptionMessage(), is("Test Exception Stack Trace"));

        assertThat(feedback.toString(), containsString("exceptionMessage=Test Exception Stack Trace"));
    }

}
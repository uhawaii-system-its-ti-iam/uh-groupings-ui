package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        feedback.setName("Testf-iwt-a TestIAM-staff");
        feedback.setEmail("iamtsta@example.com");
        feedback.setType("Problem");
        feedback.setMessage("Test Message");
        feedback.setExceptionMessage("Exception Message");

        assertThat(feedback.getName(), is("Testf-iwt-a TestIAM-staff"));
        assertThat(feedback.getEmail(), is("iamtsta@example.com"));
        assertThat(feedback.getType(), is("Problem"));
        assertThat(feedback.getMessage(), is("Test Message"));
        assertThat(feedback.getExceptionMessage(), is("Exception Message"));

        assertThat(feedback.toString(), containsString("email=iamtsta@example.com"));
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
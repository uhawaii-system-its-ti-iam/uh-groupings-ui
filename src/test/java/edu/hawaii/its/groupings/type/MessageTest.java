package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTest {

    private Message message;

    @BeforeEach
    public void setUp() {
        message = new Message();
    }

    @Test
    public void construction() {
        assertNotNull(message);
    }

    @Test
    public void setters() {
        assertNotNull(message);
        assertNull(message.getId());
        assertNull(message.getEnabled());
        assertNull(message.getText());
        assertNull(message.getTypeId());

        message.setId(666);
        assertThat(message.getId(), is(666));
    }

    @Test
    public void testToString() {
        String expected = "Message [id=null, typeId=null, enabled=null, text=null]";
        assertThat(message.toString(), containsString(expected));

        message.setId(12345);
        assertThat(message.toString(), containsString("Message [id=12345,"));
    }
}

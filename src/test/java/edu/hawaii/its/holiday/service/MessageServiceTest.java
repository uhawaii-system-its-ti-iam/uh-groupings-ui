package edu.hawaii.its.holiday.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.type.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Test
    public void findMessage() {
        Message message = messageService.findMessage(Message.GATE_MESSAGE);
        assertEquals("Y", message.getEnabled());
        assertEquals(Integer.valueOf(Message.GATE_MESSAGE), message.getTypeId());
        assertTrue(message.getText().startsWith("University of Hawaii Information"));

        // Turn down logging just for a second, just 
        // to reduce the Exception noise a little bit.
        Logger logger = (Logger) LoggerFactory.getLogger(MessageServiceImpl.class);
        Level level = logger.getLevel();
        logger.setLevel(Level.OFF);

        // No matching ID, so null returned.
        message = messageService.findMessage(-1);
        assertNull(message);

        // Cause an internal exception to happen.
        EntityManager em = messageService.getEntityManager();
        messageService.setEntityManager(null);
        message = messageService.findMessage(Message.ACCESS_DENIED_MESSAGE);
        assertNull(message);

        // Make sure the denied access message actually exists.
        messageService.evictCache();
        messageService.setEntityManager(em);
        message = messageService.findMessage(Message.ACCESS_DENIED_MESSAGE);
        assertThat(message.getId(), equalTo(Message.ACCESS_DENIED_MESSAGE));
        assertThat(message.getText(), containsString("system is restricted"));

        // Put original logging level back.
        logger.setLevel(level);
    }

    @Test
    public void update() {
        Message message = messageService.findMessage(Message.GATE_MESSAGE);
        assertEquals("Y", message.getEnabled());
        assertEquals(Integer.valueOf(1), message.getTypeId());
        assertTrue(message.getText().startsWith("University of Hawaii Information"));
        assertTrue(message.getText().endsWith("."));

        final String text = message.getText();

        message.setText("Stemming the bleeding.");
        messageService.update(message);

        message = messageService.findMessage(Message.GATE_MESSAGE);
        assertEquals("Y", message.getEnabled());
        assertEquals(Integer.valueOf(1), message.getTypeId());
        assertTrue(message.getText().equals("Stemming the bleeding."));

        // Put the original text back.
        message.setText(text);
        messageService.update(message);
        assertTrue(message.getText().startsWith("University of Hawaii Information"));
        assertTrue(message.getText().endsWith("."));
    }

    @Test
    public void messageCache() {
        Message m0 = messageService.findMessage(Message.GATE_MESSAGE);
        Message m1 = messageService.findMessage(Message.GATE_MESSAGE);
        assertSame(m0, m1);

        m0.setText("This land is your land.");
        messageService.update(m0);
        assertSame(m0, m1);

        m1 = messageService.findMessage(Message.GATE_MESSAGE);
        assertSame(m0, m1);

        Message m2 = messageService.findMessage(Message.GATE_MESSAGE);
        assertSame(m0, m2);
        assertSame(m1, m2);

        Message m3 = new Message();
        m3.setId(999);
        m3.setEnabled("Y");
        m3.setText("Testing");
        m3.setTypeId(1);
        messageService.add(m3);

        Message m4 = messageService.findMessage(999);
        assertEquals(m4, m3);
        assertSame(m4, m3);
    }

}

package edu.hawaii.its.holiday.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.type.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
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

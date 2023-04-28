package edu.hawaii.its.groupings.service;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class EmailServiceTest {

    private static boolean wasSent;

    private static SimpleMailMessage messageSent;

    public EmailService emailService;

    public EmailService mockEmailService;


    private Feedback createBaseFeedback() {
        Feedback feedback = new Feedback();
        feedback.setName("Test Iwa");
        feedback.setEmail("testiwa@hawaii.edu");
        feedback.setType("problem");
        feedback.setMessage("Some problem happened.");
        feedback.setExceptionMessage("");
        return feedback;
    }

    @BeforeEach
    public void setUp() {
        JavaMailSender sender = new MockJavaMailSender() {
            @Override
            public void send(SimpleMailMessage mailMessage) throws MailException {
                wasSent = true;
                messageSent = mailMessage;
            }
        };

        emailService = new EmailService(sender);
        emailService.setEnabled(true);
        emailService.setRecipient("address");
        mockEmailService = spy(new EmailService(sender));


        wasSent = false;
    }

    @Test
    public void sendFeedbackWithNoExceptionMessage() {
        Feedback feedback = createBaseFeedback();

        emailService.send(feedback);
        assertTrue(wasSent);

        assertTrue(messageSent.getSubject().contains("problem"));
        assertTrue(messageSent.getText().contains("Test Iwa"));
        assertTrue(messageSent.getText().contains("testiwa@hawaii.edu"));
        assertTrue(messageSent.getText().contains("Some problem happened."));
        assertFalse(messageSent.getText().contains("Stack Trace:"));
    }

    @Test
    public void sendFeedbackWithExceptionMessage() {
        Feedback feedback = createBaseFeedback();
        feedback.setExceptionMessage("ArrayIndexOutOfBoundsException");

        emailService.send(feedback);
        assertTrue(wasSent);

        assertTrue(messageSent.getSubject().contains("problem"));
        assertTrue(messageSent.getText().contains("Test Iwa"));
        assertTrue(messageSent.getText().contains("testiwa@hawaii.edu"));
        assertTrue(messageSent.getText().contains("Some problem happened."));
        assertTrue(messageSent.getText().contains("Stack Trace:"));
        assertTrue(messageSent.getText().contains("ArrayIndexOutOfBoundsException"));
    }

    @Test
    public void sendFeedbackWithMailExceptionThrown() {
        JavaMailSender senderWithException = new MockJavaMailSender() {
            @Override
            public void send(SimpleMailMessage mailMessage) throws MailException {
                wasSent = false;
                throw new MailSendException("Exception");
            }
        };

        EmailService emailServiceWithException = new EmailService(senderWithException);
        emailServiceWithException.setEnabled(true);
        emailServiceWithException.setRecipient("override@email");
        Feedback feedback = createBaseFeedback();

        emailServiceWithException.send(feedback);
        assertFalse(wasSent);
        emailServiceWithException.sendWithStack(new NullPointerException(), "Null Pointer Exception");
        assertFalse(wasSent);
    }

    @Test
    public void enabled() {
        emailService.setEnabled(false);
        assertFalse(emailService.isEnabled());
        emailService.sendWithStack(new NullPointerException(), "Null Pointer Exception");
        assertFalse(wasSent);

        emailService.setEnabled(false);
        assertFalse(emailService.isEnabled());
        emailService.send(createBaseFeedback());
        assertFalse(wasSent);

        emailService.setEnabled(true);
        assertTrue(emailService.isEnabled());
        emailService.sendWithStack(new NullPointerException(), "Null Pointer Exception");
        assertTrue(wasSent);

        emailService.setEnabled(true);
        assertTrue(emailService.isEnabled());
        emailService.send(createBaseFeedback());
        assertTrue(wasSent);
    }

    @Test
    public void overrideRecipient() {
        Feedback feedback = createBaseFeedback();

        emailService.setRecipient("override@email.com");
        emailService.send(feedback);
        assertTrue(messageSent.getText().contains("Recipient overridden"));

        emailService.setRecipient("its-iam-web-app-dev-help-l@lists.hawaii.edu");
        emailService.send(feedback);
        assertFalse(messageSent.getText().contains("Recipient overridden"));

        emailService.setRecipient("its-iam-web-app-dev-help-l@lists.hawaii.edu");
        emailService.sendWithStack(new NullPointerException(), "Null Pointer Exception");
        assertFalse(messageSent.getText().contains("Recipient overridden"));


        emailService.setEnabled(true);
        emailService.setRecipient("override@email.com");
        emailService.sendWithStack(new NullPointerException(), "Null Pointer Exception");
        assertTrue(messageSent.getText().contains("Recipient overridden"));
    }

    @Test
    public void unknownHost() throws UnknownHostException {
        Feedback feedback = createBaseFeedback();
        mockEmailService.setEnabled(true);
        mockEmailService.setRecipient("address");
        doThrow(UnknownHostException.class).when(mockEmailService).getLocalHost();

        mockEmailService.send(feedback);
        assertTrue(messageSent.getText().contains("Unknown Host"));

        mockEmailService.sendWithStack(new NullPointerException(), "Null Pointer Exception");
        assertTrue(messageSent.getText().contains("Unknown Host"));
    }

}

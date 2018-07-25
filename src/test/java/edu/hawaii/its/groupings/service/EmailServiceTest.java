package edu.hawaii.its.groupings.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.type.Feedback;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class EmailServiceTest {

    private static boolean wasSent;

    private static SimpleMailMessage messageSent;

    public EmailService emailService;

    private Feedback createBaseFeedback() {
        Feedback feedback = new Feedback();
        feedback.setName("John Doe");
        feedback.setEmail("jdoe@hawaii.edu");
        feedback.setType("problem");
        feedback.setMessage("Some problem happened.");
        feedback.setExceptionMessage("");
        return feedback;
    }

    @Before
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

        wasSent = false;
    }

    @Test
    public void sendFeedbackWithNoExceptionMessage() {
        Feedback feedback = createBaseFeedback();

        emailService.send(feedback);
        assertThat(wasSent, is(true));

        assertThat(messageSent.getSubject(), containsString("problem"));
        assertThat(messageSent.getText(), containsString("John Doe"));
        assertThat(messageSent.getText(), containsString("jdoe@hawaii.edu"));
        assertThat(messageSent.getText(), containsString("Some problem happened."));
        assertThat(messageSent.getText(), not(containsString("Stack Trace:")));
    }

    @Test
    public void sendFeedbackWithExceptionMessage() {
        Feedback feedback = createBaseFeedback();
        feedback.setExceptionMessage("ArrayIndexOutOfBoundsException");

        emailService.send(feedback);
        assertThat(wasSent, is(true));

        assertThat(messageSent.getSubject(), containsString("problem"));
        assertThat(messageSent.getText(), containsString("John Doe"));
        assertThat(messageSent.getText(), containsString("jdoe@hawaii.edu"));
        assertThat(messageSent.getText(), containsString("Some problem happened."));
        assertThat(messageSent.getText(), containsString("Stack Trace:"));
        assertThat(messageSent.getText(), containsString("ArrayIndexOutOfBoundsException"));
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

        Feedback feedback = createBaseFeedback();

        emailServiceWithException.send(feedback);
        assertThat(wasSent, is(false));
    }

    @Test
    public void enabled() {
        emailService.setEnabled(false);
        assertThat(emailService.isEnabled(), is(false));

        emailService.setEnabled(true);
        assertThat(emailService.isEnabled(), is(true));
    }

}

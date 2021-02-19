package edu.hawaii.its.groupings.service;

import edu.hawaii.its.groupings.type.Feedback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class EmailService {

    @Value("${email.send.to}")
    private String to;

    @Value("${email.send.from}")
    private String from;

    @Value("${email.is.enabled}")
    private boolean isEnabled;

    private static final Log logger = LogFactory.getLog(EmailService.class);

    private JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(Feedback feedback) {
        logger.info("Feedback received in EmailService: " + feedback);
        if (isEnabled) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(from);
            String text = "";
            String header = "UH Groupings service feedback [" + feedback.getType() + "]";
            text += "Submitted name: " + feedback.getName() + "\n\n";
            text += "Submitted email: <" + feedback.getEmail() + ">\n\n";
            text += "Feedback type: " + feedback.getType() + "\n\n";
            text += "--------------------------" + "\n\n";
            text += "Feedback: " + feedback.getMessage() + "\n\n";
            if (!feedback.getExceptionMessage().isEmpty()) {
                text += "Stack Trace: " + feedback.getExceptionMessage();
            }
            msg.setText(text);
            msg.setSubject(header);
            try {
                javaMailSender.send(msg);
            } catch (MailException ex) {
                logger.error("Error", ex);
            }
        }
    }

    public void sendWithStack(Exception e, String exceptionType) {
        logger.info("Feedback Error email has been triggered.");
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        if (isEnabled) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(from);
            String text = "";
            String header = "UH Groupings Error Response";
            text += "Cause of Response: The API threw an exception that has triggered the ErrorControllerAdvice on the UI.\n\n";
            text += "Exception Thrown: ErrorControllerAdvice threw the " + exceptionType + ".\n\n";
            text += "----------------------------------------------------" + "\n\n";
            text += "Stack Trace: \n\n" + exceptionAsString;
            msg.setText(text);
            msg.setSubject(header);
            try {
                javaMailSender.send(msg);
            } catch (MailException ex) {
                logger.error("Error", ex);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

}

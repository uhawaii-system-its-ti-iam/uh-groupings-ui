package edu.hawaii.its.groupings.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.type.Feedback;

@Service
public class EmailService {

    @Value("${email.send.recipient}")
    private String recipient;
    
    @Value("${email.send.from}")
    private String from;

    @Value("${email.is.enabled}")
    private boolean isEnabled;

    @Value("${app.environment}")
    private String environment;

    private static final Log logger = LogFactory.getLog(EmailService.class);

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(Feedback feedback) {
        logger.info("Feedback received in EmailService: " + feedback);

        if (!isEnabled) {
            logger.warn("Email service is not enabled. Set email.is.enabled property to true to enable");
            return;
        }

        String hostname = "Unknown Host";

        try {
            InetAddress ip = this.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException f) {
            logger.error("Error", f);
        }

        if (isEnabled) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(recipient);
            msg.setFrom(from);
            String text = "";
            String header = "UH Groupings service feedback [" + feedback.getType() + "]";
            text += "Host Name: " + hostname + ".\n";
            if (!recipient.equals("its-iam-web-app-dev-help-l@lists.hawaii.edu")) {
                text += "Recipient overridden to: " + recipient + "\n";
            }
            text += "----------------------------------------------------" + "\n\n";
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

        if (!isEnabled) {
            logger.warn("Email service is not enabled. Set email.is.enabled property to true to enable");
            return;
        }

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();

        InetAddress ip;
        String hostname = "Unknown Host";

        try {
            ip = this.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException f) {
            logger.error("Error", f);
        }

        if (isEnabled) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(recipient);
            msg.setFrom(from);
            String text = "";
            String header =  "(" + environment + ") UH Groupings UI Error Response";
            text += "Cause of Response: The UI threw an exception that has triggered the ErrorControllerAdvice. \n\n";
            text += "Exception Thrown: ErrorControllerAdvice threw the " + exceptionType + ".\n\n";
            text += "Host Name: " + hostname + ".\n";
            if (!recipient.equals("its-iam-web-app-dev-help-l@lists.hawaii.edu")) {
                text += "Recipient overridden to: " + recipient + "\n";
            }
            text += "----------------------------------------------------" + "\n\n";
            text += "UI Stack Trace: \n\n" + exceptionAsString;
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

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setEnvironment(String environment) { this.environment = environment; }

    public String getEnvironment() { return environment; }

    public InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

}

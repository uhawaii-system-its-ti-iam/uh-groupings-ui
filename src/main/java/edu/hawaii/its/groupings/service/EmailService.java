package edu.hawaii.its.groupings.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


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

    public void send(String name, String head, String body, String email) {
        if(isEnabled){
            logger.info("Sending email!");
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(from);
            String text = "";

            String header = "Feedback Type: " + head;
            text += "Feedback reported by " + name + " using email " + email + "\n\n";
            text += "Feedback: " + body;
        //  text += data;
        //  for(int i = 0; i < data.length; i++){
        //    text += data[i];
        //  }
            msg.setText(text);
            msg.setSubject(header);
            try {
                javaMailSender.send(msg);
            } catch (MailException ex) {
                logger.error("Error", ex);
              }

        }
    }

}

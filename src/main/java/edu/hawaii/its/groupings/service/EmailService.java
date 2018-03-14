package edu.hawaii.its.groupings.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import edu.hawaii.its.groupings.type.Feedback;


@Service
public class EmailService {

    @Value("${email.send.to}")
    private String to;

    @Value("${app.mail.from:no-reply}")
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
        if(isEnabled){
            logger.info("\n/********************************************************************************************/" +
                    "\n\nSending email!\n\n" +
                    "/********************************************************************************************/\n");
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(from);
            String text = "";
           // String header = "Aaron is testing emailing stuffs";
            String header = "Feedback Type: " + feedback.getType();
            text += "Feedback reported by " + feedback.getName() + " using email " + feedback.getEmail() + "\n\n";
            text += "Feedback: " + feedback.getMessage()    ;

            if(feedback.getExceptionError() != null)
            {
                text += "Stacktrace: " + feedback.getExceptionError();
            }
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

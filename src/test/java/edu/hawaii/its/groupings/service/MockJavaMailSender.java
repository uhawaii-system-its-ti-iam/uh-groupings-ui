package edu.hawaii.its.groupings.service;

import java.io.InputStream;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class MockJavaMailSender implements JavaMailSender {

    @Override
    public void send(SimpleMailMessage mailMessage) throws MailException {
        // Empty.
    }

    @Override
    public void send(SimpleMailMessage... mailMessages) throws MailException {
        // Empty.
    }

    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream inputStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        // Empty.
    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        // Empty.
    }

    @Override
    public void send(MimeMessagePreparator preparator) throws MailException {
        // Empty.
    }

    @Override
    public void send(MimeMessagePreparator... preparators) throws MailException {
        // Empty.
    }

}

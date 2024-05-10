package org.example.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    public static void sendEmail(String recipientEmail, String subject, String body) throws MessagingException {
        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        // Create a Session object with the specified properties and authenticator
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("chaimatlili62@gmail.com", "bxra lvjy ajes ajqs");
            }
        });

        // Create a MimeMessage object
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("chaimatlili62@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(body);

        session.setDebug(true);
        Transport.send(message);
        System.out.println("message sent successfully....");
    }
}

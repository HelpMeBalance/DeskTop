package org.example.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    public static void sendEmail(String recipientEmail, String subject, String body) throws MessagingException {
        // Set up mail server properties
        Properties props = System.getProperties();
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "true");


        // Create a Session object with the specified properties and authenticator
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("amenallah.belhouichet@esprit.tn", "dmck ewyg pkuo otsm");
                    }
                });

        // Create a MimeMessage object
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("amenallah.belhouichet@esprit.tn"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(body);

        // Send the message
        Transport.send(message);
    }
}

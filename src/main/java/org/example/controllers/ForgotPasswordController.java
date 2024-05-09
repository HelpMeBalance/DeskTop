package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.models.PasswordResetToken;
import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.Navigation;
import org.example.utils.TempUserStorage;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class ForgotPasswordController {
    public Button sendCode;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label statusLabel;



    @FXML
    private void handleSendCode() {
        String email = emailTextField.getText();
        if (email.isEmpty()) {
            statusLabel.setText("Email cannot be empty.");
            return;
        }
        // Logic to send code to email and create token in the database
        sendResetCode(email);
    }

    private void sendResetCode(String email) {
        // Generate a 6-digit verification code
        String code = String.format("%06d", new Random().nextInt(999999));
        // SMTP server information
        String host = "smtp-relay.brevo.com";
        String port = "587";
        String mailFrom = "fares2business@gmail.com";
        String password = "s8qHyjANkQ9DUO7W";

        // message info
        String mailTo = email;
        String subject = "Reset your password";
        String message = "Here is your password reset code: " + code;

        // Set properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailFrom, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30); // Code expires in 30 minutes

        // Prepare email message
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailFrom));
            InternetAddress[] toAddresses = { new InternetAddress(mailTo) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setSentDate(new java.util.Date());
            msg.setText(message);

            // Send email
            Transport.send(msg);

            statusLabel.setText("Verification code sent to " + email);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            statusLabel.setText("Failed to send email.");
        }

        // Save the token in the database
        UserService userService = new UserService();
        try {
            User user = userService.findByEmail(email);
            if (user != null) {
                PasswordResetToken token = new PasswordResetToken(0, user.getId(), code, expiresAt);
                userService.savePasswordResetToken(token);
                // set the user email in TempUserStorage
                TempUserStorage.getInstance().setUserEmail(email);
                statusLabel.setText("Verification code sent to " + email);
                Navigation.navigateTo("/fxml/Auth/VerificationCodeEntry.fxml", sendCode);
            } else {
                statusLabel.setText("No user associated with this email.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error processing your request.");
        }


    }
}

package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.example.service.UserService;
import org.example.utils.Navigation;
import org.example.utils.TempUserStorage;

import java.io.IOException;

public class PasswordResetController {
    @FXML
    private Label statusLabel;
    @FXML
    private Button resetPasswordButton;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    public void handleResetPassword() throws IOException {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        // check the length of the password must be more than 8 characters
        if (newPassword.length() < 8) {
            statusLabel.setText("Password must be at least 8 characters long.");
            return;
        }


        // Retrieve email from secure temporary storage

        String email = TempUserStorage.getInstance().getUserEmail();
        if (email == null) {
            statusLabel.setText("Session expired. Please start over.");
            return;
        }

        UserService userService = new UserService();
        System.out.println("Email: " + email + " Password: " + newPassword);
        if (userService.resetPassword(email, newPassword)) {
            Navigation.navigateTo("/fxml/Auth/Login.fxml", resetPasswordButton);
            statusLabel.setText("Password successfully reset.");
        } else {
            statusLabel.setText("Failed to reset password.");
        }
    }

}

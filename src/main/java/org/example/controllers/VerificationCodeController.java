package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.service.UserService;
import org.example.utils.Navigation;
import org.example.utils.TempUserStorage;

import java.io.IOException;

public class VerificationCodeController {
    @FXML
    public TextField codeTextField;
    @FXML
    public Label statusLabel;
    @FXML
    public Button verifyCodeButton;

    @FXML
    public void handleVerifyCode() throws IOException {
        UserService userService = new UserService();
        String code = codeTextField.getText();
        if (userService.isTokenValid(code)) {
            statusLabel.setText("Code is valid.");
            System.out.println("Code is valid.");

            // Get email from TempUserStorage
            String email = TempUserStorage.getInstance().getUserEmail();

            if (email != null && !email.isEmpty()) {
                // Navigate to password reset page
                Navigation.navigateTo("/fxml/Auth/PasswordReset.fxml", verifyCodeButton);
            } else {
                statusLabel.setText("Session expired or invalid. Please start over.");
            }
        } else {
            statusLabel.setText("Code is invalid.");
        }
    }
}

package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ResetPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    protected void handleResetPassword(ActionEvent event) {
        // Implement reset password logic here
        String email = emailField.getText().trim();
        // Validate email and send reset link
    }
}

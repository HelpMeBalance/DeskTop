package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import org.example.service.UserService;

import java.awt.*;

// ResetCodeController.java
public class ResetCodeController {

    @FXML
    private TextField resetCodeField;
    private UserService userService = new UserService();

    @FXML
    private void handleVerifyCode(ActionEvent event) {
        String enteredCode = resetCodeField.getText().trim();

        if (isCodeValid(enteredCode)) {
            showNewPasswordDialog();
        } else {
            showAlert("Invalid Code", "The code you entered is invalid or has expired.", Alert.AlertType.ERROR);
        }
    }

    private boolean isCodeValid(String code) {
        // Add logic to validate the code against the database
        return userService.isTokenValid(code); // Adjust `isTokenValid` in UserService to check codes
    }

    private void showNewPasswordDialog() {
        // Show a dialog to allow the user to reset the password

    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

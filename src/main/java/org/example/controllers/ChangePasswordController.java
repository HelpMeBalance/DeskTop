package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import org.example.service.UserService;
import org.example.models.User;

import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;

    private final UserService userService = new UserService();
    private String token; // Token passed from the URL

    public void initialize() {
        // You might want to initialize something here.
    }

    public void setToken(String token) {
        this.token = token;
    }

    @FXML
    protected void handleChangePassword(ActionEvent event) throws SQLException {
        String newPassword = newPasswordField.getText().trim();
        String confirmNewPassword = confirmNewPasswordField.getText().trim();

        if (!newPassword.equals(confirmNewPassword)) {
            showAlert("Error", "The new passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Implement token validation and password change logic
        if (userService.isTokenValid(token)) {
            User user = userService.getUserByResetToken(token);
            if (user != null) {
                userService.changePassword(user, newPassword, token);
                showAlert("Success", "Your password has been changed successfully!", Alert.AlertType.INFORMATION);
                // Close window or redirect to  screen
            } else {
                showAlert("Error", "Invalid token.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "The token is invalid or has expired.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

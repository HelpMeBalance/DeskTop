package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.Session;

import java.sql.SQLException;

public class EditPasswordController {

    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    private UserService userService = new UserService();

    @FXML
    private void handleChangePassword() {
        User user = Session.getInstance().getUser();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Password Mismatch", "New password and confirmation do not match.", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (userService.changePassword(user, currentPassword, newPassword)) {
                showAlert("Success", "Password successfully updated.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update password. Please check your current password.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while updating the password.", Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

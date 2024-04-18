package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.Session;

public class ProfileController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;

    private User user; // Assume this user is somehow passed to the controller
    private UserService userService = new UserService();

    public void initialize() {
        // Fetch the user from the session
        this.user = Session.getInstance().getUser();
        if (this.user != null) {
            firstNameField.setText(user.getFirstname());
            lastNameField.setText(user.getLastname());
        }
    }


    @FXML
    private void handleSaveChanges() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showAlert("Error", "First name and last name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        user.setFirstname(firstName);
        user.setLastname(lastName);

        try {
            userService.update(user); // Update user in the database
            showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update profile.", Alert.AlertType.ERROR);
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

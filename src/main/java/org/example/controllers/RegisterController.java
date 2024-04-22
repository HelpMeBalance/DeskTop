package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;

import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.MyDataBase;
import org.example.utils.PasswordUtil;
import java.time.LocalDateTime;  // Import this at the top
import java.time.format.DateTimeFormatter;  // Import this to format the datetime
import org.example.utils.Navigation; // Import the Navigation class

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class RegisterController {

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Label errorMessage; // Add a label in your FXML to display errors


    @FXML
    private CheckBox notARobotCheck;

    @FXML
    private CheckBox termsCheckBox;

    @FXML
    private Hyperlink termsOfServiceLink;

    @FXML
    private Hyperlink signInLink;


    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize your controller here if needed
    }

    @FXML
    private void handleRegister() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        if (!areFieldsValid()) {
            return; // Stop the registration process if validation fails
        }
        User newUser = new User();
        newUser.setEmail(emailField.getText());
        newUser.setPassword(passwordField.getText());
        newUser.setFirstname(firstnameField.getText());
        newUser.setLastname(lastnameField.getText());
        newUser.setRoles(new ArrayList<>(Arrays.asList("ROLE_USER")));
        newUser.setIs_banned(false);
        newUser.setCreated_at(LocalDateTime.now());



        // Use UserService to add the new user
        UserService userService = new UserService();

        try {
            userService.add(newUser);
            showAlert("Registration Successful", "User registered successfully.", Alert.AlertType.INFORMATION);
            try {
                // Use the Navigation utility class to navigate
                Navigation.navigateTo("/fxml/Auth/Login.fxml", signInLink);
            }
            catch (IOException e) {
                e.printStackTrace(); // Handle the exception, possibly with a user alert
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Registration Failed", "An error occurred while registering the user.", Alert.AlertType.ERROR);
            return;
        }


    }


    @FXML
    private void handleSignIn() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Auth/Login.fxml", signInLink);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }



    @FXML
    private void openTermsOfService() {
        // TODO: Open the terms of service in the user's web browser
    }

    /**
     * Validates the user input in text fields.
     *
     * @return true if the input is valid, false otherwise
     */
    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (firstnameField.getText().trim().isEmpty() || lastnameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            errors.append("First Name must be filled.\n");
            isValid = false;
        }

        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email must be filled.\n");
            isValid = false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            errors.append("Password must be filled.\n");
            isValid = false;
        }

        if (lastnameField.getText().trim().isEmpty()) {
            errors.append("Last Name must be filled.\n");
            isValid = false;
        }

        if (!passwordField.getText().equals(repeatPasswordField.getText())) {
            errors.append("Passwords do not match.\n");
            isValid = false;
        }

        if (!termsCheckBox.isSelected()) {
            errors.append("You must agree to the terms and conditions.\n");
            isValid = false;
        }

        if (!notARobotCheck.isSelected()) {
            errors.append("Please confirm that you are not a robot.\n");
            isValid = false;
        }

        // add length validation for password
        if (passwordField.getText().length() < 8) {
            errors.append("Password must be at least 8 characters long.\n");
            isValid = false;
        }

        // add length validation for email
        if (emailField.getText().length() < 5) {
            errors.append("Email must be at least 5 characters long.\n");
            isValid = false;
        }

        // add length validation for first name
        if (firstnameField.getText().length() < 2) {
            errors.append("First Name must be at least 2 characters long.\n");
            isValid = false;
        }

        // add length validation for last name
        if (lastnameField.getText().length() < 2) {
            errors.append("Last Name must be at least 2 characters long.\n");
            isValid = false;
        }


        errorMessage.setText(errors.toString());
        return isValid;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import org.example.utils.MyDataBase;
import org.example.utils.PasswordUtil;
import java.time.LocalDateTime;  // Import this at the top
import java.time.format.DateTimeFormatter;  // Import this to format the datetime

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


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
        if (!areFieldsValid()) {
            return; // Stop the registration process if validation fails
        }
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();
        // Formatting the current datetime
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter); // Format as a string

        if (!password.equals(repeatPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        try (Connection conn = MyDataBase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (firstname, lastname, email, password, roles, is_banned, created_at) VALUES (?, ?, ?, ?, ?, 0, NOW())")) {
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, "ROLE_USER");  // Ensure this matches the constraints or exists in the roles table

            int result = stmt.executeUpdate();
            System.out.println(result + " user(s) registered.");
        } catch (SQLException e) {
            System.err.println("Error when registering user: " + e.getMessage());
        }

    }


    @FXML
    private void handleSignIn() throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Auth/Login.fxml"));
        Stage stage = (Stage) firstnameField.getScene().getWindow();
        stage.setScene(new Scene(loginRoot, 1200, 700));
        stage.show();
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
            errors.append("All fields must be filled.\n");
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

        errorMessage.setText(errors.toString());
        return isValid;
    }

}

package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.MyDataBase;
import org.example.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import org.example.utils.Navigation; // Import the Navigation class
import org.example.utils.Session;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    private UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        if (!areFieldsValid()) {
            return;
        }
        String email = emailField.getText();
        String password = passwordField.getText();

        UserService userService = new UserService();

        try {
            User user = userService.selectWhere(email, password);

            if (user != null) {
                // Login successful
                // Navigate to the next screen or update the UI accordingly
                // For example, if using the `Navigation` class:
                Session.getInstance().setLoggedIn(true, user);
                try
                {
                    Navigation.navigateTo("/fxml/Home/homepage.fxml", emailField);
                } catch (IOException e) {
                    e.printStackTrace(); // Handle the exception, possibly with a user alert
                }
            } else {
                // Login failed
                System.out.println("Login failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while logging in");
        }

    }


    @FXML
    private void handleRegisterAction() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Auth/Register.fxml", loginButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email must be filled.\n");
            isValid = false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            errors.append("Password must be filled.\n");
            isValid = false;
        }

        if (!isValid) {
            // Show the error message
            System.out.println(errors.toString());
        }

        return isValid;
    }
}

package org.example.controllers;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;

import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.MyDataBase;
import org.example.utils.PasswordUtil;
import java.time.LocalDateTime;  // Import this at the top
import java.time.format.DateTimeFormatter;  // Import this to format the datetime
import org.example.utils.Navigation; // Import the Navigation class
import javafx.embed.swing.SwingFXUtils;

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
    private CheckBox termsCheckBox;

    @FXML
    private Hyperlink termsOfServiceLink;

    @FXML
    private Hyperlink signInLink;
    @FXML private Canvas captchaCanvas;
    @FXML private TextField captchaField;
    private DefaultKaptcha captchaProducer;


    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        initializeCaptcha();
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

        String enteredCaptcha = captchaField.getText();
        String realCaptcha = (String) captchaCanvas.getUserData();

        if (!enteredCaptcha.equals(realCaptcha)) {
            showAlert("Captcha Error", "Captcha does not match.", Alert.AlertType.ERROR);
            refreshCaptcha(); // Refresh captcha to give the user another try
            return;
        }


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

    private void initializeCaptcha() {
        captchaProducer = new DefaultKaptcha();
        Config config = new Config(new java.util.Properties());
        config.getProperties().setProperty("kaptcha.image.width", "200");
        config.getProperties().setProperty("kaptcha.image.height", "50");
        config.getProperties().setProperty("kaptcha.textproducer.font.size", "42");
        config.getProperties().setProperty("kaptcha.noise.color", "blue");
        captchaProducer.setConfig(config);

        refreshCaptcha();
    }

    private void refreshCaptcha() {
        String text = captchaProducer.createText();
        WritableImage image = SwingFXUtils.toFXImage(captchaProducer.createImage(text), null);
        GraphicsContext gc = captchaCanvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
        captchaCanvas.setUserData(text);  // Storing the text in the canvas for verification
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

        // add email validation using regex
        if (!emailField.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.append("Email is not valid.\n");
            isValid = false;
        }

        // kapcha validation
        if (captchaField.getText().trim().isEmpty()) {
            errors.append("Captcha must be filled.\n");
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

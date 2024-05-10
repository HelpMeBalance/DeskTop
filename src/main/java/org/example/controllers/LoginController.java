package org.example.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.*;
import org.example.utils.Navigation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    private UserService userService = new UserService();
    @FXML
    private Button forgetPasswordButton;
    @FXML
    private Label errorMessage; // Add a label in your FXML to display errors

    @FXML
    public void handleForgetPassword() {
        try {
            Navigation.navigateTo("/fxml/Auth/ForgotPassword.fxml", forgetPasswordButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        if (!areFieldsValid()) {
            return;
        }
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = userService.selectWhere(email, password);

            if (user != null) {
                if (user.getIs_banned() != null && user.getIs_banned()) {
                    LocalDateTime now = LocalDateTime.now();
                    if (user.getBan_expires_at() != null && user.getBan_expires_at().isAfter(now)) {
                        // Calculate remaining duration of the ban
                        long hoursLeft = java.time.Duration.between(now, user.getBan_expires_at()).toHours();
                        showAlert("Banned", "You are currently banned for " + hoursLeft + " more hours.", Alert.AlertType.ERROR);
                        return; // Prevent login
                    }
                }

                // Login successful, navigate to home page
                Session.getInstance().setLoggedIn(true, user);
                Navigation.navigateTo("/fxml/Home/homepage.fxml", emailField);

            } else {
                // Login failed due to invalid credentials
                showAlert("Error", "Invalid Credentials.", Alert.AlertType.ERROR);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while logging in", Alert.AlertType.ERROR);
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
    @FXML
    private void handleGoogleSignIn() {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith("http://localhost:8080/callback")) {
                String code = null;
                try {
                    code = extractCodeFromUrl(newValue);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                handleOAuthRedirect(code);
                // Close the WebView after getting the code
                Stage stage = (Stage) webView.getScene().getWindow();
                stage.close();
            }
        });

        try {
            GoogleAuthorizationCodeFlow flow = new GoogleOAuth().createFlow();
            String redirectUri = "http://localhost:8080/callback";
            GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            String authorizationUrl = url.build();
            webEngine.load(authorizationUrl);
        } catch (IOException e) {
            System.out.println("Error loading Google OAuth URL: " + e.getMessage());
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(new VBox(webView)));
        stage.show();

    }

    private void handleOAuthRedirect(String code) {
        try {
            GoogleAuthorizationCodeFlow flow = new GoogleOAuth().createFlow();
            GoogleTokenResponse response = flow.newTokenRequest(code)
                    .setRedirectUri("http://localhost:8080/callback")
                    .execute();

            if (response.getAccessToken() != null) {
                String accessToken = response.getAccessToken();
                handleUserSession(accessToken);
            } else {
                System.err.println("Failed to obtain access token.");
            }
        } catch (Exception e) {
            System.err.println("OAuth redirect handling error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleUserSession(String accessToken) {
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            GenericUrl url = new GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            String json = response.parseAsString();
            GoogleUserInfo userInfo = new Gson().fromJson(json, GoogleUserInfo.class);
            processUserInfo(userInfo);
        } catch (IOException e) {
            System.err.println("Failed to fetch user information: " + e.getMessage());
        }
    }


    private void processUserInfo(GoogleUserInfo userInfo) {
        UserService userService = new UserService();

        try {
            // Check if the user exists in your database
            User user = userService.findByGoogleId(userInfo.getId());
            if (user == null) {
                user = new User(); // Ensure all required fields are set correctly
                user.setEmail(userInfo.getEmail());
                user.setFirstname("DefaultFirstName");  // Consider using part of the email or a placeholder
                user.setLastname("DefaultLastName");
                user.setIs_banned(false);
                user.setGoogle_id(userInfo.getId());
                user.setRoles(Arrays.asList("ROLE_USER"));
                user.setCreated_at(LocalDateTime.now());
                user.setProfile_picture(userInfo.getPicture());
                user.setPassword("test"); // Set a default password or generate a random one
                userService.add(user);
                System.out.println("New user added: " + user);


            } else {
                // Update user profile picture if it has changed
                if (!user.getProfile_picture().equals(userInfo.getPicture())) {
                    user.setProfile_picture(userInfo.getPicture());
                    userService.update(user);
                    System.out.println("User profile picture updated: " + user);
                }
            }

            // Update session and navigate to homepage
            Session.getInstance().setLoggedIn(true, user);
            Navigation.navigateTo("/fxml/Home/homepage.fxml", emailField);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to process user information.", Alert.AlertType.ERROR);
        }
    }


    private void navigateToHomePage(GoogleUserInfo userInfo) {
        // Navigation logic here
        System.out.println("Navigating to home page...");
    }

    private void updateUserSession(GoogleUserInfo userInfo) {
        // Update session or database with user information
        System.out.println("Updating user session for: " );
    }

    // Inner class to handle user information JSON response
    static class GoogleUserInfo {
        private String id;
        private String email;
        private String picture;
        private boolean verified_email;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
        public boolean isVerifiedEmail() { return verified_email; }
        public void setVerifiedEmail(boolean verified_email) { this.verified_email = verified_email; }
    }



    private String extractCodeFromUrl(String url) throws UnsupportedEncodingException {
        String prefix = "code=";
        int startIndex = url.indexOf(prefix) + prefix.length();
        int endIndex = url.indexOf("&", startIndex);
        endIndex = endIndex == -1 ? url.length() : endIndex;
        String encodedCode = url.substring(startIndex, endIndex);
        String decodedCode = URLDecoder.decode(encodedCode, StandardCharsets.UTF_8.toString());
        return decodedCode;
    }


}

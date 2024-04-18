package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.utils.Navigation; // Import the Navigation class

import java.io.IOException;

public class NavbarController {
    @FXML
    private Button loginButton;
    @FXML
    private Button adminButton;
    @FXML
    private Button StoreButton;
    @FXML
    private Button HomeButton; // Add this button for the home navigation

    @FXML
    private void handleLogin() {
        try {
        // Use the Navigation utility class to navigate
        Navigation.navigateTo("/fxml/Auth/Login.fxml", loginButton);
    } catch (IOException e) {
        e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }
    @FXML
    private void handleAdmin() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Admin/admin.fxml", adminButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert

        }
        // Use the Navigation utility class to navigate
    }

    @FXML
    private void handleStore() {
        // Use the Navigation utility class to navigate
        try {
            Navigation.navigateTo("/fxml/Store/store.fxml", StoreButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    @FXML
    private void handleHome() {
        // Use the Navigation utility class to navigate
        try {
            Navigation.navigateTo("/fxml/Home/homepage.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleBookSession() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/AppointsTypes/appointsTypes.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleBraveChat() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Blog/Blog.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }
}

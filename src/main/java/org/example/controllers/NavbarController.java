package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavbarController {
    @FXML
    private Button loginButton; // This field must match the fx:id in the FXML file
    @FXML
    private Button adminButton;
    @FXML
    private Button StoreButton;
    @FXML
    private void handleLogin() throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Auth/Login.fxml"));

        // Get the current stage (window) using any known component, here it's the login button
        Stage stage = (Stage) loginButton.getScene().getWindow(); // loginButton is a Button on your Home screen

        // Set the Login screen scene to the stage and show it
        stage.setScene(new Scene(loginRoot));
        stage.show();
    }

    public void handleAdmin() throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Admin/Publication.fxml"));

        // Get the current stage (window) using any known component, here it's the login button
        Stage stage = (Stage) adminButton.getScene().getWindow(); // loginButton is a Button on your Home screen

        // Set the Login screen scene to the stage and show it
        stage.setScene(new Scene(loginRoot));
        stage.show();
    }

    public void handleStore() throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Store/Store.fxml"));

        // Get the current stage (window) using any known component, here it's the login button
        Stage stage = (Stage) StoreButton.getScene().getWindow(); // loginButton is a Button on your Home screen

        // Set the Login screen scene to the stage and show it
        stage.setScene(new Scene(loginRoot));
        stage.show();
    }
}

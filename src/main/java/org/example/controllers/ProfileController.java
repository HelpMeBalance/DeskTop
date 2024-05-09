package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.models.User;
import org.example.service.UserService;
import org.example.utils.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button uploadButton;

    private User user; // Assume this user is somehow passed to the controller
    private UserService userService = new UserService();

    public void initialize() {
        // Fetch the user from the session
        this.user = Session.getInstance().getUser();
        if (this.user != null) {
            firstNameField.setText(user.getFirstname());
            lastNameField.setText(user.getLastname());
            if (user.getProfile_picture() != null) {
                profileImageView.setImage(new Image("file:" + user.getProfile_picture()));
            }
        }
    }


    @FXML
    private void handleSaveChanges() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String profile_picture = user.getProfile_picture();


        if (firstName.isEmpty() || lastName.isEmpty()) {
            showAlert("Error", "First name and last name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setProfile_picture(profile_picture);

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

    @FXML
    private void handleUploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            String targetDir = System.getProperty("user.dir") + "/src/main/resources/assets/uploadedprofilepicture/7_images.jpg";
            try {
                Files.copy(selectedFile.toPath(), Paths.get(targetDir), StandardCopyOption.REPLACE_EXISTING);
                user.setProfile_picture(targetDir);
                profileImageView.setImage(new Image("file:" + targetDir));
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to upload image: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }

    }

    private void updateImageView(String imagePath) {

        String targetDir = System.getProperty("user.dir") + "/src/main/resources/assets/uploadedprofilepicture/7_images.jpg";
        if (imagePath != null && !imagePath.isBlank()) {
            File file = new File(targetDir);
            if (file.exists()) {
                try {
                    Image image = new Image(targetDir);
                    profileImageView.setImage(image);
                } catch (Exception e) {
                    showAlert("Error", "Could not load image: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            } else {
                showAlert("Error", "Image file does not exist: " + imagePath, Alert.AlertType.ERROR);
            }
        }
    }



}

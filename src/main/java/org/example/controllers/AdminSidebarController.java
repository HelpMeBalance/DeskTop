package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.example.utils.Navigation; // Import the Navigation class
import org.example.utils.Session;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AdminSidebarController {
    @FXML private ToggleButton usersButton;
    @FXML private ToggleButton publicationsButton;
    @FXML private HBox homeIcon;
    @FXML
    private void handleUsers() {
        navigate("/fxml/Admin/Users.fxml");
    }

    @FXML
    private void handlePublications() {
        navigate("/fxml/Admin/Publication.fxml");
    }

    @FXML
    public void handleQuiz() {
        navigate("/fxml/Admin/Quize.fxml");
    }

    // Other handlers...
    public void handleGoHome(){
        navigate("/fxml/Home/homepage.fxml");
    }
    private void navigate(String fxmlPath) {
        try {
            // Assuming usersButton or any other referenced node is part of the scene
            Navigation.navigateTo(fxmlPath, usersButton);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error alert)
        }
    }

    public void action(ActionEvent actionEvent) {
    }

    public void handleDashboard(ActionEvent actionEvent) {
    }

    public void handleAppointments() {
        try {
            Navigation.navigateTo("/fxml/Admin/Appointment.fxml", usersButton);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleConsultations() {
        try {
            Navigation.navigateTo("/fxml/Admin/Consultation.fxml", usersButton);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    public void handleCategories() {
        navigate("/fxml/Admin/Categorie.fxml");
    }

    public void handleStore() {
        navigate("/fxml/Admin/Article.fxml");
    }

    public void handleCategoriesProduit() {
        navigate("/fxml/Admin/CategorieProduit.fxml");
    }
}

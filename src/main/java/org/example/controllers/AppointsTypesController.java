package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import org.example.utils.Navigation;
import org.example.utils.Session;

import java.io.IOException;

public class AppointsTypesController {
    @FXML
    Hyperlink Individual, Couple, Child;
    boolean isLoggedIn = Session.getInstance().isLoggedIn();

    public void handleClicks(ActionEvent event) {
        RendezVousController.serviceSelected = ((Hyperlink)event.getSource()).getId();
        if(isLoggedIn){
            try {
                // Use the Navigation utility class to navigate
                Navigation.navigateTo("/fxml/Appointment/AppointmentCreation.fxml", Individual);
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception, possibly with a user alert
            }
        }
        else {
            try {
                Navigation.navigateTo("/fxml/Auth/Login.fxml", Individual);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

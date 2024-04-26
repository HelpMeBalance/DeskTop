package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import org.controlsfx.control.Rating;
import org.example.service.RendezVousService;
import org.example.utils.AppointmentDisplayBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AppointmentManagementController implements Initializable {

    @FXML
    private HBox displayHBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayAppointments();
    }
    public void displayAppointments(){
        try{
            for(var app: new RendezVousService().select()){
                displayHBox.getChildren().addAll(new AppointmentDisplayBox().AppointmentDisplayBox(app.getDateR().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString(),app.isStatut() ? "Confirmed" : "Not Confirmed",app.getNomService(), app));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}

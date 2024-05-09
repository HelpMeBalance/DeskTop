package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.Rating;
import org.example.service.RendezVousService;
import org.example.utils.AppointmentDisplayBox;
import org.example.utils.GNCarousel;
import org.example.utils.Navigation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppointmentManagementController implements Initializable {

    @FXML
    private HBox displayHBox;
    @FXML
    private VBox VBOX;

    public static HBox hboxNode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayAppointments();
        hboxNode= displayHBox;
    }
    public void displayAppointments(){
        try{
            ArrayList<VBox> list = new ArrayList<>();
            GNCarousel<VBox> carousel;
            for(var app: new RendezVousService().select()){
                if (app.getPatient().getId()==org.example.utils.Session.getInstance().getUser().getId()){
//                    displayHBox.getChildren().addAll(new AppointmentDisplayBox().AppointmentDisplayBox(app.getDateR().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),app.isStatut() ? "Confirmed" : "Not Confirmed",app.getNomService(), app));
                    list.add(new AppointmentDisplayBox().AppointmentDisplayBox(app.getDateR().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),app.isStatut() ? "Confirmed" : "Not Confirmed",app.getNomService(), app));
                }
            }
            System.out.println(list);
            carousel = new GNCarousel<>(FXCollections.observableArrayList(list));
            carousel.setMinSize(550,260);
            displayHBox.getChildren().add(carousel);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void redirectToCreation(){
        try {
            Navigation.navigateTo("/fxml/Appointment/appointsTypes.fxml", displayHBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

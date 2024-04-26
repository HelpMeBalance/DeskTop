package org.example.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.EntryViewBase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Consultation;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.ConsultationService;
import org.example.service.RendezVousService;
import org.example.service.UserService;
import org.example.test.Main;
import org.example.utils.Navigation;
import org.example.utils.Session;
import org.example.utils.UserStringConverter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class PsyAppointmentController implements Initializable {

    @FXML
    private HBox displayHBox;

    RendezVousService appServ = new RendezVousService();
    ConsultationService conServ = new ConsultationService();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initPsyPage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public VBox MyCustomPopOverContentNode(RendezVous app) {
        // Create Labels
        Label appointmentDetailsLabel = new Label("Appointment Details");
        Label serviceLabel = new Label("Service: "+ app.getNomService());
        Label statusLabel = new Label("Status: "+(app.isStatut()? "Confirmed":"Not Confirmed"));

        // Create Separator
        Separator separator1 = new Separator();
        Separator separator2 = new Separator();

        // Create Buttons
        Button appointmentInfoButton = new Button("Appointment Info");
        appointmentInfoButton.setOnAction(event -> {
            try {
                for(var c: conServ.select()){
                    if (c.getAppointment().getId() == app.getId())
                        ConsultationDisplay.con = c;
                }
                Navigation.navigateTo("/fxml/Appointment/PsyConsultationDisplay.fxml", displayHBox);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button appointmentConfirmButton = new Button("Confirm Appointment");
        appointmentConfirmButton.setOnAction(event -> {
            try {
                conServ.add(new Consultation(app, 0, -1, false, "", app.getPsy(), app.getPatient()));
                app.setStatut(true);
                appServ.update(app);
                Navigation.navigateTo("/fxml/Appointment/PsyAppointmentDisplay.fxml", displayHBox);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Create HBox for buttons
        HBox buttonHBox;
        if (app.isStatut())
            buttonHBox = new HBox(10, appointmentInfoButton);
        else
            buttonHBox = new HBox(10, appointmentConfirmButton);
        // Create VBox
        VBox vBox = new VBox(20,
                appointmentDetailsLabel,
                separator1,
                new VBox(10, serviceLabel, statusLabel),
                separator2,
                buttonHBox
        );

        // Set padding
        vBox.setPadding(new Insets(10, 20, 10, 20));

        // Set preferred widths
        vBox.setPrefWidth(238.0);
        buttonHBox.setPrefWidth(200.0);

        // Set alignment
        vBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonHBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        return vBox;
    }
    public void initPsyPage() throws SQLException {

        // test Calendarfx start
        CalendarView calendarView = new CalendarView();
//        calendarView.setOnMouseClicked(null);
        calendarView.setCreateEntryClickCount(3);
//        calendarView.setOnMouseClicked(Event::consume);
        calendarView.setPrefSize(800, 600);
        calendarView.setEnableTimeZoneSupport(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowAddCalendarButton(false);
//        calendarView.setShowPageSwitcher(false);
        calendarView.showMonthPage();

        com.calendarfx.model.Calendar Individual = new com.calendarfx.model.Calendar("Individual");
        com.calendarfx.model.Calendar Couple = new com.calendarfx.model.Calendar("Couple");
        com.calendarfx.model.Calendar Child = new com.calendarfx.model.Calendar("Child");

        Individual.setShortName("I");
        Couple.setShortName("CP");
        Child.setShortName("CH");

        // test entry (success? meh)
        for (var app: new RendezVousService().select()){
            if (app.getPsy().getId() == Session.getInstance().getUser().getId()){
                calendarView.setEntryDetailsPopOverContentCallback(param -> MyCustomPopOverContentNode(app));
                LocalDateTime startDate = app.getDateR();
                LocalDateTime startTime =startDate.plusHours(2);
                Interval eventInterval = new Interval(startDate, startTime);

                String title = app.getNomService() + " - " + app.getPatient().getFirstname() + " " + app.getPatient().getLastname();
                Entry<RendezVous> newEvent = new Entry<>(title, eventInterval);
                newEvent.setUserObject(app);
                if(app.getNomService().equalsIgnoreCase("Individual"))
                    Individual.addEntry(newEvent);
                else if (app.getNomService().equalsIgnoreCase("Couple"))
                    Couple.addEntry(newEvent);
                else if (app.getNomService().equalsIgnoreCase("Child"))
                    Child.addEntry(newEvent);
            }
        }
        // end test entry

        Individual.setStyle(com.calendarfx.model.Calendar.Style.STYLE1);
        Couple.setStyle(com.calendarfx.model.Calendar.Style.STYLE2);
        Child.setStyle(Calendar.Style.STYLE3);

        CalendarSource familyCalendarSource = new CalendarSource("Family");
        familyCalendarSource.getCalendars().addAll(Individual, Couple, Child);

        calendarView.getCalendarSources().setAll(familyCalendarSource);
        calendarView.setRequestedTime(LocalTime.now());


        displayHBox.getChildren().add(calendarView);

        // updating time
        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();
        // end updating time

        // test Calendarfx end
    }
}

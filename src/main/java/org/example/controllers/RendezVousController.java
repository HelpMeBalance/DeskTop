package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.RendezVousService;
import org.example.service.UserService;
import org.example.utils.Navigation;
import org.example.utils.UserStringConverter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

public class RendezVousController implements Initializable {
    private RendezVousService rvs = new RendezVousService();
    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<String> service;
    @FXML
    private ComboBox<User> psy;
    @FXML
    private VBox psyBox, serviceBox;
    @FXML
    private HBox dateBox;
    @FXML
    private Label errorMessage;
    private boolean testPsy, testService, testDate;
    public static String serviceSelected="";
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        date.setValue(LocalDateTime.now().toLocalDate());
        initServiceAndPsy();
        service.setValue(serviceSelected);
    }

    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (date.getValue().isBefore(LocalDate.now())) {
            errors.append("incorrect Date.\n");
            isValid = false;
        }

        if (service.getValue()==null) {
            errors.append("service must be filled.\n");
            isValid = false;
        }

        if (psy.getValue()==null) {
            errors.append("psy must be filled.\n");
            isValid = false;
        }

        errorMessage.setText(errors.toString());
        return isValid;
    }

    public void add(ActionEvent actionEvent) {
        try{
            LocalDate localDate = LocalDate.now(); // Example LocalDate object
            LocalDateTime selectedDateTime = localDate.atTime(15, 30);
            if (!areFieldsValid())
                return;

                rvs.add(new RendezVous(selectedDateTime, service.getValue(), false, false, psy.getValue(), org.example.utils.Session.getInstance().getUser()));

                // test MAILER
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp-relay.brevo.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                String username = "abdelbakikacem2015@gmail.com";
                String password = "nSbT8QjgaHd5kmhK";

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("HelpMeBalance@org.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("abdelbakikacem2015@gmail.com"));
                message.setSubject("Appointment creation");
                message.setText("your appointment has been created");

                Transport.send(message);
                // end test

                Navigation.navigateTo("/fxml/Appointment/AppointmentDisplay.fxml", psy);


        }catch (SQLException | MessagingException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void initServiceAndPsy() {
        service.setItems(FXCollections.observableArrayList("Individual","Couple","Child"));

        // Start test
        ArrayList<User> users = new ArrayList<>();
        try{
            UserService ps = new UserService();
            users = new ArrayList<>();
            for(var u: ps.select()){
                if (u.getRoles().contains("psy"))
                    users.add(u);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // End test

        psy.setItems(FXCollections.observableArrayList(users));
        psy.setConverter(new UserStringConverter());
//        service.getItems().add("String");
    }
}

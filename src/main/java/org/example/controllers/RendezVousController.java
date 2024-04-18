package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.PersonneService;
import org.example.service.RendezVousService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

public class RendezVousController implements Initializable {
    private RendezVousService rvs = new RendezVousService();
    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<String> service, psy;
    public static String serviceSelected="";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initServiceAndPsy();
        service.setValue(serviceSelected);
    }

    public void add(ActionEvent actionEvent) {
        try{
            LocalDateTime selectedDateTime = date.getValue().atStartOfDay();
            rvs.add(new RendezVous(selectedDateTime, service.getValue(), false,false, new User(1), new User(1)));

            // test MAILER
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp-relay.brevo.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            String username = "abdelbakikacem2015@gmail.com";
            String password = "nSbT8QjgaHd5kmhK";

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
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

        }catch (SQLException | MessagingException e){
            System.out.println(e.getMessage());
        }
    }
    public void initServiceAndPsy() {
        service.setItems(FXCollections.observableArrayList("Individual","Couple","Child"));

        // Start test
        ArrayList<String> names = new ArrayList<>();
        try{
            PersonneService ps = new PersonneService();
            for(var p : ps.select()){
                names.add(p.getNom());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // End test

        psy.setItems(FXCollections.observableArrayList(names));
//        service.getItems().add("String");
    }
}

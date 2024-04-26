package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
    public static String serviceSelected="";
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initServiceAndPsy();
        service.setValue(serviceSelected);
    }

    public void add(ActionEvent actionEvent) {
        try{
            LocalDateTime selectedDateTime = date.getValue().atStartOfDay();
            RendezVous rd = new RendezVous(selectedDateTime, service.getValue(), false, false, psy.getValue(), org.example.utils.Session.getInstance().getUser());

            // Assuming rendezVousService is an instance of your service class
            QuizeController.rv=rvs.add2(rd);
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
            Navigation.navigateTo("/fxml/Quiz/quize.fxml", psy);

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
            users = new ArrayList<>(ps.select());
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // End test

        psy.setItems(FXCollections.observableArrayList(users));
        psy.setConverter(new UserStringConverter());
//        service.getItems().add("String");
    }
}

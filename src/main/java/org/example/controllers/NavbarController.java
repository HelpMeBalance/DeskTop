package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.control.PopOver;
import org.example.utils.Navigation; // Import the Navigation class
import org.example.utils.Session;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavbarController {
    @FXML
    public Button chatBot;
    @FXML
    private Button loginButton;
    @FXML
    private Button StoreButton;
    @FXML
    private Button HomeButton; // Add this button for the home navigation
    @FXML
    private MenuButton profileButton;
    @FXML
    private MenuItem AdminPage, PsyPage;
    @FXML
    private void handleLogin() {
        try {
        // Use the Navigation utility class to navigate
        Navigation.navigateTo("/fxml/Auth/Login.fxml", loginButton);
    } catch (IOException e) {
        e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }


    @FXML
    private void handleStore() {
        // Use the Navigation utility class to navigate
        try {
            Navigation.navigateTo("/fxml/Store/store.fxml", StoreButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    @FXML
    private void handleHome() {
        // Use the Navigation utility class to navigate
        try {
            Navigation.navigateTo("/fxml/Home/homepage.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleBookSession() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Appointment/appointsTypes.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleBraveChat() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Blog/Blog.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleViewProfile() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Profile/profile.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void initialize() {
        // Call this method in initialize to set up the navbar based on login state
        updateLoginState();
    }

    public void updateLoginState() {
        boolean isLoggedIn = Session.getInstance().isLoggedIn();
        loginButton.setVisible(!isLoggedIn);
        profileButton.setVisible(isLoggedIn);

        boolean isAdmin = isLoggedIn && Session.getInstance().getUser().getRoles().contains("ROLE_ADMIN");
        boolean isPsy = isLoggedIn && Session.getInstance().getUser().getRoles().contains("psy");
        AdminPage.setVisible(isAdmin);
        PsyPage.setVisible(isPsy);

        if (isLoggedIn) {
            // Set the text of the profile button or configure it as needed
            profileButton.setText(Session.getInstance().getUser().getFirstname()); // Or any other user property
        }



    }

    @FXML
    private void handleLogout() {
        // Clear the session and update the navbar state
        Session.getInstance().setLoggedIn(false, null);
        updateLoginState();
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Home/homepage.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleEditPassword() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Profile/EditPassword.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }

    public void handleAdminPage() {
        try {
            // Use the Navigation utility class to navigate
            Navigation.navigateTo("/fxml/Admin/Users.fxml", HomeButton);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception, possibly with a user alert
        }
    }
    public void handleAppointmentPage() {
        try {
            Navigation.navigateTo("/fxml/Appointment/AppointmentDisplay.fxml", HomeButton);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void handlePsyPage() {
        try {
            Navigation.navigateTo("/fxml/Appointment/PsyAppointmentDisplay.fxml", HomeButton);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void ChatBot() {

        WebView webView = new WebView();
//        webView.setZoom(0.5);
        webView.setContextMenuEnabled(false);

        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load("https://www.freeaitherapist.com/onboarding");
        webEngine.getLoadWorker().exceptionProperty().addListener((obs, oldExc, newExc) -> {
            if (newExc != null) {
                newExc.printStackTrace();
            }
        });

        PopOver popOver = new PopOver();
        popOver.setContentNode(webView);

        chatBot.setOnAction(e -> {
            popOver.show(chatBot);
        });
    }
}

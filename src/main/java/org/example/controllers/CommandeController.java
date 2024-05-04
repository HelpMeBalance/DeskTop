package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.models.Commande;
import org.example.models.User;
import org.example.service.CommandeService;
import org.example.utils.Session;

import java.sql.SQLException;

public class CommandeController {
    @FXML
    private TextField addressField;
    @FXML
    private TextField paymentMethodField;
    @FXML
    private TextField saleCodeField;
    @FXML
    private Button confirmOrderButton;

    private String address;
    private String paymentMethod;
    private String saleCode;

    public CommandeController() {
        // Default constructor
    }

    @FXML
    private void initialize() {
        // Initialize method, if needed
    }

    @FXML
    private void handleConfirmOrder() {
        // Get user input from the form fields
        String address = addressField.getText();
        String paymentMethod = paymentMethodField.getText();
        String saleCode = saleCodeField.getText();

        // Retrieve the total price from the session
        double totalPrice = (double) Session.getInstance().getAttribute("totalPrice");
        int userId = Session.getInstance().getUser().getId();

        // Validate the payment method
        if (!"CARD".equalsIgnoreCase(paymentMethod) && !"CASH".equalsIgnoreCase(paymentMethod)) {
            // Display a message to the user
            showAlert("Invalid Payment Method", "Please enter either 'CARD' or 'CASH' as the payment method.");
            return; // Stop further processing
        }

        // Check if the entered sale code matches "20242024"
        if ("20242024".equals(saleCode)) {
            // Reduce the total price by 20%
            totalPrice *= 0.8; // 20% discount
        }

        if (totalPrice == 0) {
            showAlert("Cart is empty", "Please select articles to be ordered");
            return; // Stop further processing
        }

        // Create a Commande object
        Commande commande = new Commande(userId, getUsername(), address, paymentMethod, totalPrice, saleCode);

        // Add the Commande to the database
        CommandeService commandeService = new CommandeService();
        try {
            commandeService.addCommande(commande);
            // Optionally, show a confirmation message
            showConfirmationMessage(commande);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error
        }

        // Reset form fields after confirming order
        clearFormFields();
    }


    private void clearFormFields() {
        addressField.clear();
        paymentMethodField.clear();
        saleCodeField.clear();
    }

    // Getters for form field values
    public String getAddress() {
        return address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getSaleCode() {
        return saleCode;
    }
    private int getCurrentUserId() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            return -1; // Or any other appropriate value
        }
    }

    private String getUsername() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getFirstname();
        } else {
            return ""; // Or any other appropriate value
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlertConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean isAuthenticated() {
        // Check if the user is logged in
        User currentUser = Session.getInstance().getCurrentUser();
        return currentUser != null;
    }
    private void showConfirmationMessage(Commande commande) {
        int userId = Session.getInstance().getUser().getId();
        if ("20242024".equals(commande.getSaleCode())) {
            double pricebeforesale = commande.getTotalPrice()/0.8;
            String message = "Your order has been placed successfully! \n" +
                    "User ID: " + userId + "\n" +
                    "Price before sale:" +pricebeforesale +"\n" +
                    "Total Price: $" + commande.getTotalPrice() + "\n" +
                    "Delivery Address: " + commande.getAddress() + "\n" +
                    "Payment Method: " + commande.getPaymentMethod();

            showAlertConfirmation("Order Placed", message);
        }
        else
        {
            String message = "Your order has been placed successfully! \n" +
                "User ID: " + userId + "\n" +
                "Total Price: $" + commande.getTotalPrice() + "\n" +
                "Delivery Address: " + commande.getAddress() + "\n" +
                "Payment Method: " + commande.getPaymentMethod();

            showAlertConfirmation("Order Placed", message);
        }

    }
}

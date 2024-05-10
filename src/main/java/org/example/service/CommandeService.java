package org.example.service;

import javax.mail.MessagingException;
import org.example.models.Commande;
import org.example.models.User;
import org.example.service.EmailService;
import org.example.utils.MyDataBase;
import org.example.utils.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.service.EmailService.sendEmail;

public class CommandeService {
    private final Connection connect;

    public CommandeService() {
        connect = MyDataBase.getInstance().getConnection();
    }

    public void addCommande(Commande commande) throws SQLException {
        String deletePanierItemsQuery = "DELETE FROM panier WHERE user_id = ?";
        String addCommandeQuery = "INSERT INTO commande (user_id, username, address, paymentmethode, status, total_price, sale_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement deleteStatement = connect.prepareStatement(deletePanierItemsQuery);
             PreparedStatement addCommandeStatement = connect.prepareStatement(addCommandeQuery)) {
            // Set parameters for deleting Panier items
            deleteStatement.setInt(1, commande.getUserId());
            // Execute the delete query
            deleteStatement.executeUpdate();

            // Set parameters for adding Commande
            addCommandeStatement.setInt(1, commande.getUserId());
            addCommandeStatement.setString(2, commande.getUsername());
            addCommandeStatement.setString(3, commande.getAddress());
            addCommandeStatement.setString(4, commande.getPaymentMethod());
            addCommandeStatement.setInt(5, commande.getStatus());
            addCommandeStatement.setDouble(6, commande.getTotalPrice());
            addCommandeStatement.setString(7, commande.getSaleCode());

            // Execute the insert query
            addCommandeStatement.executeUpdate();

            String username = Session.getInstance().getUser().getFirstname(); // Change to the appropriate method to get the username
            String email = Session.getInstance().getUser().getEmail(); // Change to the appropriate method to get the email address
            String address = commande.getAddress();
            double totalPrice = commande.getTotalPrice();



            sendOrderConfirmationEmail(username, email, address, totalPrice);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle email sending failure
        }
    }
    private void sendOrderConfirmationEmail(String username, String email, String address, double totalPrice) throws MessagingException {
        String subject = "Order Confirmation";
        String body = "Dear " + username + ",\n\nYour order has been successfully placed!\n\n"
                + "Total Price: $" + totalPrice + "\nDelivery Address: " + address + "\n\nThank you for shopping with us!";
        sendEmail(email, subject, body);
    }


    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (PreparedStatement preparedStatement = connect.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String address = resultSet.getString("address");
                String paymentMethod = resultSet.getString("paymentmethode");
                int status = resultSet.getInt("status");
                double totalPrice = resultSet.getDouble("total_price");
                String saleCode = resultSet.getString("sale_code");

                // Create a new Commande object with retrieved values
                Commande commande = new Commande(id, userId, username, address, paymentMethod, status, totalPrice, saleCode);

                // Add the Commande object to the list
                commandes.add(commande);
            }
        }
        return commandes;
    }


    public void updateCommandeStatus(int commandeId, int newStatus) throws SQLException {
        String query = "UPDATE commande SET status = ? WHERE id = ?";

        try (PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, newStatus);
            statement.setInt(2, commandeId);
            statement.executeUpdate();
        }
    }
    public List<Commande> getCommandHistoryForUser(int userId) throws SQLException {
        List<Commande> commandHistory = new ArrayList<>();
        String query = "SELECT * FROM commande WHERE user_id = ?";

        try (PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String address = resultSet.getString("address");
                    String paymentMethod = resultSet.getString("paymentmethode");
                    int status = resultSet.getInt("status");
                    double totalPrice = resultSet.getDouble("total_price");
                    String saleCode = resultSet.getString("sale_code");

                    Commande commande = new Commande(id, userId, username, address, paymentMethod, status, totalPrice, saleCode);
                    commandHistory.add(commande);
                }
            }
        }
        return commandHistory;
    }

    public Map<String, Integer> getCommandeStatisticsByPaymentMethod() throws SQLException {
        Map<String, Integer> statistics = new HashMap<>();
        String query = "SELECT paymentmethode, COUNT(*) AS total FROM commande GROUP BY paymentmethode";
        try (PreparedStatement statement = connect.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String paymentMethod = resultSet.getString("paymentmethode");
                int totalCount = resultSet.getInt("total");
                statistics.put(paymentMethod, totalCount);
            }
        }
        return statistics;
    }

}
